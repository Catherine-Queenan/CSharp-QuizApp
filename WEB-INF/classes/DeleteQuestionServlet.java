import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class DeleteQuestionServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String questionId = req.getParameter("id");
        String quizName = req.getParameter("quizName");

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // DATABASE CONNECTION LINE
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // Class.forName("oracle.jdbc.OracleDriver"); // Oracle Driver

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", ""); // MySQL connection
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1"); // Oracle connection

            // Delete the question
            String deleteQuestionSql = "DELETE FROM questions WHERE id = ?";
            ps = con.prepareStatement(deleteQuestionSql);
            ps.setString(1, questionId);
            ps.executeUpdate();

            // Redirect back to the edit questions page after successful deletion
            res.sendRedirect("editQuestions?quizName=" + quizName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
