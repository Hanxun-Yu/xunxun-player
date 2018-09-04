package org.crashxun.player.xunxun.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.ViewIDUtil;
import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.common.MenuIDConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xunxun on 2018/2/12.
 */

public class FileBrowerView extends RelativeLayout implements IMenu {
    String TAG = "MenuView_xunxun";
    private MenuBean menuBean;
    private IMenu.OnKeyListener onKeyListener;
    private List<View> menuItemViewList;
    private Handler handler;

    public FileBrowerView(@NonNull Context context) {
        super(context);
        init();
    }

    public FileBrowerView(@NonNull Context context, int itemWidth, int itemHeight) {
        super(context);
        init();
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    int itemWidth;
    int itemHeight;

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(0,20,0,20);
        handler = new Handler(getContext().getMainLooper());
    }

    public void setData(MenuBean menuBean) {
        this.menuBean = menuBean;
        menuItemViewList = new ArrayList<>();
        initLayout(menuBean, itemWidth, itemHeight);


        if(!hided) {
            if(!menuItemViewList.isEmpty())
                menuItemViewList.get(0).requestFocus();
        } else {
            onInitAnim();
        }
    }

    ScrollView scrollView;

    private void initLayout(final MenuBean menuBean, int itemWidth, int itemHeight) {
        this.setBackgroundDrawable(null);
        removeAllViews();

        if (menuBean.items != null && !menuBean.items.isEmpty()) {
            setFocusable(false);
            int lastItemViewID = 0;
            scrollView = new ScrollView(getContext());
            scrollView.setFocusable(false);
            scrollView.setVerticalScrollBarEnabled(false);
//        sv.setLayoutParams(new ScrollView.LayoutParams(
//                ScrollView.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelOffset(R.dimen.main_sub_menu_scroll_height)));
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            scrollView.addView(relativeLayout);
            ScrollView.LayoutParams llparams = new ScrollView.LayoutParams(
                    ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
            relativeLayout.setLayoutParams(llparams);
            relativeLayout.setPadding(100, 0, 100, 0);
            relativeLayout.setClipToPadding(false);
            relativeLayout.setClipChildren(false);

            for (int i = 0; i < menuBean.items.size(); i++) {
                final MenuBean.MenuItemBean item = menuBean.items.get(i);
                String name = item.itemName;
                MenuItemView btn = null;

                switch (item.itemType) {
                    case checkbox:
                    case radiobutton:
                        btn = new MenuItemCheckbox(getContext());
                        btn.setParams(item.itemParams);
                        break;
                    case menu:
                        btn = new MenuItemMenu(getContext());
                        break;
                    case activity:
                        btn = new MenuItemAct(getContext());
//                        if(!menuBean.menuID.equals("0"))
//                            btn.hideLeftIcon();

                        if (item.itemID.equals("subtitle_adjust"))
                            btn.hideLeftIcon();
                        break;
                }

                if (item.itemID != null) {
                    if (item.itemID.equals(MenuIDConst.ID_ITEM_SUBTITLE))
                        btn.setLeftIcon(R.drawable.icon_subtitle_normal);
                    else if (item.itemID.equals(MenuIDConst.ID_ITEM_AUDIO))
                        btn.setLeftIcon(R.drawable.icon_audio_track_normal);
                    else if (item.itemID.equals(MenuIDConst.ID_ITEM_RATIO))
                        btn.setLeftIcon(R.drawable.icon_aspect_ratio_normal);
                    else if (item.itemID.equals(MenuIDConst.ID_ITEM_INFO))
                        btn.setLeftIcon(R.drawable.icon_mc_normal);
                    else {
                        if (item.itemIcon != null)
                            btn.setLeftIcon(Integer.parseInt(item.itemIcon));
                    }
                }

                LayoutParams ll = new LayoutParams(itemWidth, itemHeight);
                btn.setText(name);
                btn.setTextColor(R.color.menu_color);
//            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.main_bottom_menu_text_size));
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
                btn.setTag(item.itemID);
//            btn.setBackground(context.getResources().getDrawable(R.drawable.bottom_menu_sub_btn_selector));
//            btn.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

                if (i == 0) {
                    btn.setBackgroundResource(R.drawable.menu_btn_top);
                } else if (i == (menuBean.items.size() - 1)) {
                    btn.setBackgroundResource(R.drawable.menu_btn_bottom);
                    ll.topMargin = 2;
                    ll.addRule(RelativeLayout.BELOW, lastItemViewID);
                } else {
                    btn.setBackgroundResource(R.drawable.menu_btn);
                    ll.addRule(RelativeLayout.BELOW, lastItemViewID);
                    ll.topMargin = 2;
                }
                btn.setId(ViewIDUtil.generateViewId());
                lastItemViewID = btn.getId();

                btn.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        boolean handled = false;
                        switch (item.itemType) {
                            case activity:
                                break;
                            case menu:
                                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                    lastFocus = v;
                                    if (onKeyListener != null) {
                                        onKeyListener.onChildMenu(item.itemParams[0], FileBrowerView.this);
                                    }
                                    handled = true;
                                }
                                break;
//                            case checkbox:
//                                item.itemParams[1] = String.valueOf(!Boolean.parseBoolean(item.itemParams[1]));
//                                finalBtn1.setParams(item.itemParams);
//                                break;
//                            case radiobutton:
//                                onRadioButtonClick(v, item);
//                                break;
                        }

                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                                    keyCode == KeyEvent.KEYCODE_BACK) {
                                if (onKeyListener != null) {
                                    onKeyListener.onSuperMenu(FileBrowerView.this);
                                }
                                handled = true;
                            }
                        }


                        if (onKeyListener != null && !handled)
                            return onKeyListener.onKey(event, FileBrowerView.this);

                        return handled;
                    }
                });
                final MenuItemView finalBtn = btn;
                btn.setOnFocusChangeListener(new OnFocusChangeListener() {
                    Drawable unFocusDrawable;

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.d(TAG, "btn:" + finalBtn.getText() + " focus:" + hasFocus);

                        finalBtn.onFocus(hasFocus);
                        if (hasFocus) {
                            unFocusDrawable = v.getBackground();
                            v.setBackgroundResource(R.drawable.menu_btn_focus);
                        } else {
                            v.setBackgroundDrawable(unFocusDrawable);
                        }

                        if (!hided) {
                            onFocus(v, hasFocus);
                        }
                    }
                });

                final MenuItemView finalBtn1 = btn;
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (item.itemType) {
                            case activity:
                                sendBroadcastActivity(item);
                                break;
                            case menu:
                                lastFocus = v;
                                if (onKeyListener != null) {
                                    onKeyListener.onChildMenu(item.itemParams[0], FileBrowerView.this);
                                }
                                break;
                            case checkbox:
                                item.itemParams[1] = String.valueOf(!Boolean.parseBoolean(item.itemParams[1]));
                                finalBtn1.setParams(item.itemParams);
                                break;
                            case radiobutton:
                                onRadioButtonClick(v, item);
                                break;
                        }

                    }
                });
