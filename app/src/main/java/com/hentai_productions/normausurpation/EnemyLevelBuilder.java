package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

class EnemyLevelBuilder {

    private EnemyShipObjectHashMap enemyHashMap;
    private float canvas_bottom, canvas_right;
    private Context context;
    private PresetMovementPatterns enemyMovementBuilder = new PresetMovementPatterns();
    private int currentStageLevel;
    private final String TAG = "MY DEBUG TAG";
    
    
    // Constructor
    EnemyLevelBuilder(Context context, float canvas_right, float canvas_bottom, EnemyShipObjectHashMap enemyHashMap) {
        this.context = context;
        this.canvas_right = canvas_right;
        this.canvas_bottom = canvas_bottom;
        this.enemyHashMap = enemyHashMap;

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
        Bitmap tempEnemyShipBitmap = buildEnemyImage("enemy_ship_1", 8);

        EnemyShipObject tempEnemyObject = new EnemyShipObject(context, tempEnemyShipBitmap, 100, 0,
                -100, enemyMovementBuilder.getMovementPatterQueueForEnemyShipType(1),
                enemyMovementBuilder.getStraightIntroducingPatternForEnemyShip(1, 0, -100, 200, 400),
                "red_animated_bullet_", 10, 1, 0,
                50, 0, 0, 1000, 60);
        enemyHashMap.addEnemyShipObject(tempEnemyObject, tempEnemyObject.getEnemyShipTop(), tempEnemyObject.getEnemyShipBottom(),
                tempEnemyObject.getEnemyShipLeft(), tempEnemyObject.getEnemyShipRight());
        try { 
            Thread.sleep(1000);
        } catch (Exception e) {
            Log.e(TAG, "buildLevel1: Error while sleeping the Thread", e);
        }
    }

    // Builds Level 2
    private void buildLevel2() {

    }    
}
