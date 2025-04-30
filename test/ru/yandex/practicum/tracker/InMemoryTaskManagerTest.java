package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {


    private static Managers managers;
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        managers = new Managers();
        taskManager = managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        final int taskId = taskTest.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskTest, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskTest, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addDifferentTask() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        managers.getDefault().addTask(taskTest);
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        managers.getDefault().addEpics(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        managers.getDefault().addSubtask(subtaskTest);
        assertNotNull(managers.getDefault().getTaskById(taskTest.getId()), "Задачи не возвращаются.");
        assertNotNull(managers.getDefault().getEpicById(epicTest.getId()), "Задачи не возвращаются.");
        assertNotNull(managers.getDefault().getSubtaskById(subtaskTest.getId()), "Задачи не возвращаются.");

        assertEquals(taskTest, managers.getDefault().getTaskById(taskTest.getId()), "Задачи не совпадают.");
        assertEquals(epicTest, managers.getDefault().getEpicById(epicTest.getId()), "Задачи не совпадают.");
        assertEquals(subtaskTest, managers.getDefault().getSubtaskById(subtaskTest.getId()), "Задачи не совпадают.");
    }

    @Test
    void checkDifferentParametrs() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        managers.getDefault().addTask(taskTest);
        assertEquals(taskTest.getId(), managers.getDefault().getTaskById(taskTest.getId()).getId(), "Задачи не совпадают.");
        assertEquals(taskTest.getDescription(), managers.getDefault().getTaskById(taskTest.getId()).getDescription(), "Задачи не совпадают.");
        assertEquals(taskTest.getTitle(), managers.getDefault().getTaskById(taskTest.getId()).getTitle(), "Задачи не совпадают.");
        assertEquals(taskTest.getStatus(), managers.getDefault().getTaskById(taskTest.getId()).getStatus(), "Задачи не совпадают.");

    }
}
