using System.Security.Cryptography;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using QuizApp.Utilities;

public class SignupController : ControllerBase
{
    private readonly DatabaseUtil _databaseUtil;

    public SignupController(DatabaseUtil databaseUtil)
    {
        _databaseUtil = databaseUtil;
    }

    // POST: api/signup
    [HttpPost("signup")]
    public IActionResult Signup([FromBody] SignupRequest request)
    {
        var response = new SignupResponse();

        using (var connection = _databaseUtil.GetConnection())
        {
            connection.Open();

            // Check if the user already exists
            var command = connection.CreateCommand();
            command.CommandText = "SELECT COUNT(*) FROM users WHERE username = @username";
            command.Parameters.Add(new MySqlParameter("@username", request.Username));

            int userCount = Convert.ToInt32(command.ExecuteScalar());

            if (userCount > 0)
            {
                response.Status = "error";
                response.Message = "401 Invalid Username and Password";
                return Unauthorized(response);
            }

            // Insert new user into the database
            Guid newGuid = Guid.NewGuid();
            byte[] userId = newGuid.ToByteArray();
            string role = "g"; // Default role for the user
            string hashedPassword = HashPassword(request.Password);

            var insertCommand = connection.CreateCommand();
            insertCommand.CommandText = "INSERT INTO users (id, username, password, role) VALUES (@id, @username, @password, @role)";
            insertCommand.Parameters.Add(new MySqlParameter("@id", userId));
            insertCommand.Parameters.Add(new MySqlParameter("@username", request.Username));
            insertCommand.Parameters.Add(new MySqlParameter("@password", hashedPassword));
            insertCommand.Parameters.Add(new MySqlParameter("@role", role));

            int rowsAffected = insertCommand.ExecuteNonQuery();

            if (rowsAffected > 0)
            {
                // Session and token generation (similar to your servlet)
                HttpContext.Session.SetString("USER_ID", request.Username);
                HttpContext.Session.SetString("USER_ROLE", role);

                string token = Guid.NewGuid().ToString();
                HttpContext.Response.Cookies.Append("token", token, new CookieOptions
                {
                    HttpOnly = true,
                    Secure = true,
                    Expires = DateTime.Now.AddDays(1)
                });

                response.Status = "success";
                response.Message = "User created successfully.";
                return StatusCode(201, response);
            }
        }

        // In case of any failure
        response.Status = "error";
        response.Message = "An error occurred during user creation.";
        return StatusCode(500, response);
    }

    // Helper function to hash the password before storing it in the database
    private string HashPassword(string password)
    {
        using (System.Security.Cryptography.SHA256 sha256Hash = SHA256.Create())
        {
            byte[] bytes = sha256Hash.ComputeHash(System.Text.Encoding.UTF8.GetBytes(password));
            return Convert.ToBase64String(bytes);
        }
    }
}

// Define the SignupRequest class
public class SignupRequest
{
    public string Username { get; set; }
    public string Password { get; set; }
}

public class SignupResponse
{
    public string Status { get; set; }
    public string Message { get; set; }
}
