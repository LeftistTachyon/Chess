package Util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class DynamicArray<E> implements Iterable<E> {

    public static final int MAX_ARRAY_SIZE = 200000000;
    private static final Object[] EMPTY = new Object[0];
    private Object[] data;

    public DynamicArray() {
        data = EMPTY;
    }

    public DynamicArray(E[] array) {
        data = Arrays.copyOf(array, (array.length > MAX_ARRAY_SIZE) ? MAX_ARRAY_SIZE : array.length);
    }
    
    public DynamicArray(Collection<E> list) {
        Object[] listArray = list.toArray();
        data = (listArray.length > MAX_ARRAY_SIZE) ? Arrays.copyOf(listArray, MAX_ARRAY_SIZE) : listArray;
    }

    private void rangeCheck(int index) {
        if (index >= data.length || index < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(index) + " is out of bounds.");
        }
    }

    private void rangeCheckForAdd(int index) {
        if (index > data.length || index < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(index) + " is out of bounds.");
        }
    }

    public boolean add(E element) {
        if (data.length == MAX_ARRAY_SIZE) {
            return false;
        }
        Object[] grow = new Object[data.length + 1];
        System.arraycopy(data, 0, grow, 0, data.length);
        grow[data.length] = element;
        data = grow;
        return true;
    }
    
    public void add(int index, E element) {
        if (data.length == MAX_ARRAY_SIZE) {
            return;
        }
        rangeCheckForAdd(index);
        Object[] grow = new Object[data.length + 1];
        System.arraycopy(data, 0, grow, 0, index);
        grow[index] = element;
        System.arraycopy(data, index, grow, index + 1, data.length - index);
        data = grow;
    }
    
    public E get(int index) {
        rangeCheck(index);
        return (E) data[index];
    }
    
    public boolean isNull(int index) {
        return get(index) == null;
    }
    
    public E set(int index, E element) {
        E oldElement = get(index);
        data[index] = null;
        data[index] = element;
        return oldElement;
    }
    
    public E remove(int index) {
        E previous = get(index);
        data[index] = null;
        Object[] reduce = new Object[data.length - 1];
        System.arraycopy(data, 0, reduce, 0, index++);
        while (index < data.length) {
            reduce[index - 1] = data[index++];
        }
        data = reduce;
        return previous;
    }
    
    public boolean remove(Object obj) {
        int index = indexOf(obj);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    public void swap(int first, int second) {
        rangeCheck(first);
        rangeCheck(second);
        Object tmp = data[first];
        data[first] = null;
        data[first] = data[second];
        data[second] = null;
        data[second] = tmp;
    }
    
    public int indexOf(Object obj) {
        int index = 0;
        if (obj == null) {
            while (index < data.length) {
                if (data[index] == null) {
                    return index;
                }
                ++index;
            }
            return -1;
        }
        while (index < data.length) {
            if (obj.equals(data[index])) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    public int lastIndexOf(Object obj) {
        int index = data.length - 1;
        if (obj == null) {
            while (index >= 0) {
                if (data[index] == null) {
                    return index;
                }
                --index;
            }
            return -1;
        }
        while (index >= 0) {
            if (obj.equals(data[index])) {
                return index;
            }
            --index;
        }
        return -1;
    }
    
    public boolean contains(Object obj) {
        int index = 0;
        if (obj == null) {
            while (index < data.length) {
                if (data[index] == null) {
                    return true;
                }
                ++index;
            }
            return false;
        }
        while (index < data.length) {
            if (obj.equals(data[index])) {
                return true;
            }
            ++index;
        }
        return false;
    }

    public boolean removeFirst(Object obj) {
        int index = indexOf(obj);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }
    
    public boolean removeLast(Object obj) {
        int index = lastIndexOf(obj);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }
    
    public void removeAll(Object obj) {
        for (int index = indexOf(obj); index >= 0; index = indexOf(obj)) {
            remove(index);
        }
    }
    
    public boolean isEmpty() {
        return data.length == 0;
    }
   
    public void clear() {
        for (int index = 0; index < data.length; ++index) {
            data[index] = null;
        }
        data = null;
        data = EMPTY;
    }

    public int size() {
        return data.length;
    }
    
    public void setSize(int size) {
        if (size < 0 || size > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException();
        }
        if (size >= data.length) {
            return;
        }
        data = Arrays.copyOf(data, size);
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < data.length;
            }

            @Override
            public E next() {
                return (E) data[index++];
            }

            @Override
            public void remove() {
                DynamicArray.this.remove(--index);
            }
        };
    }
   
    
    @Override
    public String toString() {
        return Arrays.toString(data);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DynamicArray)) {
            return false;
        }
        DynamicArray<?> list = (DynamicArray<?>) obj;
        if (data.length != list.data.length) {
            return false;
        }
        for (int index = 0; index < data.length; ++index) {
            if (!Constants.equals(data[index], list.data[index])) {
                return false;
            }
        }
        return true;
    }
    
    public void sort(Comparator<E> comparator) {
        Arrays.sort((E[])data, comparator);
    }
    
    public void copyInto(E[] array) {
        System.arraycopy(data, 0, array, 0, data.length);
    }
    
    public Object[] toArray() {
        return Arrays.copyOf(data, data.length);
    }
    
    public int getCount(E element) {
        int count = 0;
        if (element == null) {
            for (Object each : data) {
                if (each == null) {
                    ++count;
                }
            }
            return count;
        }
        for (Object each : data) {
            if (element.equals(each)) {
                ++count;
            }
        }
        return count;
    }
}