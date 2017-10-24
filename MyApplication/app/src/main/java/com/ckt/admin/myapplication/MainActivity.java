package com.ckt.admin.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.admin.myapplication.Exif.Exif;
import com.ckt.admin.myapplication.Exif.ExifInterface;
import com.ckt.admin.myapplication.customview.BottomBarView;
import com.ckt.admin.myapplication.customview.FocusOverlay;
import com.ckt.admin.myapplication.manager.CameraManagerImp;
import com.ckt.admin.myapplication.manager.CameraManager;
import com.ckt.admin.myapplication.manager.CameraManager.CameraPorxy;
import com.ckt.admin.myapplication.manager.CameraParameters;
import com.ckt.admin.myapplication.manager.FocusOverlayManager;
import com.ckt.admin.myapplication.util.BitmapUtils;
import com.ckt.admin.myapplication.util.CameraSettings;
import com.ckt.admin.myapplication.util.CameraUtil;
import com.ckt.admin.myapplication.util.FileSaveServices;
import com.ckt.admin.myapplication.util.PermissionsActivity;

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
    private SurfaceView mSurfaceView;
    private BottomBarView mBottonBarView;
    private ImageButton mImageButtonExtra;
    private ImageButton mImageButtonSwitch;
    private FocusOverlay mFocusOverlay;

    private boolean mHasCriticalPermissions;
    private SurfaceHolder mSurfaceHolder;
    private Handler CameraCmdHnadler;
    public FileSaveServices mFileSaveServices;
    private FileSaveServices.OnImageSaveListener imageSaveListener;
    private int mPhoneOrientation;
    private boolean isBindService = false;
    private OrientationEventListener mPhoneOrientataionEventListener;
    private Camera.AutoFocusCallback autoFocusCallback;
    private FocusOverlayManager mFocusOverlayManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermissions() || !mHasCriticalPermissions) {
            Log.v(TAG, "onCreate: Missing critical permissions.");
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Log.d(TAG, "liang.chen init");
        init();
    }

    private void init() {
        //init view and data
        mTextView = (TextView) findViewById(R.id.tv);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mBottonBarView = (BottomBarView) findViewById(R.id.bottonbar);
        mImageButtonExtra = (ImageButton) findViewById(R.id.imgb_setting_extra);
        mImageButtonSwitch = (ImageButton) findViewById(R.id.imgb_setting_switch);
        mFocusOverlay = (FocusOverlay) findViewById(R.id.focusiverlay);
        mImageButtonSwitch.setOnClickListener(this);
        mImageButtonExtra.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mCameraManager = new CameraManagerImp();
        CameraCmdHnadler = new CameraHandler();
        mFocusOverlayManager = new FocusOverlayManager(MainActivity.this);
        autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if (b) {
                    mFocusOverlay.focusSuccess();
                    Toast.makeText(MainActivity.this, "对焦成功", Toast.LENGTH_SHORT).show();
                } else {
                    mFocusOverlay.focusFaild();
                }
            }
        };
        mBottonBarView.setBottonBarViewListener(new BottomBarView.BottonBarViewListener() {
            @Override
            public void onThumbnailClickListener() {
                Log.e(TAG, "liang.chen onThumbnailClickListener");
            }

            @Override
            public void onShutterClickListener() {
                Log.e(TAG, "liang.chen onShutterClickListener");
                CameraCmdHnadler.sendEmptyMessage(TAKE_PICTURE);
                imageSaveListener = new FileSaveServices.OnImageSaveListener() {
                    @Override
                    public void onImageSaveFinish(Uri uri) {
                        mBottonBarView.setShutterBtnEnable(true);
                        mBottonBarView.setThumnaiBtnEnable(true);
                        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                        sendBroadcast(i);
                        Toast.makeText(MainActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();
                        mBottonBarView.setShutterBtnEnable(true);
                        Bitmap thumbnai = BitmapUtils.decodeThumbnail(CameraUtil.getRealFilePath(MainActivity.this, uri));
                        mBottonBarView.upDataThumbnai(thumbnai);
                    }
                };
            }

            @Override
            public void onSettingClickListener() {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        //init some service
        Intent i = new Intent(MainActivity.this, FileSaveServices.class);
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        mPhoneOrientataionEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                mPhoneOrientation = i;
            }
        };
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

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged");
        Message msg = CameraCmdHnadler.obtainMessage();
        msg.what = START_PREVIEW;
        msg.obj = surfaceHolder;
        CameraCmdHnadler.sendMessage(msg);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
        CameraCmdHnadler.sendEmptyMessage(STOP_PREVIEW);
        CameraCmdHnadler.sendEmptyMessage(RELEASE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgb_setting_extra:

                //Toast.makeText(MainActivity.this, "extra img", Toast.LENGTH_SHORT).show();

                break;

            case R.id.imgb_setting_switch:

                break;
            default:
                //nothing to do
                break;
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "liang.chen onServiceConnected");
            CameraUtil.checkCameraFolder();
            CameraUtil.checkDebugFolder();
            FileSaveServices.MyBinder myBinder = (FileSaveServices.MyBinder) iBinder;
            mFileSaveServices = myBinder.getService();
            isBindService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "FileSaveServices disconnected");
        }
    };

    public class CameraHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_CAMERA:
                    break;

                case START_PREVIEW:
                    SurfaceHolder surfaceHolder = (SurfaceHolder) msg.obj;
                    mCameraProxyImp = mCameraManager.getCamera(MAIN_CAMERA_ID);
                    mParamters = mCameraProxyImp.getCameraParameters();
                    // TODO: will can be set
                    mParamters.setPictureSize(4160, 3120);
                    mParamters.setRotation(0);
                    mCameraProxyImp.setCameraParameters(mParamters);
                    mCameraProxyImp.setSurfaceHolder(surfaceHolder);
                    mCameraProxyImp.setDisplayOrientation(CameraParameters.PREVIEW_ROTATION_90);
                    mCameraProxyImp.startPreview();
                    break;

                case STOP_PREVIEW:
                    if (mCameraProxyImp != null) {
                        mCameraProxyImp.stopPreview();
                    }
                    break;

                case RELEASE:
                    if (mCameraProxyImp != null) {
                        mCameraProxyImp.release();
                    }
                    break;

                case TAKE_PICTURE:
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(0, cameraInfo);
                    int cameraJpegRotation = CameraUtil.getJpegRotation(mPhoneOrientation, cameraInfo);
                    mParamters.setRotation(cameraJpegRotation);
                    upCameraParameters(mParamters);
                    mBottonBarView.setShutterBtnEnable(false);
                    mBottonBarView.setThumnaiBtnEnable(false);
                    mCameraProxyImp.takePicture(null, null, null, new CameraManager.CameraPictureCallback() {
                        @Override
                        public int onPictureTaken(byte[] data, CameraPorxy cameraProxy) {
                            mBottonBarView.setShutterBtnEnable(false);
                            cameraProxy.startPreview();
                            Camera.Size size = cameraProxy.getCameraParameters().getPictureSize();
                            final ExifInterface exif = Exif.getExif(data);
                            //exif de width height
                            Integer exifWidth = exif.getTagIntValue(ExifInterface.TAG_PIXEL_X_DIMENSION);
                            Integer exifHeight = exif.getTagIntValue(ExifInterface.TAG_PIXEL_Y_DIMENSION);
                            int picWidth = size.width;
                            int picHeight = size.height;
                            long currentTime = System.currentTimeMillis();
                            mFileSaveServices.saveImageofJpeg(data, String.valueOf(currentTime), currentTime, picWidth, picHeight, data.length, ".jpeg", imageSaveListener, MainActivity.this.getContentResolver(), null);
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

    @Override
    protected void onStart() {
        super.onStart();
        mPhoneOrientataionEventListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPhoneOrientataionEventListener.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBindService) {
            unbindService(serviceConnection);
            isBindService = false;
        }
        if (mPhoneOrientataionEventListener != null) {
            mPhoneOrientataionEventListener.disable();
        }
    }

    public void upCameraParameters(Camera.Parameters p) {
        mCameraProxyImp.setCameraParameters(p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Rect rect = new Rect(0, 0, CameraUtil.getWindowWidth(MainActivity.this), CameraUtil.getWindowHeigh(MainActivity.this));
                mFocusOverlayManager.setmPreviewRect(rect);
                mFocusOverlayManager.setScreenWidth(CameraUtil.getWindowWidth(MainActivity.this));
                mFocusOverlayManager.setScreenHeight(CameraUtil.getWindowHeigh(MainActivity.this));
                mFocusOverlayManager.setmFocusAreas((int) event.getX(), (int) event.getY());
                mFocusOverlayManager.setmMeteringArea((int) event.getX(), (int) event.getY());
                Camera.Parameters parameters = mCameraProxyImp.getCameraParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFocusAreas(mFocusOverlayManager.getmFocusAreas());
                parameters.setMeteringAreas(mFocusOverlayManager.getmMeteringArea());
                final int min = parameters.getMinExposureCompensation();
                final int max = parameters.getMaxExposureCompensation();
                //focus ui start
                mFocusOverlay.setPosition((int) event.getX(), (int) event.getY());

                // TODO: 2017/10/17 will add exposure Compensation
                parameters.setExposureCompensation(1);
                mCameraProxyImp.setCameraParameters(parameters);
                mCameraProxyImp.autoFocus(autoFocusCallback);
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
