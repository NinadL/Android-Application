package com.telenav.autopilotcontrol.app.car_data;

/**
 * Created by ishwarya on 7/21/16.
 */
public class LengthSpan extends Sensor{
    double lengthMax;
    double lengthMin;

    void setlengthH(double lengthH)
    {
        this.lengthMax = lengthH;
    }

    void setlengthL(double lengthL)
    {
        this.lengthMin = lengthL;
    }

    public double getLengthMax()
    {
        return lengthMax;
    }

    public double getLengthMin()
    {
        return lengthMin;
    }
}
