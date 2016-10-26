package com.telenav.sdk_sample.text.to.speech;

import java.util.Locale;
import android.speech.tts.TextToSpeech;
import com.telenav.sdk_sample.application.SdkSampleApplication;

/**
 * class that is responsible with the text to speech engine for playing advices
 */
public class TextToSpeechManager {

    /**
     * the text to speech engine
     */
    private TextToSpeech textToSpeech;

    /**
     * initialises the text to speech engine
     */
    public void initialiseTextToSpeech() {
        textToSpeech = new TextToSpeech(SdkSampleApplication.getInstance().getApplicationContext(), new TextToSpeech.OnInitListener() {
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