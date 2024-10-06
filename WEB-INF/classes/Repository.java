
import java.sql.*;

import org.json.JSONObject;

public class Repository implements IRepository {

    public static final String URL = "jdbc:mysql://localhost:3306/quizapp";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    private Connection con = null;

    private void insertCategory(JSONObject entry) {
        try {
            //Insert Category
            String query = "INSERT INTO categories (name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(query);
            
            ps.setString(1, entry.getString("name"));
            ps.executeUpdate();

            if(entry.getString("media_id") != null){
                //Associate quiz with respective media
                String mediaQuery = "INSERT INTO category_media (category_name, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);

                mediaPs.setString(1, entry.getString("name"));
                mediaPs.setBytes(2, entry.getString("media_id").getBytes());
                mediaPs.executeUpdate();
            }
            
        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"categories\" table", ex);
        }
    }

    private void insertQuiz(JSONObject entry) {
        try {
            // Insert Quiz
            String query = "INSERT INTO quizzes (name, category_name, description) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
    
            ps.setString(1, entry.getString("name"));
            ps.setString(2, entry.getString("category_name"));
            ps.setString(3, entry.getString("description"));
            ps.executeUpdate();
    
            if (entry.getString("media_id") != null) {
                // Associate quiz with respective media
                String mediaQuery = "INSERT INTO quiz_media (quiz_name, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);
    
                mediaPs.setString(1, entry.getString("name"));
                mediaPs.setBytes(2, entry.getString("media_id").getBytes());
                mediaPs.executeUpdate();
            }
    
        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"quizzes\" table", ex);
        }
    }
    

    private void insertQuestion(JSONObject entry) {
        try {
            // Insert Question
            String query = "INSERT INTO questions (id, quiz_name, question_text, question_type) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
    
            ps.setBytes(1, entry.getString("id").getBytes());
            ps.setString(2, entry.getString("quiz_name"));
            ps.setString(3, entry.getString("question_text"));
            ps.setString(4, entry.getString("question_type"));
            ps.executeUpdate();
    
            if (entry.getString("media_id") != null) {
                // Associate question with respective media
                String mediaQuery = "INSERT INTO question_media (question_id, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);
    
                mediaPs.setBytes(1, entry.getString("id").getBytes());
                mediaPs.setBytes(2, entry.getString("media_id").getBytes());
                mediaPs.executeUpdate();
            }
    
        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"questions\" table", ex);
        }
    }    

    private void insertAnswer(JSONObject entry) {
        try {
            // Insert Answer
            String query = "INSERT INTO answers (id, question_id, answer_text, is_correct, answer_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
    
            ps.setBytes(1, entry.getString("id").getBytes());
            ps.setBytes(2, entry.getString("question_id").getBytes());
            ps.setString(3, entry.getString("answer_text"));
            ps.setInt(4, entry.getInt("is_correct"));
            ps.setString(5, entry.getString("answer_type"));
            ps.executeUpdate();
    
            if (entry.getString("media_id") != null) {
                // Associate answer with respective media
                String mediaQuery = "INSERT INTO answer_media (answer_id, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);
    
                mediaPs.setBytes(1, entry.getString("id").getBytes());
                mediaPs.setBytes(2, entry.getString("media_id").getBytes());
                mediaPs.executeUpdate();
            }
    
        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"answers\" table", ex);
        }
    }
    
    private void insertMedia(JSONObject entry) {
        try {
            // Insert Media
            String query = "INSERT INTO media (id, description, media_type, media_file_path, media_filename) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
    
            ps.setBytes(1, entry.getString("id").getBytes());
            ps.setString(2, entry.getString("description"));
            ps.setString(3, entry.getString("media_type"));
            ps.setString(4, entry.getString("media_file_path"));
            ps.setString(5, entry.getString("media_filename"));
            ps.executeUpdate();
    
        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"media\" table", ex);
        }
    }
    
    
    

    @Override
    public void init(String connectString) {
        try {
            Class.forName(connectString);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Error loading database driver", ex);
        }

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }
    }

    @Override
    public void close() {
        try {
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException("Error closing the connection to the database", ex);
        }
    }

    // "INSERT INTO media (id, media_type, media_file_path, media_filename,
    // media_start, media_end) VALUES (?, ?, ?, ?, ?, ?)";
    @Override
    public void insert(AClass entry) {
        String table = entry.getTableType();
        JSONObject newEntry = entry.serialize();
        switch (table) {
            case "category":
                insertCategory(newEntry);
                break;
            case "quiz":
                insertQuiz(newEntry);
                break;
            case "question":
                insertQuestion(newEntry);
                break;
            case "answer":
                insertAnswer(newEntry);
                break;
            case "media":
                insertMedia(newEntry);
                break;
        }
    }

    @Override
    public void update(AClass entry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String gameType, String criteria) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void select(String gameType, String values, String criteria) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
