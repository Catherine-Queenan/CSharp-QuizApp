import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class QuizServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        StringBuilder quizzesHtml = new StringBuilder();

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root", "q12773250P");
            statement = con.createStatement();

            // Query database for quizzes
            String sql = "SELECT name FROM quizzes";
            rs = statement.executeQuery(sql);

            // Generate HTML for each quiz
            while (rs.next()) {
                String quizName = rs.getString("name");
                quizzesHtml.append("<div class=\"quiz\">\n")
                           .append("<form action=\"questions\" method=\"get\">\n")
                           .append("    <input type=\"hidden\" name=\"quizName\" value=\"" + quizName + "\" />\n")
                           .append("    <input type=\"submit\" value=\"" + quizName + "\" />\n")
                           .append("</form>\n")
                           .append("  <div class=\"img\"></div>\n")
                           .append("</div>\n");
            }

            // Set quizzes as request attribute
            req.setAttribute("quizzesHtml", quizzesHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to the quiz.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/quiz.jsp");
        view.forward(req, res);
    }
}
