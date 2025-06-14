package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.InMemoryTaskManager;
import ru.yandex.practicum.tracker.TaskManager;
import ru.yandex.practicum.tracker.util.DurationAdapter;
import ru.yandex.practicum.tracker.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static  TaskManager taskManager;
    private static final int PORT = 8080;
    private static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        taskManager =  new InMemoryTaskManager(new InMemoryHistoryManager());
        start(taskManager);
    }

    public HttpTaskServer(TaskManager taskManager) {
        HttpTaskServer.taskManager = taskManager;
    }

    public static void start(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stop() throws IOException {
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
        httpServer.stop(1);
    }

    public static Gson getGson() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }
}
