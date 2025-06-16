package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Calendar.MAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected static Managers managers;
    protected TaskManager taskManager;


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
    void checkDifferentParametrs() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        assertEquals(taskTest.getId(), taskManager.getTaskById(taskTest.getId()).getId(), "Задачи не совпадают.");
        assertEquals(taskTest.getDescription(), taskManager.getTaskById(taskTest.getId()).getDescription(), "Задачи не совпадают.");
        assertEquals(taskTest.getTitle(), taskManager.getTaskById(taskTest.getId()).getTitle(), "Задачи не совпадают.");
        assertEquals(taskTest.getStatus(), taskManager.getTaskById(taskTest.getId()).getStatus(), "Задачи не совпадают.");

    }

    @Test
    void addDifferentTask() {
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        taskManager.addTask(taskTest);
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW, LocalDateTime.of(2025, MAY, 6, 20, 10), Duration.ofMinutes(10), LocalDateTime.of(2025, MAY, 6, 20, 20));
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId(), LocalDateTime.of(2025, MAY, 6, 21, 10), Duration.ofMinutes(10));
        taskManager.addSubtask(subtaskTest);
        assertNotNull(taskManager.getTaskById(taskTest.getId()), "Задачи не возвращаются.");
        assertNotNull(taskManager.getEpicById(epicTest.getId()), "Задачи не возвращаются.");
        assertNotNull(taskManager.getSubtaskById(subtaskTest.getId()), "Задачи не возвращаются.");

        assertEquals(taskTest, taskManager.getTaskById(taskTest.getId()), "Задачи не совпадают.");
        assertEquals(epicTest, taskManager.getEpicById(epicTest.getId()), "Задачи не совпадают.");
        assertEquals(subtaskTest, taskManager.getSubtaskById(subtaskTest.getId()), "Задачи не совпадают.");
    }


}
