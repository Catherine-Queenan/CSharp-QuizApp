using QuizApp.Utilities;
using Microsoft.AspNetCore.HttpOverrides;
using System.Security.Cryptography.X509Certificates;

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);

        // Add services to the container.
        builder.Services.AddControllers();
        builder.Services.AddScoped<DatabaseUtil>(); // Register DatabaseUtil as a scoped service

        // Add session services
        builder.Services.AddDistributedMemoryCache(); // Add in-memory cache
        builder.Services.AddSession(options =>
        {
            options.IdleTimeout = TimeSpan.FromMinutes(30); // Set session timeout
            options.Cookie.HttpOnly = true; // Protect cookies
            options.Cookie.IsEssential = true; // Ensure cookies are not optional
        });

        // Configure HTTPS with custom certificate
        builder.WebHost.ConfigureKestrel(serverOptions =>
        {
            serverOptions.ListenAnyIP(8080); // HTTP endpoint
            serverOptions.ListenAnyIP(8081, listenOptions =>
            {
                listenOptions.UseHttps("/https/aspnetapp.pfx", "q12773250P"); // HTTPS with certificate
            });
        });

        // Configure HTTPS redirection
        builder.Services.AddHttpsRedirection(options =>
        {
            options.RedirectStatusCode = StatusCodes.Status308PermanentRedirect;
            options.HttpsPort = 8081; // HTTPS port
        });

        var app = builder.Build();

        // Use forwarded headers to handle proxy setups (e.g., for containers)
        app.UseForwardedHeaders(new ForwardedHeadersOptions
        {
            ForwardedHeaders = ForwardedHeaders.XForwardedFor | ForwardedHeaders.XForwardedProto
        });

        app.UseStaticFiles(); // Serve static files

        // Map routes for static pages
        app.MapGet("/home", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/home.html");
        });

        app.MapGet("/login", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/login.html");
        });

        app.MapGet("/signup", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/signup.html");
        });

        // Configure the HTTP request pipeline.
        if (app.Environment.IsDevelopment())
        {
            app.UseDeveloperExceptionPage();
        }
        else
        {
            // Configure strict security headers for production
            app.UseHsts(); // Enforce HTTP Strict Transport Security (HSTS)
        }

        app.UseHttpsRedirection(); // Redirect HTTP to HTTPS
        app.UseAuthorization();
        app.UseSession(); // Enable session middleware

        // Map API controllers
        app.MapControllers();

        app.Run();
    }
}
