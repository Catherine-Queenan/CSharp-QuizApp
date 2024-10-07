import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Answer extends AClass {
    private byte[] id;
    private byte[] question_id;
    private String answer_text;
    private int is_correct = 0;
    private String answer_type;
    private byte[] media_id = null;

    public Answer() {
        super("answer");
    }

    public Answer(String constructorParams) {
        super("answer");
        String[] keyvaluePairs = constructorParams.split(",");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":==");
            if(keyvaluePair.length > 1){
            switch (keyvaluePair[0]) {
                case "id": this.id = keyvaluePair[1].getBytes(); break; // Assuming id is being set as a string representation
                case "question_id": this.question_id = keyvaluePair[1].getBytes(); break; // Same assumption
                case "answer_text": this.answer_text = keyvaluePair[1]; break;
                case "is_correct": this.is_correct = Integer.parseInt(keyvaluePair[1]); break;
                case "answer_type": this.answer_type = keyvaluePair[1]; break;
                case "media_id": this.media_id = keyvaluePair[1].getBytes(); break;
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

    public byte[] getQuestionId() {
        return this.question_id;
    }

    public void setQuestionId(byte[] question_id) {
        this.question_id = question_id;
    }

    public String getAnswerText() {
        return this.answer_text;
    }

    public void setAnswerText(String answer_text) {
        this.answer_text = answer_text;
    }

    public int isCorrect() {
        return this.is_correct;
    }

    public void setCorrect(int is_correct) {
        this.is_correct = is_correct;
    }

    public String getAnswerType() {
        return this.answer_type;
    }

    public void setAnswerType(String answer_type) {
        this.answer_type = answer_type;
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
        jo.put("id", (this.id != null ? new String(this.id, StandardCharsets.UTF_8 ): this.id));
        jo.put("question_id", (this.question_id != null ? new String(this.question_id, StandardCharsets.UTF_8 ): this.question_id));
        jo.put("answer_text", this.answer_text);
        jo.put("is_correct", this.is_correct);
        jo.put("answer_type", this.answer_type);
        jo.put("media_id", (this.media_id != null ? new String(this.media_id, StandardCharsets.UTF_8 ): this.media_id));
    } catch (Exception e) {
        e.printStackTrace();
    }
        return jo;
    }
}
