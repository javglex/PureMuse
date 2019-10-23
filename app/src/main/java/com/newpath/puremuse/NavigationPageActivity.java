package com.newpath.puremuse;

import android.animation.ArgbEvaluator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.util.MutableFloat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.newpath.puremuse.adapters.SectionsPagerAdapter;
import com.newpath.puremuse.database.AppDatabase;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.services.MusicPlayService;
import com.newpath.puremuse.ui.main.CreatePlaylistFragment;
import com.newpath.puremuse.ui.main.LargePlayerFragment;
import com.newpath.puremuse.ui.main.SongViewModel;
import com.newpath.puremuse.helpers.StoragePermissionHelper;

public class NavigationPageActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = "NavigationPageActivity";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageButton mImgBtnLeft,mImgBtnCenter,mImgBtnRight;
    private ProgressBar mProgressMusic;
    private ConstraintLayout mLayoutMiniPlayer;
    ImageButton mBtnMediaAction;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Toolbar mToolbar;
    MenuItem mItemSearch;
    MenuItem mItemSettings;
    MenuItem mItemProfile;
    private SongViewModel viewModel;
    private MediaPlayerHelper mMediaHelper;
    private MusicPlayService.TimeElapsed mTimeElapsedObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_page);
        viewModel = ViewModelProviders.of(this).get(SongViewModel.class);

        Log.d(TAG,"onCreate");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1); //begin in middle
        mViewPager.addOnPageChangeListener(new CustomOnPageChangeListener());
        mImgBtnLeft = findViewById(R.id.btn_album);
        mImgBtnCenter = findViewById(R.id.btn_explore);
        mImgBtnRight= findViewById(R.id.btn_playlist);
        mImgBtnLeft.setOnClickListener(this);
        mImgBtnCenter.setOnClickListener(this);
        mImgBtnRight.setOnClickListener(this);

        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(this);

        initToolbar();
        initViews();
        setUpColors();
        scanFiles();

    }


    public void initViews(){
        //retrieve button from small media player
        mBtnMediaAction = findViewById(R.id.btn_media_action);
        mLayoutMiniPlayer = findViewById(R.id.ll_mini_player);
        mLayoutMiniPlayer.setOnClickListener(this);
        mBtnMediaAction.setOnClickListener(this);
        mProgressMusic = findViewById(R.id.progress_music);

        mLayoutMiniPlayer.setVisibility(View.GONE);

        mMediaHelper.registerPlayerState(new MediaPlayerHelper.MusicPlayerStateChange() {
            @Override
            public void onPlaying() {
                mLayoutMiniPlayer.setVisibility(View.VISIBLE);
                mBtnMediaAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
                initProgressBar();
            }

            @Override
            public void onPaused() {
                mBtnMediaAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24dp));
            }

            @Override
            public void onStopped() {
                mLayoutMiniPlayer.setVisibility(View.GONE);
            }

            @Override
            public void onSkipped() {
            }
        });
    }

    private void initProgressBar(){

        try{    //incase our current song playing does not contain the property "getDuration".
            final AudioFileModel currentSongPlaying = mMediaHelper.getPlayedSong();
            if (currentSongPlaying==null)
                return;

            mTimeElapsedObs = new MusicPlayService.TimeElapsed() {
                @Override
                public void onTimerFired(float timeElapsed) {
                    float percentage = timeElapsed/Integer.parseInt(currentSongPlaying.getDuration());
                    percentage*=1000;
                    setProgressBarValue((int)percentage);

                }
            };
        }catch (Exception e){
            Log.e(TAG,e.getLocalizedMessage());
        }

        MusicPlayService.registerTimeElapsed(mTimeElapsedObs);

    }


    private void setProgressBarValue(int progress){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mProgressMusic.setProgress(progress,false);   //multiplied by 10 because max is out of 1000 for smoothness
        } else mProgressMusic.setProgress(progress);
    }

    private void initToolbar(){
        mToolbar=(Toolbar) findViewById(R.id.toolbar_nav);
        mToolbar.setTitle(mSectionsPagerAdapter.getPageTitle(1));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
    }

    private void scanFiles(){
        if (StoragePermissionHelper.handlePermissions(this))
            viewModel.startScan(this);

    }

    private void addLargePlayerFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LargePlayerFragment fragment = LargePlayerFragment.newInstance(0,0);
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.addToBackStack("LargePlayerFragment");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        mItemSearch= mToolbar.getMenu().findItem(R.id.action_search);
        mItemSettings= mToolbar.getMenu().findItem(R.id.action_settings);
        mItemProfile= mToolbar.getMenu().findItem(R.id.action_user);

        //only show one item per page
        mItemSearch.setVisible(false);
        mItemSettings.setVisible(false);
        mItemProfile.setVisible(true);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        initProgressBar();

    }

    @Override
    public void onPause(){
        super.onPause();
        MusicPlayService.unregisterTimeElapsed();
    }

    @Override
    public void onStop(){
        super.onStop();
        //mMediaHelper.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMediaHelper.onDestroy();
        AppDatabase.destroyInstance();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_media_action:
                mMediaHelper.togglePlay();
                break;
            case R.id.btn_album:
                mViewPager.setCurrentItem(0,true);
                break;
            case R.id.btn_explore:
                mViewPager.setCurrentItem(1,true);
                break;
            case R.id.btn_playlist:
                mViewPager.setCurrentItem(2,true);
                break;
            case R.id.ll_mini_player:
                addLargePlayerFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStackImmediate();
        else super.onBackPressed();
        //exitDialog();
    }

    public void clearBackStack(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

       for (int i = 0; i < backStackCount; i++)
            fragmentManager.popBackStack();
    }

    /**
    *  background transformation by http://kubaspatny.github.io/2014/09/18/viewpager-background-transition/
    */
    private void setUpColors(){

        Integer color1 = ContextCompat.getColor(getApplicationContext(),R.color.colorSecondary);
        Integer color2 = ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary);
        Integer color3 = ContextCompat.getColor(getApplicationContext(),R.color.colorThird);

        Integer[] colors_temp = {color1, color2, color3};
        colors = colors_temp;

    }

    /**
     *  background transformation by http://kubaspatny.github.io/2014/09/18/viewpager-background-transition/
     */
    private class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//
