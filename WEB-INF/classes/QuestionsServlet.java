import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.*;
import javax.print.attribute.standard.RequestingUserName;

import org.json.JSONObject;

public class QuestionsServlet extends HttpServlet {

    private final IRepository repository = new Repository();
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //Check if user is logged in, if not send them to login
        HttpSession session = req.getSession(false);
        
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        //Check if a quiz has been selected
        String quizName = session.getAttribute("quiz").toString();
        if (quizName == null || quizName.trim().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quiz name");
            return;
        }

        StringBuilder questionsHtml = new StringBuilder();
        StringBuilder mediaHtml = new StringBuilder();  // For holding the media HTML
        Integer currQuestion = (Integer) session.getAttribute("currQuestion");
        ArrayList<AClass> questions = (ArrayList<AClass>) session.getAttribute("questions");
        String role = (String) session.getAttribute("USER_ROLE");
        req.setAttribute("role", role);
        //Autoplay quiz
        Boolean autoplayEnabled = (Boolean) session.getAttribute("autoplay");
        if (autoplayEnabled == null) {
            autoplayEnabled = false;
        }

        req.setAttribute("autoplay", autoplayEnabled);
        //End of Autoplay functionality

        if(questions.isEmpty()){
            questionsHtml.append("<p class=\"errorMsg\">The quiz \"").append(quizName).append("\" is empty!</p>")
                        .append("<form class=\"errorBtnWrap\" action=\"home\"><button class=\"homeBtn errorHome\" type=\"Submit\">Return Home</button></form>");
            req.setAttribute("questionsHtml", questionsHtml);
            req.setAttribute("qNumber", currQuestion);
            req.setAttribute("quizSize", questions.size());

            RequestDispatcher view = req.getRequestDispatcher("/views/questions.jsp");
            view.forward(req, res);
            return;
        }

        JSONObject questionJSON = questions.get(currQuestion).serialize();

        // ArrayList<String> colors = new ArrayList<>("#A40E4C", "#D00000", "#FF4B3E", "#FFB20F");

        try {
            repository.init("com.mysql.cj.jdbc.Driver");
                 
                String questionText = questionJSON.getString("question_text");
                String questionType = questionJSON.getString("question_type");
                // // Display question
                questionsHtml.append("<div class=\"question\"").append(">\n")
                             .append("<p class=\"questionText\">").append(questionText).append("</p>\n");
                             
                if(!questionType.equals("TEXT")){
                    System.out.println("INSERT QUESTION MEDIA");
                    // Check if the media type is an image
                    if(questionType.equals("IMG")) {
                        questionsHtml.append("<div id=\"questionMedia\" style=\"display: none;\">"); // Hide if image
                    } else {
                        questionsHtml.append("<div id=\"questionMedia\">"); // Display normally for other types
                    }
                    String media = insertMedia("question", questionJSON, questionType);
                    questionsHtml.append(media != null ? media : "");
                    questionsHtml.append("</div>");
                    System.out.println("INSERT QUESTION MEDIA SUCCEEDED");
                    if((media != null) && (questionType.equalsIgnoreCase("IMG"))){
                        questionsHtml.append(media);
                    }
                }
                
                System.out.println("SELECT ANSWERS");
                String criteria = questionJSON.getString("id") +", ORDER BY rand()";
                ArrayList<AClass> answers = repository.select("answer", criteria);
                System.out.println("SELECT SUCCEEDED");
                
                StringBuilder answerVidAud = new StringBuilder();
                int countAnswer = 1;

                // Loop through answers to determine if any has media and is correct
                for(AClass answer: answers) {
                    JSONObject answerJSON = answer.serialize();
                    String answerText = answerJSON.getString("answer_text");
                    boolean isCorrect = answerJSON.getInt("is_correct") == 1;
                    String answerType = answerJSON.getString("answer_type");

                    String answerDisplay = answerType.equalsIgnoreCase("IMG") ? insertMedia("answer", answerJSON, answerType) : answerText;
                    answerDisplay = answerDisplay != null ? answerDisplay : answerText;

                    // If the answer is correct and has video or audio, add to answerVidAud
                    if(isCorrect && (answerType.equalsIgnoreCase("VID") || answerType.equalsIgnoreCase("AUD"))) {
                        answerVidAud.append("<div id=\"mediaAnswer\" style=\"display:none;\">")
                                    .append(insertMedia("answer", answerJSON, answerType))
                                    .append("</div>\n");
                    }
                }

                // Append answerVidAud to questionsHtml before displaying answers
                questionsHtml.append(answerVidAud).append("\n"); // This will ensure it appears above all answers

                
                questionsHtml.append("<div class=\"answersOption\">");

                for(AClass answer: answers) {
                    JSONObject answerJSON = answer.serialize();
                    String answerText = answerJSON.getString("answer_text");
                    boolean isCorrect = answerJSON.getInt("is_correct") == 1;
                    String answerType = answerJSON.getString("answer_type");

                    String answerDisplay = answerType.equalsIgnoreCase("IMG") ? insertMedia("answer", answerJSON, answerType) : answerText;
                    answerDisplay = answerDisplay != null ? answerDisplay : answerText;

                    // Append each answer button based on whether it's correct or incorrect
                    if(isCorrect) {
                        questionsHtml.append("<button class=\"answer").append(countAnswer).append("\" id=\"rightPlayAnswer\" onclick=\"nextQuestion()\">")
                                    .append(answerDisplay)
                                    .append("</button>\n");
                    } else {
                        questionsHtml.append("<button class=\"wrongPlayAnswer answer").append(countAnswer).append("\">")
                                    .append(answerDisplay)
                                    .append("</button>\n");
                    }
                    countAnswer++;
                }
                questionsHtml.append("</div>\n");
                questionsHtml.append("</div>\n");

            // Set question, total number of questions, and current question as request attribute           
            req.setAttribute("questionsHtml", questionsHtml);
            req.setAttribute("qNumber", currQuestion + 1);
            req.setAttribute("quizSize", questions.size());

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching questions");
            return;
        }
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

