package com.ckt.admin.myapplication.util;

import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;

import java.io.File;
import java.util.IllegalFormatCodePointException;

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

    public static int getJpegRotation(int phoneOrientation, Camera.CameraInfo cameraInfo) {
        int r = 0;
        int roration = getPhoneRotation(phoneOrientation);
        if (phoneOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                r = (cameraInfo.orientation - roration + 360) % 360;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                r = (cameraInfo.orientation + roration ) % 360;
            }
        }
        return r;
    }

    public static int getPhoneRotation(int phoneOrientation) {
        if (phoneOrientation <= 5 || phoneOrientation >= 355)
            return 0;
        else if (phoneOrientation >= 85 && phoneOrientation <= 95)
            return 90;
        else if (phoneOrientation >= 175 && phoneOrientation <= 185)
            return 180;
        else if (phoneOrientation >= 265 && phoneOrientation <= 275)
            return 270;
        return 0;
    }

}
