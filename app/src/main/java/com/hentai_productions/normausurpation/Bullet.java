package com.hentai_productions.normausurpation;

import android.graphics.Bitmap;

public class Bullet
{
    private Bitmap bulletImage;
    private int upSpeed, downSpeed, rightSpeed, leftSpeed;
    private int locationTop, locationLeft;
    private int millisBeforeNextBullet;

    Bullet(Bitmap bulletImage, int upSpeed, int downSpeed, int rightSpeed, int leftSpeed, int millisBeforeNextBullet)
    {
        this.bulletImage = bulletImage;
        this.upSpeed = upSpeed;
        this.downSpeed = downSpeed;
        this.rightSpeed = rightSpeed;
        this.leftSpeed = leftSpeed;
        this.millisBeforeNextBullet = millisBeforeNextBullet;
    }

    public Bitmap getBulletImage()
    {
        return bulletImage;
    }

    public int getUpSpeed()
    {
        return upSpeed;
    }

    public int getDownSpeed()
    {
        return downSpeed;
    }

    public int getLeftSpeed()
    {
        return leftSpeed;
    }

    public int getRightSpeed()
    {
        return rightSpeed;
    }

    public int getLocationTop()
    {
        return locationTop;
    }

    public int getLocationLeft()
    {
        return locationLeft;
    }

    void setLocationTop(int top)
    {
        locationTop = top;
    }

    void setLocationLeft(int left)
    {
        locationLeft = left;
    }
    
    int getMillisBeforeNextBullet()
    {
        return millisBeforeNextBullet;
    }
}