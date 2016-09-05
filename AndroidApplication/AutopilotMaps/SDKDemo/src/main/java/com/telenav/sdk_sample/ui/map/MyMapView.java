package com.telenav.sdk_sample.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.telenav.foundation.vo.LatLon;
import com.telenav.map.engine.GLMapImageAnnotation;
import com.telenav.map.vo.GLMapRoute;
import com.telenav.sdk.map.MapView;
import com.telenav.sdk.navigation.model.DisplayRouteInfo;
import com.telenav.sdk.navigation.model.Edge;
import com.telenav.sdk.navigation.model.RoadType;
import com.telenav.sdk.navigation.model.Segment;
import com.telenav.sdk_sample.R;

import java.util.List;

/**
 * Created by ninad on 7/7/16.
 */
public class MyMapView extends MapView
{
    private GLMapImageAnnotation startAnnotation;
    private GLMapImageAnnotation endAnnotation;


    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    //We need to override this method as we need to highlight the route of autopilot
    public void addRoute(DisplayRouteInfo displayRouteInfo, String routeStyle)
    {
        if(displayRouteInfo != null) {
            GLMapRoute glMapRoute = MyMapRenderingUtils.convertDisplayRouteInfoToGLMapRouteToDisplayHighway(displayRouteInfo);
            if(glMapRoute != null) {

                setAnnotationsOnMap(displayRouteInfo);
                super.addRoute(glMapRoute, displayRouteInfo.getRouteId(), routeStyle != null?routeStyle:"route.routeA");
            }
        }
    }


    //Method to set annotations on the maps
    public void setAnnotationsOnMap(DisplayRouteInfo displayRouteInfo)
    {
        boolean isHighwayStarted = false;

        List<Segment> segmentList = displayRouteInfo.getSegments();

        for(int i = 1; i < segmentList.size(); i++)
        {
            Segment segment = segmentList.get(i);

            List<Edge> edgeList = segment.getEdges();

            for(int j = 0; j < edgeList.size(); j++)
            {

                if(edgeList.get(j).getRoadType() == RoadType.Highway) {
                    if (!isHighwayStarted) {
                        LatLon latLonStart = new LatLon();

                        latLonStart.setLat(edgeList.get(j).getShapePoints().get(0).getLat());
                        latLonStart.setLon(edgeList.get(j).getShapePoints().get(0).getLon());

                        Bitmap bitmapStart = BitmapFactory.decodeResource(getResources(), R.drawable.autopilot_start);
                        startAnnotation = new GLMapImageAnnotation(getContext(), 0, bitmapStart, latLonStart);
                        addAnnotation(startAnnotation);

                        isHighwayStarted = true;
                    }
                }
                else
                {
                    if(isHighwayStarted)
                    {
                        LatLon latLonEnd = new LatLon();

                        latLonEnd.setLat(edgeList.get(j).getShapePoints().get(0).getLat());
                        latLonEnd.setLon(edgeList.get(j).getShapePoints().get(0).getLon());

                        Bitmap bitmapEnd = BitmapFactory.decodeResource(getResources(), R.drawable.autopilot_end);

                        endAnnotation = new GLMapImageAnnotation(getContext(), 0, bitmapEnd, latLonEnd);
                        addAnnotation(endAnnotation);

                        isHighwayStarted = false;
                    }
                }
            }
        }
    }
}
