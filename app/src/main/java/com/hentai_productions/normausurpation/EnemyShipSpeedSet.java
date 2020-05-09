package com.hentai_productions.normausurpation;

class EnemyShipSpeedSet {

    private float enemyShipUpSpeed, enemyShipDownSpeed, enemyShipLeftSpeed, enemyShipRightSpeed;

    EnemyShipSpeedSet(float enemyShipUpSpeed, float enemyShipDownSpeed, float enemyShipRightSpeed, float enemyShipLeftSpeed) {
        this.enemyShipUpSpeed = enemyShipUpSpeed;
        this.enemyShipDownSpeed = enemyShipDownSpeed;
        this.enemyShipRightSpeed = enemyShipRightSpeed;
        this.enemyShipLeftSpeed = enemyShipLeftSpeed;
    }

    float getEnemyShipDownSpeed() {
        return enemyShipDownSpeed;
    }

    float getEnemyShipLeftSpeed() {
        return enemyShipLeftSpeed;
    }

    float getEnemyShipRightSpeed() {
        return enemyShipRightSpeed;
    }

    float getEnemyShipUpSpeed() {
        return enemyShipUpSpeed;
    }
}