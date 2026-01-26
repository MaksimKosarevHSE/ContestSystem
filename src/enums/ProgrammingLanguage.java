package enums;

import testing.exceptions.RuntimeErrorException;

import java.nio.file.Path;
import java.util.Arrays;

public enum ProgrammingLanguage {
    Java(true, ".java", ".class"), Cpp(true, ".cpp", ".exe");
    public final boolean needCompilation;
    public final String sourceSuffix;
    public final String compiledSuffix;
    ProgrammingLanguage(boolean fir, String sec, String th){
        needCompilation = fir;
        sourceSuffix = sec;
        compiledSuffix = th;
    }

    public String[] getCompileCommand(Path source) {
        if (!this.needCompilation) throw new IllegalArgumentException("This language can't be compiled");

        if (this == Java){
            return new String[] {"javac", source.toString()};
        }
        if (this == Cpp){
            String fileName =  source.getFileName().toString();
            String target = source.getParent().resolve( fileName.substring(0, fileName.lastIndexOf('.')) + compiledSuffix).toString();
            var tmp = new String [] {"g++", source.toString(), "-o", target};
//            var tmp = new String [] {"g++", source.toString()};
//            System.out.println(Arrays.toString(tmp));
            return tmp;
        }
        return new String[]{};
    }


    public String[] getRunCommand(Path compiled){
        if (this == Java){
            return new String[] {"java", compiled.toString()};
        }
        if (this == Cpp){
            return new String[] {compiled.toString()};
        }
        return new String[]{};
    }

}
