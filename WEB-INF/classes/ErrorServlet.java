import jakarta.servlet.http.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.sql.*;
import java.io.*;
import org.json.JSONObject;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String errorMessage = (String) req.getSession().getAttribute("errorMessage");
        req.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/error.jsp");
        dispatcher.forward(req, res);
    }
}
