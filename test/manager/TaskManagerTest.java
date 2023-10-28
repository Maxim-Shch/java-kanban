package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected Task createTask() {
        return new Task("Задача 1", "Описание 1", Status.NEW, 180,
                LocalDateTime.of(2023, 2, 1, 15, 00));
    }

    protected Epic createEpic() {
        return new Epic("Задача 1", "Описание 1", Status.NEW);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
    }

    //history test
    @Test
    void shouldReturnHistory() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<Task> history = taskManager.getHistory();

        assertNotNull(history);
        assertEquals(3, history.size());
    }

    @Test
    void shouldReturnHistoryWhenHistoryListIsEmpty() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldAddNewTask() {
        Task task = createTask();
        final int taskId = taskManager.addNewTask(task);

        final Task saveTask = taskManager.getTaskById(taskId);

        assertNotNull(saveTask, "Задача не найдена.");
        assertEquals(task, saveTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        final Integer subtaskId = taskManager.addNewSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getListOfSubtask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldAddNewEpic() {
        Epic epic = createEpic();
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, " Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldReturnListOfTask() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Task taskOne = createTask();
        taskOne.setStartTime(task.getEndTime().plusMinutes(1)); // Изменяем время начала выполнения задачи 2
        taskManager.addNewTask(taskOne);

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(taskOne, tasks.get(1));
    }
    @Test
    void shouldReturnListOfTasksWhenTaskListIsEmpty() {
        final List<Task> tasks = taskManager.getListOfTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldReturnListOfSubtasks() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getListOfSubtask();

        assertNotNull(subtasks,"Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }
    @Test
    void shouldReturnListOfSubtaskWhenSubtasksListIsEmpty() {
        final List<Subtask> subtasks = taskManager.getListOfSubtask();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void shouldReturnListOfEpics() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Epic epicOne = createEpic();
        taskManager.addNewEpic(epicOne);

        final List<Epic> epics = taskManager.getListOfEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
        assertEquals(epicOne, epics.get(1));
    }

    @Test
    void shouldReturnTask() { //стандартное поведение
        Task task = createTask();
        taskManager.addNewTask(task);

        assertEquals(task, taskManager.getListOfTasks().get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldNotReturnTaskWhenTasksListIsEmpty() { //попытка получения задачи из пустого списка задач
        Task task = createTask();

        assertNull(taskManager.getTaskById(task.getId())); //получаем null, потому что задача была создана,
        //но не добавлена в список
    }
    @Test
    void shouldNotReturnTaskWithWrongId() { //попытка получения задачи с некорректным id
        Task task = createTask();
        taskManager.addNewTask(task);

        assertNull(taskManager.getTaskById(567));
    }

    @Test
    void shouldReturnSubtask() { //стандартное поведение
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        assertEquals(subtask, taskManager.getListOfSubtask().get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldNotReturnSubtaskWhenSubtasksListIsEmpty() { // попытка получения подзадачи из пустого списка подзадач
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);

        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldNotReturnSubtaskWithWrongId() {
        assertNull(taskManager.getSubtaskById(567));
    }

    @Test
    void shouldReturnEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);

        assertEquals(epic, taskManager.getListOfEpic().get(0));
    }

    @Test
    void shouldNotReturnEpicWhenEpicsListIsEmpty() {
        Epic epic = createEpic();

        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void shouldNotReturnEpicWithWrongId() {
        assertNull(taskManager.getEpicById(567));
    }

    @Test
    void shouldUpdateTask() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Task taskOne = new Task(task.getId(), "UPD task name", "UPD task description", Status.DONE,
                180, LocalDateTime.of(2023, 03, 03, 12, 00));
        taskManager.updateTask(taskOne);

        assertNotEquals(task, taskManager.getListOfTasks().get(0)); //проверили, что в список записалась обновленная
        //задача
    }
    @Test
    void shouldUpdateSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        Subtask subtaskOne = new Subtask(subtask.getId(), "UPD Подзадача 1 эпика 1", "Описание 1",
                Status.NEW, 60, LocalDateTime.of(2023, 9, 1, 15, 00),
                epic.getId());
        taskManager.updateSubtask(subtaskOne);

        assertNotEquals(subtask, taskManager.getListOfSubtask().get(0));
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        epic = new Epic(epic.getId(), "Обновление имени", "Обновление описания", Status.DONE);
        taskManager.updateEpic(epic);

        assertEquals(epic, taskManager.getListOfEpic().get(0));
    }

    @Test
    void shouldDeleteTask() {
        Task task =createTask();
        taskManager.addNewTask(task);
        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
        assertTrue(taskManager.getListOfTasks().isEmpty());
    }

    @Test
    void shouldNotDeleteTaskWithWrongId() {
        Task task = createTask();
        taskManager.addNewTask(task);
        taskManager.deleteTaskById(-1);

        assertEquals(List.of(task), taskManager.getListOfTasks());
    }

    @Test
    void shouldDeleteSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());

        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertTrue(taskManager.getListOfSubtask().isEmpty());
    }

    @Test
    void shouldNotDeleteSubtaskWithWrongId() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteSubtaskById(-1));

        assertEquals(List.of(subtask), taskManager.getListOfSubtask());
    }

    @Test
    void shouldDeleteEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        taskManager.deleteEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertTrue(taskManager.getListOfEpic().isEmpty());
    }

    @Test
    void shouldNotDeleteEpicWithWrongId() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteEpicById(-1));

        assertEquals(List.of(epic), taskManager.getListOfEpic());
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task1 = createTask();
        Task task2 = createTask();
        task2.setStartTime(task1.getEndTime().plusMinutes(1));//Устанавливаем  время начала задачи 2 на 1 минуту после задачи 1
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getListOfTasks().isEmpty());
    }

    @Test
    void shouldNotDeleteAllTasksWhenTasksListIsEmpty() {
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getListOfTasks().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getListOfSubtask().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasksWhenSubtasksListIsEmpty() {
        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getListOfSubtask().isEmpty());
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getListOfEpic().isEmpty());
    }

    @Test
    void shouldDelteAllEpicsWhenEpicsListIsEmpty() {
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getListOfEpic().isEmpty());
    }
}