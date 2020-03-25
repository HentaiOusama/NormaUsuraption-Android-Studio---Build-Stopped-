package com.hentai_productions.normausurpation;

public class EnemyShipSpeedSet {

    private int enemyShipUpSpeed, enemyShipDownSpeed, enemyShipLeftSpeed, enemyShipRightSpeed;

    EnemyShipSpeedSet(int enemyShipUpSpeed, int enemyShipDownSpeed, int enemyShipRightSpeed, int enemyShipLeftSpeed) {
        this.enemyShipUpSpeed = enemyShipUpSpeed;
        this.enemyShipDownSpeed = enemyShipDownSpeed;
        this.enemyShipRightSpeed = enemyShipRightSpeed;
        this.enemyShipLeftSpeed = enemyShipLeftSpeed;
    }

    public int getEnemyShipDownSpeed() {
        return enemyShipDownSpeed;
    }

    public int getEnemyShipLeftSpeed() {
        return enemyShipLeftSpeed;
    }

    public int getEnemyShipRightSpeed() {
        return enemyShipRightSpeed;
    }

    public int getEnemyShipUpSpeed() {
        return enemyShipUpSpeed;
    }
}