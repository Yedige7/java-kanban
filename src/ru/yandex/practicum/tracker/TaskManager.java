package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter ;

    public static int idCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        TaskManager.idCounter = idCounter;
    }

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    public TaskManager() {
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();

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

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public static int generateId() {
        idCounter++;
        return idCounter;
    }

    public void removeTasks(){
        tasks.clear();
    }

    public void removeEpics(){
        epics.clear();
    }

    public void removeSubtasks(){
        subtasks.clear();
    }
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpics(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
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
        subtasks.remove(id);
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

    public void changeTaskStatus(int id, Status status) {
       Task task = getTaskById(id);
       task.setStatus(status);
       updateTask(task);
    }

    public void changeSubTaskStatus(int id, Status status) {
        Subtask task = getSubtaskById(id);
        task.setStatus(status);
        updateSubtask(task);
        changeEpicStatus(task.getEpicId());
    }

    private void changeEpicStatus(int id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> subtaskList = getEpicsSubtasks(epic);
        Status status = epic.getStatus();
        Status newStatus = status;
        if(!subtaskList.isEmpty()) {
            int size = subtaskList.size();
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
            updateEpic(epic);
        }
    }



}
