package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {//создали реализацию интерфейса истории

    private List<Task> history = new ArrayList<>(); //список для истории просмотров

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);//удалили первый элемент чтобы записать  в историю новый элемент
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {//отобразили историю
        return history;
    }
}
