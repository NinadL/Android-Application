package com.telenav.autopilotcontrol.app.joglrender;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telenav.autopilotcontrol.app.car_data.DataParser;
import com.telenav.autopilotcontrol.app.car_data.Obstacles;
import com.telenav.autopilotcontrol.app.car_data.setStatusClass;
import com.telenav.autopilotcontrol.app.ui.AnimateFragment;
import com.telenav.autopilotcontrol.app.ui.MainActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by ishwarya on 7/20/16.
 */
public class draw {
    float[] mModelMatrixLane;
    float[] mProjectionMatrixLane;
    float[] mViewMatrixLane;
    float[] mMVPMatrixLane;

    private  int mBytesPerFloat = 4;
    private  int mPositionDataSize = 3;
    private  int mStrideBytes = 12;
    private  int mColorDataSize = 4;
    private  int mColorLaneSize = 3;
    private  int mNormalDataSize = 3;
    private  int mTextureCoordinateDataSize = 2;

    
    float[] mModelMatrix;
    float[] mProjectionMatrix;
    float[] mViewMatrix;
    private float[] mMVPMatrix;

    /******    texture handles     ******/
    private int mPositionHandle;
    private int mProgramHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mTextureCoordinateHandle;
    private int mUseTextureHandle;
    private int mMVMatrixHandle;
    
    Context Context;

    int mTextureUniformHandle;
    int mTextureDataHandle;
    public String errorMsg="";
    // int mPointProgramHandle;


    static private FloatBuffer laneVertexBuffer;
    static private FloatBuffer laneColorBuffer;
    static  private int laneSize;
    static boolean flag1 = false;

    static boolean flag2 = false;
    static private modelBuffers gridObj;


    static boolean flag4=false;
    static modelBuffers vehicleObj;


    static private FloatBuffer sensorVertexBuffer;
    static private FloatBuffer sensorColorBuffer;
    static private int sensorSize;
    static boolean flag3;
    private  DataParser dp = new DataParser();
    private setStatusClass statusObject = new setStatusClass();

    private short minArrayListSize = 3;
    private short xYVertexSize = 2;
    private short xYZVertexSize = 3;

    int redColorHandle;
    int yellowColorHandle;
    private static  RelativeLayout mainActivityRelativeLayout;
    public draw(){}

    public draw(RelativeLayout mainActivityRelativeLayout, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix) {

        this.mainActivityRelativeLayout = mainActivityRelativeLayout;
        mMVPMatrixLane =  mMVPMatrix;
        mViewMatrixLane = mViewMatrix;
        mModelMatrixLane = mModelMatrix;
        mProjectionMatrixLane = mProjectionMatrix;

    }


    public void setTextureValues(int mPositionHandle, int mProgramHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix, int mMVPMatrixHandle, int mColorHandle, int mNormalHandle, int mTextureCoordinateHandle, Context c, int mMVMatrixHandle, int mTextureDataHandle,int redColorHandle, int yellowColorHandle) {

        this.mPositionHandle = mPositionHandle;
        this.mProgramHandle = mProgramHandle;
        this.mMVPMatrix = mMVPMatrix;
        this.mViewMatrix = mViewMatrix;
        this.mModelMatrix= mModelMatrix;
        this.mProjectionMatrix = mProjectionMatrix;
        this.mMVPMatrixHandle = mMVPMatrixHandle;
        this.mColorHandle = mColorHandle;
        this.mNormalHandle = mNormalHandle;
        this.mTextureCoordinateHandle = mTextureCoordinateHandle;
        this.Context = c;
        this.mMVMatrixHandle = mMVMatrixHandle;
        this.mTextureDataHandle = mTextureDataHandle;
        this.redColorHandle = redColorHandle;
        this.yellowColorHandle = yellowColorHandle;
    }

    public void setBufferLane(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int Size, boolean flag){
        laneVertexBuffer = vertexBuffer;
        laneColorBuffer = colorBuffer;
        laneSize = Size;
        flag1 = flag;

    }

    public void setBufferGrid(modelBuffers object, boolean flag){
        gridObj = object;
        flag2 = flag;
    }

    public void setVehicleStatus(modelBuffers object, boolean flag) {
        Log.d("asyncTask","setVehicleStatus");
        vehicleObj = object;
        flag4 = flag;
    }

    public void setBufferSensors(FloatBuffer vertexList,FloatBuffer colorList, int Size,boolean flag) {
        Log.d("sentsensor", "flag3" );
        sensorVertexBuffer = vertexList;
        sensorColorBuffer = colorList;
        sensorSize = Size;
        flag3 = flag;
    }


