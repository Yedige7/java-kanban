package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {
    private InMemoryTaskManager taskManager ;
    private HistoryManager historyManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        Task taskTest = new Task("Test", "Test description", taskManager.generateId(), Status.NEW);

        historyManager.add(taskTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertNotEquals(0, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void checkParametrs() {
        Task taskTest = new Task("Test", "Test description", taskManager.generateId(), Status.NEW);
        historyManager.add(taskTest);
        taskTest.setStatus(Status.IN_PROGRESS);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        Task task = history.get(0);
        assertNotEquals(taskTest.getStatus(), task.getStatus(), "После добавления задачи, статусы должна быть разными.");
    }
    @Test
    void checkDiferentTypeofTask() {
        Task taskTest = new Task("Test", "Test description", taskManager.generateId(), Status.NEW);

        historyManager.add(taskTest);
        Epic epicTest = new Epic("Epic", "Epic description", taskManager.generateId(), Status.NEW);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", taskManager.generateId(), Status.NEW, epicTest.getId());
        historyManager.add(epicTest);
        historyManager.add(subtaskTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(3, history.size(), "После добавления задачи, в история не должна быть 3.");
    }

    @Test
    void countTasksInHistory() {
        Task taskTest = new Task("Test", "Test description", taskManager.generateId(), Status.NEW);
        for (int i = 0 ; i < 10; i++ ) {
            historyManager.add(taskTest);
        }
        Epic epicTest = new Epic("Epic", "Epic description", taskManager.generateId(), Status.NEW);
        historyManager.add(epicTest);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(10, history.size(), "После добавления задачи, в история не должна быть 3.");
    }




}
