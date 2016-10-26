package com.telenav.sdk_sample.ui.map;

import com.telenav.map.vo.GLMapRoute;
import com.telenav.sdk.map.MapRenderingUtils;
import com.telenav.sdk.navigation.model.DisplayRouteInfo;
import com.telenav.sdk.navigation.model.Edge;
import com.telenav.sdk.navigation.model.RoadType;
import com.telenav.sdk.navigation.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ninad on 7/7/16.
 */
public class MyMapRenderingUtils extends MapRenderingUtils
{
    static ArrayList<DistanceValues> distanceValues = new ArrayList<DistanceValues>();
    static double distance;
    static final int HIGHWAY = 1;
    static final int OTHER = 2;


//We need this method to identify the section in the map which can be used for autopilot

    public static GLMapRoute convertDisplayRouteInfoToGLMapRouteToDisplayHighway(DisplayRouteInfo route)
    {
        distanceValues.clear();
        distanceValues = new ArrayList<DistanceValues>();
        distance = 0;

        if(route == null) {
            return null;
        }
        else
        {
            List segments = route.getSegments();

            if(segments == null)
            {
                return null;
            }
            else
            {
                GLMapRoute mapRoute = new GLMapRoute();

                for(int i = 0; i < segments.size(); ++i)
                {
                    Segment segment = (Segment)segments.get(i);

                    if(segment != null)
                    {
                        GLMapRoute.GLMapSegment mapSegment = new GLMapRoute.GLMapSegment();
                        mapRoute.getSegmentList().add(mapSegment);
                        List edges = segment.getEdges();

                        if(edges != null)
                        {
                            for(int j = 0; j < edges.size(); ++j)
                            {
                                Edge edge = (Edge) edges.get(j);
                                GLMapRoute.GLMapEdge mapEdge = new GLMapRoute.GLMapEdge();

                                if(edge.getRoadType() == RoadType.Highway)
                                {
                                    mapEdge.setStyle(GLMapRoute.GLMapEdge.eEdgeStyle.eEdgeStyle_SlowSpeed);
                                    DistanceValues distanceValue = new DistanceValues();
                                    distanceValue.distance = edge.getLength();
                                    distanceValue.type = HIGHWAY;
                                    distanceValues.add(distanceValue);
                                }
                                else
                                {
                                    DistanceValues distanceValue = new DistanceValues();
                                    distanceValue.distance = edge.getLength();
                                    distanceValue.type = OTHER;
                                    distanceValues.add(distanceValue);
                                }
                                mapEdge.getLatLonList().addAll(edge.getShapePoints());
                                mapSegment.addEdge(mapEdge);
                            }
                        }
                    }
                }
                createDistanceValueArraylist();
                return mapRoute;
            }
        }
    }

    //If there are adjacent edges of same type then merge them
    static public void createDistanceValueArraylist()
    {

        for(int i = 0; i+1 < distanceValues.size(); i++)
        {
            if((distanceValues.get(i).type == distanceValues.get(i+1).type))
            {
                distanceValues.get(i).distance = distanceValues.get(i).distance + distanceValues.get(i+1).distance;
                distanceValues.remove(i+1);
                i--;
            }
        }

        for(int i = 1; i < distanceValues.size(); i++)
        {
            DistanceValues dv1 = distanceValues.get(i-1);
            DistanceValues dv2 = distanceValues.get(i);

            dv2.distance = dv1.distance+dv2.distance;
            distanceValues.set(i, dv2);
        }
    }
}

class DistanceValues
{
    int distance;
    int type;
}
