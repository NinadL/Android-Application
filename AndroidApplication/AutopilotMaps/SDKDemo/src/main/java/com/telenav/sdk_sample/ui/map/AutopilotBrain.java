package com.telenav.sdk_sample.ui.map;

import com.telenav.sdk.navigation.controller.NavigationManager;
import com.telenav.sdk.navigation.model.NavigationData;
import com.telenav.sdk.navigation.model.RoadType;
import com.telenav.sdk.navigation.model.SpeedLimit;

/**
 * Created by ninad on 7/7/16.
 */
public class AutopilotBrain
{
    static final int HIGHWAY = 1;
    static final int OTHER = 2;
    boolean isHighwayNext = false;
    public boolean areWeOnHighway = false;
    MyMapRenderingUtils myMapRenderingUtils = new MyMapRenderingUtils();


    //Gets the distance remaining distance for the autopilot to end
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

    //Gets the speed limit from the maps
    public int getSpeedLimit()
    {
        if (NavigationManager.getInstance().getNavigationData() != null) {

            NavigationData navigationData = NavigationManager.getInstance().getNavigationData();

            if(navigationData.getSpeedLimit() != null)
            {
                SpeedLimit speedLimit = navigationData.getSpeedLimit();
                double absValue = speedLimit.getValue();
                int unit = speedLimit.getUnit();

                if(unit == speedLimit.UNIT_KPH)
                {
                    absValue = absValue * 0.621371; //Convert to MPH
                }
                return (int)absValue;
            }
            else
            {
                return -1;
            }
        }
        return -1;
    }
}
