package com.telenav.autopilotcontrol.app.speech;

import android.app.Application;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ninad on 8/9/16.
 */
public class SpeechStrings extends Application{

    public TextToSpeech textToSpeechInEnglish;
    public TextToSpeech textToSpeechInSpanish;
    public TextToSpeech textToSpeechInChinese;
    public TextToSpeech textToSpeechInHindi;

    public static HashMap<String, Boolean> messagePlayed = new HashMap<String, Boolean>();
    //Node connections
    public static final String PERCEPTION_NOT_WORKING_NON_EMERGENCY = "Sensor data is not received";
    public static final String CONTROL_NOT_WORKING_NON_EMERGENCY = "Car data is not received";
    public static final String MAPS_NOT_WORKING_NON_EMERGENCY = "Map data is not received";
    public static final String MOTION_PLANNING_NOT_WORKING_NON_EMERGENCY = "Lane data is not received";
    public static final String NOT_IN_CENTER_LANE_NON_EMERGENCY = "You are not in the center lane";
    public static final String NOT_ON_HIGHWAY_NON_EMERGENCY = "You are not on highway";
    public static final String AUTOPILOT_ENDING_NON_EMERGENCY = "Autopilot ending soon";
    public static final String NO_NETWORK_NON_EMERGENCY = "No network detected";

    public static final String PERCEPTION_NOT_WORKING_EMERGENCY = "Sensor data is not received. Please take back control immediately";
    public static final String CONTROL_NOT_WORKING_EMERGENCY = "Car data is not received. Please take back control immediately";
    public static final String MAPS_NOT_WORKING_EMERGENCY = "Map data is not received. Please take back control immediately";
    public static final String MOTION_PLANNING_NOT_WORKING_EMERGENCY = "Lane data is not received. Please take back control immediately";
    public static final String NOT_IN_CENTER_LANE_EMERGENCY = "You are not in the center lane. Please take back control immediately";
    public static final String NOT_ON_HIGHWAY_EMERGENCY = "You are not on highway. Please take back control immediately";
    public static final String AUTOPILOT_ENDING_EMERGENCY = "Autopilot ending soon. Please take back control immediately";
    public static final String NO_NETWORK_EMERGENCY = "No network detected. Please take back control immediately";

    public static final String NON_EMERGENCY = "Please take back control";
    public static final String EMERGENCY_IN_ENGLISH = "Please take back control immediately";
    public static final String EMERGENCY_IN_CHINESE = "请立即收回控制权";
    public static final String EMERGENCY_IN_SPANISH = "Por favor, tome de nuevo el control de inmediato";
    public static final String EMERGENCY_IN_HINDI = "कृपया तुरंत नियंत्रण वापस ले";

    public static final String ENABLE_AUTOPILOT = "Autopilot ready to be engaged";

    public SpeechStrings(Context context)
    {

        initialiseTextToSpeech(context);

        messagePlayed.put(PERCEPTION_NOT_WORKING_NON_EMERGENCY, false);
        messagePlayed.put(CONTROL_NOT_WORKING_NON_EMERGENCY, false);
        messagePlayed.put(MAPS_NOT_WORKING_NON_EMERGENCY, false);
        messagePlayed.put(MOTION_PLANNING_NOT_WORKING_NON_EMERGENCY, false);
        messagePlayed.put(NOT_IN_CENTER_LANE_NON_EMERGENCY, false);
        messagePlayed.put(NOT_ON_HIGHWAY_NON_EMERGENCY, false);
        messagePlayed.put(AUTOPILOT_ENDING_NON_EMERGENCY, false);
        messagePlayed.put(NO_NETWORK_NON_EMERGENCY, false);
        messagePlayed.put(NON_EMERGENCY, false);
        messagePlayed.put(EMERGENCY_IN_ENGLISH, false);
        messagePlayed.put(EMERGENCY_IN_CHINESE, false);
        messagePlayed.put(EMERGENCY_IN_SPANISH, false);
        messagePlayed.put(EMERGENCY_IN_HINDI, false);
        messagePlayed.put(ENABLE_AUTOPILOT, false);
        messagePlayed.put(PERCEPTION_NOT_WORKING_EMERGENCY, false);
        messagePlayed.put(CONTROL_NOT_WORKING_EMERGENCY, false);
        messagePlayed.put(MAPS_NOT_WORKING_EMERGENCY, false);
        messagePlayed.put(MOTION_PLANNING_NOT_WORKING_EMERGENCY, false);
        messagePlayed.put(NOT_IN_CENTER_LANE_EMERGENCY, false);
        messagePlayed.put(NOT_ON_HIGHWAY_EMERGENCY, false);
        messagePlayed.put(AUTOPILOT_ENDING_EMERGENCY, false);
        messagePlayed.put(NO_NETWORK_EMERGENCY, false);
    }

