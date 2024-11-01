import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;
import org.json.JSONObject;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Invalidate the current session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        res.addCookie(tokenCookie);

        // Prepare JSON response
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Logged out successfully");

        // Set response content type to JSON
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);

        // Write JSON response to the response
        PrintWriter out = res.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}