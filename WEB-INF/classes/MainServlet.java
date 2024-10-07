import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

public class MainServlet extends HttpServlet {
    private final IRepository repository = new Repository();
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ID");
        // getUserRoleFromDatabase(username);
        StringBuilder adminHtml = new StringBuilder();
        if ("a".equals(role)) {
            adminHtml.append("<div class=\"title cherry-cream-soda\">Admin Dashboard</div>\n<div class=\"admin\">\n")
                     .append("    <button class=\"newQuiz\" onclick=\"window.location.href='createQuiz'\">Create a new Quiz</button>\n")
                     .append("   </div>\n");
        }

        req.setAttribute("adminHtml", adminHtml.toString());


        if(session.getAttribute("questions") != null){
            session.removeAttribute("questions");
            session.removeAttribute("currQuestion");
        }

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
            }

            // Set categories as request attribute
            req.setAttribute("categoriesHtml", categoriesHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
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

        // Forward the request to the main.jsp or any other page you use to display categories
        RequestDispatcher view = req.getRequestDispatcher("/views/main.jsp");
        view.forward(req, res);
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


