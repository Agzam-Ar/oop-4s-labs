using System.Reflection;
using Microsoft.EntityFrameworkCore;
using ShapesDatabase.Models;

namespace ShapesDatabase;

public class DatabaseContext : DbContext {

    public DatabaseContext(DbContextOptions<DatabaseContext> options) : base(options) { }

    public DbSet<Shape> Items { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder) {
        base.OnModelCreating(modelBuilder);

        var shapeTypes = Assembly.GetExecutingAssembly().GetTypes().Where(t => t.IsClass && !t.IsAbstract && t.IsSubclassOf(typeof(Shape)));
        foreach (var type in shapeTypes) {
            modelBuilder.Entity(type).ToTable(type.Name + "s");
        }
    }

}
