package managers;

import httpServers.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    private KVServer server;
    private TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            taskManager = Managers.getDefault();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldCorrectlyLoadTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 2, 1, 15, 00));
        taskManager.addNewTask(task);

        assertEquals(List.of(task), taskManager.getListOfTasks());

        HttpTaskManager restored = new HttpTaskManager("http://localhost:8078");
        restored.loadFromServer();

        assertEquals(restored.getListOfTasks(), taskManager.getListOfTasks());
    }

    @Test
    public void shouldCorrectlyLoadEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Description", "Title", Status.NEW);
        taskManager.addNewEpic(epic);
        assertEquals(List.of(epic), taskManager.getListOfEpic());
        HttpTaskManager restored = new HttpTaskManager("http://localhost:8078");
        restored.loadFromServer();

        assertEquals(restored.getListOfEpic(), taskManager.getListOfEpic());
    }

    @Test
    public void shouldCorrectlyLoadSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Description", "Title", Status.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        taskManager.addNewSubtask(subtask1);

        HttpTaskManager restored = new HttpTaskManager("http://localhost:8078");
        restored.loadFromServer();

        assertEquals(restored.getListOfSubtask(), taskManager.getListOfSubtask());
    }

    @Test
    public void shouldCorrectlyLoadPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2024, 2, 1, 15, 00));
        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW,60,
                LocalDateTime.of(2023, 2, 1, 15, 00));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        assertEquals(task2, taskManager.getPrioritizedTask().get(0));

        HttpTaskManager restored = new HttpTaskManager("http://localhost:8078");
        restored.loadFromServer();

        assertEquals(restored.getPrioritizedTask(), taskManager.getPrioritizedTask());
    }
}
