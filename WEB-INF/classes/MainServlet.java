import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.json.JSONObject;

public class MainServlet extends HttpServlet {
    private final IRepository repository = new Repository();
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        } else {
        
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        // Initialize JSON object to store the response
        JSONObject jsonResponse = new JSONObject();
        
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "You are not authorized to access this page.");
            res.getWriter().write(jsonResponse.toString());
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");
        // getUserRoleFromDatabase(username);
        StringBuilder adminHtml = new StringBuilder();
        if ("a".equals(role)) {
            jsonResponse.put("role", "admin");
        }

        if (session.getAttribute("questions") != null) {
            session.removeAttribute("questions");
            session.removeAttribute("currQuestion");
        }

        // try {
        //     // Attempt to retrieve the active sessions, which can throw SQLException
        //     List<ModerationSession> availableSessions = ModerationSessionManager.getActiveSessions();
        //     req.setAttribute("availableSessions", availableSessions);
        
        //     // Create a JSON array to hold the session data
        //     JSONArray jsonArray = new JSONArray();
            
        //     for (ModerationSession modSession : availableSessions) {
        //         // Create a JSON object for each session
        //         JSONObject jsonObject = new JSONObject();
        //         jsonObject.put("sessionId", modSession.getSessionId());
        //         jsonObject.put("moderator", modSession.getModerator());
        //         jsonObject.put("createdTime", modSession.getCreatedTime().toString()); // Convert to string if necessary
        
        //         // Add the JSON object to the JSON array
        //         jsonArray.put(jsonObject);
        //     }
        
        //     // Set the response type and send the JSON array
        //     res.setContentType("application/json");
        //     res.setCharacterEncoding("UTF-8");
        //     res.getWriter().write(jsonArray.toString()); // Write the JSON array as a response
        
        //     System.out.println("Available sessions: " + jsonArray.toString()); // For logging purposes
        // } catch (SQLException e) {
        //     e.printStackTrace(); // Handle the exception
        //     // You might want to return an error response here as well
        // }        

        JSONArray categoriesArray = new JSONArray();

        StringBuilder categoriesHtml = new StringBuilder();
        StringBuilder mediaHtml = new StringBuilder();
        try {
            repository.init("com.mysql.cj.jdbc.Driver");
        
            ArrayList<AClass> categories = repository.select("category", "");

            // Generate HTML for each category
            for (AClass category : categories) {
                JSONObject categoryJSON = category.serialize();
                String categoryName = categoryJSON.getString("name");

                mediaHtml.setLength(0);

                System.out.println(categoryJSON);

                if(!categoryJSON.isNull("media_id")){
                    AClass categoryMedia = repository.select("media", categoryJSON.getString("media_id")).get(0);
                    // String mediaFilePath = categoryMedia.serialize().getString("media_file_path");
                    //         mediaHtml.append("<img src=\"").append(mediaFilePath).append("\" alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                    categoryJSON.put("media", categoryMedia.serialize());
                }
                
                categoriesArray.put(categoryJSON);

                // while(rsMedia.next()){
            }
            
            // Add categores to the JSON response
            jsonResponse.put("categories", categoriesArray);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred while fetching categories.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Set status to 500
        } 
        
        // Write the JSON responses
        res.getWriter().write(jsonResponse.toString());
        out.flush();
        out.close();
    }
}
}
