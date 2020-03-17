package com.hentai_productions.normausurpation;

import android.graphics.Bitmap;

class Bullet
{
    private Bitmap bulletImage;
    private int upSpeed, downSpeed, rightSpeed, leftSpeed;
    private int bulletHeight, bulletWidth;
    private int locationTop, locationBottom, locationLeft, locationRight;
    private int millisBeforeNextBullet;

    Bullet(Bitmap bulletImage, int upSpeed, int downSpeed, int rightSpeed, int leftSpeed, int millisBeforeNextBullet)
    {
        this.bulletImage = bulletImage;
        this.upSpeed = upSpeed;
        this.downSpeed = downSpeed;
        this.rightSpeed = rightSpeed;
        this.leftSpeed = leftSpeed;
        this.millisBeforeNextBullet = millisBeforeNextBullet;
        bulletHeight = bulletImage.getScaledHeight(bulletImage.getDensity());
        bulletWidth = bulletImage.getScaledWidth(bulletImage.getDensity());
    }

    Bitmap getBulletImage()
    {
        return bulletImage;
    }

    int getUpSpeed()
    {
        return upSpeed;
    }

    int getDownSpeed()
    {
        return downSpeed;
    }

    int getLeftSpeed()
    {
        return leftSpeed;
    }

    int getRightSpeed()
    {
        return rightSpeed;
    }

    int getLocationTop()
    {
        return locationTop;
    }

    int getLocationLeft()
    {
        return locationLeft;
    }

    int getLocationBottom()
    {
        return locationBottom;
    }

    int getLocationRight()
    {
        return locationRight;
    }

    void setLocationTop(int top)
    {
        locationTop = top;
        locationBottom = locationTop + bulletHeight;
    }

    void setLocationLeft(int left)
    {
        locationLeft = left;
        locationRight = locationLeft + bulletWidth;
    }
    
    int getMillisBeforeNextBullet()
    {
        return millisBeforeNextBullet;
    }
}