package com.telenav.sdk_sample.joglrender;

import com.telenav.sdk_sample.car.data.Lane;
import com.telenav.sdk_sample.car.data.Point;

import java.util.ArrayList;

public class laneFunctions
{

    //Constants
    final short INVISIBLE_LANE = 3;
    final short SOLID_LANE = 2;
    final float SIDE_SIZE = 3.6f;
    final float CENTRE_SIZE = 1.8f;
    final short RIGHT_LANE = 0;
    final short LEFT_LANE = 1;

    public laneFunctions(){}


    /*
      This method is used to calculate the vertices,
      and assign colors to the vertices.
      The calculated vertices and colors are the stored in an arraylist,
      which would be used for rendering.

      lanePoints : the original lane points as given by motion planning.
      type : type of line to be drawn (solid/dashed)
      boundaryPoints, centreLanePoints, sideLanePoints: used to store the vertices
      boundaryColor,centreLaneColor, sideLaneColor: used to store the color of all the vertices

     */
    public void generateVertexAndColorPoints(ArrayList<Point> lanePoints, int type, int laneSide,
                                             ArrayList boundaryPoints, ArrayList centreLanePoints, ArrayList sideLanePoints,
                                             ArrayList boundaryColor, ArrayList centreLaneColor, ArrayList sideLaneColor)
    {
        short stepSize =1;

        for (int pointCount = 0; pointCount < lanePoints.size(); pointCount+=stepSize)
        {
            float bottomX = (float) lanePoints.get(pointCount).getX();
            float bottomY = (float) lanePoints.get(pointCount).getY();
            float bottomNx = (float) lanePoints.get(pointCount).getNx();
            float bottomNy = (float) lanePoints.get(pointCount).getNy();

            if (pointCount + stepSize < lanePoints.size())
            {
                float topX = (float)  lanePoints.get(pointCount +stepSize ).getX();
                float topY = (float)  lanePoints.get(pointCount +stepSize).getY();
                float topNx = (float) lanePoints.get(pointCount +stepSize).getNx();
                float topNy = (float) lanePoints.get(pointCount +stepSize).getNy();

                if ((bottomNx == 0 || bottomNy == 1 || bottomNy == -1) &&
                    (topNx == 0 || topNy == 1 || topNy == -1))
                {

                    createLine(bottomX, bottomY, topX, topY, type, laneSide,
                            boundaryPoints, centreLanePoints, sideLanePoints,
                            boundaryColor, centreLaneColor, sideLaneColor);

                }
                //If the curvature is more, then generate in-between points
                else
                {
                    float cx, cy;

                    if(bottomNy != 1 && bottomNy != -1 && bottomNx != 0 )
                    {
                        cx = topX;
                        cy = ((-1* bottomNx / bottomNy) * (topX - bottomX)) + bottomY;
                    } else {
                        cx = bottomX;
                        cy = ((-1*topNx / topNy) * (bottomX - topX)) + topY;
                    }


                    float stepIncrease = 0.333333f;
                    float prevX = bottomX;
                    float prevY = bottomY;

                    for (float t = stepIncrease; t <= 1; t += stepIncrease)
                    {
                        float newX = ((1 - t) * bottomX * (1 - t)) + (2 * t * (1 - t) * cx) + (t * t * topX);
                        float newY = ((1 - t) * bottomY * (1 - t)) + (2 * t * (1 - t) * cy) + (t * t * topY);

                        createLine(prevX, prevY, newX, newY, type, laneSide,
                                boundaryPoints, centreLanePoints, sideLanePoints,
                                boundaryColor, centreLaneColor, sideLaneColor);

                        prevX = newX;
                        prevY = newY;
                    }
                }
            }
        }
    }

