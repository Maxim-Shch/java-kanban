package manager;
import exceptions.ManagerLoadException;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager  {
    private final File file;
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    List<Integer> history = historyFromString(br.readLine());
                    fileManager.readHistory(history);
                    break;
                } else {
                    Task currentTask = fromString(line);
                    fileManager.readTasks(currentTask);
                }
            }
        }
        catch (IOException exception) {
            throw new ManagerLoadException("Ошибка чтения файла.");
        }
        return fileManager;
    }

    private void readHistory(List<Integer> history) {
        for (int i = 0; i < history.size(); i++) {
            Integer currentId = history.get(i);
            if (epics.containsKey(currentId)) {
                getEpicForFileLoad(currentId);
            } else if (subtasks.containsKey(currentId)) {
                getSubtaskForFileLoad(currentId);
            } else {
                getTaskForFileLoad(currentId);
            }
        }
    }

    private <T extends Task> void readTasks(Task currentTask) {
        if(currentTask instanceof  Epic) {
            epics.put(currentTask.getId(), (Epic) currentTask);
        } else if (currentTask instanceof  Subtask) {
            int epicId = ((Subtask) currentTask).getEpicId();
            Epic epic = epics.get(epicId);
            epic.setSubtaskId(currentTask.getId());
            epics.put(epicId, epic);
        } else {
            tasks.put(currentTask.getId(), currentTask);
        }
        if (currentTask.getId() > generatorId) {
            generatorId = currentTask.getId();
        }
    }



    private void save() {
        try(Writer writer = new FileWriter(file)) { //Исключения вида IOException
            writer.write("id, type, name,description, epic\n");
            writeTasks(getListOfTasks(), writer);
            writeTasks(getListOfEpic(), writer);
            writeTasks(getListOfSubtask(), writer);
            writer.write("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    private <T extends  Task> void writeTasks(List<T> tasks, Writer writer) throws IOException {
        for (Task task : tasks) {
            writer.write(toCsvString(task) + "\n");
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    private String toCsvString(Task task) { // метод сохранения задачи в строку
        String type = "TASK";
        String endOfString = "";
        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
            endOfString += ((Subtask) task).getEpicId();
        }
        String csvString = task.getId() + "," +
                type + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
        return csvString + endOfString;
    }

    private static Task fromString(String value) {
        String[] taskData = value.split(",");
        int id = Integer.parseInt(taskData[0]);
        TasksType tasksType = TasksType.valueOf(taskData[1]);
        String name = taskData[2];
        Status status = Status.valueOf(taskData[3]);
        String description = taskData[4];

        switch (tasksType) { //метод создания задачи из строки
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status);
            case SUBTASK:
                int epicId = Integer.parseInt(taskData[5]);
                return new Subtask(name, description, id, status, epicId);
            default:
                throw new ManagerLoadException("Ошибка восстановления задачи из строки.");
        }
    }

    private String historyToString(HistoryManager manager) { //сохранениe истории in CSV.
        List<String> taskIds = new ArrayList<>();

        for (Task task : manager.getHistory()) {
            taskIds.add(String.valueOf(task.getId()));
        }
        return String.join(",", taskIds);
    }

    private static List<Integer> historyFromString(String value) { //восстановления менеджера истории из CSV
        String[] idsString = value.split(",");

        List<Integer> tasksIds = new ArrayList<>();
        for (String idString : idsString) {
            tasksIds.add(Integer.valueOf(idString));
        }
        return tasksIds;
    }

    private void getTaskForFileLoad(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
    }

    private Epic getEpicForFileLoad(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    private void getSubtaskForFileLoad(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("data/data.csv"));
        Task taskOne = new Task("Задача 1", "Описание 1", Status.NEW);
        Task taskTwo = new Task("Задача 2", "Описание 2", Status.NEW);
        manager.createTask(taskOne); //id 1
        manager.createTask(taskTwo); //id 2

        Epic epicOne = new Epic("Эпик 1 c тремя подзадачами", "Эпик 1", Status.NEW);
        manager.createEpic(epicOne); //id 3
        Subtask subtaskOne = new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 3);
        Subtask subtaskTwo = new Subtask("Подзадача 2 эпика 1", "Описание 2", Status.NEW, 3);
        Subtask subtaskThree = new Subtask("Подзадача 3 эпика 1", "Описание 3", Status.NEW, 3);

        manager.createSubtask(subtaskOne);// id 4
        manager.createSubtask(subtaskTwo);// id 5
        manager.createSubtask(subtaskThree);// id 6

        Epic epicTwo = new Epic("Эпик 2 без подзадач", "Эпик 2", Status.NEW);

        manager.createEpic(epicTwo); // id 7

        manager.getTaskById(1);
        manager.getEpicById(7);
        manager.getSubtaskById(4);
        manager.getEpicById(3);

        FileBackedTasksManager managerTwo = loadFromFile(new File("data/data.csv"));
        managerTwo.createEpic(new Epic("Эпик после загрузки", "Проверка генерации ID", Status.NEW));
        managerTwo.createSubtask(new Subtask("Подзадача после загрузки", "Проверка генерации ID",
                Status.NEW, 8));
    }
}
