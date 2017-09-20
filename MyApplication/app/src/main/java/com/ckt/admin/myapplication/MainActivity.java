package com.ckt.admin.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.ckt.admin.myapplication.Exif.Exif;
import com.ckt.admin.myapplication.Exif.ExifInterface;
import com.ckt.admin.myapplication.Exif.ExifTag;
import com.ckt.admin.myapplication.manager.CameraManagerImp;
import com.ckt.admin.myapplication.manager.CameraManager;
import com.ckt.admin.myapplication.manager.CameraManager.CameraPorxy;
import com.ckt.admin.myapplication.manager.CameraParameters;
import com.ckt.admin.myapplication.util.CameraSettings;
import com.ckt.admin.myapplication.util.PermissionsActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private final String TAG = "MainActivity";
    private static final int MAIN_CAMERA_ID = 0;
    private static final int SUB_CAMERA_ID = 2;

    private final int OPEN_CAMERA = 0;
    private final int START_PREVIEW = 1;
    private final int STOP_PREVIEW = 2;
    private final int RELEASE = 3;
    private final int TAKE_PICTURE = 4;

    private CameraManager mCameraManager;
    private CameraPorxy mCameraProxyImp;
    private Camera.Parameters mParamters;
    private TextView mTextView;
    private ImageButton mImageButton;
    private SurfaceView mSurfaceView;

    private boolean mHasCriticalPermissions;
    private SurfaceHolder mSurfaceHolder;
    private Handler CameraCmdHnadler;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermissions() || !mHasCriticalPermissions) {
            Log.v(TAG, "onCreate: Missing critical permissions.");
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Log.d(TAG, "liang.chen init");
        init();
    }

    private void init() {
        mTextView = (TextView) findViewById(R.id.tv);
        mImageButton = (ImageButton) findViewById(R.id.btn_shutter);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mImageButton.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mCameraManager = new CameraManagerImp();
        CameraCmdHnadler = new CameraHandler();
    }

    /**
     * Checks if any of the needed Android runtime permissions are missing.
     * If they are, then launch the permissions activity under one of the following conditions:
     * a) If critical permissions are missing, display permission request again
     * b) If non-critical permissions are missing, just display permission request once.
     * Critical permissions are: camera, microphone and storage. The app cannot run without them.
     * Non-critical permission is location.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        boolean requestPermission = false;

        if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            mHasCriticalPermissions = true;
        } else {
            mHasCriticalPermissions = false;
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRequestShown = prefs.getBoolean(CameraSettings.KEY_REQUEST_PERMISSION, false);
        if (!isRequestShown || !mHasCriticalPermissions) {
            Log.v(TAG, "Request permission");
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(CameraSettings.KEY_REQUEST_PERMISSION, true);
            editor.apply();
            requestPermission = true;
        }
        return requestPermission;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");
        mCameraProxyImp = mCameraManager.getCamera(MAIN_CAMERA_ID);
        mParamters = mCameraProxyImp.getCameraParameters();

        mCameraProxyImp.setCameraParameters(mParamters);
        mCameraProxyImp.setSurfaceHolder(surfaceHolder);
        //调整输出预览数据的方向
        mCameraProxyImp.setDisplayOrientation(CameraParameters.PREVIEW_ROTATION_90);
        mCameraProxyImp.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
        mCameraProxyImp.stopPreview();
        mCameraProxyImp.release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shutter:
                CameraCmdHnadler.sendEmptyMessage(TAKE_PICTURE);
                break;
            default:
                //nothing to do
                break;
        }
    }

    public class CameraHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_CAMERA:
                    break;

                case START_PREVIEW:
                    break;

                case STOP_PREVIEW:
                    break;

                case RELEASE:
                    break;

                case TAKE_PICTURE:
                    mCameraProxyImp.takePicture(null, null, null, new CameraManager.CameraPictureCallback() {
                        @Override
                        public int onPictureTaken(byte[] data, CameraPorxy cameraProxy) {
                            cameraProxy.startPreview();
                            Toast.makeText(MainActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();
                            //读取exif信息
                            // ExifInterface exifInterface = Exif.getExif(data);
                            //List<ExifTag> tags = exifInterface.getAllTags();
                            // Log.d(TAG, "liang.chen");
                            //for (int i = 0; i < 5; i++) {
                            //    ExifTag exif = tags.get(i);
                            //    Log.d(TAG, "liang.chen:exif:tagID" + exif.getTagId() + "  tag:ValuesString" + exif.getValueAsString());
                            // }
                            // Log.d(TAG, "liang.chen:width:exifInterface orientation:" + Exif.getOrientation(data));
                            File file = new File("/sdcard/main.jpeg");
//建立输出字节流
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                //用FileOutputStream 的write方法写入字节数组
                                fos.write(data);
                                System.out.println("写入成功");
//为了节省IO流的开销，需要关闭
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            return 0;
                        }
                    });
                    break;

                default:
                    //nothing to do
                    break;


            }
        }
    }
}
