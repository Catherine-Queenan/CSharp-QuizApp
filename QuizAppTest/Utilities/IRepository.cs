using System.Formats.Asn1;

namespace QuizApp.Utilities
{
    public interface IRepository
    {
        void init(DatabaseUtil databaseUtil);
        void close();
        void insert(AClass entry);
        void update(AClass entry, string pkey, string values);
        void delete(string tableType, string criteria);
        List<AClass> select(string tableType, string criteria);
    }
}
