package com.telenav.sdk_sample.joglrender;

import com.telenav.sdk_sample.car.data.LaneModel;
import com.telenav.sdk_sample.car.data.Point;

import java.util.ArrayList;

/**
 * Created by ishwarya on 6/14/16.
 */
public class laneFunctions {

    final short finalPoint=100;


    public laneFunctions(){}



    //distance formula
    public float find_dist(float x1, float y1, float x2, float y2) {
        float dist = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return dist;
    }


    /*
     * This finds the x and y co-ordinates of the points which are "d" meters away from x0,y0..
     * points_xy[0] = left x-cordinate "d"  meter away from x0,y0
     * points_xy[1] = left y-cordinate "d"  meter away from x0,y0
     * points_xy[2] = right x-cordinate "d"  meter away from x0,y0
     * points_xy[3] = right y-cordinate "d"  meter away from x0,y0
     */
    public float[] getDistantPoints(float x0, float y0, float nx, float ny, float d)
    {
        float[] points_xy = new float[4];
        if (nx != 0 && ny != 1 && ny != -1)
        {
            points_xy[0] = (float) (x0 + (d / Math.sqrt(1 + ((nx / ny) * (nx / ny)))));
            points_xy[1] = ((nx / ny) * (points_xy[0] - x0) + y0);

            points_xy[2] = (float) (x0 - (d / Math.sqrt(1 + ((nx / ny) * (nx / ny)))));
            points_xy[3] = ((nx / ny) * (points_xy[2] - x0) + y0);
        }

        /* the lines is straight line with no curvature */
        else
        {
            points_xy[0] = x0 + d;
            points_xy[1] = y0;

            points_xy[2] = x0 - d;
            points_xy[3] = y0;
        }

        return points_xy;
    }


    //Grid Production
//    public void gridFormation(ArrayList<Point> centerLane, ArrayList vertexList,ArrayList colorList)
//    {
//
//        short stepSize = 1; //DrawEntity 4 lines to DrawEntity the  stepSize+1 lane points
//
//        for (int gridPointCount = 0; gridPointCount < (centerLane.size() - 1); gridPointCount+=stepSize) //the number of points on the lane
//        {
//            float bottomGridPointX = (float) centerLane.get(gridPointCount).getX();
//            float bottomGridPointY = (float) centerLane.get(gridPointCount).getY();
//            float bottomGridPointNx = (float) centerLane.get(gridPointCount).getNx();
//            float bottomGridPointNy = (float) centerLane.get(gridPointCount).getNy();
//
//            float[] bottomPoint;
//            float[] topPoint;
//
//
//            //horizontal lines
//            if(gridPointCount% 3 == 0)
//            {
//                bottomPoint = getDistantPoints((float) centerLane.get(gridPointCount).getY(), (float) centerLane.get(gridPointCount).getX(), (float) centerLane.get(gridPointCount).getNx(), (float) centerLane.get(gridPointCount).getNy(), 7);
//                pointCalculation(bottomPoint[1], bottomPoint[0], 0, 1, bottomPoint[3], bottomPoint[2], 0, 1, -1, vertexList, colorList);
//            }
//
//            if (gridPointCount +stepSize < centerLane.size())
//            {
//
//                float topX = (float) centerLane.get(gridPointCount +stepSize).getX();
//                float topY = (float) centerLane.get(gridPointCount +stepSize ).getY();
//                float topNx = (float) centerLane.get(gridPointCount +stepSize).getNx();
//                float topNy = (float) centerLane.get(gridPointCount +stepSize ).getNy();
//
//                for (int gridColumnNo = 1; gridColumnNo <=7; gridColumnNo++)
//                {
//                    bottomPoint = getDistantPoints(bottomGridPointY, bottomGridPointX, bottomGridPointNx, bottomGridPointNy, gridColumnNo);
//                    topPoint = getDistantPoints(topY, topX, topNx, topNy, gridColumnNo);
//
//
//                    pointCalculation(bottomPoint[1],bottomPoint[0],bottomGridPointNx,bottomGridPointNy,topPoint[1],topPoint[0],topNx,topNy,-1,vertexList,colorList);
//                    pointCalculation(bottomPoint[3],bottomPoint[2],bottomGridPointNx,bottomGridPointNy,topPoint[3],topPoint[2],topNx,topNy,-1,vertexList,colorList);
//
//                    //extension of lines
//                    if (gridPointCount+ 2*stepSize  > centerLane.size()-1 && centerLane.get(gridPointCount+ stepSize).getX() < 95)
//                    {
//
//                        float lastPointX = (float) centerLane.get(gridPointCount+ stepSize).getX();
//                        float lastPointY = (float) centerLane.get(gridPointCount+ stepSize).getY();
//                        float lastPointNx = (float) centerLane.get(gridPointCount+ stepSize).getNx();
//                        float lastPointNy = (float) centerLane.get(gridPointCount+ stepSize).getNy();
//
//                        while (lastPointX <= finalPoint && gridColumnNo ==7 ) {
//                            short step = 10;
//                            if ((int) lastPointX / 10 <= 10)
//                            {
//                                float[] tempPoints = getDistantPoints(lastPointY, lastPointX, lastPointNx, lastPointNy, 7);
//                                pointCalculation(tempPoints[1], tempPoints[0], 0, 1, tempPoints[3], tempPoints[2], 0, 1, -1, vertexList, colorList);
//                            }
//                            lastPointX = lastPointX+step;
//                            lastPointY = lastPointY+(step*-topNx/topNy);
//
//                        }
//                        //left column extension
//                        pointCalculation(topPoint[1],topPoint[0],0,1,finalPoint,topPoint[0]+((finalPoint -topPoint[1] )*-topNx/topNy),0,1,-1,vertexList,colorList);
//
//                        if(gridColumnNo  ==7  ) //extend center line only once
//                            pointCalculation(topX,topY,0,1,finalPoint,topY+((finalPoint -topX )*-topNx/topNy),0,1,-1,vertexList,colorList);
//                        //extend right columns
//                        pointCalculation(topPoint[3],topPoint[2],0,1,finalPoint,topPoint[2]+((finalPoint -topPoint[3] )*-topNx/topNy),0,1,-1,vertexList,colorList);
//
//                    }
//
//                }
//            }
//
//        }
//
//    }


