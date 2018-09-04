package org.crashxun.player.xunxun.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.activity.LoginActivity;
import org.crashxun.player.xunxun.activity.SMBServerAddActivity;
import org.crashxun.player.xunxun.activity.XunxunMainActivity;
import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.common.MenuIDConst;
import org.crashxun.player.xunxun.menu.FileBrowerView;
import org.crashxun.player.xunxun.menu.IMenu;
import org.crashxun.player.xunxun.menu.MenuBean;
import org.crashxun.player.xunxun.samba.SmbFileUtils;
import org.w3c.dom.ls.LSException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

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

    Map<String, SmbFileUtils> smbUtils = new HashMap<>();
    KeyEventDispatcher dispatcher;

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
        registReceiver();
        create();

        if (getActivity() instanceof KeyEventDispatcher)
            dispatcher = (KeyEventDispatcher) getActivity();
    }


    String onFileSelectedAction;

    private void create() {
        //        if (data == null) {
        onFileSelectedAction = getArguments().getString("params");
        MenuBean rootMenu = new MenuBean();
        rootMenu.menuID = MenuIDConst.ID_MENU_ROOT;
        rootMenu.menuName = "root";
//        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String sdcardPath = "/sdcard";
        if (new File(sdcardPath).listFiles() == null) {
            sdcardPath = Environment.getExternalStorageDirectory().getPath();
        }

        MenuBean.MenuItemBean sdcard = new MenuBean.MenuItemBean();
        sdcard.itemIcon = String.valueOf(R.drawable.fold_60_60);
        sdcard.itemName = "sdcard";
        sdcard.itemID = sdcardPath;
        sdcard.itemType = MenuBean.MenuItemBean.ItemType.menu;
        sdcard.itemParams = new String[]{sdcardPath};
        rootMenu.items.add(sdcard);


        //查询已存储smb服务器列表加入菜单
        MenuBean.MenuItemBean smb10_1_1_200 = new MenuBean.MenuItemBean();
        smb10_1_1_200.itemIcon = String.valueOf(R.drawable.fold_60_60);
        smb10_1_1_200.itemName = "10.1.1.200";
        smb10_1_1_200.itemID = "smb://10.1.1.200";
        smb10_1_1_200.itemType = MenuBean.MenuItemBean.ItemType.menu;
        smb10_1_1_200.itemParams = new String[]{"smb://10.1.1.200"};
        rootMenu.items.add(smb10_1_1_200);
        addSmbUtils("10.1.1.200");

        MenuBean.MenuItemBean smb10_1_1_201 = new MenuBean.MenuItemBean();
        smb10_1_1_201.itemIcon = String.valueOf(R.drawable.fold_60_60);
        smb10_1_1_201.itemName = "10.1.1.201";
        smb10_1_1_201.itemID = "smb://10.1.1.201";
        smb10_1_1_201.itemType = MenuBean.MenuItemBean.ItemType.menu;
        smb10_1_1_201.itemParams = new String[]{"smb://10.1.1.201"};
        rootMenu.items.add(smb10_1_1_201);
        addSmbUtils("10.1.1.201");

        MenuBean.MenuItemBean smb192_168_200_201 = new MenuBean.MenuItemBean();
        smb192_168_200_201.itemIcon = String.valueOf(R.drawable.fold_60_60);
        smb192_168_200_201.itemName = "192.168.200.201";
        smb192_168_200_201.itemID = "smb://192.168.200.201";
        smb192_168_200_201.itemType = MenuBean.MenuItemBean.ItemType.menu;
        smb192_168_200_201.itemParams = new String[]{"smb://192.168.200.201"};
        rootMenu.items.add(smb192_168_200_201);
        addSmbUtils("192.168.200.201");

        //

        MenuBean.MenuItemBean sambaItem = new MenuBean.MenuItemBean();
        sambaItem.itemIcon = String.valueOf(R.drawable.fold_60_60);
        sambaItem.itemName = "新连接";
        sambaItem.itemID = "新连接";
        sambaItem.itemType = MenuBean.MenuItemBean.ItemType.activity;
        sambaItem.itemParams = new String[]{Constant.ACTION_SMB_ADD_IP_ACT};
        rootMenu.items.add(sambaItem);


        menus.add(createMenuView(rootMenu));

//        initParams();

//        clearMenuView();
        for (IMenu menuView : menus) {
            addMenuView((FileBrowerView) menuView);
        }
    }

    BroadcastReceiver receiver;

    void registReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Constant.ACTION_LOGIN_ACT:
                        Intent intent1 = new Intent(getContext(), LoginActivity.class);
                        startActivityForResult(intent1, LoginActivity.RQUEST);
                        break;
                    case Constant.ACTION_SMB_ADD_IP_ACT:
                        Intent intent2 = new Intent(getContext(), SMBServerAddActivity.class);
                        startActivityForResult(intent2, SMBServerAddActivity.RQUEST);
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_LOGIN_ACT);
        intentFilter.addAction(Constant.ACTION_SMB_ADD_IP_ACT);
        getContext().registerReceiver(receiver, intentFilter);
    }

    private void addSmbUtils(String ip) {
        SmbFileUtils smbFileUtils = new SmbFileUtils(getContext(), ip);
        smbUtils.put(ip, smbFileUtils);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SMBServerAddActivity.RQUEST:
                    String ip = data.getStringExtra(SMBServerAddActivity.RESULT_DATA_KEY_IP);
                    onUpdateServerList(ip);
                    addSmbUtils(ip);
                    break;
                case LoginActivity.RQUEST:
                    username = data.getStringExtra(LoginActivity.RESULT_DATA_KEY_USERNAME);
                    password = data.getStringExtra(LoginActivity.RESULT_DATA_KEY_PASSWORD);
                    needUserPass = true;
                    onChildMenu(retryMenuID, currentMenuView);
                    break;
            }
        }
    }

    private boolean needUserPass = false;
    private String username = null;
    private String password = null;

    private void onUpdateServerList(String ip) {
        MenuBean.MenuItemBean sambaItem = new MenuBean.MenuItemBean();
        sambaItem.itemIcon = String.valueOf(R.drawable.fold_60_60);
        sambaItem.itemName = ip;
        sambaItem.itemID = "smb://" + ip;
        sambaItem.itemType = MenuBean.MenuItemBean.ItemType.menu;
        sambaItem.itemParams = new String[]{"smb://" + ip};
        MenuBean menuBean = findMenuByID(MenuIDConst.ID_MENU_ROOT).getData();
        List<MenuBean.MenuItemBean> list = menuBean.items;
        list.add(list.size() - 1, sambaItem);
        findMenuByID(MenuIDConst.ID_MENU_ROOT).setData(menuBean);
//        smbFileUtils = new SmbFileUtils(getContext(), "10.1.1.200");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregistReceiver();
    }

    void unregistReceiver() {
        if (receiver != null)
            getContext().unregisterReceiver(receiver);
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
    String retryMenuID = null;

    @Override
    public void onChildMenu(String childMenuID, final IMenu iMenu) {
        Log.d(TAG, "onChildMenu childMenuID:" + childMenuID);
        if (!childMenuID.equals(handlingMenuID)) {
            handlingMenuID = childMenuID;
        } else {
            return;
        }
        responseKeyEvent(false);
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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        responseKeyEvent(true);
                    }
                }, 400);
            }

            @Override
            public void onFailed() {
                responseKeyEvent(true);
            }
        });
    }

    @Override
    public void onSuperMenu(final IMenu iMenu) {
        if (iMenu.getData().superMenuID != null) {
            if (!iMenu.getData().superMenuID.equals(handlingMenuID)) {
                handlingMenuID = iMenu.getData().superMenuID;
            } else {
                return;
            }
        }
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
            //去除子项
            removeMenuView((FileBrowerView) iMenu);
            menus.remove(iMenu);
            Log.d(TAG, "onSuperMenu onCreated:menus.size:" + menus.size());
        }
    }

    public void setSupportVideoSuffix(String[] supportVideoSuffix) {
        this.defaultSupportVideoSuffix = supportVideoSuffix;
    }

    String[] defaultSupportVideoSuffix = new String[]{"mp4", "mkv", "avi", "mov", "rmvb", "mpg", "mpeg", "rm", "wmv"};
    String[] defaultUnSupportVideoPrefix = new String[]{"."};

    boolean checkSupport(String fileName) {
        boolean ret = false;
        for (String suffix : defaultSupportVideoSuffix) {
            if (fileName.toLowerCase().contains(suffix)) {
                ret = true;
                break;
            }
        }

        for (String prefix : defaultUnSupportVideoPrefix) {
            if (fileName.toLowerCase().startsWith(prefix)) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private void createMenuBean(String name, String superID, final String mID, final CreateMenuListener createMenuListener) {
        Log.d(TAG, "createMenuBean name:" + name + " superID:" + superID + " mID:" + mID);
        final MenuBean ret = new MenuBean();
        ret.menuName = name;
        ret.menuID = mID;
        ret.superMenuID = superID;


        if (mID.startsWith("smb://")) {
            showWait();
            SmbFileUtils.SmbListFilesListener listener = new SmbFileUtils.SmbListFilesListener() {
                @Override
                public void onSuccess(SmbFile[] smbFiles) {
                    needUserPass = false;
                    MenuBean.MenuItemBean itemBean;
                    try {
                        for (SmbFile file : smbFiles) {
                            boolean supported;
                            if (file.isDirectory()) {
                                if (file.getName().contains("$"))
                                    supported = false;
                                else
                                    supported = true;
                            } else {
                                supported = checkSupport(file.getName());
                            }


                            if (supported) {
                                itemBean = new MenuBean.MenuItemBean();
                                itemBean.itemIcon = file.isFile() ? String.valueOf(R.drawable.file_60_60) : String.valueOf(R.drawable.fold_60_60);
                                itemBean.itemType = file.isFile() ? MenuBean.MenuItemBean.ItemType.activity : MenuBean.MenuItemBean.ItemType.menu;
                                itemBean.itemID = file.getPath();
                                if (file.getName().lastIndexOf("/") == file.getName().length() - 1)
                                    itemBean.itemName = file.getName().substring(0, file.getName().length() - 1);
                                else
                                    itemBean.itemName = file.getName();
                                itemBean.itemParams = file.isFile() ? new String[]{onFileSelectedAction, file.getPath()} : new String[]{file.getPath()};
                                ret.items.add(itemBean);
                            }
                        }
                    } catch (SmbException e) {
                        e.printStackTrace();
                    }
                    createMenuListener.onCreated(ret);
                    hideWait();
                }

                @Override
                public void onAuthFailed(String path) {
                    retryMenuID = mID;
                    handlingMenuID = null;
                    getContext().sendBroadcast(new Intent(Constant.ACTION_LOGIN_ACT));
                    hideWait();
                    createMenuListener.onFailed();
                }

                @Override
                public void onHostUnconnected(String path) {
                    Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
                    hideWait();
                    createMenuListener.onFailed();
                }
            };
            SmbFileUtils smbFileUtils = null;
            try {
                SmbFile smbFile = new SmbFile(mID);
                smbFileUtils = smbUtils.get(smbFile.getServer());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (needUserPass)
                smbFileUtils.listSmbFilesForAndroid(mID, username, password, listener);
            else
                smbFileUtils.listSmbFilesForAndroid(mID, listener);


        } else {
            File parent = new File(mID);
            File[] childFiles = parent.listFiles();
            MenuBean.MenuItemBean itemBean;
            for (File file : childFiles) {
                if (file.isFile() && !checkSupport(file.getName()))
                    continue;

                itemBean = new MenuBean.MenuItemBean();
                itemBean.itemIcon = file.isFile() ? String.valueOf(R.drawable.file_60_60) : String.valueOf(R.drawable.fold_60_60);
                itemBean.itemType = file.isFile() ? MenuBean.MenuItemBean.ItemType.activity : MenuBean.MenuItemBean.ItemType.menu;
                itemBean.itemID = file.getAbsolutePath();
                itemBean.itemName = file.getName();
                itemBean.itemParams = file.isFile() ? new String[]{onFileSelectedAction, file.getAbsolutePath()} : new String[]{file.getAbsolutePath()};
                ret.items.add(itemBean);
            }
            createMenuListener.onCreated(ret);
        }
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

    private void showWait() {
        getView().findViewById(R.id.waitBar).setVisibility(View.VISIBLE);
    }

    private void hideWait() {
        getView().findViewById(R.id.waitBar).setVisibility(View.INVISIBLE);
    }

    private void responseKeyEvent(boolean flag) {
        if (dispatcher != null)
            dispatcher.setResponse(flag);
    }


    public interface KeyEventDispatcher {
        void setResponse(boolean flag);
    }
}
