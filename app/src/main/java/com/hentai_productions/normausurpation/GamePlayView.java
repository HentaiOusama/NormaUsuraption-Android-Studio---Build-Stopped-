package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.jetbrains.annotations.NotNull;


/* It is important to understand which method is called when.

 * When the activity_game_play_ is first opened, this view is set.
 * Now, In these order, the methods are called :-

 * 1. GamePlayView (Context, Attributes)  <-- Constructor
 * 2. surfaceCreated ()  <-- Overridden
 *

 * When app is put into recent apps, then :-
 * 1. surfaceDestroyed()  <-- Overridden
 * is called and upon reopening the application,
 * 1. surfaceCreated ()  <-- Overridden
 * is again called */


public class GamePlayView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {
    public Context context;
    private SurfaceHolder holder; //Holds the surface frame
    private Thread drawThread; // Draw Thread
    private boolean surfaceReady = false; // True when the surface is ready to draw
    private boolean drawingActive = false; // Drawing thread flag
    public Paint samplePaint = new Paint(); // Paint For drawing the sample shape
    private static int FPS = 60; // Default FPS
    private static final int MAX_FRAME_TIME = (int) (1000.0 / FPS); // Time per frame
    public boolean firstTimeCreationOfSurface = true;
    public final String TAG = "MY DEBUG TAG";
    public WindowManager windowManager;
    public DisplayMetrics metrics;
    public float canvas_right = 0, canvas_bottom = 0;


    ///// Below are Background Related Variables
    public String currentBackgroundName;
    Bitmap currentBackgroundImage = null;
    float backgroundLeft = 0, backgroundTop = 0;
    /////


    ///// Below are Friendly Ship Related Variables
    public String currentFriendlyShipName;
    public FriendlyShipObject currentFriendlyShip;
    public float screenPercentageForMovementOfShip = (float) 0.35;
    public float shipMaxTopAllowed = 0, shipMinTopAllowed = 0;
    public float tempShipTop = 0, tempShipLeft = 0, differenceInShipPosition;
    // For Introducing Space Ship
    public boolean shouldIntroduceSpaceShip = true;
    public Thread spaceShipIntroducingThread = null;
    public boolean introducingSpaceShip = false;
    /////


    ///// Variables For Building LifeBar
    public int lifeLevel = 1, lifeLevelProgress = 8; // 1 Life Level is Distributed in 10 levels. Check buildLifeBar() function to get the line that distributes 1 level into 10
    public float outerLifeBarTop, outerLifeBarLeft, outerLifeBarHeight, outerLifeBarWidth, innerLifeBarTop,
            innerLifeBarLeft, innerLifeBarHeight, innerLifeBarWidth, lifeLevelBoxTop, lifeLevelBoxLeft,
            lifeLevelBoxEdgeLength, innerLifeBarBottom, innerLifeBarProgressBarLeft, innerLifeBarProgressBarWidth,
            innerLifeBarProgressBarSingleBlockHeight, innerLifeBarProgressBarBottom;
    public Paint outerLifeBarPaint, innerLifeBarPaint, lifeBarBoxPaint, lifeBarBoxTextPaint, innerLifeBarProgressBarMainPaint;
    /////


    ///// Below are Variables regarding Friendly Bullets
    public String currentFriendlyBulletName;
    public myQueue<Bullet> friendlyBulletQueue = new myQueue<>();
    public Bullet drawBullet;
    public long frameStartTime, frameTime;
    //////


    ///// Regarding Enemies
    public EnemyShipObjectHashMap enemyHashMap = null;
    public int enemyHashMapMaxHeightKey, enemyHashMapMaxWidthKey;
    public myQueue<Bullet> tempEnemyBulletQueue;
    public EnemyLevelBuilder enemyLevelBuilder;
    /////

