package com.ckt.admin.myapplication;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ckt.admin.myapplication.manager.CameraManagerImp;
import com.ckt.admin.myapplication.manager.CameraManager;
import com.ckt.admin.myapplication.manager.CameraManager.CameraPorxy;


public class MainActivity extends AppCompatActivity {
	private final String TAG="MainActivity";
	private static final int MAIN_CAMERA_ID = 0;
	private static final int SUB_CAMERA_ID = 2;

	private CameraManager mCameraManager;
	private CameraPorxy mCameraProxyImp;
	private Camera.Parameters mParamters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		mCameraManager = new CameraManagerImp();
		//Log.d(TAG,"init():camera numbers ->"+mCameraManager.getCameraNums());
		mCameraProxyImp = mCameraManager.getCamera(MAIN_CAMERA_ID);
		mParamters = mCameraProxyImp.getCameraParameters();
		//mParamters.setFlashMode();


	}
}