//            TypedArray bgs = getResources().obtainTypedArray(R.array.bg_gradients);
//
//            // get resource ID by index
//            int b = bgs.getResourceId(position, -1);
//
//            // recycle the array
//            bgs.recycle();
//
//            if(position < (mSectionsPagerAdapter.getCount() -1) && position < (colors.length - 1)) {
//
//                int color=(Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]);
//            } else {
//                // the last page color
//                int lastPageColor=colors[colors.length - 1];
//                mViewPager.setBackgroundColor(lastPageColor);
//                //mToolbar.setBackgroundColor(lastPageColor);
//            }


        }

        @Override
        public void onPageSelected(int position) {
            //change menu title
            mToolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));
            clearBackStack();
            //handle which menu item shows on toolbar
            switch(position){
                case 0:
                    mItemSearch.setVisible(true);
                    mItemSettings.setVisible(false);
                    mItemProfile.setVisible(false);
                    break;
                case 1:
                    mItemSearch.setVisible(false);
                    mItemSettings.setVisible(false);
                    mItemProfile.setVisible(true);
                    break;
                case 2:
                    mItemSearch.setVisible(false);
                    mItemSettings.setVisible(true);
                    mItemProfile.setVisible(false);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG,"onrequestpermission result");
        StoragePermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, new StoragePermissionHelper.PermissionCallback(){
            @Override
            public void onGranted() {

                viewModel.startScan(NavigationPageActivity.this);
            }
            @Override
            public void onDenied(String err) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        });
    }




}
