package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
    }

    @Test
    void add() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        historyManager.add(taskTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertNotEquals(0, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void checkParametrs() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        historyManager.add(taskTest);
        taskTest.setStatus(Status.IN_PROGRESS);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        Task task = history.get(0);
        assertNotEquals(taskTest.getStatus(), task.getStatus(), "После добавления задачи, статусы должна быть разными.");
    }

    @Test
    void checkDiferentTypeofTask() {
        Task taskTest = new Task("Test", "Test description", Status.NEW, LocalDateTime.of(2025, 6, 5, 10, 0), Duration.ofMinutes(30));
        taskManager.addTask(taskTest);
        historyManager.add(taskTest);
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        taskManager.addSubtask(subtaskTest);
        historyManager.add(epicTest);
        historyManager.add(subtaskTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(3, history.size(), "После добавления задачи, в история не должна быть 3.");
    }

    @Test
    void countTasksInHistory() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        for (int i = 0; i < 10; i++) {
            historyManager.add(taskTest);
        }
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        historyManager.add(epicTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(2, history.size(), "После добавления задачи, в история н должно быть 2.");
    }

    @Test
    void shouldRemoveFromBeginning() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        taskManager.addTask(t1);
        historyManager.add(t1);
        historyManager.remove(t1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveFromMiddle() {
        Task t1 = new Task("T1", "desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 7, 0), Duration.ofMinutes(30));
        Task t2 = new Task("T2", "desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 8, 0), Duration.ofMinutes(30));
        Task t3 = new Task("T3", "desc", Status.NEW,
                LocalDateTime.of(2025, 6, 5, 9, 0), Duration.ofMinutes(30));
        taskManager.addTask(t1);
        taskManager.addTask(t2);
        taskManager.addTask(t3);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);
        historyManager.remove(t2.getId());
        assertEquals(List.of(t1, t3), historyManager.getHistory());
    }

    @Test
    void shouldNotAddDuplicatesToHistory() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        taskManager.addTask(t1);
        historyManager.add(t1);
        historyManager.add(t1);
        historyManager.add(t1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldReturnEmptyHistoryIfNothingAdded() {
        assertTrue(historyManager.getHistory().isEmpty());
    }
}
