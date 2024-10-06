import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

public class MainServlet extends HttpServlet {
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
        String role = getUserRoleFromDatabase(username);

        if ("a".equals(role)) {
            jsonResponse.put("role", "admin");
        }

        if (session.getAttribute("questions") != null) {
            session.removeAttribute("questions");
            session.removeAttribute("currQuestion");
        }

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
            }
            
            // Add categores to the JSON response
            jsonResponse.put("categories", categoriesArray);
        } catch (Exception e) {
            e.printStackTrace();
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

        // Write the JSON response
        res.getWriter().write(jsonResponse.toString());
        out.flush();
        out.close();
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


