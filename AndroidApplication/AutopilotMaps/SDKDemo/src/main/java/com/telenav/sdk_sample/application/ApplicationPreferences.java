package com.telenav.sdk_sample.application;

import android.content.Context;
import android.content.SharedPreferences;
import com.telenav.sdk.map.InitialiseSettings;
import com.telenav.sdk.map.MapSettings;
import com.telenav.sdk.navigation.model.NavigationSettings;

/**
 * Takes care of persisting the data between sessions
 */
public class ApplicationPreferences {

    /**
     * preference name
     */
    public static final String PREFS_NAME = "sdkSamplePrefs";

    /**
     * used for modifying values in a SharedPreferences prefs
     */
    private static SharedPreferences.Editor prefsEditor;

    /**
     * reference to preference
     */
    private SharedPreferences prefs;

    /**
     * the context
     */
    private Context context;

    /**
     * Default constructor. This class should only be instantiated from SdkSampleApplication
     * @param context - the context of the application
     */
    public ApplicationPreferences(Context context) {
        this.context = context;
        setDefaultPreferences();
    }

    /**
     * set the default values for preferences
     */
    private void setDefaultPreferences() {
        // initialize with their default values
        if (!getBooleanPreference(PreferenceTypes.K_PREFS_INITIALIZED)) {
            this.setPreference(PreferenceTypes.K_PREFS_INITIALIZED, PreferenceTypes.ON);

            this.setPreference(PreferenceTypes.K_MAP_STYLE, MapSettings.MAP_STYLE_DAY);

            this.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M2D);

            this.setPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_SHOW_MAP_SCALE_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_SHOW_MAP_ROADS_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_SHOW_MAP_REGIONS_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_SHOW_MAP_LABELS_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS, PreferenceTypes.ON);
            this.setPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW, PreferenceTypes.ON);

            this.setPreference(PreferenceTypes.K_FOLLOW_USER_POSITION, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_ROTATE_HEADING, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_MAP_PANNING, PreferenceTypes.ON);
            this.setPreference(PreferenceTypes.K_MAP_ZOOMING, PreferenceTypes.ON);
            this.setPreference(PreferenceTypes.K_MAP_TILTING, PreferenceTypes.ON);
            this.setPreference(PreferenceTypes.K_MAP_ROTATING, PreferenceTypes.ON);

            this.setPreference(PreferenceTypes.K_NAVIGATION_SIMULATION, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_MAP_DATA_PATH, PreferenceTypes.DEF_MAP_DATA_PATH);

            this.setPreference(PreferenceTypes.K_IP_ADDRESS, PreferenceTypes.DEF_IP_ADDRESS);
            this.setPreference(PreferenceTypes.K_MAP_REGION, InitialiseSettings.REGION_EU);

            this.setPreference(PreferenceTypes.K_AVOID_HIGHWAYS, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_TOLL_ROADS, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_TUNNELS, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_FERRIES, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT, PreferenceTypes.OFF);
            this.setPreference(PreferenceTypes.K_AVOID_HOV_LANES, PreferenceTypes.OFF);

            this.setPreference(PreferenceTypes.K_UNIT_TYPE, NavigationSettings.UNIT_TYPE_IMPERIAL);
            this.setPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE, NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY);

            this.savePreferences();
        }
    }

    /**
     * Initializes preferences, creates the {@link android.content.SharedPreferences} and
     * {@link android.content.SharedPreferences.Editor} objects, if needed.
     */
    private void initPreferences() {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }
    }

    /**
     * Sets a {@link String} preference
     * @param key - the key of the preference, defined in
     * {@link com.telenav.sdk_sample.application.PreferenceTypes}
     * @param value - the value of the preference
     */
    public void setPreference(String key, String value) {
        initPreferences();
        prefsEditor.putString(key, value);
    }

    /**
     * Sets an int preference
     * @param key - the key of the preference, defined in
     * {@link com.telenav.sdk_sample.application.PreferenceTypes}
     * @param value - the value of the preference
     */
    public void setPreference(String key, int value) {
        initPreferences();
        prefsEditor.putInt(key, value);
    }

    /**
     * Sets a boolean preference
     * @param key - the key of the preference, defined in
     * {@link com.telenav.sdk_sample.application.PreferenceTypes}
     * @param value - the value of the preference
     */
    public void setPreference(String key, boolean value) {
        initPreferences();
        prefsEditor.putBoolean(key, value);
    }

    /**
     * Commits the current changes to the preferences - to be called after
     * changing the preferences
     */
    public void savePreferences() {
        initPreferences();
        prefsEditor.commit();
    }

    // ================= retrieving methods =======================

    /**
     * @return {@link String} preference for the given key or null if nothing
     * was saved
     */
    public String getStringPreference(String key) {
        initPreferences();
        try {
            return prefs.getString(key, null);
        } catch (ClassCastException ex1) {
            return String.valueOf(prefs.getInt(key, 0));
        }
    }

    /**
     * @return boolean preference for the given key or false if nothing was
     * saved
     */
    public boolean getBooleanPreference(String key) {
        initPreferences();
        return prefs.getBoolean(key, false);
    }

    /**
     * @return int preference for the given key or 0 if nothing was saved
     */
    public int getIntPreference(String key) {
        initPreferences();
        return prefs.getInt(key, 0);
    }

    /**
     * @param context the context to set
     */
    public void setContext(Context context) {
        this.context = context;
    }
}