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
        builder.Services.AddControllersWithViews();
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
                listenOptions.UseHttps("certs/aspnetapp.pfx", "q12773250P"); // HTTPS with certificate
            });
        });

        // Configure HTTPS redirection
        builder.Services.AddHttpsRedirection(options =>
        {
            options.RedirectStatusCode = StatusCodes.Status308PermanentRedirect;
            options.HttpsPort = 8081; // HTTPS port
        });

        // Websocket stuff
        builder.Services.AddSession();

        var app = builder.Build();

        // Use forwarded headers to handle proxy setups (e.g., for containers)
        app.UseForwardedHeaders(new ForwardedHeadersOptions
        {
            ForwardedHeaders = ForwardedHeaders.XForwardedFor | ForwardedHeaders.XForwardedProto
        });

        // Use HTTPS redirection
        //app.UseHttpsRedirection();

        // Serve static files
        app.UseStaticFiles();

        // Enable session middleware (after static files and before routing)
        app.UseSession();

        // Enable routing
        app.UseRouting();

        // Enable authorization (if needed)
        app.UseAuthorization();

        app.MapGet("/test", async context =>
        {
            await context.Response.WriteAsync("Test route is working!");
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

        app.MapGet("/home", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/home.html");
        });

        app.MapGet("/quizzes/{*slug}", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/quizzes.html");
        });

        app.MapGet("/play", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/play.html");
        });

        app.MapGet("/moderatedQuiz/{*slug}", async context =>
        {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/moderatedMode.html");
        });

        app.MapGet("/error", async context => {
            context.Response.ContentType = "text/html";
            await context.Response.SendFileAsync("wwwroot/error.html");
        });

        // Websocket stuff
        app.UseWebSockets();
        app.UseMiddleware<QuestionWebSocketMiddleware>();
        app.UseSession();

        // Map API controllers
        app.MapControllers();

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

        app.Run();
    }

}
