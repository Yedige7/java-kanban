package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tracker.Subtask;
import ru.yandex.practicum.tracker.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) throws IOException {
        this.manager = manager;
        this.gson = gson;
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
                    sendHasInteractions(httpExchange, "HTTP-метод не разрешен для данного ресурса.");
            }
        } catch (Exception e) {
            sendHasInteractions(httpExchange, "Что-то пошло не так");
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null) {
                sendText(exchange, gson.toJson(manager.getSubtasks()));
            } else {
                int id;
                try {
                    id = Integer.parseInt(query);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный параметр запроса: " + query);
                    return;
                }
                Subtask subTask = manager.getSubtaskById(id);
                if (subTask != null) {
                    sendText(exchange, gson.toJson(subTask));
                } else {
                    sendNotFound(exchange, "Не найдено");
                }

            }
        } catch (IOException e) {
            sendHasInteractions(exchange, "Что-то пошло не так");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subTask = gson.fromJson(body, Subtask.class);

            if (subTask.getId() == 0 || manager.getSubtaskById(subTask.getId()) == null) {
                if (manager.isTaskOverlapping(subTask)) {
                    sendOverlaping(exchange, "Время выполнения задачи совпадает с существующей задачей.");
                    return;
                }
                manager.addSubtask(subTask);
            } else {
                manager.updateSubtask(subTask);
            }
            sendText(exchange, body);
        } catch (IOException e) {
            sendHasInteractions(exchange, "Что-то пошло не так");
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
            Subtask task = manager.getSubtaskById(id);
            if (task == null) {
                sendNotFound(exchange, "Не найдено");
                return;
            }
            manager.removeSubtaskByid(id);
            sendText(exchange, "Удален");
        } catch (IOException e) {
            sendHasInteractions(exchange, "Что-то пошло не так");
        }
    }
}
