package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerTest {
    private TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();
    private static final URI baseUri = URI.create("http://localhost:8080/prioritized");
    private HttpClient client;

    public PrioritizedHandlerTest() throws IOException {
        taskServer = new HttpTaskServer(taskManager);
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
        HttpTaskServer.start(taskManager);
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() throws IOException {
        HttpTaskServer.stop();
    }

    @Test
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task t1 = new Task("High", "desc", Status.NEW,
                LocalDateTime.of(2025, 6, 14, 12, 0), Duration.ofMinutes(60));
        Task t2 = new Task("Low", "desc", Status.NEW,
                LocalDateTime.of(2025, 6, 14, 14, 0), Duration.ofMinutes(30));
        taskManager.addTask(t1);
        taskManager.addTask(t2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
        assertTrue(tasks[0].getStartTime().isBefore(tasks[1].getStartTime()), "Сортировка по приоритету нарушена");
    }

    @Test
    void shouldReturnBadRequestIfQueryParamExists() throws IOException, InterruptedException {
        URI uri = URI.create(baseUri + "?1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("не принимает параметры запроса"));
    }

    @Test
    void shouldReturnErrorOnUnsupportedMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertTrue(response.body().contains("HTTP-метод не разрешен для данного ресурса."));
    }

}
