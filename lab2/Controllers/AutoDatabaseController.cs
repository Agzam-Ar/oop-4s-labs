using System.Reflection;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ShapesDatabase.Models;

namespace ShapesDatabase.Controllers;

[Route("api")]
[ApiController]
public class AutoDatabaseController : ControllerBase {

    private readonly DatabaseContext _context;
    private readonly List<Type> _availableTypes;
    private readonly Dictionary<string, ClassMeta> _classes = new Dictionary<string, ClassMeta>();

    //private Type? GetShapeType(string name) => _availableTypes.FirstOrDefault(t => t.Name.Equals(name, StringComparison.OrdinalIgnoreCase));

    public AutoDatabaseController(DatabaseContext context) {
        _context = context;
        _availableTypes = Assembly.GetExecutingAssembly().GetTypes().Where(t => t.IsClass && !t.IsAbstract && t.IsSubclassOf(typeof(Shape))).ToList();
        foreach (var type in _availableTypes) {
            _classes.Add(type.Name, new ClassMeta(type));
        }
    }

    private class ClassMeta {
        public readonly Type Type;
        private readonly List<ConstructorInfo> _constructors = new();
        private readonly List<MethodInfo> _methods = new();
        private readonly List<PropertyInfo> _properties = new();

        const BindingFlags flags = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance;

        public ClassMeta(Type type) {
            Type = type;
            var currentType = type;
            _constructors.InsertRange(0, currentType.GetConstructors(flags | BindingFlags.FlattenHierarchy));
            _methods.InsertRange(0, currentType.GetMethods(flags | BindingFlags.FlattenHierarchy).Where(m => !m.IsSpecialName && m.DeclaringType != typeof(object)));
            //_methods.InsertRange(0, currentType.GetMethods(flags | BindingFlags.FlattenHierarchy).Where(m => m.IsSpecialName && m.DeclaringType != typeof(object)));
            while (currentType != null && currentType != typeof(object)) {
                _properties.InsertRange(0, currentType.GetProperties(flags | BindingFlags.DeclaredOnly));
                currentType = currentType.BaseType;
            }
            Console.WriteLine($"New class {type.Name}: {_constructors.Count} constructors, {_properties.Count} properties, {_methods.Count} methods");
        }

        public object GetSerializable() => new {
            TypeName = Type.Name,
            Properties = _properties.Select((p, i) => new {
                Id = i,
                p.Name,
                Type = p.PropertyType.Name,
                setter = p.CanWrite && p.GetSetMethod(nonPublic: false) != null
            }),
            Constructors = _constructors.Select((c, i) => new { Id = i, Params = c.GetParameters().Select(p => new { p.Name, Type = p.ParameterType.Name }) }),
            Methods = _methods.Select((m, i) => new {
                Id = i,
                m.Name,
                Params = m.GetParameters().Select(p => new {
                    p.Name,
                    Type = p.ParameterType.Name
                })
            })
        };

        public object Create(int id, System.Text.Json.JsonElement args) {
            Console.WriteLine($"Create #{id} for {Type}");
            if (id < 0 || id >= _constructors.Count) throw new Exception("Ctor not found");
            var ctor = _constructors[id];
            var parameters = BindParameters(ctor.GetParameters(), args);
            return ctor.Invoke(parameters);
        }

        public object? Call(object instance, int id, System.Text.Json.JsonElement args) {
            if (id < 0 || id >= _methods.Count) throw new Exception("Method not found");
            var method = _methods[id];
            var parameters = BindParameters(method.GetParameters(), args);
            return method.Invoke(instance, parameters);
        }

        private object?[] BindParameters(ParameterInfo[] targetParams, System.Text.Json.JsonElement args) {
            var result = new object?[targetParams.Length];
            for (int i = 0; i < targetParams.Length; i++) {
                var p = targetParams[i];
                if (args.TryGetProperty(p.Name!, out var val)) {
                    result[i] = System.Text.Json.JsonSerializer.Deserialize(val.GetRawText(), p.ParameterType);
                } else {
                    result[i] = p.HasDefaultValue ? p.DefaultValue : null;
                }
            }
            return result;
        }

