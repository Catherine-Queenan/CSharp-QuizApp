import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.security.Principal; // Import Principal
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/questionsws")
public class QuestionWebSocket {

    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static List<WebSocketQuestion> questions = new ArrayList<>();
    private static int questionIndex = 0;

    static class WebSocketQuestion {
        String questionText;
        List<String> answers;
        List<String> images;
        List<String> videos;

        WebSocketQuestion(String questionText, List<String> answers, List<String> images, List<String> videos) {
            this.questionText = questionText;
            this.answers = answers;
            this.images = images;
            this.videos = videos;
        }
    }

    static class UserSessionData {
        String username;
        List<WebSocketQuestion> questions = new ArrayList<>();
        int currentQuestionIndex = 0;
        Map<String, Integer> answerCounts = new ConcurrentHashMap<>();

        public UserSessionData(String username) {
            this.username = username;
        }
    }

    private static Map<Session, UserSessionData> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        Principal userPrincipal = session.getUserPrincipal();
        String userName = (userPrincipal != null) ? userPrincipal.getName() : "Guest";

        System.out.println("Connection opened: " + session.getId() + " with user: " + userName);

        UserSessionData userData = new UserSessionData(userName);
        userSessions.put(session, userData);
        sessions.add(session);

        userData.questions.clear();

        try {
            sendCurrentQuestion(session); // This will send the first question if any
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
                userData.currentQuestionIndex = 0;
                JSONArray jsonArray = new JSONArray(message);
                userData.questions.clear();
                userData.answerCounts.clear();
                userData.currentQuestionIndex = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObj = jsonArray.getJSONObject(i);
                    String questionText = questionObj.getString("question");

                    // Parse answers
                    JSONArray jsonAnswers = questionObj.getJSONArray("answers");
                    List<String> answerList = new ArrayList<>();
                    for (int j = 0; j < jsonAnswers.length(); j++) {
                        answerList.add(jsonAnswers.getString(j));
                    }

                    // Parse images
                    JSONArray jsonImages = questionObj.getJSONArray("images");
                    List<String> imageList = new ArrayList<>();
                    for (int j = 0; j < jsonImages.length(); j++) {
                        imageList.add(jsonImages.getString(j));
                    }

                    // Parse videos
                    JSONArray jsonVideos = questionObj.getJSONArray("videos");
                    List<String> videoList = new ArrayList<>();
                    for (int j = 0; j < jsonVideos.length(); j++) {
                        videoList.add(jsonVideos.getString(j));
                    }

                    // Add question with answers, images, and videos to the user's session data
                    userData.questions.add(new WebSocketQuestion(questionText, answerList, imageList, videoList));
                }

                sendCurrentQuestion(session);
            } else {
                // Existing logic for handling answer or next messages
                JSONObject jsonMessage = new JSONObject(message);
    
                if (jsonMessage.has("type") && jsonMessage.getString("type").equals("answer")) {
                    String answer = jsonMessage.getString("answer");
                    userData.answerCounts.clear();
                    userData.answerCounts.put(answer, userData.answerCounts.getOrDefault(answer, 0) + 1);
                    broadcastAnswerCounts();
                } else if (jsonMessage.has("type") && jsonMessage.getString("type").equals("next")) {
                    if (userData.currentQuestionIndex < userData.questions.size() - 1) {
                        incrementQuestionForAllUsers();
                        broadcastCurrentQuestionToAll();
                    } else {
                        clearAllSessions();  // End quiz if all questions are answered
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Reset question index
        questionIndex = 0;
        // Remove sessions
        sessions.remove(session);
        userSessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    private void sendCurrentQuestion(Session session) throws IOException {
        UserSessionData userData = userSessions.get(session);
        if (userData.currentQuestionIndex != questionIndex) {
            userData.currentQuestionIndex = questionIndex;
        }
        if (userData.currentQuestionIndex < userData.questions.size()) {
            WebSocketQuestion currentQuestion = userData.questions.get(userData.currentQuestionIndex);
            
            JSONObject json = new JSONObject();
            json.put("question", currentQuestion.questionText);
            json.put("answers", currentQuestion.answers);
            json.put("images", currentQuestion.images);  // Send images to the client
            json.put("videos", currentQuestion.videos);  // Send videos to the client
            json.put("questionIndex", userData.currentQuestionIndex);
            
            session.getBasicRemote().sendText(json.toString());
        } else if(userData.currentQuestionIndex == userData.questions.size()-1) {
            JSONObject json = new JSONObject();
            json.put("type", "end");
            session.getBasicRemote().sendText(json.toString());
        }
    }

    private void clearAllSessions() {
        List<Session> sessionsToClose;
        
        synchronized (sessions) {
            // Create a new list from the sessions to avoid ConcurrentModificationException
            sessionsToClose = new ArrayList<>(sessions);
        }
        
        for (Session session : sessionsToClose) {
            try {
                session.close(); // Close the session
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Reset question index
        questionIndex = 0;
        
        // Clear the user sessions and the sessions set after closing
        userSessions.clear();
        sessions.clear();
        System.out.println("All sessions cleared.");
    }
    

    private void broadcastAnswerCounts() throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", "answerCounts");
        json.put("counts", getCombinedAnswerCounts());
        broadcast(json.toString());
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

    private Map<String, Integer> getCombinedAnswerCounts() {
        Map<String, Integer> combinedCounts = new HashMap<>();
        combinedCounts.clear();
        for (UserSessionData userData : userSessions.values()) {
            userData.answerCounts.forEach((key, value) -> {
                combinedCounts.put(key, combinedCounts.getOrDefault(key, 0) + value);
            });
        }
        return combinedCounts;
    }

    private void incrementQuestionForAllUsers() {
        questionIndex++;
        for (UserSessionData userData : userSessions.values()) {
            userData.currentQuestionIndex++;
            userData.answerCounts.clear();
        }
    }

    private void broadcastCurrentQuestionToAll() throws IOException {
        for (Session session : sessions) {
            sendCurrentQuestion(session);
        }
    }
}