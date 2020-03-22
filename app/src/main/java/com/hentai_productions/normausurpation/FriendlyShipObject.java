package com.hentai_productions.normausurpation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

class FriendlyShipObject
{
    private Context context;

    // Friendly ship related variables
    private Bitmap friendlyShipImage;
    private int friendlyShipTop, friendlyShipLeft, friendlyShipBottom, friendlyShipRight;
    private int friendlyShipHeight, friendlyShipWidth, friendlyBulletHeight, friendlyBulletWidth;

    // Friendly bullet related variables
    private String friendlyBulletImageName;
    private int totalNumberOfFrames;
    private int friendlyBulletFrameType; // 1 = looping and 2 = non Looping
    private int friendlyBulletUpSpeed, friendlyBulletDownSpeed, friendlyBulletRightSpeed, friendlyBulletLeftSpeed;
    private int millisBeforeNextFriendlyBullet;
    private int millisBeforeNextFriendlyShip;
    private myQueue<Bullet> friendlyBulletQueue = new myQueue<>() ;
    private Thread bulletThread;
    private boolean shouldBuildFriendlyBullets = false;
    private Bullet tempBullet;
    private long previousBulletStartTime, previousBulletTimeSpan;


    private String TAG = "MY DEBUG TAG";


    // Constructor
    FriendlyShipObject(Context context, @NotNull Bitmap friendlyShipImage, int friendlyShipTop, int friendlyShipLeft, String friendlyBulletImageName,
                int totalNumberOfFrames, int friendlyBulletFrameType, int friendlyBulletUpSpeed, int friendlyBulletDownSpeed,
                int friendlyBulletRightSpeed, int friendlyBulletLeftSpeed, int millisBeforeNextFriendlyBullet, int millisBeforeNextFriendlyShip)
    {
        this.context = context;
        this.friendlyShipImage = friendlyShipImage;
        this.friendlyShipTop = friendlyShipTop;
        this.friendlyShipLeft = friendlyShipLeft;
        this.friendlyBulletImageName = friendlyBulletImageName;
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.friendlyBulletFrameType = friendlyBulletFrameType;
        this.friendlyBulletUpSpeed = friendlyBulletUpSpeed;
        this.friendlyBulletDownSpeed = friendlyBulletDownSpeed;
        this.friendlyBulletRightSpeed = friendlyBulletRightSpeed;
        this.friendlyBulletLeftSpeed = friendlyBulletLeftSpeed;
        this.millisBeforeNextFriendlyBullet = millisBeforeNextFriendlyBullet;
        this.millisBeforeNextFriendlyShip = millisBeforeNextFriendlyShip;
        friendlyShipHeight = friendlyShipImage.getScaledHeight(friendlyShipImage.getDensity());
        friendlyShipWidth = friendlyShipImage.getScaledWidth(friendlyShipImage.getDensity());
        friendlyShipBottom = this.friendlyShipTop + friendlyShipHeight;
        friendlyShipRight = this.friendlyShipLeft + friendlyShipWidth;
        makeBulletTread();
    }



    // Ship related methods
    Bitmap getFriendlyShipImage()
    {
        return friendlyShipImage;
    }

    int getFriendlyShipTop()
    {
        return friendlyShipTop;
    }

    int getFriendlyShipLeft()
    {
        return friendlyShipLeft;
    }

    int getFriendlyShipBottom()
    {
        return friendlyShipTop;
    }

    int getFriendlyShipRight()
    {
        return friendlyShipLeft;
    }

    int getFriendlyShipHeight()
    {
        return friendlyShipHeight;
    }

    int getFriendlyShipWidth()
    {
        return friendlyShipWidth;
    }

    int getFriendlyBulletHeight()
    {
        return friendlyBulletHeight;
    }

    int getFriendlyBulletWidth()
    {
        return friendlyBulletWidth;
    }

    void setFriendlyShipTop(int friendlyShipTop) {
        this.friendlyShipTop = friendlyShipTop;
        friendlyShipBottom = this.friendlyShipTop + friendlyShipHeight;
    }

    void setFriendlyShipLeft(int friendlyShipLeft) {
        this.friendlyShipLeft = friendlyShipLeft;
        friendlyShipRight = this.friendlyShipLeft + friendlyShipWidth;
    }

    int getMillisBeforeNextFriendlyShip()
    {
        return millisBeforeNextFriendlyShip;
    }



    // Bullet related methods
    int getFriendlyBulletQueueSize()
    {
        return friendlyBulletQueue.getSize();
    }

    Bitmap getFriendlyBulletFrameOfBulletAtIndex(int index) {
        return friendlyBulletQueue.get(index).getBulletFrame();
    }

    int getFriendlyBulletUpSpeed(int index)
    {
        return friendlyBulletQueue.get(index).getUpSpeed();
    }

    int getFriendlyBulletDownSpeed(int index)
    {
        return friendlyBulletQueue.get(index).getDownSpeed();
    }

    int getFriendlyBulletLeftSpeed(int index)
    {
        return friendlyBulletQueue.get(index).getLeftSpeed();
    }

    int getFriendlyBulletRightSpeed(int index)
    {
        return friendlyBulletQueue.get(index).getRightSpeed();
    }

    private void makeBulletTread() {
        bulletThread = new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                double shipWidthHalf = (friendlyShipImage.getScaledWidth(friendlyShipImage.getDensity()) * 0.5);
                double shipHeightHalf = (friendlyShipImage.getScaledHeight(friendlyShipImage.getDensity()) * 0.5);
                double bulletWidthHalf = (friendlyBulletWidth) * 0.5;
                double bulletHeightHalf = (friendlyBulletHeight) * 0.5;

                do
                {
                    previousBulletStartTime = System.nanoTime() / 1000000 ;

                    tempBullet = new Bullet(context, friendlyBulletImageName, totalNumberOfFrames, friendlyBulletFrameType, friendlyBulletUpSpeed,
                            friendlyBulletDownSpeed, friendlyBulletRightSpeed, friendlyBulletLeftSpeed, millisBeforeNextFriendlyBullet);

                    tempBullet.setLocationLeft((int) (friendlyShipLeft + shipWidthHalf - bulletWidthHalf));
                    tempBullet.setLocationTop((int) (friendlyShipTop + shipHeightHalf - bulletHeightHalf));
                    friendlyBulletQueue.Enqueue(tempBullet);
                    previousBulletTimeSpan = (System.nanoTime() / 1000000) - previousBulletStartTime;
                    try {
                        Thread.sleep(millisBeforeNextFriendlyBullet - previousBulletTimeSpan);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
                while (shouldBuildFriendlyBullets);
            }
        };
    }

    void startBuildingBullets() {
        shouldBuildFriendlyBullets = true;
        bulletThread.start();
    }

    void stopBuildingBullets() {
        shouldBuildFriendlyBullets = false;
        try {
            bulletThread.join();
        } catch (Exception e) {
            Log.e(TAG, "stopBuildingBullets: ", e);
        }
    }

    myQueue<Bullet> getFriendlyBulletQueue()
    {
        return friendlyBulletQueue;
    }
}