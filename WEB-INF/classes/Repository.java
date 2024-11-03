
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


import org.json.JSONObject;

public class Repository implements IRepository {

    public static final String URL = "jdbc:mysql://localhost:3306/QuizApp";
    public static final String USER = "root";
    public static final String PASSWORD = "Cathgirlh6*";
    private Connection con = null;   

    private void insertCategory(JSONObject entry) {
        try {
            // Insert Category
            String query = "INSERT INTO categories (name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, entry.getString("name"));
            ps.executeUpdate();

            if (!entry.isNull("media_id")) {
                // Associate quiz with respective media
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
            System.out.println(entry);

            if (!entry.isNull("media_id")) {
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

            if (!entry.isNull("media_id")) {
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

            if (!entry.isNull("media_id")) {
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
            String query = "INSERT INTO media (id, description, media_type, media_file_path, media_filename, media_start, media_end) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setBytes(1, entry.getString("id").getBytes());
            ps.setString(2, entry.getString("description"));
            ps.setString(3, entry.getString("media_type"));
            ps.setString(4, entry.getString("media_file_path"));
            ps.setString(5, entry.getString("media_filename"));
            ps.setInt(6, entry.getInt("media_start"));
            ps.setInt(7, entry.getInt("media_end"));
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"media\" table", ex);
        }
    }

    private void insertWebSocketQuestion(JSONObject entry){
        try {
            // Insert WebsocketQuestion
            String query = "INSERT INTO QuestionsWithAnswers (questionId, questionText, answers, indexOfCorrect,quizName) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, entry.getString("questionId"));
            ps.setString(2, entry.getString("questionText"));
            ps.setString(3, entry.getJSONArray("answers").toString());
            ps.setInt(4, entry.getInt("indexOfCorrect"));
            ps.setString(5, entry.getString("quizName"));

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Error inserting entry into \"media\" table", ex);
        }
    }

    private void updateQuiz(JSONObject updatedEntry, String pKey, String[] values) {
        try {
            StringBuilder query = new StringBuilder("UPDATE quizzes SET ");
            for (String col : values) {
                query.append(col).append("= ?,");
            }
            query.deleteCharAt(query.length() - 1);
            query.append(" WHERE name = ?");
            PreparedStatement ps = con.prepareStatement(query.toString());
            for (int i = 1; i < values.length + 1; i++) {
                ps.setString(i, updatedEntry.getString(values[i - 1]));
            }
            ps.setString(values.length + 1, pKey);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error updating entry in the \"category\" table", ex);
        }
    }

    private String createConstructorParameters(ResultSet rs, String tableType) {
        StringBuilder parameters = new StringBuilder();
        try {
            ResultSetMetaData resultMetaData = rs.getMetaData();
            int columns = resultMetaData.getColumnCount();

            String prefix = "";
            for (int i = 1; i <= columns; i++) {
                String colName = resultMetaData.getColumnName(i);
                parameters.append(prefix).append(colName).append(":==");
                prefix = ",,,";
                if (colName.equals("id") || colName.contains("_id")) {
                    byte[] id = rs.getBytes(colName);
                    parameters.append(new String(id, StandardCharsets.UTF_8));
                } else {
                    String entry = rs.getString(colName);
                    parameters.append(entry);
                }
            }

            String mediaQuery;
            PreparedStatement psMedia = null;
            switch (tableType) {
                case "category":
                    mediaQuery = "SELECT media_id FROM category_media WHERE category_name = ?";
                    psMedia = con.prepareStatement(mediaQuery);
                    psMedia.setString(1, rs.getString("name"));
                    break;
                case "quiz":
                    mediaQuery = "SELECT media_id FROM quiz_media WHERE quiz_name = ?";
                    psMedia = con.prepareStatement(mediaQuery);
                    psMedia.setString(1, rs.getString("name"));
                    break;
                case "question":
                    mediaQuery = "SELECT media_id FROM question_media WHERE question_id = ?";
                    psMedia = con.prepareStatement(mediaQuery);
                    psMedia.setBytes(1, rs.getBytes("id"));
                    break;
                case "answer":
                    mediaQuery = "SELECT media_id FROM answer_media WHERE answer_id = ?";
                    psMedia = con.prepareStatement(mediaQuery);
                    psMedia.setBytes(1, rs.getBytes("id"));
                    break;
            }

            if (psMedia != null) {
                ResultSet rsMedia = psMedia.executeQuery();

                if (rsMedia.next()) {
                    byte[] media_id = rsMedia.getBytes("media_id");
                    parameters.append(prefix).append("media_id:==").append(new String(media_id, StandardCharsets.UTF_8));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error reading results of a select query from the database", ex);
        }

        return parameters.toString();
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
            case "websocket":
                insertWebSocketQuestion(newEntry);
                break;
        }
    }

    @Override
    public void update(AClass update, String pKey, String values) {
        String table = update.getTableType();
        JSONObject updatedEntry = update.serialize();
        String[] changeColumns = values.split(",");
        switch (table) {
            case "quiz":
                updateQuiz(updatedEntry, pKey, changeColumns);
                break;
            default:
                throw new RuntimeException("Error updating the database");
        }
    }

    @Override
    public void delete(String tableType, String criteria) {
        try {
            PreparedStatement deleteStatement = null;
            
            String update;
            byte[] id = null;
            switch (tableType) {
                case "category":
                    update = "DELETE FROM categories WHERE " + criteria;
                    break;
                case "quiz":
                    update = "DELETE FROM quizzes WHERE " + criteria;
                    break;
                case "question":
                System.out.println("HERE HERE  " + criteria);
                    id = criteria.getBytes();
                    update = "DELETE FROM questions WHERE id = ? ";
                    break;
                case "answer":
                    update = "DELETE FROM answers WHERE " + criteria;
                    break;
                case "media":
                    update = "DELETE FROM media WHERE " + criteria;
                    break;
                default:
                    throw new RuntimeException("Error deleting from the database. Entered type cannot be deleted");
            }
            
            deleteStatement = con.prepareStatement(update);

            if(id != null){
                deleteStatement.setBytes(1, id);
            }

            deleteStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error deleting from the database");
        }

    }

    @Override
    public ArrayList<AClass> select(String tableType, String criteria) {
        // SELECT columns FROM table WHERE criteria
        AClassFactory factory = new AClassFactory();
        ArrayList<AClass> selectedEntries = new ArrayList<>();

        try {

            String query;
            byte[] id = null;
            String where = (criteria.equals("")) ? criteria : "WHERE " + criteria;
            switch (tableType) {
                case "category":
                    query = "SELECT * FROM categories " + where;
                    break;
                case "quiz":
                    query = "SELECT * FROM quizzes " + where;
                    break;
                case "question":
                    query = "SELECT * FROM questions " + where;
                    break;
                case "answer":
                    query = "SELECT * FROM answers WHERE question_id = ? " + criteria.split(",")[1];
                    id = criteria.split(",")[0].getBytes();
                    break;
                case "media":
                    System.out.println(criteria);
                    id = criteria.getBytes();
                    query = "SELECT * FROM media WHERE id = ?";
                    break;
                default:
                    throw new RuntimeException("Error selecting from the database. Entered type cannot be select");
            }
            PreparedStatement selectStatement = con.prepareStatement(query);
            
            if (id != null) {
                selectStatement.setBytes(1, id);
            }

            ResultSet rs = selectStatement.executeQuery();
            System.out.println(rs.getMetaData());
            while (rs.next()) {
                System.out.println(rs);
                String parameters = createConstructorParameters(rs, tableType);
                System.out.println(parameters);
                AClass cat = factory.createAClass(tableType, parameters);
                System.out.println(cat.serialize());
                selectedEntries.add(cat);
            }

            return selectedEntries;
        } catch (SQLException ex) {
            throw new RuntimeException("Error selecting from the database", ex);
        }
    }

}
