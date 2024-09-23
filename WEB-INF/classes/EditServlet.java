import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class EditServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder editHtml = new StringBuilder();

        try {
            // DATABASE CONNECTION LINE
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/QuizApp", "root", "Cathgirlh6*"); // MySQL connection

            // Get quiz name from request parameters
            String quizName = req.getParameter("quizName");
            if (quizName == null || quizName.isEmpty()) {
                res.sendRedirect("home"); // If no quiz name is provided, redirect to home
                return;
            }

            // Query the database for the quiz details
            String quizSql = "SELECT name, description FROM quizzes WHERE name = ?";
            ps = con.prepareStatement(quizSql);
            ps.setString(1, quizName);
            rs = ps.executeQuery();

            if (rs.next()) {
                String quizTitle = rs.getString("name");
                String quizDescription = rs.getString("description");

                // Generate the edit form for the quiz
                editHtml.append("<h2>Edit Quiz: ").append(quizTitle).append("</h2>")
        .append("<form method='post' action='edit'>")
        .append("<label for='title'>Quiz Title:</label>")
        .append("<input type='text' id='title' name='title' value='").append(quizTitle).append("'><br>")
        .append("<label for='description'>Description:</label>")
        .append("<textarea id='description' name='description'>").append(quizDescription).append("</textarea><br>")
        .append("<input type='hidden' name='quizName' value='").append(quizName).append("'>")
        .append("<div class='button-container'>")
        .append("<a href='editQuestions?quizName=").append(quizName).append("' class='button-link'>Edit Questions</a>")
        .append("<button type='submit'>Save Changes</button>")
        .append("</div>")
        .append("</form>");


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Set the form as a request attribute and forward it to the JSP for rendering
        req.setAttribute("editFormHtml", editHtml.toString());
        RequestDispatcher view = req.getRequestDispatcher("/views/edit.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            // DATABASE CONNECTION LINE
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", ""); // MySQL connection

            // Get form data from the request
            String quizName = req.getParameter("quizName");
            String title = req.getParameter("title");
            String description = req.getParameter("description");

            // Update quiz in the database
            String updateQuizSql = "UPDATE quizzes SET name = ?, description = ? WHERE name = ?";
            ps = con.prepareStatement(updateQuizSql);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, quizName);
            ps.executeUpdate();

            // Redirect back to home after successful update
            res.sendRedirect("home");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
