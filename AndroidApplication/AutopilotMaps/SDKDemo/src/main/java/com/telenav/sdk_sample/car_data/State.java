package com.telenav.sdk_sample.car_data;

/**
 * Created by ninad on 6/14/16.
 */
public class State
{
    static int mode;
    static int profile;

    public void setMode(int Mode)
    {
        this.mode = Mode;
    }

    public int getMode()
    {
        return mode;
    }

    public void setProfile(int Profile)
    {
        this.profile = Profile;
    }

    public int getProfile()
    {
        return profile;
    }
}
