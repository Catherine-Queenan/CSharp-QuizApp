import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;

//Compilation command
//javac -classpath .;c:\tomcat\lib\servlet-api.jar;c:tomcat\lib\sqlConnector.jar LoginServlet.java

public class LoginServlet extends HttpServlet {

    //Sends the html file to the res, displaying the page
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        RequestDispatcher view = req.getRequestDispatcher("/views/login.html");
        view.forward(req, res);
    }

    //Logins in user
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String errMsg = "";
        Connection con = null;

        //Retrieve entered username and password
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String dbUserPassword = "";

        //Test
        res.setContentType("text/html");

        try {
            
            //DATABASE CONNECTION LINE
            con = DatabaseUtil.getConnection();
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1");
            
            Statement statement = con.createStatement();

            //Query database for the user name
            ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE username  =\"" + username + "\"");

            if (rs.next()) { //if something is returned get the password
                dbUserPassword = rs.getString("password");
            } else {
                dbUserPassword = null;
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

        
        
        if(dbUserPassword != null && dbUserPassword.equals(password)){
            //Session creation
            HttpSession session = req.getSession(true);
            session.setAttribute("USER_ID", username);
            res.setStatus(302);

            //Redirect to home page
            res.sendRedirect("home");
        } else {
            PrintWriter out = res.getWriter(); //This is temporary just to test stuff
            out.append("Failed login"); //temporary way to notify of a failed login
        }

        

    }
}