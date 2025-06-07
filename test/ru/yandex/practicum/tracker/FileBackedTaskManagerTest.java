package ru.yandex.practicum.tracker;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private static File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
    }

    @Test
    void addNewTask() {

        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic("Epic 1", "Epic Description", Status.NEW);
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 6, 5, 7, 0), Duration.ofMinutes(30));
        taskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubtasks();

        assertEquals(1, tasks.size(), "Задач должно быть 1");
        assertEquals(1, epics.size(), "Эпиков должно быть 1");
        assertEquals(1, subtasks.size(), "Подзадач должно быть 1");
    }
}

