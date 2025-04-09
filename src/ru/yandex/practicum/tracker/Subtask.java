package ru.yandex.practicum.tracker;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public void setId(int id) {
        if (id == epicId) {
            throw new IllegalArgumentException("Subtask ID must not be the same as Epic ID.");
        }
        super.setId(id);
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
