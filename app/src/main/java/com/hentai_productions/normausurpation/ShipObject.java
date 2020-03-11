package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class ShipObject
{
    private Bitmap shipImage;
    private Bitmap[] bulletImages;
    private int bulletUpSpeed, bulletDownSpeed, bulletRightSpeed, bulletLeftSpeed;
    private int millisBeforeNextBullet;
    private int currentFrameNumber = -1;
    private int totalNumberOfFrames;
    private int shipHeight, shipWidth, bulletHeight, bulletWidth;

    ShipObject(Context context, Bitmap shipImage, String bulletImageName, int totalNumberOfFrames, int bulletUpSpeed, int bulletDownSpeed,
               int bulletRightSpeed, int bulletLeftSpeed, int millisBeforeNextBullet)
    {
        this.totalNumberOfFrames = totalNumberOfFrames;
        bulletImages = new Bitmap[totalNumberOfFrames];
        for(int i = 0; i < totalNumberOfFrames; i++)
        {
            bulletImages[i] = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier(bulletImageName + i,
                            "drawable", context.getPackageName()));
        }
        shipHeight = shipImage.getScaledHeight(shipImage.getDensity());
        shipWidth = shipImage.getScaledWidth(shipImage.getDensity());
        bulletHeight = bulletImages[0].getScaledHeight(bulletImages[0].getDensity());
        bulletWidth = bulletImages[0].getScaledWidth(bulletImages[0].getDensity());
        this.shipImage = shipImage;
        this.bulletUpSpeed = bulletUpSpeed;
        this.bulletDownSpeed = bulletDownSpeed;
        this.bulletRightSpeed = bulletRightSpeed;
        this.bulletLeftSpeed = bulletLeftSpeed;
        this.millisBeforeNextBullet = millisBeforeNextBullet;
    }

    Bitmap getShipImage()
    {
        return shipImage;
    }

    Bitmap getLoopBulletFrame()
    {
        currentFrameNumber += 1;
        if(currentFrameNumber >= totalNumberOfFrames)
        {
            currentFrameNumber = 0;
        }
        return bulletImages[currentFrameNumber];
    }

    Bitmap getNonLoopBulletFrame()
    {
        if(currentFrameNumber != totalNumberOfFrames)
        {
            currentFrameNumber += 1;
        }
        return bulletImages[currentFrameNumber];
    }

    int getBulletUpSpeed()
    {
        return bulletUpSpeed;
    }

    int getBulletDownSpeed()
    {
        return bulletDownSpeed;
    }

    int getBulletLeftSpeed()
    {
        return bulletLeftSpeed;
    }

    int getBulletRightSpeed()
    {
        return bulletRightSpeed;
    }

    int getMillisBeforeNextBullet()
    {
        return millisBeforeNextBullet;
    }

    int getShipHeight()
    {
        return shipHeight;
    }

    int getShipWidth()
    {
        return shipWidth;
    }

    int getBulletHeight()
    {
        return bulletHeight;
    }

    int getBulletWidth()
    {
        return bulletWidth;
    }
}