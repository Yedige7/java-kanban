package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskList;


    public InMemoryHistoryManager() {
        this.taskList = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Task copiedTask = null;
        if (task instanceof Subtask) {
            Subtask original = (Subtask) task;
            copiedTask = new Subtask(
                    original.getTitle(),
                    original.getDescription(),
                    original.getId(),
                    original.getStatus(),
                    original.getEpicId()
            );
        } else if (task instanceof Epic) {
            Epic original = (Epic) task;
            Epic copy = new Epic(
                    original.getTitle(),
                    original.getDescription(),
                    original.getId(),
                    original.getStatus()
            );
           List<Integer> integers = original.getSubTasks();
            for (Integer id :integers) {
                copy.addSubtask(id);
            }
            copiedTask = copy;
        } else {
            Task original = task;
            copiedTask = new Task(
                    original.getTitle(),
                    original.getDescription(),
                    original.getId(),
                    original.getStatus()
            );
        }

        if (taskList.size() < 10) {
            taskList.add(copiedTask);
        } else {
            taskList.remove(0);
            taskList.add(copiedTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(taskList);
    }
}
