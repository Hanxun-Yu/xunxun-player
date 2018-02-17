package org.crashxun.player.widget.xunxun.menu;

import android.view.KeyEvent;
import android.view.View;

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
    interface OnKeyListener {
        boolean onKey(KeyEvent event, IMenu iMenu);
        void onChildMenu(String childMenuID,IMenu iMenu);
    }
}
