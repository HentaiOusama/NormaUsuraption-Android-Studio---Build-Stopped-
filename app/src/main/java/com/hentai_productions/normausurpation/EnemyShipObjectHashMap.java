package com.hentai_productions.normausurpation;

import org.jetbrains.annotations.NotNull;

import java.util.*;

// T is expected to be an EnemyShipObject here
class EnemyShipObjectHashMap {
    // heightScaleValue 385 and widthScaleValue 175 can be good
    private int heightScaleValue, widthScaleValue;

    // HashMap is Top --> Bottom --> Left --> Right
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyShipObject>>>>> enemyObjectHashMap =
            new HashMap<>();
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<EnemyShipObject>>>> tempEnemyShipObjectHashMapBottom =
            new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<EnemyShipObject>>> tempEnemyShipObjectHashMapLeft =
            new HashMap<>();
    private HashMap<Integer, ArrayList<EnemyShipObject>> tempEnemyShipObjectHashMapRight = new HashMap<>();
    private int maxHeightKey, maxWidthKey;
    private float bulletTop, bulletLeft, bulletRight, bulletBottom;
    public int bulletTopKey, bulletLeftKey, bulletRightKey, bulletBottomKey;
    private ArrayList<EnemyShipObject> tempArrayList;
    private int listLength;
    private EnemyShipObject tempEnemyShipObject;
    private int enemyCount = 0;

    EnemyShipObjectHashMap(float maxHeight, float maxWidth, int heightScaleValue , int widthScaleValue) {
        this.heightScaleValue = heightScaleValue;
        this.widthScaleValue = widthScaleValue;
        maxHeightKey = (int) (maxHeight / heightScaleValue);
        maxWidthKey = (int) (maxWidth / widthScaleValue);

        for (int i = 0; i <= maxHeightKey; i++) {
            tempEnemyShipObjectHashMapBottom = new HashMap<>();
            for (int j = 0; j <= maxHeightKey; j++) {
                tempEnemyShipObjectHashMapLeft = new HashMap<>();
                for (int k = 0; k <= maxWidthKey; k++) {
                    tempEnemyShipObjectHashMapRight = new HashMap<>();
                    for (int l = 0; l <= maxWidthKey; l++) {
                        tempArrayList = new ArrayList<EnemyShipObject>();
                        tempEnemyShipObjectHashMapRight.put(l, tempArrayList);
                    }
                    tempEnemyShipObjectHashMapLeft.put(k, tempEnemyShipObjectHashMapRight);
                }
                tempEnemyShipObjectHashMapBottom.put(j, tempEnemyShipObjectHashMapLeft);
            }
            enemyObjectHashMap.put(i, tempEnemyShipObjectHashMapBottom);
        }
    }

    void addEnemyShipObject(EnemyShipObject enemyObject, float enemyObjectTop, float enemyObjectBottom, float enemyObjectLeft, float enemyObjectRight) {
        int topKey = (int) (enemyObjectTop / heightScaleValue);
        int bottomKey = (int) (enemyObjectBottom / heightScaleValue);
        int leftKey = (int) (enemyObjectLeft / widthScaleValue);
        int rightKey = (int) (enemyObjectRight / widthScaleValue);
        ((((enemyObjectHashMap.get(topKey)).get(bottomKey)).get(leftKey)).get(rightKey)).add(enemyObject);
        enemyCount++;
    }

