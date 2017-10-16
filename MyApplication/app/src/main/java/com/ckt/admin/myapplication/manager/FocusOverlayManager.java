package com.ckt.admin.myapplication.manager;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;

import com.ckt.admin.myapplication.util.CameraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by admin on 2017/10/16.
 */

public class FocusOverlayManager {
    private final static String TAG = "FocusOverlayManager";
    private Rect mPreviewRect;
    private List<Camera.Area> mFocusAreas;
    private List<Camera.Area> mMeteringArea;
    private Context mContext;

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
        calculateTapArea(x, y, getAFRegionEdge(), mFocusAreas.get(0).rect);
    }

    public void setmMeteringArea(int x, int y) {
        if (mMeteringArea == null) {
            mMeteringArea = new ArrayList<>(1);
            Rect rect = new Rect();
            Camera.Area area = new Camera.Area(rect, 600);
            mMeteringArea.add(area);
        }
        calculateTapArea(x, y, getAERegionEdge(), mMeteringArea.get(0).rect);
    }

    public List<Camera.Area> getmFocusAreas() {
        return mFocusAreas;
    }

    public List<Camera.Area> getmMeteringArea() {
        return mMeteringArea;
    }

    public void calculateTapArea(int x, int y, int size, Rect rect) {
        int left = CameraUtil.clamp(x - size / 2, mPreviewRect.left, mPreviewRect.right - size);
        int top = CameraUtil.clamp(y - size / 2, mPreviewRect.top, mPreviewRect.bottom - size);
        Log.e(TAG, "liang.chen left:" + left + "  top:" + top);
        RectF rectF = new RectF(left, top, left + size, top + size);
        Log.e(TAG, "liang.chen left:" + left + " top:" + top + " right:" + (left + size) + " bottom:" + (top + size));
        CameraUtil.rectFToRect(rectF, rect);
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


}
