package testing;

import enums.ProgrammingLanguage;
import enums.Status;
import testing.DTO.SubmissionMetaDTO;
import testing.exceptions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sun.swing.MenuItemLayoutHelper.max;

class VerdictInfo{
    Status status;
    int executionTime = 0;
    int usedMemory = 0;
    // если статус не ОК
    int numOfFailureTest;
    int numOfFailureLine;
    String expected;
    String provided;
    boolean isDifferLen;
    String input;
    String output;

    @Override
    public String toString() {
        return "VerdictInfo{" +
                "status=" + status +
                ", executionTime=" + executionTime +
                ", usedMemory=" + usedMemory +
                ", numOfFailureTest=" + numOfFailureTest +
                ", numOfFailureLine=" + numOfFailureLine +
                ", expected='" + expected + '\'' +
                ", provided='" + provided + '\'' +
                ", isDifferLen=" + isDifferLen +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}

public class TestSystem {
    static final String PATH_TO_TESTS = "judge/tests";
    static final String PATH_TO_SESSION_STORE = "judge/sessions"; // UUID/ скомпилированный и вывод
    static final String OUTPUT_FILE_NAME = "output.txt";
    static final String SOURCE_FILE_NAME = "main";
    static BlockingQueue<SubmissionMetaDTO> submissions = new LinkedBlockingQueue<>();
    static String testCode1 = """
            #include<iostream>
            using namespace std;
            int main(){
                int t; cin >> t;
                while(t--){
                    int x, y; cin >> x >> y;
                    cout << x + y << "\\n";
                }
            }
            """;

    static String testCode2 = """
            #include<iostream>
            using namespace std;
            int main(){
                int t; cin >> t;
                while(t--){
                    int x, y; cin >> x >> y;
                    cout << x + y + 1 << "\\n";
                }
            }
            """;
    static String testCode3 = """
            #include<iostream>
            using namespace std;
            int main(){
                int t; cin >> t;
                while(true){
                    int x, y; cin >> x >> y;
                    cout << x + y + 1 << "\\n";
                }
            }
            """;
    static String testCode4 = """
            #include<iostream>
            using namespace std;
            int main(){
                int t; cin >> t;
                while(t--){
                    int x, y; cin >> x >> y;
                    cout << x + y / 0 << "\sn";
                }
            }
            """;


    public static void main(String[] args) throws InterruptedException, IOException {
//        ProcessBuilder pb = new ProcessBuilder();
//        pb.command("g++", "D:\\JavaPrjects\\FirstTest\\judje\\sessions\\4314592171209057476\\main.cpp");
//        pb.command("mkdir", "abc");
//        pb.start();

        SubmissionQueueProcessor();
    }


    public static void SubmissionQueueProcessor() throws InterruptedException {
        submissions.put(new SubmissionMetaDTO(1, 1, 1, testCode1, ProgrammingLanguage.Cpp, 1, 250));
        submissions.put(new SubmissionMetaDTO(1, 1, 1, testCode2, ProgrammingLanguage.Cpp, 1, 250));
        submissions.put(new SubmissionMetaDTO(1, 1, 1, testCode3, ProgrammingLanguage.Cpp, 1, 250));
        submissions.put(new SubmissionMetaDTO(1, 1, 1, testCode4, ProgrammingLanguage.Cpp, 1, 250));

        while (true) {
            SubmissionMetaDTO submissionMeta = submissions.take();
            var verdictInfo = new VerdictInfo();

            try {
                Path sessionDir = Files.createTempDirectory(Path.of(PATH_TO_SESSION_STORE), null);
                Path sourceFile = Files.createFile(sessionDir.resolve(SOURCE_FILE_NAME + submissionMeta.getProgrammingLanguage().sourceSuffix));
                Files.writeString(sourceFile,submissionMeta.getSourceCode());
                Path compiledFile = sourceFile;
                if (submissionMeta.getProgrammingLanguage().needCompilation) {
                    compiledFile = compileSolution(sessionDir, sourceFile, submissionMeta.getProgrammingLanguage(), submissionMeta.getTimeLimit(), verdictInfo);
//                    System.out.println(compiledFile);
                }
                testSolution(compiledFile, sessionDir, submissionMeta, verdictInfo);
                //тут комитим обработку

            } catch (CompilationTimeException ex){
                verdictInfo.status = Status.COMPILE_TIME_LIMIT;
            } catch (CompilationException ex){
                verdictInfo.status = Status.COMPILE_ERROR;
            } catch (TimeLimitException ex){
                verdictInfo.status = Status.TIME_LIMIT;
            } catch (MemoryLimitException ex){
                verdictInfo.status = Status.MEMORY_LIMIT;
            } catch (RuntimeErrorException ex){
                verdictInfo.status = Status.RUNTIME_ERROR;
            } catch (WrongAnswerException ex){
                verdictInfo.status = Status.WRONG_ANSWER;
            } catch (IOException | InterruptedException ex) {
                // возвращаем ошибку системы
                ex.printStackTrace();
            }

            System.out.println(verdictInfo + "\n");
            // пишем в бд результат, отправляем в нотификэйшн сервис
        }
    }