    public void drawToScreen()
    {
//            if (MainActivity.screenView == MainActivity.startScreen && MainActivity.autoswap == false || MainActivity.autoswap == true && !statusObject.gettingMapData()) {
//
//                GLES20.glEnable(GLES20.GL_BLEND);
//                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//
//                if (flag4) {
//                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), 3, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrix, mViewMatrix, mModelMatrix, redColorHandle);
//                }
//
//                if (flag3) {
//                    // Matrix.rotateM(mModelMatrixLane, 0, 90, 1, 0, 0);
//                    Log.d("sentsensor", "flag3" + " " + sensorSize);
//                    drawTexture(sensorVertexBuffer, sensorColorBuffer, null, null, sensorSize, 3, 0.0f, GLES20.GL_TRIANGLES, mProjectionMatrix, mViewMatrix, mModelMatrix, -2);
//                }
//            } else
            //*/
            {

                GLES20.glDisable(GLES20.GL_BLEND);

                if (flag2 && gridObj.getSize() > minArrayListSize) {
                    drawTexture(gridObj.getVertexBuffer(), gridObj.getColorBuffer(), null, null, gridObj.getSize(), xYVertexSize, 0.0f, GLES20.GL_LINES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, -2);
                }

                if (flag1 && laneSize > minArrayListSize) {
                    drawTexture(laneVertexBuffer, laneColorBuffer, null, null, laneSize, xYVertexSize, 0.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, -2);
                }
                if (flag4)
                {


                    short xTranslateValue = 6;
                    short zTranslateValue = 1;
                    float yMultiplicationFactor = 3.5f;

                    Matrix.scaleM(mModelMatrixLane, 0, 1f, 0.4f, 0.6f); //originally 0, 0.5, 0.5, 0.5
                    Matrix.translateM(mModelMatrixLane, 0, xTranslateValue, 0, 0);
//
//                    //make the car hood to face forward
                    Matrix.rotateM(mModelMatrixLane, 0, -180, 0, 0, 1);
//
//                    //make the car tires to touch the base else it is verticle instead of horizontal
                    Matrix.rotateM(mModelMatrixLane, 0, -90, 1, 0, 0);
                    Matrix.rotateM(mModelMatrixLane, 0, 0, 0, 1, 0);
                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);
                    Matrix.translateM(mModelMatrixLane, 0, -xTranslateValue, 0, 0);

////                    //obstacles
////
//                    Matrix.translateM(mModelMatrixLane, 0,-13, 0,  -10 + 3.6f );
//                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);
//
//                    Matrix.translateM(mModelMatrixLane, 0,+13, 0, 10 -3.6f );
//
//                    Matrix.translateM(mModelMatrixLane, 0,-23, 0, 5 + 1.8f );
//                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);
//
//                    Matrix.translateM(mModelMatrixLane, 0,-13, 0, -5 - 1.8f );
//                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);
//
//                    Matrix.translateM(mModelMatrixLane, 0,-13, 0, -5 -1.8f );
//                    drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);

                    if (statusObject.gettingPerceptionData() && dp.getObstaclesObject()!= null)

                    {
                        ArrayList<Obstacles> obstacleList = dp.getObstaclesObject();
                        //Log.d("entered", "he " + obstacleList.size());
                        for (int i = 0; i < obstacleList.size(); i++)
                        {
                            if (obstacleList.get(i) != null && isObstacleInAdjacentLane(obstacleList.get(i)))
                            {
                                float yCoordinate = (float)obstacleList.get(i).getPosition()[1];
                                //This scaling factor is for scaling the y coordinate of the car as per the value of y
                                float scalingIndex = 2.77f;

                                if(yCoordinate > 0.0f)
                                {
                                    scalingIndex = scalingIndex*-1;
                                }
                                //This is the actual scaling that needs to be done
                                float scalingFactor = yCoordinate * scalingIndex;

                                if(yCoordinate < 0.0f)
                                {
                                    scalingFactor = scalingFactor * -1;
                                }
                                Matrix.rotateM(mModelMatrixLane, 0, (float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2) - 180, 0, 1, 0);
                                Matrix.translateM(mModelMatrixLane,0,-(float) (obstacleList.get(i).getPosition()[0]),0,scalingFactor + (float) (obstacleList.get(i).getPosition()[1]));
                                drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, yellowColorHandle);
                                Matrix.translateM(mModelMatrixLane,0,(float) (obstacleList.get(i).getPosition()[0]),0,-scalingFactor - (float) (obstacleList.get(i).getPosition()[1]));
                                Matrix.rotateM(mModelMatrixLane, 0, -((float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2) - 180), 0, 1, 0);
                            }
                        }
                    }

                }



//                if (statusObject.gettingPerceptionData() && dp.getObstaclesObject()!= null)
//                {
//                    short xTranslateValue = -45;
//                    short zTranslateValue = 1;
//                    float yMultiplicationFactor = 3.5f;
//                    ArrayList<Obstacles> obstacleList = dp.getObstaclesObject();
//                    Log.d("entered", "he " + obstacleList.size());
//                    for (int i = 0; i < obstacleList.size(); i++)
//                    {
//                        if (obstacleList.get(i) != null && isObstacleInAdjacentLane(obstacleList.get(i)))
//                        {
//                            Log.d("entered", "he1");
//                           // Matrix.setIdentityM(mModelMatrixLane, 0);
//                            int scalingFactor = -10;
//                            if(obstacleList.get(i).getPosition()[1] < 0)
//                            {
//                                scalingFactor = scalingFactor * -1;
//                            }
//                            Matrix.translateM(mModelMatrixLane,0,-(float) (obstacleList.get(i).getPosition()[0]),0,scalingFactor + (float) (obstacleList.get(i).getPosition()[1]));
//                            //Log.d("orientation:Client", "qx: " + obstacleList.get(i).getOrientation()[0] + "qw:" + obstacleList.get(i).getOrientation()[3] + " " + ((Math.acos(obstacleList.get(i).getOrientation()[3]) * 2) - 180) + " " + Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3] * 2)));
////                          Matrix.rotateM(mModelMatrixLane, 0,  -180, 0, 1, 0); //(float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2) -180 original code
////                    //make the car hood to face forward
//                            Matrix.rotateM(mModelMatrixLane, 0, 0, 0, 0, 1);
////                    //make the car tires to touch the base else it is verticle instead of horizontal
//                            Matrix.rotateM(mModelMatrixLane, 0, 0, 1, 0, 0);
//                            Matrix.rotateM(mModelMatrixLane, 0, 0, 0, 1, 0);
////                            Matrix.rotateM(mModelMatrixLane, 0, (float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2) - 180, 0, 1, 0);
//                            //make the car tires to touch the base else it is vertical instead of horizontal
//                            //Matrix.rotateM(mModelMatrixLane, 0, -90, 1, 0, 0);
//                            drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, yellowColorHandle);
//                            Matrix.translateM(mModelMatrixLane,0,(float) (obstacleList.get(i).getPosition()[0]),0,-scalingFactor - (float) (obstacleList.get(i).getPosition()[1]));
//                        }
//                    }
//                }
            }
    }



    void drawTexture(FloatBuffer vertexBuffer,FloatBuffer colorBuffer, FloatBuffer normalBuffer, FloatBuffer textureBuffer, int size ,int dataSize,float useTexture,int mode,float[] mProjectionMatrix,float[] mViewMatrix,float[] mModelMatrix,int mTextureDataHandle)
    {

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
       // mLightPosHandleTexture = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

        mUseTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_UseTexture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");


        GLES20.glUniform2f(mUseTextureHandle,useTexture,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        vertexBuffer.position(0);
        /*
        *  mPositionHandle: The OpenGL index of the position attribute of shader program.
        *  dataSize: 2-D  or 3-D
        *  GL_FLOAT: The type of each element.
        *  false: Should fixed-point data be normalized or not
        *  0: The stride 0 means the positions should be read sequentially meaning, the buffer has only the vertex information.
              The stride tells OpenGL how far the same attribute for the next vertex is present so we can use a buffer which contains
              vertex,color,normal and texture together as well just vary the stride at that time
        */
        GLES20.glVertexAttribPointer(mPositionHandle, dataSize, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        if (colorBuffer != null) {

            colorBuffer.position(0);
            GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, 16, colorBuffer);
            GLES20.glEnableVertexAttribArray(mColorHandle);
        }

        if (normalBuffer != null) {

            normalBuffer.position(0);
            GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 0, normalBuffer);
            GLES20.glEnableVertexAttribArray(mNormalHandle);
        }

        if (textureBuffer != null) {

            textureBuffer.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, textureBuffer);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }

        //MVMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        //MVPMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);



        GLES20.glDrawArrays(mode, 0,size);
        Log.d("laneFormoation",mode+" "+size);
       // GLES20.glDeleteTextures(1,mTextureDataHandle);

    }

    boolean isObstacleInAdjacentLane(Obstacles obstacle)
    {
        double[] position = obstacle.getPosition();
        if(position[1] > 5.4 && position[1] < -5.4)
        {
            return false;
        }
        return true;
    }
}
