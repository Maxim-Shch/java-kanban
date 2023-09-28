import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(); //создали объект менеджера неявно с помощью вспомогательного класса

        Task taskOne = new Task("taskOne", "taskOneDescription", Status.NEW); //Первая задача

        Task taskTwo = new Task("taskTwo", "taskTwoDescription", Status.NEW); //Вторая задача
        taskManager.createTask(taskOne); //создали задачу = id - 1
        taskManager.createTask(taskTwo);//создали задачу = id - 2

        //Epic
        Epic epicWithTwoSubtasks = new Epic("epicOne", "epicOneDescription", Status.NEW);
        taskManager.createEpic(epicWithTwoSubtasks); //создали epic = id-3

        Subtask subtaskOne = new Subtask("subtaskOne", "subtaskOneDescription", Status.NEW,
                epicWithTwoSubtasks.getId()); //инициализировали подзадачу эпика первую
        taskManager.createSubtask(subtaskOne); //сохранили подзадачу id-4

        Subtask subtaskTwo = new Subtask("subtaskTwo", "subtaskTwoDescription", Status.NEW,
                epicWithTwoSubtasks.getId()); //инициализировали подзадачу эпика вторую
        taskManager.createSubtask(subtaskTwo); //сохранили подзадачу id-5

        Subtask subtaskThree = new Subtask("subtaskThree", "subtaskThreeDescription", Status.NEW,
                epicWithTwoSubtasks.getId()); //инициализировали подзадачу эпика третью
        taskManager.createSubtask(subtaskThree); //сохранили подзадачу id-6

        Epic epicWithOneSubtasks = new Epic("epicOneSubtask", "epicOneDescription", Status.NEW);//эпик id=6
        taskManager.createEpic(epicWithOneSubtasks); //создали эпик, который равен id = 7

        taskManager.getTaskById(1);//история
        taskManager.getEpicById(7);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());

        System.out.println();

        taskManager.getEpicById(7);//история
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());

        System.out.println();

        taskManager.deleteTaskById(2); //удаление задачи, которая есть в истории
        System.out.println(taskManager.getHistory());

        System.out.println();

        taskManager.deleteEpicById(3); //удаление эпика с тремя подзадачами
        System.out.println(taskManager.getHistory());
    }
}
