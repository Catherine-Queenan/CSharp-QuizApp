import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import org.json.JSONObject;

public class DeleteQuizServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");
        HttpSession session = req.getSession(false);
        JSONObject jsonResponse = new JSONObject();
        
        if (session == null || session.getAttribute("USER_ID") == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "You are not authorized to access this page.");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            writeResponse(res, jsonResponse);
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("USER_ROLE");

        if (!"a".equals(role)) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "401 You are not authorized to access this page");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            writeResponse(res, jsonResponse);
            return;
        }

        if(quizName == null || quizName.isEmpty()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Quiz name is required.");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Set status to 400
            writeResponse(res, jsonResponse);
            return;
        }

        try {
            IRepository repository = new Repository();
            repository.init("com.mysql.cj.jdbc.Driver");
            repository.delete("quiz", quizName);
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Quiz deleted successfully.");
            res.setStatus(HttpServletResponse.SC_OK); // Set status to 200
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Quiz not found.");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND); // Set status to 404
        }

        writeResponse(res, jsonResponse);

        // res.setContentType("application/json");
        // res.setCharacterEncoding("UTF-8");
        // PrintWriter out = res.getWriter();
        // out.print(jsonResponse.toString());
        // out.flush();
    }

    private void writeResponse(HttpServletResponse res, JSONObject jsonResponse) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    // private JSONObject deleteQuiz(String quizName, String username) {
    //     JSONObject jsonResponse = new JSONObject();

    //     if (username == null || username.isEmpty()) {
    //         jsonResponse.put("status", "error");
    //         jsonResponse.put("message", "You are not authorized to access this page.");
    //         return jsonResponse;
    //     }

    //     // String username = (String) session.getAttribute("USER_ID");
    //     // String role = getUserRoleFromDatabase(username);
    //     String role = (String)session.getAttribute("USER_ROLE");

    //     if (!"a".equals(role)) {
    //         jsonResponse.put("status", "error");
    //         jsonResponse.put("message", "401 You are not authorized to access this page");
    //         return jsonResponse;
    //     }

    //     if (quizName == null || quizName.isEmpty()) {
    //         jsonResponse.put("status", "error");
    //         jsonResponse.put("message", "Quiz name is required.");
    //         return jsonResponse;
    //     }

    //     // Connection con = null;
    //     // PreparedStatement ps = null;

    //     IRepository repository = new Repository();
    //     repository.init("com.mysql.cj.jdbc.Driver");
    //     String condition = "name = \"" + quizName + "\"";
    //     try {
    //         repository.delete("quiz", condition);
    //         jsonResponse.put("status", "success");
    //         jsonResponse.put("message", "Quiz deleted successfully.");
    //     } catch(Exception e) {
    //         jsonResponse.put("status", "error");
    //         jsonResponse.put("message", "Quiz not found.");
    //     }

    //     // try {
    //     //     Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

    //     //     con = DatabaseUtil.getConnection();
    //     //     // Delete the quiz
    //     //     String deleteQuizSql = "DELETE FROM quizzes WHERE name = ?";
    //     //     ps = con.prepareStatement(deleteQuizSql);
    //     //     ps.setString(1, quizName);
    //     //     ps.executeUpdate();

    //     // finally {
    //     //     try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
    //     //     try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
    //     // }
    //     return jsonResponse;
    // }

    //public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // COOKIE AUTHENICATION LOGIC START
        // Cookie[] cookies = req.getCookies();
        // String authToken = null;

        // if (cookies != null) {
        //     for (Cookie cookie : cookies) {
        //         if ("token".equals(cookie.getName())) {
        //             authToken = cookie.getValue();
        //             break;
        //         }
        //     }
        // }

        // // Validating the token
        // if(authToken == null || !isValidToken(authToken)) {
            
        // }
        
        // if (session == null || session.getAttribute("USER_ID") == null) {
        //     res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
        //     JSONObject jsonResponse = new JSONObject();
        //     jsonResponse.put("status", "error");
        //     jsonResponse.put("message", "You are not authorized to access this page.");
        //     res.setContentType("application/json");
        //     PrintWriter out = res.getWriter();
        //     out.print(jsonResponse.toString());
        //     out.flush();
        //     return;
        // }

        // String username = getUsernameFromToken(authToken);
        // String role = getUserRoleFromDatabase(username);

        // if (!"a".equals(role)) {
        //     res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
        //     JSONObject jsonResponse = new JSONObject();
        //     jsonResponse.put("status", "error");
        //     jsonResponse.put("message", "401 You are not authorized to access this page");
        //     res.setContentType("application/json");
        //     PrintWriter out = res.getWriter();
        //     out.print(jsonResponse.toString());
        //     out.flush();
        //     return;
        // }

    //     HttpSession session = req.getSession(false);

    //     if (session == null || session.getAttribute("USER_ID") == null) {
    //         res.sendRedirect("login");
    //         return;
    //     }

    //     String username = (String) session.getAttribute("USER_ID");
    //     String role = getUserRoleFromDatabase(username);

    //     if (!"a".equals(role)) {
    //         res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
    //         req.setAttribute("errorMessage", "You are not authorized to access this page.");
    //         RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
    //         view.forward(req, res);
    //         return;
    //     }

    //     String quizName = req.getParameter("quizName");

    //     Connection con = null;
    //     PreparedStatement ps = null;

    //     try {
    //         Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver

    //         con = DatabaseUtil.getConnection();
    //         // Delete the quiz
    //         String deleteQuizSql = "DELETE FROM quizzes WHERE name = ?";
    //         ps = con.prepareStatement(deleteQuizSql);
    //         ps.setString(1, quizName);
    //         ps.executeUpdate();

    //         // Get the referer (previous page)
    //         String referer = req.getHeader("home");

    //         // Redirect to the previous page or a default page if referer is null
    //         if (referer != null) {
    //             res.sendRedirect(referer);
    //         } else {
    //             res.sendRedirect("home"); // Fallback to a default page if no referer is found
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     } finally {
    //         try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
    //         try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
    //     }
    // }

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
