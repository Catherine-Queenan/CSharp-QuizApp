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
        String role = (String) session.getAttribute("USER_ROLE");
        String sessionId = req.getParameter("sessionId");
        if (sessionId != null) {
            System.out.println("SESSIONLIGJGDSLJ:LIJDSGODLSF " + sessionId);
            req.setAttribute("role", role);
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

        String role = (String) session.getAttribute("USER_ROLE");
        session.setAttribute("currQuestion", 0);
        session.setAttribute("role", role);
    
        // Redirect to the login page
        res.setStatus(302);
        res.sendRedirect("questions");
    }
}
