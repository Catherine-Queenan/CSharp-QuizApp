import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/questionsws")
public class QuestionWebSocket {

    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static List<WebSocketQuestion> questions = new ArrayList<>();

    static class WebSocketQuestion {
        String questionText;
        List<String> answers;

        WebSocketQuestion(String questionText, List<String> answers) {
            this.questionText = questionText;
            this.answers = answers;
        }
    }

    // User session data class as a static inner class
    static class UserSessionData {
        List<WebSocketQuestion> questions = new ArrayList<>();
        int currentQuestionIndex = 0;
        Map<String, Integer> answerCounts = new ConcurrentHashMap<>(); // Track answer counts for this user
    }

    private static Map<Session, UserSessionData> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        userSessions.put(session, new UserSessionData());
        System.out.println("New connection: " + session.getId());

        try {
            sendCurrentQuestion(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        UserSessionData userData = userSessions.get(session);

        try {
            if (message.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(message);
                userData.questions.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObj = jsonArray.getJSONObject(i);
                    String questionText = questionObj.getString("question");
                    JSONArray jsonAnswers = questionObj.getJSONArray("answers");

                    List<String> answerList = new ArrayList<>();
                    for (int j = 0; j < jsonAnswers.length(); j++) {
                        answerList.add(jsonAnswers.getString(j));
                    }

                    userData.questions.add(new WebSocketQuestion(questionText, answerList));
                }

                sendCurrentQuestion(session);
            } else {
                JSONObject jsonMessage = new JSONObject(message);

                // Handle answer selection
                if (jsonMessage.has("type") && jsonMessage.getString("type").equals("answer")) {
                    String answer = jsonMessage.getString("answer");
                    // Update this user's answer count
                    userData.answerCounts.put(answer, userData.answerCounts.getOrDefault(answer, 0) + 1);

                    // Optionally, broadcast this user's answer counts
                    broadcastAnswerCounts(session);
                }
                // Handle next question request
                else if (jsonMessage.has("type") && jsonMessage.getString("type").equals("next")) {
                    userData.currentQuestionIndex++;
                    sendCurrentQuestion(session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        userSessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    private void sendCurrentQuestion(Session session) throws IOException {
        UserSessionData userData = userSessions.get(session);
        if (userData.currentQuestionIndex < userData.questions.size()) {
            WebSocketQuestion currentQuestion = userData.questions.get(userData.currentQuestionIndex);
            JSONObject json = new JSONObject();
            json.put("question", currentQuestion.questionText);
            json.put("answers", currentQuestion.answers);
            session.getBasicRemote().sendText(json.toString());
        }
    }

    private void broadcastAnswerCounts(Session session) throws IOException {
        UserSessionData userData = userSessions.get(session);
        JSONObject json = new JSONObject();
        json.put("type", "answerCounts");
        json.put("counts", userData.answerCounts);

        session.getBasicRemote().sendText(json.toString()); // Send only to the specific user
    }

    private void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
