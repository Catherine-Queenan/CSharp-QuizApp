import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class QuestionsServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");
        if (quizName == null || quizName.trim().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quiz name");
            return;
        }

        Connection con = null;
        PreparedStatement psQuestions = null;
        PreparedStatement psAnswers = null;
        ResultSet rsQuestions = null;
        ResultSet rsAnswers = null;
        StringBuilder questionsHtml = new StringBuilder();

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root", "q12773250P");

            // Query to get questions
            String sqlQuestions = "SELECT id, question_text, question_type FROM questions WHERE quiz_name = ?";
            psQuestions = con.prepareStatement(sqlQuestions);
            psQuestions.setString(1, quizName);
            rsQuestions = psQuestions.executeQuery();

            // Generate HTML for questions
            while (rsQuestions.next()) {
                String questionId = rsQuestions.getString("id");
                String questionText = rsQuestions.getString("question_text");
                String questionType = rsQuestions.getString("question_type");

                // Display question
                questionsHtml.append("<div class=\"question\">\n")
                             .append("<p><strong>Question:</strong> ").append(questionText).append("</p>\n")
                             .append("<p><strong>Type:</strong> ").append(questionType).append("</p>\n");

                // Query to get answers for this question
                String sqlAnswers = "SELECT answer_text, is_correct, answer_type FROM answers WHERE question_id = ?";
                psAnswers = con.prepareStatement(sqlAnswers);
                psAnswers.setString(1, questionId);
                rsAnswers = psAnswers.executeQuery();

                // Display answers
                while (rsAnswers.next()) {
                    String answerText = rsAnswers.getString("answer_text");
                    boolean isCorrect = rsAnswers.getBoolean("is_correct");
                    String answerType = rsAnswers.getString("answer_type");
                    questionsHtml.append("<p>").append(answerText).append(isCorrect ? " (Correct)" : "").append("</p>\n");
                }
                
                questionsHtml.append("</div>\n");
                // Reset ResultSet and PreparedStatement for the next question
                rsAnswers.close();
                psAnswers.close();
            }

            // Set questions as request attribute
            req.setAttribute("questionsHtml", questionsHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching questions");
        } finally {
            try { if (rsQuestions != null) rsQuestions.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsAnswers != null) rsAnswers.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psQuestions != null) psQuestions.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psAnswers != null) psAnswers.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to questions.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/questions.jsp");
        view.forward(req, res);
    }
}
