package Util;

import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Objects;

//implements a map via linked list, horrible!!!
//this map does behave like a list
public class Table<K, V> extends Dictionary<K, V> {

    private static class Node<K, V> {

        private K key;
        private V value;
        private Node<K, V> next;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public static final class Entry<K, V> {
        
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public void setKey(K key) {
            this.key = key;
        }
        
        public K getKey() {
            return key;
        }
        
        public void setValue(V value) {
            this.value = value;
        }
        
        public V getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry other = (Entry) obj;
            return Constants.equals(key, other.key) && Constants.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.key);
            hash = 59 * hash + Objects.hashCode(this.value);
            return hash;
        }
    }

    private int size;
    private Node<K, V> base;

    public Table() {

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Enumeration<K> keys() {
        return new Enumeration<K>() {

            Node<K, V> current = base;

            @Override
            public boolean hasMoreElements() {
                return current != null;
            }

            @Override
            public K nextElement() {
                K key = current.key;
                current = current.next;
                return key;
            }
        };
    }

    @Override
    public Enumeration<V> elements() {
        return new Enumeration<V>() {

            Node<K, V> current = base;

            @Override
            public boolean hasMoreElements() {
                return current != null;
            }

            @Override
            public V nextElement() {
                V value = current.value;
                current = current.next;
                return value;
            }
        };
    }

    public void clear() {
        for (Node<K, V> current = base; current != null;) {
            Node<K, V> next = current.next;
            current.key = null;
            current.value = null;
            current.next = null;
            current = next;
        }
        base = null;
        size = 0;
    }

    //allow null key and null values
    @Override
    public V get(Object key) {
        if (base == null) {
            throw new EmptyStackException();
        }
        Node<K, V> current = base;
        if (key == null) {
            while (current != null) {
                if (current.key == null) {
                    return current.value;
                }
                current = current.next;
            }
        }
        else {
            while (current != null) {
                if (key.equals(current.key)) {
                    return current.value;
                }
                current = current.next;
            }
        }
        throw new IllegalArgumentException();
    }
    
    public V getValue(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        Node<K, V> current = base;
        while (current != null) {
            if (count == index) {
                return current.value;
            }
            current = current.next;
            ++count;
        }
        //should never happen
        throw new InternalError();
    }
    
    public K getKey(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        Node<K, V> current = base;
        while (current != null) {
            if (count == index) {
                return current.key;
            }
            current = current.next;
            ++count;
        }
        //should never happen
        throw new InternalError();
    }
    
    public Entry<K, V> getEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        Node<K, V> current = base;
        while (current != null) {
            if (count == index) {
                return new Entry<>(current.key, current.value);
            }
            current = current.next;
            ++count;
        }
        //should never happen
        throw new InternalError();
    }

    @Override
    public V put(K key, V value) {
        if (base == null) { //empty table
            base = new Node<>(key, value); //create base node
            ++size; //increment size
            return null;
        }

        //see if we already have the same key, then just change
        //the node's value
        Node<K, V> current = base;
        if (key == null) {
            while (current != null) {
                if (current.key == null) {
                    V previousValue = current.value;
                    current.value = value;
                    return previousValue;
                }
                if (current.next == null) {
                    //if we see that the next one is the end
                    current.next = new Node<>(key, value);
                    ++size;
                    return null;
                }
                current = current.next;
            }
        }
        else {
            while (current != null) {
                if (key.equals(current.key)) {
                    V previousValue = current.value;
                    current.value = value;
                    return previousValue;
                }
                if (current.next == null) {
                    //if we see that the next one is the end
                    current.next = new Node<>(key, value);
                    ++size;
                    return null;
                }
                current = current.next;
            }
        } 
        //should never happen
        throw new InternalError();
    }

    @Override
    public V remove(Object key) {
        if (base == null) { //empty map
            throw new EmptyStackException();
        }
        Node<K, V> current = base;
        if (key == null) {
            if (base.key == null) {
                base.value = null;
                base = base.next;
                --size;
                return current.value;
            }
           // current = current.next;
            while (current != null) {
                Node<K, V> next = current.next;
                if (next != null && next.key == null) {
                    current.next = next.next;
                    --size;
                    return next.value;
                }
                current = current.next;
            }
        }
        else {
            if (key.equals(base.key)) {
                base.key = null;
                base.value = null;
                base = base.next;
                --size;
                return current.value;
            }
            //current = current.next;
            while (current != null) {
                Node<K, V> next = current.next;
                if (next != null && key.equals(next.key)) {
                    current.next = next.next;
                    --size;
                    return next.value;
                }
                current = current.next;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        for (Node<K, V> current = base; current != null; current = current.next) {
            result.append(current.key).append(" -> ").append(current.value);
            if (current.next != null) {
                result.append(", ");
            }
            else {
                return result.append("]").toString();
            }
        }
        return result.append("]").toString();
    }

    public static void main(String[] args) {
        Table<Integer, Integer> table = new Table<>();
        for (int i = 0; i < 10; i++) {
            int random = (int) (Math.random() * 250);
            int random2 = (int) (Math.random() * 250);
            table.put(random, random2);
            //table.put(random, null);
            System.out.println(random + " " + random2);
        }
        System.out.println(table);
        for (int index = 0; index < table.size(); ++index) {
            Entry<Integer, Integer> entry = table.getEntry(index);
            System.out.println(entry.key + " " + entry.value);
        }
        table.remove(table.getKey(0));
        System.out.println();
        for (int index = 0; index < table.size(); ++index) {
            System.out.println(table.getKey(index) + " " + table.getValue(index));
        }
    }
}
