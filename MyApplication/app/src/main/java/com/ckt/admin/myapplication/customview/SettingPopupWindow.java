package com.ckt.admin.myapplication.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.admin.myapplication.MainActivity;
import com.ckt.admin.myapplication.R;
import com.ckt.admin.myapplication.util.CameraUtil;

/**
 * Created by admin on 2017/11/2.
 */

public class SettingPopupWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    private final int BACKGROUND_ALAPH = 120;
    public ISettingBtnListener iSettingBtnListener;
    private ImageButton imgbFrame;
    private ImageButton imgbFlash;
    private ImageButton imgbFrameSync;
    private ImageButton imgbZsl;
    private ImageButton imgbZsd;
    private ImageButton imgbOis;
    private ImageButton imgbExtraSetting;

    //保存到sharepreference;
    private int mFrameMode = 1;
    private boolean mIsFlashOn = false;
    private boolean mIsFrameSyncOn = false;
    private boolean mIsZslOn = false;
    private boolean mIsZsdOn = false;
    private boolean mOisOn = false;

    public SettingPopupWindow(Context context) {
        this(context, null);
    }

    public SettingPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingPopupWindow(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mScreenWidth = CameraUtil.getWindowWidth(context);
        mScreenHeight = CameraUtil.getWindowHeigh(context);
        setWidth(mScreenWidth);
        setHeight(mScreenHeight / 4);
        //setClippingEnabled(false);
        LayoutInflater lay = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.setting_control_content, null);
        initView(view);

        setContentView(view);
        setAnimationStyle(R.style.settingPopupWindow);
        setFocusable(true);
        update();
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(context.getResources().getColor(R.color.review_background));
        colorDrawable.setAlpha(BACKGROUND_ALAPH);
        setBackgroundDrawable(colorDrawable);
    }

    private void initView(View view) {
        imgbFrame = (ImageButton) view.findViewById(R.id.imgb_setting_size);
        imgbFlash = (ImageButton) view.findViewById(R.id.imgb_setting_flash);
        imgbFrameSync = (ImageButton) view.findViewById(R.id.imgb_setting_framesync);
        imgbZsl = (ImageButton) view.findViewById(R.id.imgb_setting_zsl);
        imgbZsd = (ImageButton) view.findViewById(R.id.imgb_setting_zsd);
        imgbOis = (ImageButton) view.findViewById(R.id.imgb_setting_view_ois);
        imgbExtraSetting = (ImageButton) view.findViewById(R.id.imgb_setting_extra);
        imgbFrame.setOnClickListener(this);
        imgbFlash.setOnClickListener(this);
        imgbFrameSync.setOnClickListener(this);
        imgbZsl.setOnClickListener(this);
        imgbZsd.setOnClickListener(this);
        imgbOis.setOnClickListener(this);
        imgbExtraSetting.setOnClickListener(this);
    }

    public void setSettingBtnListener(ISettingBtnListener listener) {
        this.iSettingBtnListener = listener;
    }

    public void showInParentView(View parent) {
        showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgb_setting_size:
                iSettingBtnListener.setFrameSize(mFrameMode);
                break;
            case R.id.imgb_setting_flash:
                iSettingBtnListener.setFlash(mIsFlashOn);
                break;
            case R.id.imgb_setting_framesync:
                iSettingBtnListener.setFrameSync(mIsFrameSyncOn);
                break;
            case R.id.imgb_setting_zsl:
                iSettingBtnListener.setZSL(mIsZslOn);
                break;
            case R.id.imgb_setting_zsd:
                iSettingBtnListener.setZSD(mIsZsdOn);
                break;
            case R.id.imgb_setting_view_ois:
                iSettingBtnListener.setOis(mOisOn);
                break;
            case R.id.imgb_setting_extra:
                iSettingBtnListener.setExtraSetting();
                break;
            default:
                break;
        }
    }

    public interface ISettingBtnListener {

        public void setFrameSize(int which);

        public void setFlash(boolean isOn);

        public void setFrameSync(boolean isOn);

        public void setZSL(boolean isOn);

        public void setZSD(boolean isOn);

        public void setOis(boolean isOn);

        public void setExtraSetting();


    }
}
