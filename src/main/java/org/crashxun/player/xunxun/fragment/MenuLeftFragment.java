package org.crashxun.player.xunxun.fragment;

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
import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.menu.IMenu;
import org.crashxun.player.xunxun.menu.MenuBean;
import org.crashxun.player.xunxun.menu.MenuParams;
import org.crashxun.player.xunxun.menu.MenuView;

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
        View rootView = inflater.inflate(R.layout.activity_left_menu, container, false);
        menuLayout = rootView.findViewById(R.id.menuLayout);
        titleText = rootView.findViewById(R.id.title);
        titleText.setText("");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        create();
    }

    private void create() {
        //        if (data == null) {
        String res = getFromAssets(getContext(), "menu.json");
        Gson gson = new Gson();
        data = gson.fromJson(res, new TypeToken<List<MenuBean>>() {
        }.getType());
        menus = getMenuView(data);


//        initParams();
        addView(menuLayout);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint :" + isVisibleToUser);
        if (isVisibleToUser) {
            if (listener != null)
                listener.onShow();
            setTile(findMenuByID("0").getData().menuName);
            currentMenuView = findMenuByID("0");
            currentMenuView.onComeIn();
            showShadow();
        } else {
            dismiss();
        }
    }

    public static MenuLeftFragment newInstance(String params) {
        Bundle bundle = new Bundle();
        bundle.putString("params", params);
        MenuLeftFragment f = new MenuLeftFragment();
        f.setArguments(bundle);
        return f;
    }
    public static MenuLeftFragment newInstance() {
//        Bundle bundle = new Bundle();
//        bundle.putString("params", params);
        MenuLeftFragment f = new MenuLeftFragment();
//        f.setArguments(bundle);
        return f;
    }

//    @Override
//    public void setArguments(Bundle args) {
//        super.setArguments(args);
//
//    }

    public void updateArgument(Bundle args) {
        initParams(args);
    }


    String json;

    private void initParams(Bundle args) {
        //音轨列表
        //字幕列表
        //字幕延迟时间
        //影片信息
        //画面比例
        json = args.getString("params");
        MenuParams params = new Gson().fromJson(json, MenuParams.class);
        IMenu audioSubMenu = findMenuByID("audioSub");
        MenuBean audioSubData = audioSubMenu.getData();
        audioSubData.items.clear();
        for (MenuParams.MTrackInfo mTrackInfo : params.audioList) {
            MenuBean.MenuItemBean itemBean = new MenuBean.MenuItemBean();
            itemBean.itemType = MenuBean.MenuItemBean.ItemType.radiobutton;
            itemBean.itemName = mTrackInfo.trackName;
            itemBean.itemParams = new String[]{Constant.ACTION_AUDIO_CHANGED,
                    mTrackInfo.selected ? "true" : "false", "1"};
            Map<String, String> itemParams = new HashMap<>();
            itemParams.put(Constant.KEY_PARAMS_AUDIO, String.valueOf(mTrackInfo.trackIndex));
            itemBean.itemParamsKV.add(itemParams);
            itemBean.itemID = String.valueOf(mTrackInfo.trackIndex);
            audioSubData.items.add(itemBean);
        }
        audioSubMenu.setData(audioSubData);


        IMenu ratioSubMenu = findMenuByID("ratioSub");
        MenuBean ratioSubData = ratioSubMenu.getData();
        for (MenuBean.MenuItemBean itemBean : ratioSubData.items) {
            Log.d(TAG, itemBean.toString());
            if (itemBean.itemID.equals(params.ratio)) {
                itemBean.itemParams[1] = "true";
            } else {
                itemBean.itemParams[1] = "false";
            }
        }
        ratioSubMenu.setData(ratioSubData);

        IMenu subtitleSubMenu = findMenuByID("subtitleSub");
        MenuBean subtitleSubData = subtitleSubMenu.getData();
        int index=0;
        for (MenuParams.MTrackInfo mTrackInfo : params.internalSubtitleList) {
            MenuBean.MenuItemBean itemBean = new MenuBean.MenuItemBean();
            itemBean.itemType = MenuBean.MenuItemBean.ItemType.radiobutton;
            itemBean.itemName = mTrackInfo.trackName;
            itemBean.itemParams = new String[]{Constant.ACTION_SUBTITLE_CHANGED,
                    mTrackInfo.selected ? "true" : "false", "0"};

            //取消无字幕
            if (mTrackInfo.selected) {
                subtitleSubData.items.get(0).itemParams = new String[]{Constant.ACTION_SUBTITLE_CHANGED, "false", "0"};
            }

            Map<String, String> itemParams = new HashMap<>();
            itemParams.put(Constant.KEY_PARAMS_SUBTITLE, String.valueOf(mTrackInfo.trackIndex));
            itemBean.itemParamsKV.add(itemParams);
            itemBean.itemID = String.valueOf(mTrackInfo.trackIndex);
            subtitleSubData.items.add(index+1, itemBean);
            index++;
        }
        subtitleSubMenu.setData(subtitleSubData);

    }

    private void updateSubtitle() {
        MenuBean menuBean = findMenuByID("subtitleSub").getData();
        //修改bean,加入item后

        findMenuByID("subtitleSub").setData(menuBean);

    }

    private void addView(ViewGroup parent) {
        parent.removeAllViews();
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

    private void showLeftArrow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        getView().findViewById(R.id.left_arrow).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.left_arrow).startAnimation(alphaAnimation);
    }

    private void hideLeftArrow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        getView().findViewById(R.id.left_arrow).startAnimation(alphaAnimation);
    }


    private List<IMenu> getMenuView(List<MenuBean> data) {
        List<IMenu> list = new ArrayList<>();
        for (MenuBean menuBean : data) {
            list.add(createMenuView(menuBean));
        }
        return list;
    }

    private IMenu createMenuView(MenuBean bean) {
        MenuView iMenu = new MenuView(getContext(), 400, 100);
        iMenu.setData(bean);
        iMenu.setOnKeyListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iMenu.setLayoutParams(params);
        return iMenu;
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
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                dismiss();
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public void onChildMenu(String childMenuID, IMenu iMenu) {
        iMenu.onBackground();
        setTile(findMenuByID(childMenuID).getData().menuName);
        currentMenuView = findMenuByID(childMenuID);
        currentMenuView.onComeIn();
        showLeftArrow();
    }

    IMenu currentMenuView;

    @Override
    public void onSuperMenu(IMenu iMenu) {
        if (iMenu.getData().menuID.equals("0")) {
            dismiss();
        } else {
            currentMenuView.onDismiss();
            currentMenuView = findMenuByID(iMenu.getData().superMenuID);
            currentMenuView.onComeback();
            setTile(findMenuByID(iMenu.getData().superMenuID).getData().menuName);
            if (currentMenuView.getData().menuID.equals("0")) {
                hideLeftArrow();
            }
        }
    }

    private void dismiss() {
        currentMenuView.onDismiss();
        hideShadow();
        setTile("");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                    finish();
                if (listener != null) {
                    listener.onClose();
                }
            }
        }, 400);
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
