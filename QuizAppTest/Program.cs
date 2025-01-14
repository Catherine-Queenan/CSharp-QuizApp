using QuizApp.Utilities;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddScoped<DatabaseUtil>(); // Register DatabaseUtil as a scoped service

// Add session services
builder.Services.AddDistributedMemoryCache(); // Add in-memory cache
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30); // Set session timeout (optional)
});

var app = builder.Build();

app.UseStaticFiles();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.UseSession(); // Enable session middleware
app.MapControllers();
app.Run();