    /*
    * DrawEntity dashed line
    * Mode is used to give color and thickness to the line generated.
    * distance is used to DrawEntity the parallel lane from the lane give
    */
    public void getDashedLineCoordinates(ArrayList<Point> lanePoints, int mode, int laneSide, ArrayList boundaryPoints, ArrayList boundaryColor)
    {

        int blankSpaceSize = 1;

        for (int pointCount = 0; pointCount < lanePoints.size(); pointCount+=2*blankSpaceSize)
        {
            float bottomX = (float) lanePoints.get(pointCount).getX();
            float bottomY = (float) lanePoints.get(pointCount).getY();
            float bottomNx = (float) lanePoints.get(pointCount).getNx();
            float bottomNy = (float) lanePoints.get(pointCount).getNy();



            if (pointCount + blankSpaceSize < lanePoints.size())
            {
                float topX = (float)  lanePoints.get(pointCount + blankSpaceSize ).getX();
                float topY = (float)  lanePoints.get(pointCount + blankSpaceSize ).getY();
                float topNx = (float) lanePoints.get(pointCount + blankSpaceSize ).getNx();
                float topNy = (float) lanePoints.get(pointCount + blankSpaceSize ).getNy();

                if ((bottomNx == 0 || bottomNy == 1 || bottomNy == -1) && (topNx == 0 || topNy == 1 || topNy == -1))
                {

                    createLine(bottomX, bottomY, topX, topY, mode, laneSide, boundaryPoints,null, null, boundaryColor, null, null);

                }
                else {
                    float cx, cy;

                    if(bottomNy != 1 && bottomNy != -1 && bottomNx != 0 )
                    {
                        cx = topX;
                        cy = ((-1* bottomNx / bottomNy) * (topX - bottomX)) + bottomY;
                    } else {
                        cx = bottomX;
                        cy = ((-1*topNx / topNy) * (bottomX - topX)) + topY;
                    }


                    float stepIncrease = 0.333333f;
                    float prevX = bottomX;
                    float prevY = bottomY;

                    for (float t = stepIncrease; t <= 1; t += stepIncrease) {
                        float newX;
                        //newX = bottomX*(1 - t)+t*topX;
                        newX = ((1 - t) * bottomX * (1 - t)) + (2 * t * (1 - t) * cx) + (t * t * topX);
                        float newY;
                        //newY = bottomY*(1 - t)+t*topY;
                        newY = ((1 - t) * bottomY * (1 - t)) + (2 * t * (1 - t) * cy) + (t * t * topY);
                        createLine(prevX, prevY, newX, newY, mode,laneSide, boundaryPoints, null, null, boundaryColor, null, null);

                        prevX = newX;
                        prevY = newY;
                    }

                }

            }
        }
    }

    /*
    This method is used to fill the arraylists with the vertices and appropriate colors.
    SLP: sideLanePoints
    CLP: centreLanePoints

        |SLP    |CLP|CLP|    SLP|
        |       |   |   |       |
        |       |   |   |       |
        |       |   |   |       |
        |       |   |   |       |
        |       |   |   |       |
        |       |   |   |       |
        |Left bndry |Right bndry|

     */
    public void createLine(float x1, float y1, float x2, float y2, int mode, int laneSide,
                           ArrayList boundaryPoints, ArrayList centreLanePoints, ArrayList sideLanePoints,
                           ArrayList boundaryColor, ArrayList centreLaneColor, ArrayList sideLaneColor)
    {
        float thickness;

        //Set the thickness
        if(mode == INVISIBLE_LANE)
        {
            thickness = 0.05f;
        }
        else
        {
            thickness = 0.1f;
        }

        /////////////////////////
        //  Vertex Processing  //
        /////////////////////////

        //Add the boundary points

        if(mode != 4) {
            boundaryPoints.add(x1);
            boundaryPoints.add(y1 - thickness);

            boundaryPoints.add(x1);
            boundaryPoints.add(y1 + thickness);

            boundaryPoints.add(x2);
            boundaryPoints.add(y2 + thickness);

            boundaryPoints.add(x2);
            boundaryPoints.add(y2 + thickness);

            boundaryPoints.add(x2);
            boundaryPoints.add(y2 - thickness);

            boundaryPoints.add(x1);
            boundaryPoints.add(y1 - thickness);
        }


        if(mode != 1)
        {

            //If we are rendering the right lane
            if (laneSide == RIGHT_LANE)
            {
                //We need to add the vertices which are to
                // the right of this lane to the sideLanePoints arraylist.
                sideLanePoints.add(x1);
                sideLanePoints.add(y1);

                sideLanePoints.add(x1);
                sideLanePoints.add(y1 + SIDE_SIZE);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2 + SIDE_SIZE);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2 + SIDE_SIZE);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2);

