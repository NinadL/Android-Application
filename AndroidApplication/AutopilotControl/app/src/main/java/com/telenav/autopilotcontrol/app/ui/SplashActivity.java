
package com.telenav.autopilotcontrol.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.telenav.autopilotcontrol.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int progress = 0;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressBar.setScaleY(3f);

        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++)
                {
                    progress = progress + 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                          progressBar.setProgress(progress);

                            if(progress == progressBar.getMax())
                            {
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    try
                    {
                        Thread.sleep(25);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//
    }
}
