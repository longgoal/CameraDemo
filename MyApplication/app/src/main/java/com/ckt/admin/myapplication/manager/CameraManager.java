package com.ckt.admin.myapplication.manager;

import android.hardware.Camera;

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

    public interface CameraPorxy{

    public CameraPorxy openCamera(int camId);

    public Camera.Parameters getCameraParameters();

    public void relase();

    public void setCameraParameters(Camera.Parameters cameraParameters);

    }
}
