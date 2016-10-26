package com.telenav.sdk_sample.ui.Header;

import android.net.ConnectivityManager;
import android.util.Log;

import com.telenav.sdk_sample.car.data.DataParser;
import com.telenav.sdk_sample.car.data.setStatusClass;
import com.telenav.sdk_sample.ui.map.AutopilotData;
import com.telenav.sdk_sample.ui.map.MapActivity;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by user on 25-Oct-16.
 */
public class AutopilotStatusDecisions
{
    private static AutopilotData autopilotData;
    public static Timer timerForStatusCheck;
    private static DataParser dataParser = new DataParser();
    private static setStatusClass statusObject = new setStatusClass();
    //Status will be set to 0 or 1 based on the condition.
    //If the condition is satisfied then the value is set to 1
    //else it is set to 0

    private int[] status = {WORKING, WORKING, WORKING, WORKING, WORKING};


    public void initTimer()
    {
        autopilotData = new AutopilotData();
        timerForStatusCheck = new Timer("MyTimer1", true);
        timerForStatusCheck.scheduleAtFixedRate(new StatusDecision(), 100, 1000);
    }

    void setStatus(int index)
    {
        for(int i = 0; i < index; i++)
        {
            status[i] = 1;
        }
        for(int i = index; i <5; i++)
        {
            status[i] = 0;
        }
    }

    boolean isNetworkWorking()
    {
        boolean exists = true;
//        try {
//            SocketAddress sockaddr = new InetSocketAddress("192.168.1.190", 1234);
//            // Create an unbound socket
//            Socket sock = new Socket();
//
//            // This method will block no more than timeoutMs.
//            // If the timeout occurs, SocketTimeoutException is thrown.
//            int timeoutMs = 2000;   // 2 seconds
//            sock.connect(sockaddr, timeoutMs);
//            if(exists)
//            {
//                return true;
//            }
//            else
//            {
//                setStatus(NETWORK_NOT_WORKING);
//                return false;
//            }
//        }
//        catch(Exception e){
//        }
        return exists;
    }

    boolean areNodesWorking()
    {
        if(statusObject.gettingPerceptionData() &&
                statusObject.gettingControlData() &&
                statusObject.gettingLaneData())
        {
            return true;
        }
        else
        {
            setStatus(NODES_NOT_WORKING);
            return false;
        }
    }

    boolean isDistanceToAutopilotEndMoreThan3Miles()
    {
        double remainingDistance = autopilotData.getDistance();
        //Convert meters to miles
        remainingDistance = remainingDistance * 0.000621371;

        Log.d("DistanceRemaining", String.valueOf(remainingDistance));
        if(remainingDistance > 3.00)
        {
            return true;
        }
        else
        {
            setStatus(DISTANCE_LESS_THAN_3_MILES);
            return false;
        }
    }

    boolean areWeOnHighway()
    {
        if(AutopilotData.areWeOnHighway) {
            return true;
        }
        else
        {
            setStatus(WE_ARE_NOT_HIGHWAY);
            return false;
        }
    }

    boolean isAutoPilotOnForCar()
    {
        return dataParser.getControlMessageObject().getIsAutoDrivingOn();
    }

    boolean areWeOnCentreLane()
    {
        if(statusObject.areWeOnCentreLane()) {
            return true;
        }
        else
        {
            setStatus(WE_ARE_NOT_ON_CENTRE_LANE);
            return false;
        }
    }

    //Constant index
    private static final int NETWORK_NOT_WORKING = 0;
    private static final int NODES_NOT_WORKING = 1;
    private static final int WE_ARE_NOT_HIGHWAY = 2;
    private static final int DISTANCE_LESS_THAN_3_MILES = 3;
    private static final int WE_ARE_NOT_ON_CENTRE_LANE = 4;

    private static final int NOT_WORKING = 0;
    private static final int WORKING = 1;

    private static final int MANUAL_DRIVE = 0;
    private static final int AUTOPILOT_AVAILABLE = 1;
    private static final int AUTOPILOT_ENABLED = 2;
    private static final int PREPARE_TO_TAKE_OVER = 3;
    private static final int PLEASE_TAKE_OVER = 4;
    private static final int TAKE_OVER_NOW = 5;

    private static final int DISABLED = 0;
    private static final int ENABLED = 1;

    private static final int NORMAL = 0;
    private static final int AUTOPILOT = 1;


    class StatusDecision extends TimerTask
    {

        @Override
        public void run()
        {
            Log.d("Hello", "AreWeOnHighway : "+ String.valueOf(areWeOnHighway())+ "  isAutoPilotOnForCar : "
                    + String.valueOf(isAutoPilotOnForCar()) + " isDistanceToAutopilotEndMoreThan3Miles : " + String.valueOf(isDistanceToAutopilotEndMoreThan3Miles())+
            " areWeOnCentreLane : " + String.valueOf(areWeOnCentreLane()));

            //Check if we are connected to the network
            if(!isNetworkWorking())
            {
                MapActivity.headerFragment.setAutopilotStatusImageAndText(MANUAL_DRIVE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);
            }
            else if(!areNodesWorking())
            {
                MapActivity.headerFragment.setAutopilotStatusImageAndText(MANUAL_DRIVE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);
            }
            else if(areWeOnHighway() &&
                    !isAutoPilotOnForCar() &&
                    isDistanceToAutopilotEndMoreThan3Miles() &&
                    areWeOnCentreLane())
            {
                MapActivity.headerFragment.setAutopilotStatusImageAndText(AUTOPILOT_AVAILABLE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(ENABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);
            }

            else if(isAutoPilotOnForCar())
            {
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(AUTOPILOT);


                double remainingDistance = autopilotData.getDistance();
                //Convert meters to miles
                remainingDistance = remainingDistance * 0.000621371;
                if(remainingDistance > 5.00)
                {
                    MapActivity.headerFragment.setIncreaseDecreaseSpeed(ENABLED);
                    MapActivity.headerFragment.setAutopilotStatusImageAndText(AUTOPILOT_ENABLED);
                }
                else if(isDistanceToAutopilotEndMoreThan3Miles())
                {
                    MapActivity.headerFragment.setIncreaseDecreaseSpeed(ENABLED);
                    MapActivity.headerFragment.setAutopilotStatusImageAndText(PREPARE_TO_TAKE_OVER);
                }
                else if(remainingDistance <= 3.00 && remainingDistance > 1.00)
                {
                    MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                    MapActivity.headerFragment.setAutopilotStatusImageAndText(PLEASE_TAKE_OVER);
                }
                else
                {
                    MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                    MapActivity.headerFragment.setAutopilotStatusImageAndText(TAKE_OVER_NOW);
                }
            }

            else
            {
                Log.d("StatusDecision", "else");
                MapActivity.headerFragment.setAutopilotStatusImageAndText(MANUAL_DRIVE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);

            }

        }
    }



}