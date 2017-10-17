package com.ckt.admin.myapplication.manager;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;

import com.ckt.admin.myapplication.util.CameraUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2017/10/16.
 */

public class FocusOverlayManager {
    private final static String TAG = "FocusOverlayManager";
    private Rect mPreviewRect;
    private List<Camera.Area> mFocusAreas;
    private List<Camera.Area> mMeteringArea;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    private int FOCUS_AREA_SIZE = 200;
    private int FOCUS_AREA_TOTAL_WIDTH = 2000;
    private int FOCUS_AREA_WIDTH = 1000;

    public FocusOverlayManager(Context context1) {
        this.mContext = context1;
    }

    public void setmPreviewRect(Rect rect) {
        this.mPreviewRect = rect;
    }

    public Rect getmPreviewRect() {
        if (mPreviewRect == null) {
            return null;
        }
        return mPreviewRect;
    }

    public void setmFocusAreas(int x, int y) {
        if (mFocusAreas == null) {
            mFocusAreas = new ArrayList<>(1);
            Rect rect = new Rect();
            Camera.Area area = new Camera.Area(rect, 1);
            mFocusAreas.add(area);
        }
        mFocusAreas.get(0).rect = calculateTapArea1(x, y, 1f);
    }

    public void setmMeteringArea(int x, int y) {
        if (mMeteringArea == null) {
            mMeteringArea = new ArrayList<>(1);
            Rect rect = new Rect();
            Camera.Area area = new Camera.Area(rect, 1);
            mMeteringArea.add(area);
        }
        mMeteringArea.get(0).rect = calculateTapArea1(x, y, 1f);
    }

    public List<Camera.Area> getmFocusAreas() {
        return mFocusAreas;
    }

    public List<Camera.Area> getmMeteringArea() {
        return mMeteringArea;
    }

    /*
    public void calculateTapArea(int x, int y, int size, Rect rect) {
        int left = CameraUtil.clamp(x - size / 2, mPreviewRect.left, mPreviewRect.right - size);
        int top = CameraUtil.clamp(y - size / 2, mPreviewRect.top, mPreviewRect.bottom - size);
        RectF rectF = new RectF(left, top, left + size, top + size);
        Log.e(TAG, "liang.chen left:" + left + " top:" + top + " right:" + (left + size) + " bottom:" + (top + size));
        CameraUtil.rectFToRect(rectF, rect);
    }
    */

    public Rect calculateTapArea1(float x, float y, float coefficient) {

        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();
        int left = clamp(Float.valueOf((y / mScreenHeight) * FOCUS_AREA_TOTAL_WIDTH - FOCUS_AREA_WIDTH).intValue(), areaSize);
        int top = clamp(Float.valueOf(((mScreenWidth - x) / mScreenWidth) * FOCUS_AREA_TOTAL_WIDTH - FOCUS_AREA_WIDTH).intValue(), areaSize);
        return new Rect(left, top, left + areaSize, top + areaSize);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize > FOCUS_AREA_WIDTH) {
            if (touchCoordinateInCameraReper > 0) {
                result = FOCUS_AREA_WIDTH - focusAreaSize;
            } else {
                result = -FOCUS_AREA_WIDTH + focusAreaSize;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }


    public static Rect rectFToRect(RectF rectF) {
        Rect rect = new Rect();
        CameraUtil.rectFToRect(rectF, rect);
        return rect;
    }

    public static RectF rectToRectF(Rect r) {
        return new RectF(r.left, r.top, r.right, r.bottom);
    }


    /**
     * Returns width of auto focus region in pixels.
     */
    private int getAFRegionEdge() {
        return (int) (Math.min(mPreviewRect.width(), mPreviewRect.height()) * 0.3f);
    }

    /**
     * Returns width of metering region in pixels.
     */
    private int getAERegionEdge() {
        return (int) (Math.min(mPreviewRect.width(), mPreviewRect.height()) * 0.3f);
    }

    public void setScreenWidth(int width) {
        this.mScreenWidth = width;
    }

    public void setScreenHeight(int height) {
        this.mScreenHeight = height;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

}
