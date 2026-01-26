package testing.exceptions;

public class CompilationException extends RuntimeException{
    int exitCode;
    public CompilationException(int exitCode, String msg){
        super(msg);
        this.exitCode = exitCode;
    }
}