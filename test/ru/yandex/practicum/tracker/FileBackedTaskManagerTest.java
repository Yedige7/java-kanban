package ru.yandex.practicum.tracker;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {


    private static File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
    }

    @Test
    void addNewTask() {
        manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic("Epic 1", "Epic Description", Status.NEW);
        manager.addTask(task);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubtasks();


        assertEquals(1, tasks.size(), "Задач должно быть 1");
        assertEquals(1, epics.size(), "Эпиков должно быть 1");
        assertEquals(1, subtasks.size(), "Подзадач должно быть 1");
    }
}