    // constructor
    public GamePlayView(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        setOnTouchListener(this);
        samplePaint.setColor(0xffff0000);
        samplePaint.setAntiAlias(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        /* Gets the name of background and Image from the activity_game_play and then builds the background
         * as per our needs but these things in the if only needs to be done once. Outside this might be needed
         * to build every time the surface is created. Say when the app is opened again from recent.*/
        if (firstTimeCreationOfSurface) {
            // Gets Window Details
            metrics = new DisplayMetrics();
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.getDefaultDisplay().getMetrics(metrics);
            if (metrics.heightPixels >= canvas_bottom) {
                canvas_right = metrics.widthPixels;
                canvas_bottom = metrics.heightPixels;
            }

            // Gets and Builds Background
            currentBackgroundName = GamePlay_Activity.getBackgroundName();
            currentBackgroundImage = BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(currentBackgroundName, "drawable", context.getPackageName()));
            currentFriendlyBulletName = GamePlay_Activity.getCurrentFriendlyBulletName();

            // Gets and Builds Space Ship
            currentFriendlyShipName = GamePlay_Activity.getCurrentShipName();
            currentFriendlyShip = new FriendlyShipObject(context, BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(currentFriendlyShipName, "drawable", context.getPackageName())),
                    lifeLevel, 0, 0, currentFriendlyBulletName, 10, 1,
                    35, 0, 0, 0, 400,
                    FPS);

            // Below Sets Positions and Limits for friendly Ship
            buildBackground();
            buildShip(14);
            buildLifeBar();
            shipMaxTopAllowed = canvas_bottom - ((int) ((screenPercentageForMovementOfShip * canvas_bottom) + (currentFriendlyShip.getFriendlyShipHeight() / 2)));
            shipMinTopAllowed = canvas_bottom - currentFriendlyShip.getFriendlyShipHeight();
            currentFriendlyShip.setFriendlyShipLeft((canvas_right / 2) - (currentFriendlyShip.getFriendlyShipWidth() / 2));
            currentFriendlyShip.setFriendlyShipTop(canvas_bottom);
            firstTimeCreationOfSurface = false;
        }

