package com.ckt.admin.myapplication.customview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ckt.admin.myapplication.R;
import com.ckt.admin.myapplication.manager.FocusOverlayManager;

public class FocusOverlay extends View implements FocusOverlayManager.IFocusUiHander {
    private final String TAG = "FocusOverlay";
    private final float FOCUS_CIRCLE_SIZE = 100.0f;
    private final float FOCUS_CIRCLE_RATE_START = 0.5f;
    private final float FOCUS_CIRCLE_RATE_END = 1.2f;
    private final float FOCUS_CIRCLE_RATE_MIDDLE = 1.0f;
    private final int FOCUS_CIRCLE_ANIMATOR_TIME_FIRST = 300;
    private final int FOCUS_CIRCLE_ANIMATOR_TIME_SECOND = 150;
    private final int FOCUS_MIDDLE_RECT_WIDTH = 15;
    private final int FOCUS_MIDDLE_RECT_HEIGHT = 30;
    private final int FOCUS_STATE_PROCESSING = 0x0001;
    private final int FOCUS_STATE_SUCCESS = 0x0010;
    private final int FOCUS_STATE_FAILD = 0x0100;
    private final int FOCUS_STATE_INVISIBALE = 0x1000;
    private Paint circlePaint;
    private int mViewWidth;
    private int mViewHeight;
    private float mRadius = FOCUS_CIRCLE_SIZE;
    private int mPositionX;
    private int mPositionY;
    private Handler mHandler = new FocusHandler();
    private Context mContext;

    public FocusOverlay(Context context) {
        this(context, null);
    }

    public FocusOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(mContext.getResources().getColor(R.color.panorama_mode_color));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(4);
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
        ;
        mViewWidth = wSize;
        mViewHeight = hSize;
        mPositionX = wSize / 2;
        mPositionY = hSize / 2;
        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mPositionX, mPositionY, mRadius, circlePaint);
        Path path = new Path();
        path.moveTo(mPositionX, mPositionY - mRadius);
        path.lineTo(mPositionX, mPositionY - mRadius + 30);
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.rotate(i * 90, mPositionX, mPositionY);
            canvas.drawPath(path, circlePaint);
            canvas.restore();
        }
        Path path1 = new Path();
        for (int j = 0; j < 2; j++) {
            canvas.save();
            canvas.rotate(j * 180, mPositionX, mPositionY);
            path1.moveTo(mPositionX - FOCUS_MIDDLE_RECT_WIDTH, mPositionY - FOCUS_MIDDLE_RECT_WIDTH);
            path1.lineTo(mPositionX - FOCUS_MIDDLE_RECT_HEIGHT, mPositionY - FOCUS_MIDDLE_RECT_WIDTH);
            path1.lineTo(mPositionX - FOCUS_MIDDLE_RECT_HEIGHT, mPositionY + FOCUS_MIDDLE_RECT_WIDTH);
            path1.lineTo(mPositionX - FOCUS_MIDDLE_RECT_WIDTH, mPositionY + FOCUS_MIDDLE_RECT_WIDTH);
            canvas.drawPath(path1, circlePaint);
            canvas.restore();
        }
        canvas.drawPoint(mPositionX, mPositionY, circlePaint);
    }

    @Override
    public void setPosition(int x, int y) {
        if (x < 0 || y < 0) {
            Log.e(TAG, "Focusoverylay Invalid Position");
            return;
        }
        circlePaint.setColor(mContext.getResources().getColor(R.color.panorama_mode_color));
        setVisibility(View.VISIBLE);
        mPositionX = x;
        mPositionY = y;
        focusProcessing();
    }

    @Override
    public void focusProcessing() {
        mHandler.sendEmptyMessage(FOCUS_STATE_PROCESSING);
    }

    @Override
    public void focusSuccess() {
        mHandler.sendEmptyMessageDelayed(FOCUS_STATE_SUCCESS, 800);
    }

    @Override
    public void focusFaild() {
        mHandler.sendEmptyMessageDelayed(FOCUS_STATE_FAILD, 800);
    }

    private class FocusHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case FOCUS_STATE_PROCESSING:
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(FOCUS_CIRCLE_RATE_START, FOCUS_CIRCLE_RATE_END);
                    valueAnimator.setDuration(FOCUS_CIRCLE_ANIMATOR_TIME_FIRST);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float rate = (float) valueAnimator.getAnimatedValue();
                            mRadius = rate * FOCUS_CIRCLE_SIZE;
                            invalidate();
                        }
                    });

                    ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(FOCUS_CIRCLE_RATE_END, FOCUS_CIRCLE_RATE_MIDDLE);
                    valueAnimator1.setDuration(FOCUS_CIRCLE_ANIMATOR_TIME_SECOND);
                    valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float rate = (float) valueAnimator.getAnimatedValue();
                            mRadius = rate * FOCUS_CIRCLE_SIZE;
                            Log.e(TAG, "liang.chen rate->" + rate + "  mRadius->" + mRadius);
                            invalidate();
                        }
                    });
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(valueAnimator1).after(valueAnimator);
                    animatorSet.start();
                    break;

                case FOCUS_STATE_SUCCESS:
                    circlePaint.setColor(mContext.getResources().getColor(R.color.focus_debug_success));
                    invalidate();
                    this.sendEmptyMessageDelayed(FOCUS_STATE_INVISIBALE, 1000);
                    break;

                case FOCUS_STATE_FAILD:
                    circlePaint.setColor(mContext.getResources().getColor(R.color.pano_progress_indication_fast));
                    invalidate();
                    this.sendEmptyMessageDelayed(FOCUS_STATE_INVISIBALE, 1000);
                    break;
                case FOCUS_STATE_INVISIBALE:
                    setVisibility(View.GONE);
                    break;

                default:
                    /*nothing to do */
                    break;
            }
        }
    }
}
