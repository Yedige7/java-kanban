package ru.yandex.practicum.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasks;
    private LocalDateTime endTime;


    public Epic(String title, String description, Status status) {
        super(title, description, status, null,null);
        subTasks = new ArrayList<>();
    }

    public Epic(String title, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(title, description, status, startTime, duration);
        this.endTime = endTime;
        this.subTasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }



    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }



    public void addSubtask(int id) {
        if (id == this.getId()) {
            System.out.println("Нельзя добавлять свой id");
        } else {
            subTasks.add(id);
        }
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String start = (startTime != null) ? startTime.format(formatter) : "null";
        String end = (getEndTime() != null) ? getEndTime().format(formatter) : "null";
        String durationStr = (getDuration() != null) ? duration.toMinutes() + " мин" : "null";
        return "Epic {" +
                " Title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus() +
                ", subTasksList = " + subTasks +
                ", startTime = " + start +
                ", duration = " + durationStr +
                ", endTime = " + end +
                '}';
    }
}
