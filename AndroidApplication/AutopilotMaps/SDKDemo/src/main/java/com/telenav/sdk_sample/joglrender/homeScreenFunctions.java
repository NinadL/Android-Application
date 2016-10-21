package com.telenav.sdk_sample.joglrender;

import java.util.ArrayList;

/**
 * Created by ishwarya on 7/20/16.
 */
public class homeScreenFunctions {

    int magnificationFactor =5; //to increase or decrease the size 1 being the original distance and 5 being used as distance/maginificationFactor

    /*
    * if visible = true, dist = max distance
    * else dist = minimum distance
    * orientation is the qw of quanternion to position the range of sensor
    */
    public  void getSensorCoverageBuffer(double angle, float centerx, float centery, float centerz, float dist, boolean visible, ArrayList vertexList, ArrayList colorList,float orientation)
    {
        int size = 0;
        float smallerAngleValue;
        float largerAngleValue;

        //setting the first point
        if(orientation > (float) angle + orientation) // we start drawing in clockwise direction
        {

            largerAngleValue = orientation;
            smallerAngleValue= (float) angle + orientation;

        }
        else  // we start drawing in anticlockwise direction
        {
            smallerAngleValue = orientation;
            largerAngleValue= (float) angle + orientation;
        }



        for (; smallerAngleValue < largerAngleValue; smallerAngleValue += 0.1) {
            if (smallerAngleValue + 0.1 < largerAngleValue) {

                //the filled area
                vertexList.add(centerx);
                vertexList.add(centerz);
                vertexList.add(centery);

                vertexList.add( (float) ( centerx - ( (dist / magnificationFactor) * Math.cos(smallerAngleValue) ) ));
                vertexList.add(centerz);
                vertexList.add((float) ((centery + ((dist / magnificationFactor) * Math.sin(smallerAngleValue)))));


                vertexList.add((float) ( (centerx - ((dist / magnificationFactor) * Math.cos(smallerAngleValue + 0.1)))));
                vertexList.add(centerz);
                vertexList.add((float) ( (centery + ((dist / magnificationFactor) * Math.sin(smallerAngleValue + 0.1)))));


                size+=9;

            }
        }

        //to complete the arc till largeAngleValue
        vertexList.add(centerx);
        vertexList.add(centerz);
        vertexList.add(centery);


        vertexList.add((float) ((centerx - ((dist / magnificationFactor) * Math.cos(smallerAngleValue-0.1)))));
        vertexList.add(centerz);
        vertexList.add((float) ((centery + ((dist / magnificationFactor) * Math.sin(smallerAngleValue-0.1)))));



        vertexList.add((float) ((centerx - ((dist / magnificationFactor) * Math.cos(largerAngleValue)))));
        vertexList.add(centerz);
        vertexList.add((float) ((centery + ((dist / magnificationFactor) * Math.sin(largerAngleValue)))));


        size+=9;



        for (int pointsCount= 0; pointsCount < (size/3); pointsCount++) {
            if(visible){ //green
                colorList.add(0f);
                colorList.add(1f);
                colorList.add(0f);
                colorList.add(0.3f);
            }
            else {
                colorList.add(1f);
                colorList.add(0f);
                colorList.add(0f);
                colorList.add(0.3f);
            }
        }

    }
}
