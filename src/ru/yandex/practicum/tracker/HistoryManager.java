package ru.yandex.practicum.tracker;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    List<Task> getHistory();
}
