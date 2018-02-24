package org.crashxun.player.xunxun.keyboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.fragment.FileBrowerFragment;

import static org.crashxun.player.xunxun.common.Constant.ACTION_MAIN_FILEBROWER_FILESELECTED;

/**
 * Created by xunxun on 2018/2/24.
 */

public class KeyboardTestActivity extends FragmentActivity {

    private KeyboardFragment keyboardFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        keyboardFragment = KeyboardFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.keyboard, keyboardFragment);
        transaction.commit();
        keyboardFragment.setKeyboardEventListener(new KeyboardFragment.KeyboardEventListener() {
            @Override
            public void onClose() {

                findViewById(R.id.confirm).setFocusable(true);
            }

            @Override
            public void onShow() {
                findViewById(R.id.confirm).setFocusable(false);
            }
        });


        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    keyboardFragment.setUserVisibleHint(true);
            }
        });
    }
}
