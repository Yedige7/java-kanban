package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void chekTask() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        taskManager.addEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());

        taskManager.addSubtask(subtaskTest);
        Subtask subtaskTest2 = taskManager.getSubtaskById(subtaskTest.getId());
        assertEquals(subtaskTest, subtaskTest2, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }


    @Test
    void checkSubtaskByItself() {
        Subtask subtaskTest = null;
        try {
            subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, 1);
            subtaskTest.setId(1);
        } catch (Exception ex) {
            System.out.println("Id could not be equal as Epic ID");
        }
        assertNotEquals(subtaskTest.getId(), subtaskTest.getEpicId(), "Id could not be equal as Epic ID");
    }
}
