package org.crashxun.player.xunxun.menu;

import android.content.Context;
import android.view.View;

/**
 * Created by xunxun on 2018/2/13.
 */

public class MenuItemAct extends MenuItemView {
    public MenuItemAct(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        rightIcon.setVisibility(View.INVISIBLE);
//        setRightIcon(R.drawable.arrow_right_normal);
    }

    @Override
    public void onFocus(boolean hasFocus) {
//        if(hasFocus) {
//            rightIcon.setVisibility(View.VISIBLE);
//        } else {
//            rightIcon.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onClick() {

    }

    @Override
    public void setParams(String[] params) {

    }


}
