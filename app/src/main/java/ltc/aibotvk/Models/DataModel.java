package ltc.aibotvk.Models;

/**
 * Created by admin on 30.06.2018.
 */

public class DataModel {
    private String answer, sentence;

    public DataModel(String sentence, String answer){
        this.answer = answer;
        this.sentence = sentence;
    }

    public String getAnswer(){
        return answer;
    }

    public String getSentence(){
        return sentence;
    }

    public void setData(String sentence, String answer){
        this.sentence = sentence;
        this.answer = answer;
    }
}
