package com.telenav.sdk_sample.car_data;

/**
 * Created by ninad on 6/14/16.
 */
public class Ego
{

    static double brakePedal;
    static double throttlePedal;
    static double steeringWheel;
    static double[] speed = new double[3];
    static double[] accelerate = new double[3];


    public double getBrakePedal()
    {
        return brakePedal;
    }

    public double getThrottlePedal()
    {
        return throttlePedal;
    }

    public double getSteeringWheel()
    {
        return steeringWheel;
    }

    public double[] getAccelerate()
    {
        return accelerate;
    }

    public double[] getSpeed()
    {
        return speed;
    }

    public void setBrakePedal(double BrakePedal)
    {
        this.brakePedal = BrakePedal;
    }

    public void setThrottlePedal(double ThrottlePedal)
    {
        this.throttlePedal = ThrottlePedal;
    }

    public void setSteeringWheel(double SteeringWheel)
    {
        this.steeringWheel = SteeringWheel;
    }

    public void setAccelerate(double ax, double ay, double az)
    {
        this.accelerate[0] = ax;
        this.accelerate[1] = ay;
        this.accelerate[2] = az;
    }

    public void setSpeed(double vx, double vy, double vz)
    {
        this.speed[0] = vx;
        this.speed[1] = vy;
        this.speed[2] = vz;
    }
}
