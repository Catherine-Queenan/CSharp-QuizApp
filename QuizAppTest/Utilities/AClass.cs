using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public abstract class AClass
    {
        string tableType;
        public AClass(string tableType) { this.tableType = tableType; }
        abstract public JsonObject serialize();
        public string getTableType()
        {
            return this.tableType;
        }
    }
}
