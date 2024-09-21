import jakarta.servlet.http.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            con = DatabaseUtil.getConnection();
            // Create a statement to execute SQL queries
            stmt = con.createStatement();

            // Execute a query to get all categories
            String categoryQuery = "SELECT id, name FROM Categories";
            rs = stmt.executeQuery(categoryQuery);
            String categoryName = req.getParameter("categoryName");
            int categoryId = getCategoryId(con, categoryName);
            
            // Start HTML response
            out.println("<html><head><title>Categories and Questions</title></head><body>");
            out.println("<h1>Categories and Questions</h1>");

            // Process the result set for categories
            
                // Display category
                out.println("<h2>Category: " + categoryName + "</h2>");
                
                // Execute a query to get questions for this category
                String questionQuery = "SELECT id, text FROM Questions WHERE category_id = ?";
                PreparedStatement pstmt = con.prepareStatement(questionQuery);
                pstmt.setInt(1, categoryId);
                ResultSet questionRs = pstmt.executeQuery();

                // Display questions for this category
                out.println("<ul>");
                while (questionRs.next()) {
                    out.println("<li>" + questionRs.getString("text") + "</li>");
                    // if
                }
                out.println("</ul>");

                // Close the question result set
                questionRs.close();
                pstmt.close();

            // End HTML response
            out.println("</body></html>");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("<p>Error loading database driver.</p>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error connecting to the database.</p>");
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private int getCategoryId(Connection con, String categoryName) {
        try {
            String query = "SELECT id FROM Categories WHERE name = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
