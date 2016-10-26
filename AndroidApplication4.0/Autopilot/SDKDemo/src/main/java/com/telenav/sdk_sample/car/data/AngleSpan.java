package com.telenav.sdk_sample.car.data;


/**
 * Created by ishwarya on 7/21/16.
 */
public class AngleSpan extends Sensor {

    double angleMax;
    double angleMin;

    void setAngleMax(double angleMax)
    {
        this.angleMax = angleMax;
    }

    void setAngleMin(double angleMin)
    {
        this.angleMin = angleMin;
    }

    public double getAngleMax()
    {
        return angleMax;
    }

    public double getAngleMin()
    {
        return angleMin;
    }
}
