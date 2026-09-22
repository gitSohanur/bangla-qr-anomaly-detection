package datastructure;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomQueueTest {

    @Test
    void newQueue_isEmpty() {
        CustomQueue<String> q = new CustomQueue<>();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
    }

    @Test
    void enqueue_intoEmptyQueue_becomesFront() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("A");
        assertEquals("A", q.peek());
        assertEquals(1, q.size());
    }

    @Test
    void enqueue_multiple_preservesArrivalOrder() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("T001");
        q.enqueue("T002");
        q.enqueue("T003");
        assertEquals("[T001 -> T002 -> T003]", q.toString());
    }

    @Test
    void dequeue_returnsInFifoOrder() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("T001");
        q.enqueue("T002");
        q.enqueue("T003");
        assertEquals("T001", q.dequeue());
        assertEquals("T002", q.dequeue());
        assertEquals("T003", q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    void dequeue_fromEmptyQueue_throws() {
        CustomQueue<String> q = new CustomQueue<>();
        assertThrows(NoSuchElementException.class, q::dequeue);
    }

    @Test
    void peek_doesNotRemoveElement() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("A");
        q.enqueue("B");
        assertEquals("A", q.peek());
        assertEquals("A", q.peek()); // still there
        assertEquals(2, q.size());
    }

    @Test
    void peek_onEmptyQueue_throws() {
        CustomQueue<String> q = new CustomQueue<>();
        assertThrows(NoSuchElementException.class, q::peek);
    }

    @Test
    void enqueueAfterDequeue_maintainsCorrectOrder() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("T001");
        q.enqueue("T002");
        q.dequeue(); // removes T001
        q.enqueue("T003");
        assertEquals("[T002 -> T003]", q.toString());
    }

    @Test
    void dequeueToEmptyThenEnqueueAgain_stillWorks() {
        CustomQueue<Integer> q = new CustomQueue<>();
        q.enqueue(1);
        q.dequeue();
        assertTrue(q.isEmpty());
        q.enqueue(2); // exercises tail-reset path inherited from CustomLinkedList
        assertEquals(2, q.peek());
        assertEquals(1, q.size());
    }

    @Test
    void size_tracksEnqueueAndDequeue() {
        CustomQueue<Integer> q = new CustomQueue<>();
        q.enqueue(1);
        q.enqueue(2);
        assertEquals(2, q.size());
        q.dequeue();
        assertEquals(1, q.size());
    }
}