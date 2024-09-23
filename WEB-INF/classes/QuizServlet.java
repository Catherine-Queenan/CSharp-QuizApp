import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class QuizServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username); // Fetch the role from DB
        String category = req.getParameter("categoryName");

        Connection con = null;
        Statement stmnt = null;
        ResultSet rs = null;
        StringBuilder quizzesHtml = new StringBuilder();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();
            stmnt = con.createStatement();
            rs = stmnt.executeQuery("SELECT name, description FROM quizzes WHERE category_name = \"" + category + "\";");

            while (rs.next()) {
                String quizName = rs.getString("name");
                String quizDescription = rs.getString("description");

                quizzesHtml.append("<div class=\"quiz\">\n")
                        .append("<form method=\"post\">\n")
                        .append("    <input type=\"hidden\" name=\"quizName\" value=\"").append(quizName).append("\" />\n")
                        .append("    <input type=\"submit\" value=\"").append(quizName).append("\" />\n")
                        // .append("    <label for=\"").append(quizName).append("\">").append(quizName).append("</label>")
                        .append("<p class=\"quiz-description\">").append(quizDescription).append("</p>\n")
                        .append("</form>\n");
                
                // Show "Add Question" and "Delete Quiz" buttons only for admin users
                if ("a".equals(role)) {
                    quizzesHtml.append("<div class=\"adminBtnWrap\">")
                            // .append("    <button type=\"button\" onclick=\"window.location.href='addQuestion?quizName=")
                            // .append(quizName).append("'\">Add Question</button>\n")
                            .append("    <button type=\"button\" onclick=\"window.location.href='deleteQuiz?quizName=")
                            .append(quizName).append("'\">Delete Quiz</button>\n")
                            .append("    <button type=\"button\" onclick=\"window.location.href='edit?quizName=")
                            .append(quizName).append("'\">Edit Quiz</button>\n</div>");
                }

                quizzesHtml.append("</div>\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (stmnt != null) stmnt.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        req.setAttribute("quizzesHtml", quizzesHtml.toString());
        // Forward the request to the quiz.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/quiz.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");

        Connection con = null;
        Statement stmnt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();
            stmnt = con.createStatement();
            rs = stmnt.executeQuery("SELECT id FROM questions WHERE quiz_name = \"" + quizName + "\" ORDER BY rand();");

            ArrayList<InputStream> qIDs = new ArrayList<>();
            while (rs.next()) {
                InputStream qID = rs.getBinaryStream("id");
                qIDs.add(qID);
            }

            HttpSession session = req.getSession(false);
            if(session != null){
                session.setAttribute("quiz", quizName);
                session.setAttribute("questions", qIDs);
                session.setAttribute("currQuestion", 0);
                
                res.setStatus(302);
                res.sendRedirect("questions");
            } else {
                res.setStatus(302);
                res.sendRedirect("login");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (stmnt != null) stmnt.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private String getUserRoleFromDatabase(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String role = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DatabaseUtil.getConnection();
            
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
