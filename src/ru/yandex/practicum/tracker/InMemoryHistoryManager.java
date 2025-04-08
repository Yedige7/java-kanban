package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    private final List<T> taskList;
    private int oldest;

    public InMemoryHistoryManager() {
        this.taskList = new ArrayList<>();
        this.oldest = 0;
    }

    @Override
    public void add(T task) {
        if (task == null) {
            return;
        }

        T copiedTask = null;

        if (task instanceof Subtask) {
            Subtask original = (Subtask) task;
            copiedTask = (T) new Subtask(
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
           ArrayList<Integer> integers = original.getSubTasks();
            for (Integer id :integers) {
                copy.addSubtask(id);
            }

            copiedTask = (T) copy;
        } else {
            Task original = task;
            copiedTask = (T) new Task(
                    original.getTitle(),
                    original.getDescription(),
                    original.getId(),
                    original.getStatus()
            );
        }

        if (taskList.size() < 10) {
            taskList.add(copiedTask);
        } else {
            taskList.set(oldest, copiedTask);
            oldest++;
            if(oldest >= 9) {
                oldest = 0;
            }
        }
    }

    @Override
    public List<T> getHistory() {
        return new ArrayList<>(taskList); // возвращаем копию
    }
}
