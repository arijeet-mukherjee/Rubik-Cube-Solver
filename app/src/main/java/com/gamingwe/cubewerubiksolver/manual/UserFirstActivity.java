package com.gamingwe.cubewerubiksolver.manual;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gamingwe.cubewerubiksolver.R;
import com.gamingwe.cubewerubiksolver.manual.utils.SlideAdapter;

public class UserFirstActivity extends AppCompatActivity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SlideAdapter slideAdapter;
    private int mCurrpage;
    private Button mstart;
    private TextView[] mdots;

    SharedPreferences sp;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userfirstscreen);
        mSlideViewPager=(ViewPager)findViewById(R.id.viewPager);
        mDotLayout=(LinearLayout)findViewById(R.id.dotsLayout);
        mSlideViewPager.setAdapter(slideAdapter);
        mstart=(Button)findViewById(R.id.start);
        slideAdapter=new SlideAdapter(this);
        mSlideViewPager.setAdapter(slideAdapter);
        adddotIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewPager);
        sp=getSharedPreferences("show",MODE_PRIVATE);

        if(sp.getBoolean("shown",false))
        {
            goToCubeActivity();
            finish();
        }

        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCubeActivity();
                sp.edit().putBoolean("shown",true).apply();
                finish();
            }
        });
    }

    ViewPager.OnPageChangeListener viewPager=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            adddotIndicator(position);
            mCurrpage=position;
            if(mCurrpage==0)
            {
                mstart.setEnabled(false);
                mstart.setVisibility(View.INVISIBLE);
            }
            if(mCurrpage==1)
            {
                mstart.setEnabled(true);
                mstart.setVisibility(View.VISIBLE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void adddotIndicator(int position)
    {
        mdots=new TextView[3];
        mDotLayout.removeAllViews();
        int i;
        for(i=0;i<mdots.length-1;i++)
        {
            mdots[i]=new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.cubeRed));

            mDotLayout.addView(mdots[i]);

        }
        if(mdots.length>0)
        {
            mdots[position].setTextColor(getResources().getColor(R.color.cubeBlue));
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void goToCubeActivity(){
        Intent i = new Intent(this,CubeActivity.class);
        startActivity(i);
    }
}
