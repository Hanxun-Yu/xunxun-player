package org.crashxun.player.widget.xunxun.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.internal.widget.ViewUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.crashxun.player.R;
import org.crashxun.player.widget.xunxun.ViewIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xunxun on 2018/2/12.
 */

public class MenuView extends RelativeLayout implements IMenu {
    String TAG = "MenuView_xunxun";
    private MenuBean menuBean;
    private IMenu.OnKeyListener onKeyListener;
    private List<View> menuItemViewList;
    private Handler handler;

    public MenuView(@NonNull Context context) {
        super(context);
        init();
    }

    public MenuView(@NonNull Context context,int itemWidth, int itemHeight) {
        super(context);
        init();
        this.itemWidth  = itemWidth;
        this.itemHeight = itemHeight;
    }

    int itemWidth;
    int itemHeight;
    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
        handler = new Handler(getContext().getMainLooper());
    }

    public void setData(MenuBean menuBean) {
        this.menuBean = menuBean;
        menuItemViewList = new ArrayList<>();
        initLayout(menuBean, itemWidth, itemHeight);
        onInitAnim();
    }

    ScrollView scrollView;
    private void initLayout(final MenuBean menuBean, int itemWidth, int itemHeight) {
        this.setBackgroundDrawable(null);
        removeAllViews();
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

        int lastItemViewID = 0;
        if(menuBean.items != null) {
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
                        if(!menuBean.menuID.equals("0"))
                            btn.hideLeftIcon();

                        if(menuBean.menuID.equals("subtitle_adjust"))
                        break;
                }

                if (item.itemID != null) {
                    if (item.itemID.equals("subtitle"))
                        btn.setLeftIcon(R.drawable.icon_subtitle_normal);
                    else if (item.itemID.equals("audio"))
                        btn.setLeftIcon(R.drawable.icon_audio_track_normal);
                    else if (item.itemID.equals("ratio"))
                        btn.setLeftIcon(R.drawable.icon_aspect_ratio_normal);
                    else if (item.itemID.equals("info"))
                        btn.setLeftIcon(R.drawable.icon_mc_normal);
                }

                RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(itemWidth, itemHeight);
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
                                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                    lastFocus = v;
                                    if (onKeyListener != null) {
                                        onKeyListener.onChildMenu(item.itemParams[0], MenuView.this);
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

                        if(event.getAction() == KeyEvent.ACTION_UP) {
                            if( keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                                    keyCode == KeyEvent.KEYCODE_BACK) {
                                if (onKeyListener != null) {
                                    onKeyListener.onSuperMenu(MenuView.this);
                                }
                                handled = true;
                            }
                        }


                        if (onKeyListener != null && !handled)
                            return onKeyListener.onKey(event, MenuView.this);

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
                                break;
                            case menu:
                                lastFocus = v;
                                if (onKeyListener != null) {
                                    onKeyListener.onChildMenu(item.itemParams[0], MenuView.this);
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
                btn.setFocusable(false);
                menuItemViewList.add(btn);
                relativeLayout.addView(btn, ll);

//            if(i!=titles.size()-1) {
//                ImageView saparate = new ImageView(context);
//                saparate.setImageResource(R.drawable.bottom_sub_menu_saparate);
//                linearLayout.addView(saparate);
//            }
            }
        }


        //        mFocusLayout = new FocusFrameLayout(context);
//        mFocusLayout.setBackgroundColor(Color.parseColor("#BB000000"));
//        mFocusLayout.setPadding(0,0,0,0);
        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, 450);
        scrollViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        scrollView.setClipToPadding(false);
        scrollView.setClipChildren(false);
        scrollView.setPadding(0, 0, 0, 0);
        scrollView.setLayoutParams(scrollViewParams);

        addView(scrollView);
    }

    private void onRadioButtonClick(View v, MenuBean.MenuItemBean item) {
        MenuItemCheckbox menuItemCheckbox = null;
        if (v instanceof MenuItemCheckbox) {
            menuItemCheckbox = (MenuItemCheckbox) v;
        }
        if (!Boolean.parseBoolean(item.itemParams[1])) {
            //找id相同的radiobutton
            for (int i = 0; i < menuBean.items.size(); i++) {
                MenuBean.MenuItemBean itemBean =  menuBean.items.get(i);
                if(itemBean.itemType == MenuBean.MenuItemBean.ItemType.radiobutton) {
                    if(itemBean.itemParams[2].equals(item.itemParams[2])) {
                        itemBean.itemParams[1] = "false";
                        ((MenuItemCheckbox)getItemViewByID(itemBean.itemID)).setParams(itemBean.itemParams);
                    }
                }
            }
            item.itemParams[1] = "true";
            menuItemCheckbox.setParams(item.itemParams);
            sendBroadcast(item);
        }

    }

    private void sendBroadcast(MenuBean.MenuItemBean itemBean) {
        Intent intent = new Intent(itemBean.itemParams[0]);

        boolean checked = Boolean.parseBoolean(itemBean.itemParams[1]);

        Map<String,String> param = checked?itemBean.itemParamsKV.get(0):itemBean.itemParamsKV.get(1);
        for (String key : param.keySet()) {
            intent.putExtra(key,param.get(key));
        }
//        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        getContext().sendBroadcast(intent);
        Log.d(TAG,"sendBroadcast intent:"+intent);

    }

    private MenuItemView getItemViewByID(String id) {
        for(View v:menuItemViewList) {
            if(v.getTag().equals(id))
                return (MenuItemView) v;
        }
        return null;
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onFocus(final View v, final boolean focus) {
        Log.d(TAG, "onFocus v:" + ((MenuItemView) v).getText() + " focus:" + focus);
        int animID = focus ? R.anim.menu_btn_focus : R.anim.menu_btn_unfocus;
        Interpolator interpolator = focus ? new OvershootInterpolator(6) : new DecelerateInterpolator();
        final Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), animID);
        scaleAnimation.setInterpolator(interpolator);
        scaleAnimation.setFillAfter(focus);
        if (focus)
            v.bringToFront();

        v.startAnimation(scaleAnimation);
    }

    @Override
    public void onComeIn() {
        Log.d(TAG, "onComeIn" + " left:" + getLeft() + " top:" + getTop());
//        setVisibility(View.VISIBLE);
        scrollView.scrollTo(0,0);
        hided = false;
        int delay = 0;
        for (final View view : menuItemViewList) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_comein);
            animation.setFillAfter(true);
            animation.setInterpolator(getContext(), android.R.anim.decelerate_interpolator);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (view.getVisibility() != View.VISIBLE)
                        view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (view == menuItemViewList.get(0)) {
//                        view.setFocusable(true);
//                        view.requestFocus();
                    } else if (view == menuItemViewList.get(menuItemViewList.size() - 1)) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //平移动画没有执行完，调用bringToFront会导致父控件重绘所有子控件
                                //其余子控件未结束动画，导致第二项闪烁，必须等所有动画执行完毕
                                recoveryFocusable(null);
                            }
                        }, 100);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
