import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

public class EditQuestionsServlet extends HttpServlet {

    private final IRepository repository = new Repository();


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");

         // Initialize JSON object to store the response
         JSONObject jsonResponse = new JSONObject();
        
        
        // getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        PrintWriter out = res.getWriter();

        jsonResponse.put("role", "admin");
        JSONArray questionsArray = new JSONArray();

        // Connection con = null;
        // PreparedStatement ps = null;
        // ResultSet rs = null;
        // StringBuilder questionsHtml = new StringBuilder();
        ArrayList<AClass> questions;

        try {
            // Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            repository.init("com.mysql.cj.jdbc.Driver");
            // DATABASE CONNECTION LINE
            // con = DatabaseUtil.getConnection();
            // Get quiz name from request parameters
            String quizName = req.getParameter("quizName");
            if (quizName == null || quizName.isEmpty()) {
                res.sendRedirect("home"); // If no quiz name is provided, redirect to home
                return;
            }

            // Query the database for questions related to the quiz
            // String questionsSql = "SELECT id, question_text FROM questions WHERE quiz_name = ?";
            questions = repository.select("question", "quiz_name=\"" + quizName + "\"");
            // ps = con.prepareStatement(questionsSql);
            // ps.setString(1, quizName);
            // rs = ps.executeQuery();

            // Generate HTML for questions
            // boolean hasQuestions = false;
            int numOfQs = 0;
            for(AClass question: questions) {
                JSONObject questionJSON = question.serialize();
                // hasQuestions = true;
                // InputStream questionId = rs.getBinaryStream("id");
                // String questionText = questionJSON.getString("question_text");
                
                questionJSON.put("questionNum", numOfQs++);
                // questionsHtml.append("<div class='question'>")
                //         .append("<p class='questionTitle'>").append(questionText).append("</p>")
                //         .append("<a class='deleteBtn' href='deleteQuestion?id=").append(numOfQs++).append("&quizName=").append(quizName).append("'>Delete Question</a>")
                //         .append("</div>");

                questionsArray.put(questionJSON);
           
            }

            // if (!hasQuestions) {
            //     questionsHtml.append("<p class='errorMsg'>There are currently no questions for this quiz.</p>");
            // }
            
            // // Set quiz name and questions HTML as request attributes
            // req.setAttribute("quizName", quizName);

            session.setAttribute("questions", questions);
            // req.setAttribute("questionsHtml", questionsHtml.toString());

            jsonResponse.put("questions", questionsArray);

        } catch (Exception e) {
            e.printStackTrace();
        } 
        // finally {
        //     try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        //     try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        //     try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        // }

        // Forward to JSP
        // RequestDispatcher view = req.getRequestDispatcher("/views/editQuestions.jsp");
        // view.forward(req, res);

        out.write(jsonResponse.toString());
        out.flush();
        out.close();
    }
    // private String getUserRoleFromDatabase(String username) {
    //     Connection con = null;
    //     PreparedStatement ps = null;
    //     ResultSet rs = null;
    //     String role = null;

    //     try {
    //         Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
    //         // Database connection
    //         con = DatabaseUtil.getConnection();

    //         // Query to get the user's role
    //         String sql = "SELECT role FROM users WHERE username = ?";
    //         ps = con.prepareStatement(sql);
    //         ps.setString(1, username);
    //         rs = ps.executeQuery();

    //         if (rs.next()) {
    //             role = rs.getString("role");
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     } finally {
    //         try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
    //         try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
    //         try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
    //     }

    //     return role;
    // }
}
