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
        PreparedStatement stmntMedia = null;
        ResultSet rsQuestion = null;
        ResultSet rsMedia = null;
        StringBuilder questionsHtml = new StringBuilder();

        String username = (String) session.getAttribute("USER_ID");
        String sessionId = req.getParameter("sessionId");
        String role = getUserRoleFromDatabase(username);
        req.setAttribute("role", role);
        req.setAttribute("userName", username);

        if (sessionId != null) {
            try {
                // Use sessionId to retrieve the moderation session
                ModerationSession modSession = ModerationSessionManager.getModeratedSession(sessionId, quizName);
        
                // Check if modSession was retrieved successfully
                if (modSession != null) {
                    String modSessionId = modSession.getSessionId(); // Assuming getSessionId() returns the integer ID
                    System.out.println("Moderation Session ID: " + modSessionId);
        
                    // Set modSessionId in request and session attributes for later use
                    session.setAttribute("modSessionId", modSessionId);
                    req.setAttribute("modSessionId", modSessionId);
                } else {
                    // Handle the case where no session is found for the given ID
                    req.setAttribute("errorMessage", "Failed to start moderation session.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid sessionId format in URL");
                req.setAttribute("errorMessage", "Invalid session ID.");
            }
        } else {
            req.setAttribute("errorMessage", "Session ID not provided in the URL.");
        }

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
            InputStream answerMediaId = null;
            InputStream questionMediaId = null;
            while (rsQuestion.next()) {
                String questionId = rsQuestion.getString("question_id");
                String questionText = rsQuestion.getString("question_text");
                String answerText = rsQuestion.getString("answer_text");
                boolean isCorrect = rsQuestion.getBoolean("is_correct");

                // Media IDs for the current question and answer
                questionMediaId = rsQuestion.getBinaryStream("question_media_id");
                answerMediaId = rsQuestion.getBinaryStream("answer_media_id");

                // Check if it's a new question
                if (!questionId.equals(previousQuestionId)) {
                    // Close the previous question's answer section if needed
                    if (!previousQuestionId.isEmpty()) {
                        questionsHtml.append("</div>"); // Close previous question's answers div
                    }

                    // Start a new question
                    questionsHtml.append("<div class='question'>")
                            .append("<p class='questionTitle'>").append(questionText).append("</p>");
                            

                            String mediaHtml = "";
    
                            try {
                                stmntMedia = con.prepareStatement("SELECT media_file_path,media_type FROM media WHERE id = ?");
                                stmntMedia.setBinaryStream(1, questionMediaId);
                                rsMedia = stmntMedia.executeQuery();
            
                                while (rsMedia.next()) {
            
                                    String mediaFilePath = rsMedia.getString("media_file_path");
                                    String mediaType = rsMedia.getString("media_type");
            
                                    if (mediaType.equals("IMG")) {
                                        mediaHtml = "<img data-media="+ (questionNumber+1)+" src='" + mediaFilePath + "' alt='Question Media' />";
                                    } else if (mediaType.equals("VID")) {
                                        mediaHtml = "<video controls><source data-media="+ (questionNumber+1)+" src='" + mediaFilePath + "' type='video/mp4'></video>";
                                    } else if (mediaType.equals("AUD")) {
                                        mediaHtml = "<audio data-media="+ questionNumber+" controls><source src='" + mediaFilePath + "' type='audio/mp3'></audio>";
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (rsMedia != null)
                                        rsMedia.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (stmntMedia != null)
                                        stmntMedia.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
            
                            questionsHtml.append(mediaHtml)
                            .append("<div class='answers'>");

                    // Update the previous question ID to the current one
                    previousQuestionId = questionId;

                    // Now, increment the question number, since we are starting a new question
                    questionNumber++;
                }

                // Append answers
                String answerClass = isCorrect ? "answer correct" : "answer";
                questionsHtml.append("<p data-question=").append(questionNumber).append(" class='").append(answerClass)
                        .append("'>").append(answerText).append("</p>");

            }

            // Close the last question's answer section
            if (!previousQuestionId.isEmpty()) {
                questionsHtml.append("</div>");
            }

            // select filepath from media table where id = questionMediaId

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

    // Handle request to end Moderation session
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String modSessionId = req.getParameter("modSessionId");
        if (modSessionId != null) {
            ModerationSessionManager.endModeratedSession(modSessionId); // Remove the session by ID
        }
    }


    // Media handler for both question and answer media
    public String insertMedia(Connection con, String id, String table) {
        PreparedStatement pstmntMedia = null;
        ResultSet rsMedia = null;
        StringBuilder mediaHtml = new StringBuilder();

        try {
            if (table.equalsIgnoreCase("answer")) {
                pstmntMedia = con.prepareStatement(
                        "SELECT m.media_file_path, m.media_start, m.media_end, m.description " +
                                "FROM answer_media am JOIN media m ON am.media_id = m.id WHERE am.answer_id = ?");
            } else if (table.equalsIgnoreCase("question")) {
                pstmntMedia = con.prepareStatement(
                        "SELECT m.media_file_path, m.media_start, m.media_end, m.description " +
                                "FROM question_media qm JOIN media m ON qm.media_id = m.id WHERE qm.question_id = ?");
            } else {
                return null;
            }

            pstmntMedia.setString(1, id);
            rsMedia = pstmntMedia.executeQuery();

            System.out.println(table + " media for " + id + ":" + rsMedia);
            while (rsMedia.next()) {
                String filePath = rsMedia.getString("media_file_path");
                String mediaStart = rsMedia.getString("media_start");
                String mediaEnd = rsMedia.getString("media_end");
                String description = rsMedia.getString("description");

                // Handle different media types
                if (rsMedia.getString("media_file_path") != null) {
                    mediaHtml.append("<img src='").append(filePath).append("' alt='").append(description)
                            .append("' />");
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
