package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

class EnemyShipObject implements Runnable {
    private Context context;

    // Enemy ship related variables
    private Bitmap enemyShipImage;
    private int currentLife;
    private float enemyShipTop, enemyShipLeft, enemyShipBottom, enemyShipRight;
    private float enemyShipHeight, enemyShipWidth, enemyBulletHeight, enemyBulletWidth;
    private myQueue<EnemyShipSpeedSet> enemyShipMovementPattern = new myQueue<>();
    private boolean shouldMoveEnemyShip = false;

    // Enemy bullet related variables
    private String enemyBulletImageName;
    private int totalNumberOfBulletFrames;
    private int enemyBulletFrameType; // 1 = looping and 2 = non Looping
    private int enemyBulletUpSpeed, enemyBulletDownSpeed, enemyBulletRightSpeed, enemyBulletLeftSpeed;
    private int millisBeforeNextEnemyBullet;
    private myQueue<Bullet> enemyBulletQueue = new myQueue<>();
    private boolean shouldBuildEnemyBullets = false;
    private Bullet tempBullet;
    private int bulletQueueLength;
    private final int MAX_FRAME_TIME;

    // Misc Variables
    private String TAG = "MY DEBUG TAG";
    private float canvas_bottom, canvas_right;

    // Threading Stuff below
    @Override
    public void run() {
        double shipWidthHalf = (enemyShipImage.getScaledWidth(enemyShipImage.getDensity()) * 0.5);
        double shipHeightHalf = (enemyShipImage.getScaledHeight(enemyShipImage.getDensity()) * 0.5);
        double bulletWidthHalf = (enemyBulletWidth) * 0.5;
        double bulletHeightHalf = (enemyBulletHeight) * 0.5;
        bulletPositionUpdatingThread.start();
        enemyShipPositionUpdatingThread.start();

        do {
            long previousBulletStartTime = System.nanoTime() / 1000000;

            switch (currentLife) {
                case 1:
                    buildLevel1Bullets(shipWidthHalf, shipHeightHalf, bulletWidthHalf, bulletHeightHalf);
                    break;

                case 2:
                    break;
            }

            long previousBulletTimeSpan = (System.nanoTime() / 1000000) - previousBulletStartTime;
            try {
                Thread.sleep(millisBeforeNextEnemyBullet - previousBulletTimeSpan);
            } catch (InterruptedException e) {
                Log.e(TAG, "run: ", e);
            }
        }
        while (shouldBuildEnemyBullets);
    }

