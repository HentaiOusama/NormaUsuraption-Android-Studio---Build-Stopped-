package com.hentai_productions.normausurpation;

class PresetMovementPatterns {

    private float tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed;
    private myQueue<EnemyShipSpeedSet> listOfSpeedSetForParticularEnemy;
    private float fromTop, fromLeft, toTop, toLeft;
    private final float loopsPerPixel = ((float) 1 / (float) 15);
    private int numberOfHorizontalLoops, numberOfVerticalLoops;


    // Below are movement pattern after ship has been introduced
    // ----------------------------------------------------------------------------------------------------- //

    myQueue<EnemyShipSpeedSet> getMovementPatterQueueForEnemyShipType(int enemyShipMovementType) {

        /* If enemyShipMovementType =
         *
         * 1 => Most basic ship. Movement Pattern is Oscillation at one place with small amplitude horizontally
         * 2 => Rotating in a circle
         * 3 =>
         * */

        tempUpSpeed = 0;
        tempDownSpeed = 0;
        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        listOfSpeedSetForParticularEnemy = new myQueue<>();


        /* From here onwards, just add a new method call for each case.
         * Define that new method in such a way that it has no parameters and considers listOfSpeedSetForParticularEnemy
         * as an empty myQueue in which list of EnemyShipSpeedSet has to be added. Return type = null.
         * Rest is automatically handled.
         *
         * Consider the method buildSpeedSet1() for reference and also it's corresponding case --> case 1: */


        switch (enemyShipMovementType) {
            case 1:
                buildSpeedSet1();
                break;

            case 2:
                buildSpeedSet2();
                break;

            default:
                listOfSpeedSetForParticularEnemy = null;
        }


        return listOfSpeedSetForParticularEnemy;
    }


    // These are SpeedSet building methods
    private void buildSpeedSet1() {
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

    private void buildSpeedSet2() { //  <---- Not complete yet

    }


    // Below are movement pattern for introducing the ship
    // ------------------------------------------------------------------------------------------------------- //

    myQueue<EnemyShipSpeedSet> getStraightIntroducingPatternForEnemyShip(int type, float fromTop, float fromLeft,
                                                                         float toTop, float toLeft) {


        /* type =
         *
         * 1 = First Horizontal then Vertical
         * 2 = First Vertical then Horizontal */

        this.fromTop = fromTop;
        this.fromLeft = fromLeft;
        this.toTop = toTop;
        this.toLeft = toLeft;
        tempUpSpeed = 0;
        tempDownSpeed = 0;
        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        listOfSpeedSetForParticularEnemy = new myQueue<>();

        switch (type) {
            case 1:
                makeFHTVMovementSet();
                break;

            case 2:
                makeFVTHMovementSet();
                break;
        }

        return listOfSpeedSetForParticularEnemy;
    }


    private void makeFHTVMovementSet() {
        numberOfHorizontalLoops = (int) ((toLeft - fromLeft) * loopsPerPixel);
        numberOfVerticalLoops = (int) ((toTop - fromTop) * loopsPerPixel);

        tempUpSpeed = 0;
        tempDownSpeed = 0;
        if (numberOfHorizontalLoops < 0) {
            tempLeftSpeed = ((float) 1 / loopsPerPixel);
            numberOfHorizontalLoops = Math.abs(numberOfHorizontalLoops);
        } else {
            tempRightSpeed = ((float) 1 / loopsPerPixel);
        }

        for (int i = 0; i < numberOfHorizontalLoops; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(0, 0, 0, 0));
        }

        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        if (numberOfVerticalLoops < 0) {
            tempUpSpeed = ((float) 1 / loopsPerPixel);
            numberOfVerticalLoops = Math.abs(numberOfVerticalLoops);
        } else {
            tempDownSpeed = ((float) 1 / loopsPerPixel);
        }

        for (int i = 0; i < numberOfVerticalLoops; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(0, 0, 0, 0));

        }
    }

    private void makeFVTHMovementSet() {
        numberOfHorizontalLoops = (int) ((toLeft - fromLeft) * loopsPerPixel);
        numberOfVerticalLoops = (int) ((toTop - fromTop) * loopsPerPixel);

        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        if (numberOfVerticalLoops < 0) {
            tempUpSpeed = ((float) 1 / loopsPerPixel);
            numberOfVerticalLoops = Math.abs(numberOfVerticalLoops);
        } else {
            tempDownSpeed = ((float) 1 / loopsPerPixel);
        }

        for (int i = 0; i < numberOfVerticalLoops; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(0, 0, 0, 0));

        }

        tempUpSpeed = 0;
        tempDownSpeed = 0;
        if (numberOfHorizontalLoops < 0) {
            tempLeftSpeed = ((float) 1 / loopsPerPixel);
            numberOfHorizontalLoops = Math.abs(numberOfHorizontalLoops);
        } else {
            tempRightSpeed = ((float) 1 / loopsPerPixel);
        }

        for (int i = 0; i < numberOfHorizontalLoops; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(0, 0, 0, 0));

        }
    }


    myQueue<EnemyShipSpeedSet> getDiagonalIntroducingPatternForEnemyShip(float fromTop, float fromLeft,
                                                                         float toTop, float toLeft) {

        this.fromTop = fromTop;
        this.fromLeft = fromLeft;
        this.toTop = toTop;
        this.toLeft = toLeft;
        tempUpSpeed = 0;
        tempDownSpeed = 0;
        tempLeftSpeed = 0;
        tempRightSpeed = 0;
        listOfSpeedSetForParticularEnemy = new myQueue<>();

        float tempDiagonalSpeed;
        float diagonalLength = (float) Math.sqrt((Math.pow(fromTop - toTop, 2) + Math.pow(fromLeft - toLeft, 2)));
        float horizontalLength = toLeft - fromLeft;
        float verticalLength = toTop - fromTop;
        int totalNumberOfLoops = (int) (diagonalLength * loopsPerPixel);
        tempDiagonalSpeed = diagonalLength / loopsPerPixel;

        if (horizontalLength < 0) {
            tempLeftSpeed = Math.abs(horizontalLength * tempDiagonalSpeed / diagonalLength);
        } else {
            tempRightSpeed = Math.abs(horizontalLength * tempDiagonalSpeed / diagonalLength);
        }

        if (verticalLength < 0) {
            tempUpSpeed = Math.abs(verticalLength * tempDiagonalSpeed / diagonalLength);
        } else {
            tempDownSpeed = Math.abs(verticalLength * tempDiagonalSpeed / diagonalLength);
        }

        for (int i = 0; i < totalNumberOfLoops; i++) {
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(tempUpSpeed, tempDownSpeed, tempRightSpeed, tempLeftSpeed));
            listOfSpeedSetForParticularEnemy.Enqueue(new EnemyShipSpeedSet(0, 0, 0, 0));
        }

        return listOfSpeedSetForParticularEnemy;
    }


    myQueue<EnemyShipSpeedSet> getConvexCurlIntroducingPatternForEnemyShip(float starDistanceToTravel, float movingAngle,
                                                                           float endDistanceToTravel, float fromTop,
                                                                           float fromLeft, float toTop, float toLeft) {

        /* Important description :-
         *
         * How does this method work ?
         * Consider a rectangle with (fromTop, fromLeft) and (toTop, toLeft) as opposite ends of one of it's diagonals
         *
         * 1st our ship moves startDistanceToTravel units at an angle movingAngle with horizontal or vertical depending upon
         * which one make the path convex. => 0 <= movingAngle <= 45. */
        return listOfSpeedSetForParticularEnemy;
    }

}