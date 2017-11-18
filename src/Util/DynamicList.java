package Util;

//import section
import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * This class implements an array that can store any type of object and can grow
 * or decrease its size accordingly by removing or adding elements. This
 * class has <b> very high performance </b> for small sized lists.
 *
 * <li> Note: This class cannot store primitive data types such as: boolean,
 * long, float, int, double, byte, char, short. Instead this class must use
 * their wrapper classes instead to hold those primitive values. This class can
 * hold up to (Integer.MAX_VALUE - 8) number of elements.
 * </li>
 * 
 * <li> 
 * Each DynamicList increments the field modCount every time any change has been detected.
 * </li>
 *
 * @param <E> The type of object that this DynamicList can store. All of the
 * type information in a program with a generic class is examined at compile
 * time. After compilation the type information is erased. This feature of
 * generic classes is known as {@code erasure}. During execution of the program,
 * any attempt at incorrect casting will throw a {@link ClassCastException}.
 */
//a final class is immutable and cannt be extended by a subclass
//a final class can extend other non-final classes, GOOD EX: String
//BEFORE AN OBJECT IS CONSTRCTED, INSTANCE VARIBLES DO NOT EXIST!!!
/*
A class that is final and immutable cannot have its state changed;
For example: URL : http://stackoverflow.com/questions/1783912/java-how-to-use-biginteger

You must uses the assignment "=" operator to change the "value" of a imutae class varible

String str = "sf";
str.toLowerCase();

This code above dOES NOT CHANGE THE STATE of "str" since String is immutable.

to change "str" to lower case we need to override "str" original value replcaing it with a new String.

String str = "sf";
str = str.toLowerCase();

*/
public class DynamicList<E> extends AbstractList<E>
implements List<E>, Cloneable, RandomAccess, Serializable {
//we could just implement Enumerable<E> since Enumerable<E> extends Iterable<E> 

    /**
     * The internal array buffer where all the elements of this DynamicList are
     * stored.
     */
    @java.lang.annotation.Native
    private transient Object elementData[];

    /**
     * The number of elements this DynamicList contains. Must always be less
     * than or equal to {@link capacity}. This instance variable is protected
     * for subclass use.
     */
    private transient int size;

    /**
     * The number that represents how much does this DynamicList grow when its
     * size overflows.
     */
    private transient int capacityIncrement;

    /**
     * The maximum capacity allowed of all DynamicList instances. Each DynamicList may
     * be filled all the way to this capacity. If this limit
     * is exceeded, then an {@link OutOfMemoryError} will be thrown.
     */
    protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
     //this field should be static (static is like for all people)
    //since it can safely and easliy be shared with all instances of this class
    //, DynamicList, without paying the cost of extra memeory.
    //It's like frogs sharing a pond, the pond is the same for all frogs.

    /**
     * The number of {@link DynamicList} objects that were constructed through
     * the DynamicList class constructors.
     */
    private static volatile transient int OBJECT_COUNT;

    static { OBJECT_COUNT = 0; } //static intializer to intialize static varibles
    { capacityIncrement = 10; } //normal intializer

    /**
     * Gets the number of {@link DynamicList} objects that were constructed
     * through the DynamicList class constructors only. Does not include
     * {@code DynamicList} objects that were created from the {@link #clone}
     * method.
     *
     * @return The number of {@code DynamicList} objects that were constructed
     * from constructors only.
     */
    public static final int getInstanceCount() { //static method may be called even if no objects of this class exist yet
        return OBJECT_COUNT;
    }

    /**
     * Registers the native code for this class.
     */
    private static native void registerNatives();

    /**
     * Constructs a empty DynamicList that has no elements in it with a initial
     * capacity of 10. By default this DynamicList will grow by 10 elements each
     * time it overflows.
     */
    public DynamicList() {
        this(10); //call to this or super type constructors must be on the first line.
    }

    /**
     * Constructs a empty DynamicList that has no elements in it with a
     * specified initial capacity. By default this DynamicList will grow by 10
     * elements each time it overflows.
     *
     * @param initialCapacity A specified initial capacity for this DynamicList.
     * If this value is negative a {@link NegativeArraySizeException} will be
     * thrown.
     */
    public DynamicList(int initialCapacity) {
        this(initialCapacity, 10);
    }

    /**
     * Constructs an empty DynamicList with a specified initial capacity and
     * capacity increment value.
     *
     * @param initialCapacity The initial capacity value of this DynamicList.
     * @param capacityIncrement The initial amount by which the capacity is
     * increased when this DynamicList overflows. If this specified value is
     * less than 1 it will default to 10.
     * @throws NegativeArraySizeException If the specified initial capacity is
     * negative.
     * @throws IllegalArgumentException If the specified initial capacity is too
     * large.
     */
    public DynamicList(int initialCapacity, int capacityIncrement) {
        super(); //implict
        if (initialCapacity < 0) {
            throw new NegativeArraySizeException("The specified starting capacity: " + initialCapacity + " is negative.");
        }
        if (initialCapacity > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("The specified starting capacity: " + initialCapacity + " is greater than\nthe maximum allowed capacity limit: " + MAX_ARRAY_SIZE + ".");
        }
        elementData = new Object[initialCapacity];
        size = 0; //we set the capcity but not size
        this.capacityIncrement = (capacityIncrement < 1) ? 10 : capacityIncrement;
        modCount = 0;
        ++DynamicList.OBJECT_COUNT;
    }

    public DynamicList(Collection<? extends E> c) {
        this(Objects.requireNonNull(c, "The requested collection is null."), 10);
        //require non null in constructor so the call to collection size 
        //will never throw an exception, rather the exception will be caught before.
    }

    public DynamicList(Collection<? extends E> c, int capacityIncrement) {
        super(); //calls super class constrctor, which does nothing
        Objects.requireNonNull(c, "The requested collection is null.");
        try {
            elementData = c.toArray();
        }
        catch (ClassCastException | ArrayStoreException ex) { //not needed, just in case an exception pops up
            Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
            throw ex; //rethrow the caught exception
        }
        size = c.size();
        this.capacityIncrement = (capacityIncrement < 1) ? 10 : capacityIncrement;
        modCount = 0;
        ++OBJECT_COUNT;
    }

    /**
     * This safety method checks the internal status of this DynamicList and
     * ensures that it cannot be {@code null}, if this DynamicList is
     * {@code null} a {@link NullPointerException} is thrown. If the mod count
     * of this DynamicList overflows, (becomes less than zero), a
     * {@link IllegalStateException} will be thrown and the mod count will be
     * reset to zero. It is up to the user to catch and handle any exceptions
     * thrown by this method.
     *
     * @throws NullPointerException If this DynamicList and/or its internal
     * array is {@code null}.
     */
    private void selfCheck() throws IllegalStateException, NegativeArraySizeException, NullPointerException, OutOfMemoryError, InternalError {
        //other methods that call this method do not need a "try catch" statement since IllegalStateException
        //is a RuntimeException, also InternalError CANNOT be caught since it is an Error or a 
        //subclass of Error, which any type error cannot be catched.
        synchronized (this) { //lock on, sychronize a list of operations, not just every individual
            Objects.requireNonNull(elementData, "This DynamicList's internal array buffer is null.");
            if (elementData.length > MAX_ARRAY_SIZE) { //use varible size since this methd is invoked in size()
                throw new OutOfMemoryError("The capacity of this DynamicList: " + elementData.length + " exceeds the maximum allowed limit: " + MAX_ARRAY_SIZE + ".");
            }
            if (elementData.length < 0) {
                throw new NegativeArraySizeException("The capacity: " + elementData.length + " of this DynamicList is negative.\nThis was likely an array store overflow error.");
            }
            if (size > elementData.length) {
                throw new InternalError("The size of this DynamicList: " + size + " exceeds its capacity: " + elementData.length);
            }
        }
    }

    /**
     * Sets the capacity increment of this DynamicList. Whenever the size of
     * this DynamicList reaches the capacity the DynamicList will increase its
     * capacity according to the specified argument. If this method has not been
     * called however, this DynamicList will grow by a default value. It is
     * recommended to call this method before adding many elements to this
     * DynamicList since this method will reduce incremental reallocation when
     * adding many elements.
     *
     * @param capacityIncrement A new capacity increment value for this
     * DynamicList.
     * @throws IllegalArgumentException If the specified capacity increment is
     * less than 1.
     * @see getCapacityIncrement()
     */
    public void setCapacityIncrement(int capacityIncrement) throws IllegalArgumentException { //illegal arg not needed
        selfCheck();
        if (capacityIncrement < 1) {
            throw new IllegalArgumentException("The requested capacity increment value: " + capacityIncrement + " is less than 1.");
        }
        if (this.capacityIncrement == capacityIncrement) {
            return;
        }
        this.capacityIncrement = capacityIncrement;
        ++modCount;
    }

    /**
     * Gets the capacity increment value of this DynamicList.
     *
     * @return The capacity increment value of this DynamicList.
     * @see setCapacityIncrement(int capacityIncrement)
     */
    public int getCapacityIncrement() {
        return capacityIncrement;
    }

    /**
     * Increases the capacity of this DynamicList, if necessary, to ensure that
     * it can hold at least the number of elements specified the minimum
     * capacity argument. The capacity of this DynamicList will be equal to the
     * minimum capacity argument. However if the requested minimum capacity is
     * less than or equal to this DynamicList's current capacity, or if the
     * capacity of this DynamicList is already at the maximum allowed capacity,
     * this method returns immediately (does nothing).
     *
     * @param minCapacity The desired minimum capacity.
     */
    @SuppressWarnings("UnusedAssignment")
    public void ensureCapacity(int minCapacity) {
        selfCheck();
        if (elementData.length == MAX_ARRAY_SIZE) {
            return;
        }
        if (minCapacity > elementData.length) {
            //Object grow[] = new Object[minCapacity + capacityIncrement];
            if (minCapacity > MAX_ARRAY_SIZE || minCapacity < 0) {
                minCapacity = MAX_ARRAY_SIZE;
            }
            Object grow[] = new Object[minCapacity];
            System.arraycopy(elementData, 0, grow, 0, elementData.length);
            elementData = null;
            elementData = grow;
            grow = null;
            ++modCount;
        }
    }

    /**
     * Sets the size of this DynamicList.
     * @param size A new size for this DynamicList.
     * @throws IllegalArgumentException If the requested size is negative or greater than the maximum allowed 
     * capacity.
     */
    @SuppressWarnings("UnusedAssignment")
    public void setSize(int size) {
        this.selfCheck();

        if (size > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("The requested size: " + size + " exceeds maximum allowed limit: " + MAX_ARRAY_SIZE + ".");
        }

        if (size < 0) {
            throw new IllegalArgumentException("The requested size: " + size + " is negative.");
        }

        while (size == this.size) {
            return;  //no change needed...
        }

        if (size == elementData.length || ((size < elementData.length) && (size > this.size))) {
            this.size = size;
            ++modCount;
            return;
        }

        if (size < this.size) { //if size arg less than current size, also must be less than capacity
            for (int index = size; index < this.size; index++) {
                elementData[index] = null; //padd with nulls for gc
            }
            this.size = size;
            ++modCount;
            return;
        }

        if (size > elementData.length) { //grow, request exceeds capacity
            int length = size + capacityIncrement;
            if (length < 0 | length > MAX_ARRAY_SIZE) {
                length = MAX_ARRAY_SIZE;
            }
            Object[] grow = new Object[length]; // plus capacity increment, for the buffer zone
            System.arraycopy(elementData, 0, grow, 0, this.size);
            elementData = grow;
            grow = null;
            this.size = size;
            ++modCount;
            return;
        }
        
        throw new UnsupportedOperationException(); //should never happen
    }

    /*
     public void garbageCollect() {
     System.gc();
     }
     */
    
    /**
     * This method is automatically called by this DynamicList when it needs to
     * increase it's capacity.
     */
    @SuppressWarnings("UnusedAssignment")
    private void grow() {
        //[12, 34, 64, 75, 775] null null
        // 0   1   2   3    4
        //size = 5
        selfCheck();
        if (elementData.length == MAX_ARRAY_SIZE) {
            return; //cannot grow anymore
        }
        if (size == elementData.length) { //maybe not -1
            int length = elementData.length + capacityIncrement;
            if (length < 0 || length > MAX_ARRAY_SIZE) { //case ov exceeds limit and possible overflow
                length = MAX_ARRAY_SIZE;
            }
            Object grow[] = new Object[length];
            System.arraycopy(elementData, 0, grow, 0, size);
            elementData = grow; //auto matic change, so no modCount incremnt
            grow = null;
        }
    }

    /**
     * Returns true if this DynamicList needs to grow. This method was
     * implemented so that the DynamicList methods do not automatically call
     * {@link #grow}. If this DynamicList's size reaches the threshold, this
     * method will return false.
     *
     * @return True if this DynamicList needs to grow, false otherwise.
     */
    private boolean needToGrow() {
        selfCheck();
        return size == MAX_ARRAY_SIZE ? !true : size == elementData.length;
    }

    /**
     * Appends a object to the end of this DynamicList and returns true if this
     * was successfully done, false otherwise.
     *
     * @param element The object to be appended to the end if this DynamicList.
     * @return True if the specified object was appended to this DynamicList ,
     * false otherwise.
     *
     * @see add(int index, E element)
     */
    @Override
    public boolean add(E element) {
        selfCheck();
        if (size == MAX_ARRAY_SIZE) {
            return false;
        }
        //System.out.println("Before: " + Arrays.toString(elementData) + "capacity: " + elementData.length +" Size:" + this.size);

        if (needToGrow()) {
            grow();
        }

        //System.out.println(Arrays.toString(elementData));
        elementData[size++] = element;
        //System.out.println("After: " + Arrays.toString(elementData) + "capacity: " + elementData.length +" Size:" + this.size + "\n");

        modCount = modCount + 1;
        return true;
    }

    /**
     * Inserts a object at a specified index position in this DynamicList.
     *
     * @param element The element to be inserted at a specified index position
     * in this DynamicList.
     * @param index A specified index position where the specified element will
     * be inserted at.
     *
     * @see add(E element)
     */
    @Override
    @SuppressWarnings("UnusedAssignment")
    public void add(int index, E element) {
        this.selfCheck(); //counselor will get to the ap cs 2 sechudlue change asap
        rangeCheckForAdd(index);
        switch (size()) {
            case MAX_ARRAY_SIZE: {
                return;
            }
            default: {
                break;
            }
        }

        if (needToGrow()) {
            grow();
        }

        if (index == size()) {
            elementData[size++] = element;
            ++modCount;
            return;
        }

        int originalIndex = 0; //the index used to go through old array
        int allocateIndex = 0; //the index used to allocate into new array
        Object[] grow = new Object[elementData.length];
        //grow method already used!!!
        //reduces buffer zone, rather than incrementing capacity.

        while (originalIndex < index) {
            grow[allocateIndex++] = elementData[originalIndex++];
        }

        grow[index] = element;
        ++allocateIndex;

        for (originalIndex = index; originalIndex < size();) {
            grow[allocateIndex++] = elementData[originalIndex++];
        }

        elementData = null;
        elementData = grow;
        grow = null; //dump temporary array, which wil be done 
        //automaitically by the compiler, since all local varibles
        //are destoyed once methods complete their execution.
        ++size;
        modCount++;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        this.selfCheck();
        Objects.requireNonNull(c, "The specified collection is null.");
        boolean modified = false;
        for (Iterator<? extends E> itr = c.iterator(); itr.hasNext();) {
            if (add(itr.next())) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.selfCheck();
        Objects.requireNonNull(c, "The specified collection is null.");
        rangeCheck(index);
        boolean modified = false;
        for (E each : c) { //the original index arguement will be unaffected, since primitive, a copy is made
            add(index++, each); //increments modCount!
            modified = true;
        }
        return modified;
    }

    /**
     * Removes the object at the specified index position in this DynamicList
     * and left shifts the existing objects after that index by 1.
     *
     * @param index A specified index indicating which object to remove.
     * @return The object that was removed.
     */
    @Override
    @SuppressWarnings("UnusedAssignment")
    public E remove(final int index) {
        selfCheck();
        rangeCheck(index);

        final E removedElement = get(index);
        elementData[index] = null; //let gc clear it

        Object[] without;
        without = new Object[elementData.length]; //no need to decrease buffer zone 
        //user must call trimToSize

        int originalIndex = 0; //the index used to go through old array
        int allocateIndex = 0; //the index used to allocate into new array

        //get all elements before removed element
        while (originalIndex < index) {
            without[allocateIndex++] = elementData[originalIndex++];
        }

        originalIndex = index + 1;

        //get all elements after removed element
        for (; originalIndex < size;) {
            without[allocateIndex++] = elementData[originalIndex++];
        }
        //update operations
        elementData = null;
        elementData = without;
        without = null; //no need anymore
        --size;
        ++modCount;

        return removedElement;
    }

    /**
     * Removes the first appearance of the specified object in this DynamicArray
     * and left-shifts the existing objects after its first appearance by 1. If
     * this DynamicList does not contain the specified element, this method does
     * nothing and returns false. Note that this method may be confused by the
     * compiler for the {@link remove(int index)} method when dealing with
     * primitives and their respective wrapper classes. To counter this problem
     * use the {@link removeFirst(Object obj)} method which does the same thing
     * as this method.
     *
     * @param obj The object to be removed from this DynamicList.
     * @return True if the specified object was removed, false otherwise.
     * @see remove(int index)
     * @see removeFirst(Object obj)
     * @see removeLast(Object obj)
     */
    @Override
    public boolean remove(final Object obj) {
        return removeFirst(obj);
    }

    /**
     * Removes from this DynamicList the first index of the specified object, if
     * such element exists. This implementation gets an {@link Iterator} over
     * this DynamicList and uses it's {@code remove} method.
     *
     * @param obj A specified object to be removed from this DynamicList.
     * @return True if the specified object was removed, false otherwise.
     */
    public boolean removeFirst(final Object obj) {
        selfCheck();
        if (this.equals(obj)) {
            return false;
        }
        if (obj == null) {
            for (Iterator<E> itr = iterator(); itr.hasNext();) {
                if (itr.next() == null) {
                    itr.remove(); //increments modCount
                    return true;
                }
            }
            return false;
        }
        else {
            for (Iterator<E> itr = iterator(); itr.hasNext();) {
                if (obj.equals(itr.next())) {
                    itr.remove();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Removes the last appearance of the specified object in this DynamicArray
     * and left-shifts the existing objects after its last appearance by 1. If
     * this DynamicList does not contain the specified element, this method does
     * nothing and returns false.
     *
     * @param obj The object to be removed from this DynamicList.
     * @return True if the specified object was removed, false otherwise.
     * @see remove(int index)
     * @see remove(Object obj)
     * @see removeFirst(Object obj)
     */
    public boolean removeLast(final Object obj) {
        selfCheck();
        if (equals(obj)) {
            return false;
        }
        ListIterator<E> backwards = listIterator(size());
        if (obj == null) { //null check with 2 loops, is actually faster, since we check for nullness once 
            while (backwards.hasPrevious()) {
                if (backwards.previous()== null) {
                    backwards.remove();
                    return true;
                }
            }
        }
        else {
            while (backwards.hasPrevious()) {
                if (obj.equals(backwards.previous())) {
                    backwards.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        selfCheck();
        Objects.requireNonNull(c);
        return super.removeAll(c); //increments modCount
    }

    /**
     * Removes an object at a specified index position in this DynamicList, this
     * method is faster than {@link #remove} since this method does not return
     * the removed object. Note that this method is used by the
     * {@code Iterators} and {@code ListIterators} of this class. The
     * {@link #remove(int)} method is not used, unlike other {@link List}
     * implementations.
     *
     * @param index A specified index position where the object will be removed.
     */
    @SuppressWarnings("UnusedAssignment")
    public void fastRemove(int index) {
        selfCheck();
        rangeCheck(index);

        elementData[index] = null; //let gc clear it

        Object[] without;
        without = new Object[elementData.length];

        int originalIndex = 0; //the index used to go through old array
        int allocateIndex = 0; //the index used to allocate into new array

        //get all elements before removed element
        while (originalIndex < index) {
            without[allocateIndex++] = elementData[originalIndex++];
        }

        originalIndex = index + 1;

        //get all elements after removed element
        for (; originalIndex < size;) {
            without[allocateIndex++] = elementData[originalIndex++];
        }
        //update operations
        elementData = null;
        elementData = without;
        without = null; //no need anymore, will be gc'd
        --size;
        ++modCount;
    }

    /**
     * Removes all elements starting at a start index an up to but not including
     * a end index in this DynamicList.
     *
     * @param start A specified start index where the first element will be
     * removed.
     * @param end The last element to be removed from this operation is right
     * before this index.
     */
    public final void batchRemove(int start, int end) {
        selfCheck();
        if (start < 0 || start > size || end < 0 || end > size) {
            throw new IllegalArgumentException("The requested start and/or end indexes are out of bounds.");
        }
        int cnt = 0; // 1 2 5 6 4 3 5 3
        for (Enumerator<E> e = enumerator(start); e.hasNext(); ++cnt) {
            if (cnt == (end - start)) {
                return; //end when count has been reached
            }
            e.next(); //must call this method before remove
            e.remove(); //since remove removes the last element returned by next()

               //++count; //we could increment count here...
            //but since count is incrmented automatically
            //per finished iteration, it is OK to 
            //increment count in the loop.
        }

        /*
         IS equal to:

         Enumerator<E> e = enumerator(start);  
         int count = 0;
         while (e.hasNext()) {
         if (count == (end - start)) {
         return; //end when count has been reached
         }
         e.next(); 
         e.remove();
         ++count;
         }
         */
    }

    /**
     * Removes all elements starting at a start index and up to but not
     * including a end index in this DynamicArray.
     * <li>
     * Note this method does calls {@link #batchRemove}.
     * </li>
     *
     * @param fromIndex {@inheritDoc}
     * @param toIndex {@inheritDoc}
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        batchRemove(fromIndex, toIndex);
    }

    public void removeAll(final Object obj) {
        this.selfCheck();
        int index = indexOf(obj);
        //this approach offers a direct shot at 
        //the indexes of element, if any.
        while (index >= 0) {
            //int lastRet = index; //could use the last index
            fastRemove(index); //incerements modCount
            index = indexOf(obj); //reupdate
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        selfCheck();
        Objects.requireNonNull(filter);
        return super.removeIf(filter); //increments modCount
    }

    /**
     * Removes from this DynamicList all {@code null} values or references.
     */
    public void removeAllNull() {
        selfCheck();
        /*
         int length = elementData.length;
         for (int index = 0; index < length; index++) {
         if (elementData[index] == null) {
         this.fastRemove(index--); //same as remove(i); then i--; increments modCOunt
         length = elementData.length; // must update length manually!!!
         }
         }
         */
        for (Iterator<E> itr = iterator(); itr.hasNext();) {
            if (itr.next() == null) {
                itr.remove();
            }
        }
    }

    /*
     public Class<?> DynamicListClass() {
     return getClass(); //cannot overide Object.getClass() since it is fina method..
     }
     */
    /**
     * Replaces the current existing object at the specified index of this
     * DynamicList with another object and returns the original object that was
     * replaced.
     *
     * @param index A specified index position where the existing object will
     * replaced with another object.
     * @param element The object to replace the currently existing object.
     * @return The original object that was replaced.
     */
    @Override
    public E set(final int index, final E element) {
        selfCheck();
        rangeCheck(index);
        E old = (E) elementData[index];
        elementData[index] = null;
        elementData[index] = element;
        ++modCount;
        return old;
    }

    /**
     * Replaces the current existing object at the specified index of this
     * DynamicList with another object. This method does the same thing as
     * {@link set} but is faster since it does not return the original object
     * that was replaced.
     *
     * @param index A specified index position where the existing object will
     * replaced with another object.
     * @param element The object to replace the currently existing object.
     */
    public void quickSet(int index, E element) {
        selfCheck();
        rangeCheck(index);

        elementData[index] = null; //destroy the element
        elementData[index] = element;
        ++modCount;
    }

    /**
     * Replaces all {@code null} values or references with a specified object,
     * which must not be {@code null}.
     *
     * @param element A specified {@code non-null} object to replace all
     * {@code null} values or references.
     * @throw NullPointerException If the specified object is {@code null}.
     */
    public final void replaceAllNull(E element) {
        selfCheck();
        Objects.requireNonNull(element, "The requested element is null.");
        for (int i = 0; i < size(); i++) {
            if (isNull(i)) {
                quickSet(i, element);
            }
        }
    }

    /**
     * Replaces all appearances of an old object with a different one. Note that
     * this method accepts {@code null} arguments.
     *
     * @param oldElement An old object to have all of its appearances in this
     * DynamicList replaced.
     * @param newElement An different object to replace all appearances of the
     * old object in this DynamicList.
     * @see replaceAllNull(E element)
     * @see replaceAll(UnaryOperator operator)
     */
    public final void replaceAll(E oldElement, E newElement) {
        selfCheck();
        boolean oldNull = (oldElement == null);
        boolean newNull = (newElement == null);
        if (oldNull && newNull) {
            return; //no need to replace null with null.
        }
        if (oldNull && !newNull) {
            replaceAllNull(newElement); //replace all nulls with new element
            return;
        }
        if (!oldNull && newNull) {
            for (int i = 0; i < size(); i++) {
                if (!isNull(i) && get(i).equals(oldElement)) {
                    quickSet(i, null); //newElement is null
                }
            }
            return;
        }
        if (!oldNull && !newNull) {
            for (int i = 0; i < size(); i++) {
                if (!isNull(i) && get(i).equals(oldElement)) {
                    quickSet(i, newElement);
                }
            }
            return;
        }
        throw new ThreadDeath(); //should never happen
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        this.selfCheck();
        Objects.requireNonNull(operator);
        super.replaceAll(operator); //increments modCount
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        selfCheck();
        Objects.requireNonNull(c, "The requested collection is null.");
        return super.retainAll(c); //increments modCount
    }

    /**
     * Returns the object at the specified index position of this DynamicList.
     *
     * @param index A specified index position in this DynamicList where the
     * object will be returned.
     * @return The object at the specified index position.
     * @throws IndexOutOfBoundsException If the requested index is out of
     * bounds.
     */
    @Override
    public E get(final int index) {
        selfCheck();
        rangeCheck(index);
        return (E) elementData[index];
    }

    private void rangeCheck(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("The requested index: [" + index + "] is negative.");
        }
        if (index == size()) {
            RuntimeException ex = new IndexOutOfBoundsException("The requested index: [" + index + "] is equal to\nthe size of this DynamicList: [" + size() + "]."); //could throw NoSuchElementException
            throw ex; //will throw IndexOutOfBounds not RuntimeException
            //ex will be treated as an RuntimeException when being modified, known as Dynamic binding
        }
        if (index > size()) {
            throw new IndexOutOfBoundsException("The requested index: [" + index + "] is greater than\nthe size of this DynamicList: [" + size() + "].");
        }
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("The requested index: [" + index + "] is negative.");
        }
        if (index > size()) {
            throw new IndexOutOfBoundsException("The requested index: [" + index + "] is greater than\nthe size of this DynamicList: [" + size() + "].");
        }
    }

    /**
     * Returns {@code true} if the element at the specified index is
     * {@code null}, {@code false} otherwise. This is a convenience method.
     *
     * @param index A specified index where the element in this DynamicList will
     * be tested for {@code null}.
     * @return True if the element at the specified index is {@code null}, false
     * otherwise.
     */
    public final boolean isNull(int index) { //cannot be overriden in subclasses
        
        return null == get(index);
    }
    /**
     * Gets the number of elements in this DynamicList.
     *
     * @return The number of elements in this DynamicList.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Determines whether this DynamicList is empty or not.
     *
     * @return True if this DynamicList is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        //List.super.isEmpty(); //cannot call List.super's isEmpty method since it is abstract
        return size == 0;
        //return size() == 0; //size() has selfCheck
        //could call super.isEmpty() since it calls size(), and size() is overriden here
    }
    
    /**
     * Counts the number of objects in this DynamicList that are equal to the 
     * specified object. 
     * @param element The specified element that will be counted for by this method.
     * @return The number of objects in this DynamicList that are equal to the
     * specified object. Returns the number of {@code null} elements in this
     * DynamicList, if the specified element is {@code null}.
     * @see countNull() 
     */
    public int count(E element) { //could be named getCountOf
        return (element == null) ? countNull() : countElementRecur(element, 0, 0); //== test for reference equality
    }
    
    private int countElementRecur(E element, int index, int count) {
        if (index >= size()) {
            return count;
        }
        if (elementData[index] != null && element.equals(elementData[index])) {
            ++count; //checl for null first, since the if statement will fail immediately if found a null
        }
        ++index;
        return countElementRecur(element, index, count); //make a recursive call
    }

    /**
     * Counts the number of {@code null} references in this DynamicList.
     * This is a convenience method.
     * @return The number of {@code null} references in this DynamicList.
     * @see count(E element)
     */
    public int countNull() {
        int count = 0;
        for (int index = 0; index < size(); ++index) {
            if (elementData[index] == null) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Removes all elements from this DynamicList, this DynamicList will be
     * empty after this call returns. The capacity of this DynamicList will
     * remain unchanged by this operation.
     */
    @Override
    public void clear() {
        selfCheck();
        removeAllElements();
    }

    //helper method
    private void removeAllElements() { //no security check 
        for (int i = 0; i < size; i++) {
            elementData[i] = null; //let garbage collector work
        }
        size = 0; //change size after null operation
        ++modCount;
    }
    
    private boolean isSorted(List<Integer> list) {
        for (int outer = 0; outer != list.size(); ++outer) {
            int before = list.get(outer);
            for (int inner = outer + 1; inner != list.size(); ++inner) {
                int after = list.get(inner);
                if (before > after) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void quickSort(List<Integer> list, int index) {
        if (isSorted(list)) {
            return;
        }
        int half = list.size() / 2;
        
        
        
        int current = list.get(index);
        
        for (int start = index + 1; start < list.size(); ++start) {
            if (current > list.get(start)) {
                list.set(index, list.get(start));
                list.set(start, current);
            }
        }
        
        quickSort(list.subList(0, half), index + 1);
        quickSort(list.subList(half, list.size()), index + 1);
        
    }
    

    /**
     * Reduces this DynamicList size and capacity to zero. This DynamicList will
     * empty after this call returns. Note that for performance this method does not call any other methods
     * and should be overridden in any sub class.
     */
    public void wipe() {
        selfCheck();
        for (int index = 0; index < size; index++) {
            elementData[index] = null; //garbage collect all elements
        }
        elementData = null; //dump it
        elementData = new Object[]{null}; //reset to zero capacity
        size = 0; //size back to zero
        ++modCount;
    }

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    final public void finalize() throws Throwable {
        try {
            super.finalize();
            synchronized (this) {
                wipe();
            }
        }
        catch (Throwable ex) {
            throw new InternalError(ex);
        }
        finally {
            System.runFinalization();
            Runtime.getRuntime().gc();
        }
    }
    
    /**
     * Concatenates any number of Lists to the end of this DynamicList.
     * @param lists The Lists to be concatenated to the end if this DynamicList.
     */
    public void concat(List<E>... lists) {
        for (List<E> each : lists) {
            addAll(each);
        }
    }
    
    public void removeLists(List<E>... lists) {
        for (List<E> each : lists) {
            removeAll(each);
        }
    }
    
    public boolean equals(List<E>... lists) {
        selfCheck();
        boolean allEqual = false;
        for (List<E> each : lists) {
            if (equals(each)) {
                allEqual = true;
            }
        }
        return allEqual;
    }

    /**
     * Gets the maximum number of elements that this DynamicList can currently
     * store.
     *
     * @return The maximum number of elements that this DynamicList can
     * currently store.
     */
    public int capacity() {
        return elementData.length;
    }

    /**
     * Trims this DynamicList, the capacity of this DynamicList will be reduced
     * to the size of this DynamicList. This method is used to save memory
     * space.
     */
    public void trimToSize() {
        selfCheck();
        if (size < elementData.length) {
            elementData = Arrays.copyOf(elementData, size());
            modCount++; //increment only if modified, conservative increment
        }
    }
    
    /**
     * Sorts all the elements in this DynamicList according to the specified {@link Comparator}.
     * The return type is of {@link BigInteger} since the times that it takes for this method 
     * to sort can be extremely high, even above the scope of {@link Long}.
     * @param c The specified {@code Comparator} to sort by.
     * @return The number of times it took to sort this DynamicList.
     * @deprecated This method should only be used for testing or for demonstration purposes 
     * since it is very slow. Practically impractical for real world use.
     */   
    public final BigInteger bogoSort(Comparator<? super E> c) {
        BigInteger count = BigInteger.ZERO;
        while (!isSorted(c)) {
            Collections.shuffle(this);
            count = count.add(BigInteger.ONE);
        }
        return count;
    }
    
    /**
     * Return true if this DynamicList is sorted according to the specified {@link Comparator},
     * false otherwise. This method is used for {@link #bogoSort} method.
     * @param c The specified {@code Comparator} to sort by.
     * @return {@code true} if this DynamicList is sorted according 
     * to the specified {@code Comparator}, {@code false} otherwise.
     * @deprecated It is generally not worth calling this method since it takes time 
     * to determine whether this DynamicList is sorted or not.
     */
    final public boolean isSorted(Comparator<? super E> c) {
        for (int outer = 0; outer < size(); ++outer) {
            for (int inner = outer + 1; inner < size(); ++inner) {
                if (c.compare(get(outer), get(inner)) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the first index of the specified object in this DynamicList,
     * returns -1 if this DynamicList does not have the specified object.
     *
     * @param obj The specified object to have its index in this DynamicList
     * returned by this method.
     * @return The first index of the specified object in this DynamicList, or
     * -1 if this DynamicList does not have the specified object.
     */
    @Override
    public int indexOf(final Object obj) {
        return indexOf(obj, 0);
        //return indexOfHelper(obj, 0);
    }

    /**
     * Returns the first index of the specified object in this DynamicList,
     * starting from a specified start index. This method returns -1 if this
     * DynamicList does not have the specified object after o
     *
     * @param obj The specified object to have its index in this DynamicList
     * returned by this method.
     * @param start The start index where this method will start searching for
     * the specified object's index in this DynamicList.
     * @return The first index of the specified object in this DynamicList,
     * starting from a specified start index, or -1 if this DynamicList does not
     * have the specified object.
     */
    public int indexOf(final Object obj, int start) { //start is copy, ma be changed
        rangeCheck(start);
        if (obj == null) {
            while (start < size()) {
                if (elementData[start] == null) {
                    return start;
                }
                ++start;
            }
        }
        else {
            while (start != size()) {
                if (obj.equals(elementData[start])) {
                    return start;
                }
                ++start;
            }
        }
        return -1;
        //int index = indexOfHelper(obj, startIndex);
        //return index; 
        //dont call helper method since overriding classes may not implement 
        //the Iterator or Enumerable or ListIterator interfaces but that is impossible as if you extends or override this class
        //you must implement the interfaces that this class does as well, 
        //but the methods of this class are "inherted" to the subclass, still manually is the best way here..
    }

    //helper method 
    private int indexOfHelper(final Object obj, int startIndex) {
        selfCheck();
        rangeCheck(startIndex);
        /*
         while (startIndex < 0 || startIndex >= size()) { //culd use a while loop, since it terminates by throwing exception
         throw new IndexOutOfBoundsException("The requested index: " + startIndex + " is out of bounds.");
         }
         */
        /* We could do this since a list should never have itself inside itself.
         if (equals(obj)) {
         return -1;
         }
         */
        Enumerator<E> e = enumerator(startIndex);
        if (obj == null) {
            while (e.hasNext()) {
                if (e.next() == null) {
                    return e.previousIndex();
                }
            }
            return -1;
        }
        else {
            while (e.hasNext()) {
                if (obj.equals(e.next())) {
                    return e.previousIndex();
                }
            }
            return -1;
        }
    }

    @Override
    @SuppressWarnings("null")
    public int lastIndexOf(Object obj) {
        int index = 0;
        int count = 0;
        
        if (obj == null) {
            while (index < size()) {
                if (elementData[index] == null) {
                    ++count;
                }
                ++index;
            }
            for (index = 0; index < size(); ++index) {
                if (elementData[index] == null) { //fastest
                    if (count == 1) {
                        return index;
                    }
                    --count;
                }
                /* clearer but slower since we always decrement the count
                if (foundNull) {
                    --count;
                    if (count == 0) {
                        return index;
                    }
                }
                */
                
                /*long way:
                if (count == 1 && foundNull) {
                    return index;
                }
                
                if (fountNull) {
                    --count;
                }            
                */
            }
            return -1;
        }

        else {
            while (index < size()) {
                if (elementData[index] != null && obj.equals(elementData[index])) {
                    ++count;
                }
                ++index;
            }
            for (index = 0; index < size(); ++index) {
                if (elementData[index] != null && obj.equals(elementData[index])) { //this way is clearer
                    --count;
                    if (count == 0) {
                        return index;
                    }
                }
            }
            return -1;
        }

        //return lastIndexOf(obj, size() - 1);
    }

    /**
     * Returns the last index of the specified object in this DynamicList,
     * starting backwards from a specified starting index.
     *
     * <li>
     * Note: If the specified object was not of type compatible with this
     * DynamicList, this method always returns -1.
     * </li>
     *
     * <li>
     * Note: This method works only if a {@code equals} method inherited from
     * Class {@link Object} has been properly overridden.
     * </li>
     *
     * @param obj This object that will have its last index returned by this
     * method, (starting backwards from a start index), -1 if this DynamicList
     * does not have it.
     *
     * @param end A specified starting index where this method will iterate from
     * it backwards.
     *
     * @return The last index of the specified object starting backwards from a
     * end index, returns -1 if this DynamicList does not have the specified
     * object.
     *
     * @see indexOf(Object obj)
     * @see indexOf(Object obj, int startIndex)
     * @see lastIndexOf(Object obj, int startIndex)
     * @see contains(Object obj)
     */
    public int lastIndexOf(final Object obj, int end) {
        rangeCheck(end);
        if (obj == null) {
            while (end >= 0) {
                if (elementData[end] == null) {
                    return end;
                }
                --end;
            }
            return -1;
        }
        else {
            while (end >= 0) {
                if (obj.equals(elementData[end])) { //elementData[end] could be null, so we use obj.equals
                    //instead of elementData[end].equals, since we checked that obj cannot be null here, look above
                    return end;
                }
                --end;
            }
            return -1;
        }
        //return this.lastIndexOfHelper(obj, endIndex);
    }

    private int lastIndexOfHelper(final Object obj, int endIndex) {
        selfCheck();

        /* We thertically could do this, since a list should not have itself inside itself!!!
         if (equals(obj)) {
         return -1;
         }
         */
        rangeCheck(endIndex);

        ListIterator<E> li = listIterator(endIndex + 1);
        //System.gc();
        if (obj == null) {
            while (li.hasPrevious()) {
                if (li.previous() == null) {
                    return li.nextIndex();
                }
            }
            return -1;
        }
        else {
            while (li.hasPrevious()) {
                if (obj.equals(li.previous())) { //use obj since it will never be null
                    return li.nextIndex();
                }
            }
            return -1;
        }

           //we directly 
        //use endIndex since endIndex is a COPY of the autal index arguement that gets passed 
        //in, so if we change the it in this method, it will remain the same outside of the method
           /*
         if (obj == null) {
         for (; endIndex >= 0;) {
         if (get(endIndex) == null) {
         return endIndex;
         }
         endIndex--;
         }
         return -1;
         }
         else {
         while (endIndex >= 0) {
         if (obj.equals(get(endIndex))) {
         return endIndex;
         }
         endIndex--;
         }
         return -1;
         }
         */
    }

    /**
     * Returns true if this DynamicList contains a specified object, false
     * otherwise. For efficiency this method does not call {@link #indexOf(Object obj)}.
     *
     * @param obj The object to be searched for by this method.
     * @return {@code true} if this DynamicList contains the specified object,
     * {@code false} otherwise.
     */
    @Override
    public boolean contains(Object obj) {
        return contains(obj, 0);
        /*
         Iterator<E> itr = iterator();
         if (obj == null) {
         while (itr.hasNext()) {
         if (itr.next() == obj) {
         return true;
         }
         }
         return false;
         }
         else {
         while (itr.hasNext()) {
         if (obj.equals(itr.next())) {
         return true;
         }
         }
         return false;
         }
         */
           //boolean contains = containsHelper(obj, 0); //could do this, LOL
        //return (contains) ? true : false;
    }

    /**
     * Returns true of this DynamicList contains an specified object starting
     * from a start index, false otherwise.
     *
     * @param obj The object to be searched for starting from a start index.
     * @param start The specified start index where this method will start
     * searching for the specified object.
     * @return {@code true} if this DynamicList contains the specified object
     * starting from a start index, {@code false} otherwise.
     */
    public boolean contains(final Object obj, int start) {
        rangeCheck(start); //could make temporary varaible
        if (obj == null) {
            while (start < size()) {
                if (null == elementData[start]) {
                    return true;
                }
                ++start;
            }
            return false;
        }
        else {
            while (start < size()) {
                if (obj.equals(elementData[start])) {
                    return true;
                }
                ++start;
            }
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        selfCheck();
        Objects.requireNonNull(c, "The specified Collection is null.");
        return super.containsAll(c);
    }

    private class Enumerate implements Enumerator<E> {
           //dont do Enumerate<E> since <E> will be different
        //E from the DynamicList<E>, so we keep the Enumerate
        //E implicit, inherited from outer class DynamicList

        protected int currentPos; //non-private to simplify subclass access
        int lastRetPos;
        int expectedModCount;

        protected Enumerate() {
            this(0);
        }

        Enumerate(int startIndex) {
            currentPos = startIndex;
            lastRetPos = -1;
            expectedModCount = modCount;
        }

        final void checkForComodification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Deprecated
        @Override
        public boolean hasMoreElements() {
            return currentPos != size();
        }

        @Deprecated
        @Override
        public E nextElement() {
            this.checkForComodification();
            if (currentPos < 0) {
                throw new IllegalStateException();
            }
            if (currentPos >= size()) {
                throw new NoSuchElementException();
            }
            int old = currentPos; //store the current index
            lastRetPos = currentPos;
            currentPos++; //move the current index up
            return get(old); //return the element at the old current index
        }

        @Override
        public boolean hasNext() {
            return hasMoreElements();
        }

        @Override
        public E next() {
            return nextElement();
        }

        @Override
        public void add(E element) {
            this.checkForComodification();
            if (currentPos < 0) {
                throw new IllegalStateException();
            }
            try {
                DynamicList.this.add(currentPos++, element);
                //currentPos++;
                lastRetPos = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void set(E element) {
            this.checkForComodification();
            if (this.lastRetPos < 0 || currentPos < 0) {
                throw new IllegalStateException();
            }
            try {
                DynamicList.this.quickSet(lastRetPos, element);
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            this.checkForComodification();
            if (lastRetPos < 0 || currentPos < 0) {
                throw new IllegalStateException();
            }
            try {
                DynamicList.this.fastRemove(lastRetPos);
                currentPos--; //moving the currentPos back
                lastRetPos = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void setIndex(final int index) {
            this.checkForComodification();
            rangeCheck(index);
            currentPos = index;
            lastRetPos = currentPos - 1;
        }

        @Override
        public int nextIndex() {
            return currentPos;
        }

        @Override
        public int previousIndex() {
            return currentPos - 1;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = DynamicList.this.size;
            int i = currentPos;
            if (i >= size) {
                return;
            }
            final Object[] elementData = DynamicList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            currentPos = i;
            lastRetPos = i - 1;
            checkForComodification();
        }
    }

    /**
     * A private class that implements the functions of the {@link Iterator}
     * interface that will be used to iterate over all the elements of a
     * DynamicList in sequential order. This private class is extended by
     * {@link ListItr}.
     */
    private class Itr implements Iterator<E> {

        //all instance variables are package-private to allow access 
        //to ListItr but not allow access outside this package.
        //these varibles are public inside this package only.
        /**
         * Index of element to be returned by subsequent call to {@link #next}.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or previous.
         * Reset to -1 if this element is deleted by a call to {@link #remove}.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * DynamicList should have. If this expectation is violated, the
         * iterator has detected concurrent modification.
         */
        int expectedModCount = modCount;

        //no constrctor or initalizer {} needed here
        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public E next() {
            checkForComodification();
            try {
                lastRet = cursor;
                return get(cursor++); //increment cursor afterwards
            }
            catch (IndexOutOfBoundsException ex) {
                //need to recheck for comdification since this should not happen
                checkForComodification();
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                DynamicList.this.fastRemove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                    //if going forward, since ListItr is a sublcass and ListItr can go backwards unlki an Iterator
                }
                lastRet = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                //dont need to call checkForComodification since a violation was clearly found
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            this.checkForComodification();
            Objects.requireNonNull(action);
            Iterator.super.forEachRemaining(action); //must use Iterator.super to get to the interface
            //otherwise super. just goes to superclass, which in this case is Object
        }

        final void checkForComodification() { //non-private to allow access to ListItr, final so it cannot be overridden
            if (modCount != expectedModCount) 
                throw new ConcurrentModificationException();
            
        }
    }

    /**
     * A private class that implements the functions of the {@link Iterator} and
     * {@link ListIterator} interfaces that will be used to iterate through all
     * elements of a DynamicArray in any order. This private class extends
     * {@link Itr} and implements Iterator and ListIterator.
     */
    private class ListItr extends Itr implements ListIterator<E> {

        //instance variables are inherited from Itr
        ListItr() {
            this(0);
        }

        ListItr(final int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0; //going backwards starts at [size()] not [size() - 1]
            //!= not equals comparsion is faster
        }

        @Override
        public E previous() {
            checkForComodification();
            try {
                /*
                 int i = cursor - 1;
                 E previous = get(i);
                 lastRet = cursor = i;
                 return previous;
                 */
                //clear way, same thing
                /*
                 E previous = get(cursor - 1);
                 cursor--;
                 lastRet = cursor;
                 return previous;
                 */
                //fast way
                E previous = get(cursor-- - 1);
                //cursor-- gets the original value, then decrements it later
                //--cursor decrements the original value first, then gets the lowered value
                //same for cursor++ and ++cursor
                lastRet = cursor;
                return previous;
            }
            catch (IndexOutOfBoundsException ex) {
                checkForComodification();
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(final E element) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                DynamicList.this.quickSet(lastRet, element);
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(final E element) {
            checkForComodification();
            try {
                DynamicList.this.add(cursor++, element);
                lastRet = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("UnnecessaryReturnStatement")
        public void forEachRemaining(final Consumer<? super E> action) {
            checkForComodification();
            Objects.requireNonNull(action);
            if (lastRet < cursor) {
                while (hasNext()) {
                    action.accept(next());
                }
                return; //not needed since else block was added
            }
            else { //not needed
                while (hasPrevious()) {
                    action.accept(previous());
                }
            }
        }
    }

    /**
     * Index-based split-by-two, lazily initialized Spliterator
     */
    static final class Spliterate<E> implements Spliterator<E> {

        /*
         * If DynamicLists were immutable, or structurally immutable (no
         * adds, removes, etc), we could implement their spliterators
         * with Arrays.spliterator. Instead we detect as much
         * interference during traversal as practical without
         * sacrificing much performance. We rely primarily on
         * modCounts. These are not guaranteed to detect concurrency
         * violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to
         * be worthwhile in practice. To carry this out, we (1) lazily
         * initialize fence and expectedModCount until the latest
         * point that we need to commit to the state we are checking
         * against; thus improving precision.  (This doesn't apply to
         * SubLists, that create spliterators with current non-lazy
         * values).  (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach
         * (the most performance-sensitive method). When using forEach
         * (as opposed to iterators), we can normally only detect
         * interference after actions, not before. Further
         * CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small
         * elementData array given its size(), that could only have
         * occurred due to interference.  This allows the inner loop
         * of forEach to run without any further checks, and
         * simplifies lambda-resolution. While this does entail a
         * number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation
         * occur anywhere other than inside forEach itself.  The other
         * less-often-used methods cannot take advantage of most of
         * these streamlinings.
         */
        private final DynamicList<E> list;
        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set

        /**
         * Create new Spliterate covering the given range
         */
        Spliterate(DynamicList<E> list, int origin, int fence, int expectedModCount) {
            this.list = list; // OK if null unless traversed
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            DynamicList<E> lst;
            if ((hi = fence) < 0) {
                if ((lst = list) == null) {
                    hi = fence = 0;
                }
                else {
                    expectedModCount = lst.modCount;
                    hi = fence = lst.size;
                }
            }
            return hi;
        }

        @Override
        public Spliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
            new Spliterate<>(list, lo, index = mid, expectedModCount);
        }

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked")
                E e = (E) list.elementData[i];
                action.accept(e);
                if (list.modCount != expectedModCount) {
                    throw new ConcurrentModificationException();
                }
                return true;
            }
            return false;
        } //for chinese class decorate a tissue box and bring it to class on wednesday
        //

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            DynamicList<E> lst;
            Object[] a;
            if (action == null) {
                throw new NullPointerException();
            }
            if ((lst = list) != null && (a = lst.elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = lst.modCount;
                    hi = lst.size;
                }
                else {
                    mc = expectedModCount;
                }
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked")
                        E e = (E) a[i];
                        action.accept(e);
                    }
                    if (lst.modCount == mc) {
                        return;
                    }
                }
            }
            throw new ConcurrentModificationException();
        }

        @Override
        public long estimateSize() {
            return (long) (getFence() - index);
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    /**
     * Constructs and returns an Enumerator over all elements of this
     * DynamicList in sequential order. Note that the returned Enumerator is
     * {@code fail-fast} and that an {@link Enumerator} differs from an
     * {@link Enumeration}.
     *
     * @return An {@code Enumerator} over all the elements of this DynamicList
     * in sequential order.
     * @see enumerator()
     * @see iterator()
     * @see listIterator()
     * @see listIterator(int index)
     * @see spliterator()
     */
    public Enumerator<E> enumerator() {
        selfCheck();
        return new Enumerate(0);
    }

    /**
     * Constructs and returns an Enumerator over all elements of this
     * DynamicList in sequential order starting from a start index. Note that
     * the returned Enumerator is {@code fail-fast} and that an
     * {@link Enumerator} differs from an {@link Enumeration}.
     *
     * @param start A start index where the returned {@code Enumerator} will
     * start at.
     * @return An {@code Enumerator} over all the elements of this DynamicList
     * in sequential order starting from a specified start index.
     * @see enumerator()
     * @see iterator()
     * @see listIterator()
     * @see listIterator(int index)
     * @see spliterator()
     * @throws IllegalArgumentException If the specified start index is out of
     * bounds, being less than zero or greater than the size of this
     * DynamicList.
     */
    public Enumerator<E> enumerator(int start) {
        selfCheck();
        if (start < 0 || start >= size()) {
            throw new IllegalArgumentException("The requested index: " + start + " is out of bounds.");
        }
        return new Enumerate(start);
    }

    /**
     * Constructs and returns an {@link Enumeration} over all elements of this
     * DynamicList in sequential order. Note that the returned Enumeration is
     * not {@code fail-fast} unlike an {@link Enumerator} which is
     * {@code fail-fast}.
     *
     * @return An {@code Enumeration} over all the elements of this DynamicList
     * in sequential order. Warning!!! Attempting to modify this DynamicList
     * while calling {@code hasMoreElements} or {@code nextElements} methods of
     * the returned {@code Enumeration} may yield unexpected results or throw a
     * {@link ConcurrentModificationException}.
     *
     * @see enumerator()
     * @see enumerator(int start)
     * @see iterator()
     * @see listIterator()
     * @see listIterator(int index)
     * @see spliterator()
     */
    public Enumeration<E> elements() {
        selfCheck();
        return new Enumeration<E>() {

            int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < size();
            }

            @Override
            public E nextElement() {
                synchronized (DynamicList.this) {
                    if (index < size()) {
                        return get(index++);
                    }
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    public Enumeration<E> fastElements() {     
        return new Enumeration<E>() {
            
            int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < size();
            }

            @Override
            public E nextElement() {
                if (index < size()) {
                    return get(index++);
                }
                throw new NoSuchElementException();
            }       
        };
    }

    /**
     * Constructs and returns an {@code thread-safe} and {@code fail-fast}
     * {@link Enumeration} over all elements of this DynamicList in sequential
     * order.
     * <ul>
     * Take note that:
     * <li>
     * The returned Enumeration is {@code fail-fast}, since it checks for
     * concurrent modification.
     * </li>
     * <li>
     * Attempting to modify this DynamicList while the returned
     * {@code Enumeration} is not done iterating through this DynamicList will
     * throw {@link ConcurrentModificationException}.
     * </li>
     * </ul>
     *
     * @return An {@code thread-safe} and {@code fail-fast} {@code Enumeration}
     * over all the elements of this DynamicList in sequential order.
     *
     * @see enumerator()
     * @see enumerator(int start)
     * @see elements()
     * @see iterator()
     * @see listIterator()
     * @see listIterator(int index)
     * @see spliterator()
     *
     * @deprecated This method should only be used for security purposes.
     */
    @Deprecated
    public synchronized Enumeration<E> synchronizedElements() {
        selfCheck();
        Enumeration<E> e = new Enumeration<E>() {

            int index;
            final int expectedModCount; //once this final varible is 
            //intialized, then it can never be changed

            {
                this.index = 0;
                this.expectedModCount = modCount;
            }

            private void checkStatus() {
                Objects.requireNonNull(DynamicList.this, "This DynamicList is null.");
                if (expectedModCount != modCount) { //fail if difference found
                    throw new ConcurrentModificationException("Expected mod count: " + expectedModCount + " is not equal to current mod count: " + modCount + ".");
                }
            }

            @Override
            public boolean hasMoreElements() {
                checkStatus();
                return index < size();
            }

            @Override
            public E nextElement() {
                checkStatus();
                synchronized (DynamicList.this) {
                    if (index < size()) {
                        final int tmpIndex;
                        tmpIndex = index;
                        ++index;
                        return get(tmpIndex); //could just to index++ here
                    }
                }
                throw new NoSuchElementException("No more elements left.");
            }
        };
        return e;
    }

    /**
     * Constructs and returns an {@link Iterator} over all elements of this
     * DynamicList in sequential order. Note that the returned Iterator is
     * {@code fail-fast}.
     *
     * @return An {@code Iterator} over all the elements of this DynamicList in
     * sequential order.
     * @see enumerator()
     * @see enumerator(int start)
     * @see elements()
     * @see listIterator()
     * @see listIterator(int index)
     * @see spliterator()
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Constructs and returns an {@link ListIterator} over all elements of this
     * DynamicList in sequential order. Note that the returned ListIterator is
     * {@code fail-fast}.
     *
     * @return An {@code ListIterator} over all the elements of this DynamicList
     * in sequential order.
     * @see enumerator()
     * @see enumerator(int start)
     * @see elements()
     * @see iterator()
     * @see listIterator(int index)
     * @see spliterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ListItr();
    }

    /**
     * Constructs and returns an {@link ListIterator} over all elements of this
     * DynamicList starting at a specified index, the returned
     * {@code ListIterator} is free to iterate in any direction. Note that the
     * returned ListIterator is {@code fail-fast}.
     *
     * @param index
     * @return An {@code ListIterator} over all the elements of this DynamicList
     * in sequential order.
     * @see enumerator()
     * @see enumerator(int start)
     * @see elements()
     * @see iterator()
     * @see listIterator()
     * @see spliterator()
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        selfCheck();
        rangeCheckForAdd(index); // a list Iterator may start at size() and is not restrcted to size() - 1
        return new ListItr(index);
    }

    /**
     * Constructs and returns an {@link Spliterator} over all elements of this
     * DynamicList. Note that the returned Spliterator is {@code fail-fast}.
     *
     * @return An {@code Spliterator} over all the elements of this DynamicList
     * in sequential order.
     * @see enumerator()
     * @see enumerator(int start)
     * @see elements()
     * @see iterator()
     * @see listIterator()
     * @see listIterator(int index)
     */
    @Override
    public Spliterator<E> spliterator() {
        selfCheck();
        //return java.util.Spliterators.spliterator(this, Spliterator.ORDERED);    
        return new Spliterate<>(this, 0, -1, modCount); //mod with 0
    }

    @Override
    public void forEach(final Consumer<? super E> action) {
        selfCheck();
        Objects.requireNonNull(action);
        for (Iterator<E> e = iterator(); e.hasNext();) {
            action.accept(e.next()); //e.next() is a reference to the actual element
            //thus if we do something with e.next(), the original element does that something as well
            /*
             E tmp = e.next();
             action.accept(tmp); //would do the same thing as above
             //changing tmp, changes the original element, also List classes
             //store reference(s) or reference objects, not primitives
             //primitives are "wrapped" or "boxed" to their respecitve wrapper classes
             //int wrapper class = Integer
             //boxing int makes it into Integer
             //unbocing Integer makes an int, through intValue() method
             */

        }
    }

    /**
     * Shuffles all elements randomly in this DynamicList.
     *
     * @return This DynamicList, but shuffled.
     * @throws NullPointerException If this DynamicList is {@code null}.
     */
    public DynamicList<E> shuffle() {
        selfCheck();
        Collections.shuffle(this); //works
        ++modCount;
        return this;
    }
    
    //public void shuffle() {
        
    //}

    /**
     * Shuffles all elements in this DynamicList according to a specified
     * {@link Random} argument.
     *
     * @param rnd A specified {@code Random} argument, which must not be
     * {@code null}.
     * @return This DynamicList, but shuffled.
     * @throws NullPointerException If the specified {@code Random} argument is
     * {@code null}.
     */
    public DynamicList<E> shuffle(Random rnd) {
        selfCheck();
        Collections.shuffle(this, Objects.requireNonNull(rnd));
        ++modCount;
        return this;
    }

    /**
     * Sorts all the elements in this DynamicList. This method can only sort
     * objects that implement the {@link Comparable} interface and will return
     * immediately (do nothing) if this DynamicList contains an element that
     * does not implement the {@code Comparable} interface.
     *
     * Note that sometimes processing a sorted list can be much faster than an
     * unsorted one.
     *
     * @see sort(Comparator c)
     */
    @SuppressWarnings("UnusedAssignment")
    public void sort() {
        selfCheck();
        E[] tmp = (E[]) new Object[size()];
        for (int index = 0; index < size(); ++index) {
            E value = get(index); //we make a temporary variable so we dont need to call get(index) many time        
            if (!(value instanceof Comparable)) {
                return;
            }
            tmp[index] = value;
            value = null; //destroy the temporary variable, since it has already been used.
        }
        Arrays.sort(tmp);
        System.arraycopy(tmp, 0, elementData, 0, tmp.length);
        modCount = modCount + 1;
        tmp = null;
    }

    /**
     * Reverses the order of all elements in this DynamicList. This method 
     * always runs in linear time based on the size of this DynamicList.
     */
    @SuppressWarnings("UnusedAssignment")
    public void reverse() {
        Object[] reversed = new Object[size];
        //you can declar mutiple varibles of the same type in the loop
        for (int backward = size - 1, forward = 0; backward != -1; --backward) { //!= is faster
            reversed[forward++] = elementData[backward]; 
            elementData[backward] = null;
            ///forward++;
        }
        System.arraycopy(reversed, 0, elementData, 0, size);
        reversed = null;
        ++modCount;
    }

    /**
     * Sorts all elements in ascending order in the specified DynamicList, which
     * must be of type {@link Comparable}.
     *
     * @param list The DynamicList to be sorted.
     * @deprecated The sorting algorithm of this method is very slow.
     */
    @Deprecated
    @SuppressWarnings("UnusedAssignment")
    public static void sort(DynamicList<Comparable> list) {
        Objects.requireNonNull(list);
        DynamicList<Comparable> tmp = list;
        //duplicate so the original list is not changed through remove method
        DynamicList<Comparable> sorted = new DynamicList<>();
        while (!tmp.isEmpty()) {
            Comparable smallest = getSmallest(tmp);
            sorted.add(smallest);
            tmp.remove(indexOf(tmp, smallest));
            smallest = null;
        }

        list.clear(); //may use this private methos sonce we are in its class 
        list.addAll(sorted); //modifiy the list reference
    }

    /**
     * Gets the index of a specified {@link Comparable} value in a DynamicList
     * of type {@code Comparable}. Returns -1 if the specified DynamicList does
     * not contain the requested {@code Comparable} value.
     *
     * @param list The DynamicList where the specified Comparable value will be
     * searched for and have it's index value returned, or -1 if the DynamicList
     * does not contain the requested {@code Comparable} value.
     * @param value The specified Comparable value.
     * @return The index of the specified Comparable value.
     * @throws NullPointerException If the specified Comparable value is
     * {@code null}.
     */
    private static int indexOf(DynamicList<Comparable> list, Comparable value) {
        Objects.requireNonNull(value);
        for (ListIterator<Comparable> li = list.listIterator(); li.hasNext();) {
            if (value.compareTo(li.next()) == 0) { //increment cursor, use value since it cannot be null
                return li.previousIndex(); //move back 1 
            }
        }
        return -1;
    }

    /**
     * Gets the smallest {@link Comparable} value in a specified DynamicList.
     *
     * @param list A specified DynamicList of type {@code Comparable}.
     * @return The smallest Comparable element in a specified DynamicList.
     */
    @SuppressWarnings({"FinalPrivateMethod", "UnusedAssignment"})
    private static final Comparable getSmallest(DynamicList<Comparable> list) { //useless final modifier
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }
        Comparable smallest = list.get(0);
        for (ListIterator<Comparable> li = list.listIterator(1); li.hasNext();) {
            Comparable next = li.next();
            if (smallest.compareTo(next) > 0) {
                smallest = next;
            }
            next = null;
        }
        return smallest;
    }

    /**
     * Sorts all the objects in this DynamicList according to the specified {@link Comparator}.
     * If the specified {@code Comparator} is {@code null}, this method will simply call {@link #sort()}.
     * @param c The specified {@link Comparator} that will be used to sort all the objects
     * in this DynamicList.
     * @see sort()
     * @see sort(DynamicList list)
     */
    @Override
    @SuppressWarnings("UnusedAssignment")
    public void sort(final Comparator<? super E> c) {
        if (c == null) {
            sort();
            return;
        }
        for (int outer = 0; outer < size(); ++outer) {
            for (int inner = outer + 1; inner < size(); ++inner) {
                if (c.compare(get(outer), get(inner)) > 0) {
                    E outerElement = get(outer);
                    set(outer, get(inner));
                    set(inner, outerElement);
                    outerElement = null; //destroy the temporary variable 
                }
            }
        }
    }
    
    public boolean equals(List<E> list) {
        if (list == this) {
            return true;
        }
        
        if (list.size() != size()) {
            return false;
        }
        
        for (int index = 0; index < size(); ++index) {
            if (get(index) == null && list.get(index) != null || get(index) != null && list.get(index) == null) {
                return false;
            }
            if (get(index) != null && list.get(index) != null && !get(index).equals(list.get(index))) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        selfCheck();
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof List)) {
            return false;
        }

        Iterator<E> itr1 = iterator();
        Iterator<?> itr2 = ((List<?>) obj).iterator();
        Iterator<E> itrClone = itr1;
        Iterator<?> itr2Clone = itr2;
        while (itr1.hasNext() && itr2.hasNext()) {
            E o1 = itr1.next();
            Object o2 = itr2.next();
            // System.out.println("Before: " + itr1.equals(itrClone) + " " + itr2.equals(itr2Clone));
            itrClone = itr1;
            itr2Clone = itr2;
            // System.out.println("After: " + itr1.equals(itrClone) + " " + itr2.equals(itr2Clone));
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        //itrClone and itr2Clone are references to itr1 and itr2, since [[.equals()]] returns true before and after
        //and also, the default Object method [[.equals()]] only tests 
        //for reference equals with [[ "==" ]] operator, Iterator does not override
        //the Object class EQUALS() method so therefore the following return statement IS STILL ACCURATE:

        return !(itrClone.hasNext() || itr2Clone.hasNext());
        //boolean law, the ! flips every operand (itr1.hasNext()) is an operand
        //, inculding the boolean result, itr1.hasNext() and itr2.hasNext() and the || or operator.
        //!(itr1.hasNext() || itr2.hasNext()) ----> !itr1.hasNext() && !itr2.hasNext()
    }

    @Override
    public int hashCode() {
        selfCheck();
        return super.hashCode();
    }

    @Override
    public Object[] toArray() {
        selfCheck();
        return Arrays.copyOf(elementData, size);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        selfCheck();
        Objects.requireNonNull(array, "The specified array is null.");

        if (array.length < size) {
            return (T[]) Arrays.copyOf(elementData, size, array.getClass());
        }

        System.arraycopy(elementData, 0, array, 0, size);

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }

    public void copyInto(Object[] array) {
        selfCheck();
        if (array == null) {
            throw new NullPointerException("The specified array is null."); //could use Objects.requireNonNull
        }
        System.arraycopy(elementData, 0, array, 0, size);
    }

    @Override
    public List<E> subList(int start, int end) {
        selfCheck(); //instanceof works for interfaces, see overriden method in AbstractList

        boolean startOut = ((start < 0) || (start >= size)); //temporary varibles, so we dont need to calcuate 
        boolean endOut = ((end < 0) | (end >= size)); //out of boounds mutiple times

        if (startOut && endOut) {
            throw new ArrayIndexOutOfBoundsException("The specified start index: " + Integer.toString(start) + " is out of bounds\nand the specified end index: " + end + " is out of bounds.");
        }

        if (startOut) {
            throw new ArrayIndexOutOfBoundsException("The specified start index: " + Integer.toString(end) + " is out of bounds.");
        }

        if (endOut) {
            throw new ArrayIndexOutOfBoundsException("The specified end index: " + Integer.toString(end) + " is out of bounds.");
        }

        if (start > end) {
            throw new IllegalArgumentException("The specified start index: " + start + " is greater than the specified end index: " + end + ".");
        }

        return super.subList(start, end);
    }

    @Override
    public Stream<E> stream() {
        selfCheck();
        return super.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        selfCheck();
        return super.parallelStream();
    }

    /**
     * Gets the number of times that this DynamicList has been modified
     * structurally, such as adding, removing or setting elements. If this value
     * overflows to negative (less than zero), an {@link IllegalStateException}
     * will be thrown and the modCount will be reset back to zero.
     *
     * @return The number of times this DynamicList has been modified.
     */
    public final int getModCount() {
        return modCount;
    }

    /* Test...Ignore
     private class i extends java.util.Vector {

     @Override
     public Object clone() {
     try {
     super.clone(); 
     }
     catch (CloneNotSupportedException ex) { //invalid since Vector's clone() method 
     //does not throw CloneNotSupportedException
     Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }
     */
    //Note that the access modifier for
    //this method must be protected or public
    //cannot be weaker than protected, such as private or package private.
    //in this method we dont not add a "throws CloneNotSupportedException"
    //since that would force the caller, "user" to make a try catch or
    //make their own methods throw CloneNotSupportedException 
    /*
     clone() is a method in the Java programming language for object duplication. 
     In Java, objects are manipulated through reference variables, 
     and there is no operator for copying an object—
     the assignment operator duplicates the reference, not the object. 
     The clone() method provides this missing functionality.
    
     clone() method perfomas a shallow copy, not a deep one
     a deep copy, which would like tyring to copy my starfighter game 
     exactly, even with the random speeds and ammo, it would be too 
     hard to implement.
     */
    //to get the suppress warnings, when the hint is showing, 
    //"follow the arrow" where it gives you additional options
    /**
     * {@inheritDoc}
     *
     * @return A "shallow" copy of this DynamicList.
     */
    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Object clone() {
        selfCheck();
        try {
            @SuppressWarnings("unchecked")
            DynamicList<?> clone = (DynamicList<?>) super.clone();
            clone.elementData = Arrays.copyOf(elementData, size());
            clone.modCount = 0;
            clone.size = size();
            clone.capacityIncrement = getCapacityIncrement();
            //we can access elementData and modCount directly 
            //with the clone, since we are in the DynamicList class
            //where all DynamicList methods have unrestrcited access to these fields.
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            Logger.getLogger(DynamicList.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalError(ex);
        }
    }

    /**
     * Returns a {@link String} view of this DynamicList including its elements.
     * This implementation uses a {@link StringBuilder} to create a
     * {@code String} view of this DynamicList. Sub classes may choose to
     * override this method for greater efficiency.
     *
     * @return A {@code String} view of this DynamicList.
     */
    @SuppressWarnings("UnusedAssignment")
    @Override
    public String toString() { //automaitically called when invoked in print or println
        selfCheck();
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int index = 0;
        for (;;) { //infinte loop
            E element = get(index);
            if (index == size() - 1) {
                return sb.append((element == null) ? "null]" : element.toString() + "]").toString();
            }
            sb.append((element == null) ? "null, " : element.toString() + ", ");
            ++index;
            //sb = sb.append((element == null) ? "null, " : element.toString() + ", "); //could do this
            //++index;
        }
        //throw new InternalError(); //should never happen
        /*
         for (Enumerator<E> li = enumerator(); li.hasNext(); ) {
         E tmp = li.next();
         if (li.nextIndex() == size()) {
         return sb.append((tmp == null) ? "null]" : tmp.toString() + "]").toString();
         //return sb.append((tmp == null) ? "null" : tmp.toString()).append("]").toString() //double append
         }
         sb.append((tmp == null) ? "null, " : tmp.toString() + ", ");
         //sb.append((tmp == null) ? "null" : tmp.toString()).append(", "); //may use mutiple append, as many as needed            
         tmp = null; //let gc do its work
         } 
         */
    }

    /**
     * Returns a full view of this DynamicList, including its "buffer zone".
     *
     * @return A full view of this DynamicList, including its "buffer zone".
     */
    String fullView() {
        selfCheck();
        return Arrays.toString(elementData);
    }

    /**
     * Saves the state of this {@code DynamicList} instance to a stream (that
     * is, serialize it). This method performs synchronization to ensure the
     * consistency of the serialized data.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        Objects.requireNonNull(s);
        final java.io.ObjectOutputStream.PutField fields = s.putFields();
        final Object[] data;
        synchronized (this) {
            fields.put("capacityIncrement", capacityIncrement);
            fields.put("size", size);
            data = elementData.clone();
        }
        fields.put("elementData", data);
        s.writeFields();
    }

    /**
     * Reconstitutes a <tt>DynamicList</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        Objects.requireNonNull(s);
        elementData = new Object[0];

        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in capacity
        s.readInt(); // ignored

        if (size > 0) {
            // be like clone(), allocate array based upon size not capacity
            ensureCapacity(size);

            @SuppressWarnings("MismatchedReadAndWriteOfArray")
            Object[] array = elementData;
            // Read in all elements in the proper order.
            for (int i = 0; i < size; ++i) {
                array[i] = s.readObject();
            }
        }
    }
}
