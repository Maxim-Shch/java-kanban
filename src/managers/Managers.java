package managers;

import httpServers.KVServer;
import java.io.IOException;

public class Managers {//вспомогательный класс

    public static HistoryManager getDefaultHistory() {//возращает объект-менеджер истории
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() throws IOException, InterruptedException {//возращает объект-менеджер нужного типа, к примеру taskmanager
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);//возращает реализацию памяти
    }
}