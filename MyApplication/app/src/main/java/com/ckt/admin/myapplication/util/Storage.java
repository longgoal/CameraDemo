package com.ckt.admin.myapplication.util;

import android.os.Environment;
import android.util.Log;

import com.ckt.admin.myapplication.Exif.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 2017/9/25.
 */

public class Storage {
    public static final String STORAGE_STRING_ID = "Storage";
    private static final String TAG = "Storage";

    public static final String DCIM = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString();

    public static final String DIRECTORY = DCIM + "/Camera";
    public static final String RAW_DIRECTORY = DCIM + "/Camera/raw";
    public static final String DEBUG_DIRECTORY = DCIM + "/Debug";

    public static String getImageFilePath(String title) {
        if (title.contains("yuv")) {
            return DIRECTORY + "/" + title + ".yuv";
        } else if (title.contains("jpeg")) {
            return DIRECTORY + "/" + title + ".jpeg";
        }
        return DIRECTORY + "/" + title + ".jpeg";
    }

    /**
     * save jpeg
     */
    public static int writeJpeg(String filePath, byte[] data, ExifInterface exifInterface) {
        CameraUtil.checkCameraFolder();
        if (data != null && exifInterface != null) {
            try {
                exifInterface.writeExif(data, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "liang.chen writeJpeg->fileParh:" + filePath);
        writeFile(filePath, data);
        return data.length;
    }

    /**
     * save file
     */
    public static void writeFile(String filepath, byte[] data) {
        FileOutputStream fileOutputStream = null;
        File file = new File(filepath);
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
