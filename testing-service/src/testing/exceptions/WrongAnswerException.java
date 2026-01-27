package testing.exceptions;

public class WrongAnswerException extends RuntimeException{
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