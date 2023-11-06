package managers;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;


public interface TaskManager {

    static int extractId(String query) {
        try {
            return Integer.parseInt(query.substring(query.indexOf("id=") + 3));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    List<Task> getPrioritizedTask();
    List<Task> getHistory(); //получили список истории

    // 2.1. Получение списков всех типов задач
    List<Task> getListOfTasks();

    List<Epic> getListOfEpic();

    List<Subtask> getListOfSubtask();

    // 2.2 Удаление списков всех типов задач
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    // 2.3 Получение по идентификатору
    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    // 2.4 Создание - передача объекта в качестве параметра
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    // 2.4 Обновление объектов
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // 2.5 Удаление объекта по индентификатору
    void deleteTaskById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteEpicById(Integer id);

    // 3.1 получение списка всех подзадач определенного эпика
    List<Subtask> getSubtaskOfEpic(Epic epic);

}
