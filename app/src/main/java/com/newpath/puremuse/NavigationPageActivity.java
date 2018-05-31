package com.newpath.puremuse;

import android.animation.ArgbEvaluator;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.ui.main.AlbumsFragment;
import com.newpath.puremuse.ui.main.MainFragment;
import com.newpath.puremuse.ui.main.SongViewModel;
import com.newpath.puremuse.helpers.StoragePermissionHelper;

public class NavigationPageActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = "NavigationPageActivity";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageButton mImgBtnLeft,mImgBtnCenter,mImgBtnRight;
    ImageButton btnMediaAction;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Toolbar mToolbar;
    MenuItem mItemSearch;
    MenuItem mItemSettings;
    MenuItem mItemProfile;
    private SongViewModel viewModel;
    private MediaPlayerHelper mMediaHelper;


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


        initToolbar();
        initViews();
        /*
            background transformation by http://kubaspatny.github.io/2014/09/18/viewpager-background-transition/
         */
        setUpColors();
        scanFiles();

        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(this);
       //mediaHelper.setSong();
    }

    public void initViews(){
        //retrieve button from small media player
        btnMediaAction = findViewById(R.id.btn_media_action);
        btnMediaAction.setOnClickListener(this);

    }

    private void initToolbar(){
        mToolbar=(Toolbar) findViewById(R.id.toolbar_nav);
        mToolbar.setTitle(mSectionsPagerAdapter.getPageTitle(1));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(mToolbar);
    }

    private void scanFiles(){
        if (StoragePermissionHelper.handlePermissions(this))
            viewModel.startScan(this);

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
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
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

    private void setUpColors(){

        Integer color1 = ContextCompat.getColor(getApplicationContext(),R.color.colorSecondary);
        Integer color2 = ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary);
        Integer color3 = ContextCompat.getColor(getApplicationContext(),R.color.colorThird);

        Integer[] colors_temp = {color1, color2, color3};
        colors = colors_temp;

    }


    private class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(position < (mSectionsPagerAdapter.getCount() -1) && position < (colors.length - 1)) {

                int color=(Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]);
                mViewPager.setBackgroundColor(color);
                mToolbar.setBackgroundColor(color);
            } else {
                // the last page color
                int lastPageColor=colors[colors.length - 1];
                mViewPager.setBackgroundColor(lastPageColor);
                mToolbar.setBackgroundColor(lastPageColor);
            }
        }

        @Override
        public void onPageSelected(int position) {
            //change menu title
            mToolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));

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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return AlbumsFragment.newInstance();
                case 1:
                    return MainFragment.Companion.newInstance(-1);
                case 2:
                    return AlbumsFragment.newInstance();
                default:
                    return MainFragment.Companion.newInstance(-1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Albums";
                case 1:
                    return "PureMuse";
                case 2:
                    return "Playlists";
            }
            return null;
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
