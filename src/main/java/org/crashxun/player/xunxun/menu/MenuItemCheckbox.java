package org.crashxun.player.xunxun.menu;

import android.content.Context;
import android.view.View;

import org.crashxun.player.R;

/**
 * Created by xunxun on 2018/2/13.
 */

public class MenuItemCheckbox extends MenuItemView {
    public MenuItemCheckbox(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        leftIcon.setImageResource(R.drawable.radiobutton_normal);
        rightIcon.setVisibility(View.GONE);
    }


    private boolean mChecked = false;

    public void setChecked(boolean checked) {
        mChecked = checked;
        int iconID = checked? R.drawable.radiobutton_selected :R.drawable.radiobutton_normal;
        leftIcon.setImageResource(iconID);
    }

    @Override
    public void onFocus(boolean hasFocus) {

    }

    @Override
    public void onClick() {
        setChecked(!mChecked);
    }

    @Override
    public void setParams(String[] params) {
        boolean checked = Boolean.parseBoolean(params[1]);
        setChecked(checked);
    }
}
