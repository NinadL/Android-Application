package com.telenav.sdk_sample.application;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * defines application class
 */
public class SdkSampleApplication extends Application {

    /**
     * sample application instance
     */
    private static SdkSampleApplication instance;

    /**
     * initilizeManager init-ed successfully or not;
     * used for knowing if onInitialised method was called
     */
    private boolean sInitOk = false;

    /**
     * application preferences
     */
    private ApplicationPreferences applicationPreferences;

    /**
     * @return a single instance of this class
     */
    public static SdkSampleApplication getInstance() {
        if (instance == null) {
            synchronized (SdkSampleApplication.class) {
                if (instance == null) {
                    instance = new SdkSampleApplication();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initApplication();
    }

    /**
     * Initializes some of the objects needed within the application
     */
    private void initApplication() {
        instance = this;
        if (applicationPreferences == null) {
            applicationPreferences = new ApplicationPreferences(this);
        } else {
            applicationPreferences.setContext(this);
        }
    }

    /**
     * @return instance to the {@link ApplicationPreferences} object
     */
    public ApplicationPreferences getApplicationPreferences() {
        return applicationPreferences;
    }

    /**
     *
     */
    public void setInitOk(boolean flag) {
        sInitOk = flag;
    }

    public boolean isInitOk() {
        return sInitOk;
    }
}