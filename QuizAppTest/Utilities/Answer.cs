using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public class Answer : AClass
    {
        private byte[]? id;
        private byte[]? question_id;
        private string? answer_text;
        private int is_correct = 0;
        private string? answer_type;
        private byte[]? media_id = null;

        public Answer() : base("answer")
        {
            
        }

        public Answer(string constructorParams) : base("answer")
        {
            string[] keyvaluePairs = constructorParams.Split(",,,");
            foreach (string pair in keyvaluePairs)
            {
                string[] keyvaluePair = pair.Split(":==");
                if (keyvaluePair.Length > 1)
                {
                    switch (keyvaluePair[0])
                    {
                        case "id": this.id = Convert.FromHexString(keyvaluePair[1]); break; // Assuming id is being set as a string representation
                        case "question_id": this.question_id = Convert.FromHexString(keyvaluePair[1]); break; // Same assumption
                        case "answer_text": this.answer_text = keyvaluePair[1]; break;
                        case "is_correct": this.is_correct = (keyvaluePair[1] == "1") ? 1 : 0; break;
                        case "answer_type": this.answer_type = keyvaluePair[1]; break;
                        case "media_id": this.media_id = Convert.FromHexString(keyvaluePair[1]); break;
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

        public byte[]? getQuestionId()
        {
            return this.question_id;
        }

        public void setQuestionId(byte[] question_id)
        {
            this.question_id = question_id;
        }

        public string? getAnswerText()
        {
            return this.answer_text;
        }

        public void setAnswerText(string answer_text)
        {
            this.answer_text = answer_text;
        }

        public int isCorrect()
        {
            return this.is_correct;
        }

        public void setCorrect(int is_correct)
        {
            this.is_correct = is_correct;
        }

        public string? getAnswerType()
        {
            return this.answer_type;
        }

        public void setAnswerType(string answer_type)
        {
            this.answer_type = answer_type;
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
                ["id"] = (this.id != null ? BitConverter.ToString(this.id).Replace("-", "") : ""),
                ["question_id"] = (this.question_id != null ? BitConverter.ToString(this.question_id).Replace("-", "") : ""),
                ["answer_text"] = this.answer_text,
                ["is_correct"] = this.is_correct,
                ["answer_type"] = this.answer_type,
                ["media_id"] = (this.media_id != null ? BitConverter.ToString(this.media_id).Replace("-", "") : "")
            };

            return jo;
        }
    }
}
