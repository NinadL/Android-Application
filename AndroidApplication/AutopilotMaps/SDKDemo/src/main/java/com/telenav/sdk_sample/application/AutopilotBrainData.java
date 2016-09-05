package com.telenav.sdk_sample.application;

/**
 * Created by ninad on 7/27/16.
 */
public class AutopilotBrainData
{
    static double remainingDistance;
    static int speedLimit;
    static boolean isHighwayNext = false;
    static boolean areWeOnHighway = false;

    public void setRemainingDistance(double remainingDistance)
    {
        this.remainingDistance = remainingDistance;
    }

    public void setSpeedLimit(int speedLimit)
    {
        this.speedLimit = speedLimit;
    }

    public double getRemainingDistance()
    {
        return remainingDistance;
    }

    public int getSpeedLimit()
    {
        return speedLimit;
    }

    public void setIsHighwayNext(boolean isHighwayNext)
    {
        this.isHighwayNext = isHighwayNext;
    }

    public void setAreWeOnHighway(boolean areWeOnHighway)
    {
        this.areWeOnHighway = areWeOnHighway;
    }

    public boolean getIsHighwayNext()
    {
        return isHighwayNext;
    }

    public boolean getAreWeOnHighway()
    {
        return areWeOnHighway;
    }
}
