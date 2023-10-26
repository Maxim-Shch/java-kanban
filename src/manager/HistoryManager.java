package manager;

import task.Task;

import java.util.List;

public interface HistoryManager { //создали интерфейс
    void addTask(Task task); //метод для добавления задачи в историю
    void remove(int id); //удаления задачи из просмотра
    List<Task> getHistory(); //получили список истории
}
