package com.maksim.common.enums;

import java.nio.file.Path;

public enum ProgrammingLanguage {
    Java(true, ".java", ".class"),
    Cpp(true, ".cpp", ".exe");

    public final boolean needCompilation;
    public final String sourceSuffix;
    public final String compiledSuffix;

    ProgrammingLanguage(boolean needCompilation, String sourceSuffix, String compiledSuffix) {
        this.needCompilation = needCompilation;
        this.sourceSuffix = sourceSuffix;
        this.compiledSuffix = compiledSuffix;
    }

    public String[] getCompileCommand(Path source) {
        if (!needCompilation) {
            throw new IllegalArgumentException("This language can't be compiled");
        }
        if (this == Java) {
            return new String[]{"javac", source.toString()};
        }
        if (this == Cpp) {
            String fileName = source.getFileName().toString();
            String target = source.getParent()
                    .resolve(fileName.substring(0, fileName.lastIndexOf('.')) + compiledSuffix)
                    .toString();
            return new String[]{"g++", source.toString(), "-o", target};
        }
        return new String[]{};
    }

    public String[] getRunCommand(Path compiled) {
        if (this == Java) {
            return new String[]{"java", compiled.toString()};
        }
        if (this == Cpp) {
            return new String[]{compiled.toString()};
        }
        return new String[]{};
    }
}
