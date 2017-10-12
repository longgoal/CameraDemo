package com.ckt.admin.myapplication.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
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

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

}
