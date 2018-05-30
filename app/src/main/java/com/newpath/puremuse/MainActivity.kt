package com.newpath.puremuse

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.newpath.puremuse.ui.main.MainFragment
import com.newpath.puremuse.ui.main.SongViewModel
import com.newpath.puremuse.helpers.StoragePermissionHelper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import com.newpath.puremuse.services.MusicPlayService
import android.support.v4.media.session.PlaybackStateCompat
import android.os.RemoteException
import android.widget.Button
import com.newpath.puremuse.utils.Constants
import android.content.ComponentName
import android.net.Uri
import android.os.Handler


class MainActivity : AppCompatActivity() {

    var TAG:String = "MainActivity"
    private lateinit var viewModel: SongViewModel
    private lateinit var mMediaControllerCompatCallback: MediaControllerCompat.Callback
    private lateinit var mMediaBrowserCompatConnectionCallback: MediaBrowserCompat.ConnectionCallback
    private var mMediaBrowserCompat: MediaBrowserCompat? = null
    private var mMediaControllerCompat: MediaControllerCompat? = null
    private var mPlayPauseToggleButton: Button? = null
    private var mCurrentState: String = Constants.STATE.STATE_PAUSED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)

//        // use this to start and trigger a service
//        val i = Intent(this, MusicPlayService::class.java)
//        // potentially add data to the intent
//        i.action=START_ACTION
//        startService(i)

        initConnectionCallback()
        initMediaController();
        setupMediaToggleButton()
        Handler().postDelayed({
            MediaControllerCompat.getMediaController(this@MainActivity).transportControls.pause()
        }, 5000)

        Handler().postDelayed({
            MediaControllerCompat.getMediaController(this@MainActivity).transportControls.play()
        }, 6000)

        Handler().postDelayed({
            var pathUri:Uri = Uri.parse("/storage/emulated/0/Music/eMusic/Wisin & Yandel/Mi Vida…My Life/03 Esta Noche Hay Pelea.mp3");
            MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().playFromUri(pathUri, null)
            MediaControllerCompat.getMediaController(this@MainActivity).transportControls.play()

        }, 10000)

    }


    override fun onDestroy() {
        super.onDestroy()
        if( MediaControllerCompat.getMediaController(this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(this).getTransportControls().pause();
        }

        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat!!.disconnect()
        };
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG,"before onrequestpermission result");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG,"onrequestpermission result");
        StoragePermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, object: StoragePermissionHelper.PermissionCallback{
            override fun onGranted() {

               // startScan(this@MainActivity);
            }

            override fun onDenied(err: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    fun initMediaController(){
        mMediaControllerCompatCallback = object : MediaControllerCompat.Callback() {

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                if (state == null) {
                    return
                }

                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = Constants.STATE.STATE_PAUSED
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = Constants.STATE.STATE_PAUSED
                    }
                }
            }
        }
    }

    fun initConnectionCallback(){
        mMediaBrowserCompatConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

            override fun onConnected() {
                super.onConnected()
                try {
                    mMediaControllerCompat = MediaControllerCompat(this@MainActivity, mMediaBrowserCompat!!.getSessionToken())
                    mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                    MediaControllerCompat.setMediaController(this@MainActivity, mMediaControllerCompat)
                    var pathUri:Uri = Uri.parse("/storage/emulated/0/Music/eMusic/Various Artists/Doing It in Lagos_ Boogie, Pop & Disco in 1980's Nigeria/13 Where Is the Answer.mp3");
                    MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().playFromUri(pathUri, null)

                } catch (e: RemoteException) {

                }

            }
        }
    }

    fun setupMediaToggleButton(){
       // mPlayPauseToggleButton = findViewById<Button>(R.id.button)

        mMediaBrowserCompat = MediaBrowserCompat(this@MainActivity, ComponentName(this, MusicPlayService::class.java!!),
                mMediaBrowserCompatConnectionCallback, intent.extras)
        Log.d(TAG, "mMediaBrowserCompat created")

        mMediaBrowserCompat!!.connect()
        Log.d(TAG, "mMediaBrowserCompat connect")

//        mPlayPauseToggleButton!!.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                if (mCurrentState === Constants.STATE.STATE_PAUSED) {
//                    MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().togglePlay()
//                    mCurrentState = Constants.STATE.STATE_PLAYING
//                } else {
//                    if (MediaControllerCompat.getMediaController(this@MainActivity).getPlaybackState().getState() === PlaybackStateCompat.STATE_PLAYING) {
//                        MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().pause()
//                    }
//
//                    mCurrentState = Constants.STATE.STATE_PAUSED
//                }
//            }
//        })

    }


}
