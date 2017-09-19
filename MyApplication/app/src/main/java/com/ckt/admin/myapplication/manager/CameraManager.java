package com.ckt.admin.myapplication.manager;

import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * Created by admin on 2017/9/3.
 */

public interface CameraManager {

    public int getCameraNums();

    public CameraPorxy getCamera(int camId);

    public Camera.Parameters getCameraParameters();

    public void setCameraParameters(Camera.Parameters cameraParameters);

    public void release();

    /**
     * Camera Proxy interface
     */

    public interface CameraPorxy {

        public CameraPorxy openCamera(int camId);

        public Camera.Parameters getCameraParameters();

        public void setCameraParameters(Camera.Parameters cameraParameters);

        public void setSurfaceHolder(SurfaceHolder surfaceHolder);

        public void startPreview();

        public void stopPreview();

        public void release();

        public void setRotation();

        public void setDisplayOrientation(int rotation);

    }
}
