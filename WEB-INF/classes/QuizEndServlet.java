import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

public class QuizEndServlet extends HttpServlet{
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }
        String sessionId = req.getParameter("sessionId");
        if (sessionId != null) {
            System.out.println("SESSIONLIGJGDSLJ:LIJDSGODLSF " + sessionId);
            req.setAttribute("sessionToEnd", sessionId);
        }
        RequestDispatcher view = req.getRequestDispatcher("/views/quizEnd.jsp");
        view.forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Invalidate the current session
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }
        session.setAttribute("currQuestion", 0);
    
        // Redirect to the login page
        res.setStatus(302);
        res.sendRedirect("questions");
    }
}