//            view.setFocusable(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.startAnimation(animation);
                }
            }, delay += 30);
        }
    }

    View lastFocus;

    @Override
    public void onBackground() {
        hided = true;

//       Log.d(TAG, "onComeIn" + " left:" + getLeft() + " top:" + getTop());
        for (final View view : menuItemViewList) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_background);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
//            view.clearAnimation();
            view.setFocusable(false);
            view.startAnimation(animation);
        }
    }

    @Override
    public void onComeback() {
        hided = false;
        int delay = 0;
        for (int i = 0; i < menuItemViewList.size(); i++) {
            final View view = menuItemViewList.get(i);
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_comeback);
            animation.setFillAfter(true);
            final int finalI = i;
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
//                    view.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (view == lastFocus) {
                        lastFocus = null;
                        view.setFocusable(true);
                        view.requestFocus();
                        recoveryFocusable(view);
                    }

//                    if(finalI == menuItemViewList.size() -1) {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        },200);
//
//                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.startAnimation(animation);
                }
            }, delay += 30);
//            view.startAnimation(animation);
        }
    }

    private void recoveryFocusable(View except) {
        int index = 0;
        for (View view : menuItemViewList) {

//            if(index == 0 && except==null) {
//                view.setFocusable(true);
//                view.requestFocus();
//                index++;
//            }
            if (view != except) {
                view.setFocusable(true);
            }
        }
    }

    boolean hided = false;

    @Override
    public void onDismiss() {
        lastFocus = null;
        hided = true;
        View focusView = null;
        for (final View view : menuItemViewList) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_menu_dismiss);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //有焦点的view最后清除焦点，避免scrollview滑动到底部
            if(!view.isFocused())
                view.setFocusable(false);
            else
                focusView = view;
            view.startAnimation(animation);
        }
        focusView.setFocusable(false);
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
        for (final View view : menuItemViewList) {
//            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
//            animation.setDuration(20);
//            animation.setFillAfter(true);
////            animation.setAnimationListener(new Animation.AnimationListener() {
////                @Override
////                public void onAnimationStart(Animation animation) {
////                }
////
////                @Override
////                public void onAnimationEnd(Animation animation) {
//////                    view.setVisibility(View.INVISIBLE);
////                }
////
////                @Override
////                public void onAnimationRepeat(Animation animation) {
////
////                }
////            });
////            view.setFocusable(false);
//            view.startAnimation(animation);
            view.setVisibility(View.INVISIBLE);
        }
    }
}
