import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;


public class ModeratedSessionServlet extends HttpServlet {
    private final ModerationSessionManager moderationSessionManager = new ModerationSessionManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }
        
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        JSONObject jsonResponse = new JSONObject();
        String action = req.getParameter("action");
        
        try {
            if ("startModeratedSession".equalsIgnoreCase(action)) {
                // Start a moderated session
                String moderatorId = (String) session.getAttribute("USER_ID");
                String quizName = req.getParameter("quizName");
                String modSessionId = moderationSessionManager.startModeratedSession(moderatorId, quizName);

                jsonResponse.put("status", "success");
                jsonResponse.put("sessionId", modSessionId);
            } else if ("endModeratedSession".equalsIgnoreCase(action)) {
                // End a moderated session
                String modSessionId = req.getParameter("sessionId");
                moderationSessionManager.endModeratedSession(modSessionId);

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Session ended successfully.");
            } else if ("getActiveSessions".equalsIgnoreCase(action)) {
                // Get active moderated sessions
                jsonResponse.put("status", "success");
                jsonResponse.put("sessions", moderationSessionManager.getActiveSessions());
            } else if ("getModeratedSession".equalsIgnoreCase(action)) {
                // Get moderated session details
                String modSessionId = req.getParameter("sessionId");
                String quizName = req.getParameter("quizName");
                jsonResponse.put("status", "success");
                jsonResponse.put("session", moderationSessionManager.getModeratedSession(modSessionId, quizName));
            
             }else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid action.");
            }
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.print(jsonResponse);
            out.flush();
        }
    }
}
