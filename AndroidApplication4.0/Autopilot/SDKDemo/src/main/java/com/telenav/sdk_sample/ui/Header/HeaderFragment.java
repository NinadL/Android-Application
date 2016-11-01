package com.telenav.sdk_sample.ui.Header;

import android.app.Fragment;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telenav.sdk.navigation.controller.NavigationManager;
import com.telenav.sdk.navigation.model.NavigationData;
import com.telenav.sdk.navigation.model.SpeedLimit;
import com.telenav.sdk.navigation.model.TravellingEdgeInfo;
import com.telenav.sdk.navigation.model.TurnType;
import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.ui.map.MapActivity;

public class HeaderFragment extends Fragment
{
    LinearLayout mapsInformationLayout;
    LinearLayout speedometerLayout;
    LinearLayout autopilotStatusLayout;
    ImageView turnIcon;
    TextView turnDistance;
    TextView nextStreetName;
    TextView speedLimitNormal;
    TextView speedLimitAutopilot;
    TextView referenceSpeedAutopilot;
    ProgressBar analogSpeed;
    TextView digitalSpeed;
    ImageView autopilotStatusImage;
    TextView autopilotTextStatus;
    ImageButton increaseSpeed;
    ImageButton engageAutopilot;
    ImageButton decreaseSpeed;
    Button takeControl;
    public static boolean isAutopilotOn = false;
    public static int referenceSpeed = 60;

    static CharSequence finalString;
    static CharSequence refSpeedString;

    public HeaderFragment() {
        // Required empty public constructor
    }

    public static HeaderFragment newInstance()
    {
        HeaderFragment fragment = new HeaderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_header, container, false);

        initScreen(view);

