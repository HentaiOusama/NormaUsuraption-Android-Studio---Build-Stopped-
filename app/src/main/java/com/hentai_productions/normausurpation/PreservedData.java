package com.hentai_productions.normausurpation;

class PreservedData {
    private boolean shouldIntroduceShip;
    private int lastLifeLevelProgress;
    private float lastCanvasRight, lastCanvasBottom;
    private FriendlyShipObject lastFriendlyShipObject;
    private EnemyShipObjectHashMap lastEnemyShipObjectHashMap;

    PreservedData(boolean shouldIntroduceShip, int lastLifeLevelProgress, FriendlyShipObject lastFriendlyShipObject,
                  float lastCanvasRight, float lastCanvasBottom, EnemyShipObjectHashMap lastEnemyShipObjectHashMap) {
        this.shouldIntroduceShip = shouldIntroduceShip;
        this.lastLifeLevelProgress = lastLifeLevelProgress;
        this.lastFriendlyShipObject = lastFriendlyShipObject;
        this.lastCanvasRight = lastCanvasRight;
        this.lastCanvasBottom = lastCanvasBottom;
        this.lastEnemyShipObjectHashMap = lastEnemyShipObjectHashMap;
    }

    boolean getShouldIntroduceShip() {
        return shouldIntroduceShip;
    }

    int getLastLifeLevelProgress() {
        return lastLifeLevelProgress;
    }

    FriendlyShipObject getLastShipObject() {
        return lastFriendlyShipObject;
    }

    float getLastCanvasRight() {
        return lastCanvasRight;
    }

    float getLastCanvasBottom() {
        return lastCanvasBottom;
    }

    EnemyShipObjectHashMap getLastEnemyObjectHashMap() {
        return lastEnemyShipObjectHashMap;
    }
}