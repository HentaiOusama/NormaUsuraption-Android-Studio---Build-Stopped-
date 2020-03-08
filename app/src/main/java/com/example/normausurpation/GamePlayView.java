package com.example.normausurpation;

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
    public int level = 1;
    public int tempShipTop = 0;
    Bitmap currentBackgroundImage = null;
    float backgroundLeft = 0;
    float backgroundTop = 0;
    Bitmap currentShip;
    public int ship_left = 0;
    public int ship_top = 0;
    public int ship_width = 0;
    public int ship_height = 0;
    public int canvas_right = 0;
    public int canvas_bottom = 0;
    public int shipMaxTopAllowed = 0;
    public int shipMinTopAllowed = 0;
    public String currentShipName, currentBackgroundName;
    /////

    
    
    ///// Below are game related variables
    public boolean shouldIntroduceSpaceShip = true;
    public Thread spaceShipIntroducingThread = null;
    public boolean introducingSpaceShip = false;
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
            currentBackgroundName = GamePlay_Activity.getBackgroundName();
            currentBackgroundImage = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(currentBackgroundName, "drawable", context.getPackageName()));
            currentShip = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(currentShipName, "drawable", context.getPackageName()));
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
            ship_width = currentShip.getWidth();
            ship_height = currentShip.getHeight();
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
            ship_width = currentShip.getWidth();
            ship_height = currentShip.getHeight();
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
        long frameStartTime;
        long frameTime;
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
                    // clear the screen using black
                    canvas.drawBitmap(currentBackgroundImage, 0, 0, null);

                    try
                    {
                        // Your drawing here
                        canvas.drawBitmap(currentShip, ship_left, ship_top, null);

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




    public void buildShip()
    {
        int originalHeight = currentShip.getScaledHeight(currentShip.getDensity());
        int originalWidth = currentShip.getScaledWidth(currentShip.getDensity());
        int percentageOfScreen = 15;
        float heightRequired = (float) percentageOfScreen*canvas_bottom/100;
        float widthRequired = originalWidth/(originalHeight/heightRequired);

        Log.e(TAG, "newHeight = " + heightRequired + " newWidth = " + widthRequired);
        // So we'll resize the image now.
        currentShip = Bitmap.createScaledBitmap(currentShip, (int) widthRequired, (int) heightRequired, true);
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
                                    canvas.drawBitmap(currentShip, ship_left, ship_top, null);
                                    ship_top -= 7;
                                    if(ship_top + ship_height <= canvas_bottom)
                                    {
                                        ship_top = canvas_bottom-ship_height;
                                        canvas.drawBitmap(currentBackgroundImage, 0, 0, null);
                                        canvas.drawBitmap(currentShip, ship_left, ship_top, null);
                                        introducingSpaceShip = false;
                                        shouldIntroduceSpaceShip = false;
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
}