import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;
import java.util.UUID;
import java.nio.*;
import org.json.JSONObject;
import java.util.UUID;

public class SignupServlet extends HttpServlet {
    //Creating unique user Ids
    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
    public static UUID asUuid(byte[] bytes) {
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            long firstLong = bb.getLong();
            long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
    }

    //Dispaly html page
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        RequestDispatcher view = req.getRequestDispatcher("/views/signup.html");
        view.forward(req, res);
    }

    //Send new sign up info to database 
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

        //Set content type to JSON
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        //Create JSON object to send back to client
        JSONObject jsonResponse = new JSONObject();

        try {
            con = DatabaseUtil.getConnection();
            Statement statement = con.createStatement();

            //Query database for the user name
            ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE username  ='" + username + "'");
        
            if(rs.next()) {
                // User already exists
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "401 Invalid Username and Password");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                //Create insert statement for database
                PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO users (ID, username, password, role) VALUES (?, ?, ?, ?)");
                UUID userId = UUID.randomUUID();
                preparedStatement.setBytes(1, asBytes(userId));
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, "g");

                int row = preparedStatement.executeUpdate();
                preparedStatement.close();
                
                // Session created
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

                // User created successfully
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "User created successfully.");
                res.setStatus(HttpServletResponse.SC_CREATED);
            }

            statement.close();
            con.close();
        } catch (SQLException ex) {
            System.out.println("SQL Exception:" + ex.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred while connecting to the database.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            PrintWriter out = res.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
        }

        // try {
        //     try {
        //         //DATABASE CONNECTION LINE
        //         Class.forName("com.mysql.cj.jdbc.Driver");
        //         // Class.forName("oracle.jdbc.OracleDriver");
        //     } catch (Exception ex) {}

        //     //DATABASE CONNECTION LINE 
        //     con = DatabaseUtil.getConnection();
        //      Statement statement = con.createStatement();

        //     //Query database for the user name
        //     ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE username  =\"" + username + "\"");

        //     if (rs.next()) { //if something is returned get the password
        //         username = null;
        //     } else {
        //         //Create insert statement for database
        //         PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO users (ID, username, password, role) VALUES (?, ?, ?, ?)");
        //         UUID userId = UUID.randomUUID();
        //         preparedStatement.setBytes(1, asBytes(userId));

        //         preparedStatement.setString(2, username);
        //         preparedStatement.setString(3, password);
        //         preparedStatement.setString(4, "g");
                
        //         int row = preparedStatement.executeUpdate();

        //         preparedStatement.close();
        //     }

            
        // } catch(SQLException ex) {
            
        //     System.out.println("FAILED ON SIGNUP");
        //     while (ex != null) { 
        //         System.out.println("Message: " + ex.getMessage ()); 
        //         System.out.println("SQLState: " + ex.getSQLState ()); 
        //         System.out.println("ErrorCode: " + ex.getErrorCode ()); 
        //         ex = ex.getNextException(); 
        //         System.out.println("");
        //     } 

        // }

        
        // if(username != null){
        //     //Session creation
        //     HttpSession session = req.getSession(true);
        //     session.setAttribute("USER_ID", username);
        //     res.setStatus(302);

        //     res.sendRedirect("home");
        // } else {
        //     res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
        //     req.setAttribute("errorMessage", "This username is already in use.");
        //     RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
        //     view.forward(req, res);
        //     return;
        // }
        

    }
}