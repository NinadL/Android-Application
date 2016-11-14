package com.telenav.sdk_sample.ui.map;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.telenav.entity.service.model.common.GeoPoint;
import com.telenav.foundation.vo.LatLon;
import com.telenav.map.engine.GLMapAnnotation;
import com.telenav.map.engine.GLMapImageAnnotation;
import com.telenav.map.engine.MapSelectedPickable;
import com.telenav.onboard.index.address.datatype.ShapePoints;
import com.telenav.sdk.external.ExternalDisplayManager;
import com.telenav.sdk.location.LocationListener;
import com.telenav.sdk.location.LocationManager;
import com.telenav.sdk.map.InitialiseManager;
import com.telenav.sdk.map.MapListener;
import com.telenav.sdk.map.MapSettings;
import com.telenav.sdk.map.MapView;
import com.telenav.sdk.navigation.controller.NavigationManager;
import com.telenav.sdk.navigation.controller.RouteManager;
import com.telenav.sdk.navigation.listener.NavigationRouteListener;
import com.telenav.sdk.navigation.listener.NavigationStatusListener;
import com.telenav.sdk.navigation.listener.RouteListener;
import com.telenav.sdk.navigation.listener.VoiceGuidanceListener;
import com.telenav.sdk.navigation.model.DisplayRouteInfo;
import com.telenav.sdk.navigation.model.DynamicRerouteInfo;
import com.telenav.sdk.navigation.model.Edge;
import com.telenav.sdk.navigation.model.JunctionViewInfo;
import com.telenav.sdk.navigation.model.LocationArrivalInfo;
import com.telenav.sdk.navigation.model.ManeuverInfo;
import com.telenav.sdk.navigation.model.NavigationData;
import com.telenav.sdk.navigation.model.NavigationSettings;
import com.telenav.sdk.navigation.model.NavigationStatus;
import com.telenav.sdk.navigation.model.Point;
import com.telenav.sdk.navigation.model.RouteError;
import com.telenav.sdk.navigation.model.RouteInfo;
import com.telenav.sdk.navigation.model.RouteOption;
import com.telenav.sdk.navigation.model.RouteSettings;
import com.telenav.sdk.navigation.model.ScreenPoint;
import com.telenav.sdk.navigation.model.Segment;
import com.telenav.sdk.navigation.model.Status;
import com.telenav.sdk.navigation.model.TravelEstimation;
import com.telenav.sdk.navigation.model.TurnType;
import com.telenav.sdk.navigation.model.VoiceGuidance;
import com.telenav.sdk.navigation.model.WayPoint;
import com.telenav.sdk.navigation.model.WhereAmIInfo;
import com.telenav.sdk_sample.R;
import com.telenav.sdk_sample.application.ApplicationPreferences;
import com.telenav.sdk_sample.application.PreferenceTypes;
import com.telenav.sdk_sample.application.SdkSampleApplication;
import com.telenav.sdk_sample.connectivity.NetworkUtils;
import com.telenav.sdk_sample.search.SearchManager;
import com.telenav.sdk_sample.search.SuggestionItem;
import com.telenav.sdk_sample.text.to.speech.TextToSpeechManager;
import com.telenav.sdk_sample.ui.Header.AutopilotStatusDecisions;
import com.telenav.sdk_sample.ui.Header.HeaderFragment;
import com.telenav.sdk_sample.ui.menu.MenuConstants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * Defines the fragment that encapsulates the map
 */
