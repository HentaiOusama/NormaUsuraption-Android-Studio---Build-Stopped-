package com.hentai_productions.normausurpation;

import java.util.*;

class myQueue<T> {

    private int head, tail;
    private ArrayList<T> QueueList;
    private int indexOfObjectToBeReturned_if_T_is_a_EnemyShipSpeedSet = 0;

    // Constructor
    myQueue() {
        head = 0;
        tail = -1;
        QueueList = new ArrayList<T>();
    }


    // General methods
    void Enqueue(T item) {
        QueueList.add(item);
        tail += 1;
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

    T get(int index) {
        return QueueList.get(index);
    }

    int getSize() {
        return tail - head + 1;
    }


    // T is expected to be a Bullet for below 2 methods
    void setLocationTop(int index, int LocationTop) {
        ((Bullet) QueueList.get(index)).setLocationTop(LocationTop);
    }

    void setLocationLeft(int index, int LocationLeft) {
        ((Bullet) QueueList.get(index)).setLocationLeft(LocationLeft);
    }


    // T is expected to be a EnemyShipSpeedSet
    T getNextEnemyShipSpeedSet() {
        int i = indexOfObjectToBeReturned_if_T_is_a_EnemyShipSpeedSet;
        indexOfObjectToBeReturned_if_T_is_a_EnemyShipSpeedSet++;
        if (indexOfObjectToBeReturned_if_T_is_a_EnemyShipSpeedSet == getSize()) {
            indexOfObjectToBeReturned_if_T_is_a_EnemyShipSpeedSet = 0;
        }
        return QueueList.get(i);
    }
}