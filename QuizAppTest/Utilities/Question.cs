using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public class Question : AClass
    {
        private byte[]? id;
        private string quiz_name = "";
        private string question_text = "";
        private string question_type = "";
        private byte[]? media_id;

        public Question() : base("question")
        {
            
        }

        public Question(string constructorParams) : base("question")
        {
            string[] keyvaluePairs = constructorParams.Split(",,,");
            foreach (string pair in keyvaluePairs)
            {
                string[] keyvaluePair = pair.Split(":==");
                if (keyvaluePair.Length > 1)
                {
                    switch (keyvaluePair[0])
                    {
                        case "id":
                            this.id = Convert.FromBase64String(keyvaluePair[1]);
                            break; // Assuming id is being set as a string representation
                        case "quiz_name":
                            this.quiz_name = keyvaluePair[1];
                            break;
                        case "question_text":
                            this.question_text = keyvaluePair[1];
                            break;
                        case "question_type":
                            this.question_type = keyvaluePair[1];
                            break;
                        case "media_id":
                            this.media_id = Convert.FromBase64String(keyvaluePair[1]);
                            break;
                    }
                }
            }
        }

        public byte[]? getId()
        {
            return this.id;
        }

        public void setId(byte[] id)
        {
            this.id = id;
        }

        public string getQuizName()
        {
            return this.quiz_name;
        }

        public void setQuizName(string quiz_name)
        {
            this.quiz_name = quiz_name;
        }

        public string getQuestionText()
        {
            return this.question_text;
        }

        public void setQuestionText(string question_text)
        {
            this.question_text = question_text;
        }

        public string getQuestionType()
        {
            return this.question_type;
        }

        public void setQuestionType(string question_type)
        {
            this.question_type = question_type;
        }

        public byte[]? getMediaId()
        {
            return this.media_id;
        }

        public void setMediaId(byte[] media_id)
        {
            this.media_id = media_id;
        }

    public override JsonObject serialize()
        {
            JsonObject jo = new JsonObject
            {
                ["id"] = (this.id != null ? BitConverter.ToString(this.id) : ""),
                ["quiz_name"] = this.quiz_name,
                ["question_text"] = this.question_text,
                ["question_type"] = this.question_type,
                ["media_id"] = (this.media_id != null ? BitConverter.ToString(this.media_id) : "")
            };
            

            return jo;
        }
    }
}
