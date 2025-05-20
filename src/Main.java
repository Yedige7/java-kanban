
import ru.yandex.practicum.tracker.*;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
//        File dir = new File("C:\\Users\\Dev\\IdeaProjects\\java-sprint4-hw");
//        File file = new File(dir, "file.CSV");
//        try {
//            if (file.isFile()) {
//
//                String content = Files.readString(file.toPath());
//                System.out.println(content);
//                List<String> words = new ArrayList<>();
//                try (BufferedReader reader = new BufferedReader(
//                        new FileReader(file, StandardCharsets.UTF_8))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        if (!line.isBlank()) {
//                            words.add(line.trim());
//                        }
//                    }
//                } catch (IOException e) {
//                    System.out.println("Произошла ошибка во время чтения файла.");
//                }
//            } else {
//                file.createNewFile();
//                if (file.isFile()) {
//                    System.out.println("File created");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic1 = new Epic("Epic 1", "Epic Description", Status.NEW);
        manager.addTask(task1);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epic1.getId());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubtasks();

        for (Task t : tasks) {
            System.out.println(t);
        }

        for (Epic e : epics) {
            System.out.println(e);
        }

        for (Subtask s : subtasks) {
            System.out.println(s);
        }

        assert tasks.size() == 1 : "Задач должно быть 1";
        assert epics.size() == 1 : "Эпиков должно быть 1";
        assert subtasks.size() == 1 : "Подзадач должно быть 1";

    }
}
