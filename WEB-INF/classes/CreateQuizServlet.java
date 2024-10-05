import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.ByteBuffer;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class CreateQuizServlet extends HttpServlet {

    // Convert UUID to binary (byte array)
    public byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    // Convert binary (byte array) to UUID
    public UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            res.sendRedirect("login");
            return;
        }

        String username = (String) session.getAttribute("USER_ID");
        String role = getUserRoleFromDatabase(username);

        if (!"a".equals(role)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> categories = new ArrayList<>();

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection
            con = DatabaseUtil.getConnection();

            ps = con.prepareStatement("SELECT name FROM categories");
            rs = ps.executeQuery();

            while(rs.next()){
                categories.add(rs.getString("name"));
            }

            req.setAttribute("categories", categories);

        } catch (Exception e) {
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to 401
            req.setAttribute("errorMessage", "You are not authorized to access this page.");
            RequestDispatcher view = req.getRequestDispatcher("/views/401.jsp");
            view.forward(req, res);
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psCategoryMedia = null;
        PreparedStatement psMediaC = null;
        PreparedStatement psMediaQ = null;
        PreparedStatement psQuizMedia = null;
        PreparedStatement psCategories = null;

        try {
            String quizName = req.getParameter("quizName");

            //If category was selected as other use, use the entered other category name 
            String categoryName = req.getParameter("categoryName");
            String description = req.getParameter("description");
            Part categoryPart = req.getPart("categoryMedia");
            Part quizPart = req.getPart("quizMedia");
            String categoryFileName = Paths.get(categoryPart.getSubmittedFileName()).getFileName().toString();
            String quizFileName = Paths.get(quizPart.getSubmittedFileName()).getFileName().toString();

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

            //If a new category needs creating
            if(categoryName.equalsIgnoreCase("ADDANOTHERCATEGORY")){
                String newCategory = req.getParameter("newCategory");
                psCategories = con.prepareStatement("INSERT INTO categories (name) VALUES (?)");
                psCategories.setString(1, newCategory);
                psCategories.executeUpdate();
                categoryName = newCategory;

                //Insert media for the new category
                UUID categoryUUID = UUID.randomUUID();
                byte[] categoryMediaIdBinary = uuidToBytes(categoryUUID);

                // Insert media information into the `media` table
                if(categoryPart != null){
                    String mediaUrl = null;
                    System.out.println("Current folder: " + (new File(".")).getCanonicalPath());
                    File saveFile = new File(getServletContext().getRealPath("/public/media"));
                    File file = new File(saveFile, categoryFileName);
                    categoryPart.write(file.getAbsolutePath());
                    mediaUrl = "public/media/" + categoryFileName;

                    String insertMediaSql = "INSERT INTO media (id, media_type, media_file_path, media_filename, media_start, media_end) VALUES (?, ?, ?, ?, ?, ?)";
                    psMediaC = con.prepareStatement(insertMediaSql);
                    psMediaC.setBytes(1, categoryMediaIdBinary);
                    psMediaC.setString(2, "IMG");
                    psMediaC.setString(3, mediaUrl);
                    psMediaC.setString(4, categoryFileName);
                    psMediaC.setInt(5, 0);
                    psMediaC.setInt(6, 0);
                    psMediaC.executeUpdate();

                    // Insert into `category_media` table to link the category and the media
                    String insertCategoryMediaSql = "INSERT INTO category_media (category_name, media_id) VALUES (?, ?)";
                    psCategoryMedia = con.prepareStatement(insertCategoryMediaSql);
                    psCategoryMedia.setString(1, categoryName);
                    psCategoryMedia.setBytes(2, categoryMediaIdBinary);
                    psCategoryMedia.executeUpdate();
                }
            }

            // Insert new quiz with generated keys
            String sql = "INSERT INTO quizzes (name, category_name, description) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quizName);
            ps.setString(2, categoryName);
            ps.setString(3, description);
            int affectedRows = ps.executeUpdate();

            // Generate a UID for the quiz media
            UUID quizUUID = UUID.randomUUID();
            byte[] quizMediaIdBinary = uuidToBytes(quizUUID);

            // Insert media information into the `media` table
            if(quizPart != null){
                String mediaUrl = null;
                System.out.println("Current folder: " + (new File(".")).getCanonicalPath());
                File saveFile = new File(getServletContext().getRealPath("/public/media"));
                File file = new File(saveFile, quizFileName);
                quizPart.write(file.getAbsolutePath());
                mediaUrl = "public/media/" + quizFileName;

                String insertMediaSql = "INSERT INTO media (id, media_type, media_file_path, media_filename, media_start, media_end) VALUES (?, ?, ?, ?, ?, ?)";
                psMediaQ = con.prepareStatement(insertMediaSql);
                psMediaQ.setBytes(1, quizMediaIdBinary);
                psMediaQ.setString(2, "IMG");
                psMediaQ.setString(3, mediaUrl);
                psMediaQ.setString(4, quizFileName);
                psMediaQ.setInt(5, 0);
                psMediaQ.setInt(6, 0);
                psMediaQ.executeUpdate();

                // Insert into `quiz_media` table to link the quiz and the media
                String insertQuizMediaSql = "INSERT INTO quiz_media (quiz_name, media_id) VALUES (?, ?)";
                psQuizMedia = con.prepareStatement(insertQuizMediaSql);
                psQuizMedia.setString(1, quizName);
                psQuizMedia.setBytes(2, quizMediaIdBinary);
                psQuizMedia.executeUpdate();
            }

            if (affectedRows > 0) {
                // Optionally retrieve the generated quiz ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int quizId = generatedKeys.getInt(1);
                    // Log or use quizId as needed
                }
                // res.sendRedirect("index");
                res.sendRedirect("quizzes?categoryName=" + categoryName);

            } else {
                req.setAttribute("error", "Failed to create quiz. Please try again.");
                doGet(req, res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // req.setAttribute("error", "An error occurred while creating the quiz.");
            doGet(req, res);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psQuizMedia != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psMediaQ != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psCategoryMedia != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psMediaC != null)
                    ps.close();
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
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null)
                    ps.close();
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

        return role;
    }
}
