package com.ckt.admin.myapplication.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

    public static String getImageFilePath(String title, String format) {
        if (format.contains(".yuv")) {
            return DIRECTORY + "/" + title + ".yuv";
        } else if (format.contains(".jpeg")) {
            return DIRECTORY + "/" + title + ".jpeg";
        }
        return null;
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

    public static Uri saveImageInfo(ContentResolver resolver, String title, String filePath, long date, int width, int height, long dataLength,
                                    String format) {
        Uri uri = null;
        if (resolver == null) {
            Log.e(TAG, "liang.chen saveImageinfo resolver is null");
            return null;
        }
        ContentValues c = getImageContentValues(title, filePath, date, width, height, dataLength, format);
        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c);
        } catch (Throwable throwable) {
            Log.e(TAG, "saveImageInfo save imageinfo into sqlite error");
        }
        return uri;
    }

    //get image contentvalue
    public static ContentValues getImageContentValues(String title, String filePath, long date, int width, int height, long dataLength,
                                                      String format) {
        Log.e(TAG, "liang.chen getImageContentValues->");
        ContentValues contentValues = new ContentValues(8);
        contentValues.put(MediaStore.Images.ImageColumns.TITLE, title);
        contentValues.put(MediaStore.Images.ImageColumns.DATA, filePath);
        contentValues.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
        contentValues.put(MediaStore.Images.ImageColumns.WIDTH, width);
        contentValues.put(MediaStore.Images.ImageColumns.HEIGHT, height);
        contentValues.put(MediaStore.Images.ImageColumns.SIZE, dataLength);
        contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title + ".jpeg");
        return contentValues;
    }

    public static boolean isImageExis(String fileName, ContentResolver resolver) {
        if (fileName.equals("") || resolver == null) {
            return false;
        }
        String[] project = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, project, null, null, null);
        String tempFileName;
        while (cursor.moveToNext()) {
            Log.e(TAG, "liang.chen isImageExis->id: " + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)) + "/n" +
                    "  data: " + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)) + " /n" +
                    " size: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
        }
        return true;
    }
}
