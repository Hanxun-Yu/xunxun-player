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
import org.crashxun.player.xunxun.common.MenuIDConst;
import org.crashxun.player.xunxun.menu.IMenu;
import org.crashxun.player.xunxun.menu.MenuBean;
import org.crashxun.player.xunxun.menu.MenuParams;
import org.crashxun.player.xunxun.menu.MenuView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
        rootView.findViewById(R.id.shadow).setVisibility(View.INVISIBLE);
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
            setTile(findMenuByID(MenuIDConst.ID_MENU_ROOT).getData().menuName);
            currentMenuView = findMenuByID(MenuIDConst.ID_MENU_ROOT);
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

    MenuParams params;

    final String SUBTITLE_RADIO_ID = "0";
    final String AUDIO_RADIO_ID = "1";

    int subtitleIndex = 0;//字幕加到第几个

    //如何把字幕添加进来
    //1.菜单列表实时数据保存在哪里,目前是json
    //2.获取实时数据,插入字幕数据
    private void initParams(Bundle args) {

        json = args.getString("params");
        params = new Gson().fromJson(json, MenuParams.class);

        //音轨列表
        IMenu audioSubMenu = findMenuByID(MenuIDConst.ID_MENU_AUDIO_SUB);
        MenuBean audioSubData = audioSubMenu.getData();
        audioSubData.items.clear();
        for (MenuParams.MTrackInfo mTrackInfo : params.audioList) {
            MenuBean.MenuItemBean itemBean = new MenuBean.MenuItemBean.Builder()
                    .setItemType(MenuBean.MenuItemBean.ItemType.radiobutton)
                    .setItemName(mTrackInfo.trackName)
                    .setItemID(String.valueOf(mTrackInfo.trackIndex))
                    .setRadioButtonParam(Constant.ACTION_AUDIO_CHANGED,
                            mTrackInfo.selected, AUDIO_RADIO_ID)
                    .addParamKV(0, Constant.KEY_PARAMS_AUDIO, String.valueOf(mTrackInfo.trackIndex))
                    .build();
            audioSubData.items.add(itemBean);
        }
        audioSubMenu.setData(audioSubData);

        //画面比例
        IMenu ratioSubMenu = findMenuByID(MenuIDConst.ID_MENU_RATIO_SUB);
        MenuBean ratioSubData = ratioSubMenu.getData();
        for (MenuBean.MenuItemBean itemBean : ratioSubData.items) {
            Log.d(TAG, itemBean.toString());
            itemBean.itemParams[1] = String.valueOf(itemBean.itemID.equals(params.ratio));
        }
        ratioSubMenu.setData(ratioSubData);


        //字幕列表
        IMenu subtitleSubMenu = findMenuByID(MenuIDConst.ID_MENU_SUBTITLE_SUB);
        MenuBean subtitleSubData = subtitleSubMenu.getData();
        for (MenuParams.MTrackInfo mTrackInfo : params.internalSubtitleList) {
            if (mTrackInfo.selected) {
                //如果有字幕被选中,去除"无字幕"的选中状态
                subtitleSubData.getItemByID(MenuIDConst.ID_ITEM_NO_SUBTITLE).itemParams
                        = new String[]{Constant.ACTION_SUBTITLE_CHANGED, "false", SUBTITLE_RADIO_ID};
            }
            MenuBean.MenuItemBean itemBean = getSubItem(mTrackInfo.trackName, mTrackInfo.selected,
                    true, String.valueOf(mTrackInfo.trackIndex));

            subtitleSubData.items.add(++subtitleIndex, itemBean);

        }
        subtitleSubMenu.setData(subtitleSubData);

    }

    private MenuBean.MenuItemBean getSubItem(String name, boolean selected,
                                             boolean internal, String id) {
        MenuBean.MenuItemBean itemBean = new MenuBean.MenuItemBean.Builder()
                .setItemType(MenuBean.MenuItemBean.ItemType.radiobutton)
                .setItemName(name)
                .setItemID(id)
                .setRadioButtonParam(Constant.ACTION_SUBTITLE_CHANGED, selected, SUBTITLE_RADIO_ID)
                .addParamKV(0, Constant.KEY_PARAMS_SUBTITLE_TYPE,
                        internal ? Constant.VALUE_PARAMS_SUBTITLE_TYPE_INTERNAL : Constant.VALUE_PARAMS_SUBTITLE_TYPE_EXTERNAL)
                .addParamKV(0, Constant.KEY_PARAMS_SUBTITLE_ID, id)
                .build();
        return itemBean;
    }

    public void addSubtitle(String name, String path) {
        Log.d(TAG,"addSubtitle name:"+name+" path:"+path);
        subtitleIndex++;
        MenuBean menuBean = findMenuByID(MenuIDConst.ID_MENU_SUBTITLE_SUB).getData();
        //修改bean,加入item后
        MenuBean.MenuItemBean itemBean = getSubItem(name+" "+subtitleIndex, false, false, path);
        menuBean.items.add(subtitleIndex, itemBean);

        findMenuByID(MenuIDConst.ID_MENU_SUBTITLE_SUB).updateData(menuBean);

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
        getView().findViewById(R.id.shadow).setVisibility(View.VISIBLE);
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
        if (iMenu.getData().menuID.equals(MenuIDConst.ID_MENU_ROOT)) {
            dismiss();
        } else {
            currentMenuView.onDismiss();
            currentMenuView = findMenuByID(iMenu.getData().superMenuID);
            currentMenuView.onComeback();
            setTile(findMenuByID(iMenu.getData().superMenuID).getData().menuName);
            if (currentMenuView.getData().menuID.equals(MenuIDConst.ID_MENU_ROOT)) {
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
