import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Invalidate the current session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect to the login page
        res.sendRedirect("login");
    }
}
