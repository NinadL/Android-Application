package com.telenav.sdk_sample.ui.menu;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import com.telenav.sdk.map.InitialiseSettings;
import com.telenav.sdk.map.MapSettings;
import com.telenav.sdk.navigation.model.NavigationSettings;
import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.application.ApplicationPreferences;
import com.telenav.sdk_sample.application.PreferenceTypes;
import com.telenav.sdk_sample.application.SdkSampleApplication;
import com.telenav.sdk_sample.ui.map.MapActivity;

/**
 * Defines the adapter used to display the items list contained in the sliding menu
 * Created by AlexandraV on 23.09.2015.
 */
public class SlidingMenuAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<SlidingMenuItem> slidingMenuItems;

    private ApplicationPreferences appPrefs;

    public SlidingMenuAdapter(Context context, ArrayList<SlidingMenuItem> slidingMenuItems) {
        this.context = context;
        this.slidingMenuItems = slidingMenuItems;
        this.appPrefs = SdkSampleApplication.getInstance().getApplicationPreferences();
    }

    @Override
    public int getCount() {
        return slidingMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return slidingMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((SlidingMenuItem) this.getItem(position)).getMenuItemId();
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return ((SlidingMenuItem) this.getItem(position)).getMenuItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SlidingMenuItem slidingMenuItem = (SlidingMenuItem) getItem(position);
        SubTitleMenuSectionHolder subtitleHolder = new SubTitleMenuSectionHolder();
        RadioMenuItemHolder radiobuttonHolder = new RadioMenuItemHolder();
        CheckBoxMenuItemHolder checkboxHolder = new CheckBoxMenuItemHolder();
        MapDataMenuItemHolder mapDataMenuItemHolder = new MapDataMenuItemHolder();
        IpDataMenuItemHolder ipDataMenuItemHolder = new IpDataMenuItemHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            switch (getItemViewType(position)) {
                case SlidingMenuItem.ITEM_SUBTITLE_TYPE:
                    convertView = mInflater.inflate(R.layout.drawer_subtitle_list_item, null);
                    subtitleHolder.subtitleTextView = (TextView) convertView.findViewById(R.id.subtitle_textView);
                    convertView.setTag(subtitleHolder);
                    break;
                case SlidingMenuItem.ITEM_RADIO_TYPE:
                    convertView = mInflater.inflate(R.layout.drawer_radio_button_list_item, null);
                    radiobuttonHolder.radioButton = (RadioButton) convertView.findViewById(R.id.menu_item_radiobutton);
                    convertView.setTag(radiobuttonHolder);
                    break;
                case SlidingMenuItem.ITEM_CHECKBOX_TYPE:
                    convertView = mInflater.inflate(R.layout.drawer_checkbox_list_item, null);
                    checkboxHolder.checkBox = (CheckBox) convertView.findViewById(R.id.menu_item_checkbox);
                    convertView.setTag(checkboxHolder);
                    break;
                case SlidingMenuItem.ITEM_MAP_DATA_PATH:
                    convertView = mInflater.inflate(R.layout.drawer_data_path_list_item, null);
                    mapDataMenuItemHolder.mapDataPathEditText = (EditText) convertView.findViewById(R.id.map_data_path_editText);
                    mapDataMenuItemHolder.changeTextView = (TextView) convertView.findViewById(R.id.change_textView);
                    convertView.setTag(mapDataMenuItemHolder);
                    break;
                case SlidingMenuItem.ITEM_IP_ADDRESS:
                    convertView = mInflater.inflate(R.layout.drawer_ip_path_list_item, null);
                    ipDataMenuItemHolder.IpDataPathEditText = (EditText) convertView.findViewById(R.id.ip_data_path_editText);
                    ipDataMenuItemHolder.changeTextViewForIp = (TextView) convertView.findViewById(R.id.change_textViewForIp);
                    convertView.setTag(ipDataMenuItemHolder);
                    break;
                default:
                    break;
            }
        } else {
            switch (getItemViewType(position)) {
                case SlidingMenuItem.ITEM_SUBTITLE_TYPE:
                    subtitleHolder = (SubTitleMenuSectionHolder) convertView.getTag();
                    break;
                case SlidingMenuItem.ITEM_RADIO_TYPE:
                    radiobuttonHolder = (RadioMenuItemHolder) convertView.getTag();
                    break;
                case SlidingMenuItem.ITEM_CHECKBOX_TYPE:
                    checkboxHolder = (CheckBoxMenuItemHolder) convertView.getTag();
                    break;
                case SlidingMenuItem.ITEM_IP_ADDRESS:
                    ipDataMenuItemHolder = (IpDataMenuItemHolder) convertView.getTag();
                    break;
                case SlidingMenuItem.ITEM_MAP_DATA_PATH:
                    mapDataMenuItemHolder = (MapDataMenuItemHolder) convertView.getTag();
                    break;

                default:
                    break;
            }
        }

        switch (getItemViewType(position)) {
            case SlidingMenuItem.ITEM_SUBTITLE_TYPE:
                subtitleHolder.subtitleTextView.setText(slidingMenuItem.getMenuItemLabel());
                break;
            case SlidingMenuItem.ITEM_RADIO_TYPE:
                radiobuttonHolder.radioButton.setText(slidingMenuItem.getMenuItemLabel());
                switch (slidingMenuItems.get(position).getMenuItemType()) {
                    case MenuConstants.MAP_REGION_EU_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_REGION) == InitialiseSettings.REGION_EU);
                        break;
                    case MenuConstants.MAP_REGION_NA_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_REGION) == InitialiseSettings.REGION_NA);
                        break;
                    case MenuConstants.MAP_STYLE_DAY_ITEM_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_STYLE) == MapSettings.MAP_STYLE_DAY);
                        break;
                    case MenuConstants.MAP_STYLE_NIGHT_ITEM_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_STYLE) == MapSettings.MAP_STYLE_NIGHT);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M3D_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D_NORTH_UP);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D_HEADING_UP);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M2D_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D_HEADING_UP);
                        break;
                    case MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D_NORTH_UP);
                        break;
                    case MenuConstants.NAVIGATION_REAL_ID:
                        radiobuttonHolder.radioButton.setChecked(!appPrefs.getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION));
                        break;
                    case MenuConstants.NAVIGATION_SIMULATION_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION));
                        break;
                    case MenuConstants.UNIT_TYPE_METRIC_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_UNIT_TYPE) == NavigationSettings.UNIT_TYPE_METRIC);
                        break;
                    case MenuConstants.UNIT_TYPE_IMPERIAL_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_UNIT_TYPE) == NavigationSettings.UNIT_TYPE_IMPERIAL);
                        break;
                    case MenuConstants.VOICE_GUIDANCE_NONE_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_NONE);
                        break;
                    case MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY);
                        break;
                    case MenuConstants.VOICE_GUIDANCE_PHONEME_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_PHONEME);
                        break;
                    case MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID:
                        radiobuttonHolder.radioButton.setChecked(appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_INSTRUCTION);
                        break;
                }
                break;
            case SlidingMenuItem.ITEM_CHECKBOX_TYPE:
                checkboxHolder.checkBox.setText(slidingMenuItem.getMenuItemLabel());
                switch (slidingMenuItems.get(position).getMenuItemType()) {
                    case MenuConstants.MAP_PAN_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_PANNING));
                        break;
                    case MenuConstants.MAP_ROTATE_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ROTATING));
                        break;
                    case MenuConstants.MAP_TILT_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_TILTING));
                        break;
                    case MenuConstants.MAP_ZOOM_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ZOOMING));
                        break;
                    case MenuConstants.SHOW_BUILDINGS_IN_3D_MODE_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE));
                        break;
                    case MenuConstants.ROTATE_HEADING_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
                        break;
                    case MenuConstants.MAP_POSITION_FOLLOW_USER_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION));
                        break;
                    case MenuConstants.SHOW_MAP_TERRAIN_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE));
                        break;
                    case MenuConstants.ENABLE_TRAFFIC_INCIDENTS:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS));
                        break;
                    case MenuConstants.ENABLE_TRAFFIC_FLOW:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW));
                        break;
                    case MenuConstants.ROUTE_AVOID_HIGHWAYS_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS));
                        break;
                    case MenuConstants.ROUTE_AVOID_TOLL_ROADS_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS));
                        break;
                    case MenuConstants.ROUTE_AVOID_TUNNELS_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS));
                        break;
                    case MenuConstants.ROUTE_AVOID_UNPAVED_ROAD_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD));
                        break;
                    case MenuConstants.ROUTE_AVOID_COUNTRY_BORDERS_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS));
                        break;
                    case MenuConstants.ROUTE_AVOID_FERRIES_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES));
                        break;
                    case MenuConstants.ROUTE_AVOID_HOV_LANES_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES));
                        break;
                    case MenuConstants.ROUTE_AVOID_TRAIN_TRANSPORT_ID:
                        checkboxHolder.checkBox.setChecked(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT));
                        break;
                }
                break;
            case SlidingMenuItem.ITEM_MAP_DATA_PATH:
                SpannableString changeContent = new SpannableString(context.getResources().getString(R.string.change_data_path_label));
                changeContent.setSpan(new UnderlineSpan(), 0, changeContent.length(), 0);
                mapDataMenuItemHolder.mapDataPathEditText.setText(appPrefs.getStringPreference(PreferenceTypes.K_MAP_DATA_PATH));

                mapDataMenuItemHolder.changeTextView.setText(changeContent);
                mapDataMenuItemHolder.changeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MapActivity) context).showEditDataPath();
                    }
                });

                break;
            case SlidingMenuItem.ITEM_IP_ADDRESS:
                SpannableString changeIpAddress = new SpannableString(context.getResources().getString(R.string.change_data_path_label));
                changeIpAddress.setSpan(new UnderlineSpan(), 0, changeIpAddress.length(), 0);
                ipDataMenuItemHolder.IpDataPathEditText.setText(appPrefs.getStringPreference(PreferenceTypes.K_IP_ADDRESS));

                ipDataMenuItemHolder.changeTextViewForIp.setText(changeIpAddress);
                ipDataMenuItemHolder.changeTextViewForIp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MapActivity) context).showEditIpPath();
                    }
                });

                break;
            default:
                break;
        }

        return convertView;
    }

    private static class RadioMenuItemHolder {
        private RadioButton radioButton;
    }

    private static class CheckBoxMenuItemHolder {
        private CheckBox checkBox;
    }

    private static class SubTitleMenuSectionHolder {
        private TextView subtitleTextView;
    }

    private static class MapDataMenuItemHolder {
        private EditText mapDataPathEditText;
        private TextView changeTextView;
    }

    private static class IpDataMenuItemHolder {
        private EditText IpDataPathEditText;
        private TextView changeTextViewForIp;
    }
}