package com.example.meta;

import java.lang.reflect.*;
import java.util.*;

import com.example.annotations.Named;
import com.google.gson.*;

import lombok.Setter;

public class ClassMeta {

	public final Class<?> type;
	private final List<Constructor<?>> constructors;
	private final List<Method> methods;
	private final List<Field> properties;

	public ClassMeta(Class<?> type) {
		this.type = type;
		this.constructors = Arrays.asList(type.getDeclaredConstructors());
		this.methods = new ArrayList<>();
		for (Method m : type.getMethods()) {
			if (!m.isSynthetic() && m.getDeclaringClass() != Object.class) methods.add(m);
		}
		this.properties = new ArrayList<>();
		Class<?> current = type;
		while (current != null && current != Object.class) {
			for (Field f : current.getDeclaredFields()) properties.add(f);
			current = current.getSuperclass();
		}
	}

	public Map<String, Object> getSerializable() {
		Map<String, Object> result = new HashMap<>();
		result.put("typeName", type.getSimpleName());

		var props = new ArrayList<>();
		for (int i = 0; i < properties.size(); i++) {
			var p = properties.get(i);
			var prop = new HashMap<>();
			prop.put("id", i);
			prop.put("name", p.getName());
			prop.put("type", p.getType().getSimpleName());
			prop.put("setter", p.isAnnotationPresent(Setter.class));
			props.add(prop);
		}
		result.put("properties", props);

		var ctors = new ArrayList<>();
		for (int i = 0; i < constructors.size(); i++) {
			Constructor<?> c = constructors.get(i);
			List<Map<String, String>> params = new ArrayList<>();
			for (Parameter p : c.getParameters()) {
				Map<String, String> param = new HashMap<>();
				String name = p.getName();
				try {name = p.getAnnotation(Named.class).value();} catch (Exception e) {}
				param.put("name", name);
				param.put("type", p.getType().getSimpleName());
				params.add(param);
			}
			Map<String, Object> ctor = new HashMap<>();
			ctor.put("id", i);
			ctor.put("params", params);

			ctors.add(ctor);
		}
		result.put("constructors", ctors);

		var meths = new ArrayList<>();
		for (int i = 0; i < methods.size(); i++) {
			Method m = methods.get(i);
			List<Map<String, String>> params = new ArrayList<>();
			for (Parameter p : m.getParameters()) {
				Map<String, String> param = new HashMap<>();
				param.put("name", p.getName());
				param.put("type", p.getType().getSimpleName());
				params.add(param);
			}
			Map<String, Object> meth = new HashMap<>();
			meth.put("id", i);
			meth.put("name", m.getName());
			meth.put("params", params);
			meths.add(meth);
		}
		result.put("methods", meths);

		return result;
	}

	public Object create(int ctorId, Map<String, JsonElement> args) throws Exception {
		if (ctorId < 0 || ctorId >= constructors.size()) throw new Exception("Constructor not found");
		Constructor<?> ctor = constructors.get(ctorId);
		Object[] params = bindParameters(ctor.getParameters(), args);
		return ctor.newInstance(params);
	}

	public Object call(Object instance, int methodId, Map<String, JsonElement> args) throws Exception {
		if (methodId < 0 || methodId >= methods.size()) throw new Exception("Method not found");
		Method method = methods.get(methodId);
		Object[] params = bindParameters(method.getParameters(), args);
		return method.invoke(instance, params);
	}

	public void setProperty(Object instance, int propId, Object value) throws Exception {
		if (propId < 0 || propId >= properties.size()) throw new Exception("Property not found");
		properties.get(propId).set(instance, value);
	}

	private Object[] bindParameters(Parameter[] targetParams, Map<String, JsonElement> args) {
		Object[] result = new Object[targetParams.length];
		for (int i = 0; i < targetParams.length; i++) {
			Parameter parm = targetParams[i];
			String name = parm.getName();
			try {name = parm.getAnnotation(Named.class).value();} catch (Exception e) {}
			var val = args.get(name);
			if(val instanceof JsonPrimitive p) {
				result[i] = switch (parm.getType()) {
				case Class<?> t when t == byte.class -> p.getAsByte();
				case Class<?> t when t == short.class -> p.getAsShort();
				case Class<?> t when t == int.class -> p.getAsInt();
				case Class<?> t when t == long.class -> p.getAsLong();
				case Class<?> t when t == boolean.class -> p.getAsBoolean();
				default -> p.getAsString();
				};
			}
		}
		return result;
	}

}
