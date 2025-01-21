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
            // If not set, fall back to appsettings.json.
            _connectionString = Environment.GetEnvironmentVariable("ConnectionStrings__DefaultConnection")
                                ?? configuration.GetConnectionString("DefaultConnection");
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
