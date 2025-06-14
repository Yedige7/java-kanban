package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tracker.Task;
import ru.yandex.practicum.tracker.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            URI uri = httpExchange.getRequestURI();
            String query = uri.getQuery();
            switch (method) {
                case "GET":
                    handleGet(httpExchange, query);
                    break;
                case "POST":
                    handlePost(httpExchange);
                    break;
                case "DELETE":
                    handleDelete(httpExchange, query);
                    break;
                default:
                    sendHasInteractions(httpExchange, "Not Allowed");
            }
        } catch (Exception e) {
            sendHasInteractions(httpExchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null) {
                sendText(exchange, gson.toJson(manager.getTasks()));
            } else {
                int id;
                try {
                    id = Integer.parseInt(query);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный параметр запроса: " + query);
                    return;
                }
                Task task = manager.getTaskById(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange, "Task not found");
                }

            }
        } catch (IOException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() == 0 || manager.getTaskById(task.getId()) == null) {
                if (manager.isTaskOverlapping(task)) {
                    sendOverlaping(exchange, "Task time overlaps with existing task.");
                    return;
                }
                manager.addTask(task);
            } else {
                manager.updateTask(task);
            }
            sendText(exchange, body);
        } catch (IOException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        try {
            int id;
            try {
                id = Integer.parseInt(query);
            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный параметр запроса: " + query);
                return;
            }
            Task task = manager.getTaskById(id);
            if (task == null) {
                sendNotFound(exchange, "Task not found");
                return;
            }

            manager.removeTaskByid(id);
            sendText(exchange, "DELETE");
        } catch (IOException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }
}
