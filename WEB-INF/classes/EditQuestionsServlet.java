import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class EditQuestionsServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.sendRedirect("login");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder questionsHtml = new StringBuilder();

        try {
            // DATABASE CONNECTION LINE
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // Class.forName("oracle.jdbc.OracleDriver"); // Oracle Driver

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "q12773250P"); // MySQL connection
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1"); // Oracle connection

            // Get quiz name from request parameters
            String quizName = req.getParameter("quizName");
            if (quizName == null || quizName.isEmpty()) {
                res.sendRedirect("home"); // If no quiz name is provided, redirect to home
                return;
            }

            // Query the database for questions related to the quiz
            String questionsSql = "SELECT id, question_text FROM questions WHERE quiz_name = ?";
            ps = con.prepareStatement(questionsSql);
            ps.setString(1, quizName);
            rs = ps.executeQuery();

            // Generate HTML for questions
            boolean hasQuestions = false;
            while (rs.next()) {
                hasQuestions = true;
                String questionId = rs.getString("id");
                String questionText = rs.getString("question_text");

                questionsHtml.append("<div class='col-md-12'>")
                        .append("<div class='card question-card'>")
                        .append("<div class='card-body'>")
                        .append("<p class='card-text'>").append(questionText).append("</p>")
                        .append("<a href='deleteQuestion?id=").append(questionId).append("&quizName=").append(quizName).append("' class='btn btn-danger'>Delete Question</a>")
                        .append("</div>")
                        .append("</div>")
                        .append("</div>");
            }

            if (!hasQuestions) {
                questionsHtml.append("<p>There are currently no questions for this quiz.</p>");
            }

            // Set quiz name and questions HTML as request attributes
            req.setAttribute("quizName", quizName);
            req.setAttribute("questionsHtml", questionsHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward to JSP
        RequestDispatcher view = req.getRequestDispatcher("/views/editQuestions.jsp");
        view.forward(req, res);
    }
    private String getUserRoleFromDatabase(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String role = null;

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root", "q12773250P");

            // Query to get the user's role
            String sql = "SELECT role FROM users WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                role = rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return role;
    }
}
