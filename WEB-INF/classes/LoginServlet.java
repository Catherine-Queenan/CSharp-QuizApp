import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;
import org.json.JSONObject;
import java.util.UUID;

//Compilation command
//javac -classpath .;c:\tomcat\lib\servlet-api.jar;c:tomcat\lib\sqlConnector.jar LoginServlet.java

public class LoginServlet extends HttpServlet {

    //Sends the html file to the res, displaying the page
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        RequestDispatcher view = req.getRequestDispatcher("/views/login.html");
        view.forward(req, res);
    }

    //Logs in user and returns JSON response
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        Connection con = null;

        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader reader = req.getReader();

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // JSON request for username and password values
        JSONObject jsonRequest = new JSONObject(sb.toString());
        String username = jsonRequest.getString("username");
        String password = jsonRequest.getString("password");

        String dbUserPassword = "";

        //Set content type to JSON
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        //Create JSON object to send back to client
        JSONObject jsonResponse = new JSONObject();

        //Check if username and password are empty
        try {
            //Database connection
            con = DatabaseUtil.getConnection();
            Statement statement = con.createStatement();

            //Query database for the user name
            ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE username  ='" + username + "'");
            
            if(rs.next()){ //if something is returned get the password
                dbUserPassword = rs.getString("password");
            } else {
                dbUserPassword = null;
            }

            statement.close();
            con.close();

        } catch(SQLException ex) {
            System.out.println("SQL Exception:" + ex.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred while connecting to the database.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = res.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }

        // Authentication check
        if(dbUserPassword != null && dbUserPassword.equals(password)) {
            //Session creation
            HttpSession session = req.getSession(true);
            session.setAttribute("USER_ID", username);

            //Generating a token
            String token = UUID.randomUUID().toString();

            // Cookie to store token
            Cookie tokenCookie = new Cookie("token", token);
            tokenCookie.setHttpOnly(true);
            tokenCookie.setSecure(true);
            tokenCookie.setMaxAge(60 * 60 * 24); // 1 day
            res.addCookie(tokenCookie);

            //Set JSON response for authorized access
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Login successful.");
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            //Set JSON response for unauthorized access
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "401 Invalid Login details");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        PrintWriter out = res.getWriter();
        out.print(jsonResponse.toString());
        out.flush();

    //     //Test
    //     String errMsg = "";
    //     res.setContentType("text/html");

    //     try {
            
    //         //DATABASE CONNECTION LINE
    //         con = DatabaseUtil.getConnection();
    //         // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1");
            
    //         Statement statement = con.createStatement();

    //         //Query database for the user name
    //         ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE username  =\"" + username + "\"");

    //         if (rs.next()) { //if something is returned get the password
    //             dbUserPassword = rs.getString("password");
    //         } else {
    //             dbUserPassword = null;
    //         }

    //         statement.close();
    //         con.close();

    //     } catch(SQLException ex) {
    //         errMsg = errMsg + "\n--- SQLException caught ---\n"; 
    //      while (ex != null) { 
    //         errMsg += "Message: " + ex.getMessage (); 
    //         errMsg += "SQLState: " + ex.getSQLState (); 
    //         errMsg += "ErrorCode: " + ex.getErrorCode (); 
    //         ex = ex.getNextException(); 
    //         errMsg += "";
    //      } 
    //     }

        
        
    //     if(dbUserPassword != null && dbUserPassword.equals(password)){
    //         //Session creation
    //         HttpSession session = req.getSession(true);
    //         session.setAttribute("USER_ID", username);
    //         res.setStatus(302);

    //         //Redirect to home page
    //         res.sendRedirect("home");
    //     } else {
    //         res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
    //         req.setAttribute("errorMessage", "Invalid Login Details.");
    //         RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
    //         view.forward(req, res);
    //         return;
    //     }

        

    }
}