    /*
     * DrawEntity solid line
     * Mode is used to give color and thickness to the line generated.
     * distance is used to DrawEntity the parallel lane from the lane give
     */
//    public void getSolidLineCoordinates(ArrayList<Point> LanePoints, int mode, ArrayList vertexList, ArrayList colorList,float distance)
//    {
//        short stepSize =1;
//        for (int pointCount = 0; pointCount < LanePoints.size(); pointCount+=stepSize)
//        {
//            float bottomX = (float) LanePoints.get(pointCount).getX();
//            float bottomY = (float) LanePoints.get(pointCount).getY();
//            float bottomNx = (float) LanePoints.get(pointCount).getNx();
//            float bottomNy = (float) LanePoints.get(pointCount).getNy();
//
//            if (pointCount + stepSize < LanePoints.size())
//            {
//                float topX = (float)  LanePoints.get(pointCount +stepSize ).getX();
//                float topY = (float)  LanePoints.get(pointCount +stepSize).getY();
//                float topNx = (float) LanePoints.get(pointCount +stepSize).getNx();
//                float topNy = (float) LanePoints.get(pointCount +stepSize).getNy();
//
//                if(distance != 0.0) //DrawEntity parallel lines
//                {
//                    float[] newBottomPoints = getDistantPoints(bottomY, bottomX, bottomNx, bottomNy, distance);
//                    float[] newTopPoints = getDistantPoints(topY, topX, topNx, topNy, distance);
//                    if(distance > 0)
//                    {
//                        pointCalculation(newBottomPoints[1],newBottomPoints[0],bottomNx,bottomNy,newTopPoints[1],newTopPoints[0],topNx,topNy,mode,vertexList,colorList);
//                    }
//                    else if(distance < 0)
//                    {
//                        pointCalculation(newBottomPoints[1],newBottomPoints[0],bottomNx,bottomNy,newTopPoints[1],newTopPoints[0],topNx,topNy,mode,vertexList,colorList);
//                    }
//                }
//                else
//                    pointCalculation(bottomX,bottomY,bottomNx,bottomNy,topX,topY,topNx,topNy,mode,vertexList,colorList);
//
//            }
//        }
//    }

