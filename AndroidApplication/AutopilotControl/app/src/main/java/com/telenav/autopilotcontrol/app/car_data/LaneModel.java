package com.telenav.autopilotcontrol.app.car_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ninad on 6/16/16.
 */
public class LaneModel
{
    static Lane rightRightLaneBoundary;
    static Lane rightLaneBoundary;
    static Lane leftLaneBoundary;
    static Lane leftLeftLaneBoundary;
    static final double DISTANCE_BETWEEN_LANE_BOUNDARIES = 36;

    static int NOT_PRESENT = -1;

    ArrayList<LaneModelObject> laneBoundaries = new ArrayList<LaneModelObject>();
    ArrayList<Lane> sortedBoundaries = new ArrayList<Lane>();



    public ArrayList<Lane> sortLanes(ArrayList<Lane> laneBoundaries)
    {
        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if(laneBoundaries.get(i).getType() != 9 && laneBoundaries.get(i).getType() != 10) {
                Point pointOne = laneBoundaries.get(i).getPoints().get(0);
                Point pointTwo = laneBoundaries.get(i).getPoints().get(1);

                double xOne = pointOne.getX();
                double xTwo = pointTwo.getX();

                double yOne = pointOne.getY();
                double yTwo = pointTwo.getY();

                Double y = findYIntercept(xOne, xTwo, yOne, yTwo);

                this.laneBoundaries.add(new LaneModelObject(laneBoundaries.get(i), laneBoundaries.get(i).getType(), y));
            }

        }

        Collections.sort(this.laneBoundaries, new Comparator<LaneModelObject>() {
            public int compare(LaneModelObject o1, LaneModelObject o2) {
                return o1.getY().compareTo(o2.getY());
            }
        });

