
// import jakarta.websocket.OnClose;
// import jakarta.websocket.OnMessage;
// import jakarta.websocket.OnOpen;
// import jakarta.websocket.Session;
// import jakarta.websocket.server.ServerEndpoint;
// import java.io.IOException;
// import java.util.Set;
// import java.util.concurrent.CopyOnWriteArraySet;

// @ServerEndpoint("/websocket")
// public class WebSocketEndpoint {

//     private static Set<Session> clients = new CopyOnWriteArraySet<Session>();

//     @OnOpen
//     public void onOpen(Session session) {
//         clients.add(session);
//         System.out.println("Connected: " + session.getId());
//     }

//     @OnMessage
//     public void onMessage(String message, Session session) {
//         System.out.println("Message from client: " + message);
//         // Broadcast the message to all clients
//         for (Session client : clients) {
//             if (client.isOpen() && client != session) {
//                 try {
//                     client.getBasicRemote().sendText(message);
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }

//     @OnClose
//     public void onClose(Session session) {
//         clients.remove(session);
//         System.out.println("Disconnected: " + session.getId());
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
