import java.io.IOException;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.annotation.MultipartConfig;
import java.util.UUID;

import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

@MultipartConfig
public class EditQuestionServlet extends HttpServlet {
    private final IRepository repository = new Repository();
    private final AClassFactory factory = new AClassFactory();

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
    
    @Override
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
        JSONArray answersArray = new JSONArray();
        ArrayList<AClass> answers;

        int idIndex = Integer.parseInt(req.getParameter("id"));
        ArrayList<AClass> questions = (ArrayList<AClass>) session.getAttribute("questions");

        try {
            repository.init("com.mysql.cj.jdbc.Driver");
            JSONObject editQuestion = questions.get(idIndex).serialize();

            if (!editQuestion.isNull("media_id")) {
                AClass questionMedia = repository.select("media", editQuestion.getString("media_id")).get(0);
                // String mediaFilePath =
                // categoryMedia.serialize().getString("media_file_path");
                // mediaHtml.append("<img src=\"").append(mediaFilePath).append("\"
                // alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                editQuestion.put("media", questionMedia.serialize());
            }

            jsonResponse.put("question", editQuestion);
            answers = repository.select("answer", editQuestion.getString("id"));
            for (AClass answer : answers) {
                JSONObject answerJSON = answer.serialize();
                System.out.println(answer.serialize().toString());

                if (!answerJSON.isNull("media_id")) {
                    AClass answerMedia = repository.select("media", answerJSON.getString("media_id")).get(0);
                    // String mediaFilePath =
                    // categoryMedia.serialize().getString("media_file_path");
                    // mediaHtml.append("<img src=\"").append(mediaFilePath).append("\"
                    // alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                    answerJSON.put("media", answerMedia.serialize());
                }

                answersArray.put(answerJSON);
            }

            jsonResponse.put("answers", answersArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.write(jsonResponse.toString());
        out.flush();
        out.close();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            repository.init("com.mysql.cj.jdbc.Driver");

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("USER_ID") == null) {
                res.sendRedirect("login");
                return;
            }

            String username = (String) session.getAttribute("USER_ID");
            String role = (String) session.getAttribute("USER_ROLE");

            if (!"a".equals(role)) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
                req.setAttribute("errorMessage", "You are not authorized to access this page.");
                RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
                view.forward(req, res);
                return;
            }

            String editType = req.getParameter("edit");
            String quizName = req.getParameter("quizName");

            
            ArrayList<AClass> questions = (ArrayList<AClass>) session.getAttribute("questions");
            if (editType.equalsIgnoreCase("question")) {
                editQuestion(req, questions);
            }

            questions = repository.select("question", "quiz_name=\"" + quizName + "\"");
            session.setAttribute("questions", questions);

            res.setStatus(200);
            res.getWriter().write("{\"message\": \"Question edited successfully!\"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editQuestion(HttpServletRequest req, ArrayList<AClass> questions) throws ServletException, IOException {
        String questionText = req.getParameter("questionText");
        int questionNum = Integer.parseInt(req.getParameter("id"));
        String questionType = req.getParameter("questionType");
        String parameters = "question_text:==" + questionText + ",,,question_type:==" + questionType;

        AClass question = questions.get(questionNum);
        JSONObject questionJSON = question.serialize();

        //often empty
        String newMediaId = "";
        if(questionJSON.isNull("media_id")){
            UUID mediaUUID = UUID.randomUUID();
            byte[] mediaIdBinary = uuidToBytes(mediaUUID);
            String id = new String(mediaIdBinary, StandardCharsets.UTF_8) ;

            newMediaId = "id:==" + id + ",,,";
            parameters = parameters + ",,,media_id:==" + id;
        }
        System.out.println("QUESTION JSON:" + questionJSON.toString());
        
        AClass updateMedia = null;
        if (questionType.equalsIgnoreCase("AUD") || questionType.equalsIgnoreCase("IMG")) {
            Part questionMedia = req.getPart("mediaFile");
            String fileName = questionMedia.getSubmittedFileName();
            if (fileName != null && !fileName.isEmpty()) {
                String mediaStart = req.getParameter("audioStart");
                String mediaEnd = req.getParameter("audioEnd");

                File saveFile = new File(getServletContext().getRealPath("/public/media"));
                File file = new File(saveFile, fileName);
                System.out.println("WRITE PATH:" + file.getAbsolutePath());
                questionMedia.write(file.getAbsolutePath());
                String mediaUrl = "public/media/" + fileName;

                String mediaParams = newMediaId + "media_file_path:==" + mediaUrl + ",,,media_filename:==" + fileName
                        + ",,,media_start:==" + mediaStart + ",,,media_end:==" + mediaEnd + ",,,media_type:=="
                        + questionType;
                updateMedia = factory.createAClass("media", mediaParams);

                System.out.println(mediaParams);
                
            }
        } else if (questionType.equalsIgnoreCase("VID")) {
            String videoUrl = req.getParameter("videoUrl");
            String videoStart = req.getParameter("videoStart");
            String videoEnd = req.getParameter("videoEnd");

            String mediaParams = newMediaId + "media_file_path:==" + videoUrl + ",,,media_filename:==N/A" + ",,,media_start:=="
                    + videoStart + ",,,media_end:==" + videoEnd + ",,,media_type:==" + questionType;
            
                    System.out.println(mediaParams);
                    updateMedia = factory.createAClass("media", mediaParams);
            
        }

        System.out.println(parameters);
        AClass updateQuestion = factory.createAClass("question", parameters);
        if(updateMedia != null && !questionType.equalsIgnoreCase("TEXT")){
            if(questionJSON.isNull("media_id")){
                repository.insert(updateMedia);
                repository.update(updateQuestion, questionJSON.getString("id"), "question_text,question_type,media_id");
            } else {
            repository.update(updateMedia, questionJSON.getString("media_id"),
        "media_file_path,media_filename,media_start,media_end,media_type");
        repository.update(updateQuestion, questionJSON.getString("id"), "question_text,question_type");
            }
            

        } else {
            repository.update(updateQuestion, questionJSON.getString("id"), "question_text,question_type");
        }

        
        
    }
}
