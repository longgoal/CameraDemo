package com.ckt.admin.myapplication.util;

import android.util.Log;

import java.io.File;

/**
 * Created by admin on 2017/9/25.
 */

public class CameraUtil {
    private final static String TAG = "CameraUtil";


    public static void checkCameraFolder() {
        File saveDir = new File(Storage.DIRECTORY);
        if (!saveDir.exists()) {
            Log.d(TAG, "create folder:" + saveDir.toString());
            saveDir.mkdirs();
        }
    }

    public static void checkDebugFolder() {
        File saveDir = new File(Storage.DEBUG_DIRECTORY);
        if (!saveDir.exists()) {
            Log.d(TAG, "create folder: " + saveDir.toString());
            saveDir.mkdirs();
        }
    }


}