    private Thread bulletBuildingThread = new Thread(this, "Bullet Building Thread");
    private Thread bulletPositionUpdatingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (shouldBuildEnemyBullets) {
                long updateStartTime = System.nanoTime();
                updateBulletPositions();
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
    private Thread enemyShipPositionUpdatingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            EnemyShipSpeedSet currentEnemyShipSpeedSet;
            while(shouldMoveEnemyShip) {
                long updateStartTime = System.nanoTime();
                currentEnemyShipSpeedSet = enemyShipMovementPattern.getNextEnemyShipSpeedSet();
                updateShipPosition(currentEnemyShipSpeedSet);
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


    // Constructor
    EnemyShipObject(Context context, @NotNull Bitmap enemyShipImage, int currentLife, float enemyShipTop, float enemyShipLeft,
                    myQueue<EnemyShipSpeedSet> enemyShipMovementPattern, String enemyBulletImageName, int totalNumberOfBulletFrames,
                    int enemyBulletFrameType, int enemyBulletUpSpeed, int enemyBulletDownSpeed, int enemyBulletRightSpeed,
                    int enemyBulletLeftSpeed, int millisBeforeNextEnemyBullet, int FPS) {
        this.context = context;
        this.enemyShipImage = enemyShipImage;
        this.currentLife = currentLife;
        this.enemyShipTop = enemyShipTop;
        this.enemyShipLeft = enemyShipLeft;
        this.enemyShipMovementPattern = enemyShipMovementPattern;
        this.enemyBulletImageName = enemyBulletImageName;
        this.totalNumberOfBulletFrames = totalNumberOfBulletFrames;
        this.enemyBulletFrameType = enemyBulletFrameType;
        this.enemyBulletUpSpeed = enemyBulletUpSpeed;
        this.enemyBulletDownSpeed = enemyBulletDownSpeed;
        this.enemyBulletRightSpeed = enemyBulletRightSpeed;
        this.enemyBulletLeftSpeed = enemyBulletLeftSpeed;
        this.millisBeforeNextEnemyBullet = millisBeforeNextEnemyBullet;
        MAX_FRAME_TIME = (int) (1000.0 / FPS);
        enemyShipHeight = enemyShipImage.getScaledHeight(enemyShipImage.getDensity());
        enemyShipWidth = enemyShipImage.getScaledWidth(enemyShipImage.getDensity());
        enemyShipBottom = this.enemyShipTop + enemyShipHeight;
        enemyShipRight = this.enemyShipLeft + enemyShipWidth;
        bulletQueueLength = 0;
        tempBullet = new Bullet(context, enemyBulletImageName, totalNumberOfBulletFrames, enemyBulletFrameType, enemyBulletUpSpeed,
                enemyBulletDownSpeed, enemyBulletRightSpeed, enemyBulletLeftSpeed, millisBeforeNextEnemyBullet);
        enemyBulletHeight = tempBullet.getBulletHeight();
        enemyBulletWidth = tempBullet.getBulletWidth();
    }


    // Ship related methods
    Bitmap getEnemyShipImage() {
        return enemyShipImage;
    }

    int getCurrentLife() {
        return currentLife;
    }

    float getEnemyShipTop() {
        return enemyShipTop;
    }

    float getEnemyShipBottom() {
        return enemyShipBottom;
    }

    float getEnemyShipLeft() {
        return enemyShipLeft;
    }

    float getEnemyShipRight() {
        return enemyShipRight;
    }

    float getEnemyShipHeight() {
        return enemyShipHeight;
    }

    float getEnemyShipWidth() {
        return enemyShipWidth;
    }

    private void setEnemyShipTop(float enemyShipTop) {
        this.enemyShipTop = enemyShipTop;
        enemyShipBottom = this.enemyShipTop + enemyShipHeight;
    }

    private void setEnemyShipLeft(float enemyShipLeft) {
        this.enemyShipLeft = enemyShipLeft;
        enemyShipRight = this.enemyShipLeft + enemyShipWidth;
    }

    void setCurrentLife(int currentLife) {
        this.currentLife = currentLife;
    }

    void setEnemyShipImage(@NotNull Bitmap enemyShipImage) {
        this.enemyShipImage = enemyShipImage;
        enemyShipHeight = enemyShipImage.getScaledHeight(enemyShipImage.getDensity());
        enemyShipWidth = enemyShipImage.getScaledWidth(enemyShipImage.getDensity());
        setEnemyShipTop(enemyShipTop);
        setEnemyShipLeft(enemyShipLeft);
    }

    private void updateShipPosition(@NotNull EnemyShipSpeedSet currentEnemyShipSpeedSet) {
        enemyShipTop += (currentEnemyShipSpeedSet.getEnemyShipDownSpeed() - currentEnemyShipSpeedSet.getEnemyShipUpSpeed());
        enemyShipBottom = enemyShipTop + enemyShipHeight;
        enemyShipLeft += (currentEnemyShipSpeedSet.getEnemyShipRightSpeed() -currentEnemyShipSpeedSet.getEnemyShipLeftSpeed());
        enemyShipRight = enemyShipLeft + enemyShipWidth;
    }


    // Bullet related methods
    int getEnemyBulletQueueSize() {
        return bulletQueueLength;
    }

    Bitmap getEnemyBulletFrameOfBulletAtIndex(int index) {
        return enemyBulletQueue.get(index).getBulletFrame();
    }

    float getEnemyBulletHeight() {
        return enemyBulletHeight;
    }

    float getEnemyBulletWidth() {
        return enemyBulletWidth;
    }

    private void updateBulletPositions() {
        for (int i = 0; i < bulletQueueLength; i++) {
            tempBullet = enemyBulletQueue.get(i);
            float tempTop = (tempBullet.getLocationTop() - tempBullet.getUpSpeed() + tempBullet.getDownSpeed());
            float tempLeft = (tempBullet.getLocationLeft() + tempBullet.getRightSpeed() - tempBullet.getLeftSpeed());
            if (tempTop <= 0 || tempTop >= canvas_bottom) {
                enemyBulletQueue.Dequeue(i);
                bulletQueueLength--;
                i--;
            } else if (tempLeft <= 0 || tempLeft >= canvas_right) {
                enemyBulletQueue.Dequeue(i);
                bulletQueueLength--;
                i--;
            } else {
                enemyBulletQueue.get(i).setLocationTop(tempTop);
                enemyBulletQueue.get(i).setLocationLeft(tempLeft);
            }
        }
    }

    private void buildLevel1Bullets(double shipWidthHalf, double shipHeightHalf, double bulletWidthHalf, double bulletHeightHalf) {

        tempBullet = new Bullet(context, enemyBulletImageName, totalNumberOfBulletFrames, enemyBulletFrameType, enemyBulletUpSpeed,
                enemyBulletDownSpeed, enemyBulletRightSpeed, enemyBulletLeftSpeed, millisBeforeNextEnemyBullet);
        tempBullet.setLocationLeft((float) (enemyShipLeft + shipWidthHalf - bulletWidthHalf));
        tempBullet.setLocationTop((float) (enemyShipTop + shipHeightHalf - bulletHeightHalf));
        enemyBulletQueue.Enqueue(tempBullet);
        bulletQueueLength++;
    }


    // Bullet thread related methods. These start and stop threads also handles movement of ship
    void startBuildingBullets(float canvas_bottom, float canvas_right) {
        shouldBuildEnemyBullets = true;
        shouldMoveEnemyShip = true;
        this.canvas_bottom = canvas_bottom;
        this.canvas_right = canvas_right;
        bulletBuildingThread.start();
    }

    void stopBuildingBullets() {
        shouldBuildEnemyBullets = false;
        shouldMoveEnemyShip = false;
        try {
            while (true) {
                try {
                    bulletBuildingThread.join(500);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "stopBuildingBullets: Bullet Building Thread", e);
                }
            }
            while (true) {
                try {
                    bulletPositionUpdatingThread.join(500);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "stopBuildingBullets: Bullet Building Thread", e);
                }
            }
            while (true) {
                try {
                    enemyShipPositionUpdatingThread.join(500);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "stopBuildingBullets: Bullet Building Thread", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "stopBuildingBullets: ", e);
        }
    }

    myQueue<Bullet> getEnemyBulletQueue() {
        return enemyBulletQueue;
    }
}