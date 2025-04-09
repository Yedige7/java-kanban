package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void chekTask(){
        TaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Epic", "Epic description", Status.NEW);
        manager.addEpics(epicTest);
        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask", "Subtask description", Status.NEW, epicTest.getId());
        manager.addSubtask(subtaskTest);
        subtaskTest2.setId(subtaskTest.getId());
        assertEquals(subtaskTest, subtaskTest2, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }

    // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    void checkSubtaskByItself(){
        Subtask subtaskTest = null;
        try {
            subtaskTest = new Subtask("Subtask", "Subtask description",  Status.NEW, 1);
            subtaskTest.setId(1);
        } catch (Exception ex) {
            System.out.println("Id could not be equal as Epic ID");
        }
        assertNotEquals(subtaskTest.getId(), subtaskTest.getEpicId(), "Id could not be equal as Epic ID");
    }
}
