import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class QuestionsServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        String quizName = session.getAttribute("quiz").toString();

        if (quizName == null || quizName.trim().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quiz name");
            return;
        }

        Connection con = null;
        PreparedStatement stmntQuestion = null;
        PreparedStatement stmntAnswer = null;
        ResultSet rsQuestion = null;
        ResultSet rsAnswer = null;
        StringBuilder questionsHtml = new StringBuilder();
        ArrayList<InputStream> questions = (ArrayList<InputStream>) session.getAttribute("questions");
        Integer currQuestion = (Integer) session.getAttribute("currQuestion");
        InputStream qID = questions.get(currQuestion);

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");

            // // Query to get questions
            // String sqlQuestions = "SELECT id, question_text, question_type FROM questions WHERE quiz_name = ?";
            stmntQuestion = con.prepareStatement("SELECT question_text, question_type FROM questions WHERE quiz_name = ? AND id = ?");
            stmntQuestion.setString(1, quizName);
            stmntQuestion.setBinaryStream(2, qID);
            rsQuestion = stmntQuestion.executeQuery();

            // Generate HTML for questions
            while (rsQuestion.next()) {
                // InputStream questionId = rsQuestions.getBinaryStream("id");
                // questions.add(questionId);

                String questionText = rsQuestion.getString("question_text");
                String questionType = rsQuestion.getString("question_type");

                // // Display question
                questionsHtml.append("<div class=\"question\"").append(">\n")
                             .append("<p><strong>Question:</strong> ").append(questionText).append("</p>\n");
                             

                // Query to get answers for this question
                // String sqlAnswers = "SELECT answer_text, is_correct, answer_type FROM answers WHERE question_id = ?";
                // psAnswers = con.prepareStatement(sqlAnswers);
                // psAnswers.setBinaryStream(1, questionId);
                // rsAnswers = psAnswers.executeQuery();
                stmntAnswer = con.prepareStatement("SELECT answer_text, is_correct, answer_type FROM answers WHERE question_id = ? ORDER BY rand()");
                stmntAnswer.setBinaryStream(1, qID);
                rsAnswer = stmntAnswer.executeQuery();

                // Display answers
                while (rsAnswer.next()) {
                    String answerText = rsAnswer.getString("answer_text");
                    boolean isCorrect = rsAnswer.getBoolean("is_correct");
                    String answerType = rsAnswer.getString("answer_type");
                    if(isCorrect){
                        questionsHtml.append("<form id=\"questionForm\" method=\"post\">").append("<button id=\"rightPlayAnswer\">").append(answerText).append("</button></form>\n");
                    } else {
                        questionsHtml.append("<button class=\"wrongPlayAnswer\">").append(answerText).append("</button>\n");
                    }
                    
                }
                
                questionsHtml.append("</div>\n");
                
                // Reset ResultSet and PreparedStatement for the next question
                // rsAnswer.close();
                // stmntAnswer.close();
                // psAnswers.close();
            }

            // Set questions as request attribute           
            req.setAttribute("questionsHtml", questionsHtml);
            // }

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching questions");
            return;
        } finally {
            try { if (stmntAnswer != null) stmntAnswer.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmntQuestion != null) stmntQuestion.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsQuestion != null) rsQuestion.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsAnswer != null) rsAnswer.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to questions.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/questions.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session == null){
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }
        Integer currQuestion = (Integer) session.getAttribute("currQuestion");
        ArrayList<InputStream> questions = (ArrayList<InputStream>) session.getAttribute("questions");

        if(++currQuestion >= questions.size()){
            res.setStatus(302);
            res.sendRedirect("end");
            return;
        }
        session.setAttribute("currQuestion", currQuestion);
        res.setStatus(302);
        res.sendRedirect("questions");
    }
}
