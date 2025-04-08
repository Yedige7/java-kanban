package ru.yandex.practicum.tracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    //проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void chekTask(){
        TaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Epic", "Epic description", manager.generateId(), Status.NEW);
        Epic epicTest2 = new Epic("Epic2", "Epic description2", epicTest.getId(), Status.NEW);
        assertEquals(epicTest, epicTest2, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
    }

    @Test
    void chekEpicAddSubtask(){
        TaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Epic", "Epic description", manager.generateId(), Status.NEW);
        epicTest.addSubtask(epicTest.getId());
        ArrayList<Integer> list = epicTest.getSubTasks();
        boolean chekId = false;
        for (Integer id: list) {
           if(id ==  epicTest.getId()){
               chekId= true;
           }
        }
        assertFalse("Добавился ID епика", chekId);
    }
}
