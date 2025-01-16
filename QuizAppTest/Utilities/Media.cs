using System.Text.Json.Nodes;

namespace QuizApp.Utilities
{
    public class Media : AClass
    {
        private byte[]? id;
        private string? description = "";
        private string? media_type = "";
        private string? media_file_path;
        private string? media_filename = "";
        private int media_start = 0;
        private int media_end = 0;

        public Media() : base("media")
        {
        }

        public Media(string constructorParams) : base("media")
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
                        case "description":
                            this.description = keyvaluePair[1];
                            break; // Fixed key
                        case "media_type":
                            this.media_type = keyvaluePair[1];
                            break;
                        case "media_file_path":
                            this.media_file_path = keyvaluePair[1];
                            break;
                        case "media_filename":
                            this.media_filename = keyvaluePair[1];
                            break;
                        case "media_start":
                            this.media_start = Int32.Parse(keyvaluePair[1]);
                            break;
                        case "media_end":
                            this.media_end = Int32.Parse(keyvaluePair[1]);
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

        public string? getDescription()
        {
            return this.description;
        }

        public void setDescription(string description)
        {
            this.description = description;
        }

        public string? getMediaType()
        {
            return this.media_type;
        }

        public void setMediaType(string media_type)
        {
            this.media_type = media_type;
        }

        public string? getMediaFilePath()
        {
            return this.media_file_path;
        }

        public void setMediaFilePath(string media_file_path)
        {
            this.media_file_path = media_file_path;
        }

        public string? getMediaFilename()
        {
            return this.media_filename;
        }

        public void setMediaFilename(string media_filename)
        {
            this.media_filename = media_filename;
        }

        public int getMediaStart()
        {
            return this.media_start;
        }

        public void setMediaStart(int media_start)
        {
            this.media_start = media_start;
        }

        public int getMediaEnd()
        {
            return this.media_end;
        }

        public void setMediaEnd(int media_end)
        {
            this.media_end = media_end;
        }

    public override JsonObject serialize()
        {
            JsonObject jo = new JsonObject
            {
                ["id"] = (this.id != null ? BitConverter.ToString(this.id) : ""),
                ["description"] = this.description,
                ["media_type"] = this.media_type,
                ["media_file_path"] = this.media_file_path,
                ["media_filename"] = this.media_filename,
                ["media_start"] = this.media_start,
                ["media_end"] = this.media_end
            };

            return jo;
        }
    }
}
