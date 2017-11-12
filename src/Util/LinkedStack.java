package Util;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Enumeration;

/**
 * This class implements a stack via linked list.  
 * @author Will Zhao
 * @param <E> The type of elements in this stack.
 */
public class LinkedStack<E> {
    
    private Node<E> base;
    private Node<E> end;
    private Node<E> second;
    private int size;
    
    public LinkedStack() {
        
    }
    
    /**
     * Inserts an element to the top of this stack.
     * @param element The element to insert.
     */
    public void push(E element) {
        if (base == null) {
            base = new Node<>(element);
            ++size;
            return;
        }
        else {
            Node<E> current = base;
            while (current != null) {
                if (current.next == null) {
                    end = null;
                    second = null;
                    end = (second = current).next = new Node<>(element);
                    ++size;
                    return;
                }
                current = current.next;
            }
        }
        throw new Error();
    }
    
    /**
     * Inserts an element to the bottom of this stack.
     * @param element The element to insert.
     */
    public void pushLast(E element) {
        if (base == null) {
            base = new Node<>(element);
            ++size;
            return;
        }
        Node<E> newBase = new Node<>(element);
        newBase.next = base;
        base = null;
        base = newBase;
        ++size;
    }
    
    /**
     * Retrieves but does not remove, the element at
     * the top of this stack.
     * @return The element at the top of this stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public E peek() {
        if (base == null) {
            throw new EmptyStackException();
        }
        if (base.next == null) {
            return base.element;
        }
        return end.element;
    }

    /**
     * Retrieves and removes, the element at
     * the top of this stack.
     * @return The element at the top of this stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public E pop() {
        if (base == null) {
            throw new EmptyStackException();
        }
        if (base.next == null) {
            E element = base.element;   
            base.element = null;
            base = null;
            --size;
            return element;
        }
        E element = end.element;
        end.element = null;
        end = null;
        second.next = null;
        --size;
        return element;
    }
    
    /**
     * Retrieves but does not remove, the element at
     * the bottom of this stack.
     * @return The element at the bottom of this stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public E peekLast() {
        if (base == null) {
            throw new EmptyStackException();
        }
        return base.element;
    }

    /**
     * Retrieves and removes, the element at
     * the bottom of this stack. This method enables 
     * this stack to behave as a queue.
     * @return The element at the bottom of this stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public E popLast() {
        if (base == null) {
            throw new EmptyStackException();
        }
        E element = base.element;
        base.element = null;
        Node<E> next = base.next;
        base.next = null;
        base = next;
        --size;
        return element;
    }

    /**
     * Returns the number of elements in this stack.
     *
     * @return The number of elements in this stack.
     */
    public int size() {
        return size;
    }
    
    /**
     * Determines whether or not this stack contains 
     * any elements.
     * @return {@code true} if this stack does not contain any elements, {@code false}
     * otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Removes all elements from this stack. The stack will 
     * empty when this call returns. 
     */
    public void clear() {
        Node<E> current = base;
        while (current != null) {
            Node<E> next = current.next;
            current.element = null;
            current.next = null;
            current = next;
        }
        base = null;
        size = 0;
    }
    
    /**
     * Returns an {@link Enumeration} of elements of this stack. 
     * The elements are retrieved from the bottom of the stack to the top.
     * @return An {@link Enumeration} of element of this stack, where the elements 
     * are retrieved from the bottom of the stack to the top.
     */
    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            
            Node<E> current = base;
            
            @Override
            public boolean hasMoreElements() {
                return current != null;
            }

            @Override
            public E nextElement() {
                E element = current.element;
                current = current.next;
                return element;
            }
        };
    }

    private static class Node<E> {
        
        private E element;
        private Node<E> next;
        
        private Node(E element) {
            this.element = element;
        }
    }
    
    @Override
    public String toString() {
        String[] data = new String[size];
        int index = -1;
        Node<E> current = base;
        while (current != null) {
            E element = current.element;
            data[++index] = (element == null) ? "null" : element.toString();
            current = current.next;
        }
        return Arrays.toString(data);
    }
    
    public static void main(String... args) {
        LinkedStack<Integer> list = new LinkedStack<>();
        for (int i = 0; i < 3; ++i) {
            int random = (int) (Math.random() * 250); 
            list.push(random);
            System.out.println("Push: " + random);
        }
        System.out.println(list);
        System.out.println("Peek: " + list.peek());
        System.out.println("Pop: " + list.pop());
        System.out.println(list);
        list.push(55);
        System.out.println(list);
    }
}