package com.telenav.autopilotcontrol.app.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

//import com.telenav.sdk_sample.application.SdkSampleApplication;

import java.util.Locale;

/**
 * class that is responsible with the text to speech engine for playing advices
 */
public class TextToSpeechManager {

    /**
     * the text to speech engine
     */
    private TextToSpeech textToSpeech;
    private Context context;

    public TextToSpeechManager(Context context){
        this.context = context;
    }

    /**
     * initialises the text to speech engine
     */
    public void initialiseTextToSpeech() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("en");
                    int languageAvailableCode = textToSpeech.isLanguageAvailable(locale);
                    if (languageAvailableCode == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeech.setLanguage(locale);
                    }
                }
            }
        });
    }

    /**
     * plays an advice
     * @param advice the advice that will be played
     */
    public void playAdvice(String advice) {
        if (textToSpeech != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(advice, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                textToSpeech.speak(advice, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    /**
     * stops the text to speech playing advices
     */
    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    /**
     * release the tts resources
     */
    public void release() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}