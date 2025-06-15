package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tracker.TaskManager;

import java.io.IOException;
import java.net.URI;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) throws IOException {
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
                default:
                    sendHasInteractions(httpExchange, "HTTP-метод не разрешен для данного ресурса.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendHasInteractions(httpExchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null || query.isEmpty()) {
                sendText(exchange, gson.toJson(manager.getHistory()));
            } else {
                sendError(exchange, "История не принимает параметры запроса.");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            sendHasInteractions(exchange, e.getMessage());
        }
    }

}
