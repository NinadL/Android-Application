package com.telenav.autopilotcontrol.app.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telenav.autopilotcontrol.R;
import com.telenav.autopilotcontrol.app.car_data.DataParser;
import com.telenav.autopilotcontrol.app.car_data.setStatusClass;
import com.telenav.autopilotcontrol.app.speech.SpeechStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AnimateFragment extends Fragment {

    Button rightDial;
    ProgressBar leftDial;
    RelativeLayout leftDialLayout;
    TextView digitalSpeed;
    LinearLayout accelerateBrakeWidget;
    LinearLayout setSpeed;


    ProgressBar accelerationBar;
    ProgressBar brakeBar;
    ImageView statusSymbol;
    Button increaseSpeed;
    Button decreaseSpeed;
    Button mbtSpeak;
    Button takeControl;
    ColorStateList colorStateListGreen;
    ColorStateList colorStateListRed;
    RadioButton onHighwayRadioButton;
    RadioButton leftLaneVisibleRadioButton;
    RadioButton rightButtonVisibleRadioButton;
    RadioButton areWeOnCentreLaneRadioButton;
    RadioButton distanceRemainingMoreThan3;
    public static boolean allNodesWorking = false;
    TextView errorTextBox;
    TextView speedLimit;
    String errorString = "";

    AutopilotBrainData autopilotBrainData = new AutopilotBrainData();

    Timer myTimer1;
    Timer timer = new Timer();
    boolean isTimerStarted = false;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    //MyMapRenderingUtils myMapRenderingUtils = new MyMapRenderingUtils();

    boolean isAutopilotEnabled = false;
    boolean isAutopilotOnFromNetwork = false;
    boolean isRemainingDistanceGreaterThan3Miles = false;

    boolean areWeOnHighway = false;
    boolean isHighwayNext = false;


    boolean isPerceptionWorking = false;
    boolean isControlWorking = false;
    boolean isLaneObjectWorking = false;
    boolean isMapWorking = false;
    boolean areNodesWorking = false;

    boolean isMsgPlayed = false;

    boolean isAutoPilotReadyToBeEnabled = false;

    SpeechStrings speechStrings;


    boolean noNetworkMessagePlayed = false;
    boolean noNodesWorkingMessagePlayed = false;
    boolean autopilotAvailableMessagePlayed = false;
    boolean engageAutopilotMessagePlayed = false;
    boolean autopilotEngagedMessagePlayed = false;
    boolean waitingForDataMessagePlayed = false;
    boolean userControllingMessagePLayed = false;
    boolean isEmergency = false;
    boolean isRequestSent = false;
    boolean highwayMsgSpoken = false;

    //boolean = false;
    private setStatusClass statusObject = new setStatusClass();

    public AnimateFragment() {
    }

    public static AnimateFragment newInstance(Context context) {
        AnimateFragment fragment = new AnimateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_animate, container, false);
        speechStrings = ((MainActivity)getActivity()).speechStrings;
        initScreen(view);

        final Timer myTimer3 = new Timer("MyTimer3", true);
        myTimer3.scheduleAtFixedRate(new checkNodes(), 0, 1000);

        final Timer myTimer2 = new Timer("MyTimer2", true);
        myTimer2.scheduleAtFixedRate(new StartAutoPilotScreen(), 0, 1000);

        rightDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isAutoPilotReadyToBeEnabled) {
                    ((MainActivity) getActivity()).sendControl(1);
                    isAutopilotEnabled = true;
                    Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(1000);
                }
                else
                {
                    announceErrorMessage();
                }
            }
        });

        increaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSpeak = "Speed Increased";
                MainActivity myActivity = (MainActivity) getActivity();
                myActivity.speakOverlap(textToSpeak);

                ((MainActivity) getActivity()).sendControl(2);
            }
        });

        decreaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSpeak = "Speed Decreased";
                MainActivity myActivity = (MainActivity) getActivity();
                myActivity.speakOverlap(textToSpeak);
                ((MainActivity) getActivity()).sendControl(3);
            }
        });
        takeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).sendControl(1);
                isAutopilotEnabled = true;
            }
        });

        mbtSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(view);
            }
        });


        return view;
    }

    class checkNodes extends TimerTask {

        @Override
        public void run() {

            DataParser dataParser = new DataParser();

            if (getActivity() != null)
            {
                if ( statusObject.gettingPerceptionData() && statusObject.gettingControlData() && statusObject.gettingLaneData() && statusObject.gettingMapData() ) {

                    Log.d("checkNodes","true");
                    areNodesWorking = true;
                    isPerceptionWorking = true;
                    isMapWorking = true;
                    isControlWorking = true;
                    isLaneObjectWorking = true;
                } else {
                    Log.d("checkNodes","DataParser.perceptionTimestamp: "+statusObject.gettingPerceptionData()+" DataParser.mapTimestamp:"+statusObject.gettingMapData()+" DataParser.controlTimestamp: "+statusObject.gettingControlData()+" "+statusObject.gettingLaneData());
                    areNodesWorking = false;
                    if (!statusObject.gettingPerceptionData()) {
                        isPerceptionWorking = false;
                    }
                    else {
                        isPerceptionWorking = true;
                    }
                    if (!statusObject.gettingMapData() ) {
                        isMapWorking = false;
                    }
                    else {
                        isMapWorking = true;
                    }
                    if (!statusObject.gettingControlData() ) {

                        isControlWorking = false;
                    }
                    else {
                        isControlWorking = true;
                    }
                    if (!statusObject.gettingLaneData() ) {
                        isLaneObjectWorking = false;
                    }
                    else  {
                        isLaneObjectWorking = true;
                    }

                }
            }
        }
    }

    private void initScreen(View view) {
        rightDial = (Button) view.findViewById(R.id.rightDial);
        leftDial = (ProgressBar) view.findViewById(R.id.leftDial);
        leftDialLayout = (RelativeLayout) view.findViewById(R.id.leftDialLayout);
        digitalSpeed = (TextView) view.findViewById(R.id.digital_speed);
        setSpeed = (LinearLayout) view.findViewById(R.id.set_speed);
        accelerateBrakeWidget = (LinearLayout) view.findViewById(R.id.accelerate_brake_widget);
        accelerationBar = (ProgressBar) view.findViewById(R.id.vertical_progressbar_acc);
        brakeBar = (ProgressBar) view.findViewById(R.id.vertical_progressbar_brake);
        increaseSpeed = (Button) view.findViewById(R.id.adjust_speed_increase);
        decreaseSpeed = (Button) view.findViewById(R.id.adjust_speed_decrease);
        statusSymbol = (ImageView) view.findViewById(R.id.status_symbol);
        mbtSpeak = (Button) view.findViewById(R.id.btSpeak);
        takeControl = (Button) view.findViewById(R.id.takeControl);
        onHighwayRadioButton = (RadioButton) view.findViewById(R.id.on_highway);
        leftLaneVisibleRadioButton = (RadioButton) view.findViewById(R.id.left_lane_visibility);
        rightButtonVisibleRadioButton = (RadioButton) view.findViewById(R.id.right_lane_visibility);
        areWeOnCentreLaneRadioButton = (RadioButton) view.findViewById(R.id.on_centre_lane);
        distanceRemainingMoreThan3 = (RadioButton) view.findViewById(R.id.distance_left_more_than_3);
        errorTextBox = (TextView) view.findViewById(R.id.error_text_box);
        speedLimit = (TextView) view.findViewById(R.id.speed_limit);



        rightDial.setBackgroundResource(R.drawable.steering_wheel_gray);
        statusSymbol.setImageResource(R.drawable.autopilot_off);

        colorStateListGreen = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.green) }
        );

        colorStateListRed = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.red) }
        );

        checkVoiceRecognition();
        loadFullScreen();
    }

    public void checkVoiceRecognition() {

        // Check if voice recognition is present

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            mbtSpeak.setText("Voice recognizer not present");
            Toast.makeText(getActivity(), "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void speak(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            if (resultCode == -1) {
                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                Log.d("TextMatchList", String.valueOf(textMatchList));
                isMsgPlayed = false;
                for (int i = 0; i < textMatchList.size(); i++) {
                    String voiceString = textMatchList.get(i);

                    if (voiceString.contains("enable") || voiceString.contains("autopilot"))
                    {
                        if(isAutoPilotReadyToBeEnabled && !isRequestSent)
                        {
                            ((MainActivity) getActivity()).sendControl(1);
                            isAutopilotEnabled = true;
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(1000);
                            isRequestSent = true;
                            isMsgPlayed = false;
                        }
                        else if(!isMsgPlayed)
                        {
                            announceErrorMessage();
                            isRequestSent = false;
                            isMsgPlayed = true;
                        }
                        showToastMessage("Enable autopilot");
                    }
                }
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


    void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void loadHalfScreen(View view) {

        ViewGroup.LayoutParams paramsRightButton = rightDial.getLayoutParams();
        paramsRightButton.width = 300;
        paramsRightButton.height = 300;

        rightDial.setLayoutParams(paramsRightButton);

        ViewGroup.LayoutParams paramsLeftButtonLayout = leftDialLayout.getLayoutParams();
        paramsLeftButtonLayout.width = 300;
        paramsLeftButtonLayout.height = 300;

        leftDialLayout.setLayoutParams(paramsLeftButtonLayout);

        ViewGroup.LayoutParams paramsLeftButton = leftDial.getLayoutParams();
        paramsLeftButton.width = 300;
        paramsLeftButton.height = 300;

        leftDial.setLayoutParams(paramsLeftButton);

        ViewGroup.LayoutParams accelerateBrakeWidgetLayout = accelerateBrakeWidget.getLayoutParams();
        accelerateBrakeWidgetLayout.width = 25;
        accelerateBrakeWidgetLayout.height = 300;

        accelerateBrakeWidget.setLayoutParams(accelerateBrakeWidgetLayout);

        ViewGroup.LayoutParams setSpeedLayout = setSpeed.getLayoutParams();
        setSpeedLayout.width = 60;
        setSpeedLayout.height = 300;

        setSpeed.setLayoutParams(setSpeedLayout);

        digitalSpeed.setTextSize(20);

        rightDial.setTextSize(18);

    }

    public void loadFullScreen() {
        rightDial.setBackgroundResource(R.drawable.steering_wheel_gray_gray);
        rightDial.setTextSize(30);

        ViewGroup.LayoutParams paramsRightButton = rightDial.getLayoutParams();
        paramsRightButton.width = 500;
        paramsRightButton.height = 500;

        rightDial.setLayoutParams(paramsRightButton);
        rightDial.setTextSize(30);


        ViewGroup.LayoutParams paramsLeftButtonLayout = leftDialLayout.getLayoutParams();
        paramsLeftButtonLayout.width = 500;
        paramsLeftButtonLayout.height = 500;

        leftDialLayout.setLayoutParams(paramsLeftButtonLayout);
        ViewGroup.LayoutParams paramsLeftButton = leftDial.getLayoutParams();
        paramsLeftButton.width = 500;
        paramsLeftButton.height = 500;

        leftDial.setLayoutParams(paramsLeftButton);

        ViewGroup.LayoutParams accelerateBrakeWidgetLayout = accelerateBrakeWidget.getLayoutParams();
        accelerateBrakeWidgetLayout.width = 45;
        accelerateBrakeWidgetLayout.height = 500;

        accelerateBrakeWidget.setLayoutParams(accelerateBrakeWidgetLayout);

        ViewGroup.LayoutParams setSpeedLayout = setSpeed.getLayoutParams();
        setSpeedLayout.width = 75;
        setSpeedLayout.height = 500;

        setSpeed.setLayoutParams(setSpeedLayout);

        digitalSpeed.setTextSize(35);
    }


    class StartAutoPilotScreen extends TimerTask {
        //new Handler().postDelayed(new Runnable()

        @Override
        public void run() {

            if (getView()!= null && getView().getHandler() != null) {
                getView().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        getView().setVisibility(View.VISIBLE);
                    }

                });
            }
            if(!statusObject.gettingPerceptionData()){

            }

            if((( (MainActivity)getActivity()).areWeOnStartScreen && MainActivity.autoswap == false)||(MainActivity.autoswap == true && statusObject.gettingMapData() == false) && areWeOnCentreLaneRadioButton!=null )
            {
                if( areWeOnCentreLaneRadioButton.getVisibility() != View.INVISIBLE && areWeOnCentreLaneRadioButton.getHandler()!=null)
                {
                    areWeOnCentreLaneRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            areWeOnCentreLaneRadioButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    onHighwayRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            onHighwayRadioButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    leftLaneVisibleRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            leftLaneVisibleRadioButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    rightButtonVisibleRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            rightButtonVisibleRadioButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    distanceRemainingMoreThan3.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            distanceRemainingMoreThan3.setVisibility(View.INVISIBLE);
                        }
                    });

                    statusSymbol.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            statusSymbol.setVisibility(View.INVISIBLE);
                        }
                    });

                    mbtSpeak.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mbtSpeak.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
            else
            {
                if(areWeOnCentreLaneRadioButton.getVisibility() != View.VISIBLE)
                {
                    areWeOnCentreLaneRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            areWeOnCentreLaneRadioButton.setVisibility(View.VISIBLE);
                        }
                    });

                    onHighwayRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            onHighwayRadioButton.setVisibility(View.VISIBLE);
                        }
                    });

                    leftLaneVisibleRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            leftLaneVisibleRadioButton.setVisibility(View.VISIBLE);
                        }
                    });

                    distanceRemainingMoreThan3.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            distanceRemainingMoreThan3.setVisibility(View.VISIBLE);
                        }
                    });

                    rightButtonVisibleRadioButton.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            rightButtonVisibleRadioButton.setVisibility(View.VISIBLE);
                        }
                    });

