package com.telenav.sdk_sample.joglrender;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.RelativeLayout;

import com.telenav.sdk_sample.car.data.DataParser;
import com.telenav.sdk_sample.car.data.Obstacles;
import com.telenav.sdk_sample.car.data.setStatusClass;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by ishwarya on 7/20/16.
 */
public class DrawEntity
{
    //******    texture handles     ******//
    private int mPositionHandle;
    private int mProgramHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mTextureCoordinateHandle;
    private int mUseTextureHandle;
    private int mMVMatrixHandle;

    float[] mModelMatrixLane;
    float[] mProjectionMatrixLane;
    float[] mViewMatrixLane;
    float[] mMVPMatrixLane;

    float[] mModelMatrix;
    float[] mProjectionMatrix;
    float[] mViewMatrix;
    private float[] mMVPMatrix;

    int mTextureUniformHandle;
    int mTextureDataHandle;

    //******  Buffers used to render the objects ******//

    static private FloatBuffer boundaryVertexBuffer;
    static private FloatBuffer centreLaneVertexBuffer;
    static private FloatBuffer sideLaneVertexBuffer;

    static private FloatBuffer previousBoundaryVertexBuffer;
    static private FloatBuffer previousCentreLaneVertexBuffer;
    static private FloatBuffer previousSideLaneVertexBuffer;

    static private FloatBuffer boundaryColorBuffer;
    static private FloatBuffer centreLaneColorBuffer;
    static private FloatBuffer sideLaneColorBuffer;

    static private FloatBuffer previousBoundaryColorBuffer;
    static private FloatBuffer previousCentreLaneColorBuffer;
    static private FloatBuffer previousSideLaneColorBuffer;

    static modelBuffers vehicleObj;

    //****** Sizes is required while rendering the objects ******//

    static  private int boundarySize = 0;
    static private int centreLaneSize = 0;
    static  private int sideLaneSize = 0;

    static  private int previousBoundarySize = 0;
    static private int previousCentreLaneSize = 0;
    static  private int previousSideLaneSize = 0;

    //****** Booleans values used to check if the buffers have been set ******//

    static boolean isBoundaryAvbl = false;
    static boolean isCentreLaneAvbl =false;
    static boolean isSideLaneAvbl =false;
    static boolean isVehicleObjAvbl =false;

    //****** Color handles for the car ******//

    int redColorHandle;
    int yellowColorHandle;
    int roadTextureHandle;
    int whiteColorHandle;

    //****** Constant values ******//

    private short minArrayListSize = 3;
    private short xYVertexSize = 2;
    private short xYZVertexSize = 3;

    private  int mColorDataSize = 4;
    private  int mNormalDataSize = 3;
    private  int mTextureCoordinateDataSize = 2;

    private  DataParser dp = new DataParser();
    private setStatusClass statusObject = new setStatusClass();
    private static  RelativeLayout mainActivityRelativeLayout;
    Context Context;

    public DrawEntity()
    {

    }


    public DrawEntity(RelativeLayout mainActivityRelativeLayout, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix) {

        this.mainActivityRelativeLayout = mainActivityRelativeLayout;
        mMVPMatrixLane =  mMVPMatrix;
        mViewMatrixLane = mViewMatrix;
        mModelMatrixLane = mModelMatrix;
        mProjectionMatrixLane = mProjectionMatrix;

    }

    /*
    *   This method is used to initialize the texture handles and the matrices
    */
    public void setTextureValues(int mPositionHandle, int mProgramHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix, int mMVPMatrixHandle, int mColorHandle, int mNormalHandle, int mTextureCoordinateHandle, Context c, int mMVMatrixHandle, int mTextureDataHandle,int redColorHandle, int yellowColorHandle, int roadTextureHandle, int whiteColorHandle) {

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
        this.roadTextureHandle = roadTextureHandle;
        this.whiteColorHandle = whiteColorHandle;
    }

