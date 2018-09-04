package org.crashxun.player.xunxun.menu;

import android.view.KeyEvent;

/**
 * Created by xunxun on 2018/2/12.
 */

public interface IMenu {
    void onComeIn();
    void onBackground();
    void onComeback();
    void onDismiss();
    void setOnKeyListener(OnKeyListener onKeyListener);
    MenuBean getData();
    void setData(MenuBean menuBean);
    void updateData(MenuBean menuBean);
    interface OnKeyListener {
        boolean onKey(KeyEvent event, IMenu iMenu);
        void onChildMenu(String childMenuID,IMenu iMenu);
        void onSuperMenu(IMenu iMenu);
    }
}
