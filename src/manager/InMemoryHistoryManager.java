package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {//создали реализацию интерфейса истории

    private List<Task> history = new ArrayList<>(); //список для истории просмотров

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == 10) {
                history.remove(0); //удалили первый элемент чтобы записать  в историю новый элемент
            }
            history.add(task); // поместил во внутрь проверки на null
        }
    }

    @Override
    public ArrayList<Task> getHistory() {//отобразили историю
        return new ArrayList<>(history);
    }
}
