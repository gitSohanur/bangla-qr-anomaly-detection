package datastructure;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** A singly linked list, implemented manually (no java.util.LinkedList). */
public class CustomLinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /** Appends to the end of the list. O(1) because tail is tracked. */
    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Inserts at the front of the list. O(1). */
    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        node.next = head;
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    /**
     * Removes and returns the first element.
     * @throws NoSuchElementException if the list is empty
     */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("Cannot removeFirst from an empty list");
        }
        T value = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    /**
     * Removes the first node whose value equals the given value.
     * @return true if a node was removed, false if not found
     */
    public boolean remove(T value) {
        if (head == null) {
            return false;
        }
        if (head.data.equals(value)) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }
        Node<T> prev = head;
        Node<T> curr = head.next;
        while (curr != null) {
            if (curr.data.equals(value)) {
                prev.next = curr.next;
                if (curr == tail) {
                    tail = prev;
                }
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    /** True if some element in the list equals the given value. */
    public boolean contains(T value) {
        Node<T> curr = head;
        while (curr != null) {
            if (curr.data.equals(value)) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    /** Returns the first element without removing it. */
    public T peekFirst() {
        if (head == null) {
            throw new NoSuchElementException("Cannot peekFirst on an empty list");
        }
        return head.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.data;
                current = current.next;
                return value;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> curr = head;
        while (curr != null) {
            sb.append(curr.data);
            if (curr.next != null) {
                sb.append(" -> ");
            }
            curr = curr.next;
        }
        return sb.append("]").toString();
    }
    /**
     * Inserts a value at the given index, shifting later elements back.
     * index 0 behaves like addFirst; index size behaves like addLast.
     * @throws IndexOutOfBoundsException if index is negative or greater than size
     */
    public void insertAt(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> newNode = new Node<>(value);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }
}

