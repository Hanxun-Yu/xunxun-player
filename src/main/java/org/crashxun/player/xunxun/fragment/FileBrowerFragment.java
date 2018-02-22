package org.crashxun.player.xunxun.fragment;

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

import org.crashxun.player.R;
import org.crashxun.player.xunxun.menu.FileBrowerView;
import org.crashxun.player.xunxun.menu.IMenu;
import org.crashxun.player.xunxun.menu.MenuBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xunxun on 2018/2/12.
 */

public class FileBrowerFragment extends Fragment implements IMenu.OnKeyListener {
    RelativeLayout menuLayout;
    public List<IMenu> menus = new ArrayList<>();
    Handler handler = new Handler();

    String TAG = "FileBrowerFragment_xunxun";
    TextView titleText;
    View rootView;
    IMenu currentMenuView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filebrower, container, false);
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
        MenuBean rootMenu = new MenuBean();
        rootMenu.menuID = "0";
        rootMenu.menuName = "root";
//        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String sdcardPath = "/sdcard";

        MenuBean.MenuItemBean sdcard = new MenuBean.MenuItemBean();
        sdcard.itemIcon = String.valueOf(R.drawable.fold_60_60);
        sdcard.itemName = "sdcard";
        sdcard.itemID = sdcardPath;
        sdcard.itemType = MenuBean.MenuItemBean.ItemType.menu;
        sdcard.itemParams = new String[]{sdcardPath};
        rootMenu.items.add(sdcard);


        menus.add(createMenuView(rootMenu));

//        initParams();

//        clearMenuView();
        for (IMenu menuView : menus) {
            addMenuView((FileBrowerView) menuView);
        }
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
//            showShadow();
        } else {
            dismiss();
        }
    }

    public static FileBrowerFragment newInstance(String params) {
        Bundle bundle = new Bundle();
        bundle.putString("params", params);
        FileBrowerFragment f = new FileBrowerFragment();
        f.setArguments(bundle);
        return f;
    }

    public static FileBrowerFragment newInstance() {
        FileBrowerFragment f = new FileBrowerFragment();
        return f;
    }

    private void clearMenuView() {
        Log.d(TAG, "clearMenuView");
        menuLayout.removeAllViews();
    }

    private void addMenuView(FileBrowerView menuView) {
        menuLayout.addView(menuView);
        Log.d(TAG, "addMenuView getChildCount:" + menuLayout.getChildCount());
    }

    private void removeMenuView(FileBrowerView menuView) {
        menuLayout.removeView(menuView);
        Log.d(TAG, "addMenuView getChildCount:" + menuLayout.getChildCount());
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
        FileBrowerView iMenu = new FileBrowerView(getContext(), 400, 80);
        iMenu.setData(bean);
        iMenu.setOnKeyListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iMenu.setLayoutParams(params);
        return iMenu;
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
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//                dismiss();
//                ret = true;
//            }
//        }
        return ret;
    }

    String handlingMenuID = null;
    @Override
    public void onChildMenu(String childMenuID, final IMenu iMenu) {
        if(!childMenuID.equals(handlingMenuID)) {
            handlingMenuID = childMenuID;
        } else {
            return;
        }
        //生成数据bean
        createMenuBean(new File(childMenuID).getName(), iMenu.getData().menuID, childMenuID, new CreateMenuListener() {
            @Override
            public void onCreated(MenuBean menuBean) {
                //生成子菜单
                IMenu menu = createMenuView(menuBean);
                menus.add(menu);
                addMenuView((FileBrowerView) menu);

                setTile(menu.getData().menuName);
                currentMenuView = menu;

                iMenu.onBackground();
                currentMenuView.onComeIn();
                showLeftArrow();
                Log.d(TAG, "onChildMenu onCreated:menus.size:" + menus.size());
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void onSuperMenu(final IMenu iMenu) {
        if(!iMenu.getData().superMenuID.equals(handlingMenuID)) {
            handlingMenuID = iMenu.getData().superMenuID;
        } else {
            return;
        }
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
            //去除子项
            removeMenuView((FileBrowerView) iMenu);
            menus.remove(iMenu);
            Log.d(TAG, "onSuperMenu onCreated:menus.size:" + menus.size());
        }
    }


    private void createMenuBean(String name, String superID, String mID, CreateMenuListener createMenuListener) {
        Log.d(TAG, "createMenuBean name:" + name + " superID:" + superID + " mID:" + mID);
        MenuBean ret = new MenuBean();
        ret.menuName = name;
        ret.menuID = mID;
        ret.superMenuID = superID;


        if (mID.startsWith("smb://")) {

        } else {
            File parent = new File(mID);
            File[] childFiles = parent.listFiles();
            MenuBean.MenuItemBean itemBean;
            for (File file : childFiles) {
                itemBean = new MenuBean.MenuItemBean();
                itemBean.itemIcon = file.isFile() ? String.valueOf(R.drawable.file_60_60) : String.valueOf(R.drawable.fold_60_60);
                itemBean.itemType = file.isFile() ? MenuBean.MenuItemBean.ItemType.activity : MenuBean.MenuItemBean.ItemType.menu;
                itemBean.itemID = file.getAbsolutePath();
                itemBean.itemName = file.getName();
                itemBean.itemParams = file.isFile() ? new String[]{"action"} : new String[]{file.getAbsolutePath()};
                ret.items.add(itemBean);
            }
        }
        createMenuListener.onCreated(ret);
    }

    interface CreateMenuListener {
        void onCreated(MenuBean menuBean);

        void onFailed();
    }


    private void dismiss() {
        currentMenuView.onDismiss();
//        hideShadow();
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
