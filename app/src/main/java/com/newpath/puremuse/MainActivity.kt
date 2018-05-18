package com.newpath.puremuse

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.newpath.puremuse.models.AudioFileModel
import com.newpath.puremuse.ui.main.MainFragment
import com.newpath.puremuse.ui.main.MainViewModel
import com.newpath.puremuse.utils.AudioFileScanner
import com.newpath.puremuse.utils.StoragePermissionHandler
import java.util.ArrayList
import android.content.Intent
import com.newpath.puremuse.services.MusicPlayService
import com.newpath.puremuse.utils.Constants.ACTION.START_ACTION


class MainActivity : AppCompatActivity() {

    var TAG:String = "MainActivity"
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // use this to start and trigger a service
        val i = Intent(this, MusicPlayService::class.java)
        // potentially add data to the intent
        i.action=START_ACTION
        startService(i)

    }


    fun startScan(activity: Activity){
        var audioFileScanner= AudioFileScanner();

        audioFileScanner.getAllAudioFromDevice(activity, object: AudioFileScanner.Results{
            override fun onError() {
                Log.e(TAG,"error retrieving files")
            }

            override fun onResults(list: ArrayList<AudioFileModel>) {
                Log.i(TAG,"results..")
                for (item in list){
                    Log.d(TAG, item.toString());
                }
                viewModel.updateScannedSongList(list);
            }

        } );
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG,"before onrequestpermission result");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG,"onrequestpermission result");
        StoragePermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults, object: StoragePermissionHandler.PermissionCallback{
            override fun onGranted() {

                startScan(this@MainActivity);
            }

            override fun onDenied(err: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

}
