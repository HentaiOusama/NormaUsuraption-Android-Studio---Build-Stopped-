package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

class FriendlyShipObject implements Runnable {
    private Context context;

    // Friendly ship related variables
    private Bitmap friendlyShipImage;
    private int lifeLevel;
    private float friendlyShipTop, friendlyShipLeft, friendlyShipBottom, friendlyShipRight;
    private float friendlyShipHeight, friendlyShipWidth, friendlyBulletHeight, friendlyBulletWidth;

    // Friendly bullet related variables
    private String friendlyBulletImageName;
    private int totalNumberOfFrames;
    private int friendlyBulletFrameType; // 1 = looping and 2 = non Looping
    private int friendlyBulletUpSpeed, friendlyBulletDownSpeed, friendlyBulletRightSpeed, friendlyBulletLeftSpeed;
    private int millisBeforeNextFriendlyBullet;
    private myQueue<Bullet> friendlyBulletQueue = new myQueue<>();
    private boolean shouldBuildFriendlyBullets = false;
    private Bullet tempBullet;
    private int bulletQueueLength;
    private final int MAX_FRAME_TIME;

    // Misc Variables and Overridden run Method
    private String TAG = "MY DEBUG TAG";
    private float canvas_bottom, canvas_right;

    @Override
    public void run() {
        double shipWidthHalf = (friendlyShipImage.getScaledWidth(friendlyShipImage.getDensity()) * 0.5);
        double shipHeightHalf = (friendlyShipImage.getScaledHeight(friendlyShipImage.getDensity()) * 0.5);
        double bulletWidthHalf = (friendlyBulletWidth) * 0.5;
        double bulletHeightHalf = (friendlyBulletHeight) * 0.5;
        bulletPositionUpdatingThread.start();

        do {
            long previousBulletStartTime = System.nanoTime() / 1000000;

            switch (lifeLevel) {
                case 1:
                    buildLevel1Bullets(shipWidthHalf, shipHeightHalf, bulletWidthHalf, bulletHeightHalf);
                    break;

                case 2:
                    break;
            }

            long previousBulletTimeSpan = (System.nanoTime() / 1000000) - previousBulletStartTime;
            try {
                Thread.sleep(millisBeforeNextFriendlyBullet - previousBulletTimeSpan);
            } catch (InterruptedException e) {
                Log.e(TAG, "run: ", e);
            }
        }
        while (shouldBuildFriendlyBullets);
    }