public class MapFragment extends Fragment implements MapListener, LocationListener, View.OnClickListener, RouteListener, VoiceGuidanceListener, NavigationRouteListener,
        NavigationStatusListener {

    //usa default location

    public static final double EU_DEFAULT_LON = 23.593849;

    public static final double EU_DEFAULT_LAT = 46.775192;

    //location

    /**
     * the poi's id that is created on the map (while the user is not in navigation)
     */
    private static final int MAP_LONG_CLICK_POI_ID = 10;

    /**
     * the poi's id that is created on the map during navigation(for waypoint along route or destination change)
     */
    private static final int NAVIGATION_LONG_CLICK_POI_ID = 11;

    /**
     * last known location
     */
    public static Location lastKnownLocation;

    /**
     * the map
     */
    private MyMapView mapView;

    /**
     * the map settings
     */
    private MapSettings mapSettings;

    /**
     * list view
     */
    private ListView searchResultsListView;

    /**
     * loading indicator
     */
    private ProgressBar searchResultLoadingIndicator;

    /**
     * the layout for search view
     */
    private RelativeLayout topBarLayout;

    /**
     * search result layout
     */
    private LinearLayout searchResultLayout;

    /**
     * search result no results text view
     */
    private TextView noResultsTextView;

    /**
     * list of search results coordinates
     */
    private List<GeoPoint> searchResultCoordinates;

    /**
     * search result annotation that will be displayed on the map
     */
    private GLMapImageAnnotation searchResultAnnotation;

    /**
     * suggestions list view
     */
    private ListView suggestionsListView;
    private ListView recentHistoryListView;

    /**
     * suggestions adapter
     */
    private ArrayAdapter<SuggestionItem> suggestionsAdapter;

    /**
     * TTS manager
     */
    private TextToSpeechManager textToSpeechManager;

    /**
     * route option
     */
    private RouteOption routeOption;

    /**
     * the layout containing the routes buttons
     */
    private LinearLayout routesButtons;

    /**
     * the button corresponding to the first route
     */
    private Button firstRoute;

    /**
     * the button corresponding to the second route
     */
    private Button secondRoute;

    /**
     * the button corresponding to the third route
     */
    private Button thirdRoute;

    /**
     * integer representing the route that was selected
     */
    private int selectedRoute;

    /**
     * the route if
     */
    private String routeID;

    /**
     * the list of routes
     */
    private List<RouteInfo> routes;

    /**
     * the calculate route button
     */
    private Button calculateRouteButton;

    /**
     * the navigation button
     */
    private Button naviButton;

    private LinearLayout travelEstimation;


    private TextView distanceEstimated;
    private TextView arrivalTime;
    private TextView remainingTime;

    private ImageButton slidingMenu;

    private ImageButton zoomView;

    private TextView currentStreetName;

    Timer myTimer1;

    /**
     * next street name view
     */
    private TextView nextStreetNameTextView;

    /**
     * the navigation settings
     */
    private NavigationSettings navigationSettings;

    private ImageView junctionViewImage;

    /**
     * current poi on map
     */
    private WayPoint poiOnMap;

    /**
     * way-points layout (contains two options for add way-points: along route, as new destination)
     */
    private LinearLayout waypointsLayout;

    private GLMapImageAnnotation poiOnMapAnnotation;

    /**
     * the cancel button
     */
    private Button cancelButton;

    /**
     * the application preferences
     */
    private ApplicationPreferences appPrefs;

    private boolean isStreetLevelZoomEnabled = false;

    private Activity mainActivity;

    String destinationName = "";

    TextView destinationNameTextView;

    Button recentSearch;

    ImageButton closeNavigation;

    NavigationData navigationData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MyMapView) view.findViewById(R.id.mapView);

        //the map listener MUST be set before calling the init method
        mapView.setMapListener(this);
        //initialize map
        mapSettings = mapView.initMap();


        appPrefs = SdkSampleApplication.getInstance().getApplicationPreferences();
        //set map settings
        mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M3D_HEADING_UP);
        setMapGesture();
        mapSettings.setMapStyle(MapSettings.MAP_STYLE_NIGHT);
        mapSettings.setFollowUserPosition(true);
        mapSettings.setEnableHeadingRotation(appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
        mapSettings.setShowBuildingIn3D(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_BUILDINGS_IN_3D_MODE));
        mapSettings.setShowTerrain(appPrefs.getBooleanPreference(PreferenceTypes.K_SHOW_MAP_TERRAIN_MODE));
        mapSettings.setShowTrafficIncidents(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_INCIDENTS));
        mapSettings.setShowTrafficFlow(appPrefs.getBooleanPreference(PreferenceTypes.K_ENABLE_TRAFFIC_FLOW));
        mapSettings.setLocationType(MapSettings.LOCATION_TYPE_REAL);
        mapSettings.setZoomLevel(8.0f, 0.5f);

        //routing
        RouteManager.getInstance().addRouteListener(this);
        //navigation
        NavigationManager.getInstance().addNavigationRouteListener(this);
        NavigationManager.getInstance().addVoiceGuidanceListener(this);
        NavigationManager.getInstance().addNavigationStatusListener(this);
        LocationManager.getInstance().getLocationListeners().add(this);

        initialiseViews(view);

        if (ExternalDisplayManager.getInstance().isExternalDisplayAvailable()) {
            ExternalDisplayManager.getInstance().setExternalMapSettings(mapSettings);
            ExternalDisplayManager.getInstance().startRenderOnExternalDisplay(getActivity(), 1280, 720);
        }
        AutopilotStatusDecisions autopilotStatusDecisions = new AutopilotStatusDecisions();
        autopilotStatusDecisions.initTimer();

        return view;
    }

    /**
     * sets map zoom level
     * @param zoomLevel map zoom level
     * @param animationTime zoom level animation time
     */
    public void setZoomLevel(float zoomLevel, float animationTime) {
        mapSettings.setZoomLevel(zoomLevel, animationTime);
    }

    /**
     * returns the map view
     * @return
     */
    public MapView getMapView() {
        return mapView;
    }

    private void initialiseViews(View view) {
        topBarLayout = (RelativeLayout) view.findViewById(R.id.topBarLayout);

        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = (EditText) ((SearchView) view.findViewById(R.id.searchView)).findViewById(searchSrcTextId);
        searchEditText.setTextSize(22);

        ((SearchView) view.findViewById(R.id.searchView)).setOnTouchListener(new SearchView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                recentHistoryListView.setVisibility(View.GONE);
                recentHistoryListView.setAdapter(null);
                return false;
            }
        });

        ((SearchView) view.findViewById(R.id.searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                switch (SearchManager.getInstance().getSearchType()) {
//                    case SearchManager.SEARCH_OFFLINE:
                        topBarLayout.setVisibility(View.GONE);
                        searchResultLayout.setVisibility(View.VISIBLE);
                        searchResultLoadingIndicator.setVisibility(View.VISIBLE);
                        suggestionsListView.setVisibility(View.GONE);
                        suggestionsListView.setAdapter(null);
                        recentHistoryListView.setVisibility(View.GONE);
                        recentHistoryListView.setAdapter(null);
                        ((MapActivity) mainActivity).performSearch(s, lastKnownLocation);
//                        break;
//                    case SearchManager.SEARCH_ONLINE:
//                        if (NetworkUtils.isInternetAvailable(mainActivity)) {
//                            suggestionsListView.setVisibility(View.GONE);
//                            suggestionsListView.setAdapter(null);
//                            topBarLayout.setVisibility(View.GONE);
//                            searchResultLayout.setVisibility(View.VISIBLE);
//                            searchResultLoadingIndicator.setVisibility(View.VISIBLE);
//                            ((MapActivity) mainActivity).performSearch(s, lastKnownLocation);
//                        } else {
//                            Toast.makeText(mainActivity, getString(R.string.no_internet_connection_label), Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if ((s != null) && (s.length() > 0)) {
                    recentHistoryListView.setVisibility(View.GONE);
                    recentHistoryListView.setAdapter(null);
                    switch (SearchManager.getInstance().getSearchType()) {
                        case SearchManager.SEARCH_OFFLINE:
                            ((MapActivity) mainActivity).searchForSuggestions(s, lastKnownLocation);
                            break;
                        case SearchManager.SEARCH_ONLINE:
                            if (NetworkUtils.isInternetAvailable(mainActivity)) {
                                ((MapActivity) mainActivity).searchForSuggestions(s, lastKnownLocation);
                            }
                            break;
                    }
                } else {
                    suggestionsListView.setVisibility(View.GONE);
                    suggestionsListView.setAdapter(null);
                }
                return false;
            }
        });

        searchResultLayout = (LinearLayout) view.findViewById(R.id.searchResultLayout);
        searchResultsListView = (ListView) view.findViewById(R.id.searchResultList);

        //This is the point where we store the search in the history
        //(When we click on the destination to calculate routes, simultaniously we store the result in the history)
        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GeoPoint geoPoint = searchResultCoordinates.get(position);
                destinationName = String.valueOf(searchResultsListView.getItemAtPosition(position));
                String historyString = destinationName.replace("\n", "\t");
                String history = historyString + "\t" + geoPoint.getLatitude() + "\t" + geoPoint.getLongitude();
                try {
                    ((MapActivity)getActivity()).writeToFile(history);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showSearchResultOnMap(geoPoint);
            }
        });

        searchResultLoadingIndicator = (ProgressBar) searchResultLayout.findViewById(R.id.loadingIndicator);
        noResultsTextView = (TextView) view.findViewById(R.id.noResultsTextView);
        view.findViewById(R.id.closeSearchResultsButton).setOnClickListener(this);


        //Set the listeners for all the buttons
        routesButtons = (LinearLayout) view.findViewById(R.id.routes_buttons);
        firstRoute = (Button) view.findViewById(R.id.first_route);
        firstRoute.setOnClickListener(this);
        secondRoute = (Button) view.findViewById(R.id.second_route);
        secondRoute.setOnClickListener(this);
        thirdRoute = (Button) view.findViewById(R.id.third_route);
        thirdRoute.setOnClickListener(this);
        naviButton = (Button) view.findViewById(R.id.navi_button);
        naviButton.setOnClickListener(this);
        calculateRouteButton = (Button) view.findViewById(R.id.calculateRouteButton);
        calculateRouteButton.setOnClickListener(this);
        slidingMenu = (ImageButton) view.findViewById(R.id.slidingMenuButton);
        slidingMenu.setOnClickListener(this);
        travelEstimation = (LinearLayout) view.findViewById(R.id.travel_estimation);
        closeNavigation = (ImageButton) view.findViewById(R.id.close_navigation);
        closeNavigation.setOnClickListener(this);
        recentSearch = (Button) view.findViewById(R.id.recent_history);
        recentSearch.setOnClickListener(this);
        zoomView = (ImageButton) view.findViewById(R.id.zoom_view);
        zoomView.setOnClickListener(this);

        //junction view

        junctionViewImage = (ImageView) view.findViewById(R.id.junctionViewImage);

        //waypoints buttons
        waypointsLayout = (LinearLayout) view.findViewById(R.id.waypointsLayout);
        view.findViewById(R.id.alongRouteButton).setOnClickListener(this);
        view.findViewById(R.id.newDestinationButton).setOnClickListener(this);

        //current position
        view.findViewById(R.id.currentPositionButton).setOnClickListener(this);

        //exit button
        view.findViewById(R.id.exitButton).setOnClickListener(this);

        // suggestions list view
        suggestionsListView = (ListView) view.findViewById(R.id.suggestionsListView);
        suggestionsAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, new ArrayList<SuggestionItem>());
        suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SuggestionItem currentItem = suggestionsAdapter.getItem(position);
                if (currentItem != null) {
                    if (currentItem.getId() != null) {
                        // perform a search details request for this entity id
                        ((MapActivity) mainActivity).searchForDetails(currentItem.getId());
                    } else {
                        ((MapActivity) mainActivity).performSearch(currentItem.getQuery(), lastKnownLocation);
                    }
                }
            }
        });


        //Code for recent search
        recentHistoryListView = (ListView) view.findViewById(R.id.recentHistoryListView);
        recentHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> reverseFileContents = ((MapActivity)getActivity()).fileContents;
                Collections.reverse(reverseFileContents);
                String string = reverseFileContents.get(position);
                String historyDetails[] = string.split("\t");
