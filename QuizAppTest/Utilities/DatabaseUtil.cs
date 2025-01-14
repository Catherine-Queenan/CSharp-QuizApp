using System.Data;
using MySql.Data.MySqlClient;
using Microsoft.Extensions.Configuration;

namespace QuizApp.Utilities
{
    public class DatabaseUtil
    {
        private readonly IConfiguration _configuration;

        public DatabaseUtil(IConfiguration configuration)
        {
            _configuration = configuration;
        }

        public IDbConnection GetConnection()
        {
            var connectionString = _configuration.GetConnectionString("DefaultConnection");
            return new MySqlConnection(connectionString);
        }
    }
}
