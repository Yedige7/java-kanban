package ru.yandex.practicum.tracker;

import ru.yandex.practicum.tracker.exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,title,status,description,epic,start,duration,end");
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(taskToString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        if (task instanceof Epic) {
            sb.append(Type.EPIC);
        } else if (task instanceof Subtask) {
            sb.append(Type.SUBTASK);
        } else {
            sb.append(Type.TASK);
        }
        sb.append(",").append(task.getTitle())
                .append(",").append(task.getStatus())
                .append(",").append(task.getDescription());

        if (task instanceof Subtask) {
            sb.append(",").append(((Subtask) task).getEpicId()).append(",");
        } else {
            sb.append(",").append(" ").append(",");
        }
        sb.append(task.getStartTime() != null ? task.getStartTime() : "").append(",");
        sb.append(task.getDuration() != null ? task.getDuration().toMinutes() : "").append(",");
        sb.append(task instanceof Epic && ((Epic) task).getEndTime() != null ? ((Epic) task).getEndTime() : ",");
        return sb.toString();
    }

    private Task fromString(String value) throws ManagerSaveException {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        Type type = Type.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String start = fields[6].trim();

        LocalDateTime startTime = null;
        if (start != null && !start.isEmpty()) {
            startTime = LocalDateTime.parse(start);
        }
        String durationStr = fields[7];
        long minutes = Long.parseLong(durationStr);
        Duration duration = Duration.ofMinutes(minutes);
        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            case EPIC:
                String end = fields[8].trim();
                LocalDateTime endTime = null;
                if (end != null && !end.isEmpty()) {
                    endTime = LocalDateTime.parse(end);
                }
                Epic epic = new Epic(name, description, status, startTime, duration, endTime);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, status, epicId, startTime, duration);
                subtask.setId(id);
                return subtask;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean isFirst = true;
            for (String line : lines) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                Task task = manager.fromString(line);
                int id = task.getId();
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }

                if (id > manager.getIdCounter()) {
                    manager.setIdCounter(id);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + e.getMessage());
        }

        return manager;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();

    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();

    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeTaskByid(int id) {
        super.removeTaskByid(id);
        save();
    }

    @Override
    public void removeEpicByid(int id) {
        super.removeEpicByid(id);
        save();
    }

    @Override
    public void removeSubtaskByid(int id) {
        super.removeSubtaskByid(id);
        save();
    }

}
