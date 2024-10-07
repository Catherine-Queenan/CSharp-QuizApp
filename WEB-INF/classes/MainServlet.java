import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

import org.json.JSONObject;

public class MainServlet extends HttpServlet {
    private final IRepository repository = new Repository();
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
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
<<<<<<< HEAD
        String role = getUserRoleFromDatabase(username);

=======
        String role = (String) session.getAttribute("USER_ID");
        // getUserRoleFromDatabase(username);
        StringBuilder adminHtml = new StringBuilder();
>>>>>>> 933e5804cd95d4d3afa99e04fe5a72433eb29d66
        if ("a".equals(role)) {
            jsonResponse.put("role", "admin");
        }

        if (session.getAttribute("questions") != null) {
            session.removeAttribute("questions");
            session.removeAttribute("currQuestion");
        }

<<<<<<< HEAD
        Connection con = null;
        PreparedStatement psCategories = null;
        ResultSet rsCategories = null;
        PreparedStatement psMedia = null;
        PreparedStatement psCategoryMedia = null;
        ResultSet rsMedia = null;
        ResultSet rsCategoryMedia = null;

        JSONArray categoriesArray = new JSONArray();

        try {
            // Load MySQL driver and set up connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DatabaseUtil.getConnection();

            // Query to get all categories
            psCategories = con.prepareStatement("SELECT name FROM categories");
            rsCategories = psCategories.executeQuery();

            // Query to get media for each category
            psMedia = con.prepareStatement("SELECT media_id FROM category_media WHERE category_name = ?");
            psCategoryMedia = con.prepareStatement("SELECT media_file_path FROM media WHERE id = ?");

            while (rsCategories.next()) {
                String categoryName = rsCategories.getString("name");
                JSONObject categoryObject = new JSONObject();
            
                // Get media for the category
                psMedia.setString(1, categoryName);
                rsMedia = psMedia.executeQuery();
            
                // Process the media item (assuming there is only one)
                if (rsMedia.next()) {  // Changed to check only for one media item
                    InputStream mediaId = rsMedia.getBinaryStream("media_id");
                    if (mediaId != null) {
                        psCategoryMedia.setBinaryStream(1, mediaId);
                        rsCategoryMedia = psCategoryMedia.executeQuery();
                        if (rsCategoryMedia.next()) {
                            String mediaFilePath = rsCategoryMedia.getString("media_file_path");
                            JSONObject mediaObject = new JSONObject();
                            mediaObject.put("mediaFilePath", mediaFilePath);
                            
                            // Store the media object in the category object
                            categoryObject.put("media", mediaObject);
                        }
                    }
                }
            
                // Create JSON object for the category
                categoryObject.put("categoryName", categoryName);
                categoriesArray.put(categoryObject);
=======
        // Connection con = null;
        // PreparedStatement psCategories = null;
        // ResultSet rsCategories = null;
        // PreparedStatement psMedia = null;
        // PreparedStatement psCategoryMedia = null;
        StringBuilder categoriesHtml = new StringBuilder();
        StringBuilder mediaHtml = new StringBuilder();
        // ResultSet rsMedia = null;
        // ResultSet rsCategoryMedia = null;

        try {
            // Load MySQL driver
            // Class.forName("com.mysql.cj.jdbc.Driver");
            repository.init("com.mysql.cj.jdbc.Driver");
        

            // Database connection
            // con = DatabaseUtil.getConnection();
            ArrayList<AClass> categories = repository.select("category", "");

            // psCategories = con.prepareStatement("SELECT name FROM categories");
            // rsCategories = psCategories.executeQuery();

            // psMedia = con.prepareStatement("SELECT media_id FROM category_media WHERE category_name = ?");
            // psCategoryMedia = con.prepareStatement("SELECT media_file_path FROM media WHERE id = ?");
            
            // Generate HTML for each category
            for (AClass category : categories) {
                JSONObject categoryJSON = category.serialize();
                String categoryName = categoryJSON.getString("name");

                mediaHtml.setLength(0);

                // psMedia.setString(1, categoryName);
                // rsMedia = psMedia.executeQuery();
                if(!categoryJSON.isNull("media_id")){
                    AClass categoryMedia = repository.select("media", categoryJSON.getString("media_id")).get(0);
                    String mediaFilePath = categoryMedia.serialize().getString("media_file_path");
                            mediaHtml.append("<img src=\"").append(mediaFilePath).append("\" alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                }
                

                // while(rsMedia.next()){

                //     InputStream mediaId = rsMedia.getBinaryStream("media_id");
                    
                //     if(mediaId != null) {
                //         psCategoryMedia.setBinaryStream(1, mediaId);
                //         rsCategoryMedia = psCategoryMedia.executeQuery();
                //         if(rsCategoryMedia.next()) {
                //             String mediaFilePath = rsCategoryMedia.getString("media_file_path");
                //             mediaHtml.append("<img src=\"").append(mediaFilePath).append("\" alt=\"").append(categoryName).append("\" class=\"categoryImg\">");
                //         }
                //     }
                // }

                // Create a form for each category to redirect to the quizzes page
                categoriesHtml.append("<div class=\"category\">\n")
                             .append("<form action=\"quizzes\" method=\"get\">\n")
                             .append("    <input type=\"hidden\" name=\"categoryName\" value=\"" + categoryName + "\" />\n")
                             .append("    <input type=\"submit\" value=\"" + categoryName + "\" />\n")
                             .append("  <div class=\"img\">").append(mediaHtml.toString()).append("</div>\n")
                             .append("</div>\n")
                             .append("</form>\n");
>>>>>>> 933e5804cd95d4d3afa99e04fe5a72433eb29d66
            }
            
            // Add categores to the JSON response
            jsonResponse.put("categories", categoriesArray);
        } catch (Exception e) {
            e.printStackTrace();
<<<<<<< HEAD
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred while fetching categories.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Set status to 500
        } finally {
            try { if (rsCategories != null) rsCategories.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (rsMedia != null) rsMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (rsCategoryMedia != null) rsCategoryMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psCategories != null) psCategories.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psMedia != null) psMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (psCategoryMedia != null) psCategoryMedia.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }
=======
        } 
        // finally {
        //     try { if (rsCategories != null) rsCategories.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (rsMedia != null) rsMedia.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (rsCategoryMedia != null) rsCategoryMedia.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (psCategories != null) psCategories.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (psMedia != null) psMedia.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (psCategoryMedia != null) psCategoryMedia.close(); } catch (Exception e) { e.printStackTrace(); }
        //     try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        // }
>>>>>>> 933e5804cd95d4d3afa99e04fe5a72433eb29d66

        // Write the JSON response
        res.getWriter().write(jsonResponse.toString());
        out.flush();
        out.close();
    }
<<<<<<< HEAD

    private String getUserRoleFromDatabase(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String role = null;
=======
    // private String getUserRoleFromDatabase(String username) {
    //     Connection con = null;
    //     PreparedStatement ps = null;
    //     ResultSet rs = null;
    //     String role = null;
>>>>>>> 933e5804cd95d4d3afa99e04fe5a72433eb29d66

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

<<<<<<< HEAD
        return role;
    }
}
=======
    //     return role;
    // }
}


>>>>>>> 933e5804cd95d4d3afa99e04fe5a72433eb29d66
