import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

enum ProgrammingLanguages{
    Java, Cpp
}

class TestSystemException extends RuntimeException{
    public TestSystemException(){

    }
}

class CompilationTimeException extends RuntimeException{
    public CompilationTimeException(){
    }
}

class CompilationException extends RuntimeException{
    int exitCode;
    public CompilationException(int exitCode, String msg){
        super(msg);
        this.exitCode = exitCode;
    }
}

class TimeLimitException extends RuntimeException{

}

class RuntimeErrorException extends RuntimeException{

}

class WrongAnswerException extends RuntimeException{
    boolean outputLenDiffer;
    String judgeLine;
    String contestantLine;
    int num;
    public  WrongAnswerException(String judgeLine, String contestantLine, int num){
        this(false);
        this.judgeLine = judgeLine;
        this.contestantLine = contestantLine;
        this.num = num;
    }
    public WrongAnswerException(boolean outputLenDiffer){
        this.outputLenDiffer = outputLenDiffer;
    }

}


class MemoryLimitException extends RuntimeException{

}


public class Main {

    public static File compileSolution(File sourceCode, ProgrammingLanguages language, int compileTimeLimit) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.redirectOutput(new File("out.txt"));
        String compiledPath = sourceCode.getAbsolutePath().substring(0, sourceCode.getAbsolutePath().lastIndexOf('.'));

        if (language == ProgrammingLanguages.Java){
            pb.command("javac", sourceCode.getAbsolutePath());
            compiledPath += ".class";
        } else if (language == ProgrammingLanguages.Cpp){
            pb.command("g++", sourceCode.getAbsolutePath(), "-o", sourceCode.getName().substring(0, sourceCode.getName().lastIndexOf('.')) + ".out");
            compiledPath += ".out";
        }
        try {
            Process process = pb.start();
            boolean successEnd = process.waitFor(compileTimeLimit, TimeUnit.SECONDS);
            if (!successEnd){
                throw new CompilationTimeException();
            }
            int exitCode = process.exitValue();
            if (exitCode != 0){
                String output = process.getOutputStream().toString();
                throw new CompilationException(exitCode, output);
            }
        } catch (IOException | InterruptedException ex){
            ex.printStackTrace();
           throw new TestSystemException();
        }
        return Path.of(compiledPath).toFile();
    }


    public static void exactMatchCheck(String judge, InputStream contestant){

        try (BufferedReader judgeReader = new BufferedReader(new FileReader(judge));
        BufferedReader conReader = new BufferedReader(new InputStreamReader(contestant))) {

            int lineCnt = 1;
            String line1, line2;
            while(true){
                line1 = judgeReader.readLine();
                line2 = conReader.readLine();
                if (line1 == null && line2 == null){
                    break;
                }
                if (line1 == null || line2 == null){
                    throw new WrongAnswerException(true);
                }

                line1 = line1.replaceAll("(\\n|\\s)", "");
                line2 = line2.replaceAll("(\\n|\\s)", "");
                if (!line1.equals(line2)){
                    throw new WrongAnswerException(line1, line2, lineCnt);
                }
                lineCnt++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new TestSystemException();
        }
    }

    public static void testSolution(File compiledFile, ProgrammingLanguages language, int timeLimit, List<String> judgeTestSet){
        ProcessBuilder pb = new ProcessBuilder();

        if (language == ProgrammingLanguages.Java){
            pb.command("java", compiledFile.getAbsolutePath());
        } else if (language == ProgrammingLanguages.Cpp){
            pb.command("g++", compiledFile.getAbsolutePath());
        }

        try{
            for (int i = 0; i < judgeTestSet.size(); i++){
                Process process = pb.start();
                long start = System.currentTimeMillis();
                boolean successEnd = process.waitFor(timeLimit, TimeUnit.SECONDS);
                long duration = System.currentTimeMillis() - start;
                if (!successEnd) {
                    throw new TimeLimitException();
                }
                int exitCode = process.exitValue();
                if (exitCode != 0){
                    throw new RuntimeErrorException();
                }
                exactMatchCheck(judgeTestSet.get(i), process.getInputStream());
            }
        } catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            throw new TestSystemException();
        }
    }



    public static void processSolution(File sourceCode, ProgrammingLanguages language, int timeLimit, int compileTimeLimit){
        try {
            File compiledFile = compileSolution(sourceCode, language, compileTimeLimit);
            List<String> list = List.of("1 2", "3 4");
            testSolution(compiledFile, language, timeLimit, list);
        } catch (Exception ex){

        }
    }

    public static void main(String[] args) {

    }
}