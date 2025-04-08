package ru.yandex.practicum.tracker;

public class Managers {
    private  InMemoryTaskManager inMemoryTaskManager;

    private static  InMemoryHistoryManager inMemoryHistoryManager;

    public Managers(){
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }
    TaskManager getDefault(){
        return inMemoryTaskManager;
    }

    static InMemoryHistoryManager getDefaultHistory(){
        return  inMemoryHistoryManager;
    }
}
