package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter;
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private HistoryManager historyManager;

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        this.historyManager = historyManager;
        idCounter = 0;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        changeEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    private int generateId() {
        idCounter++;
        return idCounter;
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.clear();
        removeSubtasks();

    }

    @Override
    public void removeSubtasks() {
        List<Integer> epicsList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            epicsList.add(subtask.getEpicId());
        }
        subtasks.clear();
        for (int id : epicsList) {
            changeEpicStatus(id);
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        changeEpicStatus(epic.getId());
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public void removeTaskByid(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicByid(int id) {
        List<Subtask> arrayList = getEpicsSubtasks(getEpicById(id));
        epics.remove(id);
        for (Subtask subtask : arrayList) {
            removeSubtaskByid(subtask.getId());
        }
    }

    @Override
    public void removeSubtaskByid(int id) {
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicById(subtask.getEpicId());
        subtasks.remove(id);
        if (epic != null) {
            changeEpicStatus(epic.getId());
        }
    }

    @Override
    public List<Subtask> getEpicsSubtasks(Epic epic) {
        int id = epic.getId();
        List<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtaskList.add(subtask);
            }
        }
        return subtaskList;
    }


    private void changeEpicStatus(int id) {
        Epic epic = getEpicById(id);
        List<Subtask> subtaskList = getEpicsSubtasks(epic);
        Status status = epic.getStatus();
        Status newStatus = status;
        if (!subtaskList.isEmpty()) {
            int size = subtaskList.size();
            if (size == 0) {
                epic.setStatus(Status.NEW);
                return;
            }
            int countNew = 0;
            int countDone = 0;
            for (Subtask subtask : subtaskList) {
                if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
            }
            if (size == countNew) {
                newStatus = Status.NEW;
            } else if (size == countDone) {
                newStatus = Status.DONE;
            } else {
                newStatus = Status.IN_PROGRESS;
            }
        }
        if (newStatus != status) {
            epic.setStatus(newStatus);
        }
    }


    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicsSubtasks((Epic) epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
