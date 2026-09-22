package datastructure;

/** A single link in a CustomLinkedList: holds a value and a reference to the next node. */
class Node<T> {
    T data;
    Node<T> next;

    Node(T data) {
        this.data = data;
        this.next = null;
    }
}