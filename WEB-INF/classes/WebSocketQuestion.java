
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class WebSocketQuestion extends AClass {
    String questionId;
    String questionText;
    String[] answers;
    int indexOfCorrect;
    String quizName;

    public WebSocketQuestion() {
        super("websocket");
    }

    public WebSocketQuestion(String constructorParams) {
        super("websocket");

        String[] keyvaluePairs = constructorParams.split(",");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":==");
            if(keyvaluePair.length > 1){
            switch (keyvaluePair[0]) {
                case "questionId":
                    this.questionId = keyvaluePair[1];
                    break; // Assuming id is being set as a string representation
                case "questionText":
                    this.questionText = keyvaluePair[1];
                    break;
                case "answers":
                    this.answers = keyvaluePair[1].split(",,,");
                    break;
                case "indexOfCorrect":
                    this.indexOfCorrect = Integer.parseInt(keyvaluePair[1]);
                    break;
                case "media_id": 
                    this.quizName = keyvaluePair[1]; 
                    break;
            }
        }
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject jo = new JSONObject();
        
        try {
            jo.put("questionId", this.questionId);
            jo.put("questionText", this.questionText);
            jo.put("indexOfCorrect", this.indexOfCorrect);
            jo.put("quizName", this.quizName);
            
            // Convert answers array to JSONArray
            if (this.answers != null) {
                JSONArray answersArray = new JSONArray();
                for (String answer : this.answers) {
                    answersArray.put(answer);
                }
                jo.put("answers", answersArray);
            } else {
                jo.put("answers", new JSONArray()); // Empty array if answers is null
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return jo;
    }
}

// CREATE TABLE QuestionsWithAnswers (
// questionId VARCHAR(257) NOT NULL,
//     questionText TEXT NOT NULL,
//     answers JSON NOT NULL,
//     indexOfCorrect INT NOT NULL,
//     quizName VARCHAR(255) NOT NULL,
//     PRIMARY KEY (quizName, questionId)  -- Example of a composite primary key
// );