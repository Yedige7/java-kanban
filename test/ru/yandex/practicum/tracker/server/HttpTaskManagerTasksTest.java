package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
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

public class HttpTaskManagerTasksTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
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

        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getTitle(), "Test 1");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        Task taskFromManager = taskManager.getTaskById(1);
        taskFromManager.setDescription("Updated");
        String taskJson = gson.toJson(taskFromManager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getDescription(), "Updated");
    }

    @Test
    public void testOverlapingTask() throws IOException, InterruptedException {
        Task task = new Task("Test 3", "Testing task 3",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));


        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertEquals("Некорректное код ответа", 406, response2.statusCode());
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
    }


    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // Добавляем две задачи
        taskManager.addTask(new Task("Task 1", "Desc 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10)));
        taskManager.addTask(new Task("Task 2", "Desc 2",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(20)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?abc"); // не число
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
        URI url = URI.create("http://localhost:8080/tasks?" + nonExistentId);
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
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?1");
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(url) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .header("Accept", "application/xml") // указываем заголовок Accept
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", tasksFromManager.size(), 1);
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getTitle(), "Test 2");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        int nonExistentId = 1;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?" + nonExistentId);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Удален", response.body());
    }
}