package com.hentai_productions.normausurpation;

public class PresetMovementPatterns {

    private int tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed;


    myQueue<EnemyShipSpeedSet> getMovementPatterQueueForEnemyShipType(int enemyShipMovementType) {

        /* 1 => Most basic ship. Movement Pattern is Oscillation at one place with small amplitude horizontally
         * 2 => Rotating in a circle
         * 3 => */

        tempUpSpeed = 0;
        tempDownSpeed = 0;
        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        myQueue<EnemyShipSpeedSet> listOfSpeedSetForParticularEnemy = new myQueue<>();


        /* From here onwards, just add a new method call for each case.
         * Define that method in such a way that it's parameter is an empty myQueue in which list of
         * EnemyShipSpeedSet has to be added. Return type = null. Rest is automatically handled. Preferred
         * name for the parameter is listOfSpeedSetForParticularEnemy and now coming back to our case in
         * question, it must return listOfSpeedSetForParticularEnemy after the method call.
         *
         * Consider the method buildSpeedSet1(myQueue<EnemyShipSpeedSet> listOfSpeedSetForParticularEnemy)
         * for reference. */


        switch (enemyShipMovementType) {
            case 1:

                buildSpeedSet1(listOfSpeedSetForParticularEnemy);
                return listOfSpeedSetForParticularEnemy;

            case 2:

                buildSpeedSet2(listOfSpeedSetForParticularEnemy);
                return listOfSpeedSetForParticularEnemy;

            default:
                return null;
        }
    }


    // These are SpeedSet building methods
    private void buildSpeedSet1(myQueue<EnemyShipSpeedSet> listOfSpeedSetForParticularEnemy) {
        tempRightSpeed = 2;
        for (int i = 0; i < 20; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
        }

        tempRightSpeed = 0;
        tempLeftSpeed = 2;
        for (int i = 0; i < 40; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
        }

        tempRightSpeed = 2;
        tempLeftSpeed = 0;
        for (int i = 0; i < 20; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
        }
    }

    private void buildSpeedSet2(myQueue<EnemyShipSpeedSet> listOfSpeedSetForParticularEnemy) { //  <---- Not complete yet

    }
}