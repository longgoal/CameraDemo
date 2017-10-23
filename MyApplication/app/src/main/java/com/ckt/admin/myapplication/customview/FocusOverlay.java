package com.ckt.admin.myapplication.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ckt.admin.myapplication.R;

public class FocusOverlay extends View {
    private final String TAG = "FocusOverlay";
    private Paint circlePaint;
    private int mViewWidht;
    private int mViewHeight;
    private int mRadius;

    public FocusOverlay(Context context) {
        this(context, null);
    }

    public FocusOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(context.getResources().getColor(R.color.panorama_mode_color));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode == MeasureSpec.UNSPECIFIED || hMode == MeasureSpec.UNSPECIFIED) {
            wSize = 75;
            hSize = 75;
        }
        int edge = wSize >= hSize ? hSize : wSize;
        mViewWidht = edge;
        mViewHeight = edge;
        mRadius = edge / 2;
        setMeasuredDimension(edge, edge);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mViewWidht / 2, mViewHeight / 2, mRadius, circlePaint);
        Path path = new Path();
        path.moveTo(mRadius, 0);
        path.lineTo(mRadius, mRadius / 5);
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.rotate(i * 90, mRadius, mRadius);
            canvas.drawPath(path, circlePaint);
            canvas.restore();
        }
    }
}
