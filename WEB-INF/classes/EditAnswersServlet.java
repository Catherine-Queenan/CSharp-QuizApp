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
public class EditAnswersServlet extends HttpServlet {
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
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");
        // getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        JSONObject jsonResponse = new JSONObject();

        int idIndex = Integer.parseInt(req.getParameter("id"));
        ArrayList<AClass> answers = (ArrayList<AClass>) session.getAttribute("answers");

        session.removeAttribute("answers");
        // Connection con = null;
        // PreparedStatement ps = null;

        try {
            // DATABASE CONNECTION LINE
            // Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // Class.forName("oracle.jdbc.OracleDriver"); // Oracle Driver

            repository.init("com.mysql.cj.jdbc.Driver");
            JSONObject deleteAnswer = answers.get(idIndex).serialize();
            System.out.println("HEREEEE  " + deleteAnswer);
            repository.delete("answer", deleteAnswer.getString("id"));
            ArrayList<AClass> updateAnswers = repository.select("answer", answers.get(0).serialize().getString("question_id"));
            session.setAttribute("answers", updateAnswers);


            // DATABASE CONNECTION LINE
            // con = DatabaseUtil.getConnection();
            // Delete the question
            // String deleteQuestionSql = "DELETE FROM questions WHERE id = ?";
            // ps = con.prepareStatement(deleteQuestionSql);
            // ps.setBinaryStream(1, qIDs.get(idIndex));
            // ps.executeUpdate();

            // Redirect back to the edit questions page after successful deletion
            // res.sendRedirect("editQuestions?quizName=" + quizName);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Something went wrong when deleting the question");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Question deleted successfully.");

        // finally {
        // try { if (ps != null) ps.close(); } catch (SQLException e) {
        // e.printStackTrace(); }
        // try { if (con != null) con.close(); } catch (SQLException e) {
        // e.printStackTrace(); }
        // }

        res.setStatus(200);
        res.getWriter().write("{\"message\": \"Answer deleted successfully!\"}");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");
        // getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        try{
repository.init("com.mysql.cj.jdbc.Driver");
        
        System.out.println("MADE IT TO DO POST");
        String answerText = req.getParameter("answerText");
        String correctAnswer = req.getParameter("correctAnswer");
        Part mediaFile = req.getPart("mediaFile");
        String answerType = req.getParameter("answerType");
        System.out.println(answerType);
        System.out.println("MADE IT PAST GATHERING PARAMS");

        UUID answerUUID = UUID.randomUUID();
                byte[] answerIdBinary = uuidToBytes(answerUUID);

        ArrayList<AClass> answers = (ArrayList<AClass>) session.getAttribute("answers");

        String mediaId = null;
        System.out.println(correctAnswer != null);
        
        if (answerType.equalsIgnoreCase("IMG")) {
            String fileName = mediaFile.getSubmittedFileName();
            if (fileName != null && !fileName.isEmpty()) {
                File saveFile = new File(getServletContext().getRealPath("/public/media"));
                File file = new File(saveFile, fileName);
                mediaFile.write(file.getAbsolutePath());
                String mediaUrl = "public/media/" + fileName;

                UUID mediaUUID = UUID.randomUUID();
                byte[] mediaIdBinary = uuidToBytes(mediaUUID);
                mediaId = new String(mediaIdBinary, StandardCharsets.UTF_8);

                String mediaParams = "id:==" + mediaId + ",,,media_file_path:==" + mediaUrl + ",,,media_filename:=="
                        + fileName;
                AClass updateMedia = factory.createAClass("media", mediaParams);
                repository.insert(updateMedia);
            }
        } 
        if(correctAnswer != null){
            AClass currentCorrectAnswer = null;
            for(AClass answer : answers){
                JSONObject answerJSON = answer.serialize();
                if(answerJSON.getInt("is_correct") != 0){
                    currentCorrectAnswer = answer;
                }
            }
            if(currentCorrectAnswer != null){
                System.out.println("IN UPDATE IF");

                ((Answer) currentCorrectAnswer).setCorrect(0);
                JSONObject answerJSON = currentCorrectAnswer.serialize();
                System.out.println("THE ANSWER TO BE UPDATED (CHECK IS_CORRECT)" + answerJSON.toString());
                
                repository.update(currentCorrectAnswer, answerJSON.getString("id"), "is_correct");
                if(answerType.equalsIgnoreCase("AUD") || answerType.equalsIgnoreCase("VID")){
                    System.out.println("AAAAAAAAAAAAAAA");
                    mediaId = answerJSON.getString("media_id");
                }
            }
           
        }

        String questionId = answers.get(0).serialize().getString("question_id");
        String params = "";
        if(mediaId != null){
            params = "id:==" + new String(answerIdBinary, StandardCharsets.UTF_8) +
                    ",,,answer_text:==" + answerText + ",,,answer_type:==" + answerType + 
                    ",,,is_correct:==" + (correctAnswer != null ? 1 : 0) + ",,,question_id:==" + questionId +
                    ",,,media_id:==" + mediaId;
        } else {
            params = "id:==" + new String(answerIdBinary, StandardCharsets.UTF_8) +
                    ",,,answer_text:==" + answerText + ",,,answer_type:==" + answerType + 
                    ",,,is_correct:==" + (correctAnswer != null ? 1 : 0) + ",,,question_id:==" + questionId;
                    
        }
        System.out.println(params);
        AClass newAnswer = factory.createAClass("answer", params);
        repository.insert(newAnswer);

    }catch(Exception ex){
        ex.printStackTrace();
    }

        res.setStatus(200);
        res.getWriter().write("{\"message\": \"Answer added successfully!\"}");
    }

    private void writeResponse(HttpServletResponse res, JSONObject jsonResponse) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
