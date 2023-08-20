import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager(); //создали объект менеджера
        Task taskOne = new Task("taskOne", "taskOneDescription", "NEW"); //Первая задача

        Task taskTwo = new Task("taskTwo", "taskTwoDescription", "NEW"); //Вторая задача
        taskManager.creatingTask(taskOne); //создали задачу = id - 1
        taskManager.creatingTask(taskTwo);//аналогично

        //Epic
        Epic epicWithTwoSubtasks = new Epic("epicOne", "epicOneDescription", "NEW",
                new ArrayList<>());
        taskManager.creatingEpic(epicWithTwoSubtasks); //создали epic = 3

        Subtask subtaskOne = new Subtask("subtaskOne", "subtaskOneDescription", "NEW",
                epicWithTwoSubtasks.getId()); //инициализировали подзадачу эпика первую
        taskManager.creatingSubtask(subtaskOne); //сохранили подзадачу

        Subtask subtaskTwo = new Subtask("subtaskTwo", "subtaskTwoDescription", "NEW",
                epicWithTwoSubtasks.getId()); //инициализировали подзадачу эпика первую
        taskManager.creatingSubtask(subtaskTwo); //сохранили подзадачу

        Epic epicWithOneSubtasks = new Epic("epicOneSubtask", "epicOneDescription", "NEW",
                new ArrayList<>());//эпик id=6
        taskManager.creatingEpic(epicWithOneSubtasks); //создали эпик, который равен id = 6
        Subtask oneSubtask = new Subtask("epicOneSubtask", "epicOneDescription", "NEW",
                epicWithOneSubtasks.getId());//инициализировали подзадачу эпика первую
        taskManager.creatingSubtask(oneSubtask); //сохранили подзадачу id=7

        System.out.println(taskManager.getListOfTasks());//распечатали список задач
        System.out.println(taskManager.getListOfEpic());
        System.out.println(taskManager.getListOfSubtask());

        taskOne.setStatus("IN_PROGRESS");//изменяем статус
        taskManager.updateTask(taskOne);

        subtaskOne.setStatus("DONE");
        taskManager.updateSubtask(subtaskOne);
        subtaskTwo.setStatus("DONE");
        taskManager.updateSubtask(subtaskTwo);

        System.out.println();
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfSubtask());
        System.out.println(taskManager.getListOfEpic());

        taskManager.deleteIdTask(1);
        taskManager.deleteIdEpic(3);
        System.out.println();
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfSubtask());
        System.out.println(taskManager.getListOfEpic());
    }
}