    public void pointCalculation(float bottomX,float bottomY,float bottomNx, float bottomNy, float topX, float topY, float topNx, float topNy, int mode,ArrayList vertexList,ArrayList vertexList1,  ArrayList colorList){
        if ((bottomNx == 0 || bottomNy == 1 || bottomNy == -1) && (topNx == 0 || topNy == 1 || topNy == -1))
        {

            thickLineCreation(bottomX, bottomY, topX, topY, mode,vertexList,vertexList1, colorList);

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
                thickLineCreation(prevX, prevY, newX, newY, mode,vertexList, vertexList1, colorList);

                prevX = newX;
                prevY = newY;
            }

        }
    }
    /*
    * DrawEntity dashed line
    * Mode is used to give color and thickness to the line generated.
    * distance is used to DrawEntity the parallel lane from the lane give
    */
    public void getDashedLineCoordinates(ArrayList<Point> LanePoints, int mode, ArrayList vertexList, ArrayList vertexList1,  ArrayList colorList)
    {
        short blankSpaceSize = 2;

        for (int pointCount = 0; pointCount < LanePoints.size(); pointCount+=2*blankSpaceSize)
        {
            float bottomX = (float) LanePoints.get(pointCount).getX();
            float bottomY = (float) LanePoints.get(pointCount).getY();
            float bottomNx = (float) LanePoints.get(pointCount).getNx();
            float bottomNy = (float) LanePoints.get(pointCount).getNy();



            if (pointCount + blankSpaceSize < LanePoints.size())
            {
                float topX = (float)  LanePoints.get(pointCount +blankSpaceSize  ).getX();
                float topY = (float)  LanePoints.get(pointCount  +blankSpaceSize ).getY();
                float topNx = (float) LanePoints.get(pointCount  +blankSpaceSize ).getNx();
                float topNy = (float) LanePoints.get(pointCount +blankSpaceSize  ).getNy();

                pointCalculation(bottomX,bottomY,bottomNx,bottomNy,topX,topY,topNx,topNy,mode,vertexList,vertexList1,  colorList);
            }
        }
    }


    //line dividing formula
    public float[] division_line(float x1, float y1, float x2, float y2, float ratio1, float ratio2) {

        float[] new_point = new float[2];
        new_point[0] = (x1 * ratio1 + x2 * ratio2) / (ratio1 + ratio2);
        new_point[1] = (y1 * ratio1 + y2 * ratio2) / (ratio1 + ratio2);

        return new_point;

    }


    //convert arraylist to array
    public float[] convertToArray(ArrayList list) {
        float[] array = new float[list.size()+1];
        for (int i = 0; i < list.size(); i++) {

            array[i] = Float.valueOf(String.valueOf(list.get(i)));

        }
        return array;

    }


    //creating a thick rightLane
    public void thickLineCreation(float x1, float y1, float x2, float y2, int mode, ArrayList laneVertexList, ArrayList laneVertexList1, ArrayList LaneColorLane) {
        float thickness;
        if (mode > 9)
            thickness = 0.05f;
        else
            thickness = 0.5f;
        if (mode == -1)
        {
            laneVertexList.add(x1);
            laneVertexList.add(y1);
            //  laneVertexList.add(1);

            laneVertexList.add(x2);
            laneVertexList.add(y2);
            // laneVertexList.add(1);
        }
        else if(mode == 1 )
        {
            laneVertexList.add(x1);  //x1
            laneVertexList.add(y1 - 3.6);   //y1-0.5
            // laneVertexList.add(1);

            laneVertexList.add(x1);  //x1
            laneVertexList.add(y1 + 3.6);   //y1+0.5
            // laneVertexList.add(1);

            laneVertexList.add(x2);  //x1
            laneVertexList.add(y2 + 3.6); //y2+0.5
            // laneVertexList.add(1);

            laneVertexList.add(x2);//x1
            laneVertexList.add(y2 + 3.6); //y2+0.5
            // laneVertexList.add(1);

            laneVertexList.add(x2); //x1
            laneVertexList.add(y2 - 3.6); //y2-0.5
            //  laneVertexList.add(1);

            laneVertexList.add(x1); //x1
            laneVertexList.add(y1 - 3.6); //y1-0.5


            laneVertexList1.add(x1);  //x1
            laneVertexList1.add(y1 - thickness);   //y1-0.5
            // laneVertexList.add(1);

            laneVertexList1.add(x1);  //x1
            laneVertexList1.add(y1 + thickness);   //y1+0.5
            // laneVertexList.add(1);

            laneVertexList1.add(x2);  //x1
            laneVertexList1.add(y2 + thickness); //y2+0.5
            // laneVertexList.add(1);

            laneVertexList1.add(x2);//x1
            laneVertexList1.add(y2 + thickness); //y2+0.5
            // laneVertexList.add(1);

            laneVertexList1.add(x2); //x1
            laneVertexList1.add(y2 - thickness); //y2-0.5
            //  laneVertexList.add(1);

            laneVertexList1.add(x1); //x1
            laneVertexList1.add(y1 - thickness); //y1-0.5
        }

            //  laneVertexList.add(1);


        if (mode == -1)//grid
        {
            for (int lanepoint = 0; lanepoint < 4; lanepoint++)
            {
                LaneColorLane.add(0);
                LaneColorLane.add(0);
                LaneColorLane.add(0);
                LaneColorLane.add(0.5f);
            }
        }
        else
        {
            for (int lanepoint = 0; lanepoint < 6; lanepoint++)
            {
                if (mode == 10)
                {
                    LaneColorLane.add(0);
                    LaneColorLane.add(1);
                    LaneColorLane.add(0);
                    LaneColorLane.add(0.5f);
                }
                else if (mode == LaneModel.leftLaneInvisibleType || mode == LaneModel.leftLeftLaneInvisibleType)
                {
                    LaneColorLane.add(0.5);
                    LaneColorLane.add(0.5);
                    LaneColorLane.add(0.5);
                    LaneColorLane.add(0.5f);
                }
                else if (mode == 9)
                {
                    LaneColorLane.add(1);
                    LaneColorLane.add(0);
                    LaneColorLane.add(0);
                    LaneColorLane.add(0.5f);
                }
                else
                {
                    LaneColorLane.add(1);
                    LaneColorLane.add(1);
                    LaneColorLane.add(1);
                    LaneColorLane.add(1f);
                }
            }
        }
    }
}
