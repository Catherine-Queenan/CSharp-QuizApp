import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Quiz extends AClass {
    private String name;
    private String category_name = "";
    private String description = "";
    private byte[] media_id = null;

    public Quiz() {
        super("quiz");
    }

    public Quiz(String constructorParams) {
        super("quiz");
        String[] keyvaluePairs = constructorParams.split(",,,");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":==");
            if(keyvaluePair.length > 1){
                switch (keyvaluePair[0]) {
                    case "name":
                        this.name = keyvaluePair[1];
                        break;
                    case "category_name":
                        this.category_name = keyvaluePair[1];
                        break;
                    case "description":
                        this.description = keyvaluePair[1];
                        break;
                    case "media_id":
                        this.media_id = keyvaluePair[1].getBytes();
                        break;
                }
            }
            
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return this.category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getMediaId() {
        return this.media_id;
    }

    public void setMediaId(byte[] media_id) {
        this.media_id = media_id;
    }

    @Override
    public JSONObject serialize() {
        JSONObject jo = null;

        try {
            jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("category_name", this.category_name);
            jo.put("description", this.description);
            jo.put("media_id",
                    (this.media_id != null ? new String(this.media_id, StandardCharsets.UTF_8) : this.media_id));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jo;
    }

}
