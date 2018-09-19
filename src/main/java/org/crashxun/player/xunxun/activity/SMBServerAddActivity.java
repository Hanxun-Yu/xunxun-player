package org.crashxun.player.xunxun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.keyboard.KeyboardFragment;
import org.crashxun.player.xunxun.service.MacAddressGetter;

/**
 * Created by xunxun on 2018/2/24.
 */

public class SMBServerAddActivity extends FragmentActivity {

    private KeyboardFragment keyboardFragment;
    TextView ip;
    Button confirm;
    Button cancel;

    public final static int RQUEST = 0x00020;
    public final static String RESULT_DATA_KEY_IP = "ip";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smb_ip_add);
        ip = findViewById(R.id.ip);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);

        keyboardFragment = KeyboardFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.keyboard, keyboardFragment);
        transaction.commit();
        keyboardFragment.setKeyboardEventListener(new KeyboardFragment.KeyboardEventListener() {
            @Override
            public void onClose() {
                setAllFocusable(true);
                if(edittingView!=null)
                    edittingView.requestFocus();
            }

            @Override
            public void onShow() {
                setAllFocusable(false);
            }

            @Override
            public void onCancel() {
                if(edittingView!=null)
                    edittingView.requestFocus();
            }

            @Override
            public void onConfirm(String text) {
                if(edittingView!=null) {
                    edittingView.setText(text);
                    edittingView.requestFocus();
                }
            }
        });


        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardFragment.setInputType(0);
                showKeyboard((TextView) v);
            }
        });
        String ipAdd = MacAddressGetter.getLocalIpAddress();
        if(ipAdd != null) {
            ipAdd = ipAdd.substring(0, ipAdd.lastIndexOf(".") + 1);
            ip.setText(ipAdd);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked()) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_DATA_KEY_IP,ip.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private boolean checked() {
        return true;
    }

    TextView edittingView;
    void showKeyboard(TextView textView) {
        edittingView = textView;
        keyboardFragment.setInitText(textView.getText().toString());
        keyboardFragment.setUserVisibleHint(true);
    }

    void setAllFocusable(boolean focusable) {
        ip.setFocusable(focusable);
        confirm.setFocusable(focusable);
        cancel.setFocusable(focusable);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, R.anim.act_left_menu_out);
        overridePendingTransition(0, R.anim.keyboard_comeout);
    }
}
