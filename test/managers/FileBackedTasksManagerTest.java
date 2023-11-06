package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final Path path = Path.of("data/data.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(file);
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 2, 1, 15, 00));
        taskManager.addNewTask(task);
        Epic epic = new Epic("Description", "Title", Status.NEW);
        taskManager.addNewEpic(epic);
        taskManager.getTaskById(task.getId());

        assertEquals(List.of(task), taskManager.getListOfTasks());
        assertEquals(List.of(epic), taskManager.getListOfEpic());

        FileBackedTasksManager restored = FileBackedTasksManager.loadFromFile(file);
        assertEquals(restored.getListOfTasks(), taskManager.getListOfTasks());
        assertEquals(restored.getListOfEpic(), taskManager.getListOfEpic());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, taskManager.getListOfTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getListOfEpic());
        assertEquals(Collections.EMPTY_LIST, taskManager.getListOfSubtask());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }

}