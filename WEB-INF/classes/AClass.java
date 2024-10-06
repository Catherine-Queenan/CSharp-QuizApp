import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public abstract class AClass {
    String tableType;
    public AClass(String tableType){ this.tableType = tableType; }
    abstract JSONObject serialize();
    public String getTableType(){
        return this.tableType;
    }
}
