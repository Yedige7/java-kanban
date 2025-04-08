package ru.yandex.practicum.tracker;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        if (id == epicId) {
            throw new IllegalArgumentException("Subtask ID must not be the same as Epic ID.");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask {" +
                " EpicId = " + epicId +
                ", title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus()+
                '}';
    }
}
