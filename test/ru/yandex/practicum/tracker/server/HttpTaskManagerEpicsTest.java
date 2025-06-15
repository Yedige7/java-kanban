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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
        taskServer = new HttpTaskServer(taskManager);
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
        HttpTaskServer.start(taskManager);
    }

    @AfterEach
    public void shutDown() throws IOException {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {

        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        String taskJson = gson.toJson(epicTest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Epic", tasksFromManager.get(0).getTitle());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);

        Epic taskFromManager = taskManager.getEpicById(epicTest.getId());
        taskFromManager.setDescription("Updated");
        String taskJson = gson.toJson(taskFromManager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Updated", tasksFromManager.get(0).getDescription());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // Добавляем две задачи
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        Epic epicTest2 = new Epic("Epic2", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        taskManager.addEpic(epicTest2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] tasks = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?abc"); // не число
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        int nonExistentId = 999;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?" + nonExistentId);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Не найдено", response.body());
    }

    @Test
    public void testGetExistentTask() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
        assertEquals("Некорректное имя задачи", "Epic", tasksFromManager.get(0).getTitle());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);


        int id = epicTest.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Удален", response.body());
    }
}