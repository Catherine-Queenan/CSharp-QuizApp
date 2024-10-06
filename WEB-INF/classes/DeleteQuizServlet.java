import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

public class DeleteQuizServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        // String username = (String) session.getAttribute("USER_ID");
        // String role = getUserRoleFromDatabase(username);
        String role = (String)session.getAttribute("USER_ROLE");

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        String quizName = req.getParameter("quizName");

        // Connection con = null;
        // PreparedStatement ps = null;

        IRepository repository = new Repository();
        repository.init("com.mysql.cj.jdbc.Driver");
        String condition = "name = \"" + quizName + "\"";
        repository.delete("quiz", condition);

        // try {
        //     Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

        //     con = DatabaseUtil.getConnection();
        //     // Delete the quiz
        //     String deleteQuizSql = "DELETE FROM quizzes WHERE name = ?";
        //     ps = con.prepareStatement(deleteQuizSql);
        //     ps.setString(1, quizName);
        //     ps.executeUpdate();

            // Get the referer (previous page)
            String referer = req.getHeader("Referer");

            // Redirect to the previous page or a default page if referer is null
            if (referer != null) {
                res.sendRedirect(referer);
            } else {
                res.sendRedirect("quizList"); // Fallback to a default page if no referer is found
            }

        // } catch (Exception e) {
        //     e.printStackTrace();
        // } finally {
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
