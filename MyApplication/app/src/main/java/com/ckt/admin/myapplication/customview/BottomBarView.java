package com.ckt.admin.myapplication.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ckt.admin.myapplication.R;


/**
 * Created by admin on 2017/9/28.
 */

public class BottomBarView extends FrameLayout implements View.OnClickListener {
    private String TAG = "BottomControlView";

    private CircleImageView thumnbnailBtn;
    private ImageButton shutterBtn;
    private ImageView settingBtn;
    private BottonBarViewListener mBottonBarViewListener;

    public BottomBarView(Context context) {
        this(context, null);
    }

    public BottomBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "BottomControlView init");
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_bar_wapper, this, true);
        thumnbnailBtn = (CircleImageView) findViewById(R.id.thumbnail_preview);
        thumnbnailBtn.setOnClickListener(this);
        shutterBtn = (ImageButton) findViewById(R.id.shutter_button);
        shutterBtn.setOnClickListener(this);
        settingBtn = (ImageView) findViewById(R.id.app_settings_button);
        settingBtn.setOnClickListener(this);
    }

    public void setBottonBarViewListener(BottonBarViewListener listener) {
        this.mBottonBarViewListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumbnail_preview:
                mBottonBarViewListener.onThumbnailClickListener();
                break;

            case R.id.shutter_button:
                mBottonBarViewListener.onShutterClickListener();
                break;

            case R.id.app_settings_button:
                mBottonBarViewListener.onSettingClickListener();
                break;

            default:
                //nothing
                break;
        }
    }

    public void setShutterBtnEnable(boolean state) {
        shutterBtn.setEnabled(state);
    }

    public void setThumnaiBtnEnable(boolean state) {
        thumnbnailBtn.setEnabled(state);
    }

    public void setSettingBtn(boolean state) {
        settingBtn.setEnabled(state);
    }

    public interface BottonBarViewListener {
        public void onThumbnailClickListener();

        public void onShutterClickListener();

        public void onSettingClickListener();
    }

    public void upDataThumbnai(Bitmap bitmap) {
        thumnbnailBtn.setImageBitmap(bitmap);
    }

}
