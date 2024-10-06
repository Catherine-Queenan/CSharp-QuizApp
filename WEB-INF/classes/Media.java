import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Media extends AClass {
    private byte[] id;
    private String description; // Fixed typo from "descripition"
    private String media_type;
    private String media_file_path;
    private String media_filename;

    public Media() {
        super("media");
    }

    public Media(String constructorParams) {
        super("media");
        String[] keyvaluePairs = constructorParams.split(",");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":");
            switch (keyvaluePair[0]) {
                case "id":
                    this.id = keyvaluePair[1].getBytes();
                    break; // Assuming id is being set as a string representation
                case "description":
                    this.description = keyvaluePair[1];
                    break; // Fixed key
                case "media_type":
                    this.media_type = keyvaluePair[1];
                    break;
                case "media_file_path":
                    this.media_file_path = keyvaluePair[1];
                    break;
                case "media_filename":
                    this.media_filename = keyvaluePair[1];
                    break;
            }
        }
    }

    public byte[] getId() {
        return this.id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaType() {
        return this.media_type;
    }

    public void setMediaType(String media_type) {
        this.media_type = media_type;
    }

    public String getMediaFilePath() {
        return this.media_file_path;
    }

    public void setMediaFilePath(String media_file_path) {
        this.media_file_path = media_file_path;
    }

    public String getMediaFilename() {
        return this.media_filename;
    }

    public void setMediaFilename(String media_filename) {
        this.media_filename = media_filename;
    }

    @Override
    JSONObject serialize() {
        JSONObject jo = new JSONObject(
                "{\"id\":\"" + new String(this.id, StandardCharsets.UTF_8)
                        + "\", \"description\":\"" + this.description
                        + "\", \"media_type\":\"" + this.media_type
                        + "\", \"media_file_path\":\"" + this.media_file_path
                        + "\", \"media_filename\":\"" + this.media_filename + "\"}");
        return jo;
    }

}
