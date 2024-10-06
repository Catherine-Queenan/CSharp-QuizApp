import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Category extends AClass{
    private String name;
    private byte[] media_id = null;

    public Category() {
        super("category");
    }

    public Category(String constructorParams){
        super("category");
        String[] keyvaluePairs = constructorParams.split(",");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":");
            switch (keyvaluePair[0]) {
                case "name": this.name = keyvaluePair[1]; break;
                case "media_id": this.media_id = keyvaluePair[1].getBytes(); break;
            }
        } 
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getMediaId(){
        return this.media_id;
    }

    public void setMediaId(byte[] media_id) {
        this.media_id = media_id;
    }

    @Override
    JSONObject serialize() {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("media_id", new String(this.media_id, StandardCharsets.UTF_8));

        return jo;
    }
}