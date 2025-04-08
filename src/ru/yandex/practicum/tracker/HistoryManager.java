package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager<T extends Task> {
    void add(T task);
    List<T> getHistory();
}
