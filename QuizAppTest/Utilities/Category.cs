using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public class Category : AClass
    {

        private string? name;
        private byte[]? media_id;

        public Category() : base("category")
        {
            
        }

        public Category(string constructorParams) : base("category")
        {
            string[] keyvaluePairs = constructorParams.Split(",,,");
            foreach (string pair in keyvaluePairs)
            {
                string[] keyvaluePair = pair.Split(":==");
                if (keyvaluePair.Length > 1)
                {
                    switch (keyvaluePair[0])
                    {
                        case "name": this.name = keyvaluePair[1]; break;
                        case "media_id": this.media_id = Convert.FromHexString(keyvaluePair[1]); break;
                    }
                }
            }
        }

        public string? getName()
        {
            return this.name;
        }

        public void setName(string name)
        {
            this.name = name;
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
                ["media_id"] = (this.media_id != null ? BitConverter.ToString(this.media_id).Replace("-","") : "")
            };
   
            return jo;
        }
    }
}

