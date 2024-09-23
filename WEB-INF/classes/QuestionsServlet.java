import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.print.attribute.standard.RequestingUserName;

public class QuestionsServlet extends HttpServlet {
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

        //
        Connection con = null;
        PreparedStatement stmntQuestion = null;
        PreparedStatement stmnmedia = null;
        PreparedStatement stmnQuestionmedia = null;
        PreparedStatement stmntAnswer = null;
        ResultSet rsQuestion = null;
        ResultSet rsAnswer = null;
        StringBuilder questionsHtml = new StringBuilder();
        StringBuilder mediaHtml = new StringBuilder();  // For holding the media HTML
        Integer currQuestion = (Integer) session.getAttribute("currQuestion");
        ArrayList<InputStream> questions = (ArrayList<InputStream>) session.getAttribute("questions");
        
        //Autoplay quiz
        Boolean autoplayEnabled = (Boolean) session.getAttribute("autoplay");
        if (autoplayEnabled == null) {
            autoplayEnabled = false;
        }

        req.setAttribute("autoplay", autoplayEnabled);
        //End of Autoplay functionality

        if(questions.isEmpty()){
            questionsHtml.append("<p class=\"errorMsg\">The quiz \"").append(quizName).append("\" is empty!</p>")
                        .append("<form action=\"home\"><button class=\"homeBtn\" type=\"Submit\">Return Home</button></form>");
            req.setAttribute("questionsHtml", questionsHtml);
            req.setAttribute("qNumber", currQuestion);
            req.setAttribute("quizSize", questions.size());

            RequestDispatcher view = req.getRequestDispatcher("/views/questions.jsp");
            view.forward(req, res);
            return;
        }

        InputStream qID = questions.get(currQuestion);

        // ArrayList<String> colors = new ArrayList<>("#A40E4C", "#D00000", "#FF4B3E", "#FFB20F");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // Database connection
           con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizapp", "root", "");

            con = DatabaseUtil.getConnection();
            
            // // Query to get questions
            // String sqlQuestions = "SELECT id, question_text, question_type FROM questions WHERE quiz_name = ?";
            stmntQuestion = con.prepareStatement("SELECT question_text, question_type FROM questions WHERE quiz_name = ? AND id = ?");
            stmntQuestion.setString(1, quizName);
            stmntQuestion.setBinaryStream(2, qID);
            rsQuestion = stmntQuestion.executeQuery();

            // Generate HTML for questions
            while (rsQuestion.next()) {
                        
            // stmnmedia = con.prepareStatement("SELECT  media_id  FROM question_media WHERE  question_id = ?");
            // stmnmedia.setBinaryStream(1, qID);
            // ResultSet rsmedia = stmnmedia.executeQuery();

            // while(rsmedia.next()){
            //     InputStream media_id = rsmedia.getBinaryStream("media_id");
            //     req.setAttribute("media_id", media_id);
            // }

            // stmnQuestionmedia = con.prepareStatement("SELECT media_file_path, media_type FROM media WHERE id = ?");
            // stmnQuestionmedia.setBinaryStream(1, (InputStream)req.getAttribute("media_id"));
            // ResultSet rsQuestionmedia = stmnQuestionmedia.executeQuery();

            // while(rsQuestionmedia.next()){
            //     String media_file_path = rsQuestionmedia.getString("media_file_path");
            //     String media_type = rsQuestionmedia.getString("media_type");
            //     req.setAttribute("media_file_path", media_file_path);
            //     req.setAttribute("media_type", media_type);
            //     // Check if it's a YouTube link
            //     if (media_file_path.contains("youtube.com/watch")) {
            //         // Convert YouTube URL to embed format
            //         String youtubeEmbedUrl = media_file_path.replace("watch?v=", "embed/");
            //         mediaHtml.append("<div class=\"media-item\">\n")
            //                  .append("<iframe width=\"560\" height=\"315\" src=\"" + youtubeEmbedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>\n")
            //                  .append("</div>\n");
            //     } else if ("image".equalsIgnoreCase(media_type)) {
            //         // For image media types
            //         mediaHtml.append("<div class=\"media-item\">\n")
            //                  .append("<img src=\"" + media_file_path + "\" alt=\"Image\" width=\"300\" height=\"200\" />\n")
            //                  .append("</div>\n");
            //     } else if ("video".equalsIgnoreCase(media_type)) {
            //         // For local video media types
            //         mediaHtml.append("<div class=\"media-item\">\n")
            //                  .append("<video width=\"300\" height=\"200\" controls>\n")
            //                  .append("  <source src=\"" + media_file_path + "\" type=\"video/mp4\">\n")
            //                  .append("  Your browser does not support the video tag.\n")
            //                  .append("</video>\n")
            //                  .append("</div>\n");
            //     }
            // }


            // Set the media HTML as request attribute (after the loop has finished)
            // req.setAttribute("mediaHtml", mediaHtml.toString());

                String questionText = rsQuestion.getString("question_text");
                String questionType = rsQuestion.getString("question_type");
   
                if(!questionType.equals("TEXT")){
                    System.out.println("AAAAAAAAAAAAa");
                    String media = insertMedia(con, "question", qID, questionType);
                    if(media != null){
                        System.out.println("Fuck");
                        questionsHtml.append(media);
                    }

                }
                // // Display question
                questionsHtml.append("<div class=\"question\"").append(">\n")
                             .append("<p>").append(questionText).append("</p>\n");
                             

                // Query to get answers for this question
                // String sqlAnswers = "SELECT answer_text, is_correct, answer_type FROM answers WHERE question_id = ?";
                // psAnswers = con.prepareStatement(sqlAnswers);
                // psAnswers.setBinaryStream(1, questionId);
                // rsAnswers = psAnswers.executeQuery();
                stmntAnswer = con.prepareStatement("SELECT id, answer_text, is_correct, answer_type FROM answers WHERE question_id = ? ORDER BY rand()");
                stmntAnswer.setBinaryStream(1, qID);
                rsAnswer = stmntAnswer.executeQuery();

                questionsHtml.append("<div class=\"answersOption\">");
                
                int countAnswer = 1;

                // Display answers
                while (rsAnswer.next()) {
                    InputStream answerId = rsAnswer.getBinaryStream("id");
                    String answerText = rsAnswer.getString("answer_text");
                    boolean isCorrect = rsAnswer.getBoolean("is_correct");
                    String answerType = rsAnswer.getString("answer_type");
                    String answerDisplay = answerType.equalsIgnoreCase("TEXT") ? answerText : insertMedia(con, "answer", answerId, answerType);

                    answerDisplay = answerDisplay != null ? answerDisplay : answerText;
                    if(isCorrect){
                        questionsHtml.append("<form id=\"questionForm\" method=\"post\">").append("<button class=\"answer").append(countAnswer).append("\"id=\"rightPlayAnswer\">").append(answerDisplay).append("</button></form>\n");
                    } else {
                        questionsHtml.append("<button class=\"wrongPlayAnswer answer").append(countAnswer).append("\">").append(answerDisplay).append("</button>\n");
                    }

                    countAnswer++;
                }
                questionsHtml.append("</div>");
                
                questionsHtml.append("</div>\n");
            }