                sideLanePoints.add(x1);
                sideLanePoints.add(y1);

                //We need to add the vertices which are to
                // the left of this lane to the centreLanePoints arraylist.

                centreLanePoints.add(x1);
                centreLanePoints.add(y1 - CENTRE_SIZE);

                centreLanePoints.add(x1);
                centreLanePoints.add(y1);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2 - CENTRE_SIZE);

                centreLanePoints.add(x1);
                centreLanePoints.add(y1 - CENTRE_SIZE);
            }
            //If we are rendering the left lane
            else {
                //We need to add the vertices which are to
                // the left of this lane to the sideLanePoints arraylist.

                sideLanePoints.add(x1);
                sideLanePoints.add(y1 - SIDE_SIZE);

                sideLanePoints.add(x1);
                sideLanePoints.add(y1);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2);

                sideLanePoints.add(x2);
                sideLanePoints.add(y2 - SIDE_SIZE);

                sideLanePoints.add(x1);
                sideLanePoints.add(y1 - SIDE_SIZE);

                //We need to add the vertices which are to
                // the right of this lane to the centreLanePoints arraylist.

                centreLanePoints.add(x1);
                centreLanePoints.add(y1);

                centreLanePoints.add(x1);
                centreLanePoints.add(y1 + CENTRE_SIZE);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2 + CENTRE_SIZE);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2 + CENTRE_SIZE);

                centreLanePoints.add(x2);
                centreLanePoints.add(y2);

                centreLanePoints.add(x1);
                centreLanePoints.add(y1);
            }
        }

        /////////////////////////
        //  Color Processing   //
        /////////////////////////

        for (int i = 0; i < 6; i++)
        {
            //If dash line is present, we represent it by gray color
            //The centre lane color remains same always
            if (mode == 1 || mode == 2)
            {
                boundaryColor.add(1);
                boundaryColor.add(1);
                boundaryColor.add(1);
                boundaryColor.add(1f);
            }

            if(mode == 3)
            {
                boundaryColor.add(0.5f);
                boundaryColor.add(0.5f);
                boundaryColor.add(0.5f);
                boundaryColor.add(1f);
            }
            //If solid line is present, we represent it by white color
            if(mode != 1)
            {
                sideLaneColor.add(0.25f);
                sideLaneColor.add(0.25f);
                sideLaneColor.add(0.25f);
                sideLaneColor.add(1f);
                /////////////////////////////////
//                centreLaneColor.add(0.25f);
//                centreLaneColor.add(0.25f);
//                centreLaneColor.add(0.25f);
//                centreLaneColor.add(1f);
            }
        }

        //We we are rendering right lane,
        // blending has to be done from left to right
        if(mode != 1) {
            if (laneSide == LEFT_LANE) {
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);
                /////////////////////////////////
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
            }
            //We we are rendering left lane,
            // blending has to be done from right to left
            else {
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(0.25f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);

                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(0.025f);
                centreLaneColor.add(1f);
                ///////////////////////////////
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(0.25f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
//
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(0.025f);
//                sideLaneColor.add(1f);
            }
        }
    }

    //convert arraylist to array
    public float[] convertToArray(ArrayList list) {
        float[] array = new float[list.size()+1];
        for (int i = 0; i < list.size(); i++) {

            array[i] = Float.valueOf(String.valueOf(list.get(i)));

        }
        return array;
    }
}