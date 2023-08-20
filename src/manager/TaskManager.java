package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskManager {

    Integer generatorId = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

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
    public void deletingAllTasks() {
        tasks.clear();
    }

    public void deletingAllEpic() {
        epics.clear();
    }

    public void deletingAllSubtask() {
        subtasks.clear();
    }

    //2.3 Получение по идентификатору
    public Task getTheTaskId(Integer id) {
        return tasks.get(id);
    }

    public Epic getTheEpicId(Integer id) {
        return epics.get(id);
    }

    public Subtask getTheSubtask(Integer id) {
        return subtasks.get(id);
    }

    //2.4 Создание. Сам объект должен передаваться в качестве параметра.
    public void creatingTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
    }

    public void creatingSubtask(Subtask subtask) {
        subtask.setId(++generatorId);
        Epic epic = getTheEpicId(subtask.getEpicId());
        List<Integer> subtasksId = epic.getSubtaskId();
        subtasksId.add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
    }

    public void creatingEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
    }

    //2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        updateEpicStatus(getTheEpicId(subtask.getEpicId()));
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpic(Epic epic) {
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    //2.6 Удаление по идентификатору.
    public void deleteIdTask(Integer id) {
        tasks.remove(id);
    }

    public void deleteIdSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void deleteIdEpic(Integer id) {
        Epic epic = getTheEpicId(id);
        List<Integer> subtasksOfEpic = epic.getSubtaskId();
        for (Integer subId : subtasksOfEpic) {
            subtasks.remove(subId);
        }
        epics.remove(id);
    }

    //3.1 Получение списка всех подзадач определённого эпика.
    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        List<Integer> idSubtask = epic.getSubtaskId();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : idSubtask) {
            subtasksOfEpic.add(getTheSubtask(id));
        }
        return subtasksOfEpic;
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);
        List<String> statusList = new ArrayList<>();

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus("NEW");
        } else {
            for (Subtask subtask : subtasksOfEpic) {
                statusList.add(subtask.getStatus());
            }
        }

        boolean isNew = true;
        boolean isDone = true;

        for (String status : statusList) {
            if (!Objects.equals(status, "NEW")) {
                isNew = false;
            }
            if (!Objects.equals(status, "DONE")) {
                isDone = false;
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
