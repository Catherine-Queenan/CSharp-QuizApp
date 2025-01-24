using System.Data;
using MySql.Data.MySqlClient;
using Microsoft.Extensions.Configuration;

namespace QuizApp.Utilities
{
    public class DatabaseUtil
    {
        private readonly string _connectionString;

        public DatabaseUtil(IConfiguration configuration)
        {

            // First, try to get the connection string from environment variables.
            string? dbUrl = Environment.GetEnvironmentVariable("DB_URL");  // Fallback to local
            string? dbUser = Environment.GetEnvironmentVariable("DB_USER");  // Fallback to root
            string? dbPassword = Environment.GetEnvironmentVariable("DB_PASSWORD");  // Fallback to root
            string dbName = "quizapp";  // Default DB name


            if (dbUrl != null && dbUser != null & dbPassword != null)
            {
                // Build the connection string using environment variables
                _connectionString = $"Server={dbUrl};Database={dbName};User={dbUser};Password={dbPassword};SslMode=None;";
            } else
            {
                _connectionString = configuration.GetConnectionString("DefaultConnection");
            }
            
            
            // If not set, fall back to appsettings.json.
           
        }

        public IDbConnection GetConnection()
        {
            // Validate the connection string
            if (string.IsNullOrEmpty(_connectionString))
            {
                throw new InvalidOperationException("Database connection string is not configured.");
            }

            return new MySqlConnection(_connectionString);
        }
    }
}
