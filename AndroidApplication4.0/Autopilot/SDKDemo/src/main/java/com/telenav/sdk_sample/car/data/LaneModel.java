package com.telenav.sdk_sample.car.data;

import java.util.ArrayList;

/**
 * Created by ninad on 6/16/16.
 */
public class LaneModel
{
    static Lane rightLaneBoundary;
    static Lane leftLaneBoundary;

    int leftLaneType = 3;
    int rightLaneType = 3;

    static boolean isRightRightLanePresent = false;
    static boolean isRightLanePresent = false;
    static boolean isLeftLanePresent = false;
    static boolean isLeftLeftLanePresent = false;
    static setStatusClass statusClass = new setStatusClass();

    ArrayList<Lane> sortedBoundaries = new ArrayList<Lane>();


    public ArrayList<Lane> constructLanes(ArrayList<Lane> laneBoundaries)
    {
        isRightRightLanePresent = false;
        isRightLanePresent = false;
        isLeftLanePresent = false;
        isLeftLeftLanePresent = false;

        sortedBoundaries.clear();

        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            //find ideal lane boundary for all the lanes except for the centre lane
            if(laneBoundaries.get(i).getType() != 9)
            {
                findMatchingPair(laneBoundaries.get(i));
            }
        }

        //Set the type based on the present lane boundaries
        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if (laneBoundaries.get(i).getType() == 9)
            {
                //extendLane(laneBoundaries.get(i));

                if(isLeftLanePresent && isRightLanePresent)
                {
                    rightLaneBoundary = createImaginaryLane(-1.8, laneBoundaries.get(i));
                    rightLaneBoundary.setType(rightLaneType);
                    sortedBoundaries.add(rightLaneBoundary);

                    leftLaneBoundary = createImaginaryLane(1.8, laneBoundaries.get(i));
                    leftLaneBoundary.setType(leftLaneType);
                    sortedBoundaries.add(leftLaneBoundary);

                    if(rightLaneType == 1 && leftLaneType == 1) {
                        statusClass.setAreWeOnCentreLane(true);
                    }
                    else
                    {
                        statusClass.setAreWeOnCentreLane(false);
                    }
                }
                else if(isLeftLanePresent)
                {
                    rightLaneBoundary = createImaginaryLane(-1.8, laneBoundaries.get(i));
                    rightLaneBoundary.setType(rightLaneType);
                    sortedBoundaries.add(rightLaneBoundary);

                    leftLaneBoundary = createImaginaryLane(1.8, laneBoundaries.get(i));
                    leftLaneBoundary.setType(leftLaneType);
                    sortedBoundaries.add(leftLaneBoundary);

                }
                else if(isRightLanePresent)
                {
                    rightLaneBoundary = createImaginaryLane(-1.8, laneBoundaries.get(i));
                    rightLaneBoundary.setType(rightLaneType);
                    sortedBoundaries.add(rightLaneBoundary);

                    leftLaneBoundary = createImaginaryLane(1.8, laneBoundaries.get(i));
                    leftLaneBoundary.setType(leftLaneType);
                    sortedBoundaries.add(leftLaneBoundary);
                }
                else
                {
                    rightLaneBoundary = createImaginaryLane(-1.8, laneBoundaries.get(i));
                    rightLaneBoundary.setType(rightLaneType);
                    sortedBoundaries.add(rightLaneBoundary);

                    leftLaneBoundary = createImaginaryLane(1.8, laneBoundaries.get(i));
                    leftLaneBoundary.setType(leftLaneType);
                    sortedBoundaries.add(leftLaneBoundary);
                }
                sortedBoundaries.add(laneBoundaries.get(i));
                leftLaneType = 3;
                rightLaneType = 3;
            }
        }
        return this.sortedBoundaries;
    }

    //Create imaginary lane based on the centre lanes
    Lane createImaginaryLane(double missingLaneBoundaryYCoord, Lane adjacentLane) {
        Lane imaginaryLane = new Lane();
        if (adjacentLane == null) {
            for (int i = 0; i < 100; i = i + 4) {
                Point point = new Point();
                point.setX(i);
                point.setY(missingLaneBoundaryYCoord);
                imaginaryLane.addPoint(point);
            }
        } else {
            double distanceBetweenLanes = missingLaneBoundaryYCoord - adjacentLane.getPoints().get(0).getY();

            for (int i = 0; i < adjacentLane.getPoints().size(); i++) {
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

    //Finds the closest lane boundary to the ideal lane boundary
    void findMatchingPair(Lane lane)
    {
        double y = lane.getPoints().get(0).getY();

        double targetValueOne = Math.pow((5.4 - y), 2);
        double targetValueTwo = Math.pow((1.8 - y), 2);

        double targetValueThree = Math.pow((-5.4 - y), 2);
        double targetValueFour = Math.pow((-1.8 - y), 2);

        double minValue = targetValueOne;
        double lanePresent = 5.4;

        if (targetValueTwo < minValue) {
            minValue = targetValueTwo;
            lanePresent = 1.8;
        }
        if (targetValueThree < minValue) {
            minValue = targetValueThree;
            lanePresent = -5.4;
        }
        if (targetValueFour < minValue) {
            minValue = targetValueFour;
            lanePresent = -1.8;
        }

        if (lanePresent == 5.4)
        {
            isLeftLeftLanePresent = true;
        }
        else if (lanePresent == 1.8)
        {
            isLeftLanePresent = true;
            leftLaneType = lane.getType();
        }
        else if (lanePresent == -1.8)
        {
            isRightLanePresent = true;
            rightLaneType = lane.getType();
        }
        else if (lanePresent == -5.4)
        {
            isRightRightLanePresent = true;
        }
    }

    void extendLane(Lane lane)
    {
        ArrayList<Point> points = lane.getPoints();

        if(points.get(points.size()-1).getX() < 200.0d)
        {
            Point lastPoint = points.get(points.size()-1);
            double xCoordinate = points.get(points.size()-1).getX();
            double yCoordinate = points.get(points.size()-1).getY();

            xCoordinate = xCoordinate + 2;

            while(xCoordinate < 200.0d)
            {
                Point newPoint = lastPoint;
                newPoint.setX(xCoordinate);
                lane.addPoint(lastPoint);
                xCoordinate = xCoordinate+1;
                lastPoint = newPoint;
            }
        }
    }

}