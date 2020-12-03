package com.ckt.admin.myapplication;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.admin.myapplication.Exif.Exif;
import com.ckt.admin.myapplication.Exif.ExifInterface;
import com.ckt.admin.myapplication.customview.BottomBarView;
import com.ckt.admin.myapplication.customview.FocusOverlay;
import com.ckt.admin.myapplication.customview.ResizeAbleSurfaceView;
import com.ckt.admin.myapplication.customview.SettingPopupWindow;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private final String TAG = "MainActivity";
    private static final int MAIN_CAMERA_ID = 0;
    private static final int SUB_CAMERA_ID = 1;
    private int mCurrentCameraId = 0;
    private boolean switchCamera = false;
    private boolean mPreviewSave = false;

    private final int OPEN_CAMERA = 0;
    private final int START_PREVIEW = 1;
    private final int STOP_PREVIEW = 2;
    private final int RELEASE = 3;
    private final int TAKE_PICTURE = 4;

    private CameraManager mCameraManager;
    private CameraPorxy mCameraProxyImp;
    private Camera.Parameters mParamters;
    private TextView mTextView;
    private ResizeAbleSurfaceView mSurfaceView;
    private BottomBarView mBottonBarView;
    private ImageButton mImageButtonExtra;
    private ImageButton mImageButtonSwitch;
    private FocusOverlay mFocusOverlay;

    private boolean mHasCriticalPermissions;
    private SurfaceHolder mSurfaceHolder;
    private Handler CameraCmdHandler;
    public FileSaveServices mFileSaveServices;
    private FileSaveServices.OnImageSaveListener imageSaveListener;
    private int mPhoneOrientation;
    private boolean isBindService = false;
    private OrientationEventListener mPhoneOrientataionEventListener;
    private Camera.AutoFocusCallback autoFocusCallback;
    private FocusOverlayManager mFocusOverlayManager;
    private RelativeLayout mParent;


    //// TODO: temp data , will use SharePreference
    private String[] mFlashModeValue = {"on", "auto", "torch", "off"};
    private int mCurrentFlashMode = 3;

    //内存监听
    private ComponentCallbacks2 mComponentCallbacks2 = new ComponentCallbacks2() {
        @Override
        public void onTrimMemory(int level) {

            switch (level) {
                case TRIM_MEMORY_UI_HIDDEN:
                    Toast.makeText(MainActivity.this, "TRIM_MEMORY_UI_HIDDEN", Toast.LENGTH_SHORT).show();

                    break;

                case TRIM_MEMORY_RUNNING_MODERATE:
                    Toast.makeText(MainActivity.this, "TRIM_MEMORY_RUNNING_MODERATE", Toast.LENGTH_SHORT).show();

                    break;

                case TRIM_MEMORY_RUNNING_LOW:
                    Toast.makeText(MainActivity.this, "TRIM_MEMORY_RUNNING_LOW", Toast.LENGTH_SHORT).show();

                    break;

                case TRIM_MEMORY_RUNNING_CRITICAL:
                    Toast.makeText(MainActivity.this, "TRIM_MEMORY_RUNNING_CRITICAL", Toast.LENGTH_SHORT).show();

                    break;

                case TRIM_MEMORY_BACKGROUND:
                    Toast.makeText(MainActivity.this, "TRIM_MEMORY_BACKGROUND", Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;


            }

        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {

        }
    };

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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        registerComponentCallbacks(mComponentCallbacks2);
        //init view and data
        mParent = (RelativeLayout) findViewById(R.id.activity_main);
        mTextView = (TextView) findViewById(R.id.tv);
        mSurfaceView = (ResizeAbleSurfaceView) findViewById(R.id.surfaceview);
        mSurfaceView.resize(720,1280);
        mBottonBarView = (BottomBarView) findViewById(R.id.bottonbar);
        mImageButtonExtra = (ImageButton) findViewById(R.id.imgb_setting_extra);
        mImageButtonSwitch = (ImageButton) findViewById(R.id.imgb_setting_switch);
        mFocusOverlay = (FocusOverlay) findViewById(R.id.focusiverlay);
        mImageButtonSwitch.setOnClickListener(this);
        mImageButtonExtra.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mCameraManager = new CameraManagerImp();
        CameraCmdHandler = new CameraHandler();
        mFocusOverlayManager = new FocusOverlayManager(MainActivity.this);
        autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if (b) {
                    mFocusOverlay.focusSuccess();
                    //Toast.makeText(MainActivity.this, "对焦成功", Toast.LENGTH_SHORT).show();
                } else {
                    mFocusOverlay.focusFaild();
                }
            }
        };
        mBottonBarView.setBottonBarViewListener(new BottomBarView.BottonBarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onThumbnailClickListener() {
                Log.d(TAG, "onThumbnailClickListener");

                SettingPopupWindow settingPopupWindow = new SettingPopupWindow(MainActivity.this);
                settingPopupWindow.showInParentView(mParent);
                settingPopupWindow.setSettingBtnListener(new SettingPopupWindow.ISettingBtnListener() {
                    @Override
                    public void setFrameSize(int which) {
                        Log.d(TAG, "liang.chen setFrameSize");
                    }

                    @Override
                    public void setFlash(boolean isOn) {
                        Camera.Parameters parameters = mCameraProxyImp.getCameraParameters();
                        String flashModeStr = mFlashModeValue[mCurrentFlashMode++];
                        mCurrentFlashMode = mCurrentFlashMode % 4;
                        parameters.setFlashMode(flashModeStr);
                        mCameraProxyImp.setCameraParameters(parameters);
                    }

                    @Override
                    public void setFrameSync(boolean isOn) {
                        Log.d(TAG, "liang.chen setFrameSync");
                    }

                    @Override
                    public void setZSL(boolean isOn) {
                        Log.d(TAG, "liang.chen setZSL");
                    }

                    @Override
                    public void setZSD(boolean isOn) {
                        Log.d(TAG, "liang.chen setZSD");
                    }

                    @Override
                    public void setOis(boolean isOn) {
                        Log.d(TAG, "liang.chen setOis");
                    }

                    @Override
                    public void setExtraSetting() {
                        Log.d(TAG, "liang.chen setExtraSetting");
                    }
                });
                //settingPopupWindow.setFocusable(true);
                controlAnimation(mBottonBarView, 0.0f, 1.0f, 250, View.GONE);
                settingPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        controlAnimation(mBottonBarView, 1.0f, 0.0f, 250, View.VISIBLE);
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
                    }
                });
            }

            public void controlAnimation(final View view1, float from, float to, long duration, final int isVisible) {
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, from, Animation.RELATIVE_TO_SELF, to);
                translateAnimation.setDuration(duration);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view1.setVisibility(isVisible);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view1.startAnimation(translateAnimation);
            }

            @Override
            public void onShutterClickListener() {
                Log.d(TAG, "onShutterClickListener");
                CameraCmdHandler.sendEmptyMessage(TAKE_PICTURE);
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
                //mPhoneOrientation = i;
                Log.d(TAG,"mPhoneOrientation="+mPhoneOrientation);
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
        int counts = mCameraManager.getCameraNums();
        if(counts > 0) {
            Message msg = CameraCmdHandler.obtainMessage();
            msg.what = START_PREVIEW;
            msg.obj = surfaceHolder;
            msg.arg1 = counts;
            CameraCmdHandler.sendMessage(msg);
        } else {
            Log.d(TAG, "no camere");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
        CameraCmdHandler.sendEmptyMessage(STOP_PREVIEW);
        CameraCmdHandler.sendEmptyMessage(RELEASE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgb_setting_extra:
                //Toast.makeText(MainActivity.this, "extra img", Toast.LENGTH_SHORT).show();
                break;

            case R.id.imgb_setting_switch:
                switchCamera = switchCamera ? false : true;
                if (switchCamera) {
                    mCurrentCameraId = SUB_CAMERA_ID;
                } else {
                    mCurrentCameraId = MAIN_CAMERA_ID;
                }
                CameraCmdHandler.sendEmptyMessage(STOP_PREVIEW);
                CameraCmdHandler.sendEmptyMessage(RELEASE);
                Message msg = CameraCmdHandler.obtainMessage();
                msg.what = START_PREVIEW;
                msg.obj = mSurfaceHolder;
                CameraCmdHandler.sendMessage(msg);
                break;
            default:
                //nothing to do
                break;
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
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
                    int count = msg.arg1;

                    mCameraProxyImp = mCameraManager.getCamera(mCurrentCameraId);
                    mParamters = mCameraProxyImp.getCameraParameters();
                    // TODO: will set

                    //mParamters.setPictureSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
                    //mParamters.setPictureSize(4160, 3120);
                    //mParamters.setRotation(0);
                    Camera.Size size = mParamters.getPreviewSize();
                    List<Camera.Size> previewSizes =  mParamters.getSupportedPreviewSizes();
                    List<Camera.Size> videoSizes = mParamters.getSupportedVideoSizes();

                    Log.d(TAG, "liang.chen current preview size is ->width" + size.width + "   height:" + size.height +
                            "   screen width:" + CameraUtil.getWindowWidth(MainActivity.this) + "  screen height:" + CameraUtil.getWindowHeigh(MainActivity.this));
                    //mParamters.setPreviewSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
                    mParamters.setPreviewSize(1280, 720);
                    //Log.d(TAG, "liang.chen current preview size is ->width" + size.width + "   height:" + size.height);
                    //mParamters.setPreviewFpsRange(7000,20000);
                    //List<int[]> fpsRanges = mParamters.getSupportedPreviewFpsRange();
                    //List<Integer> frameRates = mParamters.getSupportedPreviewFrameRates();
                    //mParamters.setPreviewFrameRate(30);
                    List<Integer> formats = mParamters.getSupportedPreviewFormats();
                    int format = mParamters.getPreviewFormat();
                    mCameraProxyImp.setCameraParameters(mParamters);
                    mCameraProxyImp.setSurfaceHolder(surfaceHolder);
                    mCameraProxyImp.setDisplayOrientation(getCameraDisplayOrientation(mCurrentCameraId));
                    //mCameraProxyImp.setDisplayOrientation(0);
                    mCameraProxyImp.setPreviewCallback(new MyPreviewCallback());
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
                    //mPreviewSave = true;
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(mCurrentCameraId, cameraInfo);
                    int cameraJpegRotation = CameraUtil.getJpegRotation(mPhoneOrientation, cameraInfo);
                    mParamters.setRotation(cameraJpegRotation);
                    List<Camera.Size> picSizes = mParamters.getSupportedPictureSizes();
                    //mParamters.set("zsl","on");
                    mParamters.setPictureSize(1280,720);
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
    long mLastTime = 0;
    public class MyPreviewCallback  implements   Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera){

            //Log.d(TAG,"onPreviewFrame frame per second =" + 1000/(System.currentTimeMillis() - mLastTime));
            mLastTime = System.currentTimeMillis();
            if(!mPreviewSave)
                return;
            else
                mPreviewSave = false;
            Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    data,
                    ImageFormat.NV21,
                    previewSize.width,
                    previewSize.height,
                    null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
            byte[] rawImage = baos.toByteArray();
            //将rawImage转换成bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
            savebitmap(bitmap);

        }
    };
    private void savebitmap(Bitmap bitmap)
    {
        //因为xml用的是背景，所以这里也是获得背景
        //Bitmap bitmap=((BitmapDrawable)(imageView.getBackground())).getBitmap();
        //创建文件，因为不存在2级目录，所以不用判断exist，要保存png，这里后缀就是png，要保存jpg，后缀就用jpg
        File file=new File(Environment.getExternalStorageDirectory() +"/mfw.png");
        try {
            //文件输出流
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            //压缩图片，如果要保存png，就用Bitmap.CompressFormat.PNG，要保存jpg就用Bitmap.CompressFormat.JPEG,质量是100%，表示不压缩
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            //写入，这里会卡顿，因为图片较大
            fileOutputStream.flush();
            //记得要关闭写入流
            fileOutputStream.close();
            //成功的提示，写入成功后，请在对应目录中找保存的图片
                Toast.makeText(MainActivity.this,"写入成功！目录"+Environment.getExternalStorageDirectory()+"/mfw.png",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //失败的提示
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            //失败的提示
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    public int getCameraDisplayOrientation (int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo (cameraId , info);
        int rotation = getWindowManager ().getDefaultDisplay ().getRotation ();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Log.d(TAG,"front orientation="+info.orientation);
            //info.orientation = 270;
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            Log.d(TAG,"back orientation="+info.orientation+",display rotation="+rotation);
            //info.orientation = 90;
            result = ( info.orientation - degrees + 360) % 360;
        }
        return result;
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
//                Rect rect = new Rect(0, 0, CameraUtil.getWindowWidth(MainActivity.this), CameraUtil.getWindowHeigh(MainActivity.this));
//                mFocusOverlayManager.setmPreviewRect(rect);
//                mFocusOverlayManager.setScreenWidth(CameraUtil.getWindowWidth(MainActivity.this));
//                mFocusOverlayManager.setScreenHeight(CameraUtil.getWindowHeigh(MainActivity.this));
//                mFocusOverlayManager.setmFocusAreas((int) event.getX(), (int) event.getY());
//                mFocusOverlayManager.setmMeteringArea((int) event.getX(), (int) event.getY());
//                Camera.Parameters parameters = mCameraProxyImp.getCameraParameters();
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                parameters.setFocusAreas(mFocusOverlayManager.getmFocusAreas());
//                parameters.setMeteringAreas(mFocusOverlayManager.getmMeteringArea());
//                final int min = parameters.getMinExposureCompensation();
//                final int max = parameters.getMaxExposureCompensation();
//                //focus ui start
//                mFocusOverlay.setPosition((int) event.getX(), (int) event.getY());
//                // TODO: 2017/10/17 will add exposure Compensation
//                parameters.setExposureCompensation(1);
//                mCameraProxyImp.setCameraParameters(parameters);
//                mCameraProxyImp.autoFocus(autoFocusCallback);
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


}
