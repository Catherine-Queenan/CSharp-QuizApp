import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Media extends AClass {
    private byte[] id;
    private String description = "";
    private String media_type = "";
    private String media_file_path;
    private String media_filename = "";
    private Integer media_start = 0;
    private Integer media_end = 0;

    public Media() {
        super("media");
    }

    public Media(String constructorParams) {
        super("media");
        String[] keyvaluePairs = constructorParams.split(",,,");
        for (String pair : keyvaluePairs) {
            String[] keyvaluePair = pair.split(":==");
            if(keyvaluePair.length > 1){
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
                    case "media_start":
                        this.media_start = Integer.valueOf(keyvaluePair[1]);
                        break;
                    case "media_end":
                        this.media_end = Integer.valueOf(keyvaluePair[1]);
                        break;
                }
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

    public Integer getMediaStart() {
        return this.media_start;
    }

    public void setMediaStart(Integer media_start) {
        this.media_start = media_start;
    }

    public Integer getMediaEnd() {
        return this.media_end;
    }

    public void setMediaEnd(Integer media_end) {
        this.media_end = media_end;
    }

    @Override
    public JSONObject serialize() {
        JSONObject jo = null;
        
        try {
        jo = new JSONObject();
        jo.put("id", (this.id != null ? new String(this.id, StandardCharsets.UTF_8 ): this.id));
        jo.put("description", this.description);
        jo.put("media_type", this.media_type);
        jo.put("media_file_path", this.media_file_path);
        jo.put("media_filename", this.media_filename);
        jo.put("media_start", this.media_start);
        jo.put("media_end", this.media_end);
        }catch(Exception e){
            e.printStackTrace();
        }

        return jo;
    }

}
