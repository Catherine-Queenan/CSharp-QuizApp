import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.UUID;

public class AddQuestionServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");
        System.out.println("Quiz Name: " + quizName);

        req.setAttribute("quizName", quizName);
        RequestDispatcher view = req.getRequestDispatcher("/views/addQuestion.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Connection con = null;
        PreparedStatement psQuestion = null;
        PreparedStatement psAnswer = null;

        try {
            // DATABASE CONNECTION LINE
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizapp", "root", ""); // MySQL connection

            // Generate a UUID for the question
            String questionId = UUID.randomUUID().toString();
            
            // Get form data from the request
            String quizName = req.getParameter("quizName");
            String questionText = req.getParameter("questionText");
            String questionType = req.getParameter("questionType");
            String[] answerTexts = req.getParameterValues("answerText");
            String[] correctAnswers = req.getParameterValues("correctAnswer");
            
            // Insert new question into the database
            String insertQuestionSql = "INSERT INTO questions (id, quiz_name, question_text, question_type) VALUES (?, ?, ?, ?)";
            psQuestion = con.prepareStatement(insertQuestionSql);
            psQuestion.setString(1, questionId);
            psQuestion.setString(2, quizName);
            psQuestion.setString(3, questionText);
            psQuestion.setString(4, questionType);
            psQuestion.executeUpdate();

            // Insert answers into the database
            for (int i = 0; i < answerTexts.length; i++) {
                String answerId = UUID.randomUUID().toString(); // Generate a UUID for each answer
                String isCorrect = (i < correctAnswers.length && correctAnswers[i] != null) ? "1" : "0";
                String answerType = "text"; // Default value for answer_type
                String insertAnswerSql = "INSERT INTO answers (id, question_id, answer_text, is_correct, answer_type) VALUES (?, ?, ?, ?, ?)";
                psAnswer = con.prepareStatement(insertAnswerSql);
                psAnswer.setString(1, answerId);
                psAnswer.setString(2, questionId);
                psAnswer.setString(3, answerTexts[i]);
                psAnswer.setString(4, isCorrect);
                psAnswer.setString(5, answerType);
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
}
