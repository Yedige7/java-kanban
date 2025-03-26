package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter ;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    public TaskManager() {
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        idCounter = 0;
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask){

        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public  int generateId() {
        idCounter++;
        return idCounter;
    }

    public void removeTasks(){
        tasks.clear();
    }

    public void removeEpics(){
        epics.clear();
        removeSubtasks();

    }

    public void removeSubtasks(){
        ArrayList<Integer> epicsList = new ArrayList<>();
        for (Subtask subtask: subtasks.values()) {
            epicsList.add(subtask.getEpicId());
        }
        subtasks.clear();
        for (int id: epicsList) {
            changeEpicStatus(id);
        }
    }
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpics(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        changeEpicStatus(epic.getId());
    }

    public Task getTaskById(int id){
        Task task = tasks.get(id);
        return task;
    }

    public Epic getEpicById(int id){
        Epic epic = epics.get(id);
        return epic;
    }

    public Subtask getSubtaskById(int id){
        Subtask subtask = subtasks.get(id);
        return subtask;
    }

    public void removeTaskByid(int id){
        tasks.remove(id);
    }

    public void removeEpicByid(int id){
        ArrayList<Subtask> arrayList = getEpicsSubtasks(getEpicById(id));
        epics.remove(id);
        for (Subtask subtask: arrayList) {
            removeSubtaskByid(subtask.getId());
        }
    }

    public void removeSubtaskByid(int id){
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicById(subtask.getEpicId());
        subtasks.remove(id);
        if( epic != null) {
            changeEpicStatus(epic.getId());
        }
    }

    public ArrayList<Subtask> getEpicsSubtasks(Epic epic){
        int id = epic.getId();
        ArrayList<Subtask> subtaskList =new ArrayList<>();
        for(Subtask subtask : subtasks.values()){
            if(subtask.getEpicId() == id){
                subtaskList.add(subtask);
            }
        }
        return subtaskList;
    }


    private void changeEpicStatus(int id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> subtaskList = getEpicsSubtasks(epic);
        Status status = epic.getStatus();
        Status newStatus = status;
        if(!subtaskList.isEmpty()) {
            int size = subtaskList.size();
            if (size == 0){
                epic.setStatus(Status.NEW);
                return;
            }
            int countNew = 0;
            int countDone = 0;
            for (Subtask subtask: subtaskList) {
                if(subtask.getStatus() == Status.NEW) {
                    countNew++;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
            }
            if(size == countNew) {
                newStatus = Status.NEW;
            } else if(size == countDone) {
                newStatus = Status.DONE;
            } else {
                newStatus = Status.IN_PROGRESS;
            }
        }
        if (newStatus != status) {
            epic.setStatus(newStatus);
        }
    }



}
