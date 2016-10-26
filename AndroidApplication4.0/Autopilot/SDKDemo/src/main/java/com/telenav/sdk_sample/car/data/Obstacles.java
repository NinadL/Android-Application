package com.telenav.sdk_sample.car.data;

/**
 * Created by ninad on 6/14/16.
 */
public class Obstacles {

    double[] position = new double[3];
    double[] velocity = new double[3];
    double[] orientation = new double[4];
    double[] size = new double[3];
    boolean isColliding;
    int type;

    public void setPosition(double x, double y, double z)
    {
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = z;
    }

    public void setVelocity(double vx, double vy, double vz)
    {
        this.velocity[0] = vx;
        this.velocity[1] = vy;
        this.velocity[2] = vz;
    }

    public void setOrientation(double qx, double qy, double qz, double qw)
    {
        this.orientation[0] = qx;
        this.orientation[1] = qy;
        this.orientation[2] = qz;
        this.orientation[3] = qw;
    }

    public void setSize(double x, double y, double z)
    {
        this.size[0] = x;
        this.size[1] = y;
        this.size[2] = z;
    }

    public void setIsColliding(boolean isColliding)
    {
        this.isColliding = isColliding;
    }
    public void setType(int Type)
    {
        this.type = Type;
    }


    public double[] getPosition()
    {
        return position;
    }


    public double[] getVelocity()
    {
        return velocity;
    }

    public double[] getOrientation()
    {
        return orientation;
    }

    public double[] getSize()
    {
        return size;
    }

    public boolean getIsColliding()
    {
        return isColliding;
    }

    public int getType()
    {
        return type;
    }

}
