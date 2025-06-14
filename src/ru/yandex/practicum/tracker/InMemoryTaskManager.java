package ru.yandex.practicum.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;

    protected int getIdCounter() {
        return idCounter;
    }

    protected void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        this.historyManager = historyManager;
        idCounter = 0;
        prioritizedTasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task, Task otherTask) {
                if (task.getStartTime() == null) return -1;
                if (otherTask.getStartTime() == null) return 1;
                return task.getStartTime().compareTo(otherTask.getStartTime());
            }
        });
    }

    private boolean isTimeOverlap(Task a, Task b) {
        return !(a.getEndTime().isBefore(b.getStartTime()) || a.getStartTime().isAfter(b.getEndTime()));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getId())); // удалить старую
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другими");
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        prioritizedTasks.remove(subtasks.get(subtask.getId())); // удалить старую
        if (isTaskOverlapping(subtask)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другими");
        }
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        changeEpicStatus(subtask.getEpicId());
        changeEpicTime(subtask.getEpicId());
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
        List<Task> taskList = getTasks();
        taskList.stream().map(task -> prioritizedTasks.remove(task));
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
        List<Subtask> subtaskList = getSubtasks();
        subtaskList.stream().map(task -> prioritizedTasks.remove(task));
        subtasks.clear();
        for (int id : epicsList) {
            changeEpicStatus(id);
            changeEpicTime(id);
        }
    }

    public boolean isTaskOverlapping(Task task) {
        return getPrioritizedTasks().stream()
                .filter(other -> other.getStartTime() != null && other.getEndTime() != null)
                .anyMatch(other -> isTimeOverlap(task, other));
    }

    @Override
    public void addTask(Task task) {
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Задача пересекается с другими");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTaskOverlapping(subtask)) {
            throw new IllegalArgumentException("Обновлённая подзадача пересекается с другими");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        Epic epic = getEpicById(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        changeEpicStatus(epic.getId());
        changeEpicTime(subtask.getEpicId());
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
        prioritizedTasks.remove(tasks.get(id));
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
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        if (epic != null) {
            changeEpicStatus(epic.getId());
            changeEpicTime(epic.getId());
        }
    }

    @Override
    public List<Subtask> getEpicsSubtasks(Epic epic) {
        int id = epic.getId();
        return subtasks.values().stream().filter(sub -> sub.getEpicId() == id).collect(Collectors.toList());
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

    private void changeEpicTime(int id) {
        Epic epic = getEpicById(id);
        List<Subtask> subtaskList = getEpicsSubtasks(epic);
        Duration totalDuration = Duration.ZERO;
        if (!subtaskList.isEmpty()) {
            int size = subtaskList.size();
            if (size == 0) {
                epic.setStartTime(null);
                epic.setEndTime(null);
                epic.setDuration(Duration.ZERO);
                return;
            }
            LocalDateTime earliestStart = subtaskList.stream()
                    .filter(sub -> sub.getStartTime() != null && sub.getDuration() != null)
                    .map(Subtask::getStartTime).min(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDateTime latestEnd = subtaskList.stream()
                    .filter(sub -> sub.getStartTime() != null && sub.getDuration() != null)
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo).orElse(null);
            if (earliestStart != null && latestEnd != null) {
                totalDuration = Duration.between(earliestStart, latestEnd);
            }
            if (earliestStart != null && latestEnd != null) {
                epic.setStartTime(earliestStart);
                epic.setEndTime(latestEnd);
                epic.setDuration(totalDuration);
            } else {
                epic.setStartTime(null);
                epic.setEndTime(null);
                epic.setDuration(Duration.ZERO);
            }
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
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