//            btn.setAlpha(0);

//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int index = (int) v.getTag();
//                    Intent intent = new Intent();
//                    intent.setAction(BroadcastActionKey.SMARTHOTEL_ACTION);
//                    intent.putExtra(BroadcastActionKey.SMARTHOTEL_KEY_ACTION_ID, titles.get(index).id);
//                    intent.putExtra(IntentExtraKey.RESOURCE_ROOT_PATH,resourceRootPath);
//                    mContext.sendBroadcast(intent);
//                }
//            });
                btn.setFocusable(true);
                btn.setFocusableInTouchMode(true);
                menuItemViewList.add(btn);
                relativeLayout.addView(btn, ll);

//            if(i!=titles.size()-1) {
//                ImageView saparate = new ImageView(context);
//                saparate.setImageResource(R.drawable.bottom_sub_menu_saparate);
//                linearLayout.addView(saparate);
//            }
            }


            //        mFocusLayout = new FocusFrameLayout(context);
//        mFocusLayout.setBackgroundColor(Color.parseColor("#BB000000"));
//        mFocusLayout.setPadding(0,0,0,0);
            LayoutParams scrollViewParams = new LayoutParams(
                    ScrollView.LayoutParams.MATCH_PARENT, 450);
            scrollViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            scrollView.setClipToPadding(false);
            scrollView.setClipChildren(false);
            scrollView.setPadding(0, 0, 0, 0);
            scrollView.setLayoutParams(scrollViewParams);

            addView(scrollView);
        } else {
            setFocusable(true);
            setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    boolean handled = false;
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                                keyCode == KeyEvent.KEYCODE_BACK) {
                            if (onKeyListener != null) {
                                onKeyListener.onSuperMenu(FileBrowerView.this);
                            }
                            handled = true;
                        }
                    }
                    return handled;
                }
            });

            TextView textView = new TextView(getContext());
            textView.setText("无文件");
            textView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1,-1);
            addView(textView,layoutParams);

        }

    }

    private void onRadioButtonClick(View v, MenuBean.MenuItemBean item) {
        MenuItemCheckbox menuItemCheckbox = null;
        if (v instanceof MenuItemCheckbox) {
            menuItemCheckbox = (MenuItemCheckbox) v;
        }
        if (!Boolean.parseBoolean(item.itemParams[1])) {
            //找id相同的radiobutton
            for (int i = 0; i < menuBean.items.size(); i++) {
                MenuBean.MenuItemBean itemBean = menuBean.items.get(i);
                if (itemBean.itemType == MenuBean.MenuItemBean.ItemType.radiobutton) {
                    if (itemBean.itemParams[2].equals(item.itemParams[2])) {
                        itemBean.itemParams[1] = "false";
                        ((MenuItemCheckbox) getItemViewByID(itemBean.itemID)).setParams(itemBean.itemParams);
                    }
                }
            }
            item.itemParams[1] = "true";
            menuItemCheckbox.setParams(item.itemParams);
            sendBroadcast(item);
        }

    }

    private void sendBroadcastActivity(MenuBean.MenuItemBean itemBean) {
        Intent intent = new Intent(itemBean.itemParams[0]);
        if(itemBean.itemParams.length>1) {
            intent.putExtra(Constant.KEY_PARAMS_ACTIVITY, itemBean.itemParams[1]);
        }
//        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        getContext().sendBroadcast(intent);
        Log.d(TAG, "sendBroadcast intent:" + intent);
    }

    private void sendBroadcast(MenuBean.MenuItemBean itemBean) {
        Intent intent = new Intent(itemBean.itemParams[0]);

        boolean checked = Boolean.parseBoolean(itemBean.itemParams[1]);

        Map<String, String> param = checked ? itemBean.itemParamsKV.get(0) : itemBean.itemParamsKV.get(1);
        for (String key : param.keySet()) {
            intent.putExtra(key, param.get(key));
        }
//        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        getContext().sendBroadcast(intent);
        Log.d(TAG, "sendBroadcast intent:" + intent);

    }

    private MenuItemView getItemViewByID(String id) {
        for (View v : menuItemViewList) {
            if (v.getTag().equals(id))
                return (MenuItemView) v;
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onFocus(final View v, final boolean focus) {
        Log.d(TAG, "onFocus v:" + ((MenuItemView) v).getText() + " focus:" + focus);
        if(v.getTag()==null || !v.getTag().equals(focus)) {
            int animID = focus ? R.anim.menu_btn_focus : R.anim.menu_btn_unfocus;
            Interpolator interpolator = focus ? new OvershootInterpolator(6) : new DecelerateInterpolator();
            final Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), animID);
            scaleAnimation.setInterpolator(interpolator);
            scaleAnimation.setFillAfter(focus);
            if (focus)
                v.bringToFront();

            v.startAnimation(scaleAnimation);
            v.setTag(focus);
        }
    }

    @Override
    public void onComeIn() {
        Log.d(TAG, "onComeIn" + " left:" + getLeft() + " top:" + getTop());
//        setVisibility(View.VISIBLE);
        if(scrollView != null)
            scrollView.scrollTo(0, 0);

        hided = false;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_comein);
        animation.setFillAfter(true);
        animation.setInterpolator(getContext(), android.R.anim.decelerate_interpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (getVisibility() != View.VISIBLE)
                    setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!menuItemViewList.isEmpty())
                    menuItemViewList.get(0).requestFocus();
                else
                    requestFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }

    View lastFocus;

    @Override
    public void onBackground() {
        hided = true;

//       Log.d(TAG, "onComeIn" + " left:" + getLeft() + " top:" + getTop());
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_background);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                    view.setVisibility(View.INVISIBLE);
                if (getVisibility() != View.INVISIBLE)
                    setVisibility(View.INVISIBLE);
                clearFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);

    }

    @Override
    public void onComeback() {
        hided = false;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_comeback);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                    view.setVisibility(View.VISIBLE);
                if (getVisibility() != View.VISIBLE)
                    setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                    if (lastFocus != null) {
                        lastFocus.requestFocus();
                    }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);

    }

