public interface IRepository {
    void init(String connectString);
    void close();
    void insert(AClass entry);
    void update(AClass entry);
    void delete(String gameType, String criteria);
    void select(String gameType, String values, String criteria);
}
