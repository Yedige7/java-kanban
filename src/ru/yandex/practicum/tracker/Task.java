package ru.yandex.practicum.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String title;
    private String description;

    private int id;

    private Status status;

    protected Duration duration;

    protected LocalDateTime startTime;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration =  Duration.ofMinutes(10);
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        if (startTime != null) {
            this.startTime = startTime;
        } else {
            this.startTime = LocalDateTime.now();
        }
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
       this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Task {" +
                " Title = '" + title + '\'' +
                ", description = '" + description + '\'' +
                ", id = " + id +
                ", status = " + status +
                ", startTime = " + startTime.format(formatter) +
                ", duration = " + duration.toMinutes() + " мин" +
                ", endTime = " + getEndTime().format(formatter) +
                '}';
    }
}
