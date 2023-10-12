package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {

    Integer generatorId = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();//создали менеджера истории


    //2.1 получение списка всех задач
    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    //2.2 Удаление всех задач
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtaskIds();
            updateEpicStatus(epic);
        }

        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    //2.3 Получение по идентификатору@Override
    @Override
    public Task getTaskById(Integer id) { //записали задачу в историю просмотров через обращение к менеджеру истории
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //2.4 Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(++generatorId);
            epic.setSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++generatorId);
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    //2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            if (Objects.equals(subtask.getEpicId(), subtasks.get(subtaskId).getEpicId())) {
                subtasks.put(subtaskId, subtask);
                updateEpicStatus(epics.get(subtask.getEpicId()));
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicForUpdate = epics.get(epic.getId());
            epicForUpdate.setName(epic.getName());
            epicForUpdate.setDescription(epic.getDescription());
        }
    }

    //2.6 Удаление по идентификатору.
    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);//удаление истории просмотров
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Integer epicId = subtask.getEpicId();
        subtasks.remove(id);
        epics.get(epicId).deleteSubtaskId(id);
        updateEpicStatus(epics.get(epicId));
        historyManager.remove(id);//удаление истории просмотров
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        for (Integer subId : epic.getSubtaskIds()) {
            subtasks.remove(subId);
            historyManager.remove(subId);
        }
        epics.remove(id);
        historyManager.remove(id);//удаление истории просмотров
    }

    //3.1 Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        List<Integer> idSubtask = epic.getSubtaskIds();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : idSubtask) {
            subtasksOfEpic.add(subtasks.get(id));
        }
        return subtasksOfEpic;
    }

    @Override
    public List<Task> getHistory() {//получили историю через обращение к менеджеру истории
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);

        boolean isNew = true;
        boolean isDone = true;

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        } else {
            for (Subtask subtask : subtasksOfEpic) {
                Status status = subtask.getStatus();
                if (!Objects.equals(status, Status.NEW)) {
                    isNew = false;
                }
                if (!Objects.equals(status, Status.DONE)) {
                    isDone = false;
                }
            }
        }

        if (isNew) {
            epic.setStatus(Status.NEW);
        } else if (isDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
