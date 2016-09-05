package com.telenav.autopilotcontrol.app.car_data;

import android.util.Log;

/**
 * Created by ishwarya on 8/15/16.
 */
public class setStatusClass {
    public static long controlTimestamp= 0;
    public static long perceptionTimestamp= 0;
    public static long laneTimestamp= 0;
    public static long mapTimestamp= 0;

    public static boolean leftLaneVisible= false ;
    public static boolean rightLaneVisible = false;
    public static boolean onCentreLane = false;

    int controlMargin = 2000;
    int perceptionMargin = 3500;
    int laneMargin = 3500;
    int mapMargin = 8000;

    public void setControlTimestamp(long timestamp){
        controlTimestamp = timestamp;
    }
    public void setPerceptionTimestamp(long timestamp){
        perceptionTimestamp = timestamp;
    }
    public void setLaneTimestamp(long timestamp){
        laneTimestamp = timestamp;
    }
    public void setMapTimestamp(long timestamp){
        mapTimestamp = timestamp;
    }

    public void setIsLeftLaneVisible(boolean leftLaneVisible){
        this.leftLaneVisible = leftLaneVisible;
    }

    public void setIsRightLaneVisible(boolean rightLaneVisible){
        this.rightLaneVisible = rightLaneVisible;
    }

    public void setAreWeOnCentreLane(boolean onCentreLane){
        this.onCentreLane = onCentreLane;
    }



    public boolean gettingControlData(){
        return (System.currentTimeMillis() - controlTimestamp <= controlMargin);
    }
    public boolean gettingPerceptionData(){
        return System.currentTimeMillis() - perceptionTimestamp <= perceptionMargin;
    }
    public boolean gettingLaneData(){
        return System.currentTimeMillis() - laneTimestamp <= laneMargin;
    }
    public boolean gettingMapData(){
        return System.currentTimeMillis() - mapTimestamp <= mapMargin;
    }

    public boolean isLeftLaneVisible(){
        return leftLaneVisible;
    }

    public boolean isRightLaneVisible(){
        Log.d("rightLaneVisible","value"+this.rightLaneVisible);
        return this.rightLaneVisible;
    }

    public boolean areWeOnCentreLane(){
        return this.onCentreLane;
    }


}
