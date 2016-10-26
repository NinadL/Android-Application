package com.telenav.sdk_sample.car.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import org.json.parser.ParseException;

//import com.telenav.sdk_sample.joglrender.bufferGenerate;

/**
 * Created by ninad on 6/14/16.
 */
public class DataParser
{
    static State stateObject = new State();
    static Ego egoObject = new Ego();
    static ArrayList<Sensor> sensorObject = new ArrayList<Sensor>();
    static ArrayList<Obstacles> obstaclesObject = new ArrayList<Obstacles>();
    static ArrayList<Lane> laneObject = new ArrayList<Lane>();
    static ControlMessage controlMessageObject = new ControlMessage();
    static ArrayList<Lane> sortedLane = new ArrayList<Lane>();
    private Object mutex =new Object();
    public static JSONObject jsonObject;
    public static String controlString;
    public static JSONObject autopilotJsonObject;
    public static long controlTimestamp = 0;
    public static long perceptionTimestamp= 0;
    public static long mapTimestamp= 0;
    public static long laneObjectTimestamp = 0;
    //private bufferGenerate bufferGenerate;
    private setStatusClass statusObject = new setStatusClass();

    public Object getMutex(){
        return mutex;
    }

    public DataParser(){}

    public ControlMessage getControlMessageObject()
    {
        return controlMessageObject;
    }

    public State getStateObject()
    {
        return stateObject;
    }

    public Ego getSelfObject()
    {
        Log.d("Ego Object", String.valueOf(egoObject));
        return egoObject;
    }

    public ArrayList<Sensor> getSensorObject()
    {
        ArrayList<Sensor> localDeepCopy = null;
        synchronized (mutex) {
            if (sensorObject != null) {
                localDeepCopy = new ArrayList<Sensor>(sensorObject);
                //  mutex.notify();
                //  Log.d("motex","sent");
            }

        }
        return localDeepCopy;
    }

    public ArrayList<Lane> getLaneObject() {
        ArrayList<Lane> localDeepCopy = null;
        synchronized (mutex) {
            if (sortedLane != null) {
                localDeepCopy = new ArrayList<Lane>(sortedLane);
                //  mutex.notify();
                //  Log.d("motex","sent");
            }

        }
        return localDeepCopy;
    }

    public ArrayList<Obstacles> getObstaclesObject()
    { ArrayList<Obstacles> localDeepCopy = null;
        synchronized (mutex) {
            if (obstaclesObject != null){
                localDeepCopy = new ArrayList<Obstacles>(obstaclesObject);
               // Log.d("deepcopy timestamp",localDeepCopy.size()+" "+ obstaclesObject.size());
            }
        }
        return localDeepCopy;
    }

