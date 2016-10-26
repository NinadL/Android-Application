package com.telenav.sdk_sample.car.data;

/**
 * Created by ninad on 6/16/16.
 */
public class ControlMessage {

    public static boolean isAutoDrivingOn = false;
    public static boolean isAutoDrivingReady = false;

    static boolean turnOnAutoDrive = false;

    public void setIsAutoDrivingOn(boolean isAutoDrivingOn)
    {
        this.isAutoDrivingOn = isAutoDrivingOn;
    }

    public void setIsAutoDrivingReady(boolean isAutoDrivingReady)
    {
        this.isAutoDrivingReady = isAutoDrivingReady;
    }

    public void setTurnOnAutoDrive(boolean turnOnAutoDrive)
    {
        this.turnOnAutoDrive = turnOnAutoDrive;
    }

    public boolean getTurnOnAutoDrive()
    {
        return turnOnAutoDrive;
    }


    public boolean getIsAutoDrivingOn()
    {
        return isAutoDrivingOn;
    }

    public boolean getIsAutoDrivingReady()
    {
        return  isAutoDrivingReady;
    }
}
