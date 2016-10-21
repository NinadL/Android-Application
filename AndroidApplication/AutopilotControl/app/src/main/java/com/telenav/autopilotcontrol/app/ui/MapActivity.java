package com.telenav.autopilotcontrol.app.ui;

import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telenav.autopilotcontrol.R;
import com.telenav.autopilotcontrol.app.car_data.Lane;
import com.telenav.autopilotcontrol.app.car_data.SocketClient;
import com.telenav.autopilotcontrol.app.car_data.SocketServer;
import com.telenav.autopilotcontrol.app.joglrender.OBJParser;
import com.telenav.autopilotcontrol.app.joglrender.openGLRenderer;
import com.telenav.autopilotcontrol.app.speech.SpeechStrings;
import com.telenav.autopilotcontrol.app.speech.TextToSpeechManager;

import java.util.Locale;

/**
 * Created by ishwarya on 8/17/16.
 */
public class MapActivity extends AppCompatActivity  {

    public static final short startScreen = 1;
    public static final short controlScreen = 2;
    private SocketClient c0=null, c1=null, c2=null, c3=null;
    private SocketServer ss = null;
    public static final String ANIMATE_FRAGMENT = "ANIMATE_FRAGMENT";
    private AnimateFragment animateFragment;
    public static boolean areWeOnStartScreen = true;
    private volatile boolean onPostResumeCalled;
    boolean click = true;
    public static TextView PerceptionNotWorking;
    public static TextView otherNodesNotWorking;
    boolean autoSwapIsClicked = false;


    private TextToSpeechManager textToSpeechManager;
    //Buttons for swiping the screen
    GLSurfaceView gl;


    TextToSpeech tts;
    public static SpeechStrings speechStrings;

    public static short screenView = startScreen;
    public static boolean autoswap = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ss = new SocketServer();
        c0 = new SocketClient("localhost",45001,0);
        c1 = new SocketClient("localhost",45011,1);
        c2 = new SocketClient("localhost",45021,2);
        c3 = new SocketClient("localhost",45031,3);
        setContentView(R.layout.activity_main);

        textToSpeechManager = new TextToSpeechManager(this);
        textToSpeechManager.initialiseTextToSpeech();

        onPostResumeCalled = false;
        Lane p = new Lane();
        p.init();


            initScreen();
            initJoglScreen();


    }

    private void initJoglScreen(){

        new processObjFile().execute();
        gl = (GLSurfaceView) findViewById(R.id.jogl_render);
        gl.setEGLContextClientVersion(2);

        gl.setRenderer( new openGLRenderer(this,(RelativeLayout) findViewById(R.id.left_pane)));

    }

    private void initScreen()
    {
        RelativeLayout leftLayout = (RelativeLayout) findViewById(R.id.left_pane);
        RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        leftLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        attachFragmentToActivity();

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                tts.setLanguage(Locale.US);
            }
        }
        );


        speechStrings = new SpeechStrings(getApplicationContext());
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                tts.setLanguage(Locale.US);
            }
        }
        );
    }

    public void sendControl(int value)
    {
        //TODO - MODIFY THIS
        c0.sendMessage(String.valueOf(value));
        //cService.sendMessage(String.valueOf(value)) ;
    }
    private void attachFragmentToActivity() {

        animateFragment = new AnimateFragment();
        getFragmentManager().beginTransaction().replace(R.id.left_pane, animateFragment, ANIMATE_FRAGMENT).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void speak(String text)
    {
        if (tts != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, "utterance_id");
            } else {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }


       @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ss != null) {
            ss.closeAll();
            ss = null;
        }

        if (c0 != null) {
            c0.closeConnection();
            c0 = null;
        }
        if (c1 != null) {
            c1.closeConnection();
            c1 = null;
        }
        if (c2 != null) {
            c2.closeConnection();
            c2 = null;
        }
        if (c3 != null) {
            c3.closeConnection();
            c3 = null;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    void sendSomething(){
        cService.sendMessage("something\n");
    }*/



    public void speakOverlap(String text) {
        if (tts != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_id");
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
       // gl.onStop();
    }



    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        gl.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        gl.onPause();
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

}
