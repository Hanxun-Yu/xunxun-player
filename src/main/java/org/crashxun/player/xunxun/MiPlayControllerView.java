package org.crashxun.player.xunxun;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.api.IPlayController;
import org.crashxun.player.xunxun.api.IPlayControllerView;

/**
 * Created by xunxun on 2018/1/30.
 */

public class MiPlayControllerView implements IPlayControllerView {
    String TAG = "MiPlayControllerView_xunxun";
    private View mRoot;
    private View bottomPart;
    private View topPart;
    private Context mContext;
    private IPlayController mPlayController;


    private MiProgressBar miProgressBar;
    private ViewGroup cursorView;
    private TextView cursorText;
    private ViewGroup statusView;
    private ImageView statusImg1;
    private ImageView statusImg2;
    private Animation statusAnim;
    private TextView titleText;
    private TimeView timeView;

    private Status currentStatus;

    public MiPlayControllerView(Context context, IPlayController iPlayController) {
        mContext = context;
        mPlayController = iPlayController;
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.mi_playcontrollerview, null);
        mRoot.setOnTouchListener(onRootTouchListener);
        mRoot.setOnKeyListener(onRootKeyListener);

        initChildView();
    }


    private View.OnTouchListener onRootTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mPlayController.handlerEvent(event);
        }
    };

    View.OnKeyListener onRootKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return mPlayController.handlerEvent(event);
        }
    };

    private void initChildView() {
        miProgressBar = mRoot.findViewById(R.id.miProgressBar);
        bottomPart = mRoot.findViewById(R.id.bottomPart);
        topPart = mRoot.findViewById(R.id.topPart);
        titleText = mRoot.findViewById(R.id.titleText);
        cursorView = mRoot.findViewById(R.id.cursorLayout);
        cursorText = mRoot.findViewById(R.id.cursorText);
        timeView = mRoot.findViewById(R.id.timeView);
        timeView.setTextSize(50);
        timeView.setTextBold(true);

        statusView = mRoot.findViewById(R.id.status_parent);
        statusImg1 = statusView.findViewById(R.id.status_img1);
        statusImg2 = statusView.findViewById(R.id.status_img2);

        hideCursorView();
        hideStatusIcon();
        bottomPart.setVisibility(View.INVISIBLE);
        topPart.setVisibility(View.INVISIBLE);
    }

    private void hideCursorView() {
        cursorView.setVisibility(View.INVISIBLE);
    }

    private void showCursorView() {
        cursorView.setVisibility(View.VISIBLE);
    }

    private boolean isShowing = false;

    @Override
    public View getView() {
        return mRoot;
    }

    @Override
    public void show() {
        Log.d("xunxun", "show");
//        mRoot.setVisibility(View.VISIBLE);
//        mRoot.requestFocus();
        isShowing = true;
        bottomPartUp();
        topPartDown();
    }

    @Override
    public boolean isShowing() {
//        return mRoot.getVisibility() == View.VISIBLE;
        return isShowing;
    }

    @Override
    public void hide() {
        Log.d("xunxun", "hide");
//        isShowing = false;
        bottomPartDown();
        topPartUp();
//        mRoot.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setTitle(String title) {
        titleText.setText(title);
    }

    @Override
    public void setCurrentProgress(int perThousands) {
        Log.d("xunxun", "setCurrentProgress perThousands:" + perThousands);
        miProgressBar.setProgress(perThousands);
    }

    @Override
    public void setCurrentTimeText(String time) {
        miProgressBar.setCurrentProgressText(time);
    }

    @Override
    public void setCursorProgress(int perThousands) {
        miProgressBar.setSecondaryProgress(perThousands);
        showCursorView();
        moveCursorView();
    }

    @Override
    public void setCursorTimeText(String time) {
        cursorText.setText(time);
    }

    @Override
    public void setTotalTimeText(String time) {
        miProgressBar.setEndProgressText(time);
    }

    @Override
    public void setBufferingProgress(int percent) {

    }


    @Override
    public void showStatusIcon(Status status) {

        if (currentStatus != status) {
            Log.d(TAG, "showStatusIcon:" + status);
            currentStatus = status;
            statusImg2.clearAnimation();
            statusView.clearAnimation();
            switch (status) {
                case forward:
                    statusImg1.setImageDrawable(null);
                    statusImg2.setImageResource(R.drawable.control_button_forward);
                    break;
                case backward:
                    statusImg1.setImageDrawable(null);
                    statusImg2.setImageResource(R.drawable.control_button_rewind);
                    break;

                case play:
                    break;
                case buffering:
                    statusImg1.setImageResource(R.drawable.icon_loading_bg);
                    statusImg2.setImageResource(R.drawable.icon_loading);
                    statusAnim = AnimationUtils.loadAnimation(mContext, R.anim.load_rotate);
                    LinearInterpolator lir = new LinearInterpolator();
                    statusAnim.setInterpolator(lir);
                    statusImg2.startAnimation(statusAnim);
                    break;
                case pause:
                    statusImg1.setImageDrawable(null);
                    statusImg2.setImageResource(R.drawable.content_mid_stop_focused);
                    break;
            }
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideStatusIcon() {
        Log.d(TAG, "hideStatusIcon");
        currentStatus = null;
        statusView.startAnimation(getStatusHideAnim());
    }

    private void moveCursorView() {
        int cursorLeft = 0;
        int cursorTop = 0;
        int cursorRight = 0;
        int cursorBottom = 0;


        //get secondaryProgress left
        int perThouand = miProgressBar.getSecondaryProgress();
        cursorLeft = miProgressBar.getLeft() + miProgressBar.getWidth() * perThouand / miProgressBar.getMax()
                - cursorView.getWidth() / 2;
        cursorTop = miProgressBar.getBottom() - cursorView.getHeight();
        cursorRight = cursorLeft + cursorView.getWidth();
        cursorBottom = cursorTop + cursorView.getHeight();
        Log.d(TAG,"miProgressBar.getBottom()="+miProgressBar.getBottom()+" cursorView.getHeight()="+cursorView.getHeight());

//        Log.d(TAG,"cursorLeft="+cursorLeft+" cursorTop="+cursorTop+" cursorRight="+cursorRight+" cursorBottom="+cursorBottom);

        //用一个第一阶段就生效的方式
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cursorView.getLayoutParams();
        //导包要导相对布局的包x
        params.leftMargin = cursorLeft;
        params.topMargin = cursorTop;
        cursorView.setLayoutParams(params);
//        cursorView.layout(cursorLeft,cursorTop,cursorRight,cursorBottom);
    }


    Animation bottomPartDownAnim;
    Animation bottomPartUpAnim;
    Animation topPartDownAnim;
    Animation topPartUpAnim;

    public void bottomPartUp() {
//        Log.d(TAG, "bottomPartUp:" + bottomPart.getAnimation());
        bottomPartUpAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_part_to_up);
        bottomPartUpAnim.setFillAfter(true);
        bottomPartUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Log.d(TAG, "onAnimationStart top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
                bottomPart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Log.d(TAG, "onAnimationEnd top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());

//                    bottomPart.setAnimation(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        bottomPart.startAnimation(bottomPartUpAnim);
    }

    public void bottomPartDown() {
//        Log.d(TAG, "bottomPartDown getAnimcation:" + bottomPart.getAnimation());
//        if(bottomPart.getAnimation() == null) {
        bottomPartDownAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_part_to_down);
        bottomPartDownAnim.setFillAfter(true);
        bottomPartDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Log.d(TAG, "onAnimationStart top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Log.d(TAG, "onAnimationEnd top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
//                    bottomPart.setVisibility(View.INVISIBLE);
//                    bottomPart.setAnimation(null);
                hideCursorView();
                isShowing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bottomPart.startAnimation(bottomPartDownAnim);
//        }
    }


    public void topPartUp() {
//        Log.d(TAG, "bottomPartUp:" + bottomPart.getAnimation());
        topPartUpAnim = AnimationUtils.loadAnimation(mContext, R.anim.top_part_to_up);
        topPartUpAnim.setFillAfter(true);
        topPartUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Log.d(TAG, "onAnimationStart top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
                topPart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Log.d(TAG, "onAnimationEnd top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());

//                    bottomPart.setAnimation(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        topPart.startAnimation(topPartUpAnim);
    }

    public void topPartDown() {
//        Log.d(TAG, "bottomPartDown getAnimcation:" + bottomPart.getAnimation());
//        if(bottomPart.getAnimation() == null) {
        topPartDownAnim = AnimationUtils.loadAnimation(mContext, R.anim.top_part_to_down);
        topPartDownAnim.setFillAfter(true);
        topPartDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Log.d(TAG, "onAnimationStart top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Log.d(TAG, "onAnimationEnd top:" + bottomPart.getTop() + " bottom:" + bottomPart.getBottom());
//                    bottomPart.setVisibility(View.INVISIBLE);
//                    bottomPart.setAnimation(null);
//                hideCursorView();
//                isShowing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topPart.startAnimation(topPartDownAnim);
//        }
    }

//    Animation getStatusShowAnim() {
//        Animation maohaoVis = new AlphaAnimation(0f, 1f);
//        maohaoVis.setDuration(500);
//        maohaoVis.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        return maohaoVis;
//    }

    Animation getStatusHideAnim() {
        Animation maohaoInvis = new AlphaAnimation(1f, 0f);
        maohaoInvis.setDuration(500);
        maohaoInvis.setFillAfter(true);
        maohaoInvis.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                statusImg1.setImageDrawable(null);
                statusImg2.setImageDrawable(null);
                statusImg2.clearAnimation();
//                statusView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return maohaoInvis;
    }
}
