
import java.util.List;
public class WebSocketQuestion {
    String questionText;
    List<String> answers;

    public WebSocketQuestion(String questionText, List<String> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }
}