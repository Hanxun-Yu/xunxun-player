package org.crashxun.player.widget.xunxun.menu;

import android.content.Context;
import android.view.View;

import org.crashxun.player.R;

/**
 * Created by xunxun on 2018/2/13.
 */

public class MenuItemMenu extends MenuItemView {
    public MenuItemMenu(Context context) {
        super(context);
        rightIcon.setVisibility(View.INVISIBLE);
        setRightIcon(R.drawable.arrow_right_normal);
    }


    @Override
    public void onFocus(boolean hasFocus) {
        if(hasFocus) {
            rightIcon.setVisibility(View.VISIBLE);
        } else {
            rightIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick() {

    }

    @Override
    public void setParams(String[] params) {

    }


}
