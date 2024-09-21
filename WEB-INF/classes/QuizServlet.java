import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class QuizServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
            return;
        }

        String category = req.getParameter("categoryName");

        Connection con = null;
        Statement stmnt = null;
        ResultSet rs = null;
        StringBuilder quizzesHtml = new StringBuilder();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
             con = DatabaseUtil.getConnection();
            stmnt = con.createStatement();
            rs = stmnt.executeQuery("SELECT name FROM quizzes WHERE category_name = \"" + category + "\";");

            while (rs.next()) {
                String quizName = rs.getString("name");
                quizzesHtml.append("<div class=\"quiz\">\n")
                        .append("<form method=\"post\">\n")
                        .append("    <input type=\"hidden\" name=\"quizName\" value=\"" + quizName + "\" />\n")
                        .append("    <input type=\"submit\" value=\"" + quizName + "\" />\n")
                        .append("</form>\n")
                        .append("  <div class=\"img\"></div>\n")
                        .append("</div>\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (stmnt != null)
                    stmnt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        req.setAttribute("quizzesHtml", quizzesHtml);
        // Forward the request to the quiz.jsp
        RequestDispatcher view = req.getRequestDispatcher("/views/quiz.jsp");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizName = req.getParameter("quizName");

        Connection con = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ArrayList<InputStream> qIDs = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Driver
            con = DatabaseUtil.getConnection();
            stmnt = con.createStatement();
            rs = stmnt.executeQuery("SELECT id FROM questions WHERE quiz_name = \"" + quizName + "\" ORDER BY rand();");

            while (rs.next()) {
                InputStream qID = rs.getBinaryStream("id");
                qIDs.add(qID);
            }

            HttpSession session = req.getSession(false);
            if(session != null){
                session.setAttribute("quiz", quizName);
                session.setAttribute("questions", qIDs);
                session.setAttribute("currQuestion", 0);
                
                res.setStatus(302);
                res.sendRedirect("questions");
            } else {
                res.setStatus(302);
                res.sendRedirect("login");
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (stmnt != null)
                    stmnt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
