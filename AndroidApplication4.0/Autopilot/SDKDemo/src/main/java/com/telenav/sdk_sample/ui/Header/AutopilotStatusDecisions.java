package com.telenav.sdk_sample.ui.Header;

import android.net.ConnectivityManager;
import android.util.Log;

import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.car.data.DataParser;
import com.telenav.sdk_sample.car.data.setStatusClass;
import com.telenav.sdk_sample.text.to.speech.Speech;
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
    public static Timer timerForSpeedUpdate;
    private static DataParser dataParser = new DataParser();
    private static setStatusClass statusObject = new setStatusClass();
    private static Speech speech= new Speech();
    //Status will be set to 0 or 1 based on the condition.
    //If the condition is satisfied then the value is set to 1
    //else it is set to 0

    public static int[] status = {1, 1, 1, 1, 1};


    public void initTimer()
    {
        autopilotData = new AutopilotData();
        timerForStatusCheck = new Timer("MyTimer1", true);
        timerForStatusCheck.scheduleAtFixedRate(new StatusDecision(), 100, 1000);

        timerForSpeedUpdate = new Timer("MyTimer2", true);
        timerForSpeedUpdate.scheduleAtFixedRate(new SetSpeed(), 50, 1000);
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
        if(AutopilotData.areWeOnHighway)
        {
            if(!speech.onHighwayMessagePlayed)
            {
                MapActivity.textToSpeechManager.playAdvice(speech.onHighwayMessage);
                speech.onHighwayMessagePlayed = true;
            }
            return true;
        }
        else
        {
            setStatus(WE_ARE_NOT_HIGHWAY);
            speech.onHighwayMessagePlayed = false;
            return false;
        }
    }

    boolean isAutoPilotOnForCar()
    {
        return dataParser.getControlMessageObject().getIsAutoDrivingOn();
    }

    boolean areWeOnCentreLane()
    {
        if (statusObject.areWeOnCentreLane()) {
            return true;
        } else {
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


    class SetSpeed extends TimerTask
    {
        @Override
        public void run()
        {
            MapActivity.headerFragment.setSpeed();
        }
    }

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

                speech.autopilotEnableMessagePlayed = false;
                speech.autopilotEnabledMessagePlayed = false;
                MapActivity.headerFragment.canAutopilotBeEnabled = false;
            }
            else if(!areNodesWorking())
            {
                MapActivity.headerFragment.setAutopilotStatusImageAndText(MANUAL_DRIVE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);

                speech.autopilotEnableMessagePlayed = false;
                speech.autopilotEnabledMessagePlayed = false;
                MapActivity.headerFragment.canAutopilotBeEnabled = false;
            }
            else if(areWeOnHighway() &&
                    !isAutoPilotOnForCar() &&
                    isDistanceToAutopilotEndMoreThan3Miles() &&
                    areWeOnCentreLane())
            {
                MapActivity.headerFragment.canAutopilotBeEnabled = true;

                if(!speech.autopilotEnableMessagePlayed)
                {
                    MapActivity.textToSpeechManager.playAdvice(speech.autopilotEnableMessage);
                    speech.autopilotEnableMessagePlayed = true;
                }

                MapActivity.headerFragment.setAutopilotStatusImageAndText(AUTOPILOT_AVAILABLE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(ENABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);

                speech.autopilotEnabledMessagePlayed = false;
            }

            else if(isAutoPilotOnForCar())
            {

                if(!speech.autopilotEnabledMessagePlayed)
                {
                    MapActivity.textToSpeechManager.playAdvice(speech.autopilotEnabledMessage);
                    speech.autopilotEnabledMessagePlayed = true;
                }

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

                speech.autopilotEnableMessagePlayed = false;
                MapActivity.headerFragment.canAutopilotBeEnabled = false;
            }

            else
            {
                Log.d("StatusDecision", "else");
                MapActivity.headerFragment.setAutopilotStatusImageAndText(MANUAL_DRIVE);
                MapActivity.headerFragment.setIncreaseDecreaseSpeed(DISABLED);
                MapActivity.headerFragment.setEngageAutopilot(DISABLED);
                MapActivity.headerFragment.setVisibilityForSpeedLimitAndRefSpeed(NORMAL);

                speech.autopilotEnableMessagePlayed = false;
                speech.autopilotEnabledMessagePlayed = false;
                MapActivity.headerFragment.canAutopilotBeEnabled = false;
            }
        }
    }



}
