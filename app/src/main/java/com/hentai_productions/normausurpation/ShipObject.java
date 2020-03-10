package com.hentai_productions.normausurpation;

import android.graphics.Bitmap;

public class ShipObject
{
    private Bitmap shipImage, bulletImage;
    private int bulletUpSpeed, bulletDownSpeed, bulletRightSpeed, bulletLeftSpeed;
    private int millisBeforeNextBullet;

    ShipObject(Bitmap shipImage, Bitmap bulletImage, int bulletUpSpeed, int bulletDownSpeed, int bulletRightSpeed, int bulletLeftSpeed, int millisBeforeNextBullet)
    {
        this.shipImage = shipImage;
        this.bulletImage = bulletImage;
        this.bulletUpSpeed = bulletUpSpeed;
        this.bulletDownSpeed = bulletDownSpeed;
        this.bulletRightSpeed = bulletRightSpeed;
        this.bulletLeftSpeed = bulletLeftSpeed;
        this.millisBeforeNextBullet = millisBeforeNextBullet;
    }

    public Bitmap getShipImage()
    {
        return shipImage;
    }

    public Bitmap getBulletImage()
    {
        return bulletImage;
    }

    public int getBulletUpSpeed()
    {
        return bulletUpSpeed;
    }

    public int getBulletDownSpeed()
    {
        return bulletDownSpeed;
    }

    public int getBulletLeftSpeed()
    {
        return bulletLeftSpeed;
    }

    public int getBulletRightSpeed()
    {
        return bulletRightSpeed;
    }

    public int getMillisBeforeNextBullet()
    {
        return millisBeforeNextBullet;
    }
}