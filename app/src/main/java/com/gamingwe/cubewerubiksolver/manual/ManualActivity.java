package com.gamingwe.cubewerubiksolver.manual;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.gamingwe.cubewerubiksolver.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;

//import android.widget.Toolbar;

public class ManualActivity extends AppCompatActivity implements EditScrambleDialog.EditScrambleDialogListener {

    public static final String INITIAL_INPUT_TYPE = "initial input type";
    public static final String MANUAL_COLOR_INPUT = "manual color input";
    public static final String CAMERA_INPUT = "camera input";
    public static final String ALL_COLORS_INPUTTED = "all colors inputted";
    public static final String COLORS_INPUTTED_LEFT = "colors inputted left";
    public static final String COLORS_INPUTTED_UP = "colors inputted up";
    public static final String COLORS_INPUTTED_FRONT = "colors inputted front";
    public static final String COLORS_INPUTTED_BACK = "colors inputted back";
    public static final String COLORS_INPUTTED_RIGHT = "colors inputted right";
    public static final String COLORS_INPUTTED_DOWN = "colors inputted down";
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    private final String TEXT_SCRAMBLE = "text scramble";
    private final String COLOR_INPUT = "color input";
    private String currentMode = TEXT_SCRAMBLE;
    private TextSolutionFragment solutionFragment;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView drawerList;
    private String[] navDrawerTitles;
    private Drawable drawable;
    //AdConsent adConsent;
    LinearLayout ll_ad;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;






    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mInterstitialAd = new InterstitialAd(ManualActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial__adUnit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        MobileAds.initialize(this, getString(R.string.admob_app_id));

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_menu);
       toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        //toolbar.setOverflowIcon(drawable);
        //toolbar.setNavigationIcon(drawable);

        /**
        final Animation myAnimbounce = AnimationUtils.loadAnimation(ManualActivity.this, R.anim.bounce);
        final Animation myAnimfade = AnimationUtils.loadAnimation(ManualActivity.this, R.anim.fade);
        final Animation myAnimslidedown = AnimationUtils.loadAnimation(ManualActivity.this, R.anim.slide_down_dialog);
        final Animation myAnimslideup = AnimationUtils.loadAnimation(ManualActivity.this, R.anim.slide_up_dialog);

         */


        navDrawerTitles = getResources().getStringArray(R.array.nav_drawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (NavigationView) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerToggle.setDrawerIndicatorEnabled(true);

        drawerToggle.setDrawerSlideAnimationEnabled(true);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerList.setNavigationItemSelectedListener(new DrawerClickListener());
        hideItem();


        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            Bundle args = (extras != null) ? extras.getBundle(ALL_COLORS_INPUTTED) : null;
            String inputType = (args != null) ? args.getString(INITIAL_INPUT_TYPE) : " ";
            ColorInputFragment colorInputFragment = new ColorInputFragment();
            if (inputType != null && inputType.equals(CAMERA_INPUT)) {
                Log.d("Starting Color Input", "true");
               // ColorInputFragment colorInputFragment = new ColorInputFragment();

                if(args != null) {
                    colorInputFragment.setArguments(args);
                } else {
                    Log.d("Colors are null", "TRUE");
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, colorInputFragment, "Color Input Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.color_input);

                /**
                solutionFragment = new TextSolutionFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, solutionFragment, "Text Solution Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.text_scramble);
                */

            } else {
                /**
                solutionFragment = new TextSolutionFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, solutionFragment, "Text Solution Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.text_scramble);
                 */
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, colorInputFragment, "Color Input Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.color_input);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Color Input");

        //added
        getSupportActionBar().setHomeAsUpIndicator(drawable);



        mAdView = (AdView) findViewById(R.id.adView);
       // mAdView.setAdSize(AdSize.BANNER);
       // mAdView.setAdUnitId(getString(R.string.banner_home_footer));

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                //.addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
               // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
               // Toast.makeText(ManualActivity.this,
                 //       "onAdFailedToLoad() with error code: " + errorCode,
                   //     Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String scramble) {
        solutionFragment.onDialogPositiveClick(dialog, scramble);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        //drawerToggle.setHomeAsUpIndicator(drawable);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerClickListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.text_scramble:


                    if (!currentMode.equals(TEXT_SCRAMBLE)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new TextSolutionFragment(), "Text Solution Fragment")
                                .addToBackStack(null)
                                .commit();
                        currentMode = TEXT_SCRAMBLE;
                        drawerLayout.closeDrawers();
                    }
                    break;
                case R.id.color_input:
                    if (!currentMode.equals(COLOR_INPUT)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new ColorInputFragment(), "Color Input Fragment")
                                .addToBackStack(null)
                                .commit();
                        toolbar.setTitle("Color Input");
                        currentMode = COLOR_INPUT;
                        toolbar.setTitle("Color Input");
                        drawerLayout.closeDrawers();
                    }
                    break;
                case R.id.camera_input:
                    if (!currentMode.equals("Camera Input")) {
                        Intent intent = new Intent(getApplicationContext(), CaptureCubeActivity.class);
                        startActivity(intent);
                        currentMode = "Camera Input";
                        drawerList.setCheckedItem(R.id.color_input);
                        drawerLayout.closeDrawers();
                    }
                    break;
                case R.id.home:
                    Intent intent = new Intent(ManualActivity.this, CubeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void hideItem()
    {
        drawerList =  (NavigationView)findViewById(R.id.left_drawer);
        Menu nav_Menu = drawerList.getMenu();
        nav_Menu.findItem(R.id.text_scramble).setVisible(false);
    }
}
