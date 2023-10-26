package manager;

import exceptions.ManagerValidateException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    Integer generatorId = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();//создали менеджера истории

    Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task o1, Task o2) {

            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    };

    Set<Task> prioritizedTasks = new TreeSet<>(comparator); // отсортированный по приоритету список задач
    List<Task> tasksWithoutStartTime = new ArrayList<>();

    private void validateTaskPriority(Task task) {
        checkTaskTime(task);
    }

    protected void checkTaskTime(Task task) {
        if (task.getStartTime() == null && task.getEndTime() == null) {
            tasksWithoutStartTime.add(task);
        } else {
            addToPrioritizedTask(task);
        }
    }

    private void addToPrioritizedTask(Task task) {
        if (isIntersected(task)) {
            throw new ManagerValidateException("Задача " + task.getId() + " пересекается с другой задачей. " +
                    "Измените время начала выполнения задачи.");
        } else {
            prioritizedTasks.add(task);
        }
    }

    private void checkTaskBeforeUpdating(Task task) {
        List<Task> tasksList = getPrioritizedTask();
        for(Task taskFromList : tasksList) {
            if(taskFromList.getId() == task.getId()) {
                prioritizedTasks.remove(taskFromList);
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTask() {
        List<Task> tasksPriority = new ArrayList<>(prioritizedTasks);
        tasksPriority.addAll(tasksWithoutStartTime);
        return tasksPriority;
    }

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
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    //2.4 Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addNewTask(Task task) {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        validateTaskPriority(task);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Epic currentEpic = epics.get(subtask.getEpicId());
        if (currentEpic == null) {
            System.out.println("Такого эпика не существует!" + subtask.getEpicId());
            return -1;
        }
        Integer id = ++generatorId;
        subtask.setId(id);
        currentEpic.setSubtaskId(id);
        subtasks.put(id, subtask);
        validateTaskPriority(subtask);
        updateEpicStatus(currentEpic);
        updateEpicTime(currentEpic);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    //2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
        checkTaskBeforeUpdating(task);
        validateTaskPriority(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            if (Objects.equals(subtask.getEpicId(), subtasks.get(subtaskId).getEpicId())) {
                subtasks.put(subtaskId, subtask);
                checkTaskBeforeUpdating(subtask);
                validateTaskPriority(subtask);
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
            epicForUpdate.setStatus(epic.getStatus());
            epicForUpdate.setDuration(epic.getDuration());
            epicForUpdate.setEndTime(epic.getEndTime());
            updateEpicTime(epic);
            epics.put(epic.getId(), epic);
        }
    }
    private void updateEpicTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        } else {
            epic.setStartTime(getStartTimeOfEpic(epic));
            epic.setEndTime(getEndTimeOfEpic(epic));
            epic.setDuration(getEpicDuration(epic));
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
        updateEpicTime(epics.get(epicId));
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

    public LocalDateTime getStartTimeOfEpic(Epic epic) {

        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);

        List<LocalDateTime> listWithTime = new ArrayList<>();
        for (Subtask subtask : subtasksOfEpic) {
            LocalDateTime subStartTime = subtask.getStartTime();
            listWithTime.add(subStartTime);
        }
        LocalDateTime epicStartTime = listWithTime.get(0); // переменная для сравнения времени

        for (LocalDateTime dateTime : listWithTime) { // проходимся по списку времени, сравниваем каждый элемент с первым
            if (dateTime.isBefore(epicStartTime)) {
                epicStartTime = dateTime;
            }
        }
        return epicStartTime;
    }

    public LocalDateTime getEndTimeOfEpic(Epic epic) {

        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);

        List<LocalDateTime> listWithTime = new ArrayList<>();
        for (Subtask subtask : subtasksOfEpic) {
            LocalDateTime subEndTime = subtask.getEndTime();
            listWithTime.add(subEndTime);
        }
        LocalDateTime epicEndTime = listWithTime.get(0); // переменная для сравнения времени

        for (LocalDateTime dateTime : listWithTime) { // проходимся по списку времени, сравниваем каждый элемент с первым
            if (dateTime.isAfter(epicEndTime)) {
                epicEndTime = dateTime;
            }
        }
        return epicEndTime;
    }

    public long getEpicDuration(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtaskOfEpic(epic);
        long epicDuration = 0;
        for(Subtask subtask : subtasksOfEpic) {
            epicDuration += subtask.getDuration();
        }
        return epicDuration;
    }

    public boolean isIntersected(Task task) { // проверка пересечения
        List<Task> list = getPrioritizedTask();
        boolean isIntersected = false;

        for (Task sortedTask : list) {
            LocalDateTime startDate1 = task.getStartTime();
            LocalDateTime endDate1 = task.getEndTime();
            LocalDateTime startDate2 = sortedTask.getStartTime();
            LocalDateTime endDate2 = sortedTask.getEndTime();

            if (startDate2 != null && endDate2 != null) {
                isIntersected = isItCaseOfIntersection(startDate1, endDate1, startDate2, endDate2);
            }
            if (isIntersected) {
                break;
            }
        }
        return isIntersected;
    }

    public boolean isItCaseOfIntersection(LocalDateTime from1, LocalDateTime to1, LocalDateTime from2, LocalDateTime to2) {
        return ((from2.isBefore(from1) && to2.isAfter(from1)) || (from2.isAfter(from1) && from2.isBefore(to1))
                && (from1.isEqual(from2) || to1.isEqual(to2))) ;
    }
}
