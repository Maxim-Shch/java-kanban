package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskManager {

    private Integer generatorId = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //2.1 получение списка всех задач
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getListOfEpic() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getListOfSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    //2.2 Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtaskIds();
            updateEpicStatus(epic);
        }
    }

    //2.3 Получение по идентификатору
    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    //2.4 Создание. Сам объект должен передаваться в качестве параметра.
    public void createTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(++generatorId);
            epic.setSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
        }
    }

    public void createEpic(Epic epic) {
        epic.setId(++generatorId);
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    //2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            if (Objects.equals(subtask.getEpicId(), subtasks.get(subtaskId).getEpicId())) {
                subtasks.put(subtaskId, subtask);
                updateEpicStatus(epics.get(subtask.getEpicId()));
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicForUpdate = epics.get(epic.getId());
            epicForUpdate.setName(epic.getName());
            epicForUpdate.setDescription(epic.getDescription());
        }
    }

    //2.6 Удаление по идентификатору.
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Integer epicId = subtask.getEpicId();
        subtasks.remove(id);
        epics.get(epicId).deleteSubtaskId(id);
        updateEpicStatus(epics.get(epicId));
    }

    public void deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        for (Integer subId : epic.getSubtaskIds()) {
            subtasks.remove(subId);
        }
        epics.remove(id);
    }

    //3.1 Получение списка всех подзадач определённого эпика.
    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        List<Integer> idSubtask = epic.getSubtaskIds();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : idSubtask) {
            subtasksOfEpic.add(subtasks.get(id));
        }
        return subtasksOfEpic;
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);

        boolean isNew = true;
        boolean isDone = true;

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus("NEW");
            return;
        } else {
            for (Subtask subtask : subtasksOfEpic) {
                String status = subtask.getStatus();
                if (!Objects.equals(status, "NEW")) {
                    isNew = false;
                }
                if (!Objects.equals(status, "DONE")) {
                    isDone = false;
                }
            }
        }

        if (isNew) {
            epic.setStatus("NEW");
        } else if (isDone) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }
}
