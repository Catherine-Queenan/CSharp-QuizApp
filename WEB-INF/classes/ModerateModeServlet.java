import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;

public class ModerateModeServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Check if user is logged in, if not, redirect to login
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        // Check if a quiz has been selected
        String quizName = req.getParameter("quizName");
        if (quizName == null || quizName.trim().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quiz name");
            return;
        }

        Connection con = null;
        PreparedStatement stmntQuestion = null;
        ResultSet rsQuestion = null;
        StringBuilder questionsHtml = new StringBuilder();

        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username);
        req.setAttribute("role", role);
        req.setAttribute("userName", username);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();

            // Query to get questions, answers, and media
            String sql = "SELECT q.id AS question_id, q.question_text, a.id AS answer_id, a.answer_text, a.is_correct, "
                    +
                    "qm.media_id AS question_media_id, am.media_id AS answer_media_id " +
                    "FROM questions q " +
                    "LEFT JOIN answers a ON q.id = a.question_id " +
                    "LEFT JOIN question_media qm ON q.id = qm.question_id " +
                    "LEFT JOIN answer_media am ON a.id = am.answer_id " +
                    "WHERE q.quiz_name = ?";
            stmntQuestion = con.prepareStatement(sql);
            stmntQuestion.setString(1, quizName);
            rsQuestion = stmntQuestion.executeQuery();

            // Generate HTML for each question and answer
            String previousQuestionId = "";
            int questionNumber = 0;
            while (rsQuestion.next()) {
                String questionId = rsQuestion.getString("question_id");
                String questionText = rsQuestion.getString("question_text");
                String answerText = rsQuestion.getString("answer_text");
                boolean isCorrect = rsQuestion.getBoolean("is_correct");

                // Media IDs for the current question and answer
                InputStream questionMediaId = rsQuestion.getBinaryStream("question_media_id");
                InputStream answerMediaId = rsQuestion.getBinaryStream("answer_media_id");

                // Check if it's a new question
                if (!questionId.equals(previousQuestionId)) {
                    // Close the previous question's answer section if needed
                    if (!previousQuestionId.isEmpty()) {
                        questionsHtml.append("</div>"); // Close previous question's answers div
                    }

                    // Start a new question
                    questionsHtml.append("<div class='question'>")
                            .append("<p class='questionTitle'>").append(questionText).append("</p>")
                            .append("<div class='answers'>");

                    // Append question media (if available)
                    String questionMediaHtml = insertMedia(con, "question", questionMediaId, "IMG");
                    if (questionMediaHtml != null) {
                        questionsHtml.append(questionMediaHtml);
                    }

                    // Update the previous question ID to the current one
                    previousQuestionId = questionId;

                    // Now, increment the question number, since we are starting a new question
                    questionNumber++;
                }

                // Append answers
                String answerClass = isCorrect ? "answer correct" : "answer";
                questionsHtml.append("<p data-question=").append(questionNumber).append(" class='").append(answerClass)
                        .append("'>").append(answerText).append("</p>");

                // Append answer media (if available)
                String answerMediaHtml = insertMedia(con, "answer", answerMediaId, "IMG");
                if (answerMediaHtml != null) {
                    questionsHtml.append(answerMediaHtml);
                }
            }

            // Close the last question's answer section
            if (!previousQuestionId.isEmpty()) {
                questionsHtml.append("</div>");
            }

            // Close the last question's answer section
            if (!previousQuestionId.isEmpty()) {
                questionsHtml.append("</div>");
            }

            req.setAttribute("questionsHtml", questionsHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching questions");
            return;
        } finally {
            try {
                if (stmntQuestion != null)
                    stmntQuestion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rsQuestion != null)
                    rsQuestion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Forward the request to moderateMode.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/moderateMode.jsp");
        view.forward(req, res);
    }

    // Media handler for both question and answer media
    public String insertMedia(Connection con, String table, InputStream id, String type) {
        PreparedStatement pstmntMedia = null;
        PreparedStatement pstmntMediaId = null;
        ResultSet rsMedia = null;
        ResultSet rsMediaId = null;
        StringBuilder mediaHtml = new StringBuilder();

        try {
            if (table.equalsIgnoreCase("answer")) {
                pstmntMediaId = con.prepareStatement("SELECT media_id FROM answer_media WHERE answer_id = ?");
            } else if (table.equalsIgnoreCase("question")) {
                pstmntMediaId = con.prepareStatement("SELECT media_id FROM question_media WHERE question_id = ?");
            } else {
                return null;
            }

            pstmntMediaId.setBinaryStream(1, id);
            rsMediaId = pstmntMediaId.executeQuery();
            InputStream mediaId = null;
            if (rsMediaId.next()) {
                mediaId = rsMediaId.getBinaryStream("media_id");
            }

            pstmntMedia = con.prepareStatement(
                    "SELECT media_file_path, media_start, media_end, description FROM media WHERE id = ?");
            pstmntMedia.setBinaryStream(1, mediaId);
            rsMedia = pstmntMedia.executeQuery();

            // Handle different media types
            if (rsMedia.next()) {
                String filePath = rsMedia.getString("media_file_path");
                String mediaStart = rsMedia.getString("media_start");
                String mediaEnd = rsMedia.getString("media_end");
                String description = rsMedia.getString("description");

                switch (type) {
                    case "VID":
                        mediaHtml.append("<video controls src='").append(filePath).append("#t=").append(mediaStart)
                                .append(",").append(mediaEnd).append("'></video>");
                        break;
                    case "IMG":
                        mediaHtml.append("<img src='").append(filePath).append("' alt='").append(description)
                                .append("' />");
                        break;
                    case "AUD":
                        mediaHtml.append("<audio controls src='").append(filePath).append("#t=").append(mediaStart)
                                .append(",").append(mediaEnd).append("'></audio>");
                        break;
                }
            }

            return mediaHtml.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (pstmntMedia != null)
                    pstmntMedia.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rsMedia != null)
                    rsMedia.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmntMediaId != null)
                    pstmntMediaId.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rsMediaId != null)
                    rsMediaId.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            ps = con.prepareStatement("SELECT role FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                role = rs.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return role;
    }
}
