package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tracker.Epic;

import ru.yandex.practicum.tracker.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) throws IOException {
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
            sendHasInteractions(httpExchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null) {
                sendText(exchange, gson.toJson(manager.getEpics()));
            } else {
                int id;
                try {
                    id = Integer.parseInt(query);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный параметр запроса: " + query);
                    return;
                }
                Epic epic = manager.getEpicById(id);
                if (epic != null) {
                    sendText(exchange, gson.toJson(epic));
                } else {
                    sendNotFound(exchange, "Не найдено");
                }

            }
        } catch (IOException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() == 0 || manager.getEpicById(epic.getId()) == null) {
                if (manager.isTaskOverlapping(epic)) {
                    sendOverlaping(exchange, "Время выполнения задачи совпадает с существующей задачей.");
                    return;
                }
                manager.addEpic(epic);
            } else {
                manager.updateEpic(epic);
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
            Epic task = manager.getEpicById(id);
            if (task == null) {
                sendNotFound(exchange, "Не найдено");
                return;
            }
            manager.removeEpicByid(id);
            sendText(exchange, "Удален");
        } catch (IOException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }
}
