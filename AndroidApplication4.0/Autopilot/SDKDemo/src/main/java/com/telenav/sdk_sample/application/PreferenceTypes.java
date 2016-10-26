package com.telenav.sdk_sample.application;

/**
 * Class for defining keys for {@link ApplicationPreferences} All keys should be
 * defined here, in order to have a centralized way of handling preferences(
 * keys will start with "K_", default values will start with "DEF_")
 */
public class PreferenceTypes {

    /**
     * value defining that a property is on
     */
    public static final boolean ON = true;

    /**
     * value defining that a property is off
     */
    public static final boolean OFF = false;

    public static final String K_PREFS_INITIALIZED = "prefsInitialized";

    // map style
    public static final String K_MAP_STYLE = "mapStyle";

    // map orientation
    public static final String K_MAP_ORIENTATION = "mapOrientation";

    // show buildings in 3D mode
    public static final String K_SHOW_BUILDINGS_IN_3D_MODE = "showBuildingsIn3DMode";

    //rotate heading
    public static final String K_ROTATE_HEADING = "rotateHeading";

    // show map scale
    public static final String K_SHOW_MAP_SCALE_MODE = "showMapScaleMode";

    // show map roads
    public static final String K_SHOW_MAP_ROADS_MODE = "showMapRoadsDMode";

    // show map regions
    public static final String K_SHOW_MAP_REGIONS_MODE = "showMapRegionsMode";

    // show map labels
    public static final String K_SHOW_MAP_LABELS_MODE = "showMapLabelsMode";

    // show map terrain
    public static final String K_SHOW_MAP_TERRAIN_MODE = "showMapTerrainDMode";

    // traffic incidents
    public static final String K_ENABLE_TRAFFIC_INCIDENTS = "enableTrafficIncidents";

    // traffic flow
    public static final String K_ENABLE_TRAFFIC_FLOW = "enableTrafficFlow";

    // follow user position
    public static final String K_FOLLOW_USER_POSITION = "followUserPosition";

    // multi-gestures modes
    public static final String K_MAP_PANNING = "mapPanning";

    public static final String K_MAP_ZOOMING = "mapZooming";

    public static final String K_MAP_TILTING = "mapTilting";

    public static final String K_MAP_ROTATING = "mapRotating";

    //navigation
    public static final String K_NAVIGATION_SIMULATION = "navigationSimulationMode";

    //map data path
    public static final String K_MAP_DATA_PATH = "mapDataPath";

    public static final String DEF_MAP_DATA_PATH = "/storage/sdcard1/EU_20160617/TelenavMapData";

    public static final String DEF_IP_ADDRESS = "192.168.1.20";

    public static final String K_IP_ADDRESS = "ipAddress";

    //map region
    public static final String K_MAP_REGION = "mapRegion";


    //route options
    public static final String K_AVOID_HIGHWAYS = "avoidHighways";

    public static final String K_AVOID_TOLL_ROADS = "avoidTollRoads";

    public static final String K_AVOID_TUNNELS = "avoidTunnels";

    public static final String K_AVOID_UNPAVED_ROAD = "avoidUnpavedRoad";

    public static final String K_AVOID_COUNTRY_BORDERS = "avoidCountryBorders";

    public static final String K_AVOID_FERRIES = "avoidFerries";

    public static final String K_AVOID_HOV_LANES = "avoidHovLanes";

    public static final String K_AVOID_TRAIN_TRANSPORT = "avoidTrainTransport";

    //unit types
    public static final String K_UNIT_TYPE = "unitType";

    //voice guidance types
    public static final String K_VOICE_GUIDANCE_TYPE = "voiceGuidanceType";
}