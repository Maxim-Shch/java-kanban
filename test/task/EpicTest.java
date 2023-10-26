package task;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    protected Epic createEpic() {
        return new Epic("Epic for test", "Description of epic", Status.NEW);
    }

    //a. Пустой список подзадач эпика
    @Test
    public void epicStatusShouldBeNewWhenSubtasksOfEpicAreEmpty() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.NEW, savedEpic.status); //New, потому что нет на данный момент подзадач
    }
    //b. Все подзадачи со статусом New
    @Test
    public void epicStatusShouldBeNewWhenAllSubtaskOfEpicAreNew() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtaskOne = new Subtask("Подзадача 1 эпи 1", "Описание 1", Status.NEW, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        Subtask subtaskTwo = new Subtask("Подзадача 2 эпика 1", "Описание 2", Status.NEW, 60,
                LocalDateTime.of(2023, 7, 1, 15, 00), epic.getId());
        taskManager.addNewSubtask(subtaskOne);
        taskManager.addNewSubtask(subtaskTwo);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.NEW, savedEpic.status); //New, потому что все подзадачи New
    }
    //c. Все подзадачи со статусом Done
    @Test
    public void epicStatusShouldBeDoneWhenAllSubtasksOfEpicAreDone() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtaskOne = new Subtask("Подзадача 1 эпика", "Описание 1",Status.DONE, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        Subtask subtaskTwo = new Subtask("Подзадача 2 эпика 1", "Описание 2", Status.DONE, 60,
                LocalDateTime.of(2023, 7, 1, 15, 00), epic.getId());
        taskManager.addNewSubtask(subtaskOne);
        taskManager.addNewSubtask(subtaskTwo);
        final Epic saveEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.DONE, saveEpic.status); //Done потому что все его подзадачи Done
    }
    //d. Подзадачи со статусами NEW and Done
    @Test
    public void epicStatusBeInProgressWhenSubtaskOfEpicAreNewAndDone() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtaskOne = new Subtask("Подзадача 1 эпика", "Описание 1",Status.NEW, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        Subtask subtaskTwo = new Subtask("Подзадача 2 эпика 1", "Описание 2", Status.DONE, 60,
                LocalDateTime.of(2023, 7, 1, 15, 00), epic.getId());
        taskManager.addNewSubtask(subtaskOne);
        taskManager.addNewSubtask(subtaskTwo);
        final Epic saveEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.IN_PROGRESS, saveEpic.status); //IN_PROGRESS потому что одна подзадача NEW, вторая Done
    }
    //e. Подзадачи со статусом IN_PROGRESS
    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksOfEpicAreInProgress() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtaskOne = new Subtask("Подзадача 1 эпика", "Описание 1",Status.IN_PROGRESS, 60,
                LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        Subtask subtaskTwo = new Subtask("Подзадача 2 эпика 1", "Описание 2", Status.IN_PROGRESS, 60,
                LocalDateTime.of(2023, 7, 1, 15, 00), epic.getId());
        taskManager.addNewSubtask(subtaskOne);
        taskManager.addNewSubtask(subtaskTwo);
        final Epic saveEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.IN_PROGRESS, saveEpic.status); //IN_PROGRESS потому все его подзадачи IN_PROGRESS
    }
}