package com.telenav.autopilotcontrol.app.car_data;

import java.util.ArrayList;

/**
 * Created by ninad on 6/14/16.
 */
public class Lane {

    ArrayList<Point> Points = new ArrayList<Point>();

    int type;

    public void addPoint(Point point) {
        Points.add(point);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Point> getPoints() {
        return Points;
    }

    public static Lane leftLane = new Lane();
    public static Lane centerLane = new Lane();
    public static Lane rightLane = new Lane();

    public static Lane lLane() {
        return leftLane;
    }

    public static Lane rLane() {
        return rightLane;
    }

    public static Lane cLane() {
        return centerLane;
    }

    public void init() {

        rightLane.setType(9);
        rightLane.addPoint(new Point(0.00,-1.00,0.00,1.8));
        rightLane.addPoint(new Point(0.0179971,-0.999838,3,1.827));
        rightLane.addPoint(new Point(0.0359767,-0.999353,6,1.908));
        rightLane.addPoint(new Point(0.0539214,-0.998545,9,2.043));
        rightLane.addPoint(new Point(0.0718141,-0.997418,12,2.232));
        rightLane.addPoint(new Point(0.0896377,-0.995974,15,2.475));
        rightLane.addPoint(new Point(0.107376,-0.994219,18,2.772));
        rightLane.addPoint(new Point(0.125012,-0.992155,21,3.123));
        rightLane.addPoint(new Point(0.14253,-0.989791,24,3.528));
        rightLane.addPoint(new Point(0.159915,-0.987131,27,3.987));
        rightLane.addPoint(new Point(0.177153,-0.984183,30,4.5));
        rightLane.addPoint(new Point(0.194229,-0.980956,33,5.067));
        rightLane.addPoint(new Point(0.211131,-0.977458,36,5.688));
        rightLane.addPoint(new Point(0.227845,-0.973697,39,6.363));
        rightLane.addPoint(new Point(0.24436,-0.969684,42,7.092));
        rightLane.addPoint(new Point(0.260666,-0.965429,45,7.875));
        rightLane.addPoint(new Point(0.276751,-0.960942,48,8.712));
        rightLane.addPoint(new Point(0.292607,-0.956233,51,9.603));
        rightLane.addPoint(new Point(0.308226,-0.951313,54,10.548));
        rightLane.addPoint(new Point(0.323599,-0.946194,57,11.547));
        rightLane.addPoint(new Point(0.338719,-0.940887,60,12.6));


        centerLane.addPoint(new Point(0.00,-1.00,0,0.00));
        centerLane.addPoint(new Point(0.0179971,-0.999838,3,0.027));
        centerLane.addPoint(new Point(0.0359767,-0.999353,6,0.108));
        centerLane.addPoint(new Point(0.0539214,-0.998545,9,0.243));
        centerLane.addPoint(new Point(0.0718141,-0.997418,12,0.432));
        centerLane.addPoint(new Point(0.0896377,-0.995974,15,0.675));
        centerLane.addPoint(new Point(0.107376,-0.994219,18,0.972));
        centerLane.addPoint(new Point(0.125012,-0.992155,21,1.323));
        centerLane.addPoint(new Point(0.14253,-0.989791,24,1.728));
        centerLane.addPoint(new Point(0.159915,-0.987131,27,2.187));
        centerLane.addPoint(new Point(0.177153,-0.984183,30,2.7));
        centerLane.addPoint(new Point(0.194229,-0.980956,33,3.267));
        centerLane.addPoint(new Point(0.211131,-0.977458,36,3.888));
        centerLane.addPoint(new Point(0.227845,-0.973697,39,4.563));
        centerLane.addPoint(new Point(0.24436,-0.969684,42,5.292));
        centerLane.addPoint(new Point(0.260666,-0.965429,45,6.075));
        centerLane.addPoint(new Point(0.276751,-0.960942,48,6.912));
        centerLane.addPoint(new Point(0.292607,-0.956233,51,7.803));
        centerLane.addPoint(new Point(0.308226,-0.951313,54,8.748));
        centerLane.addPoint(new Point(0.323599,-0.946194,57,9.747));
        centerLane.addPoint(new Point(0.338719,-0.940887,60,10.8));

//
//        centerLane.addPoint(new Point(0, -0.45508, -0.054606, -0.99851));
//        centerLane.addPoint(new Point(3, -0.61621, -0.052657, -0.99861));
//        centerLane.addPoint(new Point(6, -0.77146, -0.050708, -0.99871));
//        centerLane.addPoint(new Point(9, -0.92085, -0.048758, -0.99881));
//        centerLane.addPoint(new Point(12, -1.0644, -0.046808, -0.9989));
//        centerLane.addPoint(new Point(15, -1.202, -0.044857, -0.99899));
//        centerLane.addPoint(new Point(18, -1.3338, -0.042906, -0.99908));
//        centerLane.addPoint(new Point(21, -1.4629, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(24, -1.5927, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(27, -1.7226, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(30, -1.8524, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(33, -1.9822, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(36, -2.112, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(39, -2.2418, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(42, -2.3716, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(45, -2.5015, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(48, -2.6313, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(51, -2.7611, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(54, -2.8909, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(57, -3.0207, -0.043231, -0.99907));
//        centerLane.addPoint(new Point(60, -3.1505, -0.043231, -0.99907));

    }
}

/*
class Point
{
    double x;
    double y;
    double nx;
    double ny;

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setNx(double nx)
    {
        this.nx = nx;
    }

    public void setNy(double ny)
    {
        this.ny = ny;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getNx()
    {
        return nx;
    }

    public double getNy()
    {
        return ny;
    }
}*/