    public void parseJsonMotionRequestString(String response)  {

        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject(response);
            laneObject.clear();
            sortedLane.clear();
            if (!jsonObject.isNull("lane"))
            {

            statusObject.setLaneTimestamp(System.currentTimeMillis());

            JSONArray lane = jsonObject.getJSONArray("lane");
            //Log.d("laneObjectLength", lane.length() +"");
            for (int i = 0; i < lane.length(); i++)
            {
                JSONObject laneData = lane.getJSONObject(i);
                Lane laneDataObject = new Lane();

                if (laneData.get("type") != null)
                {
                    Log.d("type data",""+laneData.getInt("type"));
                    laneDataObject.setType(laneData.getInt("type"));
                }
                JSONArray pointsData = laneData.getJSONArray("points");
                if (pointsData != null)
                {

                    for (int j = 0; j < pointsData.length(); j++)
                    {
                        Point pointsDataObject = new Point();
                        JSONObject point = pointsData.getJSONObject(j);
                        pointsDataObject.setX(Double.valueOf(point.get("x").toString()));
                        pointsDataObject.setY(Double.valueOf(point.get("y").toString()));
                        pointsDataObject.setNx(Double.valueOf(point.get("nx").toString()));
                        pointsDataObject.setNy(Double.valueOf(point.get("ny").toString()));

                        laneDataObject.addPoint(pointsDataObject);
                    }
                    laneObject.add(laneDataObject);
                }
            }
            Log.d("sortedLane Size,",sortedLane.size()+" ");
        }
        LaneModel laneModel = new LaneModel();
        sortedLane = laneModel.sortLanes(laneObject);

//            //Obstacles
//
//            obstaclesObject.clear();
//            if(!jsonObject.isNull("obstacles"))
//            {
//                JSONArray obstacles = jsonObject.getJSONArray("obstacles");
//                // Log.d("obstacles timestamp",obstacles.length()+" ");
//                for (int i = 0; i < obstacles.length(); i++)
//                {
//                    JSONObject obstacleData = obstacles.getJSONObject(i);
//
//                    Obstacles obstaclesDataObject = new Obstacles();
//
//                    if(obstacleData.get("collidingObject") != null)
//                    {
//                        obstaclesDataObject.setIsColliding(Boolean.valueOf(obstacleData.get("collidingObject").toString()));
//                    }
//
//                    if(obstacleData.get("type") != null)
//                    {
//                        obstaclesDataObject.setType(Integer.valueOf(obstacleData.get("type").toString()));
//                    }
//
//                    JSONObject size = obstacleData.getJSONObject("size");
//
//                    if(size != null)
//                    {
//                        obstaclesDataObject.setSize(Double.valueOf(size.get("x").toString()),
//                                Double.valueOf(size.get("y").toString()),
//                                Double.valueOf(size.get("z").toString()));
//                    }
//
//                    JSONObject position = obstacleData.getJSONObject("position");
//
//                    if(position != null) {
//                        obstaclesDataObject.setPosition(Double.valueOf(position.get("x").toString()),
//                                Double.valueOf(position.get("y").toString()),
//                                Double.valueOf(position.get("z").toString()));
//                    }
//
//                    JSONObject velocity = obstacleData.getJSONObject("velocity");
//
//                    if(velocity != null) {
//                        obstaclesDataObject.setVelocity(Double.valueOf(velocity.get("vx").toString()),
//                                Double.valueOf(velocity.get("vy").toString()),
//                                Double.valueOf(velocity.get("vz").toString()));
//                    }
//
//
//                    JSONObject orientation = obstacleData.getJSONObject("orientation");
//                    if(orientation != null) {
//                        obstaclesDataObject.setOrientation(Double.valueOf(orientation.get("qx").toString()),
//                                Double.valueOf(orientation.get("qy").toString()),
//                                Double.valueOf(orientation.get("qz").toString()),
//                                Double.valueOf(orientation.get("qw").toString()));
//                    }
//
//                    obstaclesObject.add(obstaclesDataObject);
//                }
//                // Log.d("timestamp obobject ","Number od obstacles added :"+ obstaclesObject.size()+" ");
//            }

        }
        catch (JSONException e)
        {
            Log.d("json parser error",response);
            e.printStackTrace();
        }


    }
    public void parseJsonRequestString(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        statusObject.setPerceptionTimestamp(System.currentTimeMillis());
        //State
        if(!jsonObject.isNull("state"))
        {
            JSONObject state = jsonObject.getJSONObject("state");
            if (!state.isNull("mode") && !state.isNull("profile"))
            {
                stateObject.setMode(Integer.valueOf(state.get("mode").toString()));
                stateObject.setProfile(Integer.valueOf(state.get("profile").toString()));
            }
            else
            {
                Log.d("State Date : ", "null");
            }
        }

        //Ego

        if(!jsonObject.isNull("ego"))
        {

            JSONObject self = jsonObject.getJSONObject("ego");

            if (!self.isNull("brakePedal") && !self.isNull("throttlePedal") &&
                    !self.isNull("steeringWheel"))
            {
                egoObject.setBrakePedal(Double.valueOf((self.get("brakePedal")).toString()));
                egoObject.setThrottlePedal(Double.valueOf((self.get("throttlePedal")).toString()));
                egoObject.setSteeringWheel(Double.valueOf((self.get("steeringWheel")).toString()));
            }
            else
            {
                Log.d("Self : ", "No brake pedal, throttle pedal and steering wheel data");
            }
            JSONObject accelerate = self.getJSONObject("accelerate");

            if (accelerate != null)
            {
                egoObject.setAccelerate(Double.valueOf(accelerate.get("ax").toString()),
                        Double.valueOf(accelerate.get("ay").toString()),
                        Double.valueOf(accelerate.get("az").toString()));
            }
            else
            {
                Log.d("Ego -> Accelerate", "not set");
            }
            JSONObject speed = self.getJSONObject("speed");
            // Log.d("speed Data timestamp","check");
            if(speed != null)
            {
                egoObject.setSpeed(Double.valueOf(speed.get("vx").toString()),
                        Double.valueOf(speed.get("vy").toString()),
                        Double.valueOf(speed.get("vz").toString()));
            }
            else
            {
                Log.d("Ego -> Accelerate", "not set");
            }
        }
        else
        {
            Log.d("Ego : ", "null");
        }

        //Sensor

        sensorObject.clear();

        if(!jsonObject.isNull("sensor"))
        {
            JSONArray sensor = jsonObject.getJSONArray("sensor");
            for (int i = 0; i < sensor.length(); i++)
            {
                JSONObject sensorData = sensor.getJSONObject(i);
                Sensor sensorDataObject = new Sensor();

                if(sensorData.get("id") != null &&
                        sensorData.get("name") != null &&
                        sensorData.get("status") != null)
                {
                    sensorDataObject.setId(Integer.valueOf(sensorData.get("id").toString()));
                    sensorDataObject.setName(sensorData.get("name").toString());
                    sensorDataObject.setStatus(Integer.valueOf(sensorData.get("status").toString()));
                }

                if(sensorData.get("position") != null)
                {
                    JSONObject sensorPosition = sensorData.getJSONObject("position");

                    sensorDataObject.setPosition(sensorPosition.getDouble("x"),
                            sensorPosition.getDouble("y"),
                            sensorPosition.getDouble("z"));
                }

                if(sensorData.get("orientation") != null)
                {
                    JSONObject sensorOrientation = sensorData.getJSONObject("orientation");

                    sensorDataObject.setOrientation(sensorOrientation.getDouble("qw"),
                            sensorOrientation.getDouble("qx"),
                            sensorOrientation.getDouble("qy"),
                            sensorOrientation.getDouble("qz"));
                }

                JSONArray fieldOfView = sensorData.getJSONArray("fieldOfView");
                Log.d("sensor data", fieldOfView.toString());
                if(fieldOfView != null)
                {
                    for (int j = 0; j < fieldOfView.length(); j++)
                    {
                        JSONObject fov = fieldOfView.getJSONObject(i);
                        JSONObject as = fov.getJSONObject("angleSpan");
                        AngleSpan angleSpan = new AngleSpan();

                        if(as != null)
                        {
                            angleSpan.setAngleMax(Double.valueOf(as.get("angleMax").toString()));
                            angleSpan.setAngleMin(Double.valueOf(as.get("angleMin").toString()));
                        }

                        LengthSpan lengthSpan = new LengthSpan();
                        JSONObject ls = fov.getJSONObject("lengthSpan");
                        if(ls != null)
                        {
                            lengthSpan.setlengthH(Double.valueOf(ls.get("lengthMax").toString()));
                            lengthSpan.setlengthL(Double.valueOf(ls.get("lengthMin").toString()));
                        }

                        FieldOfView fieldOfViewObject = new FieldOfView(angleSpan, lengthSpan);
                        sensorDataObject.addFieldOfView(fieldOfViewObject);
                    }
                    sensorObject.add(sensorDataObject);
                    Log.d("Sensor data123 : ", sensorDataObject.getFieldOfViews().get(0).getAngleSpan().getAngleMax() + "");
                }
            }
        }
        else
        {
            Log.d("Sensor data : ", "null");
        }

        //Obstacles

        obstaclesObject.clear();
        if(!jsonObject.isNull("obstacles"))
        {

            JSONArray obstacles = jsonObject.getJSONArray("obstacles");
            // Log.d("obstacles timestamp",obstacles.length()+" ");
            for (int i = 0; i < obstacles.length(); i++)
            {
               // Log.d("obstacle entered",obstacles.toString());
                JSONObject obstacleData = obstacles.getJSONObject(i);

                Obstacles obstaclesDataObject = new Obstacles();
                try
                {
                    if(obstacleData.get("collidingObject") != null)
                    {
                            obstaclesDataObject.setIsColliding(Boolean.valueOf(obstacleData.get("collidingObject").toString()));

                    }
                }
                catch (JSONException E)
                {
                    Log.d("Missing Field","collidingObject"+E.toString());
                }

                if(obstacleData.get("type") != null)
                {
                    obstaclesDataObject.setType(Integer.valueOf(obstacleData.get("type").toString()));
                }

                JSONObject size = obstacleData.getJSONObject("size");

                if(size != null)
                {
                    obstaclesDataObject.setSize(Double.valueOf(size.get("x").toString()),
                            Double.valueOf(size.get("y").toString()),
                            Double.valueOf(size.get("z").toString()));
                }

                JSONObject position = obstacleData.getJSONObject("position");

                if(position != null) {
                    obstaclesDataObject.setPosition(Double.valueOf(position.get("x").toString()),
                            Double.valueOf(position.get("y").toString()),
                            Double.valueOf(position.get("z").toString()));
                }

                JSONObject velocity = obstacleData.getJSONObject("velocity");

                if(velocity != null) {
                    obstaclesDataObject.setVelocity(Double.valueOf(velocity.get("vx").toString()),
                            Double.valueOf(velocity.get("vy").toString()),
                            Double.valueOf(velocity.get("vz").toString()));
                }


                JSONObject orientation = obstacleData.getJSONObject("orientation");
                if(orientation != null) {
                    obstaclesDataObject.setOrientation(Double.valueOf(orientation.get("qx").toString()),
                            Double.valueOf(orientation.get("qy").toString()),
                            Double.valueOf(orientation.get("qz").toString()),
                            Double.valueOf(orientation.get("qw").toString()));
                }

                obstaclesObject.add(obstaclesDataObject);

            }
            // Log.d("timestamp obobject ","Number od obstacles added :"+ obstaclesObject.size()+" ");
        }

    }


    void parseControlString(String str) throws JSONException
    {
        final String IS_AUTOPILOT_ON = "1";
        final String IS_AUTOPILOT_READY = "2";
        final String IS_AUTOPILOT_OFF = "3";
        final String IS_AUTOPILOT_NOT_READY = "4";

        statusObject.setControlTimestamp(System.currentTimeMillis());
        if(str.equals(IS_AUTOPILOT_ON))
        {
            Log.d("Autopilot", "On");
            controlMessageObject.setIsAutoDrivingOn(true);
        }
        else if(str.equals(IS_AUTOPILOT_READY))
        {
            Log.d("Autopilot", "Ready");

            controlMessageObject.setIsAutoDrivingReady(true);


        }
        else if(str.equals(IS_AUTOPILOT_OFF))
        {
            Log.d("Autopilot", "Off");
            controlMessageObject.setIsAutoDrivingOn(false);
        }
        else if(str.equals(IS_AUTOPILOT_NOT_READY))
        {
            Log.d("Autopilot", "Not Ready");
            controlMessageObject.setIsAutoDrivingReady(false);

        }
    }
}


