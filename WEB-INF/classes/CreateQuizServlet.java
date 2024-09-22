import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.net.URLEncoder;
import java.sql.*;

public class CreateQuizServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.sendRedirect("login");
            return;
        }

        // Forward to the quiz creation form page
        RequestDispatcher view = req.getRequestDispatcher("/views/createQuiz.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("addQuestion");
            return;
        }
    
        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username);
    
        if (!"a".equals(role)) {
            res.sendRedirect("login");
            return;
        }
    
        Connection con = null;
        PreparedStatement ps = null;
    
        try {
            String quizName = req.getParameter("quizName");
            String categoryName = req.getParameter("categoryName");
            String description = req.getParameter("description");
    
            if (quizName == null || quizName.trim().isEmpty() ||
                categoryName == null || categoryName.trim().isEmpty()) {
                req.setAttribute("error", "Quiz name and category name are required.");
                doGet(req, res);
                return;
            }
    
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
    
            // Database connection
            con = DatabaseUtil.getConnection();
    
            // Insert new quiz with generated keys
            String sql = "INSERT INTO quizzes (name, category_name, description) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quizName);
            ps.setString(2, categoryName);
            ps.setString(3, description);
            int affectedRows = ps.executeUpdate();
    
            if (affectedRows > 0) {
                // Optionally retrieve the generated quiz ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int quizId = generatedKeys.getInt(1);
                    // Log or use quizId as needed
                }
                // res.sendRedirect("index");
            res.sendRedirect("addQuestion?quizName=" + URLEncoder.encode(quizName, "UTF-8"));

            } else {
                req.setAttribute("error", "Failed to create quiz. Please try again.");
                doGet(req, res);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            // req.setAttribute("error", "An error occurred while creating the quiz.");
            doGet(req, res);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
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