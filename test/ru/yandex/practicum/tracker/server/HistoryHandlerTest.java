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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest {

    private TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();
    private static final URI baseUri = URI.create("http://localhost:8080/history");
    private HttpClient client;

    public HistoryHandlerTest() throws IOException {
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
    void shouldReturnHistoryOnGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "desc", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri)
                .GET()
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> history = List.of(gson.fromJson(response.body(), Task[].class));
        assertEquals(1, history.size());
        assertEquals("Test Epic", history.get(0).getTitle());
    }

    @Test
    void shouldReturnBadRequestOnQueryParam() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + "?1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("История не принимает параметры"));
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
