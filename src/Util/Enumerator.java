package Util;

/**
 * This interface defines the framework for a way to transverse all elements 
 * of a Collection or List, in sequential order without manually 
 * keeping track of an index variable (such as in a for loop). 
 * Like an Iterator this interface is fail-fast, but allows 
 * reseting of the index that it is currently on (while iterating through a List), in other words 
 * this make it possible to <b>reuse the same Enumerator multiple times</b>, instead of making more new Enumerators.
 * In addition this interface provides optional methods for adding and setting elements in a List.
 * 
 * This interface combines the functionality of a Iterator and an Enumeration
 * , and also with a flavor of a ListIterator, since this interface has {@code add} and {@code set} methods.
 * 
 * Note: This interface should only be used by List classes or any classes that <b>extend</b> the 
 * abstract class, <b>(AbstractList)</b> and <b>implement</b> the <b>(List interface)</b>.
 * 
 * @author Will Zhao 
 * @see java.util.Collection
 * @see java.util.List
 * @see java.util.AbstractList
 * @see java.util.ArrayList
 * @see java.util.LinkedList
 * @see java.util.Vector
 * @since JDK 1.8
 * @param <E> Allows for generic handling so that any class can use this interface.
 */

/*
 * Interfaces can extend any amount of interfaces, but cannot implement anything, 
 * such as other interfaces, classes, and abstract classes/
 * Classes can extends 1 class or abstract class and can implement any number of interfaces.

 * All methods a public abstract, even if you dont write it, or they must be defualt

 * All varibes are public static final.
 */
public abstract interface Enumerator<E> extends java.util.Enumeration<E>, java.util.Iterator<E> {
    
    long SERIAL = 23215215151L;
    //always public static final, even if modifiers were not typed
    
    /**
     * Determines whether or not this Enumerator has more elements left.
     * @return True if this Enumerator has more elements left, false otherwise.
     * @deprecated This method has a long name, {@link hasNext()} should be used instead.
     */
    @Deprecated
    @Override
    boolean hasMoreElements();
    
    /**
     * Returns the element at the current implicit cursor of this Enumerator and increments the cursor.
     * (Each time this method is called, this process is repeated, unless a NoSuchElementException is thrown).
     *
     * @return The element of a list at the current implicit cursor of this Enumerator.
     * @throws java.util.NoSuchElementException If this Enumerator has no more elements left.
     * @deprecated This method has a long name, {@link next()} should be used instead.
     */
    @Deprecated
    @Override
    E nextElement();
    
    /**
     * Determines whether or not this Enumerator has more elements left, this method does the same thing as 
     * {@link hasMoreElements()}.
     * @return True if this Enumerator has more elements left, false otherwise.
     */
    @Override
    boolean hasNext();
    
    /**
     * Returns the element at the current implicit cursor of this Enumerator and increments the cursor.
     * (Each time this method is called, this process is repeated, unless a NoSuchElementException is thrown).
     * Note: This method does the same thing as {@link nextElement()}.
     * @return The element of a list at the current implicit cursor of this Enumerator.
     * @throws java.util.NoSuchElementException If this Enumerator has no more elements left.
     */
    @Override
    E next();
    
    /**
     * Returns the index of the element that would be returned by a
     * subsequent call to {@link #next}. (Returns list size if the Enumerator
     * is at the end of the list.)
     *
     * @return The index of the element that would be returned by a
     *         subsequent call to {@code next}, or list size if the Enumerator is at the end of the list
     */
    int nextIndex();
    
    /**
     * Returns the previous index that this Enumerator was on. (Returns -1 if the Enumerator 
     * is at the beginning of the list.
     * @return The previous index that this Enumerator was on, or -1 if this Enumerator is
     * at the beginning of the list.
     */
    int previousIndex();
    
    /**
     * Resets this Enumerator's implicit cursor to the specified argument cursor. 
     * (Optional operation, any class that uses the Enumerator interface does not need to implement or override this method).
     * @param cursor A new cursor index for this Enumerator to reset to, 
     * this allows a single Enumerator Object to be used multiple times.
     * @throws UnsupportedOperationException If this method is not supported by this Enumerator.
     */
    default void setIndex(int cursor) {
        throw new UnsupportedOperationException("setCursor");
    }
    
    /**
     * Removes from the list the last element that was returned by {@link
     * nextElement()} (optional operation). This call can
     * only be made once per call to {@link nextElement()}.
     * It can be made only if {@link add()} has not been
     * called after the last call to {@clink nextElement()}.
     *
     * @throws UnsupportedOperationException If the {@link remove()}
     *         operation is not supported by this Enumerator.
     * @throws IllegalStateException If {@link nextlement()} has not been called, or {@link remove()} or
     *         {@link add()} have been called after the last call to
     *         {@link nextElement()}.
     */
    @Override
    default void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * Replaces the last element returned by {@link #nextElement} 
     * or {@link next}
     * with the specified element (optional operation).
     * This call can be made only if neither {@link #remove} nor {@link
     * #add} have been called after the last call to {@code nextElement} or {@code next} 
     *
     * @param element The element with which to replace the last element returned by
     *          {@code next}.
     * @throws UnsupportedOperationException If the {@code set} operation
     *         is not supported by this Enumerator.
     * @throws ClassCastException If the class of the specified element
     *         prevents it from being added to this list.
     * @throws IllegalArgumentException If some aspect of the specified
     *         element prevents it from being added to this list.
     * @throws IllegalStateException If {@code nextElement} have been called or {@code next}, or if {@code remove} or
     *         {@code add} have been called after the last call to
     *         {@code nextElement} or {@code next()}.
     */
    default void set(E element) {
        throw new UnsupportedOperationException("set");
    }

    /**
     * Inserts the specified element into the list (optional operation).
     * The element is inserted immediately before the element that
     * would be returned by {@link #nextElement()}, if any.  (If the
     * list contains no elements, the new element becomes the sole element
     * on the list.)  The new element is inserted before the implicit
     * cursor: a subsequent call to {@code nextElement()} would be unaffected.
     *
     * @param element The element to insert.
     * @throws UnsupportedOperationException If the {@code add} method is
     *         not supported by this Enumerator.
     * @throws ClassCastException If the class of the specified element
     *         prevents it from being added to this list.
     * @throws IllegalArgumentException If some aspect of this element
     *         prevents it from being added to this list.
     */
    default void add(E element) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public default void forEachRemaining(java.util.function.Consumer<? super E> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        while (hasNext()) {
            action.accept(next());
        }
    }
}