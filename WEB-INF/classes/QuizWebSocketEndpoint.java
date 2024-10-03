// import jakarta.websocket.OnClose;
// import jakarta.websocket.OnMessage;
// import jakarta.websocket.OnOpen;
// import jakarta.websocket.Session;
// import jakarta.websocket.server.ServerEndpoint;

// import java.io.IOException;
// import java.util.Set;
// import java.util.concurrent.CopyOnWriteArraySet;

// @ServerEndpoint("/quizWebSocket")
// public class QuizWebSocketEndpoint {

//     private static Set<Session> clients = new CopyOnWriteArraySet<>();

//     @OnOpen
//     public void onOpen(Session session) {
//         clients.add(session);
//         System.out.println("New connection opened: " + session.getId());
//     }

//     @OnMessage
//     public void onMessage(String message, Session session) {
//         // Handle incoming messages (if necessary)
//     }

//     @OnClose
//     public void onClose(Session session) {
//         clients.remove(session);
//         System.out.println("Connection closed: " + session.getId());
//     }

//     public static void broadcast(String message) {
//         for (Session client : clients) {
//             if (client.isOpen()) {
//                 try {
//                     client.getBasicRemote().sendText(message);
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }
// }
