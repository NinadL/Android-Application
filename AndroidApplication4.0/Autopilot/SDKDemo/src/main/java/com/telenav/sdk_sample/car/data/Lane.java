package com.telenav.sdk_sample.car.data;

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

    //Init method
    public void init() {

        rightLane.setType(2);
        rightLane.addPoint(new Point(0, 0 ,0, 1.8));
        rightLane.addPoint(new Point(0, 0 ,20, 1.8));
        rightLane.addPoint(new Point(0, 0 ,40, 1.8));
        rightLane.addPoint(new Point(0, 0 ,60, 1.8));
        rightLane.addPoint(new Point(0, 0 ,80, 1.8));
        rightLane.addPoint(new Point(0, 0 ,100, 1.8));
        rightLane.addPoint(new Point(0, 0 ,120, 1.8));
        rightLane.addPoint(new Point(0, 0 ,140, 1.8));
        rightLane.addPoint(new Point(0, 0 ,160, 1.8));
        rightLane.addPoint(new Point(0, 0 ,180, 1.8));
        rightLane.addPoint(new Point(0, 0 ,200, 1.8));

        centerLane.setType(9);
        centerLane.addPoint(new Point(0, 0 ,0, 0));
        centerLane.addPoint(new Point(0, 0 ,20, 0));
        centerLane.addPoint(new Point(0, 0 ,40, 0));
        centerLane.addPoint(new Point(0, 0 ,60, 0));
        centerLane.addPoint(new Point(0, 0 ,80, 0));
        centerLane.addPoint(new Point(0, 0 ,100, 0));
        centerLane.addPoint(new Point(0, 0 ,120, 0));
        centerLane.addPoint(new Point(0, 0 ,140, 0));
        centerLane.addPoint(new Point(0, 0 ,160, 0));
        centerLane.addPoint(new Point(0, 0 ,180, 0));
        centerLane.addPoint(new Point(0, 0 ,200, 0));

        leftLane.setType(2);
        leftLane.addPoint(new Point(0, 0 ,0, -1.8));
        leftLane.addPoint(new Point(0, 0 ,20, -1.8));
        leftLane.addPoint(new Point(0, 0 ,40, -1.8));
        leftLane.addPoint(new Point(0, 0 ,60, -1.8));
        leftLane.addPoint(new Point(0, 0 ,80, -1.8));
        leftLane.addPoint(new Point(0, 0 ,100, -1.8));
        leftLane.addPoint(new Point(0, 0 ,120, -1.8));
        leftLane.addPoint(new Point(0, 0 ,140, -1.8));
        leftLane.addPoint(new Point(0, 0 ,160, -1.8));
        leftLane.addPoint(new Point(0, 0 ,180, -1.8));
        leftLane.addPoint(new Point(0, 0 ,200, -1.8));
    }
}
