package manager;

import task.Task;

import java.util.List;

public interface HistoryManager { //создали интерфейс
    void add(Task task); //метод для добавления задачи в историю
    List<Task> getHistory(); //получили список истории
}
