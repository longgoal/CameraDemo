package com.ckt.admin.myapplication.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.WindowManager;

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
                Log.d(TAG,"front orientation="+cameraInfo.orientation);
                //cameraInfo.orientation = 270;
                r = (cameraInfo.orientation - roration + 360) % 360;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG,"back orientation="+cameraInfo.orientation);
                //cameraInfo.orientation = 90;
                r = (cameraInfo.orientation + roration) % 360;
            }
        }
        return r;
    }

    public static int getPhoneRotation(int phoneOrientation) {
//        if (phoneOrientation <= 5 || phoneOrientation >= 355)
//            return 0;
//        else if (phoneOrientation >= 85 && phoneOrientation <= 95)
//            return 90;
//        else if (phoneOrientation >= 175 && phoneOrientation <= 185)
//            return 180;
//        else if (phoneOrientation >= 265 && phoneOrientation <= 275)
//            return 270;
//        return 0;
        int input = phoneOrientation;
        phoneOrientation = ((phoneOrientation + 45) / 90 * 90)%360;
        Log.d(TAG,"getPhoneRotation phoneOrientation="+phoneOrientation+",input="+input);
        return  phoneOrientation;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static int getWindowWidth(Context context) {

        WindowManager wm = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        Log.e(TAG, "FocusOverlayManager mScreenWidth:" + mScreenWidth);
        return mScreenWidth;
    }

    public static int getWindowHeigh(Context context) {

        WindowManager wm = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        Log.e(TAG, "FocusOverlayManager mScreenHeight:" + mScreenHeigh);
        return mScreenHeigh;
    }

    /**
     * Clamps x to between min and max (inclusive on both ends, x = min --> min,
     * x = max --> max).
     */
    public static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * Clamps x to between min and max (inclusive on both ends, x = min --> min,
     * x = max --> max).
     */
    public static float clamp(float x, float min, float max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public static Rect rectFToRect(RectF rectF) {
        Rect rect = new Rect();
        rectFToRect(rectF, rect);
        return rect;
    }

    public static RectF rectToRectF(Rect r) {
        return new RectF(r.left, r.top, r.right, r.bottom);
    }

    public static void rectFToRect(RectF rectF, Rect rect) {
        rect.left = Math.round(rectF.left);
        rect.top = Math.round(rectF.top);
        rect.right = Math.round(rectF.right);
        rect.bottom = Math.round(rectF.bottom);
    }

}