    private Thread bulletBuildingThread = new Thread(this, "Bullet Building Thread");
    private Thread bulletPositionUpdatingThread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (shouldBuildFriendlyBullets) {
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


    // Constructor
    FriendlyShipObject(Context context, @NotNull Bitmap friendlyShipImage, int lifeLevel, float friendlyShipTop, float friendlyShipLeft,
                       String friendlyBulletImageName, int totalNumberOfFrames, int friendlyBulletFrameType, int friendlyBulletUpSpeed,
                       int friendlyBulletDownSpeed, int friendlyBulletRightSpeed, int friendlyBulletLeftSpeed, int millisBeforeNextFriendlyBullet,
                       int FPS) {
        this.context = context;
        this.friendlyShipImage = friendlyShipImage;
        this.lifeLevel = lifeLevel;
        this.friendlyShipTop = friendlyShipTop;
        this.friendlyShipLeft = friendlyShipLeft;
        this.friendlyBulletImageName = friendlyBulletImageName;
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.friendlyBulletFrameType = friendlyBulletFrameType;
        this.friendlyBulletUpSpeed = friendlyBulletUpSpeed;
        this.friendlyBulletDownSpeed = friendlyBulletDownSpeed;
        this.friendlyBulletRightSpeed = friendlyBulletRightSpeed;
        this.friendlyBulletLeftSpeed = friendlyBulletLeftSpeed;
        this.millisBeforeNextFriendlyBullet = millisBeforeNextFriendlyBullet;
        MAX_FRAME_TIME = (int) (1000.0 / FPS);
        friendlyShipHeight = friendlyShipImage.getScaledHeight(friendlyShipImage.getDensity());
        friendlyShipWidth = friendlyShipImage.getScaledWidth(friendlyShipImage.getDensity());
        friendlyShipBottom = this.friendlyShipTop + friendlyShipHeight;
        friendlyShipRight = this.friendlyShipLeft + friendlyShipWidth;
        bulletQueueLength = 0;
        tempBullet = new Bullet(context, friendlyBulletImageName, totalNumberOfFrames, friendlyBulletFrameType, friendlyBulletUpSpeed,
                friendlyBulletDownSpeed, friendlyBulletRightSpeed, friendlyBulletLeftSpeed, millisBeforeNextFriendlyBullet);
        friendlyBulletHeight = tempBullet.getBulletHeight();
        friendlyBulletWidth = tempBullet.getBulletWidth();
    }


    // Ship related methods
    Bitmap getFriendlyShipImage() {
        return friendlyShipImage;
    }

    int getLifeLevel() {
        return lifeLevel;
    }

    float getFriendlyShipTop() {
        return friendlyShipTop;
    }

    float getFriendlyShipLeft() {
        return friendlyShipLeft;
    }

    float getFriendlyShipBottom() {
        return friendlyShipBottom;
    }

    float getFriendlyShipRight() {
        return friendlyShipRight;
    }

    float getFriendlyShipHeight() {
        return friendlyShipHeight;
    }

    float getFriendlyShipWidth() {
        return friendlyShipWidth;
    }

    void setFriendlyShipTop(float friendlyShipTop) {
        this.friendlyShipTop = friendlyShipTop;
        friendlyShipBottom = this.friendlyShipTop + friendlyShipHeight;
    }

    void setFriendlyShipLeft(float friendlyShipLeft) {
        this.friendlyShipLeft = friendlyShipLeft;
        friendlyShipRight = this.friendlyShipLeft + friendlyShipWidth;
    }

    void setLifeLevel(int lifeLevel) {
        this.lifeLevel = lifeLevel;
    }

    void setFriendlyShipImage(@NotNull Bitmap friendlyShipImage) {
        this.friendlyShipImage = friendlyShipImage;
        friendlyShipHeight = friendlyShipImage.getScaledHeight(friendlyShipImage.getDensity());
        friendlyShipWidth = friendlyShipImage.getScaledWidth(friendlyShipImage.getDensity());
        setFriendlyShipTop(friendlyShipTop);
        setFriendlyShipLeft(friendlyShipLeft);
    }


    // Bullet related methods
    int getFriendlyBulletQueueSize() {
        return bulletQueueLength;
    }

    Bitmap getFriendlyBulletFrameOfBulletAtIndex(int index) {
        return friendlyBulletQueue.get(index).getBulletFrame();
    }

    float getFriendlyBulletHeight() {
        return friendlyBulletHeight;
    }

    float getFriendlyBulletWidth() {
        return friendlyBulletWidth;
    }

    private void updateBulletPositions() {
        for (int i = 0; i < bulletQueueLength; i++) {
            tempBullet = friendlyBulletQueue.get(i);
            float tempTop = (tempBullet.getLocationTop() - tempBullet.getUpSpeed() + tempBullet.getDownSpeed());
            float tempLeft = (tempBullet.getLocationLeft() + tempBullet.getRightSpeed() - tempBullet.getLeftSpeed());
            if (tempTop <= 0 || tempTop >= canvas_bottom) {
                friendlyBulletQueue.Dequeue(i);
                bulletQueueLength--;
                i--;
            } else if (tempLeft <= 0 || tempLeft >= canvas_right) {
                friendlyBulletQueue.Dequeue(i);
                bulletQueueLength--;
                i--;
            } else {
                friendlyBulletQueue.get(i).setLocationTop(tempTop);
                friendlyBulletQueue.get(i).setLocationLeft(tempLeft);
            }
        }
    }


    // Bullet thread related methods
    private void buildLevel1Bullets(double shipWidthHalf, double shipHeightHalf, double bulletWidthHalf, double bulletHeightHalf) {

        tempBullet = new Bullet(context, friendlyBulletImageName, totalNumberOfFrames, friendlyBulletFrameType, friendlyBulletUpSpeed,
                friendlyBulletDownSpeed, friendlyBulletRightSpeed, friendlyBulletLeftSpeed, millisBeforeNextFriendlyBullet);
        tempBullet.setLocationLeft((float) (friendlyShipLeft + shipWidthHalf - bulletWidthHalf));
        tempBullet.setLocationTop((float) (friendlyShipTop + shipHeightHalf - bulletHeightHalf));
        friendlyBulletQueue.Enqueue(tempBullet);
        bulletQueueLength++;
    }

    void startBuildingBullets(float canvas_bottom, float canvas_right) {
        shouldBuildFriendlyBullets = true;
        this.canvas_bottom = canvas_bottom;
        this.canvas_right = canvas_right;
        bulletBuildingThread.start();
    }

    void stopBuildingBullets() {
        shouldBuildFriendlyBullets = false;
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
        } catch (Exception e) {
            Log.e(TAG, "stopBuildingBullets: ", e);
        }
    }

    myQueue<Bullet> getFriendlyBulletQueue() {
        return friendlyBulletQueue;
    }
}