import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
        PreparedStatement psQuiz = null;
        ResultSet rsQuiz = null;
        PreparedStatement psMedia = null;
        PreparedStatement psQuizMedia = null;
        ResultSet rsMedia = null;
        ResultSet rsQuizMedia = null;

        List<Map<String, String>> quizzes = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();

            psQuiz = con.prepareStatement("SELECT name, description FROM quizzes WHERE category_name = ?;");
            psQuiz.setString(1, category);
            rsQuiz = psQuiz.executeQuery();

            psMedia = con.prepareStatement("SELECT media_id FROM quiz_media WHERE quiz_name = ?");
            psQuizMedia = con.prepareStatement("SELECT media_file_path FROM media WHERE id = ?");

            while (rsQuiz.next()) {
                String quizName = rsQuiz.getString("name");
                String quizDescription = rsQuiz.getString("description");

                psMedia.setString(1, quizName);
                rsMedia = psMedia.executeQuery();

                StringBuilder mediaHtml = new StringBuilder();
                while (rsMedia.next()) {
                    InputStream mediaId = rsMedia.getBinaryStream("media_id");

                    if (mediaId != null) {
                        psQuizMedia.setBinaryStream(1, mediaId);
                        rsQuizMedia = psQuizMedia.executeQuery();
                        if (rsQuizMedia.next()) {
                            String mediaFilePath = rsQuizMedia.getString("media_file_path");
                            mediaHtml.append("<img src=\"").append(mediaFilePath).append("\" alt=\"").append(quizName).append("\" class=\"categoryImg\">");
                        }
                    }
                }

                Map<String, String> quizData = new HashMap<>();
                quizData.put("name", quizName);
                quizData.put("description", quizDescription);
                quizData.put("mediaHtml", mediaHtml.toString());
                quizzes.add(quizData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rsQuiz != null) rsQuiz.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (rsMedia != null) rsMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (rsQuizMedia != null) rsQuizMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psQuiz != null) psQuiz.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psMedia != null) psMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psQuizMedia != null) psQuizMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }

        //Application of functional programming: Use Stream API to generate HTML
        String quizzesHtml = quizzes.stream()
            .map(quiz -> {
                String quizName = quiz.get("name");
                String quizDescription = quiz.get("description");
                String mediaHtml = quiz.get("mediaHtml");

                StringBuilder html = new StringBuilder();
                html.append("<div class=\"quiz\">\n")
                    .append("<form method=\"post\">\n")
                    .append("    <input type=\"hidden\" name=\"quizName\" value=\"").append(quizName).append("\" />\n")
                    .append("    <input type=\"submit\" value=\"").append(quizName).append("\" />\n")
                    .append("<p class=\"quiz-description\">").append(quizDescription).append("</p>\n")
                    .append("  <div class=\"img\">").append(mediaHtml).append("</div>\n")
                    .append("</form>\n");

                // Show "Add Question" and "Delete Quiz" buttons only for admin users
                if ("a".equals(role)) {
                    html.append("<div class=\"adminBtnWrap\">")
                        .append("    <button type=\"button\" onclick=\"window.location.href='deleteQuiz?quizName=")
                        .append(quizName).append("'\">Delete Quiz</button>\n")
                        .append("    <button type=\"button\" onclick=\"window.location.href='edit?quizName=")
                        .append(quizName).append("'\">Edit Quiz</button>\n</div>");
                }

                html.append("</div>\n");
                return html.toString();
            })
            .collect(Collectors.joining());

        req.setAttribute("quizzesHtml", quizzesHtml);
        // Forward the request to the quiz.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/quiz.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();
            ps = con.prepareStatement("SELECT id FROM questions WHERE quiz_name = ? ORDER BY rand();");
            ps.setString(1, quizName);
            rs = ps.executeQuery();

            ArrayList<InputStream> qIDs = new ArrayList<>();
            while (rs.next()) {
                InputStream qID = rs.getBinaryStream("id");
                qIDs.add(qID);
            }

            HttpSession session = req.getSession(false);
            if (session != null) {
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
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
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
