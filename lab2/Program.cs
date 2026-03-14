
using System.Reflection;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using ShapesDatabase.Models;
using Swashbuckle.AspNetCore.SwaggerGen;

namespace ShapesDatabase
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddDbContext<ShapesDatabase.DatabaseContext>(options => options.UseSqlite("Data Source=shapes.db"));

            // Add services to the container.
            builder.Services.AddControllers()
            .AddJsonOptions(options => {
                options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
            });

            // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();
            builder.Services.AddSwaggerGen(options => { // Classes autodoc
                var shapeTypes = Assembly.GetExecutingAssembly().GetTypes()
                    .Where(t => t.IsClass && !t.IsAbstract && t.IsSubclassOf(typeof(Shape)));

                foreach (var type in shapeTypes) {
                    options.DocumentFilter<ExplicitSchemaDocumentFilter>(type);
                }
                options.UseAllOfForInheritance();
                options.UseOneOfForPolymorphism();
            });

            // Tabels autofill
            var app = builder.Build();
            using (var scope = app.Services.CreateScope()) {
                var context = scope.ServiceProvider.GetRequiredService<ShapesDatabase.DatabaseContext>();
                context.Database.EnsureCreated();
            }

            // Configure the HTTP request pipeline.
            if(app.Environment.IsDevelopment()) {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            // static (wwwroot)
            app.UseDefaultFiles();
            //app.UseStaticFiles();
            app.UseStaticFiles(new StaticFileOptions {
                OnPrepareResponse = ctx => {
                    if (app.Environment.IsDevelopment()) {
                        ctx.Context.Response.Headers.Append("Cache-Control", "no-cache, no-store");
                        ctx.Context.Response.Headers.Append("Expires", "-1");
                    }
                }
            });
            app.UseRouting();

            // other
            app.UseHttpsRedirection();
            app.UseAuthorization();
            app.MapControllers();

            app.Run();
        }
    }

    public class ExplicitSchemaDocumentFilter : IDocumentFilter {
        private readonly Type _type;

        public ExplicitSchemaDocumentFilter(Type type) {
            _type = type;
        }

        public void Apply(OpenApiDocument swaggerDoc, DocumentFilterContext context) {
            context.SchemaGenerator.GenerateSchema(_type, context.SchemaRepository);
        }
    }
}