     /*
     *   This method is used to set the vertex and the color for the boundary.
     *   We check if the buffer is empty or not. If it is empty, then we use the previously
     *   rendered contents again.
     */
    public void setBoundaryBuffer(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int size, boolean flag)
    {
        if(vertexBuffer == null || vertexBuffer.capacity() - vertexBuffer.remaining() == 0)
        {
            boundaryVertexBuffer = previousBoundaryVertexBuffer;
            boundaryColorBuffer = previousBoundaryColorBuffer;
            boundarySize = previousBoundarySize;
        }
        else
        {
            previousBoundaryVertexBuffer = boundaryVertexBuffer = vertexBuffer;
            previousBoundaryColorBuffer = boundaryColorBuffer = colorBuffer;
            previousBoundarySize = boundarySize = size;

        }
        isBoundaryAvbl = flag;
    }

    /*
    *   This method is used to set the vertex and the color for the centre part of the lane.
    *   We check if the buffer is empty or not. If it is empty, then we use the previously
    *   rendered contents again.
    */
    public void setCentreLaneBuffer(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int size, boolean flag)
    {
        if(vertexBuffer == null || vertexBuffer.capacity() - vertexBuffer.remaining() == 0)
        {
            centreLaneVertexBuffer = previousCentreLaneVertexBuffer;
            centreLaneColorBuffer = previousCentreLaneColorBuffer;
            centreLaneSize = previousCentreLaneSize;
        }
        else
        {
            previousCentreLaneVertexBuffer = centreLaneVertexBuffer = vertexBuffer;
            previousCentreLaneColorBuffer = centreLaneColorBuffer = colorBuffer;
            previousCentreLaneSize = centreLaneSize = size;

        }
        isCentreLaneAvbl = flag;
    }

    /*
    *   This method is used to set the vertex and the color for the side part of the lane.
    *   We check if the buffer is empty or not. If it is empty, then we use the previously
    *   rendered contents again.
    */
    public void setSideLaneBuffer(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int size, boolean flag)
    {
        if(vertexBuffer == null || vertexBuffer.capacity() - vertexBuffer.remaining() == 0)
        {
            sideLaneVertexBuffer = previousSideLaneVertexBuffer;
            sideLaneColorBuffer = previousSideLaneColorBuffer;
            sideLaneSize = previousSideLaneSize;
        }
        else
        {
            previousSideLaneVertexBuffer = sideLaneVertexBuffer = vertexBuffer;
            previousSideLaneColorBuffer = sideLaneColorBuffer = colorBuffer;
            previousSideLaneSize = sideLaneSize = size;

        }
        isSideLaneAvbl = flag;

    }

    /*
    *   This method is used to set the vehicle object.
    */
    public void setVehicleStatus(modelBuffers object, boolean flag)
    {
        vehicleObj = object;
        isVehicleObjAvbl = flag;
    }

