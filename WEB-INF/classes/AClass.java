import org.json.JSONObject;


public abstract class AClass {
    String tableType;
    public AClass(String tableType){ this.tableType = tableType; }
    abstract public JSONObject serialize();
    public String getTableType(){
        return this.tableType;
    }
}
