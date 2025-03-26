package ru.yandex.practicum.tracker;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasks;
    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        subTasks = new ArrayList<>();
    }

    public void addSubtask(int id){
        subTasks.add(id);
    }

    @Override
    public String toString() {
        return "Subtask {" +
                " Title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus()+
                ", subTasksList = " + subTasks +
                '}';
    }
}
