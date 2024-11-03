import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import jakarta.servlet.annotation.MultipartConfig;
import java.util.ArrayList;

import org.json.JSONObject;

@MultipartConfig
public class EditServlet extends HttpServlet {
    private final IRepository repository = new Repository();
    private final AClassFactory factory = new AClassFactory();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        JSONObject jsonResponse = new JSONObject();

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");
        // getUserRoleFromDatabase(username);

        jsonResponse.put("role", role);

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        
        // Connection con = null;
        // PreparedStatement ps = null;
        // ResultSet rs = null;
        StringBuilder editHtml = new StringBuilder();

        try {
            // Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // DATABASE CONNECTION LINE
            // con = DatabaseUtil.getConnection();

            repository.init("com.mysql.cj.jdbc.Driver");

            // Get quiz name from request parameters
            String quizName = req.getParameter("quizName");
            if (quizName == null || quizName.isEmpty()) {
                res.sendRedirect("home"); // If no quiz name is provided, redirect to home
                return;
            }

            // Query the database for the quiz details
            // String quizSql = "SELECT name, description FROM quizzes WHERE name = ?";
            // ps = con.prepareStatement(quizSql);
            // ps.setString(1, quizName);
            // rs = ps.executeQuery();

            AClass quiz = repository.select("quiz", "name=\"" + quizName +"\"").get(0);
            JSONObject quizJSON = quiz.serialize();

            if(!quizJSON.isNull("media_id")){
                AClass quizMedia = repository.select("media", quizJSON.getString("media_id")).get(0);
                // String mediaFilePath = categoryMedia.serialize().getString("media_file_path");
                //         mediaHtml.append("<img src=\"").append(mediaFilePath).append("\" alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                quizJSON.put("media", quizMedia.serialize());
            }
            jsonResponse.put("quiz", quizJSON);
                // String quizTitle = quizJSON.getString("name");
                // String quizDescription = quizJSON.getString("description");

                // // Generate the edit form for the quiz
                // editHtml.append("<div class=\"title cherry-cream-soda\">Edit Quiz: ").append(quizTitle).append("</div>")
                //     .append("<form class=\"eidtQuizForm\" method='post' action='edit'>")
                //     .append("<label for='title'>Quiz Title:</label>")
                //     .append("<input type='text' id='title' name='title' value='").append(quizTitle).append("'>")
                //     .append("<label for='description'>Description:</label>")
                //     .append("<textarea id='description' name='description'>").append(quizDescription).append("</textarea>")
                //     .append("<input type='hidden' name='quizName' value='").append(quizName).append("'>")
                //     .append("<div class='button-container'>")
                //     .append("<a href='editQuestions?quizName=").append(quizName).append("' class='button-link'>Go to List of Questions</a>")
                //     .append("<button class=\"saveBtn\" type='submit'>Save Changes</button>")
                //     .append("</div>")
                //     .append("</form>");
            

        } catch (Exception e) {
            e.printStackTrace();
        } 
        // finally {
        //     try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        //     try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        //     try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        // }

        // Set the form as a request attribute and forward it to the JSP for rendering
        // req.setAttribute("editFormHtml", editHtml.toString());
        // RequestDispatcher view = req.getRequestDispatcher("/views/edit.jsp");
        // view.forward(req, res);
        PrintWriter out = res.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Connection con = null;
        // PreparedStatement ps = null;

        try {
            // Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // DATABASE CONNECTION LINE
            // con = DatabaseUtil.getConnection();

            repository.init("com.mysql.cj.jdbc.Driver");

            // Get form data from the request
            String quizName = req.getParameter("quizName");
            String title = req.getParameter("title");
            String description = req.getParameter("description");

            String parameters = "name:==" + title + ",,,description:==" + description;
           
            // Update quiz in the database
            String updateQuizSql = "UPDATE quizzes SET name = ?, description = ? WHERE name = ?";
            // ps = con.prepareStatement(updateQuizSql);
            // ps.setString(1, title);
            // ps.setString(2, description);
            // ps.setString(3, quizName);
            // ps.executeUpdate();

            AClass updateQuiz = factory.createAClass("quiz", parameters);
            repository.update(updateQuiz, quizName, "description,name");


            Part quizMedia = req.getPart("quizMedia");
            String fileName = quizMedia.getSubmittedFileName();

            if(fileName != null && !fileName.isEmpty()){
                File saveFile = new File(getServletContext().getRealPath("/public/media"));
                File file = new File(saveFile, fileName);
                quizMedia.write(file.getAbsolutePath());
                String mediaUrl = "../public/media/" + fileName;

                String mediaParams = "media_file_path:==" + mediaUrl + ",,,media_filename:==" + fileName;
                AClass updateMedia = factory.createAClass("media", mediaParams);

                ArrayList<AClass> quiz = repository.select("quiz", "name=\"" + quizName + "\"");
                JSONObject quizJSON = quiz.get(0).serialize();
                repository.update(updateMedia, quizJSON.getString("media_id"), "media_file_path,media_filename");
            }
            // Redirect back to home after successful update
            res.setStatus(200); 
            res.getWriter().write("{\"message\": \"Quiz edited successfully!\", \"quizName\": \"" + title + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
        } 
        // finally {
        //     try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        //     try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        // }
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
