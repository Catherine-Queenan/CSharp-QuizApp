import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.util.UUID;
import java.nio.ByteBuffer;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class AddQuestionServlet extends HttpServlet {

    // Convert UUID to binary (byte array)
    public byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    // Convert binary (byte array) to UUID
    public UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

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

        String quizName = req.getParameter("quizName");
        req.setAttribute("quizName", quizName);
        RequestDispatcher view = req.getRequestDispatcher("/views/addQuestion.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Connection con = null;
        PreparedStatement psQuestion = null;
        PreparedStatement psAnswer = null;
        PreparedStatement psMedia = null;
        PreparedStatement psQuestionMedia = null;

        try {
            // Load MySQL driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
             con = DatabaseUtil.getConnection();
            // Get form data from the request
            // String quizName = req.getParameter("quizName");
            String quizName = req.getParameter("quizName");
            String questionText = req.getParameter("questionText");
            String questionType = req.getParameter("questionType");
            String videoUrl = req.getParameter("videoUrl");
            int videoStart = Integer.parseInt(req.getParameter("videoStart"));
            int videoEnd = Integer.parseInt(req.getParameter("videoEnd"));
            Part filePart = req.getPart("mediaFile");
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String[] answerTexts = req.getParameterValues("answerText");
            String correctAnswer = req.getParameter("correctAnswer");

            // Generate a UUID for the question
            UUID questionUUID = UUID.randomUUID();
            byte[] questionIdBinary = uuidToBytes(questionUUID);

            // Insert the new question into the `questions` table
            String insertQuestionSql = "INSERT INTO questions (id, quiz_name, question_text, question_type) VALUES (?, ?, ?, ?)";
            psQuestion = con.prepareStatement(insertQuestionSql);
            psQuestion.setBytes(1, questionIdBinary);
            psQuestion.setString(2, quizName);
            psQuestion.setString(3, questionText);
            psQuestion.setString(4, questionType);
            psQuestion.executeUpdate();

            
             // Generate a UUID for the media
             UUID mediaUUID = UUID.randomUUID();
             byte[] mediaIdBinary = uuidToBytes(mediaUUID);
            
            // Insert media information into the `media` table
            String mediaUrl = null;
            if( questionType.equals("VID")){
                mediaUrl = videoUrl;
            } else if(!questionType.equals("TEXT")){
                System.out.println("Current folder: " + (new File(".")).getCanonicalPath());
                File saveFile = new File(getServletContext().getRealPath("/public/media"));
                File file = new File(saveFile, fileName);
                filePart.write(file.getAbsolutePath());
                mediaUrl = "public/media/" + fileName;
                videoStart = Integer.parseInt(req.getParameter("audioStart"));
                videoEnd = Integer.parseInt(req.getParameter("audioEnd"));
            }
            
            String insertMediaSql = "INSERT INTO media (id, media_type, media_file_path, media_filename, media_start, media_end) VALUES (?, ?, ?, ?, ?, ?)";
            psMedia = con.prepareStatement(insertMediaSql);
            psMedia.setBytes(1, mediaIdBinary);
            psMedia.setString(2, questionType);
            psMedia.setString(3, mediaUrl);
            psMedia.setString(4, "fileName");
            psMedia.setInt(5, videoStart);
            psMedia.setInt(6, videoEnd);
            psMedia.executeUpdate();

            // Insert into `question_media` table to link the question and the media
            String insertQuestionMediaSql = "INSERT INTO question_media (question_id, media_id) VALUES (?, ?)";
            psQuestionMedia = con.prepareStatement(insertQuestionMediaSql);
            psQuestionMedia.setBytes(1, questionIdBinary);
            psQuestionMedia.setBytes(2, mediaIdBinary);
            psQuestionMedia.executeUpdate();

            // Insert answers into the `answers` table
            for (int i = 0; i < answerTexts.length; i++) {
                UUID answerUUID = UUID.randomUUID();
                byte[] answerIdBinary = uuidToBytes(answerUUID);
                boolean isCorrect = (correctAnswer != null && Integer.parseInt(correctAnswer) == i + 1);

                String insertAnswerSql = "INSERT INTO answers (id, question_id, answer_text, is_correct, answer_type) VALUES (?, ?, ?, ?, ?)";
                psAnswer = con.prepareStatement(insertAnswerSql);
                psAnswer.setBytes(1, answerIdBinary);
                psAnswer.setBytes(2, questionIdBinary);
                psAnswer.setString(3, answerTexts[i]);
                psAnswer.setBoolean(4, isCorrect);
                psAnswer.setString(5, "text");  // Assuming answer_type is 'text' for now
                psAnswer.executeUpdate();
            }

            // Redirect back to the quiz creation page or show success message
            res.sendRedirect("editQuestions?quizName=" + quizName);

        } catch (Exception e) {
            throw new ServletException("Error processing question addition", e);
        } finally {
            try {
                if (psQuestion != null) psQuestion.close();
                if (psAnswer != null) psAnswer.close();
                if (psMedia != null) psMedia.close();
                if (psQuestionMedia != null) psQuestionMedia.close();
                if (con != null) con.close();
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
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DatabaseUtil.getConnection();
            
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