//                Log.d("Name: ", historyDetails[0]);
//                Log.d("Address: ", historyDetails[1]);
//                Log.d("Latitude: ", historyDetails[2]);
//                Log.d("Longitude: ", historyDetails[3]);
                destinationName = historyDetails[0];
                Point point = new Point(Double.valueOf(historyDetails[2]),Double.valueOf(historyDetails[3]) );
                calculateRoute(point);
                recentHistoryListView.setVisibility(View.INVISIBLE);
                try {
                    ((MapActivity)getActivity()).writeToFile(string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //cancel button
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        routeOption = new RouteOption();
        navigationSettings = NavigationManager.getInstance().getNavigationSettings();
        distanceEstimated = (TextView) view.findViewById(R.id.distance_estimation);
        arrivalTime = (TextView) view.findViewById(R.id.arrival_time);
        destinationNameTextView = (TextView) view.findViewById(R.id.destination_name);
        currentStreetName = (TextView) view.findViewById(R.id.currentStreetName);
        remainingTime = (TextView) view.findViewById(R.id.remaining_time);

    }

    /**
     * sets search results
     * @param searchResultList search result list
     * @param searchResultsCoordinates search result coordinates
     */
    public void setSearchResults(final List<String> searchResultList, List<GeoPoint> searchResultsCoordinates) {
        this.searchResultCoordinates = searchResultsCoordinates;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultLayout.setVisibility(View.VISIBLE);
                searchResultLoadingIndicator.setVisibility(View.GONE);
                if (suggestionsListView != null) {
                    suggestionsListView.setVisibility(View.GONE);
                }
                if (topBarLayout != null) {
                    topBarLayout.setVisibility(View.GONE);
                }
                if (searchResultList.size() == 0) {
                    noResultsTextView.setVisibility(View.VISIBLE);
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, searchResultList)
                    {
                        //This override is required for changing the UI for the part where we view the results
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent)
                        {

                            GradientDrawable gd = new GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT,
                                    new int[] {0xFF3A59B3,0xFF1086EA});

                            View view = super.getView(position, convertView, parent);

                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setBackground(gd);
                            textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                            textView.setTextSize(22);
                            textView.setTextColor(Color.WHITE);
                            return view;
                        }
                    };
                    searchResultsListView.setAdapter(adapter);
                    searchResultsListView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * updates suggestions results info
     * @param suggestionsResults suggestions results
     */
    public void updateSuggestionsResultsInfo(final List<SuggestionItem> suggestionsResults) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchResultLayout.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (suggestionsResults.size() > 0) {
                    suggestionsListView.setVisibility(View.VISIBLE);
                    suggestionsAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, suggestionsResults);
                    suggestionsListView.setAdapter(MapFragment.this.suggestionsAdapter);
                } else {
                    MapFragment.this.suggestionsListView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * show entity details info on map
     * @param currentGeopoint current geopoint
     */
    public void showSearchResultOnMap(final GeoPoint currentGeopoint) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchResultLayout != null) {
                   // searchResultLayout.setVisibility(View.GONE);
                }
                if (suggestionsListView != null) {
                    suggestionsListView.setVisibility(View.GONE);
                }
                if (topBarLayout != null) {
                    topBarLayout.setVisibility(View.GONE);
                }
                if (searchResultAnnotation != null) {
                    mapView.removeAnnotation(searchResultAnnotation);
                }
               // cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                calculateRouteButton.setVisibility(View.VISIBLE);
                if (mainActivity != null && isAdded()) {

                    //Code for custom routing (We do not enter destination, but we click on the map and route to that point)
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.generic_pin_on_map_icon_unfocused);
                    LatLon latLon = new LatLon();
                    latLon.setLat(currentGeopoint.getLatitude());
                    latLon.setLon(currentGeopoint.getLongitude());
                    searchResultAnnotation = new GLMapImageAnnotation(mainActivity, 0, bitmap, latLon);
                    mapView.addAnnotation(searchResultAnnotation);
                    mapView.moveTo(latLon);
                    poiOnMap = new WayPoint();
                    poiOnMap.setCoordinates(new Point(latLon.getLat(), latLon.getLon()));
                    mapSettings.setZoomLevel(13.0f, 0.5f);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = getActivity();
        //text to speech
        textToSpeechManager = new TextToSpeechManager();
        textToSpeechManager.initialiseTextToSpeech();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        if (ExternalDisplayManager.getInstance().isExternalDisplayAvailable()) {
            ExternalDisplayManager.getInstance().pauseRenderOnExternalDisplay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (ExternalDisplayManager.getInstance().isExternalDisplayAvailable()) {
            ExternalDisplayManager.getInstance().resumeRenderOnExternalDisplay();
        }
        if (mainActivity != null) {
            LocationManager.getInstance().startLocationUpdates(mainActivity.getApplicationContext());
        }
    }

    @Override
    public void onDestroyView() {
        MapView.destroyMap();
        LocationManager.getInstance().stopLocationUpdates();
        textToSpeechManager.release();
        super.onDestroyView();
    }

    @Override
    public void onActionUp(ScreenPoint screenPoint) {}

    @Override
    public void onMapRotate(float rotationAngle) {}

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onDoubleTap(ScreenPoint screenPoint) {}

    @Override
    public void onPinch() {}

    @Override
    public void onPan() {}

    @Override
    public void onRouteSelected(String routeName) {
        mapView.highlightRoute(routeName);
    }

    @Override
    public void onClick(MapSelectedPickable mapSelectedPickable, GLMapAnnotation mapAnnotation, ScreenPoint screenPoint) {
        if (waypointsLayout.getVisibility() == View.VISIBLE) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waypointsLayout.setVisibility(View.GONE);
                }
            });
            mapView.removeAnnotation(poiOnMapAnnotation);
        }
    }

    @Override
    public void onLongClick(MapSelectedPickable mapSelectedPickable, GLMapAnnotation mapAnnotation, ScreenPoint screenPoint) {
        if (searchResultLayout.getVisibility() != View.VISIBLE && mainActivity != null && isAdded()) {
            if (mapView != null) {
                if (!NavigationManager.getInstance().getNavigationData().isNavigationStarted()) {
                    mapView.clearRoutes();
                    if (poiOnMapAnnotation != null) {
                        mapView.removeAnnotation(poiOnMapAnnotation);
                    }
                }
            }
            Bitmap bitmap;
            int poiId;
            if (NavigationManager.getInstance().getNavigationData().isNavigationStarted()) {
                poiId = NAVIGATION_LONG_CLICK_POI_ID;
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.generic_pin_on_map_icon_unfocused);
            } else {
                poiId = MAP_LONG_CLICK_POI_ID;
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.generic_pin_on_map_icon_unfocused);
            }

            LatLon latLon = new LatLon();
            latLon.setLat(mapSelectedPickable.getLat());
            latLon.setLon(mapSelectedPickable.getLon());
            poiOnMapAnnotation = new GLMapImageAnnotation(mainActivity, 0, bitmap, latLon);
            poiOnMapAnnotation.setId(poiId);
            mapView.addAnnotation(poiOnMapAnnotation);
            mapView.moveTo(latLon);

            poiOnMap = new WayPoint();
            poiOnMap.setCoordinates(new Point(mapSelectedPickable.getLat(), mapSelectedPickable.getLon()));
        }

        if (mapSelectedPickable != null) {
            if (NavigationManager.getInstance().getNavigationData().isNavigationStarted()) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waypointsLayout.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchResultLayout.getVisibility() == View.VISIBLE) {
                            return;
                        }

                        routesButtons.setVisibility(View.GONE);
                        naviButton.setVisibility(View.GONE);
                        topBarLayout.setVisibility(View.GONE);
                        //cancelButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.INVISIBLE);
                        calculateRouteButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public void onMove(MapSelectedPickable mapSelectedPickable, GLMapAnnotation mapAnnotation, ScreenPoint screenPoint) {}

    @Override
    public void onDown(MapSelectedPickable mapSelectedPickable, GLMapAnnotation mapAnnotation, ScreenPoint screenPoint) {}

    @Override
    public void onCancel(MapSelectedPickable mapSelectedPickable, GLMapAnnotation mapAnnotation, ScreenPoint screenPoint) {}

    @Override
    public void onMapUpdateZoom(float updatedZoom) {}

    @Override
    public void onMapFrameUpdated() {}

    @Override
    public void onMapDeclinationChanged(double v) {}

    @Override
    public void onMapLoadStatusChanged(int mapLoadStatus) {
        if (mapLoadStatus == MapView.MAP_LOAD_STATUS_CREATE_RENDER) {
            LatLon latLon = new LatLon();
            if (lastKnownLocation != null) {
                latLon.setLat(lastKnownLocation.getLatitude());
                latLon.setLon(lastKnownLocation.getLongitude());
                mapView.moveTo(latLon);
                LocationManager.getInstance().updateLocation(lastKnownLocation);
            } else {
                Location location = new Location("");
                location.setLatitude(EU_DEFAULT_LAT);
                location.setLongitude(EU_DEFAULT_LON);
                latLon.setLat(EU_DEFAULT_LAT);
                latLon.setLon(EU_DEFAULT_LON);
                lastKnownLocation = location;
                mapView.moveTo(latLon);
                LocationManager.getInstance().updateLocation(location);
            }

            //set custom current vehicle position icon
            if (mainActivity != null) {
                ImageView imageView = new ImageView(getActivity().getApplicationContext());
                imageView.setMinimumHeight(76);
                imageView.setMinimumWidth(82);
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.navigation_icon));
                mapSettings.setCurrentVehiclePositionIconFromView(imageView);
            }

            mapSettings.setZoomLevel(8.0f, 0.5f);
        }
    }

    /**
     * release the created annotations
     */
    private void releaseAnnotations() {
        poiOnMapAnnotation = null;
        searchResultAnnotation = null;
    }


    //OnClick method for all the buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                calculateRouteButton.setVisibility(View.GONE);
                if (naviButton.getVisibility() == View.VISIBLE) {
                    ((MapActivity) mainActivity).enableSettingsMenu();
                    naviButton.setVisibility(View.GONE);
                    mapView.clearRoutes();
                    routesButtons.setVisibility(View.GONE);
                }
                if (mapView != null) {
                    mapView.removeAllAnnotation();
                    releaseAnnotations();
                }
                cancelButton.setVisibility(View.GONE);
                topBarLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.calculateRouteButton:
                calculateRouteButton.setVisibility(View.GONE);
                calculateRoute(poiOnMap.getCoordinates());
                break;
            case R.id.alongRouteButton:
                List<WayPoint> waypoints = new ArrayList<>();
                if (TextUtils.isEmpty(routeID)) {
                    waypoints.add(poiOnMap);
                    RouteManager.getInstance().getRouteSettings().setWaypoints(waypoints);
                    RouteManager.getInstance().updateRouteSettings(RouteManager.getInstance().getRouteSettings());
                } else {
                    waypoints.add(poiOnMap);
                    RouteManager.getInstance().getRouteSettings().setWaypoints(waypoints);
                    RouteManager.getInstance().updateRouteSettings(RouteManager.getInstance().getRouteSettings());
                }
                waypointsLayout.setVisibility(View.GONE);
                break;
            case R.id.newDestinationButton:
                if (searchResultAnnotation != null) {
                    //remove old destination if it was from search result
                    mapView.removeAnnotation(searchResultAnnotation);
                } else {
                    //remove old destination if it was from search result
                    mapView.removeAnnotation(MAP_LONG_CLICK_POI_ID);
                }

                RouteManager.getInstance().getRouteSettings().setDestination(poiOnMap);
                RouteManager.getInstance().updateRouteSettings(RouteManager.getInstance().getRouteSettings());
                waypointsLayout.setVisibility(View.GONE);
                break;
            case R.id.closeSearchResultsButton:
                searchResultsListView.setAdapter(null);
                if (searchResultAnnotation != null) {
                    mapView.removeAnnotation(searchResultAnnotation);
                }
                if (mapView != null) {
                    mapView.clearRoutes();
                }
                noResultsTextView.setVisibility(View.GONE);
                searchResultLoadingIndicator.setVisibility(View.GONE);
                topBarLayout.setVisibility(View.VISIBLE);
                searchResultLayout.setVisibility(View.GONE);
                routesButtons.setVisibility(View.GONE);
                naviButton.setVisibility(View.GONE);
                ((MapActivity) mainActivity).enableSettingsMenu();
                break;
            case R.id.slidingMenuButton:
                ((MapActivity) mainActivity).openCloseSlidingMenu();
                break;
            case R.id.first_route:
                firstRoute.setBackgroundColor(getResources().getColor(R.color.button_background_color));
                secondRoute.setBackgroundColor(getResources().getColor(R.color.white));
                thirdRoute.setBackgroundColor(getResources().getColor(R.color.white));
                selectedRoute = 0;
                mapView.highlightRoute(0);
                break;
            case R.id.second_route:
                secondRoute.setBackgroundColor(getResources().getColor(R.color.button_background_color));
                firstRoute.setBackgroundColor(getResources().getColor(R.color.white));
                thirdRoute.setBackgroundColor(getResources().getColor(R.color.white));
                selectedRoute = 1;
                mapView.highlightRoute(1);
                break;
            case R.id.third_route:
                thirdRoute.setBackgroundColor(getResources().getColor(R.color.button_background_color));
                secondRoute.setBackgroundColor(getResources().getColor(R.color.white));
                firstRoute.setBackgroundColor(getResources().getColor(R.color.white));
                selectedRoute = 2;
                mapView.highlightRoute(2);
                break;
            case R.id.navi_button:
                if (mainActivity != null && isAdded()) {
                    if (naviButton.getText().toString().equals(getResources().getString(R.string.start_navi_button_label)))
                    {
                        ((MapActivity)getActivity()).isNavigationStarted = true;
                        searchResultLayout.setVisibility(View.GONE);
                        routesButtons.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        slidingMenu.setVisibility(View.GONE);


                        naviButton.setText(getResources().getText(R.string.stop_navi_button_label));
                        //nextStreetNameTextView.setVisibility(View.VISIBLE);
//                        navigationDetails.setVisibility(View.VISIBLE);
//                        navigationDetailsExt.setVisibility(View.VISIBLE);
                        travelEstimation.setVisibility(View.VISIBLE);
                        currentStreetName.setVisibility(View.VISIBLE);
                        zoomView.setVisibility(View.VISIBLE);
                        zoomView.setImageResource(R.drawable.map_overview_inactive);
                        isStreetLevelZoomEnabled = true;

                        mapSettings.setZoomLevel(1.0f, 1.0f);

                        if (selectedRoute != -1) {
                            navigationSettings.setSimulationMode(appPrefs.getBooleanPreference(PreferenceTypes.K_NAVIGATION_SIMULATION));

                            mapSettings.setLocationType(MapSettings.LOCATION_TYPE_MATCHED);

                            NavigationManager.getInstance().startNavigation(routes.get(selectedRoute).getRouteId());
                        }
                        myTimer1 = new Timer("MyTimer1", true);
                        //myTimer1.scheduleAtFixedRate(new SendData(), 100, 500);


                        naviButton.setVisibility(View.GONE);
                    }
                    //This part of the code is never called
                    else {
                        if (textToSpeechManager != null) {
                            textToSpeechManager.stop();
                        }
                        ((MapActivity)getActivity()).isNavigationStarted = false;
                        myTimer1.cancel();
                        myTimer1.purge();
                        // nextStreetNameTextView.setVisibility(View.GONE);
//                        navigationDetails.setVisibility(View.GONE);
//                        navigationDetailsExt.setVisibility(View.GONE);
//                        travelEstimation.setVisibility(View.GONE);
                        currentStreetName.setVisibility(View.GONE);
                        junctionViewImage.setVisibility(View.GONE);
                        NavigationManager.getInstance().stopNavigation();
                        mapSettings.setLocationType(MapSettings.LOCATION_TYPE_REAL);
                        mapView.removeAllAnnotation();
                        releaseAnnotations();
                        ((MapActivity) mainActivity).enableSettingsMenu();
                    }
                }
                break;

            //This is used to stop navigation
            case R.id.close_navigation:
                if (textToSpeechManager != null) {
                    textToSpeechManager.stop();
                }
                myTimer1.cancel();
                myTimer1.purge();
