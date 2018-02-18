package org.crashxun.player.widget.xunxun.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.crashxun.player.R;
import org.crashxun.player.fragments.TracksFragment;
import org.crashxun.player.widget.xunxun.common.Constant;
import org.crashxun.player.widget.xunxun.menu.IMenu;
import org.crashxun.player.widget.xunxun.menu.MenuBean;
import org.crashxun.player.widget.xunxun.menu.MenuParams;
import org.crashxun.player.widget.xunxun.menu.MenuView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xunxun on 2018/2/12.
 */

public class MenuLeftFragment extends Fragment implements IMenu.OnKeyListener {
    RelativeLayout menuLayout;
    public List<IMenu> menus;
    private static List<MenuBean> data;
    Handler handler = new Handler();

    String TAG = "MenuLeftActivity_xunxun";
    TextView titleText;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_left_menu,container,false);
        menuLayout = rootView.findViewById(R.id.menuLayout);
        titleText = rootView.findViewById(R.id.title);

        return rootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //        if (data == null) {
        String res = getFromAssets(getContext(), "menu.json");
        titleText.setText("");

        Gson gson = new Gson();
        data = gson.fromJson(res, new TypeToken<List<MenuBean>>() {
        }.getType());
//        }
        initParams();
        initDynamicMenu();
        menus = getMenuView(data);

        Log.d(TAG, "data:" + data);

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {

//            }
//        }, 100);

        addView(menuLayout);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG,"setUserVisibleHint :"+isVisibleToUser);
        if(isVisibleToUser) {
            if(listener!=null)
                listener.onShow();
            setTile(findMenuByID("0").getData().menuName);
            findMenuByID("0").onComeIn();
            showShadow();
        } else {

        }
    }

    public static MenuLeftFragment newInstance() {
        MenuLeftFragment f = new MenuLeftFragment();
        return f;
    }

    String json;
    public MenuLeftFragment setArgument(String params) {
        this.json = params;
        return this;
    }

    private void initParams() {
        //音轨列表
        //字幕列表
        //字幕延迟时间
        //影片信息
        //画面比例

        MenuParams params = new Gson().fromJson(json, MenuParams.class);
        for (MenuParams.MTrackInfo mTrackInfo : params.audioList) {
            MenuBean.MenuItemBean itemBean = new MenuBean.MenuItemBean();
            itemBean.itemType = MenuBean.MenuItemBean.ItemType.radiobutton;
            itemBean.itemName = mTrackInfo.trackName;
            itemBean.itemParams = new String[]{Constant.ACTION_AUDIO_CHANGED,
                    mTrackInfo.selected ? "true" : "false", "1"};
            Map<String,String> itemParams = new HashMap<>();
            itemParams.put(Constant.KEY_PARAMS_AUDIO,String.valueOf(mTrackInfo.trackIndex));
            itemBean.itemParmasKV.add(itemParams);
            itemBean.itemID = String.valueOf(mTrackInfo.trackIndex);
            getAudioMenu().items.add(itemBean);
        }


        for(MenuBean.MenuItemBean itemBean : getRatioMenu().items) {
            if(itemBean.itemID.equals(params.ratio)) {
                itemBean.itemParams[1] = "true";
            } else {
                itemBean.itemParams[1] = "false";
            }
        }

    }

    private MenuBean getRatioMenu() {
        MenuBean ret = null;
        for (MenuBean bean : data) {
            if (bean.menuID.equals("ratioSub"))
                ret = bean;
        }
        return ret;
    }

    private MenuBean getAudioMenu() {
        MenuBean ret = null;
        for (MenuBean bean : data) {
            if (bean.menuID.equals("audioSub"))
                ret = bean;
        }
        return ret;
    }

    private void initDynamicMenu() {

    }

    private void addView(ViewGroup parent) {
        for (IMenu iMenu : menus) {
            parent.addView((MenuView) iMenu);
        }
    }

    private void removeView(ViewGroup parent) {
        parent.removeAllViews();
    }


    private void showShadow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        getView().findViewById(R.id.shadow).startAnimation(alphaAnimation);
    }

    private void hideShadow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        getView().findViewById(R.id.shadow).startAnimation(alphaAnimation);
    }


    private List<IMenu> getMenuView(List<MenuBean> data) {
        List<IMenu> list = new ArrayList<>();
        for (MenuBean menuBean : data) {
            MenuView iMenu = new MenuView(getContext());
            iMenu.setData(menuBean, 400, 100);
            iMenu.setOnKeyListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            iMenu.setLayoutParams(params);
            list.add(iMenu);
        }
        return list;
    }



//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.d("xunxun", "onKeyUp ");
//        return super.onKeyUp(keyCode, event);
//    }

//    @Override
//    public void finish() {
//        super.finish();
////        overridePendingTransition(0, R.anim.act_left_menu_out);
//        overridePendingTransition(0, 0);
//
//    }

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
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_BACK:
//                    handleKeyBack(iMenu);
//                    ret = true;
//                    break;
//            }
//        }
        return ret;
    }

    @Override
    public void onChildMenu(String childMenuID, IMenu iMenu) {
        iMenu.onBackground();
        setTile(findMenuByID(childMenuID).getData().menuName);
        findMenuByID(childMenuID).onComeIn();
    }

    @Override
    public void onSuperMenu(IMenu iMenu) {
        iMenu.onDismiss();
        if (iMenu.getData().menuID.equals("0")) {
            hideShadow();
            setTile("");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    finish();
                    if(listener!=null) {
                        listener.onClose();
                    }
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
        AlphaAnimation oldAnim = new AlphaAnimation(1.0f, 0.0f);
        oldAnim.setDuration(200);
        final AlphaAnimation newAnim = new AlphaAnimation(0.0f, 1.0f);
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

    MenuEventListener listener;

    public void setListener(MenuEventListener listener) {
        this.listener = listener;
    }

    public interface MenuEventListener {
        void onClose();
        void onShow();
    }
}
