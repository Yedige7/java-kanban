package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void chekTask(){
        TaskManager manager = new InMemoryTaskManager();
        Task taskTest = new Task("Test", "Test description", manager.generateId(), Status.NEW);
        Task task = new Task("Test2", "Test description 2",taskTest.getId(), Status.NEW);
        assertEquals(taskTest, task, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }
}
