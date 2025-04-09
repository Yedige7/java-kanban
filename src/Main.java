import ru.yandex.practicum.tracker.*;

import static ru.yandex.practicum.tracker.Status.DONE;
import static ru.yandex.practicum.tracker.Status.IN_PROGRESS;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task taskTest = new Task("Test", "Test description", Status.NEW);

        System.out.println("taskTest" + taskTest);
        taskManager.addTask(taskTest);


        System.out.println("taskTest 2 " + taskTest);


    }
}