        sortLanesAsPerOrder();

        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if(laneBoundaries.get(i).getType() == 9 || laneBoundaries.get(i).getType() == 10)
            {
                sortedBoundaries.add(laneBoundaries.get(i));
            }
        }
        return this.sortedBoundaries;
    }


    private double findYIntercept(double x1, double x2, double y1, double y2)
    {
        double y;
        if (x1 != 0)
        {
            y = y1 + ((y2 - y1)/(x2 - x1))*(0 - x1);
        }
        else {
            return y1;
        }
        return y;
    }

    private void sortLanesAsPerOrder()
    {
        rightRightLaneBoundary = null;
        rightLaneBoundary = null;
        leftLaneBoundary = null;
        leftLeftLaneBoundary = null;

        if (laneBoundaries.size() == 1)
        {
            sortForOneLaneBoundaries();

        }
        else if (laneBoundaries.size() == 2)
        {
            sortForTwoLaneBoundaries();

        }
        else if (laneBoundaries.size() == 3)
        {
            sortForThreeLaneBoundaries();

        }
        else if (laneBoundaries.size() == 4) {
            leftLeftLaneBoundary = laneBoundaries.get(0).laneboundary;
            leftLaneBoundary = laneBoundaries.get(1).laneboundary;
            rightLaneBoundary = laneBoundaries.get(2).laneboundary;
            rightRightLaneBoundary = laneBoundaries.get(3).laneboundary;
        }
        sortedBoundaries.clear();
        sortedBoundaries.add(leftLeftLaneBoundary);
        sortedBoundaries.add(leftLaneBoundary);
        sortedBoundaries.add(rightLaneBoundary);
        sortedBoundaries.add(rightRightLaneBoundary);
    }

    void sortForThreeLaneBoundaries()
    {
        int numberOfPositives = 0;
        int numberOfNegatives = 0;
        double targetValueOne;
        double targetValueTwo;


        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if (laneBoundaries.get(i).getY() > 0)
            {
                numberOfPositives++;
            }
            else
            {
                numberOfNegatives++;
            }
        }

        if(numberOfNegatives == 1)
        {
            targetValueOne = Math.pow((-5.4-(laneBoundaries.get(0).getY())), 2);
            targetValueTwo = Math.pow((-1.8-(laneBoundaries.get(0).getY())), 2);
        }
        else
        {
            targetValueOne = Math.pow((5.4-(laneBoundaries.get(2).getY())), 2);
            targetValueTwo = Math.pow((1.8-(laneBoundaries.get(2).getY())), 2);
        }


        if (targetValueOne < targetValueTwo && numberOfNegatives == 1)
        {
            System.out.println("Missing lane at -1.8");
            rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            rightLaneBoundary = null;
            leftLaneBoundary = laneBoundaries.get(1).laneboundary;
            leftLeftLaneBoundary = laneBoundaries.get(2).laneboundary;
        }
        else if (numberOfNegatives == 1)
        {
            System.out.println("Missing lane at -5.4");
            rightRightLaneBoundary = null;
            rightLaneBoundary = laneBoundaries.get(0).laneboundary;
            leftLaneBoundary = laneBoundaries.get(1).laneboundary;
            leftLeftLaneBoundary = laneBoundaries.get(2).laneboundary;
        }

        if (targetValueOne < targetValueTwo && numberOfPositives == 1)
        {
            System.out.println("Missing lane at 1.8");
            rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            rightLaneBoundary = laneBoundaries.get(1).laneboundary;
            leftLaneBoundary = null;
            leftLeftLaneBoundary = laneBoundaries.get(2).laneboundary;
        }
        else if (numberOfPositives == 1)
        {
            System.out.println("Missing lane at 5.4");
            rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            rightLaneBoundary = laneBoundaries.get(1).laneboundary;
            leftLaneBoundary = laneBoundaries.get(2).laneboundary;
            leftLeftLaneBoundary = null;
        }
    }

    void sortForTwoLaneBoundaries()
    {
        int numberOfPositives = 0;
        int numberOfNegatives = 0;

        double targetValueOne = 0;
        double targetValueTwo = 0;
        double targetValueThree = 0;
        double targetValueFour = 0;

        boolean rr = true, r = true, l = true, ll = true;

        for (int i = 0; i < laneBoundaries.size(); i++)
        {
            if (laneBoundaries.get(i).getY() > 0)
            {
                numberOfPositives++;
            }
            else
            {
                numberOfNegatives++;
            }
        }

        if(numberOfPositives == 1)
        {
            targetValueOne = Math.pow((5.4-(laneBoundaries.get(1).getY())), 2);
            targetValueTwo = Math.pow((1.8-(laneBoundaries.get(1).getY())), 2);

            targetValueThree = Math.pow((-5.4-(laneBoundaries.get(0).getY())), 2);
            targetValueFour = Math.pow((-1.8-(laneBoundaries.get(0).getY())), 2);

            if(targetValueOne > targetValueTwo)
            {
                System.out.println("Missing lane at 5.4");
                leftLeftLaneBoundary = null;
                ll = false;
            }
            else if(targetValueTwo > targetValueOne)
            {
                System.out.println("Missing lane at 1.8");
                leftLaneBoundary = null;
                l = false;
            }
            if(targetValueThree > targetValueFour)
            {
                System.out.println("Missing lane at -5.4");
                rightRightLaneBoundary = null;
                rr = false;

            }
            else if(targetValueFour > targetValueThree)
            {
                System.out.println("Missing lane at -1.8");
                rightLaneBoundary = null;
                r = false;
            }
        }
        else if(numberOfNegatives == 2)
        {
            System.out.println("Missing lane at 1.8");
            System.out.println("Missing lane at 5.4");
            rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            rightLaneBoundary = laneBoundaries.get(1).laneboundary;
            leftLaneBoundary = null;
            leftLeftLaneBoundary = null;
        }
        else if(numberOfPositives == 2)
        {
            System.out.println("Missing lane at -1.8");
            System.out.println("Missing lane at -5.4");
            rightRightLaneBoundary = null;
            rightLaneBoundary = null;
            leftLaneBoundary = laneBoundaries.get(0).laneboundary;
            leftLeftLaneBoundary = laneBoundaries.get(1).laneboundary;
        }
        if (ll == false || l == false || r == false || rr == false)
        {
            if(ll == false && r == false)
            {
                leftLaneBoundary = laneBoundaries.get(1).laneboundary;
                rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            }
            else if(ll == false && rr == false)
            {
                leftLaneBoundary =  laneBoundaries.get(1).laneboundary;
                rightLaneBoundary = laneBoundaries.get(0).laneboundary;
            }
            else if(l == false && r == false)
            {
                leftLeftLaneBoundary = laneBoundaries.get(1).laneboundary;
                rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            }
            else if(l == false && rr == false)
            {
                leftLeftLaneBoundary = laneBoundaries.get(1).laneboundary;
                rightLaneBoundary = laneBoundaries.get(0).laneboundary;
            }
        }

    }

    void sortForOneLaneBoundaries()
    {
        double targetValueOne = 0;
        double targetValueTwo = 0;
        double targetValueThree = 0;
        double targetValueFour = 0;

        targetValueOne = Math.pow((-5.4-(laneBoundaries.get(0).getY())), 2);
        targetValueTwo = Math.pow((-1.8-(laneBoundaries.get(0).getY())), 2);
        targetValueThree = Math.pow((5.4-(laneBoundaries.get(0).getY())), 2);
        targetValueFour = Math.pow((1.8-(laneBoundaries.get(0).getY())), 2);


        if (targetValueOne < targetValueTwo &&
                targetValueOne < targetValueThree &&
                targetValueOne < targetValueFour)
        {
            rightRightLaneBoundary = laneBoundaries.get(0).laneboundary;
            rightLaneBoundary = null;
            leftLaneBoundary = null;
            leftLeftLaneBoundary = null;
        }
        else if(targetValueTwo < targetValueOne &&
                targetValueTwo < targetValueThree &&
                targetValueTwo < targetValueFour)
        {
            rightRightLaneBoundary = null;
            rightLaneBoundary = laneBoundaries.get(0).laneboundary;
            leftLaneBoundary = null;
            leftLeftLaneBoundary = null;
        }
        else if(targetValueThree < targetValueOne &&
                targetValueThree < targetValueTwo &&
                targetValueThree < targetValueFour)
        {
            rightRightLaneBoundary = null;
            rightLaneBoundary = null;
            leftLaneBoundary = null;
            leftLeftLaneBoundary =  laneBoundaries.get(0).laneboundary;
        }
        else
        {
            rightRightLaneBoundary = null;
            rightLaneBoundary = null;
            leftLaneBoundary = laneBoundaries.get(0).laneboundary;
            leftLeftLaneBoundary = null;

        }
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