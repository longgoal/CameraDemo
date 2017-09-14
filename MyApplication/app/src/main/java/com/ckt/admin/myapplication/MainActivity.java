package com.ckt.admin.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ckt.admin.myapplication.manager.CameraManagerImp;
import com.ckt.admin.myapplication.manager.CameraManager;
import com.ckt.admin.myapplication.manager.CameraManager.CameraPorxy;
import com.ckt.admin.myapplication.util.CameraSettings;
import com.ckt.admin.myapplication.util.PermissionsActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
	private final String TAG="MainActivity_liangchen";
	private static final int MAIN_CAMERA_ID = 0;
	private static final int SUB_CAMERA_ID = 2;

	private CameraManager mCameraManager;
	private CameraPorxy mCameraProxyImp;
	private Camera.Parameters mParamters;
	private TextView mTextView;
	private ImageButton mImageButton;
	private boolean mHasCriticalPermissions;

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
		Log.d(TAG,"liang.chen init");
		init();
	}

	private void init() {
		mTextView = (TextView) findViewById(R.id.tv);
		mCameraManager = new CameraManagerImp();
		Log.d(TAG,"init():camera numbers ->"+mCameraManager.getCameraNums());
		mCameraProxyImp = mCameraManager.getCamera(MAIN_CAMERA_ID);
		mParamters = mCameraProxyImp.getCameraParameters();
		mTextView.setText("mParamters:"+mParamters.getPreviewSize() + "   parameters:"+mParamters.getAntibanding());

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
		if(!isRequestShown || !mHasCriticalPermissions) {
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
}
