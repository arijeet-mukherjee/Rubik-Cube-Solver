package com.gamingwe.cubewerubiksolver.manual;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.VideoView;

import com.gamingwe.cubewerubiksolver.R;

public class SplashActivity extends Activity {

    private VideoView imgAnim;
    private TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        imgAnim=(VideoView)findViewById(R.id.animimage);
        myText=(TextView) findViewById(R.id.appname);
        Animation hmyanim = AnimationUtils.loadAnimation(this, R.anim.fade);
        hmyanim.setDuration(800);


        try {
            //VideoView videoHolder = new VideoView(this);


            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
            imgAnim.setVideoURI(video);
            myText.startAnimation(hmyanim);
            imgAnim.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }
            });
            imgAnim.start();
        } catch (Exception ex) {
            jump();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        jump();
        return true;
    }

    private void jump() {
        if (isFinishing())
            return;
        startActivity(new Intent(this, UserFirstActivity.class));
        finish();
    }
}
