
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONObject;

public class Question extends AClass {
    private byte[] id;
    private String quiz_name = "";
    private String question_text = "";
    private String question_type = "";
    private byte[] media_id = null;

    public Question() {
        super("question");
    }

    public Question(String constructorParams) {
        super("question");
        String[] keyvaluePairs = constructorParams.split(",,,");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":==");
            if(keyvaluePair.length > 1){
            switch (keyvaluePair[0]) {
                case "id":
                    this.id = keyvaluePair[1].getBytes();
                    break; // Assuming id is being set as a string representation
                case "quiz_name":
                    this.quiz_name = keyvaluePair[1];
                    break;
                case "question_text":
                    this.question_text = keyvaluePair[1];
                    break;
                case "question_type":
                    this.question_type = keyvaluePair[1];
                    break;
                case "media_id": 
                    this.media_id = keyvaluePair[1].getBytes(); 
                    break;
            }
        }
        }
    }

    public byte[] getId() {
        return this.id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getQuizName() {
        return this.quiz_name;
    }

    public void setQuizName(String quiz_name) {
        this.quiz_name = quiz_name;
    }

    public String getQuestionText() {
        return this.question_text;
    }

    public void setQuestionText(String question_text) {
        this.question_text = question_text;
    }

    public String getQuestionType() {
        return this.question_type;
    }

    public void setQuestionType(String question_type) {
        this.question_type = question_type;
    }

    public byte[] getMediaId(){
        return this.media_id;
    }

    public void setMediaId(byte[] media_id) {
        this.media_id = media_id;
    }

    @Override
    public JSONObject serialize() {
        JSONObject jo = null;
    
    try {
        jo = new JSONObject();
        jo.put("id", (this.id != null ? new String(this.id, StandardCharsets.UTF_8) : this.id));
        jo.put("quiz_name", this.quiz_name);
        jo.put("question_text", this.question_text);
        jo.put("question_type", this.question_type);
        jo.put("media_id", (this.media_id != null ? new String(this.media_id, StandardCharsets.UTF_8) : this.media_id));
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return jo;
    }
}