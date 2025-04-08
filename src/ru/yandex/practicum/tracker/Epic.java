package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasks;
    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        subTasks = new ArrayList<>();
    }

    public void addSubtask(int id){
        if(id == this.getId()){
            System.out.println("Нельзя добавлять свой id");

        } else {

            subTasks.add(id);
        }
    }
    public List<Integer> getSubTasks(){

        return subTasks;
    }

    @Override
    public String toString() {
        return "Epic {" +
                " Title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus()+
                ", subTasksList = " + subTasks +
                '}';
    }
}
