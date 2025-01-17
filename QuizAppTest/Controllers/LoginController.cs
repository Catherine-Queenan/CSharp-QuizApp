using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using QuizApp.Utilities;
using System.Security.Cryptography;
using System.Text;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class LoginController : ControllerBase
    {
        private readonly DatabaseUtil _databaseUtil;

        public LoginController(DatabaseUtil databaseUtil)
        {
            _databaseUtil = databaseUtil;
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest request)
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SELECT password, role FROM users WHERE username = @username";
                command.Parameters.Add(new MySqlParameter("@username", request.Username));

                using (var reader = command.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        var dbPassword = reader["password"]?.ToString();
                        var userRole = reader["role"]?.ToString();

                        // Hash the entered password and compare with the hashed password in the database
                        string hashedEnteredPassword = HashPassword(request.Password);

                        if (dbPassword == hashedEnteredPassword)
                        {
                            HttpContext.Session.SetString("USER_ID", request.Username);
                            HttpContext.Session.SetString("USER_ROLE", reader["role"]?.ToString() ?? "g");

                            reader.Close();
                            // Successful login
                            return Ok(new { Status = "Success", Message = "Login successful.", Role = userRole });
                        }
                    }
                }
            }


            // Invalid login
            return Unauthorized(new { Status = "Error", Message = "Invalid username or password." });
        }

        // Helper function to hash the password before comparing
        private string HashPassword(string password)
        {
            using (SHA256 sha256Hash = SHA256.Create())
            {
                byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(password));
                return Convert.ToBase64String(bytes);
            }
        }
    }

    public class LoginRequest
    {
        public string Username { get; set; }
        public string Password { get; set; }
    }
}
