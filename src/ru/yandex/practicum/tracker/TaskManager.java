package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();


    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void addTask(Task task);

    void addEpics(Epic epic);

    void addSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void removeTaskByid(int id);

    void removeEpicByid(int id);

    void removeSubtaskByid(int id);

    List<Subtask> getEpicsSubtasks(Epic epic);

    List<Task> getHistory();
}
