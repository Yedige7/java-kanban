package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void beforeEach() {
        managers = new Managers();
        taskManager = managers.getDefault();
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
    }

    @Test
    void shouldNotAddTaskWithIntersection() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 10, 15), Duration.ofMinutes(30));
        taskManager.addTask(task1);
        RuntimeException e = assertThrows(RuntimeException.class, () -> taskManager.addTask(task2));
        assertTrue(e.getMessage().contains("пересекается"));
    }

    @Test
    void shouldAddTaskWithoutIntersection() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 11, 0), Duration.ofMinutes(30));
        taskManager.addTask(task1);
        assertDoesNotThrow(() -> taskManager.addTask(task2));
    }

    @Test
    void shouldReturnNewStatusIfAllSubtasksAreNew() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),  LocalDateTime.of(2025, 6, 5, 7, 0), Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),  LocalDateTime.of(2025, 6, 5, 8, 0), Duration.ofMinutes(30)));
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnDoneStatusIfAllSubtasksAreDone() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Sub1", "Desc", Status.DONE, epic.getId(),  LocalDateTime.of(2025, 6, 5, 7, 0), Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),  LocalDateTime.of(2025, 6, 5, 8, 0), Duration.ofMinutes(30)));
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnInProgressIfMixedStatus() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Sub1", "Desc", Status.NEW, epic.getId()));
        taskManager.addSubtask(new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),  LocalDateTime.of(2025, 6, 5, 6, 0), Duration.ofMinutes(30)));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnInProgressIfAllInProgress() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Sub1", "Desc", Status.IN_PROGRESS, epic.getId()));
        taskManager.addSubtask(new Subtask("Sub2", "Desc", Status.IN_PROGRESS, epic.getId(),  LocalDateTime.of(2025, 6, 5, 6, 0), Duration.ofMinutes(30)));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

}
