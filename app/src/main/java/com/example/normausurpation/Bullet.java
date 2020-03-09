package com.example.normausurpation;

public class Bullet
{
    private String name;
    private int upSpeed, downSpeed, rightSpeed, leftSpeed;
    private int locationTop, locationLeft;

    Bullet(String name, int upSpeed, int downSpeed, int rightSpeed, int leftSpeed)
    {
        this.name = name;
        this.upSpeed = upSpeed;
        this.downSpeed = downSpeed;
        this.rightSpeed = rightSpeed;
        this.leftSpeed = leftSpeed;
    }

    public String getBulletName()
    {
        return name;
    }

    public int getUpSpeed()
    {
        return upSpeed;
    }

    public int getDownSpeed()
    {
        return downSpeed;
    }

    public int getLeftSpeed()
    {
        return leftSpeed;
    }

    public int getRightSpeed()
    {
        return rightSpeed;
    }

    public int getLocationTop()
    {
        return locationTop;
    }

    public int getLocationLeft()
    {
        return locationLeft;
    }

    public void setLocationTop(int top)
    {
        locationTop = top;
    }

    public void setLocationLeft(int left)
    {
        locationLeft = left;
    }
}
