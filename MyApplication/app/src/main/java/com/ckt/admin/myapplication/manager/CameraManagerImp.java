package com.ckt.admin.myapplication.manager;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by admin on 2017/9/3.
 * current open single camera
 */

public class CameraManagerImp implements CameraManager {
    private static final String TAG = "CameraManagerImpl";

    private CameraPorxy mCameraPorxy;

    public CameraManagerImp(){
        mCameraPorxy = new CameraProxyImpl();
    }

    @Override
    public int getCameraNums() {
       int nums = Camera.getNumberOfCameras();
        if (nums == 0)
            Log.d(TAG,"getCameraNums():camera num is 0 can not exe camera ");
        return nums;
    }

    @Override
    public CameraPorxy getCamera(int camId) {
        return mCameraPorxy.openCamera(camId);
    }

    @Override
    public Camera.Parameters getCameraParameters() {
        return mCameraPorxy.getCameraParameters();
    }

    @Override
    public void setCameraParameters(Camera.Parameters cameraParameters) {
        mCameraPorxy.setCameraParameters(cameraParameters);
    }

    @Override
    public void release() {
        mCameraPorxy.relase();
    }


    /**
     *  camera proxy impl
     *
     */
    public class CameraProxyImpl implements CameraPorxy {

        Camera camera;

        public CameraProxyImpl(){}

        @Override
        public CameraProxyImpl openCamera(int camId) {

            camera = camera.open(camId);
            if (camera == null)
                return null;
            return this;
        }

        @Override
        public Camera.Parameters getCameraParameters() {
            return camera.getParameters();
        }

        @Override
        public void relase() {
            camera.release();
        }

        @Override
        public void setCameraParameters(Camera.Parameters cameraParameters) {
            camera.setParameters(cameraParameters);
        }
    }
}
