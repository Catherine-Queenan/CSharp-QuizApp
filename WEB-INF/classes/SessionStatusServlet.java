import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class SessionStatusServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get current session, do not create if it doesn't exist
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null); // Check if user attribute exists

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"loggedIn\": " + isLoggedIn + "}");
        out.flush();
    }
}
