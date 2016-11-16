package com.telenav.sdk_sample.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.telenav.entity.service.model.common.GeoPoint;
import com.telenav.entity.service.model.v4.Entity;
import com.telenav.entity.service.model.v4.EntityDetailResponse;
import com.telenav.entity.service.model.v4.EntitySearchResponse;
import com.telenav.entity.service.model.v4.EntitySuggestionResponse;
import com.telenav.entity.service.model.v4.Suggestion;
import com.telenav.foundation.log.LogEnum;
import com.telenav.sdk.external.ExternalDisplayManager;
import com.telenav.sdk.map.InitialiseManager;
import com.telenav.sdk.map.InitialiseSettings;
import com.telenav.sdk.map.InitialiseStatusListener;
import com.telenav.sdk.map.MapSettings;
import com.telenav.sdk.map.MapView;
import com.telenav.sdk.map.SdkInitialization;
import com.telenav.sdk.navigation.controller.NavigationManager;
import com.telenav.sdk.navigation.model.NavigationSettings;
import com.telenav.sdk.navigation.model.TrafficSettings;
import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.application.ApplicationPreferences;
import com.telenav.sdk_sample.application.PreferenceTypes;
import com.telenav.sdk_sample.application.SdkSampleApplication;
import com.telenav.sdk_sample.car.data.SocketServer;
import com.telenav.sdk_sample.connectivity.NetworkConnectionMonitor;
import com.telenav.sdk_sample.joglrender.OBJParser;
import com.telenav.sdk_sample.joglrender.OpenGLRenderer;
import com.telenav.sdk_sample.search.SearchManager;
import com.telenav.sdk_sample.search.SearchRequestObject;
import com.telenav.sdk_sample.search.SuggestionItem;
import com.telenav.sdk_sample.text.to.speech.TextToSpeechManager;
import com.telenav.sdk_sample.ui.Header.AutopilotStatusDecisions;
import com.telenav.sdk_sample.ui.Header.HeaderFragment;
import com.telenav.sdk_sample.ui.menu.MenuConstants;
import com.telenav.sdk_sample.ui.menu.SlidingMenuAdapter;
import com.telenav.sdk_sample.ui.menu.SlidingMenuItem;
import com.telenav.sdk_sample.car.data.SocketClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements InitialiseStatusListener {

    /**
     * developer api key
     */
    public static final String DEVELOPER_API_KEY = "f5af3b9d-7899-40b0-aa2a-5a513dbca330";

    /**
     * developer secret api key
     */
    public static final String DEVELOPER_SECRET_API_KEY = "3060f702-e4fd-4e97-a55c-afdc9709d186";

    /**
     * the map fragment tag
     */
    public static final String MAP_FRAGMENT = "MapFragment";

    /**
     * the header fragment tag
     */
    public static final String HEADER_FRAGMENT = "HeaderFragment";

    /**
     * used for permissions request callback
     */
    private static final int PERMISSIONS_REQUEST = 2;

    /**
     * the map fragment
     */
    private MapFragment mapFragment;

    //Header fragment
    public static HeaderFragment headerFragment= new HeaderFragment();

    public static TextToSpeechManager textToSpeechManager;

    //sliding menu

    private NetworkConnectionMonitor networkConnectionMonitor;

    private ArrayList<SlidingMenuItem> slidingList;

    private DrawerLayout slidingDrawerLayout;

    private ListView slidingListView;

    private ActionBarDrawerToggle slidingDrawerToggle;

    private SlidingMenuAdapter slidingMenuAdapter;

    private boolean isSettingsMenuLocked;

    private ApplicationPreferences appPrefs;


    public boolean isNavigationStarted = false;


    String ipAddress = "198.168.1.101";

    public ArrayAdapter arrayAdapter;

    public ArrayList<String> fileContents = new ArrayList<String>();
    public ArrayList<String> recentHistoryDestinationName = new ArrayList<>();
    public int totalItemsInHistory = 0;

    private SocketClient c0=null, c1=null, c2=null, c3=null;
    private SocketServer ss = null;

    GLSurfaceView gl;


    /**
     * used for knowing if onPostResume method was called
     */
    private volatile boolean onPostResumeCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ss = new SocketServer();
        c0 = new SocketClient("localhost",45001,0);
        c1 = new SocketClient("localhost",45011,1);
        c2 = new SocketClient("localhost",45021,2);
        c3 = new SocketClient("localhost",45031,3);

        setContentView(R.layout.activity_main);

        onPostResumeCalled = false;

        initializeSlidingMenuForMap();

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            requestPermissions();
        } else {
            // Pre-Marshmallow
            initMap();
        }
        initJoglScreen();
    }

    private void initMap() {
        SdkSampleApplication sdkApp = (SdkSampleApplication) getApplication();
        if (!sdkApp.isInitOk()) {
            InitialiseSettings initialiseSettings = new InitialiseSettings();
            initialiseSettings.setApiKey(DEVELOPER_API_KEY);
            initialiseSettings.setApiSecretKey(DEVELOPER_SECRET_API_KEY);
            initialiseSettings.setMapDataPath(appPrefs.getStringPreference(PreferenceTypes.K_MAP_DATA_PATH));
            initialiseSettings.setRegion(appPrefs.getIntPreference(PreferenceTypes.K_MAP_REGION) == (InitialiseSettings.REGION_EU) ? InitialiseSettings.REGION_EU :
                    InitialiseSettings.REGION_NA);
            initialiseSettings.setDefaultLocation(MapFragment.EU_DEFAULT_LAT, MapFragment.EU_DEFAULT_LON);
            InitialiseManager.getInstance().setInitialiseSettings(initialiseSettings);
            InitialiseManager.getInstance().addInitStatusListener(this);
            InitialiseManager.getInstance().initialise(getApplicationContext());
            ipAddress = appPrefs.getStringPreference(PreferenceTypes.K_IP_ADDRESS);
            //client = new SocketClient(ipAddress,45002);
            Log.d("ipAddress", "is"+ ipAddress);

            textToSpeechManager = new TextToSpeechManager();
            textToSpeechManager.initialiseTextToSpeech();

            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Location Unavailable")
                        .setMessage("Please turn on the gps location.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }
    }

    private void initJoglScreen(){

        new processObjFile().execute();
        gl = (GLSurfaceView) findViewById(R.id.jogl_render);
        gl.setEGLContextClientVersion(2);

        gl.setRenderer( new OpenGLRenderer(this,(RelativeLayout) findViewById(R.id.leftPane)));

    }

    /**
     * Request the permission needed for the app for API>23
     */
    public void requestPermissions() {
        final List<String> permissionsList = new ArrayList<String>();
        addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
        addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION);
        addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionsList.size() > 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                // Need Rationale
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.grant_access_to_gps_external))
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MapActivity.this,
                                                permissionsList.toArray(new String[permissionsList.size()]), PERMISSIONS_REQUEST);
                                    }
                                }
                        )
                        .setNegativeButton(R.string.cancel_label, null)
                        .create()
                        .show();
                return;
            } else {
                if (permissionsList.size() > 0) {
                    ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                            PERMISSIONS_REQUEST);
                }
            }
        } else {
            initMap();
        }
    }

    /**
     * Add required permission to the list and check if not already granted access
     * @param permissionsList - the list with the permissions
     * @param permission - the permission the will be added
     * @return - true - if permission added to list
     */
    private void addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0) {
                boolean granted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // Permission Denied
                        Toast.makeText(this, getResources().getString(R.string.permissions_denied), Toast.LENGTH_SHORT).show();
                        granted = false;
                        break;
                    }
                }
                if (granted) {
                    Toast.makeText(this, getResources().getString(R.string.permissions_granted), Toast.LENGTH_SHORT).show();
                    initMap();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * registers the network connection broadcast receiver
     */
    private void registerNetworkConnectionBroadcastReceiver() {
        networkConnectionMonitor = new NetworkConnectionMonitor();
        registerReceiver(networkConnectionMonitor, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        onPostResumeCalled = true;
        if (((SdkSampleApplication) getApplication()).isInitOk()) {
            attachFragmentToActivity();
        }
    }

    @Override
    public void onInitialised(SdkInitialization initialization) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((SdkSampleApplication) getApplication()).setInitOk(true);
                if (onPostResumeCalled) {
                    attachFragmentToActivity();
                }
            }
        });
    }

    /**
     * Attach the mapFragment to the activity
     */
    private void attachFragmentToActivity() {
        if (mapFragment != null && mapFragment.isAdded()) {
            return;
        }
        registerNetworkConnectionBroadcastReceiver();

        mapFragment = new MapFragment();

        getFragmentManager().beginTransaction().replace(R.id.header_frame, headerFragment, HEADER_FRAGMENT).commit();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, mapFragment, MAP_FRAGMENT).commit();
        AutopilotStatusDecisions autopilotStatusDecisions = new AutopilotStatusDecisions();
        autopilotStatusDecisions.initTimer();
         setDefaultNavigationSettingsValues();
    }

    /**
     * sets default navigation settings values
     */
    private void setDefaultNavigationSettingsValues() {
        NavigationSettings navigationSettings = NavigationManager.getInstance().getNavigationSettings();

        byte unitType = (byte) SdkSampleApplication.getInstance().getApplicationPreferences().getIntPreference(PreferenceTypes.K_UNIT_TYPE);
        if (unitType == NavigationSettings.UNIT_TYPE_IMPERIAL) {
            navigationSettings.setUnitType(NavigationSettings.UNIT_TYPE_IMPERIAL);
        } else if (unitType == NavigationSettings.UNIT_TYPE_METRIC) {
            navigationSettings.setUnitType(NavigationSettings.UNIT_TYPE_METRIC);
        }

        byte voiceGuidanceType = (byte) SdkSampleApplication.getInstance().getApplicationPreferences().getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE);
        if (voiceGuidanceType == NavigationSettings.VOICE_GUIDANCE_NONE) {
            navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_NONE);
        } else if (voiceGuidanceType == NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY) {
            navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY);
        } else if (voiceGuidanceType == NavigationSettings.VOICE_GUIDANCE_PHONEME) {
            navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_PHONEME);
        } else if (voiceGuidanceType == NavigationSettings.VOICE_GUIDANCE_INSTRUCTION) {
            navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_INSTRUCTION);
        }

        byte[] enabledZoneAlerts = new byte[4];
        enabledZoneAlerts[0] = NavigationSettings.ZONE_ALERT_INCIDENT;
        enabledZoneAlerts[1] = NavigationSettings.ZONE_ALERT_RAILWAY_CROSSING;
        enabledZoneAlerts[2] = NavigationSettings.ZONE_ALERT_SPEED_CAMERA;
        enabledZoneAlerts[3] = NavigationSettings.ZONE_ALERT_SCHOOL;
        navigationSettings.setEnabledZoneAlerts(enabledZoneAlerts);

        navigationSettings.setLogLevel(LogEnum.LogPriority.debug);

        navigationSettings.setJunctionWidth(400);

        navigationSettings.setJunctionHeight(480);

        navigationSettings.setJunctionArrowColor(0);

        navigationSettings.setSimulateSpeed(100);

        navigationSettings.setSimulationMode(SdkSampleApplication.getInstance().getApplicationPreferences().getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION));

        TrafficSettings trafficSettings = new TrafficSettings();
        trafficSettings.setRerouteMode(TrafficSettings.REROUTE_MODE_AUTO);
        trafficSettings.setTrafficAlertEnabled(true);
        navigationSettings.setTrafficSettings(trafficSettings);
    }


    public void sendControl(int value)
    {
        //TODO - MODIFY THIS
        c0.sendMessage(String.valueOf(value));
        //cService.sendMessage(String.valueOf(value)) ;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (textToSpeechManager != null) {
            textToSpeechManager.stop();
        }
        textToSpeechManager.release();

        MapView.destroyMap();
        if (networkConnectionMonitor != null) {
            unregisterReceiver(networkConnectionMonitor);
        }
        if (NavigationManager.getInstance().getNavigationData().isNavigationStarted()) {
            NavigationManager.getInstance().stopNavigation();
        }
        ExternalDisplayManager.getInstance().stopRenderingOnExternalDisplay();

        if (ss != null) {
            ss.closeAll();
            ss = null;
        }

        if (c0 != null) {
            try {
                c0.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c0 = null;
        }
        if (c1 != null) {
            try {
                c1.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c1 = null;
        }
        if (c2 != null) {
            try {
                c2.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c2 = null;
        }
        if (c3 != null) {
            try {
                c3.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c3 = null;
        }
    }



    /**
     * performs search
     * @param searchedKeyword the searched keyword
     * @param location the location from which the search will be made
     */
    public void performSearch(final String searchedKeyword, final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchManager searchManager = SearchManager.getInstance();
                GeoPoint geoPoint = new GeoPoint();
                if (location != null) {
                    geoPoint.setLatitude(location.getLatitude());
                    geoPoint.setLongitude(location.getLongitude());
                }
                SearchRequestObject searchRequestObject = new SearchRequestObject();
                searchRequestObject.setSearchQuery(searchedKeyword);
                searchRequestObject.setLocation(geoPoint);
                EntitySearchResponse entitySearchResponse = searchManager.search(searchRequestObject);
                List<Entity> searchResults = entitySearchResponse.getResults();
                List<String> searchResultList = new ArrayList<>();
                List<GeoPoint> searchResultCoordinates = new ArrayList<>();
                for (Entity entity : searchResults) {
                    if ((entity.getPlace() != null) && (entity.getPlace().getAddress() != null) && (entity.getPlace().getAddress().get(0) != null)) {
                        StringBuilder searchResultName = null;
                        if (entity.getPlace().getName() != null) {
                            searchResultName = new StringBuilder(entity.getPlace().getName());
                        }
                        if (searchResultName != null) {
                            searchResultName.append("\n").append(entity.getPlace().getAddress().get(0).getFormattedAddress());
                        } else {
                            searchResultName = new StringBuilder(entity.getPlace().getAddress().get(0).getFormattedAddress());
                        }
                        searchResultCoordinates.add(entity.getPlace().getAddress().get(0).getGeoCoordinates());
                        searchResultList.add(searchResultName.toString());
                    }
                }
                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT);
                if (mapFragment != null && mapFragment.isVisible()) {
                    mapFragment.setSearchResults(searchResultList, searchResultCoordinates);
                }
            }
        }).start();
    }

    /**
     * searches for suggestions
     * @param searchedKeyword the keyword used as a tip for suggestions list
     * @param location the location from which the suggestions will be returned
     */
    public void searchForSuggestions(final String searchedKeyword, final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchManager searchManager = SearchManager.getInstance();
                GeoPoint geoPoint = new GeoPoint();
                if (location != null) {
                    geoPoint.setLatitude(location.getLatitude());
                    geoPoint.setLongitude(location.getLongitude());
                }
                SearchRequestObject searchRequestObject = new SearchRequestObject();
                searchRequestObject.setSearchQuery(searchedKeyword);
                searchRequestObject.setLocation(geoPoint);
                EntitySuggestionResponse entitySuggestionResponse = searchManager.getSuggestions(searchRequestObject);
                List<Suggestion> suggestionsList = entitySuggestionResponse.getResults();
                List<SuggestionItem> suggestionsResults = new ArrayList<>();
                if (suggestionsList != null) {
                    for (Suggestion currentSuggestion : suggestionsList) {
                        if ((currentSuggestion != null) && (currentSuggestion.getLabel() != null)) {
                            SuggestionItem suggestionItem = new SuggestionItem();
                            suggestionItem.setLabel(currentSuggestion.getLabel());
                            suggestionItem.setQuery(currentSuggestion.getQuery());
                            suggestionItem.setId(currentSuggestion.getId());
                            if (currentSuggestion.getEntity() != null) {
                                suggestionItem.setEntityId(currentSuggestion.getEntity().getId());
                            }
                            suggestionsResults.add(suggestionItem);
                        }
                    }
                }
                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT);
                if (mapFragment != null) {
                    mapFragment.updateSuggestionsResultsInfo(suggestionsResults);
                }
            }
        }).start();
    }

    /**
     * searches for suggestions
     * @param entityId entity ID
     */
    public void searchForDetails(final String entityId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchManager searchManager = SearchManager.getInstance();
                SearchRequestObject searchRequestObject = new SearchRequestObject();
                List<String> entitiesIds = new ArrayList<>();
                entitiesIds.add(entityId);
                searchRequestObject.setEntityIds(entitiesIds);
                EntityDetailResponse entityDetailResponse = searchManager.getDetails(searchRequestObject);
                List<Entity> entityDetailsList = entityDetailResponse.getResults();
                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT);
                // don't know if, for getting the coordinates, we will use address field from EntityDetails or address field from place field
                if ((mapFragment != null) && (entityDetailsList != null) && (entityDetailsList.size() > 0) && (entityDetailsList.get(0) != null)) {
                    if ((entityDetailsList.get(0).getAddress() != null) && (entityDetailsList.get(0).getAddress().getGeoCoordinates() != null)) {
                        mapFragment.showSearchResultOnMap(entityDetailsList.get(0).getAddress().getGeoCoordinates());
                    } else if ((entityDetailsList.get(0).getPlace() != null) && (entityDetailsList.get(0).getPlace().getAddress() != null) && (entityDetailsList.get(0).getPlace
                            ().getAddress().get(0) != null) && (entityDetailsList.get(0).getPlace().getAddress().get(0).getGeoCoordinates() != null)) {
                        mapFragment.showSearchResultOnMap(entityDetailsList.get(0).getPlace().getAddress().get(0).getGeoCoordinates());
                    }
                }
            }
        }).start();
    }

    /**
     * initializes the list in the right sliding menu
     */
    public void initializeSlidingMenuForMap() {
        appPrefs = SdkSampleApplication.getInstance().getApplicationPreferences();
        slidingDrawerLayout = (DrawerLayout) findViewById(R.id.fragment_container);
        slidingListView = (ListView) findViewById(R.id.sliding_drawer_list);

        slidingList = new ArrayList<>();

        //map data path
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_data_path_label).toUpperCase(), MenuConstants
                .MAP_DATA_PATH_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_MAP_DATA_PATH, appPrefs.getStringPreference(PreferenceTypes.K_MAP_DATA_PATH), MenuConstants.MAP_DATA_PATH_ID,
                false));

        //ip address
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.ip_data_path_label).toUpperCase(), MenuConstants
                .IP_DATA_PATH_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_IP_ADDRESS, appPrefs.getStringPreference(PreferenceTypes.K_IP_ADDRESS), MenuConstants.IP_DATA_PATH_ID,
                false));


        //map region
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_region_label).toUpperCase(), MenuConstants
                .MAP_REGION_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_region_eu_label), MenuConstants.MAP_REGION_EU_ID, appPrefs
                .getIntPreference(PreferenceTypes.K_MAP_REGION) == InitialiseSettings.REGION_EU));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_region_na_label), MenuConstants.MAP_REGION_NA_ID, appPrefs
                .getIntPreference(PreferenceTypes.K_MAP_REGION) == InitialiseSettings.REGION_NA));
        // map style
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_style_label).toUpperCase(), MenuConstants
                .MAP_STYLE_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_style_day_label), MenuConstants.MAP_STYLE_DAY_ITEM_ID,
                appPrefs.getIntPreference(PreferenceTypes.K_MAP_STYLE) == MapSettings.MAP_STYLE_DAY));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_style_night_label), MenuConstants.MAP_STYLE_NIGHT_ITEM_ID,
                appPrefs.getIntPreference(PreferenceTypes.K_MAP_STYLE) == MapSettings.MAP_STYLE_NIGHT));

        // map orientation
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_orientation_label).toUpperCase(), MenuConstants
                .MAP_ORIENTATION_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m3_label), MenuConstants
                .MAP_ORIENTATION_M3D_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m3_north_up_label), MenuConstants
                .MAP_ORIENTATION_M3D_NORTH_UP_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D_NORTH_UP));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m3_heading_up_label), MenuConstants
                .MAP_ORIENTATION_M3D_HEADING_UP_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M3D_HEADING_UP));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m2_label), MenuConstants
                .MAP_ORIENTATION_M2D_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m2_north_up_label), MenuConstants
                .MAP_ORIENTATION_M2D_NORTH_UP_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D_NORTH_UP));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.map_orientation_m2_heading_up_label), MenuConstants
                .MAP_ORIENTATION_M2D_HEADING_UP_ID, appPrefs.getIntPreference(PreferenceTypes.K_MAP_ORIENTATION) == MapSettings.ORIENTATION_MODE_M2D_HEADING_UP));

        // position mode
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_position_mode_label).toUpperCase(), MenuConstants
                .MAP_POSITION_MODE_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_position_follow_vehicle_label), MenuConstants
                .MAP_POSITION_FOLLOW_USER_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION)));

        // map interaction mode
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_interaction_label).toUpperCase(), MenuConstants
                .MAP_INTERACTION_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_multitouch_gesture_pan_label), MenuConstants
                .MAP_PAN_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_PANNING)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_multitouch_gesture_rotate_label), MenuConstants
                .MAP_ROTATE_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ROTATING)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_multitouch_gesture_tilt_label), MenuConstants
                .MAP_TILT_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_TILTING)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_multitouch_gesture_zoom_label), MenuConstants
                .MAP_ZOOM_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ZOOMING)));

        // buildings in 3D mode
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_features_label).toUpperCase(), MenuConstants
                .MAP_FEATURES_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.show_buildings_in_3d_mode_label), MenuConstants
                .SHOW_BUILDINGS_IN_3D_MODE_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.rotate_heading_label), MenuConstants
                .ROTATE_HEADING_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING)));

        // terrain
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.map_terrain_label).toUpperCase(), MenuConstants
                .MAP_TERRAIN_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.map_terrain_action_label), MenuConstants
                .SHOW_MAP_TERRAIN_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE)));

        // traffic
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.enable_traffic_label).toUpperCase(), MenuConstants
                .TRAFFIC_ENABLED_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.enable_traffic_incidents_checkbox_text), MenuConstants
                .ENABLE_TRAFFIC_INCIDENTS,
                appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.enable_traffic_flow_checkbox_text), MenuConstants
                .ENABLE_TRAFFIC_FLOW,
                appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW)));

        //navigation mode
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.navigation_mode_label).toUpperCase(), MenuConstants
                .NAVIGATION_SELECTION_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.navigation_mode_real_label), MenuConstants.NAVIGATION_REAL_ID,
                !appPrefs.getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.navigation_mode_simulation_label), MenuConstants
                .NAVIGATION_SIMULATION_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION)));

        //unit type
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.unit_type_label).toUpperCase(), MenuConstants
                .UNIT_TYPE_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.imperial_label), MenuConstants.UNIT_TYPE_IMPERIAL_ID,
                appPrefs.getIntPreference(PreferenceTypes.K_UNIT_TYPE) == NavigationSettings.UNIT_TYPE_IMPERIAL));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.metric_label), MenuConstants
                .UNIT_TYPE_METRIC_ID, appPrefs.getIntPreference(PreferenceTypes.K_UNIT_TYPE) == NavigationSettings.UNIT_TYPE_METRIC));

        // voice guidance type
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.voice_guidance_type_label).toUpperCase(), MenuConstants
                .VOICE_GUIDANCE_TYPE_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.voice_guidance_none_label), MenuConstants.VOICE_GUIDANCE_NONE_ID,
                appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_NONE));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.voice_guidance_orthography_label), MenuConstants
                .VOICE_GUIDANCE_ORTHOGRAPHY_ID, appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.voice_guidance_phoneme_label), MenuConstants
                .VOICE_GUIDANCE_PHONEME_ID, appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_PHONEME));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_RADIO_TYPE, getResources().getString(R.string.voice_guidance_instruction_label), MenuConstants
                .VOICE_GUIDANCE_INSTRUCTION_ID, appPrefs.getIntPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE) == NavigationSettings.VOICE_GUIDANCE_INSTRUCTION));

        //route options
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_SUBTITLE_TYPE, getResources().getString(R.string.route_options_label).toUpperCase(), MenuConstants
                .ROUTE_OPTIONS_ID, false));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_highways), MenuConstants
                .ROUTE_AVOID_HIGHWAYS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_toll_roads), MenuConstants.ROUTE_AVOID_TOLL_ROADS_ID,
                appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_tunnels), MenuConstants
                .ROUTE_AVOID_TUNNELS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_unpaved_road), MenuConstants
                .ROUTE_AVOID_UNPAVED_ROAD_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_country_borders), MenuConstants
                .ROUTE_AVOID_COUNTRY_BORDERS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_ferries), MenuConstants
                .ROUTE_AVOID_FERRIES_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_hov_lanes), MenuConstants
                .ROUTE_AVOID_HOV_LANES_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES)));
        slidingList.add(new SlidingMenuItem(SlidingMenuItem.ITEM_CHECKBOX_TYPE, getResources().getString(R.string.avoid_train_transport), MenuConstants
                .ROUTE_AVOID_TRAIN_TRANSPORT_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT)));


        if (slidingListView != null) {
            slidingMenuAdapter = new SlidingMenuAdapter(this, slidingList);
            slidingListView.setAdapter(slidingMenuAdapter);
            slidingListView.setOnItemClickListener(new SlidingMenuItemClickListener());
        }

        slidingDrawerToggle = new ActionBarDrawerToggle(this, slidingDrawerLayout, null, R.string.app_name, R.string.app_name) {
        };

        slidingDrawerLayout.setDrawerListener(slidingDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        slidingDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        slidingDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * handles opening and closing the sliding right menu
     */
    public void openCloseSlidingMenu() {
        if (slidingDrawerLayout != null && !isSettingsMenuLocked) {
            if (slidingDrawerLayout.isDrawerOpen(slidingListView)) {
                slidingDrawerLayout.closeDrawer(slidingListView);
            } else {
                slidingDrawerLayout.openDrawer(slidingListView);
            }
        }
    }

    public void showEditDataPath() {
        final EditText mapDataPath = new EditText(this);
        mapDataPath.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mapDataPath.setText(appPrefs.getStringPreference(PreferenceTypes.K_MAP_DATA_PATH));

        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.new_map_data_path_massage)).setView(mapDataPath).setPositiveButton(getResources().getString(R
                .string.save_data_path_label), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                appPrefs.setPreference(PreferenceTypes.K_MAP_DATA_PATH, mapDataPath.getText().toString());
                appPrefs.savePreferences();
                Toast.makeText(MapActivity.this, getString(R.string.change_data_path_explanation), Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    public void showEditIpPath() {
        final EditText ipDataPath = new EditText(this);
        ipDataPath.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        ipDataPath.setText(appPrefs.getStringPreference(PreferenceTypes.K_IP_ADDRESS));

        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.new_ip_data_path_massage)).setView(ipDataPath).setPositiveButton(getResources().getString(R
                .string.save_data_path_label), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                appPrefs.setPreference(PreferenceTypes.K_IP_ADDRESS, ipDataPath.getText().toString());
                appPrefs.savePreferences();
                Toast.makeText(MapActivity.this, getString(R.string.change_data_path_explanation), Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    /**
     * enables the settings sliding menu
     */
    public void enableSettingsMenu() {
        isSettingsMenuLocked = false;
        slidingDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * disables the settings sliding menu
     */
    public void disableSettingsMenu() {
        isSettingsMenuLocked = true;
        slidingDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private class SlidingMenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mapFragment != null) {
                int type = slidingList.get(position).getMenuItemId();
                switch (type) {
                    case SlidingMenuItem.ITEM_RADIO_TYPE:
                        mapFragment.setRadioButtonChoice(slidingList.get(position).getMenuItemType());
                        if (!slidingList.get(position).isMenuItemSelected()) {

                            slidingList.get(position).setMenuItemSelected(true);
                            switch (slidingList.get(position).getMenuItemType()) {
                                case MenuConstants.MAP_REGION_EU_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_REGION, InitialiseSettings.REGION_EU);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_REGION_NA_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_REGION_NA_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_REGION, InitialiseSettings.REGION_NA);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_REGION_EU_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_STYLE_DAY_ITEM_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_STYLE, MapSettings.MAP_STYLE_DAY);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_STYLE_NIGHT_ITEM_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_STYLE_NIGHT_ITEM_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_STYLE, MapSettings.MAP_STYLE_NIGHT);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_STYLE_DAY_ITEM_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M2D_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M2D);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M2D_HEADING_UP);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M2D_NORTH_UP);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M3D_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M3D);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M3D_NORTH_UP);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_MAP_ORIENTATION, MapSettings.ORIENTATION_MODE_M3D_HEADING_UP);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.NAVIGATION_REAL_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_NAVIGATION_SIMULATION, false);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.NAVIGATION_SIMULATION_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.NAVIGATION_SIMULATION_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_NAVIGATION_SIMULATION, true);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.NAVIGATION_REAL_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.UNIT_TYPE_IMPERIAL_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_UNIT_TYPE, NavigationSettings.UNIT_TYPE_IMPERIAL);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.UNIT_TYPE_METRIC_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.UNIT_TYPE_METRIC_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_UNIT_TYPE, NavigationSettings.UNIT_TYPE_METRIC);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.UNIT_TYPE_IMPERIAL_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.VOICE_GUIDANCE_NONE_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE, NavigationSettings.VOICE_GUIDANCE_NONE);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_NONE_ID).setMenuItemSelected(true);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_PHONEME_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE, NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID).setMenuItemSelected(true);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_NONE_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_PHONEME_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.VOICE_GUIDANCE_PHONEME_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE, NavigationSettings.VOICE_GUIDANCE_PHONEME);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_PHONEME_ID).setMenuItemSelected(true);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_NONE_ID).setMenuItemSelected(false);
                                    break;
                                case MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID:
                                    appPrefs.setPreference(PreferenceTypes.K_VOICE_GUIDANCE_TYPE, NavigationSettings.VOICE_GUIDANCE_INSTRUCTION);
                                    appPrefs.savePreferences();
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID).setMenuItemSelected(true);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_NONE_ID).setMenuItemSelected(false);
                                    slidingList.get(MenuConstants.VOICE_GUIDANCE_PHONEME_ID).setMenuItemSelected(false);
                                    break;
                            }
                            if (slidingMenuAdapter != null) {
                                slidingMenuAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case SlidingMenuItem.ITEM_CHECKBOX_TYPE:
                        int checkBoxType = slidingList.get(position).getMenuItemType();

                        switch (checkBoxType) {
                            case MenuConstants.MAP_PAN_ID:
                                appPrefs.setPreference(PreferenceTypes.K_MAP_PANNING, !appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_PANNING));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_PANNING));
                                mapFragment.setMapGesture();
                                break;
                            case MenuConstants.MAP_ROTATE_ID:
                                appPrefs.setPreference(PreferenceTypes.K_MAP_ROTATING, !appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ROTATING));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ROTATING));
                                mapFragment.setMapGesture();
                                break;
                            case MenuConstants.MAP_TILT_ID:
                                appPrefs.setPreference(PreferenceTypes.K_MAP_TILTING, !appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_TILTING));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_TILTING));
                                mapFragment.setMapGesture();
                                break;
                            case MenuConstants.MAP_ZOOM_ID:
                                appPrefs.setPreference(PreferenceTypes.K_MAP_ZOOMING, !appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ZOOMING));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ZOOMING));
                                mapFragment.setMapGesture();
                                break;
                            case MenuConstants.SHOW_BUILDINGS_IN_3D_MODE_ID:
                                appPrefs.setPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE, !appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE));
                                mapFragment.setShowBuildingsIn3DMode(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.ROTATE_HEADING_ID:
                                appPrefs.setPreference(PreferenceTypes.K_ROTATE_HEADING, !appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
                                mapFragment.rotateHeading(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.MAP_POSITION_FOLLOW_USER_ID:
                                appPrefs.setPreference(PreferenceTypes.K_FOLLOW_USER_POSITION, !appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION));
                                mapFragment.setFollowUserPosition(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.SHOW_MAP_TERRAIN_ID:
                                appPrefs.setPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE, !appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE));
                                mapFragment.setShowTerrainMode(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.ENABLE_TRAFFIC_INCIDENTS:
                                appPrefs.setPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS, !appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS));
                                mapFragment.setEnableTrafficIncidents(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.ENABLE_TRAFFIC_FLOW:
                                appPrefs.setPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW, !appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW));
                                mapFragment.setEnableTrafficFlow(slidingList.get(position).isMenuItemSelected());
                                break;
                            case MenuConstants.ROUTE_AVOID_HIGHWAYS_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_HIGHWAYS, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_HIGHWAYS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS));
                                break;
                            case MenuConstants.ROUTE_AVOID_TOLL_ROADS_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_TOLL_ROADS, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_TOLL_ROADS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS));
                                break;
                            case MenuConstants.ROUTE_AVOID_TUNNELS_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_TUNNELS, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_TUNNELS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS));
                                break;
                            case MenuConstants.ROUTE_AVOID_UNPAVED_ROAD_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_UNPAVED_ROAD_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD));
                                break;
                            case MenuConstants.ROUTE_AVOID_FERRIES_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_FERRIES, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_FERRIES_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES));
                                break;
                            case MenuConstants.ROUTE_AVOID_COUNTRY_BORDERS_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_COUNTRY_BORDERS_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS));
                                break;
                            case MenuConstants.ROUTE_AVOID_HOV_LANES_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_HOV_LANES, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_HOV_LANES_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES));
                                break;
                            case MenuConstants.ROUTE_AVOID_TRAIN_TRANSPORT_ID:
                                appPrefs.setPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT, !appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT));
                                appPrefs.savePreferences();
                                slidingList.get(position).setMenuItemSelected(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT));
                                mapFragment.setRouteOption(MenuConstants.ROUTE_AVOID_TRAIN_TRANSPORT_ID, appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT));
                                break;
                        }
                        if (slidingMenuAdapter != null) {
                            slidingMenuAdapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;

                }
            }

            if (slidingDrawerLayout.isDrawerOpen(slidingListView)) {
                slidingDrawerLayout.closeDrawer(slidingListView);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    //File operations - Clear file, read file, write file

    /**
     * Clears the existing file by writing an empty string in MODE_PRIVATE
    */
    void cleanFile()
    {
        String filename = "myfile";
        String blankString = "";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(blankString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the file
     */
    void writeToFile(String history) throws IOException {
        String filename = "myfile";
        String string = history;
        String blankString = "";
        FileOutputStream outputStream;

        readfile();

        //If the existing file is selected, then remove it from the file and create a new entry of the same.
        //Thus the entry is automatically moved to the top of the file.
        for(int i = 0; i < fileContents.size(); i++)
        {
            if(fileContents.get(i).equals(string))
            {
                fileContents.remove(i);
            }
        }

        if(fileContents.size() < 10)
        {
            fileContents.add(string);
        }
        else
        {
            fileContents.remove(0);
            fileContents.add(string);
        }


        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(blankString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < fileContents.size(); i++)
        {
            String value = fileContents.get(i) + "\n";
            try {
                outputStream = openFileOutput(filename, Context.MODE_APPEND);
                outputStream.write(value.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Read the file and store it in an arraylist
    void readfile() throws IOException {

        fileContents.clear();
        recentHistoryDestinationName.clear();
        totalItemsInHistory = 0;

        if(fileExistance("myfile")) {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("myfile")));

            String inputString;

            StringBuffer stringBuffer = new StringBuffer();

            while ((inputString = inputReader.readLine()) != null) {
                fileContents.add(inputString);
                String historyDetails[] = inputString.split("\t");
                recentHistoryDestinationName.add(historyDetails[0]);
                totalItemsInHistory++;
            }
            Log.d("Stringdata", String.valueOf(fileContents));
        }
    }

    //Check if the file exists in the internal memory
    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public HeaderFragment getHeaderObjectReference()
    {
        return headerFragment;
    }

    public class processObjFile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.d("asyncTask","started");
            OBJParser parser=new OBJParser(getBaseContext());
            parser.parseOBJ();
            Log.d("asyncTask","end of async");
            return null;
        }


        @Override
        protected void onPreExecute() {



        }

        @Override
        protected void onPostExecute(Void param) {

        }



        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    public void moveToRouteMid()
    {
        mapFragment.pointToMidRoute();

    }
}