    public void initialiseTextToSpeech(Context context) {

        textToSpeechInEnglish = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("en");
                    int languageAvailableCode = textToSpeechInEnglish.isLanguageAvailable(locale);
                    if (languageAvailableCode == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeechInEnglish.setLanguage(locale);
                    }
                }
            }
        });

        textToSpeechInChinese = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("zh");
                    int languageAvailableCode = textToSpeechInChinese.isLanguageAvailable(locale);
                    if (languageAvailableCode == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeechInChinese.setLanguage(locale);
                    }
                }
            }
        });

        textToSpeechInSpanish = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("es");
                    int languageAvailableCode = textToSpeechInSpanish.isLanguageAvailable(locale);
                    if (languageAvailableCode == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeechInSpanish.setLanguage(locale);
                    }
                }
            }
        });

        textToSpeechInHindi = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("hi");
                    int languageAvailableCode = textToSpeechInHindi.isLanguageAvailable(locale);
                    if (languageAvailableCode == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeechInHindi.setLanguage(locale);
                    }
                }
            }
        });
    }
    /**
     * plays an advice
     * @param advice the advice that will be played
     */
    public void playAdviceInEnglish(String advice) throws InterruptedException {
        if (textToSpeechInEnglish != null && !messagePlayed.get(advice)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechInEnglish.speak(advice, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                textToSpeechInEnglish.speak(advice, TextToSpeech.QUEUE_ADD, null);
            }

            for (Map.Entry<String, Boolean> entry : messagePlayed.entrySet()) {
                if (entry.getKey().equals(advice))
                {
                    final Map.Entry<String, Boolean> newEntry = entry;

                    entry.setValue(true);
                    //Thread.currentThread().sleep(10000);
                }
            }
            if(advice.equals(ENABLE_AUTOPILOT)) {
                for (Map.Entry<String, Boolean> entry : messagePlayed.entrySet()) {
                    if (!entry.getKey().equals(advice)) {
                        entry.setValue(false);
                    }
                }
            }
        }
    }

    public void playAdviceInChinese(String advice) {
        if (textToSpeechInChinese != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechInChinese.speak(advice, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                textToSpeechInChinese.speak(advice, TextToSpeech.QUEUE_ADD, null);
            }
//            for (Map.Entry<String, Boolean> entry : messagePlayed.entrySet()) {
//                if(entry.getKey().equals(advice)) {
//                    entry.setValue(true);
//                }
//                else
//                {
//                    entry.setValue(false);
//                }
//            }
        }
    }

    public void playAdviceInSpanish(String advice) {
        if (textToSpeechInSpanish != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechInSpanish.speak(advice, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                textToSpeechInSpanish.speak(advice, TextToSpeech.QUEUE_ADD, null);
            }
//            for (Map.Entry<String, Boolean> entry : messagePlayed.entrySet()) {
//                if(entry.getKey().equals(advice)) {
//                    entry.setValue(true);
//                }
//                else
//                {
//                    entry.setValue(false);
//                }
//            }
        }
    }

    public void playAdviceInHindi(String advice) {
        if (textToSpeechInHindi != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechInHindi.speak(advice, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                textToSpeechInHindi.speak(advice, TextToSpeech.QUEUE_ADD, null);
            }
//            for (Map.Entry<String, Boolean> entry : messagePlayed.entrySet()) {
//                if(entry.getKey().equals(advice)) {
//                    entry.setValue(true);
//                }
//                else
//                {
//                    entry.setValue(false);
//                }
//            }
        }
    }
    /**
     * stops the text to speech playing advices
     */
    public void stop() {
        if (textToSpeechInEnglish != null) {
            textToSpeechInEnglish.stop();
        }
        if (textToSpeechInChinese != null) {
            textToSpeechInChinese.stop();
        }
        if (textToSpeechInSpanish != null) {
            textToSpeechInSpanish.stop();
        }
        if (textToSpeechInHindi != null) {
            textToSpeechInHindi.stop();
        }
    }

    /**
     * release the tts resources
     */
    public void release() {
        if (textToSpeechInEnglish != null) {
            textToSpeechInEnglish.shutdown();
        }
        if (textToSpeechInChinese != null) {
            textToSpeechInChinese.shutdown();
        }
        if (textToSpeechInSpanish != null) {
            textToSpeechInSpanish.shutdown();
        }
        if (textToSpeechInHindi != null) {
            textToSpeechInHindi.shutdown();
        }
    }
}