        if(req.getParameter("restart") != null){
            Collections.shuffle(questions);
            session.setAttribute("currQuestion", 0);
            session.setAttribute("questions", questions);
            res.setStatus(302);
            res.sendRedirect("questions");
            return;
        }
        


        if(++currQuestion >= questions.size()){
            res.setStatus(302);
            res.sendRedirect("end");
            return;
        }
        session.setAttribute("currQuestion", currQuestion);
        res.setStatus(302);
        res.sendRedirect("questions");
    }

    private String renderAudio(JSONObject mediaJSON, String table) {
        StringBuilder mediaHtml = new StringBuilder();
        String filePath = mediaJSON.getString("media_file_path");
        int mediaStart = mediaJSON.getInt("media_start");
        int mediaEnd = mediaJSON.getInt("media_end");
    
        mediaHtml.append("<audio id=\"audio-").append(table).append("\" preload controls ontimeupdate=\"").append(table).append("Audio()\">\n")
                .append("<source src=\"").append(filePath).append("#t=").append(mediaStart).append("\" type=\"audio/mp3\">")
                .append("</audio>")
                .append("<input type=\"hidden\" id=\"videoStart-").append(table).append("\" value=\"").append(mediaStart).append("\">\n")
                .append("<input type=\"hidden\" id=\"videoEnd-").append(table).append("\" value=\"").append(mediaEnd).append("\">\n");
        return mediaHtml.toString();
    }
    
    
    public String insertMedia(String table, JSONObject question, String type){
        StringBuilder mediaHtml = new StringBuilder();

        try {

            String media_id = question.getString("media_id");
            System.out.println(question.getString("media_id"));
            System.out.println(media_id);
            ArrayList<AClass> media = repository.select("media", media_id);
            JSONObject mediaJSON = media.get(0).serialize();

            String filePath;
            int mediaStart;
            int mediaEnd;
            switch(type){
                case "VID":
                        filePath = mediaJSON.getString("media_file_path").split("=")[1];
                        mediaStart = mediaJSON.getInt("media_start");
                        mediaEnd = mediaJSON.getInt("media_end");

                        mediaHtml.append("<input type=\"hidden\" id=\"videoId-").append(table).append("\" value=\"").append(filePath).append("\">\n")
                                .append("<input type=\"hidden\" id=\"videoStart-").append(table).append("\" value=\"").append(mediaStart).append("\">\n")
                                .append("<input type=\"hidden\" id=\"videoEnd-").append(table).append("\" value=\"").append(mediaEnd).append("\">\n")
                                .append("<div id=\"player-") .append(table).append("\"></div>");
                    
                    break;
                case "IMG":
                        filePath = mediaJSON.getString("media_file_path");


                        String alt = mediaJSON.getString("description");

                        mediaHtml.append("<div class=\"imgWrap\"><img alt=\"").append(alt).append("\"width=\"300\" height=\"200\" src=\"").append(filePath).append("\"></div>\n");
                    break;
                case "AUD":
                    mediaHtml.append(renderAudio(mediaJSON, table));
                    break;                
                default :
                    return null;
            }  
            return mediaHtml.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // private String getUserRoleFromDatabase(String username) {
    //     Connection con = null;
    //     PreparedStatement ps = null;
    //     ResultSet rs = null;
    //     String role = null;

    //     try {
    //         // Load MySQL driver
    //         Class.forName("com.mysql.cj.jdbc.Driver");

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
