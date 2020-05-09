package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

class EnemyLevelBuilder {

    private EnemyShipObjectHashMap enemyHashMap, tempEnemyHashMap;
    private float[] constructionValues = new float[6];
    private boolean shouldUpdateHashMap = false;
    private float canvas_bottom, canvas_right;
    private Context context;
    private PresetMovementPatterns enemyMovementBuilder = new PresetMovementPatterns();
    private int currentStageLevel;
    private final String TAG = "MY DEBUG TAG";
    private EnemyShipObject tempEnemyObject;
    private float leftCenterOffset = -50;
    private final int MAX_FRAME_TIME;


    // Constructor
    EnemyLevelBuilder(Context context, float canvas_right, float canvas_bottom, EnemyShipObjectHashMap enemyHashMap,
                      int MAX_FRAME_TIME) {
        this.context = context;
        this.canvas_right = canvas_right;
        this.canvas_bottom = canvas_bottom;
        this.enemyHashMap = enemyHashMap;
        this.MAX_FRAME_TIME = MAX_FRAME_TIME;
    }


    // This thread handles the building of the level
    private Thread levelBuildingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            switch (currentStageLevel) {
                case 1:
                    buildLevel1();
                    break;

                case 2:
                    buildLevel2();
                    break;

                default:
            }
        }
    };


    // This thread handles updating the hashMap
    private Thread hashMapUpdatingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (shouldUpdateHashMap) {
                long updateStartTime = System.nanoTime();
                enemyHashMap.getConstructionValuesIn(constructionValues);
                tempEnemyHashMap = new EnemyShipObjectHashMap(constructionValues[0], constructionValues[1], (int) constructionValues[2],
                        (int) constructionValues[3], constructionValues[4], constructionValues[5]);
                int maxHeightKey = tempEnemyHashMap.getMaxHeightKey();
                int maxWidthKey = tempEnemyHashMap.getMaxWidthKey();
                for(int i = -3; i <= maxHeightKey; i++) {
                    for (int j = -3; j <= maxHeightKey; j++) {
                        for (int k = -3; k <= maxWidthKey; k++) {
                            for (int l = -3; l <= maxWidthKey; l++) {
                                int listLength = enemyHashMap.getEnemyShipObjectListSizeWithKeys(i, j, k, l);
                                for(int m = 0; m < listLength; m++) {
                                    try {
                                        EnemyShipObject temp = enemyHashMap.getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m);
                                        tempEnemyHashMap.addEnemyShipObject(temp, temp.getEnemyShipTop(), temp.getEnemyShipBottom(),
                                                temp.getEnemyShipLeft(), temp.getEnemyShipRight(), false);
                                    } catch (Exception e) {
                                        Log.e(TAG, "hashMapUpdaterIndexError :- ", e);
                                    }
                                }
                            }
                        }
                    }
                }

                try {
                    enemyHashMap = (EnemyShipObjectHashMap) tempEnemyHashMap.clone();
                } catch (CloneNotSupportedException e) {
                    Log.e(TAG, "Error while cloning:- ", e);
                }
                long updatingTime = (System.nanoTime() - updateStartTime) / 1000000;
                if (updatingTime < MAX_FRAME_TIME) {
                    try {
                        Thread.sleep(MAX_FRAME_TIME - updatingTime);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
            }
        }
    };


    // This is the main method called by the GamePlayView asking this class to build the HashMap given in the constructor
    void startBuildingLevel(int currentStageLevel) {
        this.currentStageLevel = currentStageLevel;
        levelBuildingThread.start();
    }

    void stopBuildingLevel() {
        while (true) {
            try {
                levelBuildingThread.join();
                break;
            } catch (Exception e) {
                Log.e(TAG, "stopBuildingLevel: ", e);
            }
        }
        shouldUpdateHashMap = false;
        while (true) {
            try {
                hashMapUpdatingThread.join();
                break;
            } catch (Exception e) {
                Log.e(TAG, "stopBuildingLevel: ", e);
            }
        }
    }

    // Builds the image of the enemyShip
    private Bitmap buildEnemyImage(String imageName, float heightAsPercentageOfScreen) {
        Bitmap tempEnemyShipBitmap = BitmapFactory.decodeResource(context.getResources(),
                context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));

        int originalHeight = tempEnemyShipBitmap.getScaledHeight(tempEnemyShipBitmap.getDensity());
        int originalWidth = tempEnemyShipBitmap.getScaledWidth(tempEnemyShipBitmap.getDensity());
        float heightRequired = heightAsPercentageOfScreen * canvas_bottom / 100;
        float widthRequired = originalWidth / (originalHeight / heightRequired);
        // So we'll resize the image now.
        tempEnemyShipBitmap = Bitmap.createScaledBitmap(tempEnemyShipBitmap, (int) widthRequired, (int) heightRequired, true);
        return tempEnemyShipBitmap;
    }

    // Builds Level 1
    private void buildLevel1() {
        Bitmap tempEnemyShipBitmap2, tempEnemyShipBitmap1 = buildEnemyImage("enemy_ship_1", (float) 5.5);

        tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap1, 100, 0,
                -170, enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(1, 0, -170, 200, 150),
                "red_animated_bullet_", 10, 1, 0,
                50, 0, 0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight(), true);

        tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap1, 100, 0,
                canvas_right, enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(1, 0, canvas_right, 200,
                        canvas_right - 150 - tempEnemyObject.getEnemyShipWidth()), "red_animated_bullet_",
                10, 1, 0, 50, 0,
                0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight(), true);
        try {
            Thread.sleep(40);
        } catch (Exception e) {
            Log.e(TAG, "buildLevel1: Error while sleeping the Thread", e);
        }

        tempEnemyShipBitmap2 = buildEnemyImage("enemy_ship_1", (float) 9.0);
        tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap2, 100, -200,
                ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset,
                enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(2, -200,
                        ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset, 200,
                        ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset), "red_animated_bullet_",
                10, 1, 0, 50, 0,
                0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight(), true);
        try {
            Thread.sleep(40);
        } catch (Exception e) {
            Log.e(TAG, "buildLevel1: Error while sleeping the Thread", e);
        }


        // In the end, start hashMapUpdater :-
        shouldUpdateHashMap = true;
        hashMapUpdatingThread.start();
    }


    // Builds Level 2  <--- Under Construction
    private void buildLevel2() {
        Bitmap tempEnemyShipBitmap2, tempEnemyShipBitmap1 = buildEnemyImage("/X/X INPUT NAME HERE /X/X", (float) 5.5);

        tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap1, 100, 0,
                -170, enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(1, 0, -170, 200, 150),
                "red_animated_bullet_", 10, 1, 0,
                50, 0, 0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight(), true);

        tempEnemyShipBitmap2 = buildEnemyImage("/X/X INPUT NAME HERE /X/X", (float) 9.0);
        tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap2, 100, -200,
                ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset,
                enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(2, -200,
                        ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset, 200,
                        ((canvas_right) / 2) - ((tempEnemyObject.getEnemyShipWidth()) / 2) + leftCenterOffset), "red_animated_bullet_",
                10, 1, 0, 50, 0,
                0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight(), true);
        try {
            Thread.sleep(40);
        } catch (Exception e) {
            Log.e(TAG, "buildLevel1: Error while sleeping the Thread", e);
        }


        // In the end, start hashMapUpdater :-
        shouldUpdateHashMap = true;
        hashMapUpdatingThread.start();
    }
}