package core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class HashStorage<K, V> implements Map<K, V> {
    private int size;
    private int pointer = -1;
    private List<Integer> hashTable;
    private List<Tuple<K, V>> table = new ArrayList<>();
    private Function<K, Integer> hasher;
    
    HashStorage(int size, Function<K, Integer> hasher) {
        if (size <= 0) {
            throw new IllegalArgumentException("Map size must be greater than 0: " + size);
        }
        this.size = size;
        hashTable = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            hashTable.add(i, -1);
        }
        this.hasher = hasher;
    }
    
    @Override
    public int size() {
        int[] s = {0};
        table.forEach(el -> {
            if (el.getKey() != null) {
                s[0]++;
            }
        });
        return s[0];
    }
    
    @Override
    public boolean isEmpty() {
        return pointer == -1 || size() == 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        
        K k;
        
        try {
            k = (K) key;
        }
        catch (ClassCastException e) {
            return false;
        }
        
        int hash = hasher.apply(k) % size;
        
        if (hashTable.get(hash) == -1) {
            return false;
        }
        else {
            Tuple<K, V> el = table.get(hashTable.get(hash));
            while (true) {
                if (el.getKey() != null && el.getKey().equals(k)) {
                    return true;
                }
                if (el.getChain() == -1) {
                    break;
                }
                el = table.get(el.getChain());
            }
        }
        
        return false;
    }
    
    @Override
    public boolean containsValue(Object value) {
        V v;
        try {
            v = (V) value;
        }
        catch (ClassCastException e) {
            return false;
        }
        for (Tuple<K, V> el : table) {
            if (el.getKey() != null && v != null ? el.getData().equals(v) : el.getData() == null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
    
        K k;
    
        try {
            k = (K) key;
        }
        catch (ClassCastException e) {
            return null;
        }
    
        int hash = hasher.apply(k) % size;
    
        if (hashTable.get(hash) == -1) {
            return null;
        }
        else {
            Tuple<K, V> el = table.get(hashTable.get(hash));
            while (true) {
                if (el.getKey() != null && el.getKey().equals(k)) {
                    return el.getData();
                }
                if (el.getChain() == -1) {
                    break;
                }
                el = table.get(el.getChain());
            }
        }
        
        return null;
    }
    
    @Nullable
    @Override
    public V put(Object key, Object value) {
        if (key == null) {
            return null;
        }
    
        K k;
        V v;
    
        try {
            k = (K) key;
            v = (V) value;
        }
        catch (ClassCastException e) {
            return null;
        }
    
        int hash = hasher.apply(k) % size;
    
        if (hashTable.get(hash) == -1) {
            pointer++;
            hashTable.set(hash, pointer);
            table.add(pointer, new Tuple<>(k, v));
            return null;
        }
        else {
            Tuple<K, V> el = table.get(hashTable.get(hash));
            while (true) {
                if (el.getKey() != null && el.getKey().equals(k)) {
                    V data = el.getData();
                    el.setData(v);
                    return data;
                }
                if (el.getChain() == -1) {
                    break;
                }
                el = table.get(el.getChain());
            }
            pointer++;
            hashTable.set(hash, pointer);
            table.add(pointer, new Tuple<>(k, v));
            el.setChain(pointer);
        }
    
        return null;
    }
    
    @Override
    public V remove(Object key) {
        if (key == null) {
            return null;
        }
    
        K k;
    
        try {
            k = (K) key;
        }
        catch (ClassCastException e) {
            return null;
        }
    
        int hash = hasher.apply(k) % size;
    
        if (hashTable.get(hash) == -1) {
            return null;
        }
        else {
            Tuple<K, V> el = table.get(hashTable.get(hash));
            while (true) {
                if (el.getKey() != null && el.getKey().equals(k)) {
                    el.setKey(null);
                    return el.getData();
                }
                if (el.getChain() == -1) {
                    break;
                }
                el = table.get(el.getChain());
            }
        }
    
        return null;
    }
    
    @Override
    public void putAll(@NotNull Map m) {
        for (Object key : m.keySet()) {
            put(key, m.get(key));
        }
    }
    
    @Override
    public void clear() {
        table.clear();
        for (int i = 0; i < hashTable.size(); i++) {
            hashTable.set(i, -1);
        }
    }
    
    @NotNull
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Tuple<K, V> el : table) {
            if (el.getKey() != null) {
                set.add(el.getKey());
            }
        }
        return set;
    }
    
    @NotNull
    @Override
    public Collection<V> values() {
        return table.stream().filter(el -> el.getKey() != null).map(Tuple::getData).collect(ArrayList::new, List::add, List::addAll);
    }
    
    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();
        for (Tuple<K, V> el : table) {
            if (el.getKey() != null) {
                set.add(new SEntry<>(el.getKey(), el.getData()));
            }
        }
        return set;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashStorage<?, ?> that = (HashStorage<?, ?>) o;
        return size == that.size &&
                pointer == that.pointer &&
                Objects.equals(hashTable, that.hashTable) &&
                Objects.equals(hasher, that.hasher);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(size, pointer, hashTable, hasher);
    }
    
    private static class Tuple<K, V> {
        private V data;
        private K key;
        private int chain = -1;
        
        Tuple(K key, V data) {
    
            this.data = data;
            this.key = key;
        }
    
        V getData() {
            return data;
        }
    
        void setData(V data) {
            this.data = data;
        }
    
        K getKey() {
            return key;
        }
    
        void setKey(K key) {
            this.key = key;
        }
    
        int getChain() {
            return chain;
        }
    
        void setChain(int chain) {
            this.chain = chain;
        }
    }
    
    static class SEntry<K, V> implements Entry<K, V> {
    
        private K key;
        private V value;
        
        SEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return key;
        }
    
        @Override
        public V getValue() {
            return value;
        }
    
        @Override
        public V setValue(V value) {
            V temp = this.value;
            this.value = value;
            return temp;
        }
    }
}
