package com.example;

import com.example.meta.ClassMeta;
import com.example.models.*;
import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class Server {

	public final Map<String, ClassMeta> classes = new HashMap<>();
    private final DatabaseContext context = new DatabaseContext();

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    @SuppressWarnings("unchecked")
	public void start() throws IOException {
        List<Class<? extends Shape>> types = List.of(Rect.class, Ring.class);

        for (var type : types) classes.put(type.getSimpleName(), new ClassMeta(type));
        
        HttpServer server = HttpServer.create(new InetSocketAddress(7053), 0);
        
        var atypes = new ArrayList<>(classes.keySet());
        server.createContext("/api/types", e -> sendJson(e, atypes));
        
        server.createContext("/api/meta", e -> {
        	try {
        		String path = e.getRequestURI().getPath();
        		String[] parts = path.split("/");
        		String type = parts[parts.length - 1];
        		var meta = classes.get(type);
        		sendJson(e, meta.getSerializable());
        	} catch (Exception ex) {
        		sendError(e, 500, ex);
        	}
        });
        server.createContext("/api/obj", e -> {
            try {
                String path = e.getRequestURI().getPath();
                String[] parts = path.split("/");
                String type = parts[parts.length - 1];
                ClassMeta meta = classes.get(type);
                sendJson(e, context.getItems((Class<Shape>) meta.type));
            } catch (Exception ex) {
                sendError(e, 500, ex);
            }
        });
        server.createContext("/api/create", e -> {
            try {
            	String path = e.getRequestURI().getPath();
            	String[] parts = path.split("/");
            	String typeName = parts[parts.length - 2];
            	int ctorId = Integer.parseInt(parts[parts.length - 1]);
            	ClassMeta meta = classes.get(typeName);
            	var args = parseBody(e);
                Object instance = meta.create(ctorId, args);
                context.add(instance);
                sendJson(e, instance);
            } catch (Exception ex) {
                sendError(e, 500, ex);
            }
        });
        server.createContext("/api/call", e -> {
            try {
                String path = e.getRequestURI().getPath();
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 2]);
                int methodId = Integer.parseInt(parts[parts.length - 1]);
                Shape item = findShapeById(id);
                ClassMeta meta = classes.get(item.getClass().getSimpleName());
                var args = parseBody(e);
                Object result = meta.call(item, methodId, args);
                context.update(item);
                Map<String, Object> response = new HashMap<>();
                response.put("result", result);
                sendJson(e, response);
            } catch (Exception ex) {
                sendError(e, 500, ex);
            }
        });
        server.createContext("/api/prop", e -> {
            try {
            	String path = e.getRequestURI().getPath();
            	String[] parts = path.split("/");

            	int id = Integer.parseInt(parts[parts.length - 2]);
            	int propId = Integer.parseInt(parts[parts.length - 1]);

            	Shape item = findShapeById(id);
            	ClassMeta meta = classes.get(item.getClass().getSimpleName());

            	var args = parseBody(e);
            	Object value = args.get("value");

                meta.setProperty(item, propId, value);
                context.update(item);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                sendJson(e, response);
            } catch (Exception ex) {
                sendError(e, 500, ex);
            }
        });
        server.createContext("/api/delete", e -> {
        	try {
        		String[] parts = e.getRequestURI().getPath().split("/");
        		int id = Integer.parseInt(parts[parts.length - 1]);
        		Shape item = findShapeById(id);
        		context.delete(item);
        		Map<String, Object> response = new HashMap<>();
        		response.put("success", true);
        		response.put("deletedId", id);
        		response.put("type", item.getClass().getSimpleName());
        		sendJson(e, response);
        	} catch (Exception ex) {
        		sendError(e, 500, ex);
        	}
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started");
    }

    @SuppressWarnings("unchecked")
	private Shape findShapeById(int id) {
        for (ClassMeta meta : classes.values()) {
            Shape item = context.findById((Class<Shape>) meta.type, id);
            if (item != null) return item;
        }
        return null;
    }

    private Map<String, JsonElement> parseBody(HttpExchange exchange) throws IOException {
        try (Reader reader = new InputStreamReader(exchange.getRequestBody())) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                Map<String, JsonElement> map = new HashMap<>();
                element.getAsJsonObject().entrySet().forEach(e -> {
                    map.put(e.getKey(), e.getValue());
                });
                return map;
            }
            return new HashMap<>();
        }
    }

    private void sendJson(HttpExchange exchange, Object data) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        String json = new Gson().toJson(data);
        byte[] bytes = json.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int code, Exception e) throws IOException {
    	e.printStackTrace();
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        sendJson(exchange, error);
        exchange.close();
    }
    
}
