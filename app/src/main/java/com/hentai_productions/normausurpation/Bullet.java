package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class Bullet {
    private Bitmap[] bulletImages;
    private int upSpeed, downSpeed, rightSpeed, leftSpeed;
    private float bulletHeight, bulletWidth;
    private float locationTop, locationBottom, locationLeft, locationRight;
    private int millisBeforeNextBullet;
    private int currentFrameNumber = -1;
    private int totalNumberOfFrames;
    private int frameType;  // 1 = looping and 2 = non Looping

    Bullet(Context context, String bulletImageName, int totalNumberOfFrames, int frameType, int upSpeed,
           int downSpeed, int rightSpeed, int leftSpeed, int millisBeforeNextBullet) {
        this.frameType = frameType;
        this.totalNumberOfFrames = totalNumberOfFrames;
        bulletImages = new Bitmap[totalNumberOfFrames];
        for (int i = 0; i < totalNumberOfFrames; i++) {
            bulletImages[i] = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier(bulletImageName + i, "drawable", context.getPackageName()));
        }
        this.upSpeed = upSpeed;
        this.downSpeed = downSpeed;
        this.rightSpeed = rightSpeed;
        this.leftSpeed = leftSpeed;
        this.millisBeforeNextBullet = millisBeforeNextBullet;
        bulletHeight = bulletImages[0].getScaledHeight(bulletImages[0].getDensity());
        bulletWidth = bulletImages[0].getScaledWidth(bulletImages[0].getDensity());
    }

    Bitmap getBulletFrame() {
        if (frameType == 1) {
            return getLoopBulletFrame();
        } else if (frameType == 2) {
            return getNonLoopBulletFrame();
        } else {
            return null;
        }
    }

    private Bitmap getLoopBulletFrame() {
        currentFrameNumber += 1;
        if (currentFrameNumber >= totalNumberOfFrames) {
            currentFrameNumber = 0;
        }
        return bulletImages[currentFrameNumber];
    }

    private Bitmap getNonLoopBulletFrame() {
        if (currentFrameNumber != totalNumberOfFrames) {
            currentFrameNumber += 1;
        }
        return bulletImages[currentFrameNumber];
    }

    int getUpSpeed() {
        return upSpeed;
    }

    int getDownSpeed() {
        return downSpeed;
    }

    int getLeftSpeed() {
        return leftSpeed;
    }

    int getRightSpeed() {
        return rightSpeed;
    }

    float getLocationTop() {
        return locationTop;
    }

    float getLocationLeft() {
        return locationLeft;
    }

    float getLocationBottom() {
        return locationBottom;
    }

    float getLocationRight() {
        return locationRight;
    }

    float getBulletHeight() {
        return bulletHeight;
    }

    float getBulletWidth() {
        return bulletWidth;
    }

    void setLocationTop(float top) {
        locationTop = top;
        locationBottom = locationTop + bulletHeight;
    }

    void setLocationLeft(float left) {
        locationLeft = left;
        locationRight = locationLeft + bulletWidth;
    }

    int getMillisBeforeNextBullet() {
        return millisBeforeNextBullet;
    }
}