package com.telenav.autopilotcontrol.app.ui;

import com.telenav.autopilotcontrol.app.car_data.DataParser;
import com.telenav.autopilotcontrol.app.car_data.Ego;

/**
 * Created by ishwarya on 7/29/16.
 */
    public class AutopilotBrainData
    {
        static boolean isNavigationStarted= false;
        static double remainingDistance;
        static int speedLimit = 0;
        static boolean isHighwayNext = false;
        static boolean areWeOnHighway = false;
        private DataParser dataParser = new DataParser();

        public void setIsNavigationStarted(boolean  isNavigationStarted)
        {
            this.isNavigationStarted = isNavigationStarted;
        }

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

        public boolean getIsNavigationStarted()
        {
            return isNavigationStarted;
        }
        public boolean getIsHighwayNext()
        {
            return isHighwayNext;
        }

        public boolean getAreWeOnHighway()
        {
            return areWeOnHighway;
        }

        public float getSteeringWheelPosition()
        {

            dataParser = new DataParser();

            if(dataParser.getSelfObject()!= null)
            {
                Ego ego = dataParser.getSelfObject();

                if(ego.getSteeringWheel() != 0)
                {
                    double steeringWheelPosition = ego.getSteeringWheel();

                    return (float)((steeringWheelPosition * 180)/3.14159);
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                return 0;
            }
        }

        public int getSpeed()
        {

            int finalSpeed;

            dataParser = new DataParser();

            if(dataParser.getSelfObject()!= null)
            {
                Ego ego = dataParser.getSelfObject();

                if(ego.getSpeed() != null)
                {
                    double[] speed = ego.getSpeed();

                    finalSpeed = (int) (Math.sqrt((speed[0] * (speed[0])) + (speed[1] * (speed[1])) + (speed[2] * (speed[2]))));

                    return (int) (finalSpeed*2.23694);
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }

    }
