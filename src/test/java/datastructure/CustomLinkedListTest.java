package datastructure;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    @Test
    void newList_isEmpty() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addLast_singleElement_setsHeadAndTail() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.addLast("A");
        assertEquals(1, list.size());
        assertEquals("A", list.peekFirst());
    }

    @Test
    void addLast_multipleElements_preservesOrder() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        assertEquals("[1 -> 2 -> 3]", list.toString());
        assertEquals(3, list.size());
    }

    @Test
    void addFirst_insertsAtFront() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(2);
        list.addFirst(1);
        assertEquals("[1 -> 2]", list.toString());
    }

    @Test
    void addFirst_onEmptyList_setsTailToo() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addFirst(1);
        list.addLast(2); // proves tail was set correctly by addFirst
        assertEquals("[1 -> 2]", list.toString());
    }

    @Test
    void removeFirst_onEmptyList_throws() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void removeFirst_returnsAndRemovesHead() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        assertEquals(1, list.removeFirst());
        assertEquals("[2]", list.toString());
    }

    @Test
    void removeFirst_lastElement_resetsTail() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1);
        list.removeFirst();
        list.addLast(2); // if tail wasn't reset, this could corrupt the list
        assertEquals("[2]", list.toString());
        assertEquals(1, list.size());
    }

    @Test
    void remove_headElement_works() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2); list.addLast(3);
        assertTrue(list.remove(1));
        assertEquals("[2 -> 3]", list.toString());
    }

    @Test
    void remove_middleElement_works() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2); list.addLast(3);
        assertTrue(list.remove(2));
        assertEquals("[1 -> 3]", list.toString());
    }

    @Test
    void remove_tailElement_updatesTail() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2); list.addLast(3);
        assertTrue(list.remove(3));
        list.addLast(4); // if tail wasn't updated, this would corrupt the list
        assertEquals("[1 -> 2 -> 4]", list.toString());
    }

    @Test
    void remove_nonExistingItem_returnsFalse() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1);
        assertFalse(list.remove(99));
        assertEquals(1, list.size());
    }

    @Test
    void remove_fromEmptyList_returnsFalse() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertFalse(list.remove(1));
    }

    @Test
    void contains_findsExistingValue() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2);
        assertTrue(list.contains(2));
        assertFalse(list.contains(99));
    }

    @Test
    void iterator_traversesInOrder() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2); list.addLast(3);
        Iterator<Integer> it = list.iterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void iterator_onEmptyList_hasNoElements() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertFalse(list.iterator().hasNext());
    }

    @Test
    void forEach_worksViaIterable() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1); list.addLast(2);
        int sum = 0;
        for (int v : list) {
            sum += v;
        }
        assertEquals(3, sum);
    }
}