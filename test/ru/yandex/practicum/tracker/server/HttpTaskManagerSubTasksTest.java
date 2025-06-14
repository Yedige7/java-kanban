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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubTasksTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubTasksTest() throws IOException {
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
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        String taskJson = gson.toJson(subtaskTest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Subtask", tasksFromManager.get(0).getTitle());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        taskManager.addSubtask(subtaskTest);

        Subtask taskFromManager = taskManager.getSubtaskById(2);
        taskFromManager.setDescription("Updated");
        String taskJson = gson.toJson(taskFromManager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Updated", tasksFromManager.get(0).getDescription());
    }

    @Test
    public void testOverlapingTask() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        String taskJson = gson.toJson(subtaskTest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertEquals("Некорректное код ответа", 406, response2.statusCode());
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
    }


    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask2", "Subtask description", Status.NEW, epicTest.getId(),
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(20));

        taskManager.addSubtask(subtaskTest);
        taskManager.addSubtask(subtaskTest2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?abc"); // не число
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
        URI url = URI.create("http://localhost:8080/subtasks?" + nonExistentId);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Task not found", response.body());
    }

    @Test
    public void testGetExistentTask() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        taskManager.addSubtask(subtaskTest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
        assertEquals("Некорректное имя задачи", "Subtask", tasksFromManager.get(0).getTitle());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        taskManager.addSubtask(subtaskTest);

        int id = subtaskTest.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("DELETE", response.body());
    }
}