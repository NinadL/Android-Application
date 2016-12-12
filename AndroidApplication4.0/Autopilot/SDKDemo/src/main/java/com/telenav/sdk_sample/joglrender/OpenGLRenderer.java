package com.telenav.sdk_sample.joglrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.RelativeLayout;

import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.car.data.DataParser;
import com.telenav.sdk_sample.car.data.Lane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by ishwarya on 7/14/16.
 */

/**
 * Created by ishwarya on 7/14/16.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer
{
    //******    texture handles     ******//

    private int mMVPMatrixHandleTexture;
    private int mMVMatrixHandleTexture;
    private int mTextureUniformHandle;
    private int mPositionHandleTexture;
    private int mColorHandleTexture;
    private int mNormalHandle;
    private int mTextureCoordinateHandle;
    private int mProgramHandleTexture;
    private int mPointProgramHandle;
    private int mTextureDataHandle;

    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    float[] mModelMatrixLane = new float[16];
    float[] mProjectionMatrixLane = new float[16];
    float[] mViewMatrixLane = new float[16];
    float[] mMVPMatrixLane = new float[16];

    //******    constants     ******//

    final short INVISIBLE_LANE = 3;
    final short SOLID_LANE = 2;

    final short RIGHT_LANE = 0;
    final short LEFT_LANE = 1;


    private DrawEntity drawEntityObjectForBuffer = new DrawEntity();
    private laneFunctions laneFunctionsObject = new laneFunctions();
    static ArrayList<Lane> previousLaneBoundaries = new ArrayList<>();
    final Timer laneTimer = new Timer(true);
    private DataParser dp = new DataParser();
    Context context;
    private DrawEntity drawEntityObject;
    private RelativeLayout mainActivityRelativeLayout;
    int dataSize2D = 2;
    int dataSize3D = 3;


    /*
     * Constructor to initialize the lanes and call
     * the timer class to construct the lanes periodically
     */
    public OpenGLRenderer(Context context, RelativeLayout rl)
    {
        this.context = context;
        this.mainActivityRelativeLayout = rl;
        init();

        laneTimer.scheduleAtFixedRate(new LaneVertexCalculator(),0,85);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {  // Set the background clear color to gray.
        GLES20.glClearColor(0.025f,0.025f,0.025f, 1f);

        /*
        * horizontal shift if both "eyeX" and "lookX" are set to same value else model gets aligned at some angle instead of being oriented horizontally
        * "lookY" shifts the view up/down ,-40 places it in center of screen and  "eyeY" makes the camera to go up/down
        *
        * */
        final float eyeX = -21.0f;
        final float eyeY =0.0f;
        final float eyeZ = 7.0f;

        // We are looking toward the distance
        final float lookX = -8.0f;
        final float lookY = -0.0f;
        final float lookZ = -10.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 1.0f;
        final float upY = 0.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final float eyeXLane = -21.0f;//-17
        final float eyeYLane = 0.0f;
        final float eyeZLane = 7.0f;

        // We are looking toward the distance
        final float lookXLane = -8.0f;
        final float lookYLane = 0.0f;
        final float lookZLane = -10.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upXLane = 1.0f;
        final float upYLane = 0.0f;
        final float upZLane = 0.0f;

        Matrix.setLookAtM(mViewMatrixLane, 0, eyeXLane, eyeYLane, eyeZLane, lookXLane, lookYLane, lookZLane, upXLane, upYLane, upZLane);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        drawEntityObject = new DrawEntity(mainActivityRelativeLayout,mMVPMatrixLane,mViewMatrixLane,mModelMatrixLane,mProjectionMatrixLane);

        final String vertexShaderTexture = getVertexShader();
        final String fragmentShaderTexture = getFragmentShader();

        final int vertexShaderHandleTexture = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderTexture);
        final int fragmentShaderHandleTexture =compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderTexture);

        mProgramHandleTexture = createAndLinkProgram(vertexShaderHandleTexture, fragmentShaderHandleTexture,
                new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});

        // Load the texture


        GLES20.glUseProgram(mProgramHandleTexture);

        int roadTextureHandle = loadTexture(context, R.drawable.road_color);
        int redColorHandle  = mTextureDataHandle = OpenGLRenderer.loadTexture(context, R.raw.rio_red);
        int yellowColorHandle  = mTextureDataHandle = OpenGLRenderer.loadTexture(context, R.raw.rio_yellow);
        int whiteColorHandle  = OpenGLRenderer.loadTexture(context, R.drawable.white_strip);

        drawEntityObject.setTextureValues(mPositionHandleTexture,mProgramHandleTexture,mMVPMatrix,mViewMatrix,mModelMatrix,mProjectionMatrix,mMVPMatrixHandleTexture,mColorHandleTexture,mNormalHandle,mTextureCoordinateHandle,context,mMVMatrixHandleTexture,mTextureDataHandle,redColorHandle,yellowColorHandle, roadTextureHandle, whiteColorHandle);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrixLane, 0, -1.3f, 1.3f, -1, 1, 1, 100);


        final float ratioHomeScreen = (float) width / height;
        final float leftHomeScreen = -ratio;
        final float rightHomeScreen = ratio;
        final float bottomHomeScreen = -1;
        final float topHomeScreen =1;
        final float nearHomeScreen =1;
        final float farHomeScreen = 50.0f;

        Matrix.frustumM(mProjectionMatrix, 0, leftHomeScreen, rightHomeScreen, bottomHomeScreen, topHomeScreen, nearHomeScreen, farHomeScreen);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mModelMatrixLane, 0);

        Matrix.translateM(mModelMatrixLane,0,-24,0,0f);
        Matrix.scaleM(mModelMatrixLane,0,0.55f,1.5f,-1f);

        drawEntityObject.drawToScreen();
    }

    public FloatBuffer convertToFloatBuffer(ArrayList List,int dataSize){
        FloatBuffer Buffer;
        Buffer = ByteBuffer.allocateDirect(List.size() * 4 * dataSize)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        Buffer.put(laneFunctionsObject.convertToArray(List));
        return Buffer;
    }

    private int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            GLES20.glShaderSource(shaderHandle, shaderSource);
            GLES20.glCompileShader(shaderHandle);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0)
            {
                Log.d( "tag","Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }


    public static String readTextFileFromRawResource(final Context context,
                                                     final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }

    private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0)
            {
                Log.e("TAG", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }


    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];
        GLES20.glDeleteTextures(1, textureHandle, 0);
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    protected String getVertexShader()
    {
        return readTextFileFromRawResource(context, R.raw.vertex_shader);
    }

    protected String getFragmentShader()
    {
        return readTextFileFromRawResource(context, R.raw.fragment_shader);
    }

    void init()
    {
        Lane lane = new Lane();
        lane.init();

        previousLaneBoundaries.add(lane.leftLane);
        previousLaneBoundaries.add(lane.rightLane);
        previousLaneBoundaries.add(lane.centerLane);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
      Class : LaneVertexCalculator
            This class is used to calculate the vertices of the lane boundaries
            for displaying them on OpenGl
     */
    private class LaneVertexCalculator extends TimerTask {

        ArrayList<Lane> laneBoundaryData = new ArrayList<Lane>();

        @Override
        public void run()
        {
            ArrayList boundaryPoints = new ArrayList();
            ArrayList centreLanePoints = new ArrayList();
            ArrayList sideLanePoints = new ArrayList();

            ArrayList boundaryColor = new ArrayList();
            ArrayList centreLaneColor = new ArrayList();
            ArrayList sideLaneColor = new ArrayList();

            laneBoundaryData = dp.getLaneObject();

            //Clear all the existing data
             boundaryPoints.clear();
             centreLanePoints.clear();
             centreLaneColor.clear();
             boundaryColor.clear();

            ArrayList<Lane> localLaneBoundaryData = laneBoundaryData;


            //If we do not receive any lanes, use the previous lane boundaries
            //Else, save the current lane boundaries.
            if(localLaneBoundaryData.size() == 0)
            {
                localLaneBoundaryData.clear();
                localLaneBoundaryData = previousLaneBoundaries;
            }
            else
            {
                previousLaneBoundaries.clear();
                previousLaneBoundaries = localLaneBoundaryData;
            }


            //Iterate through the lane boundaries and display the appropriate boundary based on the type
            for (int i = 0; i < localLaneBoundaryData.size(); i++)
            {
                if (localLaneBoundaryData.get(i) != null )
                {
                    switch( localLaneBoundaryData.get(i).getType())
                    {
                        case 1:
                            if(i == 0) {
                                laneFunctionsObject.getDashedLineCoordinates(localLaneBoundaryData.get(i).getPoints(), 1, RIGHT_LANE,
                                        boundaryPoints, boundaryColor);

                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), 4, RIGHT_LANE,
                                        null, centreLanePoints, sideLanePoints,
                                        null, centreLaneColor, sideLaneColor);
                            }
                            else if(i == 1)
                            {
                                laneFunctionsObject.getDashedLineCoordinates(localLaneBoundaryData.get(i).getPoints(), 1, LEFT_LANE,
                                        boundaryPoints, boundaryColor);

                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), 4, LEFT_LANE,
                                        null, centreLanePoints, sideLanePoints,
                                        null, centreLaneColor, sideLaneColor);
                            }
                            break;



                        case 3:
                            //dashed lane boundary
                            if(i == 0) {
                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), INVISIBLE_LANE, RIGHT_LANE,
                                        boundaryPoints, centreLanePoints, sideLanePoints,
                                        boundaryColor, centreLaneColor, sideLaneColor);
                            }
                            else if(i == 1)
                            {
                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), INVISIBLE_LANE, LEFT_LANE,
                                        boundaryPoints, centreLanePoints, sideLanePoints,
                                        boundaryColor, centreLaneColor, sideLaneColor);
                            }
                            break;
                        case 2:
                            //solid lane boundary
                            if(i == 0)
                            {
                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), SOLID_LANE, RIGHT_LANE,
                                        boundaryPoints, centreLanePoints,sideLanePoints,
                                        boundaryColor, centreLaneColor,  sideLaneColor);

                            }
                            else if(i == 1)
                            {
                                laneFunctionsObject.generateVertexAndColorPoints(localLaneBoundaryData.get(i).getPoints(), SOLID_LANE, LEFT_LANE,
                                        boundaryPoints, centreLanePoints,sideLanePoints,
                                        boundaryColor, centreLaneColor,  sideLaneColor);
                            }
                            break;
                    }
                }

            }

            //If all vertices and colors are set, then render the lane
            if (boundaryPoints.size() != 0 && boundaryColor.size() != 0 &&
                centreLanePoints.size() != 0 &&  centreLaneColor.size() != 0 &&
                sideLanePoints.size() != 0 && sideLaneColor.size() != 0)
            {

                FloatBuffer boundaryVertexBuffer = convertToFloatBuffer(boundaryPoints,dataSize2D);
                FloatBuffer boundaryColorBuffer = convertToFloatBuffer(boundaryColor,dataSize2D);

                FloatBuffer centreLaneVertexBuffer = convertToFloatBuffer(centreLanePoints,dataSize2D);
                FloatBuffer centreLaneColorBuffer = convertToFloatBuffer(centreLaneColor,dataSize2D);

                FloatBuffer sideLaneVertexBuffer = convertToFloatBuffer(sideLanePoints,dataSize2D);
                FloatBuffer sideLaneColorBuffer = convertToFloatBuffer(sideLaneColor,dataSize2D);

                drawEntityObjectForBuffer.setSideLaneBuffer(sideLaneVertexBuffer, sideLaneColorBuffer, sideLanePoints.size() / 2, true);
                drawEntityObjectForBuffer.setCentreLaneBuffer(centreLaneVertexBuffer, centreLaneColorBuffer, centreLanePoints.size() / 2, true);
                drawEntityObjectForBuffer.setBoundaryBuffer(boundaryVertexBuffer, boundaryColorBuffer, boundaryPoints.size() / 2, true);
            }
        }
    }
}