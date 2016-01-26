package org.progos.tasteofdubaicms.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.progos.tasteofdubaicms.R;


public class SplashActivity extends Activity {
    /**
     * Stopping splash screen starting home activity.
     */
    private static final int STOPSPLASH = 0;
    /**
     * Time duration in millisecond for which your splash screen should visible
     */
    private static final long SPLASHTIME = 1000;

    Context context;

    //********************************************************************************************************************************/

    /**
     * Handler for splash screen
     */
    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case STOPSPLASH:
                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    break;
            }
        }
    };

    //********************************************************************************************************************************/

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_splash);
        context = getApplicationContext();
        gotoNextScreen();
    }

    //********************************************************************************************************************************/

    private void gotoNextScreen() {
        // TODO Auto-generated method stub
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    //********************************************************************************************************************************/

}