//    private void recoveryFocusable(View except) {
//        int index = 0;
//        for (View view : menuItemViewList) {
//
////            if(index == 0 && except==null) {
////                view.setFocusable(true);
////                view.requestFocus();
////                index++;
////            }
//            if (view != except) {
//                view.setFocusable(true);
//                view.setFocusableInTouchMode(true);
//            }
//        }
//    }

    boolean hided = true;

    @Override
    public void onDismiss() {
        lastFocus = null;
        hided = true;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_dismiss);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                    view.setVisibility(View.INVISIBLE);
                if (getVisibility() != View.INVISIBLE)
                    setVisibility(View.INVISIBLE);
                clearFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);

    }



    @Override
    public void setOnKeyListener(IMenu.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    @Override
    public MenuBean getData() {
        return menuBean;
    }

    private void onInitAnim() {
//        for (final View view : menuItemViewList) {
////            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
////            animation.setDuration(20);
////            animation.setFillAfter(true);
//////            animation.setAnimationListener(new Animation.AnimationListener() {
//////                @Override
//////                public void onAnimationStart(Animation animation) {
//////                }
//////
//////                @Override
//////                public void onAnimationEnd(Animation animation) {
////////                    view.setVisibility(View.INVISIBLE);
//////                }
//////
//////                @Override
//////                public void onAnimationRepeat(Animation animation) {
//////
//////                }
//////            });
//////            view.setFocusable(false);
////            view.startAnimation(animation);
//            view.setVisibility(View.INVISIBLE);
//        }
        setVisibility(INVISIBLE);
    }
}
