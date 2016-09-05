package com.telenav.autopilotcontrol.app.car_data;

/**
 * Created by ishwarya on 7/21/16.
 */
public class FieldOfView extends Sensor {
    AngleSpan angleSpan = new AngleSpan();
    LengthSpan lengthSpan = new LengthSpan();

    FieldOfView(AngleSpan angleSpan, LengthSpan lengthSpan)
    {
        this.angleSpan = angleSpan;
        this.lengthSpan = lengthSpan;
    }

    public AngleSpan getAngleSpan()
    {
        return this.angleSpan;
    }

    public LengthSpan getLengthSpan()
    {
        return this.lengthSpan;
    }
}