//                ((MapActivity)getActivity()).isNavigationStarted = false;
                // nextStreetNameTextView.setVisibility(View.GONE);
//                navigationDetails.setVisibility(View.GONE);
//                navigationDetailsExt.setVisibility(View.GONE);
                travelEstimation.setVisibility(View.GONE);
                currentStreetName.setVisibility(View.GONE);
                junctionViewImage.setVisibility(View.GONE);
                zoomView.setVisibility(View.GONE);
                zoomView.setImageResource(R.drawable.map_overview_active);
                isStreetLevelZoomEnabled = false;
                slidingMenu.setVisibility(View.VISIBLE);
                NavigationManager.getInstance().stopNavigation();
                mapSettings.setLocationType(MapSettings.LOCATION_TYPE_REAL);
                mapView.removeAllAnnotation();
                releaseAnnotations();
                mapSettings.setZoomLevel(8.0f, 0.5f);
                ((MapActivity) mainActivity).enableSettingsMenu();
                break;
            case R.id.currentPositionButton:
                if (lastKnownLocation != null) {
                    LatLon latLon = new LatLon();
                    latLon.setLat(lastKnownLocation.getLatitude());
                    latLon.setLon(lastKnownLocation.getLongitude());
                    mapView.moveTo(latLon);
                }
                break;
            case R.id.exitButton:
                InitialiseManager.getInstance().release();
                ExternalDisplayManager.getInstance().stopRenderingOnExternalDisplay();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;

            case R.id.nextStreetNameView:
                NavigationManager.getInstance().playSegmentAudio(-1);
                break;

            case R.id.recent_history:
                try {
                    ((MapActivity)getActivity()).readfile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList<String> reverseArrayList = ((MapActivity)getActivity()).recentHistoryDestinationName;
                Collections.reverse(reverseArrayList); // <- You need to reverse the arraylist as the last entry should be the first one
                ((MapActivity)getActivity()).arrayAdapter = new ArrayAdapter(mainActivity, android.R.layout.simple_list_item_1, reverseArrayList);
                recentHistoryListView.setAdapter(((MapActivity)getActivity()).arrayAdapter);
                recentHistoryListView.setVisibility(View.VISIBLE);
                break;

            case R.id.zoom_view:
                if(isStreetLevelZoomEnabled)
                {
                    zoomView.setImageResource(R.drawable.map_overview_active);
                    mapSettings.setZoomLevel(8.0f, 0.5f);
                    mapSettings.setFollowUserPosition(!appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION));
                    mapSettings.setEnableHeadingRotation(!appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
                    isStreetLevelZoomEnabled = false;
                    pointToMidRoute();
                }
                else
                {
                    zoomView.setImageResource(R.drawable.map_overview_inactive);
                    mapSettings.setZoomLevel(1.0f, 1.0f);
                    mapSettings.setFollowUserPosition(appPrefs.getBooleanPreference(PreferenceTypes.K_FOLLOW_USER_POSITION));
                    mapSettings.setEnableHeadingRotation(appPrefs.getBooleanPreference(PreferenceTypes.K_ROTATE_HEADING));
                    isStreetLevelZoomEnabled = true;
                }

        }
    }

    /**
     * update UI when exit navigation
     */
    private void updateUIWhenExitNavigation() {
        if (mainActivity != null) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (waypointsLayout != null) {
                        waypointsLayout.setVisibility(View.GONE);
                    }
                    routesButtons.setVisibility(View.GONE);
//                    navigationDetails.setVisibility(View.GONE);
//                    navigationDetailsExt.setVisibility(View.GONE);
                    travelEstimation.setVisibility(View.GONE);
                    currentStreetName.setVisibility(View.GONE);
                    naviButton.setVisibility(View.GONE);
                    topBarLayout.setVisibility(View.VISIBLE);
                    selectedRoute = -1;
                    if (searchResultAnnotation != null) {
                        mapView.removeAnnotation(searchResultAnnotation);
                    }
                    if (mapView != null) {
                        mapView.removeAllAnnotation();
                        releaseAnnotations();
                        mapView.clearRoutes();
                    }
                    ((MapActivity) mainActivity).enableSettingsMenu();
                }
            });
        }
    }

    @Override
    public void onRouteCalculationCompleted(final List<RouteInfo> routes, String componentID) {
        if (routes != null && routes.size() > 0) {
            this.routes = routes;
            showRoutes(routes, MapView.ROUTE_RENDER_STYLE_PLANNING);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    topBarLayout.setVisibility(View.GONE);
                    suggestionsListView.setVisibility(View.GONE);
                    routesButtons.setVisibility(View.VISIBLE);
                    calculateRouteButton.setVisibility(View.GONE);
                    routesButtons.setVisibility(View.VISIBLE);
                    if (mainActivity != null && isAdded()) {
                        naviButton.setText(getResources().getText(R.string.start_navi_button_label));
                    }
                    mapView.highlightRoute(0);
                    firstRoute.setSelected(true);
                    selectedRoute = 0;
                    naviButton.setVisibility(View.VISIBLE);


                    switch (routes.size()) {
                        case 1:
                            firstRoute.setVisibility(View.GONE);
                            secondRoute.setVisibility(View.GONE);
                            thirdRoute.setVisibility(View.GONE);
                            break;
                        case 2:
                            firstRoute.setVisibility(View.VISIBLE);
                            secondRoute.setVisibility(View.VISIBLE);
                            thirdRoute.setVisibility(View.GONE);
                            break;
                        case 3:
                            firstRoute.setVisibility(View.VISIBLE);
                            secondRoute.setVisibility(View.VISIBLE);
                            thirdRoute.setVisibility(View.VISIBLE);
                            break;
                    }

                    ((MapActivity) mainActivity).disableSettingsMenu();
                }
            });
        }
    }

    @Override
    public void onRouteCalculationFailed(RouteError routeError, String componentID) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity, getString(R.string.route_calculation_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showRoutes(List<RouteInfo> routes, int routeRenderStyle) {
        Rect rect = new Rect(0, 0, mapView.getWidth(), mapView.getHeight());
        if (mapView != null && routes != null && rect != null) {
            mapView.showRoutes(routes, rect, routeRenderStyle);
        }
    }

    /**
     * Checks weather the map style or orientation should be modified, and makes the changes accordingly
     * @param id the value corresponding to the clicked radio button
     */
    public void setRadioButtonChoice(int id) {
        if (mapSettings != null) {
            switch (id) {
                case MenuConstants.MAP_STYLE_DAY_ITEM_ID:
                    mapSettings.setMapStyle(MapSettings.MAP_STYLE_DAY);
                    break;
                case MenuConstants.MAP_STYLE_NIGHT_ITEM_ID:
                    mapSettings.setMapStyle(MapSettings.MAP_STYLE_NIGHT);
                    break;
                case MenuConstants.MAP_ORIENTATION_M3D_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M3D);
                    break;
                case MenuConstants.MAP_ORIENTATION_M3D_NORTH_UP_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M3D_NORTH_UP);
                    break;
                case MenuConstants.MAP_ORIENTATION_M3D_HEADING_UP_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M3D_HEADING_UP);
                    break;
                case MenuConstants.MAP_ORIENTATION_M2D_HEADING_UP_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M2D_HEADING_UP);
                    break;
                case MenuConstants.MAP_ORIENTATION_M2D_NORTH_UP_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M2D_NORTH_UP);
                    break;
                case MenuConstants.MAP_ORIENTATION_M2D_ID:
                    mapSettings.setOrientationMode(MapSettings.ORIENTATION_MODE_M2D);
                    break;
                case MenuConstants.UNIT_TYPE_IMPERIAL_ID:
                    navigationSettings.setUnitType(NavigationSettings.UNIT_TYPE_IMPERIAL);
                    break;
                case MenuConstants.UNIT_TYPE_METRIC_ID:
                    navigationSettings.setUnitType(NavigationSettings.UNIT_TYPE_METRIC);
                    break;
                case MenuConstants.VOICE_GUIDANCE_NONE_ID:
                    navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_NONE);
                    break;
                case MenuConstants.VOICE_GUIDANCE_ORTHOGRAPHY_ID:
                    navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_ORTHOGRAPHY);
                    break;
                case MenuConstants.VOICE_GUIDANCE_PHONEME_ID:
                    navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_PHONEME);
                    break;
                case MenuConstants.VOICE_GUIDANCE_INSTRUCTION_ID:
                    navigationSettings.setVoiceGuidanceType(NavigationSettings.VOICE_GUIDANCE_INSTRUCTION);
                    break;
            }
        }
    }

    /**
     * sets the route option for the route that will be calculated
     * @param option - the option that will be enabled / disabled
     * @param isEnabled - true if the option should be enabled/disabled
     */
    public void setRouteOption(int option, boolean isEnabled) {
        switch (option) {
            case MenuConstants.ROUTE_AVOID_HIGHWAYS_ID:
                routeOption.setAvoidHighway(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_TOLL_ROADS_ID:
                routeOption.setAvoidTollRoad(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_TUNNELS_ID:
                routeOption.setAvoidTunnel(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_UNPAVED_ROAD_ID:
                routeOption.setAvoidUnpavedRoad(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_COUNTRY_BORDERS_ID:
                routeOption.setAvoidCountryBorders(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_FERRIES_ID:
                routeOption.setAvoidFerry(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_HOV_LANES_ID:
                routeOption.setAvoidHOVLane(isEnabled);
                break;
            case MenuConstants.ROUTE_AVOID_TRAIN_TRANSPORT_ID:
                routeOption.setAvoidCarTrain(isEnabled);
                break;
        }
    }

    /**
     * makes the changes when showBuildingsIn3DMode checkbox was pressed
     */
    public void setShowBuildingsIn3DMode(boolean showBuildingsIn3DMode) {
        mapSettings.setShowBuildingIn3D(showBuildingsIn3DMode);
    }

    /**
     * makes the changes when rotate heading checkbox was pressed
     */
    public void rotateHeading(boolean rotateHeading) {
        mapSettings.setEnableHeadingRotation(rotateHeading);
    }

    /**
     * makes the changes when follow user position checkbox was pressed
     */
    public void setFollowUserPosition(boolean followUserPosition) {
        mapSettings.setFollowUserPosition(followUserPosition);
    }

    /**
     * makes the changes when terrain checkbox was pressed
     */
    public void setShowTerrainMode(boolean showTerrain) {
        mapSettings.setShowTerrain(showTerrain);
    }

    /**
     * makes the changes when enableTrafficIncidents radio button was pressed
     */
    public void setEnableTrafficIncidents(boolean enableTrafficIncidents) {
        mapSettings.setShowTrafficIncidents(enableTrafficIncidents);
    }

    /**
     * makes the changes when enableTrafficFlow radio button was pressed
     */
    public void setEnableTrafficFlow(boolean enableTrafficFlow) {
        mapSettings.setShowTrafficFlow(enableTrafficFlow);
    }

    @Override
    public void onNewNavigationRoute(Status eventStatus, DisplayRouteInfo displayRouteInfo) {
        if (eventStatus == Status.Successful) {
            if (displayRouteInfo != null) {
                routeID = displayRouteInfo.getRouteId();
                // update route on the map
                mapView.clearRoutes();
                mapView.addRoute(displayRouteInfo, MapView.ROUTE_STYLE);
                mapView.highlightRoute(0);
            }
        }
    }

    @Override
    public void onNavigationReroute(Status eventStatus, DisplayRouteInfo displayRouteInfo) {
        if (eventStatus == Status.Successful) {
            if (displayRouteInfo != null) {
                routeID = displayRouteInfo.getRouteId();
            }
            // update route on the map
            if (displayRouteInfo != null) {
                mapView.clearRoutes();
                //Add route button is customized
                mapView.addRoute(displayRouteInfo, MapView.ROUTE_STYLE);
                mapView.highlightRoute(0);
            }
        }
    }

    @Override
    public void onNavigationDynamicReroute(Status eventStatus, DynamicRerouteInfo dynamicRerouteInfo) {

    }

    //This method will continuously update the UI
    // (Time and distance remaining to the destination, the current street namre and the next street name and the turn icon)
    @Override
    public void onUpdateNavigationStatus(Status eventStatus, final NavigationStatus status)
    {
        if (NavigationManager.getInstance().getNavigationData() != null) {
            navigationData = NavigationManager.getInstance().getNavigationData();

            if (NavigationManager.getInstance().getNavigationData().isNavigationStarted()) {
                showTurnArrowOnMap();

                if (!TextUtils.isEmpty(NavigationManager.getInstance().getNavigationData().getNextStreetName()) && mainActivity != null) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setImageResource(navigationData, MapActivity.headerFragment);

                            //Remove this later
                            MapActivity.headerFragment.setNormalAndAutopilotSpeedLimit();
                            MapActivity.headerFragment.setNextStreetName(NavigationManager.getInstance().getNavigationData().getNextStreetName());
                            double distanceToTurn = (double) NavigationManager.getInstance().getNavigationData().getDistanceToTurn();
                            //Convert the distance to miles and round it off
                            distanceToTurn = Math.round(((float)distanceToTurn* 0.000621371)*10.0)/10.0;
                            MapActivity.headerFragment.setTurnDistance(distanceToTurn);

                            TravelEstimation travelEstimation = NavigationManager.getInstance().getNavigationData().getDestinationTravelEstimation();

                            if(travelEstimation != null) {
                                int hours = travelEstimation.getArriveTimeInMinutes() / 60; //since both are ints, you get an int
                                int minutes = travelEstimation.getArriveTimeInMinutes() % 60;

                                String _24HourTime = String.valueOf(hours)+":"+String.valueOf(minutes);

                                try {
                                    //Convert 24 hr format to 12 hr format

                                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                    Date _24HourDt = null;
                                    _24HourDt = _24HourSDF.parse(_24HourTime);
                                    arrivalTime.setText(_12HourSDF.format(_24HourDt));

                                    //Get remaining time
                                    Date currentTime = _24HourSDF.parse(_24HourSDF.format(new Date()));
                                    Date arrivalTime = _24HourDt;

                                    long diff = arrivalTime.getTime() - currentTime.getTime();
                                    long diffMinutes = diff / (60 * 1000);

                                    int remainingHours = (int)diffMinutes / 60; //since both are ints, you get an int
                                    int remainingMinutes  = (int)diffMinutes % 60;
                                    System.out.printf("%d:%02d", hours, minutes);

                                    if(remainingHours != 0)
                                    {
                                        remainingTime.setText(String.valueOf(remainingHours) + " hr " + String.valueOf(remainingMinutes) + " min");
                                    }
                                    else
                                    {
                                        remainingTime.setText(String.valueOf(remainingMinutes) + " min");
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                distanceEstimated.setText(String.valueOf(convertMetersToMiles(travelEstimation.getDistance())) + " mi");
                            }
                            destinationNameTextView.setText(destinationName);
                            currentStreetName.setText(NavigationManager.getInstance().getNavigationData().getCurrentStreetName());
                        }
                    });
                }
            }

        }

        if (status.getNavigationStateNotification() == NavigationStatus.NOTIFICATION_NAV_SESSION_ENDED) {
            if (textToSpeechManager != null) {
                textToSpeechManager.stop();
            }
            updateUIWhenExitNavigation();
        } else {
            showJunctionViewInfo(status);
        }
    }

    @Override
    public void onWaypointReached(int waypointIndex) {}

    @Override
    public void onWaypointLeft(int waypointIndex) {}

    @Override
    public void onDestinationReached(LocationArrivalInfo locationArrivalInfo) {}

    @Override
    public void onWhereAmIInfoUpdated(Status status, WhereAmIInfo whereAmIInfo) {

    }

    @Override
    public void onNavigationStopped(Status eventStatus, final NavigationStatus status) {}

    /**
     * show the junction view info if available
     * @param status - the current navigation status
     */
    private void showJunctionViewInfo(final NavigationStatus status) {
        if ((status.getNavigationStateNotification() == NavigationStatus.NOTIFICATION_SHOW_JUNCTION_VIEW) && status.getJunctionViewInfo() != null && (status.getJunctionViewInfo()
                .getType() == JunctionViewInfo.TYPE_JUNCTION_VIEW) && status.getJunctionViewInfo().getJunctionViewData() != null) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //update junction view
                    byte[] pngData = status.getJunctionViewInfo().getJunctionViewData().getPngData();
                    if (pngData != null && mainActivity != null && isAdded()) {
                        BitmapDrawable bitmap = new BitmapDrawable(mainActivity.getResources(), BitmapFactory.decodeByteArray(pngData, 0, pngData.length));
                        junctionViewImage.setVisibility(View.VISIBLE);
                        junctionViewImage.setBackground(bitmap);
                    }
                }
            });
        } else if ((status.getNavigationStateNotification() == NavigationStatus.NOTIFICATION_SHOW_JUNCTION_VIEW) && status.getJunctionViewInfo() != null && (status
                .getJunctionViewInfo().getType() == JunctionViewInfo.TYPE_INTERSECTION_VIEW)) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    junctionViewImage.setBackground(null);
                    junctionViewImage.setVisibility(View.GONE);
                }
            });
        } else if (status.getNavigationStateNotification() == NavigationStatus.NOTIFICATION_HIDE_JUNCTION_VIEW) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    junctionViewImage.setBackground(null);
                    junctionViewImage.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onVoiceGuidance(VoiceGuidance voiceGuidance) {
        StringBuilder guidanceTextStringBuilder = new StringBuilder();
        for (String guidanceText : voiceGuidance.getGuidanceTexts()) {
            guidanceTextStringBuilder.append(guidanceText).append(" ");
        }
        textToSpeechManager.playAdvice(guidanceTextStringBuilder.toString());
    }

    /**
     * calculates a route from the current position to the given point
     * @param point current point
     */
    public void calculateRoute(Point point) {
        RouteSettings routeSettings = new RouteSettings();
        WayPoint destination = new WayPoint();
        destination.setCoordinates(point);
        routeSettings.setDestination(destination);
        routeSettings.setRouteOutputOption(RouteSettings.ROUTE_OUTPUT_FULL);
        routeSettings.setMaxRouteNumber(3);
        routeSettings.setOptimizeWayPoints(true);

        routeOption.setAvoidHighway(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HIGHWAYS));
        routeOption.setAvoidTollRoad(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TOLL_ROADS));
        routeOption.setAvoidTunnel(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TUNNELS));
        routeOption.setAvoidUnpavedRoad(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_UNPAVED_ROAD));
        routeOption.setAvoidCountryBorders(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_COUNTRY_BORDERS));
        routeOption.setAvoidFerry(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_FERRIES));
        routeOption.setAvoidHOVLane(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_HOV_LANES));
        routeOption.setAvoidCarTrain(appPrefs.getBooleanPreference(PreferenceTypes.K_AVOID_TRAIN_TRANSPORT));

        routeSettings.setRouteOption(routeOption);
        mapSettings.setZoomLevel(13.0f, 0.5f);
        RouteManager.getInstance().calculateRoute(routeSettings);
    }

    /**
     * @return returns the displayed routes on the map
     */
    public List<RouteInfo> getDisplayedRoutes() {
        return routes;
    }

    /**
     * sets all the map gesture(e.g: zooming, panning, rotating, tilting)
     */
    public void setMapGesture() {
        mapSettings.setRotatingEnabled(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ROTATING));
        mapSettings.setPanningEnabled(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_PANNING));
        mapSettings.setZoomingEnabled(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_ZOOMING));
        mapSettings.setTiltingEnabled(appPrefs.getBooleanPreference(PreferenceTypes.K_MAP_TILTING));
    }

    @Override
    public void onNewLocationReceived(Location location) {
        lastKnownLocation = LocationManager.getInstance().getLastLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * @param turnType current turn type object
     * @return true, if current turn type indicates a way-point or a destination
     */
    private boolean isWayPointOrDestinationTurnType(TurnType turnType) {
        return turnType == TurnType.LocationAhead || turnType == TurnType.LocationLeft || turnType == TurnType.LocationRight;
    }

    /**
     * shows turn arrow on map
     */
    private void showTurnArrowOnMap() {
        NavigationManager navigationManager = NavigationManager.getInstance();
        if (navigationManager.getNavigationData().getDisplayRouteInfo() != null && navigationManager.getNavigationData().getDisplayRouteInfo().getSegments() != null &&
                (navigationManager.getNavigationData().getDisplayRouteInfo().getSegments().size() > (navigationManager.getNavigationData().getCurrentSegment() + 1))) {
            Segment nextSegment = navigationManager.getNavigationData().getDisplayRouteInfo().getSegments().get(navigationManager.getNavigationData().getCurrentSegment() + 1);
            if (!isWayPointOrDestinationTurnType(nextSegment.getTurnType())) {
                mapView.showTurnArrow(NavigationManager.getInstance().getNavigationData().getCurrentSegment() + 1);
            }
        }
    }

    //This will display the turn icons
    private void setImageResource(NavigationData navigationData, HeaderFragment headerFragment)
    {
        ManeuverInfo maneuverInfo = navigationData.getCurrentManeuverInfo();
        if(maneuverInfo != null)
        {
            TurnType turnType = maneuverInfo.getTurnType();
            headerFragment.setTurnIcon(turnType);
        }
    }

    public double convertMetersToMiles(double meters)
    {
        double miles = meters*0.000621371;
        return Double.valueOf(Math.round((float)miles*10.0)/10.0);
    }


    //Send data to Control application
//    class SendData extends TimerTask
//    {
//
//        @Override
//        public void run()
//        {
//            AutopilotData autopilotBrain = new AutopilotData();
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("distance_remaining", String.valueOf(autopilotBrain.getDistance()));
//                jsonObject.put("speed_limit", String.valueOf(autopilotBrain.getSpeedLimit()));
//                jsonObject.put("is_highway_next", String.valueOf(autopilotBrain.isHighwayNext));
//                jsonObject.put("are_we_on_highway", String.valueOf(autopilotBrain.areWeOnHighway));
//                jsonObject.put("isNavigationStarted", String.valueOf(((MapActivity)getActivity()).isNavigationStarted));
//
//                ((MapActivity)getActivity()).sendControl(jsonObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    void pointToMidRoute()
    {
        List<Segment> segmentList = navigationData.getSegments();
        Segment segment = segmentList.get(segmentList.size()/2);
        List<Edge> edgeList = segment.getEdges();
        Edge edge = edgeList.get(edgeList.size()/2);
        List<LatLon> shapePointsList = edge.getShapePoints();
        LatLon latLon = shapePointsList.get(shapePointsList.size()/2);
        mapView.moveTo(latLon);
    }
}


