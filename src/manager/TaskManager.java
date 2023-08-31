package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;


public interface TaskManager {

    List<Task> getListOfTasks();

    List<Epic> getListOfEpic();

    List<Subtask> getListOfSubtask();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteEpicById(Integer id);

    List<Subtask> getSubtaskOfEpic(Epic epic);

    List<Task> getHistory();
}
