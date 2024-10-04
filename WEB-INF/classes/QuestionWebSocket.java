import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import org.json.JSONException;

@ServerEndpoint("/questionsws")
public class QuestionWebSocket {

     private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
     private static List<String> questions = new ArrayList<>();
     private static Map<String, Integer> answers = new HashMap<>();
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);

        // Parse the JSON message
    //     JSONObject json = new JSONObject(message);
    //     String type = json.getString("type");
    //     String question = json.getString("question");
    //     String answer = json.getString("answer");
        if(answers.containsKey(message)){
            answers.put(message, answers.get(message) + 1);
        } else {
            answers.put(message, 1);
        }
       broadcast(answers);

    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

   
    private void broadcast(Map<String, Integer> answers2) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(new JSONObject(answers2).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