        return view;
    }

    void initScreen(View view)
    {
        mapsInformationLayout = (LinearLayout) view.findViewById(R.id.maps_information_layout);
        speedometerLayout = (LinearLayout) view.findViewById(R.id.speedometer_layout);
        autopilotStatusLayout = (LinearLayout) view.findViewById(R.id.autopilot_status_layout);

        turnIcon = (ImageView) view.findViewById(R.id.turn_icon);
        turnDistance = (TextView) view.findViewById(R.id.turn_distance);
        nextStreetName = (TextView) view.findViewById(R.id.next_street_name);
        speedLimitNormal = (TextView) view.findViewById(R.id.speedlimit_normal);
        speedLimitAutopilot = (TextView) view.findViewById(R.id.speedlimit_autopilot);
        referenceSpeedAutopilot = (TextView) view.findViewById(R.id.reference_speed_autopilot);
        analogSpeed = (ProgressBar) view.findViewById(R.id.analog_speed);
        digitalSpeed = (TextView) view.findViewById(R.id.digital_speed);
        autopilotStatusImage = (ImageView) view.findViewById(R.id.autopilot_status_image);
        autopilotTextStatus = (TextView) view.findViewById(R.id.autopilot_text_status);
        increaseSpeed = (ImageButton) view.findViewById(R.id.increase_speed);
        engageAutopilot = (ImageButton) view.findViewById(R.id.engage_autopilot);
        decreaseSpeed = (ImageButton) view.findViewById(R.id.decrease_speed);
        takeControl = (Button) view.findViewById(R.id.take_control);

        increaseSpeed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                referenceSpeed = referenceSpeed + 1;
                ((MapActivity) getActivity()).sendControl(2);

            }
        });

        engageAutopilot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MapActivity) getActivity()).sendControl(1);
                ((MapActivity) getActivity()).moveToRouteMid();
                isAutopilotOn = true;
            }
        });

        decreaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                referenceSpeed = referenceSpeed - 1;
                ((MapActivity) getActivity()).sendControl(3);
            }
        });

        takeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapActivity) getActivity()).sendControl(1);
            }
        });
    }

    public void setVisibilityForSpeedLimitAndRefSpeed(final int index)
    {
        switch (index)
        {
            case 0:
                referenceSpeedAutopilot.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        referenceSpeedAutopilot.setVisibility(View.GONE);
                    }
                });

                speedLimitAutopilot.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        speedLimitAutopilot.setVisibility(View.GONE);
                    }
                });

                speedLimitNormal.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        speedLimitNormal.setVisibility(View.VISIBLE);
                    }
                });
                break;

            case 1:
                referenceSpeedAutopilot.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        referenceSpeedAutopilot.setVisibility(View.VISIBLE);
                        setReferenceSpeedAutopilot();
                    }
                });

                speedLimitAutopilot.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        speedLimitAutopilot.setVisibility(View.VISIBLE);
                    }
                });

                speedLimitNormal.getHandler().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        speedLimitNormal.setVisibility(View.GONE);
                    }
                });

        }
    }

    public void setReferenceSpeedAutopilot()
    {
        String refSpeed = String.valueOf(referenceSpeed);
        SpannableString currentSpeedSpannableString = new SpannableString(refSpeed);
        currentSpeedSpannableString.setSpan(new RelativeSizeSpan(1.7f), 0, refSpeed.length(), 0);
        currentSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, refSpeed.length(), 0);
        refSpeedString = TextUtils.concat("LIMIT\n",currentSpeedSpannableString);

        referenceSpeedAutopilot.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                referenceSpeedAutopilot.setText(refSpeedString);
            }
        });

    }

    public void setEngageAutopilot(final int index)
    {
        engageAutopilot.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                switch(index)
                {
                    case 0:
                        engageAutopilot.setImageResource(R.drawable.engage_autopilot_disabled);
                        engageAutopilot.setClickable(false);
                        break;

                    case 1:
                        engageAutopilot.setImageResource(R.drawable.engage_autopilot_enabled);
                        engageAutopilot.setClickable(true);
                        break;
                }
            }
        });

    }

    public void setIncreaseDecreaseSpeed(final int index)
    {
        increaseSpeed.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                switch(index)
                {
                    case 0:
                        increaseSpeed.setImageResource(R.drawable.increase_speed_disabled);
                        increaseSpeed.setClickable(false);
                        break;

                    case 1:
                        increaseSpeed.setImageResource(R.drawable.increase_speed_enabled);
                        increaseSpeed.setClickable(true);
                        break;
                }
            }
        });

        decreaseSpeed.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                switch(index)
                {
                    case 0:
                        decreaseSpeed.setImageResource(R.drawable.decrease_speed_disabled);
                        decreaseSpeed.setClickable(false);
                        break;

                    case 1:
                        decreaseSpeed.setImageResource(R.drawable.decrease_speed_enabled);
                        decreaseSpeed.setClickable(true);
                        break;
                }
            }
        });
    }

    public void setAutopilotStatusImageAndText(final int index)
    {

        autopilotStatusImage.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                switch(index)
                {
                    case 0:
                        autopilotStatusImage.setImageResource(R.drawable.manual_mode);
                        break;

                    case 1:
                        autopilotStatusImage.setImageResource(R.drawable.autopilot_avbl);
                        break;

                    case 2:
                        autopilotStatusImage.setImageResource(R.drawable.autopilot_enabled);
                        break;

                    case 3:
                        autopilotStatusImage.setImageResource(R.drawable.autopilot_ending);
                        break;

                    case 4:
                        autopilotStatusImage.setImageResource(R.drawable.autopilot_waring);
                        break;

                    case 5:
                        autopilotStatusImage.setImageResource(R.drawable.autopilot_serious);
                        break;
                }
            }
        });

        autopilotTextStatus.getHandler().post(new Runnable() {
            @Override
            public void run()
            {
                switch(index)
                {
                    case 0:
                        autopilotTextStatus.setText(R.string.manual_mode);
                        break;

                    case 1:
                        autopilotTextStatus.setText(R.string.autopilot_available);
                        break;

                    case 2:
                        autopilotTextStatus.setText(R.string.autopilot_enabled);
                        autopilotTextStatus.setBackgroundColor(getResources().getColor(R.color.autopilot_enable_color));
                        break;

                    case 3:
                        autopilotTextStatus.setText(R.string.prepare_to_take_control);
                        autopilotTextStatus.setBackgroundColor(getResources().getColor(R.color.warning_color));
                        break;

                    case 4:
                        autopilotTextStatus.setText(R.string.please_take_over);
                        autopilotTextStatus.setBackgroundColor(getResources().getColor(R.color.warning_color));
                        break;

                    case 5:
                        autopilotTextStatus.setText(R.string.take_over_now);
                        autopilotTextStatus.setBackgroundColor(getResources().getColor(R.color.error_color));
                        break;
                }
            }
        });

        if(index == 5)
        {
            mapsInformationLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mapsInformationLayout.setBackgroundColor(getResources().getColor(R.color.emergency_background_color));
                }
            });
            speedometerLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    speedometerLayout.setBackgroundColor(getResources().getColor(R.color.emergency_background_color));
                }
            });
            autopilotStatusLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    autopilotStatusLayout.setBackgroundColor(getResources().getColor(R.color.emergency_background_color));
                }
            });
        }

        else
        {
            mapsInformationLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mapsInformationLayout.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                }
            });
            speedometerLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    speedometerLayout.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                }
            });
            autopilotStatusLayout.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    autopilotStatusLayout.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                }
            });
        }
    }

    public void setNormalAndAutopilotSpeedLimit()
    {
        if (NavigationManager.getInstance().getNavigationData() != null) {

            NavigationData navigationData = NavigationManager.getInstance().getNavigationData();

            if (navigationData.getSpeedLimit() != null) {
                SpeedLimit speedLimit = navigationData.getSpeedLimit();
                double absValue = speedLimit.getValue();
                int unit = speedLimit.getUnit();

                if (unit == speedLimit.UNIT_KPH) {
                    absValue = absValue * 0.621371;//Convert to MPH
                }
                int limit = (int)absValue;
                speedLimitNormal.setText(String.valueOf(limit));
                speedLimitAutopilot.setText(String.valueOf(limit));

            }
        }
    }

    public void setSpeed()
    {
        if (NavigationManager.getInstance().getNavigationData() != null)
        {
            NavigationData navigationData = NavigationManager.getInstance().getNavigationData();

            if (navigationData.getTravellingEdgeInfo() != null)
            {
                TravellingEdgeInfo travellingEdgeInfo= navigationData.getTravellingEdgeInfo();

                int speed = (int)travellingEdgeInfo.getInstanceSpeed();
                analogSpeed.setProgress(speed);

                String currentSpeed = String.valueOf(speed);
                SpannableString currentSpeedSpannableString = new SpannableString(currentSpeed);
                currentSpeedSpannableString.setSpan(new RelativeSizeSpan(1.7f), 0, currentSpeed.length(), 0);
                currentSpeedSpannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentSpeed.length(), 0);
                finalString = TextUtils.concat(currentSpeedSpannableString, "\nmph");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        digitalSpeed.setText(finalString);
                    }
                });
            }
        }

    }

    public void setNextStreetName(String name)
    {
        nextStreetName.setText(name);

    }

    public void setTurnDistance(Double distanceToTurn)
    {
        turnDistance.setText(String.valueOf(distanceToTurn) + " mi");
    }

    public void setTurnIcon(TurnType turnType)
    {
        switch (turnType) {
            case Continue:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case TurnSlightRight:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_right_icon_focused);
                break;
            case TurnRight:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_right_icon_focused);
                break;
            case TurnHardRight:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_hard_right_unfocused);
                break;
            case RightUTurn:
                turnIcon.setImageResource(R.drawable.lane_assist_uturnright_icon_focused);
                break;
            case LeftUTurn:
                turnIcon.setImageResource(R.drawable.lane_assist_uturnleft_icon_focused);
                break;
            case TurnHardLeft:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_hard_left_unfocused);
                break;
            case TurnLeft:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_left_icon_focused);
                break;
            case TurnSlightLeft:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_left_icon_focused);
                break;
            case EnterLeft:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_left_icon_focused);
                break;
            case EnterRight:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_right_icon_focused);
                break;
            case EnterAhead:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case ExitLeft:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_exit_left_unfocused);
                break;
            case ExitRight:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_exit_right_unfocused);
                break;
            case ExitAhead:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case MergeLeft:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_merge_left_unfocused);
                break;
            case MergeRight:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_merge_right_unfocused);
                break;
            case MergeAhead:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_continue_unfocused);
                break;
            case LocationLeft:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_on_left_unfocused);
                break;
            case LocationRight:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_on_right_unfocused);
                break;
            case LocationAhead:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_ahead_focused);
                break;
            case StayLeft:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case StayRight:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case StayMiddle:
                turnIcon.setImageResource(R.drawable.lane_assist_ahead_icon);
                break;
            case HookTurnRight:
                turnIcon.setImageResource(R.drawable.lane_assist_turn_right_icon_focused);
                break;
            case PassTollBotth:
                turnIcon.setImageResource(R.drawable.list_turn_icon_big_tollbooth_unfocused);
                break;
        }
    }
}
