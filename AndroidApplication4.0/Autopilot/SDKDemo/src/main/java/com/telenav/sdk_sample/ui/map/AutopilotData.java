package com.telenav.sdk_sample.ui.map;

import com.telenav.sdk.navigation.controller.NavigationManager;
import com.telenav.sdk.navigation.model.NavigationData;
import com.telenav.sdk.navigation.model.RoadType;
import com.telenav.sdk.navigation.model.SpeedLimit;
import com.telenav.sdk_sample.car.data.DataParser;
import com.telenav.sdk_sample.car.data.Ego;

/**
 * Created by ninad on 7/7/16.
 */
public class AutopilotData
{
    static final int HIGHWAY = 1;
    static final int OTHER = 2;
    public static boolean isHighwayNext = false;
    public static boolean areWeOnHighway = false;
    MyMapRenderingUtils myMapRenderingUtils = new MyMapRenderingUtils();
    public static DataParser dataParser;


    //Gets the remaining distance for the autopilot to end
    //(Get the current distance travelled and then check the distance value to see the range in which we fall.
    // Return the distance which is at the end of this range)
    public double getDistance()
    {
        int remainingDistanceTillNextEdge;

        if (NavigationManager.getInstance().getNavigationData() != null && myMapRenderingUtils.distanceValues.size() > 0)
        {
            NavigationData navigationData = NavigationManager.getInstance().getNavigationData();
            int travelledDistance = navigationData.getTravelledDistance();

            if(navigationData.getRoadType() == RoadType.Highway)
            {
                areWeOnHighway = true;
            }
            else
            {
                areWeOnHighway = false;
            }

            for (int i = 0; i+1 < myMapRenderingUtils.distanceValues.size(); i++)
            {
                if ( travelledDistance < myMapRenderingUtils.distanceValues.get(i).distance)
                {
                    remainingDistanceTillNextEdge = myMapRenderingUtils.distanceValues.get(i).distance - travelledDistance;

                    if(MyMapRenderingUtils.distanceValues.get(i+1).type == HIGHWAY)
                    {
                        isHighwayNext = true;
                    }
                    else
                    {
                        isHighwayNext = false;
                    }
                    return remainingDistanceTillNextEdge;
                }
            }
        }
        return 0;
    }

    public int getSpeed()
    {
        int finalSpeed = 0;

        dataParser = new DataParser();

        if(dataParser.getSelfObject() != null)
        {
            Ego ego = dataParser.getSelfObject();
            if(ego.getSpeed() != null)
            {
                double[] speed = ego.getSpeed();

                finalSpeed = (int)(Math.sqrt(speed[0]*speed[1]*speed[2] + speed[0]*speed[1]*speed[2]));
                return (int)(finalSpeed * 2.23694);
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

}
