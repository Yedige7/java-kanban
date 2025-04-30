package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    private static  Managers managers = new Managers();
    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void chekTask(){
        TaskManager manager = managers.getDefault();
        Task taskTest = new Task("Test", "Test description", Status.NEW);
        manager.addTask(taskTest);
        Task task = new Task("Test2", "Test description 2", Status.NEW);
        task.setId(taskTest.getId());
        assertEquals(taskTest, task, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }
}
