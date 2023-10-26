package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {//создали реализацию интерфейса истории

    public CustomLinkedList<Task> historyLinkedList = new CustomLinkedList<>();
    private Map<Integer, Node<Task>> mapForHistoryList = new HashMap<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            remove(task.getId());
            historyLinkedList.linkLast(task);
        }
    }
    @Override
    public void remove(int id) { //удаления задачи из просмотра
        historyLinkedList.removeNode(mapForHistoryList.remove(id));
    }

    @Override
    public List<Task> getHistory() {//отобразили историю
        return historyLinkedList.getTasks();
    }

    class CustomLinkedList<T> {
        private Node<Task> head;
        private Node<Task> tail;

        private void linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<Task>(oldTail, task, null);
            tail = newNode;
            mapForHistoryList.put(task.getId(), newNode);
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        private List<Task> getTasks() {
            List<Task> historyOfTasks = new ArrayList<>();
            Node<Task> currentNode = head;
            while (currentNode != null) {
                historyOfTasks.add(currentNode.data);
                currentNode = currentNode.next;
            }
            return historyOfTasks;
        }

        private void removeNode(Node<Task> node) {
            if (node != null) {
                final Node<Task> next = node.next;
                final Node<Task> prev = node.prev;

                if (head == node && tail == node) {
                    head = null;
                    tail = null;
                } else if (head == node) {
                    head = next;
                    head.prev = null;
                } else if (tail == node) {
                    tail = prev;
                    tail.next = null;
                } else {
                    prev.next = next;
                    next.prev = prev;
                }
            }
        }
    }
}
