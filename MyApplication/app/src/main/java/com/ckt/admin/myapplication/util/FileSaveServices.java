package com.ckt.admin.myapplication.util;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ckt.admin.myapplication.Exif.ExifInterface;

import java.io.File;

/**
 * Created by admin on 2017/9/25.
 */

public class FileSaveServices extends Service {
    private final String TAG = "FileSaveServices";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder {

        public FileSaveServices getService() {
            return FileSaveServices.this;
        }
    }

    private MyBinder myBinder = new MyBinder();

    public void saveImageofJpeg(byte[] imgDatas, String title, long date, int width, int height, long dataLength,
                                String format, OnImageSaveListener listener, ContentResolver contentResolver, ExifInterface exifInterface) {
        SaveAsyncTask saveAsyncTask = new SaveAsyncTask(imgDatas, title, date, width, height, dataLength, format, listener, contentResolver, exifInterface);
        saveAsyncTask.execute();
    }

    public class SaveAsyncTask extends AsyncTask<Void, Void, Uri> {
        private byte[] mImgDatas;
        private String mTitle;
        private long mDate;
        private int mWidth;
        private int mHeight;
        private String mFormat;
        private long mDataLength;
        private OnImageSaveListener mListener;
        private ContentResolver mContentResolver;
        private ExifInterface mExifInterface;

        public SaveAsyncTask(byte[] imgDatas, String title, long data, int width, int height, long dataLength,
                             String format, OnImageSaveListener listener, ContentResolver contentResolver, ExifInterface exifInterface) {

            this.mImgDatas = imgDatas;
            this.mTitle = title;
            this.mDate = data;
            this.mWidth = width;
            this.mHeight = height;
            this.mFormat = format;
            this.mListener = listener;
            this.mContentResolver = contentResolver;
            this.mExifInterface = exifInterface;
            this.mDataLength = dataLength;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "start save jpeg image");
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            Log.d(TAG, "progressing to save jpeg image");
            String fileName = Storage.getImageFilePath(mTitle, mFormat);
            Storage.writeJpeg(fileName, mImgDatas, null);
            Uri uri = Storage.saveImageInfo(mContentResolver, mTitle, fileName, mDate, mWidth, mHeight, mDataLength, "");
            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            mListener.onImageSaveFinish(uri);
        }
    }

    public interface OnImageSaveListener {
        public void onImageSaveFinish(Uri uri);
    }
}
