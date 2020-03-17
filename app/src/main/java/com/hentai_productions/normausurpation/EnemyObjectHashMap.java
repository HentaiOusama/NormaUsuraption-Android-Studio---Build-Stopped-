package com.hentai_productions.normausurpation;
import java.util.*;

// T is expected to be an EnemyObject here
class EnemyObjectHashMap
{
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>>>> enemyObjectHashMap =
            new HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>>>>();
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>>> tempEnemyObjectHashMapBottom;
    private HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>> tempEnemyObjectHashMapLeft;
    private HashMap<Integer, ArrayList<EnemyObject>> tempEnemyObjectHashMapRight;
    private int maxHeightKey, maxWidthKey;
    private ArrayList<EnemyObject> tempArrayList;

    public EnemyObjectHashMap(int maxHeight, int maxWidth)
    {
        maxHeightKey = maxHeight;
        maxWidthKey = maxWidth;

        for(int i = 0; i <= maxHeightKey; i++)
        {
            tempEnemyObjectHashMapBottom = null;
            for (int j = 0; j <= maxHeightKey; j++)
            {
                tempEnemyObjectHashMapLeft = null;
                for (int k = 0; k <= maxWidthKey; k++)
                {
                    tempEnemyObjectHashMapRight = null;
                    for (int l = 0; l <= maxWidthKey; l++)
                    {
                        tempArrayList = new ArrayList<EnemyObject>();
                        tempEnemyObjectHashMapRight.put(l, tempArrayList);
                    }
                    tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                }
                tempEnemyObjectHashMapBottom.put(j, tempEnemyObjectHashMapLeft);
            }
            enemyObjectHashMap.put(i, tempEnemyObjectHashMapBottom);
        }
    }

    void addEnemyObject(EnemyObject enemyObject, int enemyObjectTop, int enemyObjectBottom, int enemyObjectLeft, int enemyObjectRight)
    {
        int topKey = enemyObjectTop/50;
        int bottomKey = enemyObjectBottom/50;
        int leftKey = enemyObjectLeft/50;
        int rightKey = enemyObjectRight/50;
        ((((enemyObjectHashMap.get(topKey)).get(bottomKey)).get(leftKey)).get(rightKey)).add(enemyObject);

    }

    void changeHashMapSize(int maxHeight, int maxWidth)
    {
        int tempMaxHeightKey = maxHeight/50;
        int tempMaxWidthKey = maxWidth/50;

        if(tempMaxHeightKey>maxHeight || tempMaxWidthKey>maxWidth)
        {
            for(int i = 0; i <= tempMaxHeightKey; i++)
            {
                if(i <= maxHeight)
                {
                    for (int j = 0; j <= tempMaxHeightKey; j++)
                    {
                        if(j <= maxHeight)
                        {
                            for (int k = 0; k <= tempMaxWidthKey; k++)
                            {
                                if(k <= maxWidth)
                                {
                                    for (int l = maxWidth+1; l <= tempMaxWidthKey; l++)
                                    {
                                        tempArrayList = new ArrayList<EnemyObject>();
                                        ((enemyObjectHashMap.get(i)).get(j)).get(k).put(l, tempArrayList);
                                    }
                                }
                                else
                                {
                                    tempEnemyObjectHashMapRight = null;
                                    for (int l = 0; l <= maxWidthKey; l++)
                                    {
                                        tempArrayList = new ArrayList<EnemyObject>();
                                        tempEnemyObjectHashMapRight.put(l, tempArrayList);
                                    }
                                    tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                                }
                            }
                        }
                        else
                        {
                            tempEnemyObjectHashMapLeft = null;
                            for (int k = 0; k <= maxWidthKey; k++)
                            {
                                tempEnemyObjectHashMapRight = null;
                                for (int l = 0; l <= maxWidthKey; l++)
                                {
                                    tempArrayList = new ArrayList<EnemyObject>();
                                    tempEnemyObjectHashMapRight.put(l, tempArrayList);
                                }
                                tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                            }
                            tempEnemyObjectHashMapBottom.put(j, tempEnemyObjectHashMapLeft);
                        }
                    }
                }
                else
                {
                    tempEnemyObjectHashMapBottom = null;
                    for (int j = 0; j <= maxHeightKey; j++)
                    {
                        tempEnemyObjectHashMapLeft = null;
                        for (int k = 0; k <= maxWidthKey; k++)
                        {
                            tempEnemyObjectHashMapRight = null;
                            for (int l = 0; l <= maxWidthKey; l++)
                            {
                                tempArrayList = new ArrayList<EnemyObject>();
                                tempEnemyObjectHashMapRight.put(l, tempArrayList);
                            }
                            tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                        }
                        tempEnemyObjectHashMapBottom.put(j, tempEnemyObjectHashMapLeft);
                    }
                    enemyObjectHashMap.put(i, tempEnemyObjectHashMapBottom);
                }
            }
        }
    }

    void removeEnemyShipsCoincidingWithGivenBullet(Bullet bullet)
    {

    }
}