        surfaceReady = true;
        if (shouldIntroduceSpaceShip || introducingSpaceShip) {
            if (spaceShipIntroducingThread != null) {
                // drawThread was already running
                introducingSpaceShip = false;
                try {
                    spaceShipIntroducingThread.join();
                } catch (InterruptedException e) { // do nothing
                }
            }
            introduceSpaceShip();
        } else {
            if (drawThread != null) {
                // drawThread was already running
                drawingActive = false;
                try {
                    drawThread.join();
                } catch (InterruptedException e) { // do nothing
                }
            }
            currentFriendlyShip.setFriendlyShipTop(canvas_bottom - currentFriendlyShip.getFriendlyShipHeight());
            startDrawThread();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (canvas_bottom <= height) {
            canvas_bottom = height;
            canvas_right = width;
            currentBackgroundImage = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(currentBackgroundName, "drawable", context.getPackageName()));
            buildBackground();
            buildShip(14);
            buildLifeBar();
            shipMaxTopAllowed = canvas_bottom - ((int) ((screenPercentageForMovementOfShip * canvas_bottom) + (currentFriendlyShip.getFriendlyShipHeight() / 2)));
            shipMinTopAllowed = canvas_bottom - currentFriendlyShip.getFriendlyShipHeight();
            if (enemyHashMap != null) {
                enemyHashMap.changeHashMapSize((int) canvas_bottom, (int) canvas_right);
            }
        }
    }


    @Override
    public boolean onTouch(View v, @NotNull MotionEvent event) {
        // Handle touch events
        tempShipLeft = event.getX() - (currentFriendlyShip.getFriendlyShipWidth() / 2);
        tempShipTop = event.getY() - (11 * currentFriendlyShip.getFriendlyShipHeight() / 14);
        updateFriendlyShipPosition();
        return true;
    }


    // To be Modified as per new changes
    // This handles what is drawn on screen
    @Override
    public void run() {
        if (spaceShipIntroducingThread != null) {
            introducingSpaceShip = false;
            shouldIntroduceSpaceShip = false;
            while (true) {
                try {
                    spaceShipIntroducingThread.join(500);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "run: Not able to Join", e);
                }
            }
            spaceShipIntroducingThread = null;
        }
        buildLevel(1);
        try {
            while (drawingActive) {
                if (holder == null) {
                    return;
                }
                frameStartTime = System.nanoTime();
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    try {
                        // First is Background
                        canvas.drawBitmap(currentBackgroundImage, 0, 0, null);


                        // Below part draws enemy bullets and ships
                        enemyHashMapMaxHeightKey = enemyHashMap.getMaxHeightKey();
                        enemyHashMapMaxWidthKey = enemyHashMap.getMaxWidthKey();
                        for (int i = -3; i <= enemyHashMapMaxHeightKey; i++) {
                            for (int j = -3; j <= enemyHashMapMaxHeightKey; j++) {
                                for (int k = -3; k <= enemyHashMapMaxWidthKey; k++) {
                                    for (int l = -3; l <= enemyHashMapMaxWidthKey; l++) {
                                        int lengthOfList = enemyHashMap.getEnemyShipObjectListSizeWithKeys(i, j, k, l);
                                        for (int m = 0; m < lengthOfList; m++) {
                                            tempEnemyBulletQueue = enemyHashMap.getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).getEnemyBulletQueue();
                                            int queueSize = tempEnemyBulletQueue.getSize();
                                            for (int n = 0; n < queueSize; n++) {
                                                canvas.drawBitmap(tempEnemyBulletQueue.get(n).getBulletFrame(),
                                                        tempEnemyBulletQueue.get(n).getLocationLeft(),
                                                        tempEnemyBulletQueue.get(n).getLocationTop(), null);
                                            }
                                            canvas.drawBitmap(enemyHashMap.getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).getEnemyShipImage(),
                                                    enemyHashMap.getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).getEnemyShipLeft(),
                                                    enemyHashMap.getEnemyShipObjectWithKeysAndIndex(i, j, k, l, m).getEnemyShipTop(), null);
                                        }
                                    }
                                }
                            }
                        }

                        // Below loop draws the friendly bullets on screen
                        friendlyBulletQueue = currentFriendlyShip.getFriendlyBulletQueue();
                        try {
                            for (int i = 0; i < currentFriendlyShip.getFriendlyBulletQueueSize(); i++) {
                                drawBullet = friendlyBulletQueue.get(i);
                                canvas.drawBitmap(currentFriendlyShip.getFriendlyBulletFrameOfBulletAtIndex(i),
                                        drawBullet.getLocationLeft(), drawBullet.getLocationTop(), null);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "run: Error in Bullet Queue", e);
                        }

                        // Second Last is Life Bar
                        canvas.drawRoundRect(outerLifeBarLeft, outerLifeBarTop, (outerLifeBarLeft + outerLifeBarWidth),
                                (outerLifeBarTop + outerLifeBarHeight), 12, 12, outerLifeBarPaint);
                        canvas.drawRoundRect(innerLifeBarLeft, innerLifeBarTop, (innerLifeBarLeft + innerLifeBarWidth),
                                (innerLifeBarTop + innerLifeBarHeight), 12, 12, innerLifeBarPaint);
                        canvas.drawRoundRect(lifeLevelBoxLeft, lifeLevelBoxTop, (lifeLevelBoxLeft + lifeLevelBoxEdgeLength),
                                (lifeLevelBoxTop + lifeLevelBoxEdgeLength), 12, 12, lifeBarBoxPaint);
                        canvas.drawText(Integer.toString(lifeLevel), (float) (lifeLevelBoxLeft + (lifeLevelBoxEdgeLength * 0.25)),
                                (lifeLevelBoxTop + lifeLevelBoxEdgeLength - 5), lifeBarBoxTextPaint);
                        canvas.drawRoundRect(innerLifeBarProgressBarLeft,
                                (innerLifeBarProgressBarBottom - (lifeLevelProgress * innerLifeBarProgressBarSingleBlockHeight)),
                                (innerLifeBarProgressBarWidth + innerLifeBarProgressBarLeft), innerLifeBarProgressBarBottom,
                                12, 12, innerLifeBarProgressBarMainPaint);

                        // Last is Ship
                        canvas.drawBitmap(currentFriendlyShip.getFriendlyShipImage(), currentFriendlyShip.getFriendlyShipLeft(),
                                currentFriendlyShip.getFriendlyShipTop(), null);

                    } finally {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                // calculate the time required to draw the frame in ms
                frameTime = (System.nanoTime() - frameStartTime) / 1000000;
                // If faster than the max fps -> limit the FPS
                if (frameTime < MAX_FRAME_TIME) {
                    try {
                        Thread.sleep(MAX_FRAME_TIME - frameTime);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: FPS :- ", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }
    }


    @Override
    public void surfaceDestroyed(@NotNull SurfaceHolder holder) {
        stopAllThreads();
        holder.getSurface().release();
        this.holder = null;
        surfaceReady = false;
    }


    // Stops all threads
    public void stopAllThreads() {
        if (drawThread != null) {
            drawingActive = false;
            while (true) {
                try {
                    drawThread.join(5000);
                    break;
                } catch (Exception e) {
                    // Couldn't join the thread
                }
            }
            drawThread = null;
        }
        if (spaceShipIntroducingThread != null) {
            introducingSpaceShip = false;
            shouldIntroduceSpaceShip = false;
            while (true) {
                try {
                    spaceShipIntroducingThread.join(5000);
                    break;
                } catch (Exception e) {
                    // Couldn't join the thread
                }
            }
            spaceShipIntroducingThread = null;
        }
        currentFriendlyShip.stopBuildingBullets();
        enemyHashMap.stopAllEnemyShipThreads();
        enemyLevelBuilder.stopBuildingLevel();
    }


    // Creates a new draw thread and starts it.
    public void startDrawThread() {
        if (surfaceReady && drawThread == null) {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            buildBullets();
            drawThread.start();
        }
    }


    // Builds background to fit screen
    public void buildBackground() {
        float newHeight, newWidth;
        int originalHeight = currentBackgroundImage.getScaledHeight(currentBackgroundImage.getDensity());
        int originalWidth = currentBackgroundImage.getScaledWidth(currentBackgroundImage.getDensity());
        float heightRatio = originalHeight / canvas_bottom;
        float widthRatio = originalWidth / canvas_right;

        // Below if else ladder best fits canvas into the image
        if (originalHeight >= canvas_bottom && originalWidth >= canvas_right) {
            if (heightRatio >= widthRatio) {
                newWidth = originalWidth;
                newHeight = canvas_bottom * widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight / 2) - (newHeight / 2);
            } else {
                newHeight = originalHeight;
                newWidth = canvas_right * heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            }
        } else if (originalHeight >= canvas_bottom || originalWidth >= canvas_right) {
            if (originalHeight <= canvas_bottom) {
                newHeight = originalHeight;
                newWidth = canvas_right / heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            } else {
                newWidth = originalWidth;
                newHeight = canvas_bottom / widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight / 2) - (newHeight / 2);
            }
        } else {
            if (heightRatio <= widthRatio) {
                newHeight = originalHeight;
                newWidth = canvas_right * heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            } else {
                newWidth = originalWidth;
                newHeight = canvas_bottom * widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight / 2) - (newHeight / 2);
            }
        }

        // Now we have new width and height and top, left co - ordinates to crop and resize image into canvas size
        currentBackgroundImage = Bitmap.createBitmap(currentBackgroundImage, (int) backgroundLeft, (int) backgroundTop,
                (int) newWidth, (int) newHeight);
        // Now aspect ratio of currentBackgroundImage is same as our canvas but actual dimensions may be larger or smaller
        // So we'll resize the image now. Above only cropped it.
        currentBackgroundImage = Bitmap.createScaledBitmap(currentBackgroundImage, (int) canvas_right, (int) canvas_bottom, true);
    }


    public void buildLifeBar() {
        outerLifeBarHeight = (float) (canvas_bottom * 0.20);
        outerLifeBarWidth = (float) (canvas_right * 0.04);
        outerLifeBarTop = (float) ((canvas_bottom * 0.5) - (outerLifeBarHeight * 0.5));
        outerLifeBarLeft = (float) ((canvas_right - (canvas_right * 0.02)) - (outerLifeBarWidth));
        innerLifeBarWidth = (float) (0.80 * outerLifeBarWidth);
        innerLifeBarLeft = (float) (outerLifeBarLeft + (0.5 * outerLifeBarWidth) - (0.5 * innerLifeBarWidth));
        lifeLevelBoxEdgeLength = innerLifeBarWidth;
        float gap = (outerLifeBarWidth - innerLifeBarWidth) / 2;
        innerLifeBarTop = outerLifeBarTop + gap;
        innerLifeBarHeight = outerLifeBarHeight - lifeLevelBoxEdgeLength - gap - gap - gap;
        lifeLevelBoxLeft = innerLifeBarLeft;
        lifeLevelBoxTop = innerLifeBarTop + innerLifeBarHeight + gap;
        innerLifeBarBottom = innerLifeBarTop + innerLifeBarHeight;
        innerLifeBarProgressBarWidth = (float) (0.82 * innerLifeBarWidth);
        float innerGap = (float) ((innerLifeBarWidth - innerLifeBarProgressBarWidth) * 0.5);
        innerLifeBarProgressBarLeft = innerLifeBarLeft + innerGap;
        innerLifeBarProgressBarBottom = innerLifeBarBottom - innerGap;
        innerLifeBarProgressBarSingleBlockHeight = ((innerLifeBarHeight - innerGap - innerGap) / 10); // This 10 divides 1 Life Level into 10
        outerLifeBarPaint = new Paint();
        innerLifeBarPaint = new Paint();
        lifeBarBoxPaint = new Paint();
        lifeBarBoxTextPaint = new Paint();
        innerLifeBarProgressBarMainPaint = new Paint();
        outerLifeBarPaint.setARGB(80, 255, 229, 0);
        innerLifeBarPaint.setARGB(100, 137, 255, 0);
        lifeBarBoxPaint.setARGB(90, 0, 226, 255);
        lifeBarBoxTextPaint.setARGB(180, 0, 0, 0);
        innerLifeBarProgressBarMainPaint.setARGB(125, 255, 183, 177);
        outerLifeBarPaint.setAntiAlias(true);
        innerLifeBarPaint.setAntiAlias(true);
        lifeBarBoxPaint.setAntiAlias(true);
        lifeBarBoxTextPaint.setAntiAlias(true);
        innerLifeBarProgressBarMainPaint.setAntiAlias(true);
        lifeBarBoxTextPaint.setTextSize(lifeLevelBoxEdgeLength);
    }


    public void buildShip(int scaleValue) {
        float originalHeight = currentFriendlyShip.getFriendlyShipImage().getScaledHeight(currentFriendlyShip.getFriendlyShipImage().getDensity());
        float originalWidth = currentFriendlyShip.getFriendlyShipImage().getScaledWidth(currentFriendlyShip.getFriendlyShipImage().getDensity());
        float heightRequired = (scaleValue * canvas_bottom / 100);
        float widthRequired = originalWidth / (originalHeight / heightRequired);
        // So we'll resize the image now.
        currentFriendlyShip.setFriendlyShipImage(Bitmap.createScaledBitmap(currentFriendlyShip.getFriendlyShipImage(),
                (int) widthRequired, (int) heightRequired, true));
    }


    // Introduces Space Ship
    public void introduceSpaceShip() {
        if (surfaceReady && spaceShipIntroducingThread == null) {
            spaceShipIntroducingThread = new Thread("Space Ship Introducing Thread") {
                @Override
                public void run() {
                    super.run();
                    long frameStartTime;
                    long frameTime;
                    try {
                        Bitmap currentFriendlyShipImage = currentFriendlyShip.getFriendlyShipImage();
                        float ship_left = currentFriendlyShip.getFriendlyShipLeft();
                        float ship_top = currentFriendlyShip.getFriendlyShipTop();
                        while (introducingSpaceShip) {
                            if (holder == null) {
                                return;
                            }

                            frameStartTime = System.nanoTime();
                            Canvas canvas = holder.lockCanvas();
                            if (canvas != null) {
                                canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                                try {
                                    canvas.drawBitmap(currentFriendlyShipImage, ship_left, ship_top, null);
                                    ship_top -= 4;
                                    if (ship_top + currentFriendlyShip.getFriendlyShipHeight() <= canvas_bottom) {
                                        ship_top = canvas_bottom - currentFriendlyShip.getFriendlyShipHeight();
                                        canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                                        canvas.drawBitmap(currentFriendlyShipImage, ship_left, ship_top, null);
                                        introducingSpaceShip = false;
                                        shouldIntroduceSpaceShip = false;
                                        currentFriendlyShip.setFriendlyShipTop(ship_top);
                                        currentFriendlyShip.setFriendlyShipLeft(ship_left);
                                        startDrawThread();
                                    }
                                } finally {
                                    holder.unlockCanvasAndPost(canvas);
                                }
                            }

                            // calculate the time required to draw the frame in ms
                            frameTime = (System.nanoTime() - frameStartTime) / 1000000;

                            if (frameTime < MAX_FRAME_TIME) // faster than the max fps - limit the FPS
                            {
                                try {
                                    Thread.sleep(MAX_FRAME_TIME - frameTime);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "introduceSpaceShip -> run: ", e);
                    }
                }
            };
            introducingSpaceShip = true;
            spaceShipIntroducingThread.start();
        }
    }


    // Updates the position of the friendly Space Ship
    public void updateFriendlyShipPosition() {
        for (int i = 0; i < 5; i++) {
            differenceInShipPosition = (currentFriendlyShip.getFriendlyShipLeft() - tempShipLeft);
            if (Math.abs(differenceInShipPosition) >= 150) {
                if (differenceInShipPosition < 0) {
                    tempShipLeft = currentFriendlyShip.getFriendlyShipLeft() + 8;
                } else {
                    tempShipLeft = currentFriendlyShip.getFriendlyShipLeft() - 8;
                }
            }
            currentFriendlyShip.setFriendlyShipLeft(tempShipLeft);
            differenceInShipPosition = (currentFriendlyShip.getFriendlyShipTop() - tempShipTop);
            if (Math.abs(differenceInShipPosition) >= 150) {
                if (differenceInShipPosition < 0) {
                    tempShipTop = currentFriendlyShip.getFriendlyShipTop() + 2;
                } else {
                    tempShipTop = currentFriendlyShip.getFriendlyShipTop() - 4;
                }
            }
            if (tempShipTop <= shipMaxTopAllowed) {
                currentFriendlyShip.setFriendlyShipTop(shipMaxTopAllowed);
            } else {
                currentFriendlyShip.setFriendlyShipTop(Math.min(tempShipTop, shipMinTopAllowed));
            }
        }
    }


    // Builds Bullets
    public void buildBullets() {
        currentFriendlyShip.startBuildingBullets(canvas_bottom, canvas_right);
    }


    // Calls relevant function that builds enemies as per the current level
    public void buildLevel(int currentStageLevel) {
        enemyHashMap = new EnemyShipObjectHashMap(canvas_bottom, canvas_right, 385, 175,
                canvas_right, canvas_bottom);
        currentFriendlyShip.takeEnemyHashMap(enemyHashMap);
        enemyLevelBuilder = new EnemyLevelBuilder(context, canvas_right, canvas_bottom, enemyHashMap, MAX_FRAME_TIME);
        enemyLevelBuilder.startBuildingLevel(currentStageLevel);
    }


    public PreservedData getDataToBePreserved() {
        PreservedData preservedData = new PreservedData(shouldIntroduceSpaceShip, lifeLevelProgress, currentFriendlyShip,
                canvas_right, canvas_bottom, enemyHashMap);
        stopAllThreads();
        return preservedData;
    }


    void setOldData(@NotNull Context context, @NotNull PreservedData lastPreservedData) {
        this.context = context;
        shouldIntroduceSpaceShip = lastPreservedData.getShouldIntroduceShip();
        lifeLevelProgress = lastPreservedData.getLastLifeLevelProgress();
        currentFriendlyShip = lastPreservedData.getLastShipObject();
        enemyHashMap = lastPreservedData.getLastEnemyObjectHashMap();
        this.canvas_right = lastPreservedData.getLastCanvasRight();
        this.canvas_bottom = lastPreservedData.getLastCanvasBottom();
        buildShip(14);
    }
}