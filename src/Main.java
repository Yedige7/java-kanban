import ru.yandex.practicum.tracker.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task taskTest = new Task("Test", "Test description", TaskManager.generateId(), Status.NEW);
        Task taskTest2 = new Task("Test2", "Test description 2", TaskManager.generateId(), Status.NEW);

        taskManager.addTask(taskTest);
        taskManager.addTask(taskTest2);

        Epic epicTest = new Epic("Epic", "Epic description", TaskManager.generateId(), Status.NEW);

        Epic epicTest2 = new Epic("Epic2", "Epic description2", TaskManager.generateId(), Status.NEW);

        System.out.println("epicTest" + epicTest);
        System.out.println("epicTest 2" + epicTest2);

        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", TaskManager.generateId(), Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask2", "Subtask description2", TaskManager.generateId(), Status.NEW, epicTest.getId());
        Subtask subtaskTest3 = new Subtask("Subtask3", "Subtask description3", TaskManager.generateId(), Status.NEW, epicTest2.getId());

        taskManager.addEpics(epicTest);
        taskManager.addEpics(epicTest2);

        taskManager.addSubtask(subtaskTest);
        taskManager.addSubtask(subtaskTest2);
        taskManager.addSubtask(subtaskTest3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("-----------");

        taskManager.changeTaskStatus(1, Status.IN_PROGRESS);
        taskManager.changeTaskStatus(2, Status.DONE);
        taskManager.changeSubTaskStatus(7,Status.DONE);
        taskManager.changeSubTaskStatus(6,Status.IN_PROGRESS);
        taskManager.changeSubTaskStatus(5,Status.DONE);
        System.out.println("-----------");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("-----------");
        taskManager.changeSubTaskStatus(6,Status.DONE);

        System.out.println("-----------");
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.removeTaskByid(1);
        taskManager.removeEpicByid(4);

        System.out.println("-----------");

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());



    }
}
