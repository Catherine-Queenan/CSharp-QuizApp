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

        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        // To build HTML content
        StringBuilder cardsHtml = new StringBuilder();

        try {
            try {
                // DATABASE CONNECTION LINE
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Class.forName("oracle.jdbc.OracleDriver");
            } catch (Exception ex) {}

            // DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root", "q12773250P");
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1");

            statement = con.createStatement();

            //CHANGE TO CATEGORY
            // Query database for the categories
            String sql = "SELECT name FROM categories";
            rs = statement.executeQuery(sql);

            // Generate HTML for each category
            while (rs.next()) {
                String categoryName = rs.getString("name");
                cardsHtml.append("<div class=\"category\">\n")
                         .append("  <div class=\"c-title\">").append(categoryName).append("</div>\n")
                         .append("  <div class=\"img\"></div>\n")
                         .append("</div>\n");
            }

            // Set categories as request attribute
            req.setAttribute("categoriesHtml", cardsHtml.toString());

        } catch (Exception e) {
            e.printStackTrace(); 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Forward the request to index.html but keeping the URL as /home
        RequestDispatcher view = req.getRequestDispatcher("/views/index.jsp");
        view.forward(req, res);
    }
}
