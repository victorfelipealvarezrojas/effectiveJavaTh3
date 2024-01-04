package org.javaEffective.charapter2.item7;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {
    @Test
    public void testMemoryLeak()  {
        Stack stack = new Stack();

        for (int i = 0; i < 500; i++) {
            stack.push(new Object());
        }

        assertEquals(500, stack.getSize());

        while (stack.getSize() > 0) {
            stack.pop();
        }

        for (Object element : stack.getElements()) {
            assertNull(element);
        }

        assertEquals(0, stack.getSize());
    }
}