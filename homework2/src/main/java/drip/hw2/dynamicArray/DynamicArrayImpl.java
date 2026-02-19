package main.java.drip.hw2.dynamicArray;

import java.util.Arrays;

public class DynamicArrayImpl implements DynamicArray {
    int countElements = 0;
    int[] array;

    public DynamicArrayImpl(int size) {
        array = new int[size];
    }

    public DynamicArrayImpl() {
        this(1);
    }

    private void ExpandArrayTwice()  {
        this.array = Arrays.copyOf(this.array, this.array.length * 2);
    }

    @Override
    public int size() {
        return this.countElements;
    }

    @Override
    public boolean isEmpty() {
        return countElements == 0;
    }

    @Override
    public boolean contains(int element) {
        for (int i = 0; i < this.countElements; i++) {
            if (array[i] == element) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean add(int e) {
        if (array.length == countElements) {
            ExpandArrayTwice();
        }

        this.array[this.countElements] = e;
        this.countElements++;

        return true;
    }

    @Override
    public boolean containsAll(DynamicArray c) {
        for (int i = 0; i < c.size(); i++) {
            if (!contains(c.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(DynamicArray c) {
        if (c.isEmpty()) {
            return false;
        }

        for (int i = 0; i < c.size(); i++) {
            this.add(c.get(i));
        }

        return true;
    }

    @Override
    public boolean addAll(int index, DynamicArray c) {
        if (index < 0 || index > this.countElements) {
            throw new IndexOutOfBoundsException();
        }

        if (c.isEmpty()) {
            return false;
        }

        DynamicArrayImpl rebuilt = new DynamicArrayImpl();

        for (int i = 0; i < index; i++) {
            rebuilt.add(this.array[i]);
        }

        for (int i = 0; i < c.size(); i++) {
            rebuilt.add(c.get(i));
        }

        for (int i = index; i < this.countElements; i++) {
            rebuilt.add(this.array[i]);
        }

        this.array = rebuilt.array;
        this.countElements = rebuilt.countElements;

        return true;
    }

    @Override
    public boolean removeAll(DynamicArray c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean isChanged = false;
        for (int i = 0; i < this.countElements; i++) {
            if (c.contains(this.array[i])) {
                isChanged = true;
                this.remove(i);
            }
        }

        return isChanged;
    }

    @Override
    public boolean retainAll(DynamicArray c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean isChanged = false;
        for (int i = 0; i < this.countElements; i++) {
            if (!c.contains(this.array[i])) {
                isChanged = true;
                this.remove(i);
            }
        }
        return isChanged;
    }

    @Override
    public void sort(){
        Arrays.sort(this.array, 0, this.countElements);
    }

    @Override
    public void clear() {
        this.countElements = 0;
        this.array = new int[1];
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= this.countElements) {
            throw new IndexOutOfBoundsException();
        }
        return this.array[index];
    }

    @Override
    public int set(int index, int element) {
        if (index < 0 || index >= this.countElements) {
            throw new IndexOutOfBoundsException();
        }
        int oldValue = this.array[index];
        this.array[index] = element;
        return oldValue;
    }

    @Override
    public void add(int index, int element) {
        if (index < 0 || index > this.countElements) {
            throw new IndexOutOfBoundsException();
        }

        if (this.array.length == this.countElements) {
            ExpandArrayTwice();
        }

        for (int i = this.countElements; i > index; i--) {
            this.array[i] = this.array[i - 1];
        }

        this.array[index] = element;
        this.countElements++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= this.countElements) {
            throw new IndexOutOfBoundsException();
        }

        int removedElement = this.array[index];
        for (int i = index; i < this.countElements - 1; i++) {
            this.array[i] = this.array[i + 1];
        }
        this.countElements--;

        return removedElement;
    }

    @Override
    public int indexOf(int element) {
        for (int i = 0; i < this.countElements; i++) {
            if (this.array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(int element) {
        for (int i = this.countElements - 1; i >= 0; i--) {
            if (this.array[i] == element) {
                return i;
            }
        }
        return -1;
    }
}
