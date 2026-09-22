package datastructure;

import java.util.NoSuchElementException;

/**
 * A FIFO queue for sequential transaction processing.
 * Built on top of CustomLinkedList: enqueue appends to the back,
 * dequeue removes from the front.
 */
public class CustomQueue<T> {
    private final CustomLinkedList<T> items;

    public CustomQueue() {
        items = new CustomLinkedList<>();
    }

    /** Adds an item to the back of the queue. O(1). */
    public void enqueue(T value) {
        items.addLast(value);
    }

    /**
     * Removes and returns the item at the front of the queue.
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue from an empty queue");
        }
        return items.removeFirst();
    }

    /**
     * Returns the item at the front without removing it.
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Cannot peek an empty queue");
        }
        return items.peekFirst();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        return items.toString();
    }
}