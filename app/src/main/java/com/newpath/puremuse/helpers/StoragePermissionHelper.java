package com.newpath.puremuse.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class StoragePermissionHelper {
    private static final String TAG = "StorageHandler";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static Activity mActivity;

    public static boolean handlePermissions(Activity activity){
        mActivity=activity;
        if (!hasPermission()) {
            requestPermission();
            return false;
        } else
            return true;
    }

    private static boolean hasPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"Permission is granted");
                //File write logic here
                return true;
            } else {
                Log.w(TAG,"Permission is revoked");
                return false;
            }
        } else {
            Log.i(TAG,"Permission is automatically granted if SDK < Marshmallow");
            return true;
        }

    }

    private static void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(mActivity, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, PermissionCallback cb){

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    cb.onGranted();
                } else {
                    cb.onDenied("Permission Denied, You cannot use local drive .");
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            break;
        }

    }

    public interface PermissionCallback{
        public void onGranted();
        public void onDenied(String err);
    }

}
