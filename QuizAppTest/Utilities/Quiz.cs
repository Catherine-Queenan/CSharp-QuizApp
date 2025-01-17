using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public class Quiz : AClass
    {
        private string name = "";
        private string category_name = "";
        private string description = "";
        private byte[]? media_id;

        public Quiz() : base("quiz")
        {
            
        }

        public Quiz(string constructorParams) : base("quiz")
        {
            string[] keyvaluePairs = constructorParams.Split(",,,");
            foreach (string pair in keyvaluePairs)
            {
                string[] keyvaluePair = pair.Split(":==");
                if (keyvaluePair.Length > 1)
                {
                    switch (keyvaluePair[0])
                    {
                        case "name":
                            this.name = keyvaluePair[1];
                            break;
                        case "category_name":
                            this.category_name = keyvaluePair[1];
                            break;
                        case "description":
                            this.description = keyvaluePair[1];
                            break;
                        case "media_id":
                            this.media_id = Convert.FromHexString(keyvaluePair[1]);
                            break;
                    }
                }

            }
        }

        public string getName()
        {
            return this.name;
        }

        public void setName(string name)
        {
            this.name = name;
        }

        public string getCategoryName()
        {
            return this.category_name;
        }

        public void setCategoryName(string category_name)
        {
            this.category_name = category_name;
        }

        public string getDescription()
        {
            return this.description;
        }

        public void setDescription(string description)
        {
            this.description = description;
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
                ["name"] = this.name,
                ["category_name"] = this.category_name,
                ["description"] = this.description,
                ["media_id"] = (this.media_id != null ? BitConverter.ToString(this.media_id).Replace("-", "") : "")
            };

            return jo;
        }
    }
}
