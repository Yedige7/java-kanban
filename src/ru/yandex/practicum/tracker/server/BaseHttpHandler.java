package ru.yandex.practicum.tracker.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, 200, text, "application/json;charset=utf-8");
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendResponse(h, 404, text, "text/plain; charset=UTF-8");
    }

    protected void sendOverlaping(HttpExchange h, String text) throws IOException {
        sendResponse(h, 406, text, "text/plain; charset=UTF-8");
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendResponse(h, 500, text, "text/plain; charset=UTF-8");
    }

    public static void sendError(HttpExchange h, String text) throws IOException {
        sendResponse(h, 400, text, "text/plain; charset=UTF-8");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String message, String contentType) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

}