//                    if(allNodesWorking)
                    {
                        statusSymbol.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                statusSymbol.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    mbtSpeak.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mbtSpeak.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            if (DataParser.autopilotBrainData != null && DataParser.autopilotBrainData.getIsNavigationStarted()== true && !isTimerStarted )
            {
                myTimer1 = new Timer("MyTimer1", true);
                myTimer1.scheduleAtFixedRate(new Distance(), 100, 1000);
                isTimerStarted = true;
            } else if( DataParser.autopilotBrainData == null ||  DataParser.autopilotBrainData !=null && DataParser.autopilotBrainData.getIsNavigationStarted() != true ) {
                {
                    isTimerStarted = false;
                    Log.d("isNavigationStarted", (String.valueOf(DataParser.autopilotBrainData != null && DataParser.autopilotBrainData.getIsNavigationStarted() != true) + " " + isTimerStarted));
                }
            }
        }

        // }
    }
    class Distance extends TimerTask {

        @Override
        public void run() {
//            final double remainingDistance = convertMetersToMiles(autopilotBrain.getDistance());
//            boolean isHighwayNext = autopilotBrain.isHighwayNext;
//            Log.d("remainingDistance", String.valueOf(remainingDistance) + " " + String.valueOf(isHighwayNext));
            if(rightDial != null) {
                rightDial.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        rightDial.setRotation(autopilotBrainData.getSteeringWheelPosition());
                    }
                });
            }


            try {
                setRightDial();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            leftDial.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(statusObject.gettingPerceptionData()) {
                        leftDial.setProgress(autopilotBrainData.getSpeed());
                    }
                    else
                    {
                        leftDial.setProgress(0);
                    }
                }
            });
            digitalSpeed.getHandler().post(new Runnable() {
                @Override
                public void run() {

                    String currentSpeed = "-";
                    String speedLimit = "  --  ";
                    String max = " MAX ";


                    if (autopilotBrainData.getSpeed() != -1 && statusObject.gettingPerceptionData())
                    {
                        currentSpeed = String.valueOf(autopilotBrainData.getSpeed());
                    }
                    SpannableString currentSpeedSpannableString = new SpannableString(currentSpeed);
                    currentSpeedSpannableString.setSpan(new RelativeSizeSpan(1.7f), 0, currentSpeed.length(), 0);


                    if(autopilotBrainData != null && autopilotBrainData.getSpeedLimit() > 0 && (Integer.valueOf(currentSpeed) > (autopilotBrainData.getSpeedLimit() + 5)))
                    {

                        currentSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, currentSpeed.length(), 0);
                    }
                    else if(autopilotBrainData != null && autopilotBrainData.getSpeedLimit() > 0 && (Integer.valueOf(currentSpeed) > (autopilotBrainData.getSpeedLimit())) && (Integer.valueOf(currentSpeed) < (autopilotBrainData.getSpeedLimit()) + 5))
                    {
                        currentSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, currentSpeed.length(), 0);
                    }
                    else {
                        currentSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentSpeed.length(), 0);
                    }

                    SpannableString speedLimitSpannableString = new SpannableString(speedLimit);
                    speedLimitSpannableString.setSpan(new RelativeSizeSpan(0.5f), 0, speedLimit.length(), 0);
                    speedLimitSpannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, speedLimit.length(), 0);
                    speedLimitSpannableString.setSpan(new BackgroundColorSpan(Color.WHITE), 0, speedLimit.length(), 0);

                    SpannableString maxSpeedSpannableString = new SpannableString(max);
                    maxSpeedSpannableString.setSpan(new RelativeSizeSpan(0.3f), 0, max.length(), 0);
                    maxSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, max.length(), 0);
                    maxSpeedSpannableString.setSpan(new BackgroundColorSpan(Color.WHITE), 0, max.length(), 0);

                    CharSequence finalString = TextUtils.concat(currentSpeedSpannableString, "\n");

                    digitalSpeed.setText(finalString);
                    Log.d("Digital Speed", "Setting");
                }
            });

            if (autopilotBrainData.getSpeedLimit() != -1 && statusObject.gettingMapData()) {
                speedLimit.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        speedLimit.setText(String.valueOf(DataParser.autopilotBrainData.getSpeedLimit()));
                    }
                });
            }


            accelerationBar.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (statusObject.gettingPerceptionData()) {
                        DataParser dataParser = new DataParser();
                        Log.d("throttle Pedal", String.valueOf((int) dataParser.getSelfObject().getThrottlePedal() * 10));
                        accelerationBar.setProgress((int) dataParser.getSelfObject().getThrottlePedal() * 100);
                    }
                }
            });

            brakeBar.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (statusObject.gettingPerceptionData()) {
                        DataParser dataParser = new DataParser();
                        brakeBar.setProgress((int) dataParser.getSelfObject().getBrakePedal() * 100);
                    }
                }
            });
        }
    }

    void setRightDial() throws InterruptedException
    {

        if (DataParser.autopilotBrainData != null) {
            final double remainingDistance = convertMetersToMiles(DataParser.autopilotBrainData.getRemainingDistance());


            distanceRemainingMoreThan3.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (remainingDistance > 3) {
                        distanceRemainingMoreThan3.setButtonTintList(colorStateListGreen);
                    }
                    else
                    {
                        distanceRemainingMoreThan3.setButtonTintList(colorStateListRed);
                    }
                }
            });
            if (remainingDistance > 3) {
                isRemainingDistanceGreaterThan3Miles = true;
            } else {
                isRemainingDistanceGreaterThan3Miles = false;
            }
            isHighwayNext = DataParser.autopilotBrainData.getIsHighwayNext();
            areWeOnHighway = DataParser.autopilotBrainData.getAreWeOnHighway();

            onHighwayRadioButton.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(areWeOnHighway) {
                        onHighwayRadioButton.setButtonTintList(colorStateListGreen);
                    }
                    else
                    {
                        onHighwayRadioButton.setButtonTintList(colorStateListRed);
                    }
                }
            });

            leftLaneVisibleRadioButton.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(statusObject.isLeftLaneVisible()) {
                        leftLaneVisibleRadioButton.setButtonTintList(colorStateListGreen);
                    }
                    else
                    {
                        leftLaneVisibleRadioButton.setButtonTintList(colorStateListRed);
                    }
                }
            });

            rightButtonVisibleRadioButton.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(statusObject.isRightLaneVisible()) {
                        rightButtonVisibleRadioButton.setButtonTintList(colorStateListGreen);
                    }
                    else
                    {
                        rightButtonVisibleRadioButton.setButtonTintList(colorStateListRed);
                    }
                }
            });

            areWeOnCentreLaneRadioButton.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(statusObject.areWeOnCentreLane()) {
                        areWeOnCentreLaneRadioButton.setButtonTintList(colorStateListGreen);
                    }
                    else
                    {
                        areWeOnCentreLaneRadioButton.setButtonTintList(colorStateListRed);
                    }
                }
            });




            DataParser dataParser = new DataParser();
            isAutopilotOnFromNetwork = dataParser.getControlMessageObject().getIsAutoDrivingOn();


            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            String textToSpeak;
            MainActivity myActivity = (MainActivity) getActivity();


            //No Network
            if (activeNetwork == null)
            {
                noNetwork();
                errorTextBox.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        errorTextBox.setVisibility(View.INVISIBLE);
                    }
                });
            }
            //No nodes connected - Perception, Control, Maps, Motion Planning
            else if (!areNodesWorking)
            {
                noNodes();
            }
            //Autopilot available in (We are not on highway yet and car is not on autopilot mode)
            else if (isHighwayNext &&
                    !isAutopilotOnFromNetwork)
            {
                isAutopilotEnabled = false;
                isAutoPilotReadyToBeEnabled = false;
                rightDial.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_gray_gray)) {
                            rightDial.setBackgroundResource(R.drawable.steering_wheel_gray_gray);
                        }
                        rightDial.setText("Autopilot\navailable\nin " + remainingDistance + " mi");
                    }
                });
                autopilotToBeEnableMessage(remainingDistance);

                errorTextBox.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        errorTextBox.setVisibility(View.INVISIBLE);
                    }
                });


            }
            //Autopilot ready to be enabled (We are on highway and the highway distance spans for more than 3 miles)
            else if (areWeOnHighway &&
                    !isAutopilotOnFromNetwork &&
                    isRemainingDistanceGreaterThan3Miles && statusObject.areWeOnCentreLane())
            {
                isAutopilotEnabled = false;
                rightDial.post(new Runnable() {
                    @Override
                    public void run() {
                        rightDial.setBackgroundResource(R.drawable.steering_wheel_blue);
                        rightDial.setText("  Engage\nAutopilot\n");
                        isAutoPilotReadyToBeEnabled = true;
                    }
                });
                enableAutoPilotMessage();
                errorTextBox.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        errorTextBox.setVisibility(View.INVISIBLE);
                    }
                });


            }
            //Autopilot has been enabled and we are on highway
            else if (isAutopilotOnFromNetwork &&
                    areWeOnHighway)
            {
                isAutopilotEnabled = true;
                autopilotEngagedMessage();

                if (remainingDistance > 3)
                {
                    distanceGreaterThanThreeMiles(remainingDistance);

                } else
                {
                    distanceLessThanThreeMiles(remainingDistance);
                }
                errorTextBox.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        errorTextBox.setVisibility(View.INVISIBLE);
                    }
                });
            }
            //If none of the above conditions are true, we are waiting for data
            else
            {
                waitForData();
                errorTextBox.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        errorTextBox.setVisibility(View.INVISIBLE);
                    }
                });
            }

            //Update status symbol
            updateStatusSymbol();

            //What to do during emergency
            checkForEmergency();


            if(areWeOnHighway && !highwayMsgSpoken)
            {
                textToSpeak = "We are on highway. Please move to centre lane.";
                myActivity.speak(textToSpeak);
                highwayMsgSpoken = true;
            }
            else if(!areWeOnHighway)
            {
                highwayMsgSpoken = false;
            }
        }
    }

    public double convertMetersToMiles(double meters) {
        double miles = meters * 0.000621371;
        return Double.valueOf(Math.round((float) miles * 10.0) / 10.0);
    }

    public void announceErrorMessage()
    {
        String textToSpeak;
        MainActivity myActivity = (MainActivity) getActivity();
        textToSpeak = "Cannot enable autopilot now because ";
        myActivity.speak(textToSpeak);

        if(!isPerceptionWorking)
        {
            textToSpeak = "Sensor data is not received.";
            myActivity.speak(textToSpeak);
        }
        if(!isControlWorking)
        {
            textToSpeak = "Car data is not received.";
            myActivity.speak(textToSpeak);
        }
        if(!isMapWorking)
        {
            textToSpeak = "Map data is not received";
            myActivity.speak(textToSpeak);
        }
        if(!isLaneObjectWorking)
        {
            textToSpeak = "Lane data is not received";
            myActivity.speak(textToSpeak);
        }
        if(!areWeOnHighway)
        {
            textToSpeak = "We are not on highway";
            myActivity.speak(textToSpeak);
        }
        if(!statusObject.areWeOnCentreLane())
        {
            textToSpeak = "We are not on the centre lane.";
            myActivity.speak(textToSpeak);
        }
        if(!isRemainingDistanceGreaterThan3Miles){
            textToSpeak = "Distance less than 3 miles.";
            myActivity.speak(textToSpeak);
        }
    }

    void noNetwork() throws InterruptedException {
        rightDial.post(new Runnable() {
            @Override
            public void run() {
                if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_gray_gray)) {
                    rightDial.setBackgroundResource(R.drawable.steering_wheel_gray_gray);
                }
                rightDial.setText("No\nNetwork");
            }
        });

        if (isAutopilotOnFromNetwork)
        {
            speechStrings.playAdviceInEnglish(speechStrings.NO_NETWORK_EMERGENCY);
            for (Map.Entry<String, Boolean> entry : speechStrings.messagePlayed.entrySet()) {
                if (!entry.getKey().equals(speechStrings.NO_NETWORK_EMERGENCY)) {
                    entry.setValue(false);
                }
            }
        } else
        {
            speechStrings.playAdviceInHindi(speechStrings.NO_NETWORK_NON_EMERGENCY);
            for (Map.Entry<String, Boolean> entry : speechStrings.messagePlayed.entrySet()) {
                if (!entry.getKey().equals(speechStrings.NO_NETWORK_NON_EMERGENCY)) {
                    entry.setValue(false);
                }
            }
        }
        isAutoPilotReadyToBeEnabled = false;
    }

    void noNodes() throws InterruptedException
    {
        errorString = "";
        if (isAutopilotOnFromNetwork) {
            if(!isPerceptionWorking)
            {
                errorString = errorString + "Perception data not received\n";
//                speechStrings.playAdviceInEnglish(speechStrings.PERCEPTION_NOT_WORKING_EMERGENCY);
            }
            if(!isControlWorking)
            {
                errorString = errorString + "Control data not received\n";
//                speechStrings.playAdviceInEnglish(speechStrings.CONTROL_NOT_WORKING_EMERGENCY);
            }
            if(!isMapWorking)
            {
                errorString = errorString + "Map data not received\n";
//                speechStrings.playAdviceInEnglish(speechStrings.MAPS_NOT_WORKING_EMERGENCY);
            }
            if(!isLaneObjectWorking)
            {
                errorString = errorString + "Lane data not received\n";
//                speechStrings.playAdviceInEnglish(speechStrings.MOTION_PLANNING_NOT_WORKING_EMERGENCY);
            }
        } else
        {
            if(!isPerceptionWorking)
            {
                errorString = errorString + "Perception data not received\n";
                speechStrings.playAdviceInEnglish(speechStrings.PERCEPTION_NOT_WORKING_NON_EMERGENCY);
            }
            if(!isControlWorking)
            {
                errorString = errorString + "Control data not received\n";
                speechStrings.playAdviceInEnglish(speechStrings.CONTROL_NOT_WORKING_NON_EMERGENCY);
            }
            if(!isMapWorking)
            {
                errorString = errorString + "Map data not received\n";
                speechStrings.playAdviceInEnglish(speechStrings.MAPS_NOT_WORKING_NON_EMERGENCY);
            }
            if(!isLaneObjectWorking) {
                errorString = errorString + "Lane data not received\n";
                speechStrings.playAdviceInEnglish(speechStrings.MOTION_PLANNING_NOT_WORKING_NON_EMERGENCY);
            }
        }
        errorTextBox.getHandler().post(new Runnable() {
            @Override
            public void run() {
                errorTextBox.setVisibility(View.VISIBLE);
                errorTextBox.setText(errorString);
            }
        });
        rightDial.post(new Runnable() {
            @Override
            public void run() {
                if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_gray_gray)) {
                    rightDial.setBackgroundResource(R.drawable.steering_wheel_gray_gray);
                }
                rightDial.setText("Nodes\nnot\nWorking");
            }
        });
        isAutoPilotReadyToBeEnabled = false;


    }

    void autopilotToBeEnableMessage(double remainingDistance)
    {
        if (autopilotAvailableMessagePlayed == false)
        {
            ((MainActivity)getActivity()).speak("Autopilot available in " + remainingDistance + "miles");

            noNetworkMessagePlayed = false;
            noNodesWorkingMessagePlayed = false;
            autopilotAvailableMessagePlayed = true;
            engageAutopilotMessagePlayed = false;
            autopilotEngagedMessagePlayed = false;
            waitingForDataMessagePlayed = false;

            for (Map.Entry<String, Boolean> entry : speechStrings.messagePlayed.entrySet()) {
                if (!entry.getKey().equals(speechStrings.NO_NETWORK_NON_EMERGENCY)) {
                    entry.setValue(false);
                }
            }
        }
    }

    void waitForData()
    {
        rightDial.post(new Runnable() {
            @Override
            public void run() {
                if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_gray_gray)) {
                    rightDial.setBackgroundResource(R.drawable.steering_wheel_gray_gray);
                }
                isAutoPilotReadyToBeEnabled = false;
                rightDial.setText("Autopilot\nnot avbl");
            }
        });

        if (isAutopilotOnFromNetwork) {
            if (waitingForDataMessagePlayed == false) {
                ((MainActivity)getActivity()).speak("We are waiting for data. Please take back control immediately");

                noNetworkMessagePlayed = false;
                noNodesWorkingMessagePlayed = false;
                autopilotAvailableMessagePlayed = false;
                engageAutopilotMessagePlayed = false;
                autopilotEngagedMessagePlayed = false;
                waitingForDataMessagePlayed = true;
            }
        } else {
            if (waitingForDataMessagePlayed == false) {
                ((MainActivity)getActivity()).speak("We are waiting for data.");

                noNetworkMessagePlayed = false;
                noNodesWorkingMessagePlayed = false;
                autopilotAvailableMessagePlayed = false;
                engageAutopilotMessagePlayed = false;
                autopilotEngagedMessagePlayed = false;
                waitingForDataMessagePlayed = true;
            }
        }
    }

    void enableAutoPilotMessage()
    {
        if (engageAutopilotMessagePlayed == false) {
            ((MainActivity)getActivity()).speak("Engage Autopilot");

            noNetworkMessagePlayed = false;
            noNodesWorkingMessagePlayed = false;
            autopilotAvailableMessagePlayed = false;
            engageAutopilotMessagePlayed = true;
            autopilotEngagedMessagePlayed = false;
            waitingForDataMessagePlayed = false;
        }
    }

    void autopilotEngagedMessage()
    {
        if (autopilotEngagedMessagePlayed == false) {
            ((MainActivity)getActivity()).speak("Autopilot engaged");

            noNetworkMessagePlayed = false;
            noNodesWorkingMessagePlayed = false;
            autopilotAvailableMessagePlayed = false;
            engageAutopilotMessagePlayed = false;
            autopilotEngagedMessagePlayed = true;
            waitingForDataMessagePlayed = false;
        }
    }

    void distanceGreaterThanThreeMiles(double remainingDistance)
    {
        final double distance = remainingDistance;
        rightDial.post(new Runnable() {
            @Override
            public void run() {

                if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_gray)) {
                    rightDial.setBackgroundResource(R.drawable.steering_wheel_gray);
                }
                rightDial.setText(Html.fromHtml("<font size=\"12\" face=\"arial\" color=\"#01545A\">" + distance + "</font>" + "<br/>" +
                        "MI" + "\nto end"));
                isEmergency = false;
            }
        });
    }

    void distanceLessThanThreeMiles(double remainingDistance)
    {
        final double distance = remainingDistance;
        rightDial.post(new Runnable() {
            @Override
            public void run() {

                if (rightDial.getBackground() != getResources().getDrawable(R.drawable.steering_wheel_red)) {
                    rightDial.setBackgroundResource(R.drawable.steering_wheel_red);
                }
                rightDial.setText(Html.fromHtml("<font size=\"12\" face=\"arial\" color=\"#01545A\">" + distance + "</font>" + "<br/>" +
                        "MI" + "\nto end"));
            }
        });

        if (distance == 3) {
            String textToSpeak = "Autopilot section ending. Please take over the control";
            ((MainActivity)getActivity()).speak(textToSpeak);
        }
        if (distance == 2) {
            String textToSpeak = "Autopilot section ending soon. Please take over the control";
            ((MainActivity)getActivity()).speak(textToSpeak);
        }
        if (distance == 1) {
            isEmergency = true;
            String textToSpeak = "Please take back the control. Autopilot section has finished";
            ((MainActivity)getActivity()).speak(textToSpeak);
        }
        if (distance == 0.5) {
            isEmergency = true;
            String textToSpeak = "Emergency procedure has started. Please take back the control";
            ((MainActivity)getActivity()).speak(textToSpeak);
        }
    }

    void updateStatusSymbol()
    {
        if (isAutopilotOnFromNetwork) {
            statusSymbol.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    statusSymbol.setImageResource(R.drawable.autopilot_on);
                }
            });
        } else {
            statusSymbol.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    statusSymbol.setImageResource(R.drawable.autopilot_off);
                }
            });
        }
    }

    void checkForEmergency()
    {
        if(isEmergency && isAutopilotOnFromNetwork)
        {
            int delay = 1000; // delay for 1 sec.
            int period = 10000; // repeat every 10 sec.

            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {
                    ((MainActivity) getActivity()).speak("Please take back control");
                    speechStrings.playAdviceInChinese(speechStrings.EMERGENCY_IN_CHINESE);
                    speechStrings.playAdviceInSpanish(speechStrings.EMERGENCY_IN_SPANISH);
                    speechStrings.playAdviceInHindi(speechStrings.EMERGENCY_IN_HINDI);  // display the data
                }
            }, delay, period);
        }
        else
        {
            timer.purge();
            timer.cancel();
        }
    }

}