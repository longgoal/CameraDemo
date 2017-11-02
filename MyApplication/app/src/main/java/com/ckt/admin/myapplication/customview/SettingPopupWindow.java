package com.ckt.admin.myapplication.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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

public class SettingPopupWindow extends PopupWindow {

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    private final int BACKGROUND_ALAPH = 120;

    public SettingPopupWindow(Context context) {
        this(context, null);
    }

    public SettingPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingPopupWindow(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TextView view1 = new TextView(context);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view1.setLayoutParams(layoutParams);
        view1.setText("PopupWindowceshi ");
        view1.setTextColor(context.getResources().getColor(R.color.blue_button_text_color));
        view1.setTextSize(15);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams1);
        //linearLayout.addView(view1, layoutParams);
        Button button = new Button(context);
        button.setLayoutParams(layoutParams);
        button.setText("ceshi button");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"popupWindow tan chu ",Toast.LENGTH_SHORT).show();;
            }
        });
        linearLayout.addView(button,layoutParams);
        mScreenWidth = CameraUtil.getWindowWidth(context);
        mScreenHeight = CameraUtil.getWindowHeigh(context);
        setWidth(mScreenWidth);
        setHeight(mScreenHeight / 5);
        setContentView(linearLayout);
        setAnimationStyle(R.style.settingPopupWindow);
        setFocusable(true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(context.getResources().getColor(R.color.review_background));
        colorDrawable.setAlpha(BACKGROUND_ALAPH);
        setBackgroundDrawable(colorDrawable);
    }

    public void showInParentView(View parent) {
        showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


}
