package ru.yandex.practicum.tracker;

public class Managers {
    private InMemoryTaskManager inMemoryTaskManager;

    public Managers() {
        inMemoryTaskManager = new InMemoryTaskManager(getDefaultHistory());
    }

    public TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
