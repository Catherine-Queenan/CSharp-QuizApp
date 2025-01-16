namespace QuizApp.Utilities
{
    public class AClassFactory
    {
        public AClass createAClass(string tableType, string constructorParameters)
        {
            if (tableType == "category")
            {
                return new Category(constructorParameters);
            }
            else if (tableType== "quiz")
            {
                return new Quiz(constructorParameters);
            }
            else if (tableType == "question")
            {
                return new Question(constructorParameters);
            }
            else if (tableType == "answer")
            {
                return new Answer(constructorParameters);
            }
            else if (tableType == "media")
            {
                return new Media(constructorParameters);
            }
            return null;
        }
    }
}
