package task;

import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    List<Integer> subtaskId;


    public Epic(String name, String description, String status, List<Integer> subtaskId) {
        super(name, description, status);
        this.subtaskId = subtaskId;
    }

    public Epic(String name, String description, int id, String status, List<Integer> subtaskId) {
        super(name, description, id, status);
        this.subtaskId = subtaskId;
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(List<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskId=" + subtaskId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
