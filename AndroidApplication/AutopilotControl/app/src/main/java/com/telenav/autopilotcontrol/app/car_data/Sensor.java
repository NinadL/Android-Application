package com.telenav.autopilotcontrol.app.car_data;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ninad on 6/14/16.
 */
public class Sensor {

    int id;
    String name;
    int status;
    ArrayList<FieldOfView> fieldOfViews = new ArrayList<FieldOfView>();
    double[] position = new double[3];
    double[] orientation = new double[4];


    public void setId(int Id)
    {
        this.id = Id;
    }

    public void setName(String Name)
    {
        this.name = Name;
    }

    public void setStatus(int Status)
    {
        this.status = Status;
    }

    public void addFieldOfView(FieldOfView fieldOfViewObject)
    {
        Log.d("sensor data12345 set",fieldOfViews.toString());
        fieldOfViews.add(fieldOfViewObject);
    }

    public void setPosition(double x, double y, double z)
    {
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = z;
    }

    public void setOrientation(double w, double x, double y, double z)
    {
        this.orientation[0] = w;
        this.orientation[1] = x;
        this.orientation[2] = y;
        this.orientation[3] = z;
    }
    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getStatus()
    {
        return status;
    }

    public ArrayList<FieldOfView> getFieldOfViews()
    {
        ArrayList fieldOfViewsDeepCopy = new ArrayList(fieldOfViews);
        Log.d("sensor data12345",fieldOfViews.toString());
        return fieldOfViewsDeepCopy;
    }

    public double[] getPosition()
    {
        return position;
    }

    public double[] getOrientation()
    {
        return orientation;
    }
}

