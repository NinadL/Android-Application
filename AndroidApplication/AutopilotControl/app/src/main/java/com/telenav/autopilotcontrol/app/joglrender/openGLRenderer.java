package com.telenav.autopilotcontrol.app.joglrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.RelativeLayout;

import com.telenav.autopilotcontrol.R;
import com.telenav.autopilotcontrol.app.car_data.DataParser;
import com.telenav.autopilotcontrol.app.car_data.FieldOfView;
import com.telenav.autopilotcontrol.app.car_data.Lane;
import com.telenav.autopilotcontrol.app.car_data.Sensor;
import com.telenav.autopilotcontrol.app.car_data.setStatusClass;

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
public class openGLRenderer implements GLSurfaceView.Renderer
{
    float[] mModelMatrixLane = new float[16];
    float[] mProjectionMatrixLane = new float[16];
    float[] mViewMatrixLane = new float[16];
    float[] mMVPMatrixLane = new float[16];


    /******    texture handles     ******/
    private int mMVPMatrixHandleTexture;
    private int mMVMatrixHandleTexture;
    private int mTextureUniformHandle;
    private int mPositionHandleTexture;
    private int mColorHandleTexture;
    private int mNormalHandle;
    private int mTextureCoordinateHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];


    private int mProgramHandleTexture;
    private int mPointProgramHandle;
    private int mTextureDataHandle;

    private final int mColorDataSize = 4;
    private final int mColorLaneSize = 3;
    private final int mNormalDataSize = 3;

    Context context;

    short gridMode = -10;

    //private final Handler handler = new Handler();

    private draw drawObjectForBuffer = new draw();
    private draw drawObject;
    private laneFunctions laneFunctionsObject = new laneFunctions();

    private DataParser dp = new DataParser();
    private setStatusClass statusObject = new setStatusClass();

    final Timer sensorStatusTimer = new Timer(true);
    final Timer laneTimer = new Timer(true);
    final Timer gridTimer = new Timer(true);

    //private Handler handler = new Handler();
    Lane centerLane =Lane.cLane();
    Lane rightLane =Lane.rLane();
    Lane leftLane = Lane.lLane();
    private RelativeLayout mainActivityRelativeLayout;
    int dataSize2D = 2;
    int dataSize3D = 3;

    public openGLRenderer(){}

    public openGLRenderer(Context context, RelativeLayout rl)
    {
        this.context = context;
        this.mainActivityRelativeLayout = rl;
        sensorStatusTimer.scheduleAtFixedRate(new senorStatusTimerClass(),0,1000);
        laneTimer.scheduleAtFixedRate(new laneCalculationTimerClass(),0,85);
        gridTimer.scheduleAtFixedRate(new gridProductionTimerClass(),0,150);

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {  // Set the background clear color to gray.
        GLES20.glClearColor(0f,0f,0f, 0.5f);

        /*
        * horizontal shift if both "eyeX" and "lookX" are set to same value else model gets aligned at some angle instead of being oriented horizontally
        * "lookY" shifts the view up/down ,-40 places it in center of screen and  "eyeY" makes the camera to go up/down
        *
        * */
        final float eyeX = -3;                                     ;
        final float eyeY =22f;
        final float eyeZ = 0;

        // We are looking toward the distance
        final float lookX = -3f;
        final float lookY = -15f;
        final float lookZ = -10.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final float eyeXLane = -21;//-17
        final float eyeYLane = 0f;
        final float eyeZLane = 13;

        // We are looking toward the distance
        final float lookXLane = -0.0f;
        final float lookYLane = 0.0f;
        final float lookZLane = -10.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upXLane = 1.0f;
        final float upYLane = 0.0f;
        final float upZLane = 0.0f;

        Matrix.setLookAtM(mViewMatrixLane, 0, eyeXLane, eyeYLane, eyeZLane, lookXLane, lookYLane, lookZLane, upXLane, upYLane, upZLane);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        drawObject = new draw(mainActivityRelativeLayout,mMVPMatrixLane,mViewMatrixLane,mModelMatrixLane,mProjectionMatrixLane);

        final String vertexShaderTexture = getVertexShader();
        final String fragmentShaderTexture = getFragmentShader();

        final int vertexShaderHandleTexture = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderTexture);
        final int fragmentShaderHandleTexture =compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderTexture);

        mProgramHandleTexture = createAndLinkProgram(vertexShaderHandleTexture, fragmentShaderHandleTexture,
                new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});
        GLES20.glUseProgram(mProgramHandleTexture);
        int redColorHandle =  mTextureDataHandle = openGLRenderer.loadTexture(context, R.raw.rio_red);
        int yellowColorHandle =  mTextureDataHandle = openGLRenderer.loadTexture(context, R.raw.rio_yellow);

        drawObject.setTextureValues(mPositionHandleTexture,mProgramHandleTexture,mMVPMatrix,mViewMatrix,mModelMatrix,mProjectionMatrix,mMVPMatrixHandleTexture,mColorHandleTexture,mNormalHandle,mTextureCoordinateHandle,context,mMVMatrixHandleTexture,mTextureDataHandle,redColorHandle,yellowColorHandle);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -1;
        final float right = 1;
        final float bottom = -1;
        final float top =1;
        final float near = -10;
        final float far = 10.0f;

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



        drawObject.drawToScreen();                      // Draw the cube (NEW)


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



    /*
    * Calculates the sensor coverage
    * (network data) x = -x(for drawing)
    * (network data) y = y(for drawing)]
    * (network data) z = z(for drawing)
    * (network data) theta = -theta(for drawing)
    * sensorList.get(i).position[0] = x;
    * sensorList.get(i).position[1] = y;
    * sensorList.get(i).position[2] = z;
     * sensorList.get(i).orientation[0] = qw;
        */

    class senorStatusTimerClass extends TimerTask
    {
        public void run() {
            if ( statusObject.gettingPerceptionData() ){

                ArrayList sensorPoints = new ArrayList();
                ArrayList sensorColorList = new ArrayList();

                ArrayList<Sensor> sensorList = dp.getSensorObject();


                for (int i = 0; i < sensorList.size(); i++) {
                    ArrayList<FieldOfView> fOVList = sensorList.get(i).getFieldOfViews();
                    for(int j = 0; j<fOVList.size();j++)
                    {
                        short xAdjustmentValue = 1;
                        short zMultiplicationFactor = 4;
                        boolean visibility = false;

                        if(sensorList.get(i).getStatus() == 1) {
                            visibility = true;
                        }

                        double lengthMax = fOVList.get(i).getLengthSpan().getLengthMax();
                        //Log.d("Sensor data",(xAdjustmentValue + -(sensorList.get(i).getPosition()[0])) +" "+ sensorList.get(i).getPosition()[1]+" "+(zMultiplicationFactor * sensorList.get(i).getPosition()[2])+ "orientation" +(float) Math.acos(-sensorList.get(i).getOrientation()[3])*2 +" "+sensorList.get(i).getOrientation()[0]+" "+Math.acos(sensorList.get(i).getOrientation()[3]));

                        /*displaying the available coverage area that the sensor senses in green*/

                        double angleMax = fOVList.get(j).getAngleSpan().getAngleMax();
                        new homeScreenFunctions().getSensorCoverageBuffer(angleMax, (float) (xAdjustmentValue + (-sensorList.get(i).getPosition()[0])), (float) (sensorList.get(i).getPosition()[1]), (float) (zMultiplicationFactor * sensorList.get(i).getPosition()[2]), (float) lengthMax, visibility, sensorPoints, sensorColorList, (float) Math.acos(-sensorList.get(i).getOrientation()[0])*2);

                        double angleMin = fOVList.get(i).getAngleSpan().getAngleMin();
                        new homeScreenFunctions().getSensorCoverageBuffer(angleMin, (float) (xAdjustmentValue + -sensorList.get(i).getPosition()[0]), (float)  (sensorList.get(i).getPosition()[1]), (float) (zMultiplicationFactor * sensorList.get(i).getPosition()[2]), (float) lengthMax, visibility, sensorPoints, sensorColorList, (float) Math.acos(-sensorList.get(i).getOrientation()[0])*2);


                        double lengthMin = fOVList.get(i).getLengthSpan().getLengthMin();

                        if (lengthMin > 0)
                        {
                            new homeScreenFunctions().getSensorCoverageBuffer(angleMax, (float) (xAdjustmentValue + -new Sensor().getPosition()[0]), (float) new Sensor().getPosition()[1], (float) (zMultiplicationFactor * new Sensor().getPosition()[2]), (float) lengthMin, false, sensorPoints, sensorColorList,  (float) Math.acos(sensorList.get(i).getOrientation()[0])*2);
                            new homeScreenFunctions().getSensorCoverageBuffer(angleMin, (float) (xAdjustmentValue + -new Sensor().getPosition()[0]), (float) new Sensor().getPosition()[1], (float) (zMultiplicationFactor * new Sensor().getPosition()[2]), (float) lengthMin, false, sensorPoints, sensorColorList,  (float) Math.acos(sensorList.get(i).getOrientation()[0])*2);
                        }

                        if (sensorPoints.size() > 0)
                        {
                            FloatBuffer vertexList = convertToFloatBuffer(sensorPoints, dataSize3D);
                            FloatBuffer colorList = convertToFloatBuffer(sensorColorList,dataSize3D);
                            new draw().setBufferSensors(vertexList, colorList, sensorPoints.size() / 3, true);

                        }
                        else
                            new draw().setBufferSensors(null, null, 0, false);
                        // Log.d("sensorProductionEnds", String.valueOf("  " + sensorPoints.size()));
                    }
                }
            }
            else
                new draw().setBufferSensors(null, null, 0, false);
        }
    }


    private class laneCalculationTimerClass extends TimerTask {

        ArrayList<Lane> laneBoundaryData = new ArrayList<Lane>();


        @Override
        public void run() {
            boolean leftLanePresent = false;
            boolean rightLanePresent = false;
            boolean leftLaneDashed = false;
            boolean rightLaneDashed = false;

           if (statusObject.gettingLaneData())
            {
                // long waitTime = 100;


                laneBoundaryData = dp.getLaneObject();
                ArrayList lanePoints = new ArrayList();
                ArrayList laneColorList = new ArrayList();
                ArrayList<Lane> localLaneBoundaryData = laneBoundaryData;

                long startTime = System.currentTimeMillis();
                for (int lane_no = 0; lane_no < localLaneBoundaryData.size(); lane_no++) {
                    if (localLaneBoundaryData.get(lane_no) != null )//{&&  localLaneBoundaryData.get(lane_no).getType() < 9  ) {
                    {
                        Log.d("lane number",lane_no+"lane type "+localLaneBoundaryData.get(lane_no).getType());
                        if(lane_no == 1)
                            leftLanePresent = true;
                        if(lane_no == 2) {
                            rightLanePresent = true;
                            Log.d("lane ty", "rightLanePresent " + rightLanePresent);
                        }

                        /*
                         * LaneType {DASHED = 1, SOLID = 2,UNDECIDED =3, ROAD_EDGE=4, DOUBLE_LANE_MARK=5, BOTTS_DOTS=6, INVALID=7, UNKNOWN=8, CENTER_LINE=9, PATH=10
                         */
                        switch( localLaneBoundaryData.get(lane_no).getType()){
                            case -1:
                                laneFunctionsObject.getDashedLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), -1, lanePoints, laneColorList);
                                break;
                            case -2:
                                laneFunctionsObject.getSolidLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), -2, lanePoints, laneColorList,0);
                                break;
                            case 1://dashed
//                                if(lane_no == 1 )
//                                {
//                                    laneFunctionsObject.getSolidLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), 1, lanePoints, laneColorList,3.6f);
//                                    leftLaneDashed = true;
//                                }
//                                if( lane_no ==2)
//                                {
//                                    laneFunctionsObject.getSolidLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), 1, lanePoints, laneColorList,-3.6f);
//                                    rightLaneDashed = true;
//                                }
                                laneFunctionsObject.getDashedLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), 1, lanePoints, laneColorList);
                                break;
                            case 9: //centerlane
                                new laneFunctions().getDashedLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), 9, lanePoints, laneColorList);
                                break;
                            default:
                                laneFunctionsObject.getSolidLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), localLaneBoundaryData.get(lane_no).getType(), lanePoints, laneColorList,0);
                        }
                    }

                }



                Log.d("laneProductionEnds", String.valueOf(" timeTaken " + (System.currentTimeMillis() - startTime)) + "bufferSize " + lanePoints.size());

                if (lanePoints.size() != 0) {
                    FloatBuffer vertexBuffer = convertToFloatBuffer(lanePoints,dataSize2D);
                    FloatBuffer colorBuffer = convertToFloatBuffer(laneColorList,dataSize2D);

                    drawObjectForBuffer.setBufferLane(vertexBuffer, colorBuffer, lanePoints.size() / 2, true);

                }
                else
                    drawObjectForBuffer.setBufferLane(null, null, 0, false);
            }
            else{
                drawObjectForBuffer.setBufferLane(null, null, 0, false);
            }
            statusObject.setIsLeftLaneVisible(leftLanePresent);
            Log.d("lane type","rightLanePresent1 "+ rightLanePresent);
            statusObject.setIsRightLaneVisible(rightLanePresent);
            statusObject.setAreWeOnCentreLane(leftLaneDashed && rightLaneDashed);
        }
    }

    private class gridProductionTimerClass extends TimerTask {

        ArrayList<Lane> laneBoundaryData = new ArrayList<Lane>();
        @Override
        public void run() {
           if((statusObject.gettingLaneData()))
            {
                long waitTime = 100;

                laneBoundaryData = dp.getLaneObject();
                ArrayList gridPoints = new ArrayList();
                ArrayList gridColorList = new ArrayList();
                long startTime = System.currentTimeMillis();


                ArrayList<Lane> localLaneBoundaryData = laneBoundaryData;
//                new laneFunctions().getSolidLineCoordinates(centerLane.getPoints(), gridMode, gridPoints, gridColorList,0);
//                new laneFunctions().gridFormation(centerLane.getPoints(), gridPoints,gridColorList);

                for (int lane_no = 0; lane_no < localLaneBoundaryData.size(); lane_no++)
                {
                    if (localLaneBoundaryData.get(lane_no) != null &&  localLaneBoundaryData.get(lane_no).getType() == 9  )
                    {
                        new laneFunctions().getSolidLineCoordinates(localLaneBoundaryData.get(lane_no).getPoints(), gridMode, gridPoints, gridColorList,0);
                        new laneFunctions().gridFormation(localLaneBoundaryData.get(lane_no).getPoints(), gridPoints,gridColorList);

                    }
                }

                if (gridPoints.size() != 0) {
                    FloatBuffer vertexList = convertToFloatBuffer(gridPoints,dataSize2D);
                    FloatBuffer colorList = convertToFloatBuffer(gridColorList,dataSize2D);

                    modelBuffers obj = new modelBuffers();
                    obj.setBuffer(vertexList, colorList, null, null, gridPoints.size() / 2);
                    drawObjectForBuffer.setBufferGrid(obj, true);
                }
                else
                    drawObjectForBuffer.setBufferGrid(null, false);

                Log.d("gridProduction"," "+(System.currentTimeMillis()- startTime));

            }
            else
                drawObjectForBuffer.setBufferGrid(null, false);
        }
    }

}
