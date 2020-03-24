package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

class EnemyObject {
    private Context context;

    // Enemy ship related variables
    private Bitmap enemyShipImage;
    private float enemyShipTop, enemyShipLeft, enemyShipBottom, enemyShipRight;
    private float enemyShipHeight, enemyShipWidth, enemyBulletHeight, enemyBulletWidth;

    // Enemy bullet related variables
    private String enemyBulletImageName;
    private int totalNumberOfFrames;
    private int enemyBulletFrameType; // 1 = looping and 2 = non Looping
    private int enemyBulletUpSpeed, enemyBulletDownSpeed, enemyBulletRightSpeed, enemyBulletLeftSpeed;
    private int millisBeforeNextEnemyBullet;
    private int millisBeforeNextEnemyShip;
    private myQueue<Bullet> enemyBulletQueue = new myQueue<>();
    private Thread bulletThread;
    private boolean shouldBuildEnemyBullets = false;
    private Bullet tempBullet;
    private long previousBulletStartTime, previousBulletTimeSpan;
    private int bulletQueueLength = 0;
    private float tempLeft, tempTop;

    // Misc Variables
    private String TAG = "MY DEBUG TAG";
    private float canvas_bottom, canvas_right;
    private Thread bulletPositionUpdatingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            for (int i = 0; i < bulletQueueLength; i++) {
                tempBullet = enemyBulletQueue.get(i);
                tempTop = (tempBullet.getLocationLeft() + tempBullet.getRightSpeed() - tempBullet.getLeftSpeed());
                tempLeft = (tempBullet.getLocationTop() - tempBullet.getUpSpeed() + tempBullet.getDownSpeed());

                if (tempTop <= -5 || tempTop >= canvas_bottom) {
                    enemyBulletQueue.Dequeue(i);
                    bulletQueueLength--;
                    i--;
                } else if (tempLeft <= -5 || tempLeft >= canvas_right) {
                    enemyBulletQueue.Dequeue(i);
                    bulletQueueLength--;
                    i--;
                } else {
                    enemyBulletQueue.get(i).setLocationTop(tempTop);
                    enemyBulletQueue.get(i).setLocationLeft(tempLeft);
                }
            }
        }
    };


    // Constructor
    EnemyObject(Context context, @NotNull Bitmap enemyShipImage, float enemyShipTop, float enemyShipLeft, String enemyBulletImageName,
                int totalNumberOfFrames, int enemyBulletFrameType, int enemyBulletUpSpeed, int enemyBulletDownSpeed,
                int enemyBulletRightSpeed, int enemyBulletLeftSpeed, int millisBeforeNextEnemyBullet, int millisBeforeNextEnemyShip) {
        this.context = context;
        this.enemyShipImage = enemyShipImage;
        this.enemyShipTop = enemyShipTop;
        this.enemyShipLeft = enemyShipLeft;
        this.enemyBulletImageName = enemyBulletImageName;
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.enemyBulletFrameType = enemyBulletFrameType;
        this.enemyBulletUpSpeed = enemyBulletUpSpeed;
        this.enemyBulletDownSpeed = enemyBulletDownSpeed;
        this.enemyBulletRightSpeed = enemyBulletRightSpeed;
        this.enemyBulletLeftSpeed = enemyBulletLeftSpeed;
        this.millisBeforeNextEnemyBullet = millisBeforeNextEnemyBullet;
        this.millisBeforeNextEnemyShip = millisBeforeNextEnemyShip;
        enemyShipHeight = enemyShipImage.getScaledHeight(enemyShipImage.getDensity());
        enemyShipWidth = enemyShipImage.getScaledWidth(enemyShipImage.getDensity());
        enemyShipBottom = this.enemyShipTop + enemyShipHeight;
        enemyShipRight = this.enemyShipLeft + enemyShipWidth;
        makeBulletTread();
    }


    // Ship related methods
    Bitmap getEnemyShipImage() {
        return enemyShipImage;
    }

    float getEnemyShipTop() {
        return enemyShipTop;
    }

    float getEnemyShipLeft() {
        return enemyShipLeft;
    }

    float getEnemyShipBottom() {
        return enemyShipTop;
    }

    float getEnemyShipRight() {
        return enemyShipLeft;
    }

    float getEnemyShipHeight() {
        return enemyShipHeight;
    }

    float getEnemyShipWidth() {
        return enemyShipWidth;
    }

    void setEnemyShipTop(float enemyShipTop) {
        this.enemyShipTop = enemyShipTop;
        enemyShipBottom = this.enemyShipTop + enemyShipHeight;
    }

    void setEnemyShipLeft(float enemyShipLeft) {
        this.enemyShipLeft = enemyShipLeft;
        enemyShipRight = this.enemyShipLeft + enemyShipWidth;
    }

    int getMillisBeforeNextEnemyShip() {
        return millisBeforeNextEnemyShip;
    }


    // Bullet related methods
    int getEnemyBulletQueueSize() {
        return enemyBulletQueue.getSize();
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

    void updateEnemyBulletPositions(float canvas_bottom, float canvas_right) {
        this.canvas_bottom = canvas_bottom;
        this.canvas_right = canvas_right;
        bulletPositionUpdatingThread.start();
    }

    private void makeBulletTread() {
        bulletThread = new Thread() {
            @Override
            public void run() {
                super.run();
                double shipWidthHalf = (enemyShipImage.getScaledWidth(enemyShipImage.getDensity()) * 0.5);
                double shipHeightHalf = (enemyShipImage.getScaledHeight(enemyShipImage.getDensity()) * 0.5);
                double bulletWidthHalf = (enemyBulletWidth) * 0.5;
                double bulletHeightHalf = (enemyBulletHeight) * 0.5;

                do {
                    previousBulletStartTime = System.nanoTime() / 1000000;

                    tempBullet = new Bullet(context, enemyBulletImageName, totalNumberOfFrames, enemyBulletFrameType, enemyBulletUpSpeed,
                            enemyBulletDownSpeed, enemyBulletRightSpeed, enemyBulletLeftSpeed, millisBeforeNextEnemyBullet);

                    tempBullet.setLocationLeft((int) (enemyShipLeft + shipWidthHalf - bulletWidthHalf));
                    tempBullet.setLocationTop((int) (enemyShipTop + shipHeightHalf - bulletHeightHalf));
                    enemyBulletQueue.Enqueue(tempBullet);
                    previousBulletTimeSpan = (System.nanoTime() / 1000000) - previousBulletStartTime;
                    try {
                        Thread.sleep(millisBeforeNextEnemyBullet - previousBulletTimeSpan);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
                while (shouldBuildEnemyBullets);
            }
        };
    }

    void startBuildingBullets() {
        shouldBuildEnemyBullets = true;
        bulletThread.start();
    }

    void stopBuildingBullets() {
        shouldBuildEnemyBullets = false;
        try {
            bulletThread.join();
            bulletPositionUpdatingThread.join();
        } catch (Exception e) {
            Log.e(TAG, "stopBuildingBullets: ", e);
        }
    }

    myQueue<Bullet> getEnemyBulletQueue() {
        return enemyBulletQueue;
    }
}