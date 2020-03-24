package com.hentai_productions.normausurpation;

import java.util.*;

// T is expected to be a Bullet here
class myQueue<T> {

    private int head, tail;
    private ArrayList<T> QueueList;

    myQueue() {
        head = 0;
        tail = -1;
        QueueList = new ArrayList<T>();
    }

    void Dequeue(int index) {
        if (tail >= head) {
            tail -= 1;
            QueueList.remove(index);
        }
    }

    void DequeueAll() {
        while (tail >= head) {
            QueueList.remove(tail);
            tail -= 1;
        }
    }

    void Enqueue(T item) {
        QueueList.add(item);
        tail += 1;
    }

    void setLocationTop(int index, int LocationTop) {
        ((Bullet) QueueList.get(index)).setLocationTop(LocationTop);
    }

    void setLocationLeft(int index, int LocationLeft) {
        ((Bullet) QueueList.get(index)).setLocationLeft(LocationLeft);
    }

    T get(int index) {
        return QueueList.get(index);
    }

    int getSize() {
        return tail - head + 1;
    }
}