
import java.util.ArrayList;

public interface IRepository {
    void init(String connectString);
    void close();
    void insert(AClass entry);
    void update(AClass entry, String pkey, String values);
    void delete(String tableType, String criteria);
    ArrayList<AClass> select(String tableType, String criteria);
}
