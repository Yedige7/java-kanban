package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubtaskTest {

    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void chekTask(){
        TaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Epic", "Epic description", manager.generateId(), Status.NEW);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", manager.generateId(), Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask", "Subtask description", subtaskTest.getId(), Status.NEW, epicTest.getId());

        assertEquals(subtaskTest, subtaskTest2, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }

    // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    void checkSubtaskByItself(){
        Subtask subtaskTest = null;
        try {
            subtaskTest = new Subtask("Subtask", "Subtask description", 1, Status.NEW, 1);
        } catch (Exception ex) {
            assertNull(subtaskTest, "Not  null subtask");
        }
        assertNull(subtaskTest, "Not  null subtask");
    }
}
