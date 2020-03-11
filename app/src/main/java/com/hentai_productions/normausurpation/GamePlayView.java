package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.Queue;


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


public class GamePlayView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable
{

    private SurfaceHolder holder; //Holds the surface frame
    private Thread drawThread; // Draw Thread
    private boolean surfaceReady = false; // True when the surface is ready to draw
    private boolean drawingActive = false; // Drawing thread flag
    public Paint samplePaint = new Paint(); // Paint For drawing the sample shape
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0); // Time per frame for 60 FPS
    public Context context;
    public boolean firstTimeCreationOfSurface = true;
    public final String TAG = "MY DEBUG TAG";


    ///// Below are Drawing related variables
    public int tempShipTop = 0;
    Bitmap currentBackgroundImage = null, currentShipImage = null;
    float backgroundLeft = 0, backgroundTop = 0;
    public int ship_left = 0, ship_top = 0, ship_width = 0, ship_height = 0, shipMaxTopAllowed = 0, shipMinTopAllowed = 0;
    public int canvas_right = 0, canvas_bottom = 0;
    public ShipObject currentShip;
    public String currentShipName, currentBackgroundName, currentFriendlyBulletName;
    public myQueue<Bullet> bulletQueue = new myQueue<Bullet>() ;
    public Bullet bullet, drawBullet;
    public long frameStartTime, frameTime, previousBulletStartTime = 0, previousBulletTimeSpan;
    public boolean shouldBuildBullets = true;
    /////

    
    ///// Below are game related variables
    // Introducing Space Ship
    public boolean shouldIntroduceSpaceShip = true;
    public Thread spaceShipIntroducingThread = null;
    public boolean introducingSpaceShip = false;

    // Regarding Bullets
    public Thread bulletBuildingThread = null;
    public int numberOfBullets = 0, tempBulletTop, tempBulletLeft, numberOfBulletsToDraw;

    //Regarding LifeBar
    public int lifeLevel = 1;
    public int outerLifeBarTop, outerLifeBarLeft, outerLifeBarHeight, outerLifeBarWidth, innerLifeBarTop,
            innerLifeBarLeft, innerLifeBarHeight, innerLifeBarWidth, lifeLevelBoxTop, lifeLevelBoxLeft,
            lifeLevelBoxEdgeLength, innerLifeBarBottom;
    public Paint outerLifeBarPaint, innerLifeBarPaint, lifeBarBoxPaint, lifeBarBoxTextPaint;
    /////

    // constructor
    public GamePlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        setOnTouchListener(this);
        samplePaint.setColor(0xffff0000);
        samplePaint.setAntiAlias(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        this.holder = holder;
        
        /* Gets the name of background and Image from the activity_game_play and then builds the background
         * as per our needs but these things in the if only needs to be done once. Outside this might be needed to everytime the surface is created.
         * Say when the app is opened again from recent.*/
        if(firstTimeCreationOfSurface)
        {
            currentShipName = GamePlay_Activity.getCurrentShipName();
            currentFriendlyBulletName = GamePlay_Activity.getCurrentFriendlyBulletName();
            currentShip = new ShipObject(context, BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(currentShipName, "drawable", context.getPackageName())),
                    currentFriendlyBulletName, 10, 30, 0,
                    0, 0, 200);
            currentBackgroundName = GamePlay_Activity.getBackgroundName();
            Log.e(TAG, "surfaceCreated: " + currentBackgroundName);
            currentBackgroundImage = BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(currentBackgroundName, "drawable", context.getPackageName()));
            currentShipImage = currentShip.getShipImage();
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.getDefaultDisplay().getMetrics(metrics);
            if (metrics.heightPixels >= canvas_bottom) {
                canvas_right = metrics.widthPixels;
                canvas_bottom = metrics.heightPixels;
            }
            buildBackground();
            buildShip();
            buildLifeBar();
            ship_width = currentShipImage.getWidth();
            ship_height = currentShipImage.getHeight();
            ship_left = (canvas_right/2) - (ship_width/2);
            ship_top = canvas_bottom;
            shipMaxTopAllowed = canvas_bottom - ((int) ((0.28*canvas_bottom) + (ship_height / 2)));
            shipMinTopAllowed = canvas_bottom-ship_height;
            firstTimeCreationOfSurface = false;
        }

        surfaceReady = true;
        
        if(shouldIntroduceSpaceShip || introducingSpaceShip)
        {
            if(spaceShipIntroducingThread != null)
            {
                // drawThread was already running
                introducingSpaceShip = false;
                try {
                    spaceShipIntroducingThread.join();
                } catch (InterruptedException e) { // do nothing
                }
            }
            introduceSpaceShip();
        }
        else 
        {
            if (drawThread != null) {
                // drawThread was already running
                drawingActive = false;
                try {
                    drawThread.join();
                    bulletBuildingThread.join();
                } catch (InterruptedException e) { // do nothing
                }
            }
            ship_top = canvas_bottom - ship_height;
            startDrawThread();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if(canvas_bottom <= height)
        {
            canvas_bottom = height;
            canvas_right = width;
            currentBackgroundImage = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(currentBackgroundName, "drawable", context.getPackageName()));
            buildBackground();
            buildShip();
            buildLifeBar();
            ship_width = currentShipImage.getWidth();
            ship_height = currentShipImage.getHeight();
            shipMaxTopAllowed = canvas_bottom - ((int) ((0.28*canvas_bottom) + (ship_height / 2)));
            shipMinTopAllowed = canvas_bottom-ship_height;
        }
        // resize your UI
    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        // Handle touch events
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();
        ship_left = point.x - (ship_width/2);
        tempShipTop = point.y - (3*ship_height/4);
        if(tempShipTop <= shipMaxTopAllowed)
        {
            ship_top = shipMaxTopAllowed;
        }
        else
        {
            ship_top = Math.min(tempShipTop, shipMinTopAllowed);
        }
        return true;
    }


    @Override
    public void run()
    {
        try
        {
            while (drawingActive)
            {
                if (holder == null)
                {
                    return;
                }

                frameStartTime = System.nanoTime();
                Canvas canvas = holder.lockCanvas();
                if (canvas != null)
                {
                    try
                    {
                        // Your drawing here
                        // First is Background
                        canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                        numberOfBullets = bulletQueue.getSize();
                        for(int i = 0; i < numberOfBullets; i++)
                        {
                            bullet = bulletQueue.get(i);
                            tempBulletLeft = bullet.getLocationLeft() + bullet.getRightSpeed() - bullet.getLeftSpeed();
                            tempBulletTop = (bullet.getLocationTop()) - bullet.getUpSpeed() + bullet.getDownSpeed();
                            if(tempBulletTop <= 0 || tempBulletTop >= canvas_bottom || tempBulletLeft <= 0 || tempBulletLeft >= canvas_right)
                            {
                                bulletQueue.Dequeue(i);
                                numberOfBullets -= 1;
                                i -= 1;
                            }
                            else
                            {
                                bulletQueue.setLocationTop(i, tempBulletTop);
                                bulletQueue.setLocationLeft(i, tempBulletLeft);
                            }
                        }
                        numberOfBulletsToDraw = bulletQueue.getSize();
                        for(int i = 0; i < numberOfBulletsToDraw; i++)
                        {
                            drawBullet = bulletQueue.get(i);
                            canvas.drawBitmap(drawBullet.getBulletImage(), drawBullet.getLocationLeft(), drawBullet.getLocationTop(), null);
                        }

                        // Second Last is Life Bar
                        canvas.drawRoundRect(outerLifeBarLeft, outerLifeBarTop, (outerLifeBarLeft + outerLifeBarWidth),
                                (outerLifeBarTop + outerLifeBarHeight),15,15, outerLifeBarPaint);
                        canvas.drawRoundRect(innerLifeBarLeft, innerLifeBarTop, (innerLifeBarLeft + innerLifeBarWidth),
                                (innerLifeBarTop + innerLifeBarHeight),15,15, innerLifeBarPaint);
                        canvas.drawRoundRect(lifeLevelBoxLeft, lifeLevelBoxTop, (lifeLevelBoxLeft + lifeLevelBoxEdgeLength),
                                (lifeLevelBoxTop + lifeLevelBoxEdgeLength),15,15, lifeBarBoxPaint);
                        canvas.drawText(Integer.toString(lifeLevel), (float) (lifeLevelBoxLeft + (lifeLevelBoxEdgeLength*0.25)),
                                (lifeLevelBoxTop + lifeLevelBoxEdgeLength - 5), lifeBarBoxTextPaint);

                        // Last is Ship
                        canvas.drawBitmap(currentShipImage, ship_left, ship_top, null);

                    }
                    finally
                    {

                        holder.unlockCanvasAndPost(canvas);
                    }
                }

                // calculate the time required to draw the frame in ms
                frameTime = (System.nanoTime() - frameStartTime) / 1000000;

                if (frameTime < MAX_FRAME_TIME) // faster than the max fps - limit the FPS
                {
                    try
                    {
                        Thread.sleep(MAX_FRAME_TIME - frameTime);
                    } catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        stopDrawThread();
        holder.getSurface().release();
        this.holder = null;
        surfaceReady = false;
    }


    // Stops the drawing thread
    public void stopDrawThread()
    {
        if (drawThread == null)
        {
            spaceShipIntroducingThread = null;
            return;
        }
        drawingActive = false;
        while (true)
        {
            try
            {
                drawThread.join(5000);
                break;
            } catch (Exception e)
            {
                // Couldn't join the thread
            }
        }
        drawThread = null;
        bulletBuildingThread = null;
        spaceShipIntroducingThread = null;
    }


    // Creates a new draw thread and starts it.
    public void startDrawThread()
    {
        if (surfaceReady && drawThread == null)
        {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
            buildBullets(lifeLevel);
        }
    }


    // Builds background to fit screen
    public void buildBackground()
    {
        float newHeight, newWidth;
        int originalHeight = currentBackgroundImage.getScaledHeight(currentBackgroundImage.getDensity());
        int originalWidth = currentBackgroundImage.getScaledWidth(currentBackgroundImage.getDensity());
        float heightRatio = (float) originalHeight/canvas_bottom;
        float widthRatio = (float) originalWidth/canvas_right;

        Log.e(TAG, "originalHeight = " + originalHeight + " originalWidth = " + originalWidth + " CanvasHeight X CanvasWidth = " + canvas_bottom + "X" + canvas_right);

        // Below if else ladder best fits canvas into the image
        if(originalHeight >= canvas_bottom && originalWidth >= canvas_right)
        {
            if(heightRatio >= widthRatio)
            {
                newWidth = originalWidth;
                newHeight = canvas_bottom*widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight/2) - (newHeight / 2);
            }
            else
            {
                newHeight = originalHeight;
                newWidth = canvas_right*heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            }
        }
        else if (originalHeight >= canvas_bottom || originalWidth >= canvas_right)
        {
            if(originalHeight <= canvas_bottom)
            {
                newHeight = originalHeight;
                newWidth = canvas_right/heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            }
            else
            {
                newWidth = originalWidth;
                newHeight = canvas_bottom/widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight/2) - (newHeight / 2);
            }
        }
        else
        {
            if(heightRatio <= widthRatio)
            {
                newHeight = originalHeight;
                newWidth = canvas_right*heightRatio;
                backgroundTop = 0;
                backgroundLeft = (float) (originalWidth / 2) - (newWidth / 2);
            }
            else
            {
                newWidth = originalWidth;
                newHeight = canvas_bottom*widthRatio;
                backgroundLeft = 0;
                backgroundTop = (float) (originalHeight/2) - (newHeight / 2);
            }
        }

        Log.e(TAG, "newHeight = " + newHeight + " newWidth = " + newWidth + " top , left = " + backgroundTop + " , " + backgroundLeft);
        // Now we have new width and height and top, left co - ordinates to crop and resize image into canvas size
        currentBackgroundImage = Bitmap.createBitmap(currentBackgroundImage, (int) backgroundLeft, (int) backgroundTop, (int) newWidth, (int) newHeight);
        // Now aspect ratio of currentBackgroundImage is same as our canvas but actual dimensions may be larger or smaller
        // So we'll resize the image now. Above only cropped it.
        currentBackgroundImage = Bitmap.createScaledBitmap(currentBackgroundImage, canvas_right, canvas_bottom, true);
    }


    public void buildLifeBar()
    {
        outerLifeBarHeight = (int) (canvas_bottom * 0.25);
        outerLifeBarWidth = (int) (canvas_right * 0.075);
        outerLifeBarTop = (int) ((canvas_bottom*0.5) - (outerLifeBarHeight*0.5));
        outerLifeBarLeft = (int) ((canvas_right - (canvas_right*0.02)) - (outerLifeBarWidth));
        innerLifeBarWidth = (int) (0.75 * outerLifeBarWidth);
        innerLifeBarLeft = (int) (outerLifeBarLeft + (0.5 * outerLifeBarWidth) - (0.5 * innerLifeBarWidth));
        lifeLevelBoxEdgeLength = innerLifeBarWidth;
        int gap = (outerLifeBarWidth - innerLifeBarWidth)/2;
        innerLifeBarTop = outerLifeBarTop + gap;
        innerLifeBarHeight = outerLifeBarHeight - lifeLevelBoxEdgeLength - gap - gap - gap;
        lifeLevelBoxLeft = innerLifeBarLeft;
        lifeLevelBoxTop = innerLifeBarTop + innerLifeBarHeight + gap;
        innerLifeBarBottom = innerLifeBarTop + innerLifeBarHeight;
        outerLifeBarPaint = new Paint();
        innerLifeBarPaint = new Paint();
        lifeBarBoxPaint = new Paint();
        lifeBarBoxTextPaint = new Paint();
        outerLifeBarPaint.setARGB(188, 255, 229, 0);
        innerLifeBarPaint.setARGB(222, 137, 255, 0);
        lifeBarBoxPaint.setARGB(195, 0, 226, 255);
        lifeBarBoxTextPaint.setARGB(220, 0, 0, 0);
        outerLifeBarPaint.setAntiAlias(true);
        innerLifeBarPaint.setAntiAlias(true);
        lifeBarBoxPaint.setAntiAlias(true);
        lifeBarBoxTextPaint.setAntiAlias(true);
        lifeBarBoxTextPaint.setTextSize(lifeLevelBoxEdgeLength);
    }


    public void buildShip()
    {
        int originalHeight = currentShipImage.getScaledHeight(currentShipImage.getDensity());
        int originalWidth = currentShipImage.getScaledWidth(currentShipImage.getDensity());
        int percentageOfScreen = 15;
        float heightRequired = (float) percentageOfScreen*canvas_bottom/100;
        float widthRequired = originalWidth/(originalHeight/heightRequired);

        Log.e(TAG, "newHeight = " + heightRequired + " newWidth = " + widthRequired);
        // So we'll resize the image now.
        currentShipImage = Bitmap.createScaledBitmap(currentShipImage, (int) widthRequired, (int) heightRequired, true);
    }

    
    // Introduces Space Ship
    public void introduceSpaceShip()
    {
        if (surfaceReady && spaceShipIntroducingThread == null)
        {
            spaceShipIntroducingThread = new Thread("Space Ship Introducing Thread")
            {
                @Override
                public void run()
                {
                    super.run();

                    long frameStartTime;
                    long frameTime;
                    try
                    {
                        while (introducingSpaceShip)
                        {
                            if (holder == null)
                            {
                                return;
                            }

                            frameStartTime = System.nanoTime();
                            Canvas canvas = holder.lockCanvas();
                            if (canvas != null)
                            {
                                // clear the screen using black
                                canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                                try
                                {
                                    // Your drawing here
                                    canvas.drawBitmap(currentShipImage, ship_left, ship_top, null);
                                    ship_top -= 7;
                                    if(ship_top + ship_height <= canvas_bottom)
                                    {
                                        ship_top = canvas_bottom-ship_height;
                                        canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                                        canvas.drawBitmap(currentShipImage, ship_left, ship_top, null);
                                        introducingSpaceShip = false;
                                        shouldIntroduceSpaceShip = false;
                                        spaceShipIntroducingThread = null;
                                        startDrawThread();
                                    }
                                }
                                finally
                                {

                                    holder.unlockCanvasAndPost(canvas);
                                }
                            }

                            // calculate the time required to draw the frame in ms
                            frameTime = (System.nanoTime() - frameStartTime) / 1000000;

                            if (frameTime < MAX_FRAME_TIME) // faster than the max fps - limit the FPS
                            {
                                try
                                {
                                    Thread.sleep(MAX_FRAME_TIME - frameTime);
                                } catch (InterruptedException e)
                                {
                                    // ignore
                                }
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            introducingSpaceShip = true;
            spaceShipIntroducingThread.start();
        }
    }


    // Builds Bullets
    public void buildBullets(int lifeLevel)
    {
        if(shouldBuildBullets && bulletBuildingThread == null)
        {
            switch (lifeLevel)
            {
                case 1 :
                    bulletBuildingThread = new Thread("Bullets Building Thread")
                    {
                        @Override
                        public void run() {
                            super.run();

                            double shipWidthHalf = (currentShipImage.getScaledWidth(currentShipImage.getDensity()) * 0.5);
                            double shipHeightHalf = (currentShipImage.getScaledHeight(currentShipImage.getDensity()) * 0.5);
                            double bulletWidthHalf = (currentShip.getBulletWidth()) * 0.5;
                            double bulletHeightHalf = (currentShip.getBulletHeight()) * 0.5;

                            do
                            {
                                previousBulletStartTime = System.nanoTime() / 1000000 ;
                                bullet = new Bullet(currentShip.getLoopBulletFrame(), currentShip.getBulletUpSpeed(), currentShip.getBulletDownSpeed(),
                                        currentShip.getBulletRightSpeed(), currentShip.getBulletLeftSpeed(), currentShip.getMillisBeforeNextBullet());

                                bullet.setLocationLeft((int) (ship_left + shipWidthHalf - bulletWidthHalf));
                                bullet.setLocationTop((int) (ship_top + shipHeightHalf - bulletHeightHalf));
                                bulletQueue.Enqueue(bullet);
                                previousBulletTimeSpan = (System.nanoTime() / 1000000) - previousBulletStartTime;
                                try {
                                    Thread.sleep(bullet.getMillisBeforeNextBullet() - previousBulletTimeSpan);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "run: ", e);
                                }
                            }
                            while (shouldBuildBullets);
                        }
                    };
                    bulletBuildingThread.start();
                    break;

                case 2:
                    break;

                case 3:
                    break;
            }
        }
        else if (bulletBuildingThread != null && shouldBuildBullets)
        {
            bulletBuildingThread = null;
            buildBullets(lifeLevel);
        }
    }


    // Calls relevant function that builds enemies as per the current level
    public void buildLevel(int currentLevel)
    {
        switch (currentLevel)
        {
            case 1:
                buildLevel1();
                break;

            case 2 :
                buildLevel2();
                break;

            default :
                break;
        }
    }


    // Builds Level 1
    public void buildLevel1()
    {

    }


    // Builds Level 2
    public void buildLevel2()
    {

    }
}