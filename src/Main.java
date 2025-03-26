import ru.yandex.practicum.tracker.*;

import static ru.yandex.practicum.tracker.Status.DONE;
import static ru.yandex.practicum.tracker.Status.IN_PROGRESS;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task taskTest = new Task("Test", "Test description", taskManager.generateId(), Status.NEW);
        Task taskTest2 = new Task("Test2", "Test description 2", taskManager.generateId(), Status.NEW);

        taskManager.addTask(taskTest);
        taskManager.addTask(taskTest2);

        Epic epicTest = new Epic("Epic", "Epic description", taskManager.generateId(), Status.NEW);

        Epic epicTest2 = new Epic("Epic2", "Epic description2", taskManager.generateId(), Status.NEW);

        System.out.println("epicTest" + epicTest);
        System.out.println("epicTest 2" + epicTest2);

        Subtask subtaskTest = new Subtask("Subtask", "Subtask description", taskManager.generateId(), Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask2", "Subtask description2", taskManager.generateId(), Status.NEW, epicTest.getId());
        Subtask subtaskTest3 = new Subtask("Subtask3", "Subtask description3", taskManager.generateId(), Status.NEW, epicTest2.getId());

        taskManager.addEpics(epicTest);
        taskManager.addEpics(epicTest2);

        taskManager.addSubtask(subtaskTest);
        taskManager.addSubtask(subtaskTest2);
        taskManager.addSubtask(subtaskTest3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("-----------");

        taskTest.setStatus(IN_PROGRESS);
        taskTest2.setStatus(DONE);
        subtaskTest3.setStatus(DONE);
        subtaskTest2.setStatus(IN_PROGRESS);
        subtaskTest.setStatus(DONE);

        System.out.println("-----------");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("-----------");
        subtaskTest2.setStatus(DONE);

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
