package manager;

public class Managers {//вспомогательный класс

    public static TaskManager getDefault() {//возращает объект-менеджер нужного типа, к примеру taskmanager
        return new InMemoryTaskManager();//возращает реализацию памяти
    }

    public static HistoryManager getDefaultHistory() {//возращает объект-менеджер истории
        return new InMemoryHistoryManager();
    }
}
