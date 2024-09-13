import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;

public class LoginServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("/views/login.html");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String errMsg = "";
        Connection con = null;

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String dbUserPassword = "";

        //Test
        res.setContentType("text/html");

        try {
            try {
                //DATABASE CONNECTION LINE
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Class.forName("oracle.jdbc.OracleDriver");
            } catch (Exception ex) {}

            //DATABASE CONNECTION LINE
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "CS-pain-2024");
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1");
            Statement statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM users");
            
            while (rs.next()) {
                if(rs.getString("username").equals(username)){
                    dbUserPassword = rs.getString("password");
                    break;
                }
            }

            statement.close();
            con.close();

        } catch(SQLException ex) {
            errMsg = errMsg + "\n--- SQLException caught ---\n"; 
         while (ex != null) { 
            errMsg += "Message: " + ex.getMessage (); 
            errMsg += "SQLState: " + ex.getSQLState (); 
            errMsg += "ErrorCode: " + ex.getErrorCode (); 
            ex = ex.getNextException(); 
            errMsg += "";
         } 
        }

        PrintWriter out = res.getWriter();
        if(dbUserPassword.equals(password)){
            out.println("Successful login");
        } else {
            out.println("Failed login");
        }

    }
}