    /*
    *   This method is used to draw various objects on the screen
    */
    public void drawToScreen()
    {

        //Check if the boundary is ready to be rendered, and non-empty
        if (isBoundaryAvbl && boundarySize > minArrayListSize)
        {
            drawTexture(boundaryVertexBuffer, boundaryColorBuffer,
                        null, null, boundarySize,
                        xYVertexSize, 0.0f, GLES20.GL_TRIANGLES,
                        mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, -2);
        }

        //Check if the centre lane part is ready to be rendered, and non-empty
        if (isCentreLaneAvbl && centreLaneSize > minArrayListSize)
        {
            drawTexture(centreLaneVertexBuffer, centreLaneColorBuffer, null, null, centreLaneSize, xYVertexSize, 0.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, -2);
        }

        //Check if the side lane part is ready to be rendered, and non-empty
        if (isSideLaneAvbl && sideLaneSize > minArrayListSize)
        {
            drawTexture(sideLaneVertexBuffer, sideLaneColorBuffer, null, null, sideLaneSize, xYVertexSize, 0.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, -2);
        }

        //Check if vehicle object is ready to be rendered
        if (isVehicleObjAvbl)
        {
            ////////////////////////////////////////
            //  Setup the view for the vehicles   //
            ////////////////////////////////////////

            short xTranslateValue = 6;

            Matrix.scaleM(mModelMatrixLane, 0, 0.8f, 0.4f, 0.6f);
            //Matrix.scaleM(mModelMatrixLane, 0, 1f, 0.4f, 0.6f);
            Matrix.translateM(mModelMatrixLane, 0, xTranslateValue, 0, 0);

            //make the car hood to face forward
            Matrix.rotateM(mModelMatrixLane, 0, -180, 0, 0, 1);

            //make the car tires to touch the base else it is vertical instead of horizontal
            Matrix.rotateM(mModelMatrixLane, 0, -90, 1, 0, 0);
            Matrix.rotateM(mModelMatrixLane, 0, 0, 0, 1, 0);

            GLES20.glFlush();

            ////////////////////////////////////////
            //  Draw the vehicles                 //
            ////////////////////////////////////////

            //Draw our car
            drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, redColorHandle);
            Matrix.translateM(mModelMatrixLane, 0, -xTranslateValue, 0, 0);

            //Draw the obstacles
            if (statusObject.gettingPerceptionData() && dp.getObstaclesObject()!= null)
            {
                ArrayList<Obstacles> obstacleList = dp.getObstaclesObject();
                for (int i = 0; i < obstacleList.size(); i++)
                {
                    if (obstacleList.get(i) != null && isObstacleInAdjacentLane(obstacleList.get(i)))
                    {
                        float yCoordinate = (float)obstacleList.get(i).getPosition()[1];
                        float xCoordinate = (float)obstacleList.get(i).getPosition()[0];

//                        float yCoordinate = -3.6f;
//                        float xCoordinate = 10.0f;

                        Matrix.scaleM(mModelMatrixLane, 0, 1f, 1f, 1f);

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

                        //Translate the obstacle, rotate it to the desired angle, render the obstacle,
                        // move the perception back to origin
                        //Matrix.translateM(mModelMatrixLane,0,-(float) (xCoordinate),0,scalingFactor + (float) (yCoordinate));
                        Matrix.translateM(mModelMatrixLane,0,-(float) (obstacleList.get(i).getPosition()[0]),0,scalingFactor + (float) (obstacleList.get(i).getPosition()[1]));
                        Matrix.rotateM(mModelMatrixLane, 0, (float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2), 0, 1, 0);
                        drawTexture(vehicleObj.getVertexBuffer(), vehicleObj.getColorBuffer(), vehicleObj.getNormalBuffer(), vehicleObj.getTextureBuffer(), vehicleObj.getSize(), xYZVertexSize, 1.0f, GLES20.GL_TRIANGLES, mProjectionMatrixLane, mViewMatrixLane, mModelMatrixLane, yellowColorHandle);
                        Matrix.rotateM(mModelMatrixLane, 0, -((float) Math.toDegrees(Math.acos(obstacleList.get(i).getOrientation()[3]) * 2)), 0, 1, 0);
                        Matrix.translateM(mModelMatrixLane,0,(float) (xCoordinate),0,-scalingFactor - (float) (yCoordinate));
                    }
                }
            }
        }
    }


    /*
    *   This method is used to render the objects on the screen
    */
    void drawTexture(FloatBuffer vertexBuffer,FloatBuffer colorBuffer,
                     FloatBuffer normalBuffer, FloatBuffer textureBuffer, int size,
                     int dataSize,float useTexture,int mode, float[] mProjectionMatrix,
                     float[] mViewMatrix,float[] mModelMatrix,int mTextureDataHandle)
    {

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
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

        if(vertexBuffer != null)
        {
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
        }
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

        GLES20.glDrawArrays(mode, 0, size);
    }

    /*
     *  This method is used to check if the obstacle is in the adjacent lane.
     *  We check if the obstacle is at a distance of >5.4 or <-5.4 on Y axis.
     */
    boolean isObstacleInAdjacentLane(Obstacles obstacle)
    {
        double[] position = obstacle.getPosition();
        if(position[1] > 5.4d || position[1] < -5.4d)
        {
            return false;
        }
        return true;
    }
}