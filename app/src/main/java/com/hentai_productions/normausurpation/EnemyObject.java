package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


// Lot of work has to be done here. Not complete yet
class EnemyObject
{
    private Bitmap enemyShipImage;
    private Bitmap[] enemyBulletImages;
    private int millisBeforeNextEnemyShip;
    private int[] currentFrameNumber;
    private int totalNumberOfFrames;
    private int enemyShipHeight, enemyShipWidth, enemyBulletHeight, enemyBulletWidth;
    private myQueue<Bullet> enemyBulletQueue = new myQueue<Bullet>() ;
    private int enemyBulletQueueSize;

    EnemyObject(Context context, Bitmap enemyShipImage, String enemyBulletImageName, int totalNumberOfFrames, myQueue<Bullet> enemyBulletQueue, int millisBeforeNextEnemyShip)
    {
        this.totalNumberOfFrames = totalNumberOfFrames;
        enemyBulletImages = new Bitmap[totalNumberOfFrames];
        for(int i = 0; i < totalNumberOfFrames; i++)
        {
            enemyBulletImages[i] = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier(enemyBulletImageName + i,
                            "drawable", context.getPackageName()));
        }
        enemyShipHeight = enemyShipImage.getScaledHeight(enemyShipImage.getDensity());
        enemyShipWidth = enemyShipImage.getScaledWidth(enemyShipImage.getDensity());
        enemyBulletHeight = enemyBulletImages[0].getScaledHeight(enemyBulletImages[0].getDensity());
        enemyBulletWidth = enemyBulletImages[0].getScaledWidth(enemyBulletImages[0].getDensity());
        this.enemyShipImage = enemyShipImage;
        this.enemyBulletQueue = enemyBulletQueue;
        this.millisBeforeNextEnemyShip = millisBeforeNextEnemyShip;
        enemyBulletQueueSize = enemyBulletQueue.getSize();
        currentFrameNumber = new int[enemyBulletQueueSize];
        for (int i = 0; i < enemyBulletQueueSize; i++)
        {
            currentFrameNumber[i] = -1;
        }
    }

    Bitmap getEnemyShipImage()
    {
        return enemyShipImage;
    }

    int getEnemyBulletQueueSize()
    {
        return enemyBulletQueueSize;
    }

    Bitmap getLoopEnemyBulletFrame(int index)
    {
        currentFrameNumber[index] += 1;
        if(currentFrameNumber[index] >= totalNumberOfFrames)
        {
            currentFrameNumber[index] = 0;
        }
        return enemyBulletImages[currentFrameNumber[index]];
    }

    Bitmap getNonLoopEnemyBulletFrame(int index)
    {
        if(currentFrameNumber[index] != totalNumberOfFrames)
        {
            currentFrameNumber[index] += 1;
        }
        return enemyBulletImages[currentFrameNumber[index]];
    }

    int getEnemyBulletUpSpeed(int index)
    {
        return enemyBulletQueue.get(index).getUpSpeed();
    }

    int getEnemyBulletDownSpeed(int index)
    {
        return enemyBulletQueue.get(index).getDownSpeed();
    }

    int getEnemyBulletLeftSpeed(int index)
    {
        return enemyBulletQueue.get(index).getLeftSpeed();
    }

    int getEnemyBulletRightSpeed(int index)
    {
        return enemyBulletQueue.get(index).getRightSpeed();
    }

    int getMillisBeforeNextEnemyBullet(int index)
    {
        return enemyBulletQueue.get(index).getMillisBeforeNextBullet();
    }

    int getMillisBeforeNextEnemyShip()
    {
        return millisBeforeNextEnemyShip;
    }

    int getEnemyShipHeight()
    {
        return enemyShipHeight;
    }

    int getEnemyShipWidth()
    {
        return enemyShipWidth;
    }

    int getEnemyBulletHeight()
    {
        return enemyBulletHeight;
    }

    int getEnemyBulletWidth()
    {
        return enemyBulletWidth;
    }
}