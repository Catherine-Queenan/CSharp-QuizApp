import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.print.attribute.standard.RequestingUserName;
import org.json.JSONArray;


public class ModerateModeServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //Check if user is logged in, if not send them to login
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        //Check if a quiz has been selected
        String quizName = req.getParameter("quizName");
        if (quizName == null || quizName.trim().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quiz name");
            return;
        }

        //
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
            // Database connection

            con = DatabaseUtil.getConnection();
            
            // // Query to get questions
            stmntQuestion = con.prepareStatement("SELECT questionText, answers,indexOfCorrect FROM QuestionsWithAnswers WHERE quizName = ?");
            stmntQuestion.setString(1, quizName);
            rsQuestion = stmntQuestion.executeQuery();


            // Generate HTML for each question
            int questionNumber = 1;
            while (rsQuestion.next()) {
                String questionText = rsQuestion.getString("questionText");
                String answers = rsQuestion.getString("answers");
                JSONArray answersArray = new JSONArray(answers);
                int indexOfCorrect = rsQuestion.getInt("indexOfCorrect");

                questionsHtml.append("<div class='question'>")
                        .append("<p class='questionTitle'>").append(questionText).append("</p>")
                        .append("<div class='answers'>");

                for (int i = 0; i < answersArray.length(); i++) {
                    String answer = answersArray.getString(i);
                    if (i == indexOfCorrect) {
                        //append correct answer with class name containing 'questionNumber' and 'correct'
                        questionsHtml.append("<p data-question="+questionNumber+" class='answer correct'>").append(answer).append("</p>");
                    } else {
                        questionsHtml.append("<p data-question="+questionNumber+" class='answer'>").append(answer).append("</p>");
                    }
                }
                questionNumber++;
            }

            req.setAttribute("questionsHtml", questionsHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching questions");
            return;
        } finally {
            try { if (stmntQuestion != null) stmntQuestion.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsQuestion != null) rsQuestion.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to questions.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/moderateMode.jsp");
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
                                .append("<div class=\"videoWrap\"><div id=\"player\"></div></div>");
                    }
                    
                    break;
                case "IMG":
                    while(rsMedia.next()){
                        String filePath = rsMedia.getString("media_file_path");
                        String alt = rsMedia.getString("description");

                        mediaHtml.append("<div class=\"imgWrap\"><img alt=\"").append(alt).append("\"width=\"300\" height=\"200\" src=\"").append(filePath).append("\"></div>\n");
                    }
                    break;
                case "AUD":
                    while(rsMedia.next()){
                        String filePath = rsMedia.getString("media_file_path");
                        String mediaStart = rsMedia.getString("media_start");
                        String mediaEnd = rsMedia.getString("media_end");

                        mediaHtml.append("<div class=\"audioWrap\"><audio preload controls ontimeupdate=\"audio()\">\n")
                                .append("<source src=\"").append(filePath).append("#t=").append(mediaStart).append("\" type=\"audio/mp3\">")
                                .append("</audio></div>")
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
