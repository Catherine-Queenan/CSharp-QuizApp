import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

public class MainServlet extends HttpServlet {
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
        String role = getUserRoleFromDatabase(username);
        StringBuilder adminHtml = new StringBuilder();
        if ("a".equals(role)) {
            
            adminHtml.append("<h1>Admin dashboard</h1>\n<div class=\"admin\">\n")
                     .append("    <button onclick=\"window.location.href='createQuiz'\">Create a new Quiz</button>\n")
                     .append("   </div>\n");
                     
        }

        req.setAttribute("adminHtml", adminHtml.toString());


        if(session.getAttribute("questions") != null){
            session.removeAttribute("questions");
            session.removeAttribute("currQuestion");
        }

        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        StringBuilder categoriesHtml = new StringBuilder();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DatabaseUtil.getConnection();
            statement = con.createStatement();

            // Query database for categories
            String sql = "SELECT name FROM categories";
            rs = statement.executeQuery(sql);

            // Generate HTML for each category
            while (rs.next()) {
                String categoryName = rs.getString("name");

                // Create a form for each category to redirect to the quizzes page
                categoriesHtml.append("<div class=\"category\">\n")
                             .append("<form action=\"quizzes\" method=\"get\">\n")
                             .append("    <input type=\"hidden\" name=\"categoryName\" value=\"" + categoryName + "\" />\n")
                             .append("    <input type=\"submit\" value=\"" + categoryName + "\" />\n")
                             .append("</form>\n")
                             .append("  <div class=\"img\"></div>\n")
                             .append("</div>\n");
            }

            // Set categories as request attribute
            req.setAttribute("categoriesHtml", categoriesHtml.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to the main.jsp or any other page you use to display categories
        RequestDispatcher view = req.getRequestDispatcher("/views/main.jsp");
        view.forward(req, res);
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


