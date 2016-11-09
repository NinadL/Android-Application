package com.telenav.sdk_sample.car.data;

import java.util.ArrayList;

/**
 * Created by ninad on 6/16/16.
 */
public class LaneModel
{
    static Lane rightRightLaneBoundary;
    static Lane rightLaneBoundary;
    static Lane leftLaneBoundary;
    static Lane leftLeftLaneBoundary;

    static int NOT_PRESENT = -1;

    ArrayList<LaneModelObject> laneBoundaries = new ArrayList<LaneModelObject>();
    ArrayList<Lane> sortedBoundaries = new ArrayList<Lane>();



    public ArrayList<Lane> sortLanes(ArrayList<Lane> laneBoundaries)
    {

        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if(laneBoundaries.get(i).getType() == 9)
            {

                rightRightLaneBoundary = createImaginaryLane(-5.4, laneBoundaries.get(i));
                rightRightLaneBoundary.setType(2);
                sortedBoundaries.add(rightRightLaneBoundary);

                rightLaneBoundary = createImaginaryLane(-1.8, laneBoundaries.get(i));
                rightLaneBoundary.setType(1);
                sortedBoundaries.add(rightLaneBoundary);

                leftLaneBoundary = createImaginaryLane(1.8, laneBoundaries.get(i));
                leftLaneBoundary.setType(1);
                sortedBoundaries.add(leftLaneBoundary);

                leftLeftLaneBoundary = createImaginaryLane(5.4, laneBoundaries.get(i));
                leftLeftLaneBoundary.setType(2);
                sortedBoundaries.add(leftLeftLaneBoundary);
            }
        }
        return this.sortedBoundaries;
    }

    //Create imaginary lane based on the surrounding lanes
    Lane createImaginaryLane(double missingLaneBoundaryYCoord, Lane adjacentLane)
    {
        Lane imaginaryLane = new Lane();
        if(adjacentLane == null)
        {
            for(int i = 0; i < 100 ; i=i+4)
            {
                Point point = new Point();
                point.setX(i);
                point.setY(missingLaneBoundaryYCoord);
                imaginaryLane.addPoint(point);
            }
        }
        else
        {
            double distanceBetweenLanes = missingLaneBoundaryYCoord - adjacentLane.getPoints().get(0).getY();

            for(int i = 0; i < adjacentLane.getPoints().size(); i++)
            {
                Point point = new Point();
                point.setX(adjacentLane.getPoints().get(i).getX());
                point.setY(adjacentLane.getPoints().get(i).getY() + distanceBetweenLanes);
                point.setNx(adjacentLane.getPoints().get(i).getNx());
                point.setNy(adjacentLane.getPoints().get(i).getNy());
                imaginaryLane.addPoint(point);
            }
        }
        return imaginaryLane;
    }
}

class LaneModelObject
{
    int id;
    Lane laneboundary;
    int type;
    Double y;

    public LaneModelObject(Lane laneboundary, int type, double y)
    {
        this.laneboundary = laneboundary;
        this.type = type;
        this.y = y;
    }

    public Double getY()
    {
        return y;
    }
}