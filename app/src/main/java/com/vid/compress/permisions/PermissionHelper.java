package com.vid.compress.permisions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    public static void grantStorageReadWrite(Activity context){
        // check for storage permission
        if(checkStoragePermissionDenied(context)){
            // ask for the permission
            ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private  static boolean checkStoragePermissionDenied(Activity context){
        // check for storage permission
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED;
    }
}