        public void SetPropertyValue(object instance, int id, System.Text.Json.JsonElement value) {
            if (id < 0 || id >= _properties.Count) throw new Exception("Property not found");
            var prop = _properties[id];
            if (!prop.CanWrite) throw new Exception("Property is read-only");

            var typedValue = System.Text.Json.JsonSerializer.Deserialize(value.GetRawText(), prop.PropertyType);
            prop.SetValue(instance, typedValue);
        }
    }

    [HttpGet("types")]
    public IActionResult GetTypes() {
        return Ok(_availableTypes.Select(t => t.Name).ToList());
    }

    [HttpGet("meta/{type}")]
    public IActionResult GetTypeMetadata(string type) {
        if (!_classes.TryGetValue(type, out var meta)) return NotFound();
        return Ok(meta.GetSerializable());
    }

    [HttpGet("obj/{type}")]
    public IActionResult GetByType(string type) {
        if (!_classes.TryGetValue(type, out var meta)) return NotFound($"Type '{type}' not found.");
        var items = _context.Items
            .AsNoTracking()
            .AsEnumerable()
            .Where(x => x.GetType() == meta.Type)
            .ToList();

        return Ok(items.Select(x => (object)x));
    }

    [HttpPost("{type}")]
    public async Task<IActionResult> Create(string type, [FromBody] Shape shape) {
        _context.Items.Add(shape);
        await _context.SaveChangesAsync();
        return Ok(shape);
    }

    [HttpPost("create/{typeName}/{ctorId}")]
    public IActionResult CreateInstance(string typeName, int ctorId, [FromBody] System.Text.Json.JsonElement args) {
        if (!_classes.TryGetValue(typeName, out var meta)) return NotFound();

        var instance = meta.Create(ctorId, args);
        _context.Add(instance);
        _context.SaveChanges();

        return Ok(instance);
    }

    [HttpPost("call/{id:int}/{methodId:int}")]
    public async Task<IActionResult> CallMethod(int id, int methodId, [FromBody] System.Text.Json.JsonElement args) {
        var instance = await _context.Items.FindAsync(id);
        if (instance == null) return NotFound($"Object with ID {id} not found.");

        string typeName = instance.GetType().Name;
        if (!_classes.TryGetValue(typeName, out var meta)) return NotFound($"Metadata for type '{typeName}' not found.");

        try {
            var result = meta.Call(instance, methodId, args);
            await _context.SaveChangesAsync();
            return Ok(new { Result = result });
        } catch (Exception ex) {
            return BadRequest(new { Error = ex.InnerException?.Message ?? ex.Message });
        }
    }

    [HttpPost("prop/{id:int}/{propId:int}")]
    public async Task<IActionResult> SetProperty(int id, int propId, [FromBody] System.Text.Json.JsonElement value) {
        var instance = await _context.Items.FindAsync(id);
        if (instance == null) return NotFound();
        if (!_classes.TryGetValue(instance.GetType().Name, out var meta)) return NotFound();

        try {
            meta.SetPropertyValue(instance, propId, value);
            await _context.SaveChangesAsync();
            return Ok(new { Success = true });
        } catch (Exception ex) {
            return BadRequest(ex.Message);
        }
    }

    [HttpGet("delete/{id:int}")]
    public async Task<IActionResult> Delete(int id) {
        var item = await _context.Items.FindAsync(id);
        if (item == null) return NotFound();
        try {
            _context.Items.Remove(item);
            await _context.SaveChangesAsync();
            return Ok(new { Success = true, DeletedId = id, Type = item.GetType().Name });
        } catch (Exception ex) {
            return BadRequest(ex.Message);
        }
    }

}
