package com.hentai_productions.normausurpation;

class PreservedData
{
    private boolean shouldIntroduceShip;
    private int lastShipLeft, lastShipTop, lastLifeLevel, lastLifeLevelProgress, lastCanvasRight, lastCanvasBottom;
    private ShipObject lastShipObject;
    private myQueue<Bullet> lastBulletQueue;
    private EnemyObjectHashMap lastEnemyObjectHashMap;

    PreservedData (boolean shouldIntroduceShip, int lastShipLeft, int lastShipTop, int lastLifeLevel, int lastLifeLevelProgress,
                   int lastCanvasRight, int lastCanvasBottom, ShipObject lastShipObject, myQueue<Bullet> lastBulletQueue,
                   EnemyObjectHashMap lastEnemyObjectHashMap)
    {
        this.shouldIntroduceShip = shouldIntroduceShip;
        this.lastShipLeft = lastShipLeft;
        this.lastShipTop = lastShipTop;
        this.lastLifeLevel = lastLifeLevel;
        this.lastLifeLevelProgress = lastLifeLevelProgress;
        this.lastCanvasRight = lastCanvasRight;
        this.lastCanvasBottom = lastCanvasBottom;
        this.lastShipObject = lastShipObject;
        this.lastBulletQueue = lastBulletQueue;
        this.lastEnemyObjectHashMap = lastEnemyObjectHashMap;
    }

    boolean getShouldIntroduceShip()
    {
        return shouldIntroduceShip;
    }

    int getLastShipLeft()
    {
        return lastShipLeft;
    }

    int getLastShipTop()
    {
        return lastShipTop;
    }

    int getLastLifeLevel()
    {
        return lastLifeLevel;
    }

    int getLastLifeLevelProgress()
    {
        return lastLifeLevelProgress;
    }

    int getLastCanvasRight()
    {
        return lastCanvasRight;
    }

    int getLastCanvasBottom()
    {
        return lastCanvasBottom;
    }

    ShipObject getLastShipObject()
    {
        return lastShipObject;
    }

    myQueue<Bullet> getLastBulletQueue()
    {
        return lastBulletQueue;
    }

    EnemyObjectHashMap getLastEnemyObjectHashMap()
    {
        return lastEnemyObjectHashMap;
    }
}