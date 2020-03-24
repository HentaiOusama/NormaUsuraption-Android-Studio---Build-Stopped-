package com.hentai_productions.normausurpation;

import org.jetbrains.annotations.NotNull;

import java.util.*;

// T is expected to be an EnemyObject here
class EnemyObjectHashMap {
    // scaleValue 50 can be good
    private int scaleValue;

    // HashMap is Top --> Bottom --> Left --> Right
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>>>> enemyObjectHashMap =
            new HashMap<>();
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>>> tempEnemyObjectHashMapBottom =
            new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<EnemyObject>>> tempEnemyObjectHashMapLeft =
            new HashMap<>();
    private HashMap<Integer, ArrayList<EnemyObject>> tempEnemyObjectHashMapRight = new HashMap<>();
    private int maxHeightKey, maxWidthKey;
    private float bulletTop, bulletLeft, bulletRight, bulletBottom;
    public int bulletTopKey, bulletLeftKey, bulletRightKey, bulletBottomKey;
    private ArrayList<EnemyObject> tempArrayList;
    private int listLength;
    private EnemyObject tempEnemyObject;
    private int enemyCount = 0;

    EnemyObjectHashMap(float maxHeight, float maxWidth, int scaleValue) {
        this.scaleValue = scaleValue;
        maxHeightKey = (int) (maxHeight / scaleValue);
        maxWidthKey = (int) (maxWidth / scaleValue);

        for (int i = 0; i <= maxHeightKey; i++) {
            tempEnemyObjectHashMapBottom = new HashMap<>();
            for (int j = 0; j <= maxHeightKey; j++) {
                tempEnemyObjectHashMapLeft = new HashMap<>();
                for (int k = 0; k <= maxWidthKey; k++) {
                    tempEnemyObjectHashMapRight = new HashMap<>();
                    for (int l = 0; l <= maxWidthKey; l++) {
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

    void addEnemyObject(EnemyObject enemyObject, float enemyObjectTop, float enemyObjectBottom, float enemyObjectLeft, float enemyObjectRight) {
        int topKey = (int) (enemyObjectTop / scaleValue);
        int bottomKey = (int) (enemyObjectBottom / scaleValue);
        int leftKey = (int) (enemyObjectLeft / scaleValue);
        int rightKey = (int) (enemyObjectRight / scaleValue);
        ((((enemyObjectHashMap.get(topKey)).get(bottomKey)).get(leftKey)).get(rightKey)).add(enemyObject);
        enemyCount++;
    }

    void changeHashMapSize(int maxHeight, int maxWidth) {
        int tempMaxHeightKey = maxHeight / scaleValue;
        int tempMaxWidthKey = maxWidth / scaleValue;

        if (tempMaxHeightKey > maxHeightKey || tempMaxWidthKey > maxWidthKey) {
            for (int i = 0; i <= tempMaxHeightKey; i++) {
                if (i <= maxHeightKey) {
                    for (int j = 0; j <= tempMaxHeightKey; j++) {
                        if (j <= maxHeightKey) {
                            for (int k = 0; k <= tempMaxWidthKey; k++) {
                                if (k <= maxWidthKey) {
                                    for (int l = maxWidthKey + 1; l <= tempMaxWidthKey; l++) {
                                        tempArrayList = new ArrayList<EnemyObject>();
                                        ((enemyObjectHashMap.get(i)).get(j)).get(k).put(l, tempArrayList);
                                    }
                                } else {
                                    tempEnemyObjectHashMapRight = new HashMap<>();
                                    for (int l = 0; l <= tempMaxWidthKey; l++) {
                                        tempArrayList = new ArrayList<EnemyObject>();
                                        tempEnemyObjectHashMapRight.put(l, tempArrayList);
                                    }
                                    tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                                }
                            }
                        } else {
                            tempEnemyObjectHashMapLeft = new HashMap<>();
                            for (int k = 0; k <= tempMaxWidthKey; k++) {
                                tempEnemyObjectHashMapRight = new HashMap<>();
                                for (int l = 0; l <= tempMaxWidthKey; l++) {
                                    tempArrayList = new ArrayList<EnemyObject>();
                                    tempEnemyObjectHashMapRight.put(l, tempArrayList);
                                }
                                tempEnemyObjectHashMapLeft.put(k, tempEnemyObjectHashMapRight);
                            }
                            tempEnemyObjectHashMapBottom.put(j, tempEnemyObjectHashMapLeft);
                        }
                    }
                } else {
                    tempEnemyObjectHashMapBottom = new HashMap<>();
                    for (int j = 0; j <= tempMaxHeightKey; j++) {
                        tempEnemyObjectHashMapLeft = new HashMap<>();
                        for (int k = 0; k <= tempMaxWidthKey; k++) {
                            tempEnemyObjectHashMapRight = new HashMap<>();
                            for (int l = 0; l <= tempMaxWidthKey; l++) {
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

    void removeEnemyShipsCoincidingWithGivenBullet(@NotNull Bullet bullet) {
        bulletTop = bullet.getLocationTop();
        bulletLeft = bullet.getLocationLeft();
        bulletRight = bullet.getLocationRight();
        bulletBottom = bullet.getLocationBottom();
        bulletTopKey = (int) (bulletTop / scaleValue);
        bulletBottomKey = (int) (bulletBottom / scaleValue);
        bulletLeftKey = (int) (bulletLeft / scaleValue);
        bulletRightKey = (int) (bulletRight / scaleValue);

        for (int i = 0; i <= bulletBottomKey; i++) {
            for (int j = bulletTopKey; j <= maxHeightKey; j++) {
                for (int k = 0; k <= bulletRightKey; k++) {
                    for (int l = bulletLeftKey; l <= maxWidthKey; l++) {
                        for (int m = 0; m < (((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).size()); m++) {
                            tempEnemyObject = (((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).get(m));

                            // There is this offset of 5 in if statement. This is to ensure that it looks as though the bullet has hit the enemy
                            // and not the it vanished after coming close to the enemy.
                            if ((tempEnemyObject.getEnemyShipBottom() >= (bulletTop + 5)) && (tempEnemyObject.getEnemyShipTop() <= (bulletBottom - 5))
                                    && (tempEnemyObject.getEnemyShipLeft() <= (bulletRight - 5)) && (tempEnemyObject.getEnemyShipRight() >= (bulletLeft + 5))) {
                                ((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).get(m).stopBuildingBullets();
                                ((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).remove(m);
                                m--;
                            }
                        }
                    }
                }
            }
        }
    }

    int getMaxHeightKey() {
        return maxHeightKey;
    }

    int getMaxWidthKey() {
        return maxWidthKey;
    }

    int getEnemyObjectListSizeWithKeys(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight) {
        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).size());
    }

    EnemyObject getEnemyObjectWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index) {
        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index));
    }

    void stopAllEnemyBullets() {
        for (int i = 0; i <= maxHeightKey; i++) {
            for (int j = 0; j <= maxHeightKey; j++) {
                for (int k = 0; k <= maxWidthKey; k++) {
                    for (int l = 0; l <= maxWidthKey; l++) {
                        int len = getEnemyObjectListSizeWithKeys(i, j, k, l);
                        for (int m = 0; m < len; m++) {
                            getEnemyObjectWithKeysAndIndex(i, j, k, l, m).stopBuildingBullets();
                        }
                    }
                }
            }
        }
    }

    void startAllEnemyBullets() {
        for (int i = 0; i <= maxHeightKey; i++) {
            for (int j = 0; j <= maxHeightKey; j++) {
                for (int k = 0; k <= maxWidthKey; k++) {
                    for (int l = 0; l <= maxWidthKey; l++) {
                        int len = getEnemyObjectListSizeWithKeys(i, j, k, l);
                        for (int m = 0; m < len; m++) {
                            getEnemyObjectWithKeysAndIndex(i, j, k, l, m).startBuildingBullets();
                        }
                    }
                }
            }
        }
    }


//
//    int getEnemyShipTopOfEnemyShipWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index)
//    {
//        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index).getEnemyShipTop());
//    }
//
//    int getEnemyShipBottomOfEnemyShipWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index)
//    {
//        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index).getEnemyShipBottom());
//    }
//
//    int getEnemyShipLeftOfEnemyShipWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index)
//    {
//        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index).getEnemyShipLeft());
//    }
//
//    int getEnemyShipRightOfEnemyShipWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index)
//    {
//        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index).getEnemyShipRight());
//    }
}