            // Set question, total number of questions, and current question as request attribute           
            req.setAttribute("questionsHtml", questionsHtml);
            req.setAttribute("qNumber", currQuestion + 1);
            req.setAttribute("quizSize", questions.size());

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
    
    public String insertMedia(Connection con, String table, InputStream id, String type){
        PreparedStatement pstmntMedia = null;
        PreparedStatement pstmntMediaId = null;
        ResultSet rsMedia = null;
        ResultSet rsMediaId = null;
        StringBuilder mediaHtml = new StringBuilder();

        try {
            if(table.equalsIgnoreCase("answer")){
                pstmntMediaId = con.prepareStatement("SELECT media_id FROM answer_media WHERE  answer_id = ?");
            } else if (table.equalsIgnoreCase("question")) {
                pstmntMediaId = con.prepareStatement("SELECT media_id FROM question_media WHERE  question_id = ?");
            } else {
                return null;
            }
            pstmntMediaId.setBinaryStream(1, id);
            rsMediaId = pstmntMediaId.executeQuery();
            InputStream mediaId = null;
            while(rsMediaId.next()){
                mediaId = rsMediaId.getBinaryStream("media_id");
            }

            pstmntMedia = con.prepareStatement("SELECT media_file_path, media_start, media_end, description FROM media WHERE id = ?");
            pstmntMedia.setBinaryStream(1, mediaId);
            rsMedia = pstmntMedia.executeQuery();
            switch(type){
                case "VID":
                    while(rsMedia.next()){
                        String filePath = rsMedia.getString("media_file_path").split("=")[1];
                        String mediaStart = rsMedia.getString("media_start");
                        String mediaEnd = rsMedia.getString("media_end");

                        mediaHtml.append("<input type=\"hidden\" id=\"videoId\" value=\"").append(filePath).append("\">\n")
                                .append("<input type=\"hidden\" id=\"videoStart\" value=\"").append(mediaStart).append("\">\n")
                                .append("<input type=\"hidden\" id=\"videoEnd\" value=\"").append(mediaEnd).append("\">\n")
                                .append("<div id=\"player\"></div>");
                    }
                    
                    break;
                case "IMG":
                    while(rsMedia.next()){
                        String filePath = rsMedia.getString("media_file_path");
                        String alt = rsMedia.getString("description");

                        mediaHtml.append("<img alt=\"").append(alt).append("\"width=\"300\" height=\"200\" src=\"").append(filePath).append("\">\n");
                    }
                    System.out.println("aaaaa");
                    break;
                case "AUD":
                    while(rsMedia.next()){
                        String filePath = rsMedia.getString("media_file_path");
                        String mediaStart = rsMedia.getString("media_start");
                        String mediaEnd = rsMedia.getString("media_end");

                        mediaHtml.append("<audio preload controls ontimeupdate=\"audio()\">\n")
                                .append("<source src=\"").append(filePath).append("#t=").append(mediaStart).append("\" type=\"audio/mp3\">")
                                .append("</audio>")
                                .append("<input type=\"hidden\" id=\"videoStart\" value=\"").append(mediaStart).append("\">\n")
                                .append("<input type=\"hidden\" id=\"videoEnd\" value=\"").append(mediaEnd).append("\">\n");
                    }
                    
                    break;                
                default :
                    return null;
            }

            pstmntMedia.close();
            rsMedia.close(); 
            pstmntMediaId.close();       
            rsMediaId.close();       
            return mediaHtml.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            try { if (pstmntMedia != null) pstmntMedia.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
            try { if (rsMedia != null) rsMedia.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
            try { if (pstmntMediaId != null) pstmntMediaId.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
            try { if (rsMediaId != null) rsMediaId.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
            return null;
        }
    }
}
