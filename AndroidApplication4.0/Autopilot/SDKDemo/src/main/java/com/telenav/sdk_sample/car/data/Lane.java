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
        centerLane.setType(9);
        leftLane.setType(2);

        double nx = 0;
        double ny = 0;
        double x = 0;
        double ry = 1.8;
        double ly = -1.8;
        double cy = 0;

        for(int i = 0; i < 20 ; i++)
        {
            Point point = new Point();
            point.setNy(ny);
            point.setNx(nx);
            point.setX(x);
            point.setY(ry);

            x = x + 5;
            rightLane.addPoint(point);
        }

        x = 0;
        for(int i = 0; i < 20 ; i++)
        {
            Point point = new Point();
            point.setNy(ny);
            point.setNx(nx);
            point.setX(x);
            point.setY(ly);

            x = x + 5;

            leftLane.addPoint(point);
        }

        x = 0;
        for(int i = 0; i < 20 ; i++)
        {
            Point point = new Point();
            point.setNy(ny);
            point.setNx(nx);
            point.setX(x);
            point.setY(cy);

            x = x + 5;

            centerLane.addPoint(point);
        }
    }
}
