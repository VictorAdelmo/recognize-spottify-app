package com.vgp.recognizeartists.global.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    public PermissionUtils() {}

    public static boolean permissionsGranted(Context context, String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null){
            for (String permission : PERMISSIONS){
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