    public static void testSolution(Path compiledFile, Path sessionDir, SubmissionMetaDTO submissionMeta, VerdictInfo verdictInfo) throws IOException, InterruptedException {

        Path judgeTestDir = Path.of(PATH_TO_TESTS).resolve("problem_" + submissionMeta.getProblemId());
        int testsCnt = Integer.valueOf(Files.readString(judgeTestDir.resolve("meta.txt")));

        for (int i = 1; i <= testsCnt; i++) {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(submissionMeta.getProgrammingLanguage().getRunCommand(compiledFile));
            pb.redirectInput(new File(judgeTestDir.resolve(i + ".in").toString()));

            Process process = pb.start();
//            System.out.println("START ");
            long start = System.currentTimeMillis();
            boolean successEnd = process.waitFor(submissionMeta.getTimeLimit(), TimeUnit.SECONDS);
//            System.out.println("END");
            int duration = (int) (System.currentTimeMillis() - start);
            verdictInfo.usedMemory = 123;
            verdictInfo.executionTime = duration;
            if (!successEnd) {
                process.destroyForcibly();
                verdictInfo.numOfFailureTest = i;
                throw new TimeLimitException();
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                verdictInfo.numOfFailureTest = i;
//                System.out.println(new String(process.getInputStream().readAllBytes()));
                throw new RuntimeErrorException();
            }
//            System.out.println("CHECK ");
            exactMatchCheck(judgeTestDir.resolve(i + ".out"), process.getInputStream(), verdictInfo, i);
//            System.out.println("END");
        }

        verdictInfo.status = Status.OK;
    }


    public static Path compileSolution(Path sessionDir, Path sourcePath, ProgrammingLanguage language, int compileTimeLimit, VerdictInfo verdictInfo) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        Path outputPath = sessionDir.resolve(OUTPUT_FILE_NAME);

        pb.redirectOutput(outputPath.toFile());
        pb.redirectErrorStream(true);

        String[] compileCommand = language.getCompileCommand(sourcePath);
        pb.command(compileCommand);

        Process process = pb.start();
        boolean successEnd = process.waitFor(10, TimeUnit.SECONDS);
//        System.out.println(new String(Files.readAllBytes(outputPath)));
        if (!successEnd) {
            process.destroyForcibly();
            throw new CompilationTimeException();
        }
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String out =  new String(Files.readAllBytes(outputPath));
            verdictInfo.output = out;
            throw new CompilationException(exitCode, out);
        }

        String sourcePathString = sourcePath.toString();
        return Path.of(sourcePathString.substring(0, sourcePathString.lastIndexOf(".")) + language.compiledSuffix);
    }


    public static void exactMatchCheck(Path judgeSolution, InputStream contestantSolution, VerdictInfo verdictInfo, int testNum) throws IOException{
        try (BufferedReader judgeReader = new BufferedReader(new FileReader(judgeSolution.toFile()));
             BufferedReader conReader = new BufferedReader(new InputStreamReader(contestantSolution))) {

            int lineCnt = 1;
            String line1, line2;
            while (true) {
                line1 = judgeReader.readLine();
                line2 = conReader.readLine();
                if (line1 == null && line2 == null) {
                    break;
                }
                if (line1 == null || line2 == null) {
                    verdictInfo.numOfFailureTest = testNum;
                    verdictInfo.isDifferLen = true;
                    throw new WrongAnswerException(true);
                }

                line1 = line1.replaceAll("(\\n|\\s)", "");
                line2 = line2.replaceAll("(\\n|\\s)", "");
                if (!line1.equals(line2)) {
                    verdictInfo.numOfFailureTest = testNum;
                    verdictInfo.expected = line1;
                    verdictInfo.provided = line2;
                    verdictInfo.numOfFailureLine = lineCnt;
                    throw new WrongAnswerException(line1, line2, lineCnt);
                }
                lineCnt++;
            }
        }
    }
}