    void changeHashMapSize(int maxHeight, int maxWidth) {
        int tempMaxHeightKey = maxHeight / heightScaleValue;
        int tempMaxWidthKey = maxWidth / widthScaleValue;

        if (tempMaxHeightKey > maxHeightKey || tempMaxWidthKey > maxWidthKey) {
            for (int i = 0; i <= tempMaxHeightKey; i++) {
                if (i <= maxHeightKey) {
                    for (int j = 0; j <= tempMaxHeightKey; j++) {
                        if (j <= maxHeightKey) {
                            for (int k = 0; k <= tempMaxWidthKey; k++) {
                                if (k <= maxWidthKey) {
                                    for (int l = maxWidthKey + 1; l <= tempMaxWidthKey; l++) {
                                        tempArrayList = new ArrayList<EnemyShipObject>();
                                        ((enemyObjectHashMap.get(i)).get(j)).get(k).put(l, tempArrayList);
                                    }
                                } else {
                                    tempEnemyShipObjectHashMapRight = new HashMap<>();
                                    for (int l = 0; l <= tempMaxWidthKey; l++) {
                                        tempArrayList = new ArrayList<EnemyShipObject>();
                                        tempEnemyShipObjectHashMapRight.put(l, tempArrayList);
                                    }
                                    tempEnemyShipObjectHashMapLeft.put(k, tempEnemyShipObjectHashMapRight);
                                }
                            }
                        } else {
                            tempEnemyShipObjectHashMapLeft = new HashMap<>();
                            for (int k = 0; k <= tempMaxWidthKey; k++) {
                                tempEnemyShipObjectHashMapRight = new HashMap<>();
                                for (int l = 0; l <= tempMaxWidthKey; l++) {
                                    tempArrayList = new ArrayList<EnemyShipObject>();
                                    tempEnemyShipObjectHashMapRight.put(l, tempArrayList);
                                }
                                tempEnemyShipObjectHashMapLeft.put(k, tempEnemyShipObjectHashMapRight);
                            }
                            tempEnemyShipObjectHashMapBottom.put(j, tempEnemyShipObjectHashMapLeft);
                        }
                    }
                } else {
                    tempEnemyShipObjectHashMapBottom = new HashMap<>();
                    for (int j = 0; j <= tempMaxHeightKey; j++) {
                        tempEnemyShipObjectHashMapLeft = new HashMap<>();
                        for (int k = 0; k <= tempMaxWidthKey; k++) {
                            tempEnemyShipObjectHashMapRight = new HashMap<>();
                            for (int l = 0; l <= tempMaxWidthKey; l++) {
                                tempArrayList = new ArrayList<EnemyShipObject>();
                                tempEnemyShipObjectHashMapRight.put(l, tempArrayList);
                            }
                            tempEnemyShipObjectHashMapLeft.put(k, tempEnemyShipObjectHashMapRight);
                        }
                        tempEnemyShipObjectHashMapBottom.put(j, tempEnemyShipObjectHashMapLeft);
                    }
                    enemyObjectHashMap.put(i, tempEnemyShipObjectHashMapBottom);
                }
            }
        }
    }

    void removeEnemyShipsCoincidingWithGivenBullet(@NotNull Bullet bullet) {
        bulletTop = bullet.getLocationTop();
        bulletLeft = bullet.getLocationLeft();
        bulletRight = bullet.getLocationRight();
        bulletBottom = bullet.getLocationBottom();
        bulletTopKey = (int) (bulletTop / heightScaleValue);
        bulletBottomKey = (int) (bulletBottom / heightScaleValue);
        bulletLeftKey = (int) (bulletLeft / widthScaleValue);
        bulletRightKey = (int) (bulletRight / widthScaleValue);

        for (int i = 0; i <= bulletBottomKey; i++) {
            for (int j = bulletTopKey; j <= maxHeightKey; j++) {
                for (int k = 0; k <= bulletRightKey; k++) {
                    for (int l = bulletLeftKey; l <= maxWidthKey; l++) {
                        for (int m = 0; m < (((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).size()); m++) {
                            tempEnemyShipObject = (((((enemyObjectHashMap.get(i)).get(j)).get(k)).get(l)).get(m));

                            // There is this offset of 5 in if statement. This is to ensure that it looks as though the bullet has hit the enemy
                            // and not the it vanished after coming close to the enemy.
                            if ((tempEnemyShipObject.getEnemyShipBottom() >= (bulletTop + 5)) && (tempEnemyShipObject.getEnemyShipTop() <= (bulletBottom - 5))
                                    && (tempEnemyShipObject.getEnemyShipLeft() <= (bulletRight - 5)) && (tempEnemyShipObject.getEnemyShipRight() >= (bulletLeft + 5))) {
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

    int getEnemyShipObjectListSizeWithKeys(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight) {
        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).size());
    }

    EnemyShipObject getEnemyShipObjectWithKeysAndIndex(int shipKeyTop, int shipKeyBottom, int shipKeyLeft, int shipKeyRight, int index) {
        return (((((enemyObjectHashMap.get(shipKeyTop)).get(shipKeyBottom)).get(shipKeyLeft)).get(shipKeyRight)).get(index));
    }

    void stopAllEnemyBullets() {
        for (int i = 0; i <= maxHeightKey; i++) {
            for (int j = 0; j <= maxHeightKey; j++) {
                for (int k = 0; k <= maxWidthKey; k++) {
                    for (int l = 0; l <= maxWidthKey; l++) {
                        int len = getEnemyShipObjectListSizeWithKeys(i, j, k, l);
                        for (int m = 0; m < len; m++) {
                            getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).stopBuildingBullets();
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
                        int len = getEnemyShipObjectListSizeWithKeys(i, j, k, l);
                        for (int m = 0; m < len; m++) {
                            getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).startBuildingBullets(0, 0);
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