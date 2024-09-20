import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.util.UUID;
import jakarta.servlet.http.Part;

public class AddQuestionServlet extends HttpServlet {
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
        System.out.println("Quiz Name: " + quizName);

        req.setAttribute("quizName", quizName);
        RequestDispatcher view = req.getRequestDispatcher("/views/addQuestion.jsp");
        view.forward(req, res);
    }

    

        private static final String UPLOAD_DIR = "uploads"; // Directory where media files will be stored
    
        public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            Connection con = null;
            PreparedStatement psQuestion = null;
            PreparedStatement psAnswer = null;
    
            try {
                // Load MySQL driver and establish connection
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root", "q12773250P");
    
                // Get form data from the request
                String quizName = req.getParameter("quizName");
                String questionText = req.getParameter("questionText");
                String questionType = req.getParameter("questionType");
                String mediaType = req.getParameter("mediaType");
                Part mediaPart = req.getPart("mediaFile"); // Media file input
                String[] answerTexts = req.getParameterValues("answerText");
                String correctAnswer = req.getParameter("correctAnswer");
    
                // Generate a UUID for the question
                String questionId = UUID.randomUUID().toString();
    
                // Handle media upload (if any)
                String mediaUrl = null;
                if (mediaPart != null && mediaPart.getSize() > 0) {
                    String fileName = Paths.get(mediaPart.getSubmittedFileName()).getFileName().toString();
                    String uploadPath = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
    
                    // Create upload directory if it doesn't exist
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
    
                    // Save the media file
                    String filePath = uploadPath + File.separator + fileName;
                    mediaPart.write(filePath);
                    mediaUrl = UPLOAD_DIR + "/" + fileName; // Store relative path to be used in the app
                }
    
                // Insert new question into the database, including media if applicable
                String insertQuestionSql = "INSERT INTO questions (id, quiz_name, question_text, question_type, media_type, media_url) VALUES (?, ?, ?, ?, ?, ?)";
                psQuestion = con.prepareStatement(insertQuestionSql);
                psQuestion.setString(1, questionId);
                psQuestion.setString(2, quizName);
                psQuestion.setString(3, questionText);
                psQuestion.setString(4, questionType);
                psQuestion.setString(5, mediaType);
                psQuestion.setString(6, mediaUrl);
                psQuestion.executeUpdate();
    
                // Insert answers into the database
                for (int i = 0; i < answerTexts.length; i++) {
                    String answerId = UUID.randomUUID().toString();
                    boolean isCorrect = (correctAnswer != null && Integer.parseInt(correctAnswer) == i);
    
                    String insertAnswerSql = "INSERT INTO answers (id, question_id, answer_text, is_correct, answer_type) VALUES (?, ?, ?, ?, ?)";
                    psAnswer = con.prepareStatement(insertAnswerSql);
                    psAnswer.setString(1, answerId);
                    psAnswer.setString(2, questionId);
                    psAnswer.setString(3, answerTexts[i]);
                    psAnswer.setBoolean(4, isCorrect);
                    psAnswer.setString(5, "text"); // Default value for answer_type
                    psAnswer.executeUpdate();
                }
    
                // Redirect back to the edit questions page after successful addition
                res.sendRedirect("editQuestions?quizName=" + quizName);
    
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (psQuestion != null) psQuestion.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (psAnswer != null) psAnswer.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
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
