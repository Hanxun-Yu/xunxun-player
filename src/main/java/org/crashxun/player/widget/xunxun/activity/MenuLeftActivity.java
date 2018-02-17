package org.crashxun.player.widget.xunxun.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.crashxun.player.R;
import org.crashxun.player.widget.xunxun.menu.IMenu;
import org.crashxun.player.widget.xunxun.menu.MenuBean;
import org.crashxun.player.widget.xunxun.menu.MenuView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xunxun on 2018/2/12.
 */

public class MenuLeftActivity extends Activity implements IMenu.OnKeyListener {
    RelativeLayout menuLayout;
    public List<IMenu> menus;
    private static List<MenuBean> data;
    Handler handler = new Handler();

    String TAG = "MenuLeftActivity_xunxun";
    TextView titleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_menu);
        menuLayout = findViewById(R.id.menuLayout);
        titleText = findViewById(R.id.title);
        if (data == null) {
            String res = getFromAssets(this, "menu.json");
            Gson gson = new Gson();
            data = gson.fromJson(res, new TypeToken<List<MenuBean>>() {
            }.getType());
        }
        menus = getMenuView(data);
        Log.d(TAG, "data:" + data);

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        findMenuByID("0").onComeIn();
        titleText.setText("");
        setTile(findMenuByID("0").getData().menuName);
//            }
//        }, 100);

        addView(menuLayout);
        showShadow();
    }

    private void addView(ViewGroup parent) {
        for (IMenu iMenu : menus) {
            parent.addView((MenuView) iMenu);
        }
    }

    private void removeView(ViewGroup parent) {
        parent.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showShadow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.shadow).startAnimation(alphaAnimation);
    }

    private void hideShadow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.shadow).startAnimation(alphaAnimation);
    }


    private List<IMenu> getMenuView(List<MenuBean> data) {
        List<IMenu> list = new ArrayList<>();
        for (MenuBean menuBean : data) {
            MenuView iMenu = new MenuView(this);
            iMenu.setData(menuBean, 400, 100);
            iMenu.setOnKeyListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            iMenu.setLayoutParams(params);
            list.add(iMenu);
        }
        return list;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("xunxun", "onKeyUp ");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, R.anim.act_left_menu_out);
        overridePendingTransition(0, 0);

    }

    public String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            int lineNum = 0;
            while ((line = bufReader.readLine()) != null) {
                if (lineNum != 0)
                    Result += "\n";
                lineNum++;
                Result += line;
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onKey(KeyEvent event, IMenu iMenu) {
        Log.d(TAG, "onKey action:" + event.getAction() + " code:" + event.getKeyCode() + " iMenu:" + iMenu.getData().menuID);
        boolean ret = false;
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    handleKeyBack(iMenu);
                    ret = true;
                    break;
            }
        }
        return ret;
    }

    @Override
    public void onChildMenu(String childMenuID, IMenu iMenu) {
        iMenu.onBackground();
        setTile(findMenuByID(childMenuID).getData().menuName);
        findMenuByID(childMenuID).onComeIn();
    }


    private void handleKeyBack(IMenu iMenu) {
        iMenu.onDismiss();
        if (iMenu.getData().menuID.equals("0")) {
            hideShadow();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 400);
        } else {
            findMenuByID(iMenu.getData().superMenuID).onComeback();
            setTile(findMenuByID(iMenu.getData().superMenuID).getData().menuName);
        }
    }

    IMenu findMenuByID(String id) {
        for (IMenu iMenu : menus) {
            if (id.equals(iMenu.getData().menuID)) {
                return iMenu;
            }
        }
        return null;
    }

    void setTile(final String title) {
        AlphaAnimation oldAnim = new AlphaAnimation(1.0f,0.0f);
        oldAnim.setDuration(200);
        final AlphaAnimation newAnim = new AlphaAnimation(0.0f,1.0f);
        newAnim.setDuration(200);

        oldAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                titleText.setText(title);
                titleText.startAnimation(newAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        titleText.startAnimation(oldAnim);
    }

}
