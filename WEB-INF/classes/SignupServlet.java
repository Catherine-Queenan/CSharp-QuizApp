import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.sql.*;
import java.io.*;
import java.util.UUID;

/**
 * MySQL:
 * CREATE TABLE users  (ID BINARY(16), username CHAR (20), password CHAR(30), role CHAR(1));
 * 
 * Oracle:
 * CREATE TABLE users  (ID RAW(16), username CHAR (20), password CHAR(30), role CHAR(1));
 */
public class LoginServlet extends HttpServlet {
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

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("/views/signup.html");
        view.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String errMsg = "";
        Connection con = null;

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            try {
                //DATABASE CONNECTION LINE
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Class.forName("oracle.jdbc.OracleDriver");
            } catch (Exception ex) {}

            //DATABASE CONNECTION LINE 
            //CHANGE THE NAME OF DATABASE, USER, and PASSWORD
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "CS-pain-2024");
            // con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "oracle1");
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO users (ID, username, password, role) VALUES (?, ?, ?, ?)");
            UUID userId = UUID.randomUUID();
            preparedStatement.setBytes(1, asBytes(userId));

            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, "g");
            int row = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch(SQLException ex) {
            while (ex != null) { 
                System.out.println("Message: " + ex.getMessage ()); 
                System.out.println("SQLState: " + ex.getSQLState ()); 
                System.out.println("ErrorCode: " + ex.getErrorCode ()); 
                ex = ex.getNextException(); 
                System.out.println("");
            } 
        }

    }
}