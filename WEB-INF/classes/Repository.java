
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


import org.json.JSONObject;

public class Repository implements IRepository {

    public static final String URL = "jdbc:mysql://localhost:3306/QuizApp";
    public static final String USER = "root";
    public static final String PASSWORD = "mySQL20@$CHANGE";
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
                if(!col.equalsIgnoreCase("media_id")){
                    query.append(col).append("= ?,");
                }
                
            }
            int keyPos = 0;
            query.deleteCharAt(query.length() - 1);
            query.append(" WHERE name = ?");
            System.out.println(query.toString());
            PreparedStatement ps = con.prepareStatement(query.toString());
            for (int i = 1; i <= values.length; i++) {
                if(values[i - 1].equalsIgnoreCase("media_id")){
                    System.out.println("ADDING ENTRY TO QUIZ_MEDIA");
                    // Associate answer with respective media
                String mediaQuery = "INSERT INTO quiz_media (quiz_name, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);

                    mediaPs.setString(1, pKey);
                    mediaPs.setBytes(2, updatedEntry.getString("media_id").getBytes());
                    mediaPs.executeUpdate();
                } else {
                    ps.setString(i, updatedEntry.getString(values[i - 1]));
                }
                keyPos++;
                
            }
            
            ps.setString(keyPos + 1, pKey);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new RuntimeException("Error updating entry in the \"quizzes\" table", ex);
        }
    }

    private void updateQuestion(JSONObject updatedEntry, String pKey, String[] values){
        try {
            StringBuilder query = new StringBuilder("UPDATE questions SET ");
            for (String col : values) {
                if(!col.equalsIgnoreCase("media_id")){
                    query.append(col).append("= ?,");
                }
            }

            System.out.println("UPDATED ENTRY:" + updatedEntry.toString());

            int keyPos = 0;
            query.deleteCharAt(query.length() - 1);
            query.append(" WHERE id = ?");
            PreparedStatement ps = con.prepareStatement(query.toString());
            for (int i = 1; i <= values.length; i++) {
                System.out.println("VALUE" + values[i - 1]);
                if(values[i - 1].equalsIgnoreCase("media_id")){
                    System.out.println("INSERTING INTO QUESTION_MEDIA");
                    // Associate answer with respective media
                String mediaQuery = "INSERT INTO question_media (question_id, media_id) VALUES (?, ?)";
                PreparedStatement mediaPs = con.prepareStatement(mediaQuery);

                    mediaPs.setBytes(1, pKey.getBytes());
                    mediaPs.setBytes(2, updatedEntry.getString("media_id").getBytes());
                    mediaPs.executeUpdate();
                } else {
                    ps.setString(i, updatedEntry.getString(values[i - 1]));
                    keyPos++;
                }
                
            }
            ps.setBytes(keyPos + 1, pKey.getBytes());
            ps.executeUpdate();

            System.out.println("QUESTION UPDATED");
        } catch (SQLException ex) {
            throw new RuntimeException("Error updating entry in the \"question\" table", ex);
        }
    }

    private void updateMedia(JSONObject updatedEntry, String pKey, String[] values){
        try {
            StringBuilder query = new StringBuilder("UPDATE media SET ");
            for (String col : values) {
                query.append(col).append("= ?,");
            }
            query.deleteCharAt(query.length() - 1);
            query.append(" WHERE id = ?");
            PreparedStatement ps = con.prepareStatement(query.toString());
            for (int i = 1; i <= values.length; i++) {
                if(values[i - 1].equalsIgnoreCase("media_start") || values[i - 1].equalsIgnoreCase("media_end")){
                    ps.setInt(i, updatedEntry.getInt(values[i - 1]));
                } else {
                    ps.setString(i, updatedEntry.getString(values[i - 1]));
                }
                
            }
            ps.setBytes(values.length + 1, pKey.getBytes());
            ps.executeUpdate();

            System.out.println("MEDIA UPDATED");
        } catch (SQLException ex) {
            throw new RuntimeException("Error updating entry in the \"media\" table", ex);
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
            case "media":
                updateMedia(updatedEntry, pKey, changeColumns);
                break;
            case "question":
                updateQuestion(updatedEntry, pKey, changeColumns);
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
                    AClass cat = select("category", "name=" + criteria).get(0);
                    JSONObject catJSON = cat.serialize();
                    if(!catJSON.isNull("media_id")){
                        PreparedStatement  deleteCatMedia= con.prepareStatement("DELETE FROM category_media WHERE category_name =" + criteria);
                        deleteCatMedia.executeUpdate();
                    }
                    update = "DELETE FROM categories WHERE name=\"" + criteria + "\"";

                    break;
                case "quiz":
                System.out.println("DELETING QUIZ");
                    AClass quiz = select("quiz", "name=\"" + criteria + "\"").get(0);
                    JSONObject quizJSON = quiz.serialize();
                    System.out.println("QUIZ TO BE DELETE: " + quizJSON.toString());
                    if(!quizJSON.isNull("media_id")){
                        PreparedStatement deleteQuizMedia = con.prepareStatement("DELETE FROM quiz_media WHERE quiz_name =\"" + criteria + "\"");
                        deleteQuizMedia.executeUpdate();
                    }
                    
                    update = "DELETE FROM quizzes WHERE name=\"" + criteria + "\"";
                    break;
                case "question":
                    id = criteria.getBytes();
                    update = "DELETE FROM questions WHERE id = ? ";
                    break;
                case "answer":
                id = criteria.getBytes();
                    update = "DELETE FROM answers WHERE id= ?";
                    break;
                case "media":
                id = criteria.getBytes();
                    update = "DELETE FROM media WHERE id= ?";
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
                    String [] queryCrits = criteria.split(",");
                    query = "SELECT * FROM answers WHERE question_id = ? ";
                    if(queryCrits.length > 1){
                        query = query + queryCrits[1];
                    }
                    
                    id = queryCrits[0].getBytes();
                    break;
                case "media":
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
            while (rs.next()) {
                String parameters = createConstructorParameters(rs, tableType);
                AClass cat = factory.createAClass(tableType, parameters);
                selectedEntries.add(cat);
            }

            return selectedEntries;
        } catch (SQLException ex) {
            throw new RuntimeException("Error selecting from the database", ex);
        }
    }

}
