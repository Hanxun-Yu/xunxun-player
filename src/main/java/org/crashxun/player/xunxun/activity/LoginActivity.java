package org.crashxun.player.xunxun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.fragment.FileBrowerFragment;
import org.crashxun.player.xunxun.keyboard.KeyboardFragment;

import static org.crashxun.player.xunxun.common.Constant.ACTION_MAIN_FILEBROWER_FILESELECTED;

/**
 * Created by xunxun on 2018/2/24.
 */

public class LoginActivity extends FragmentActivity {

    private KeyboardFragment keyboardFragment;
    TextView username;
    TextView password;
    Button confirm;
    Button cancel;

    public final static int RQUEST = 0x00010;
    public final static String RESULT_DATA_KEY_USERNAME = "username";
    public final static String RESULT_DATA_KEY_PASSWORD = "password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
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


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardFragment.setInputType(0);
                showKeyboard((TextView) v);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardFragment.setInputType(1);
                showKeyboard((TextView) v);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked()) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_DATA_KEY_USERNAME,username.getText().toString());
                    intent.putExtra(RESULT_DATA_KEY_PASSWORD,password.getText().toString());
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
        username.setFocusable(focusable);
        password.setFocusable(focusable);
        confirm.setFocusable(focusable);
        cancel.setFocusable(focusable);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, R.anim.act_left_menu_out);
        overridePendingTransition(0, 0);
    }
}
