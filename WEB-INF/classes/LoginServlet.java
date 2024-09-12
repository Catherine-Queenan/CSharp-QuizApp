import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;

public class LoginServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("/views/login.html");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       	
      }
}