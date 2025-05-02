package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> taskListMap;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.taskListMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Task copiedTask = null;
        if (task instanceof Subtask) {
            Subtask original = (Subtask) task;
            copiedTask = new Subtask(
                    original.getTitle(),
                    original.getDescription(),
                    original.getStatus(),
                    original.getEpicId()
            );
            copiedTask.setId(original.getId());
        } else if (task instanceof Epic) {
            Epic original = (Epic) task;
            Epic copy = new Epic(
                    original.getTitle(),
                    original.getDescription(),
                    original.getStatus()
            );
            copy.setId(original.getId());
            List<Integer> integers = original.getSubTasks();
            for (Integer id : integers) {
                copy.addSubtask(id);
            }
            copiedTask = copy;
        } else {
            Task original = task;
            copiedTask = new Task(
                    original.getTitle(),
                    original.getDescription(),
                    original.getStatus()
            );
            copiedTask.setId(original.getId());
        }

        linkLast(copiedTask);
    }

    public void linkLast(Task task) {
        if (taskListMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
            newNode.prev = oldTail;
        }

        taskListMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.task);
            current = current.next;
        }
        return list;

    }

    private void removeNode(Node node) {
        Node oldHead = node.prev;
        Node oldTail = node.next;
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = taskListMap.get(id);
        if (node != null) {
            removeNode(node);
        }
        taskListMap.remove(id);
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "Node " + task + "; prev: " + prev + "; next: " + next;
        }
    }
}
