package org.crashxun.player.xunxun.keyboard;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;


/**
 * Created by yuhanxun on 15/7/18.
 */
public class KeyBoardBtnView extends RelativeLayout {

    public KeyBoardBtnView(Context context) {
        super(context);
        init();
    }

    public KeyBoardBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public KeyBoardBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int focusBorder = 3;


    TextView textView;
    ImageView imageView;
    ImageView  boarderView;
    private void init() {
        setFocusable(true);
        textView = new TextView(getContext());
        textView.setFocusable(false);
        textView.getPaint().setFakeBoldText(true);
        LayoutParams textViewParams = new LayoutParams(-1, -1);
        textViewParams.setMargins(focusBorder, focusBorder, focusBorder, focusBorder);
        textView.setLayoutParams(textViewParams);
        textView.setGravity(Gravity.CENTER);

        imageView = new ImageView(getContext());
        LayoutParams imageParams = new LayoutParams(-1,-1);
        imageParams.setMargins(focusBorder, focusBorder, focusBorder, focusBorder);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setLayoutParams(imageParams);


        boarderView = new ImageView(getContext());
        boarderView.setFocusable(false);
        boarderView.setBackgroundResource(R.drawable.keyboard_btn_border);
        LayoutParams borderViewParams = new LayoutParams(-1, -1);
        boarderView.setLayoutParams(borderViewParams);


        boarderView.setVisibility(INVISIBLE);
        addView(boarderView);
        addView(textView);
        addView(imageView);
    }



    public void setText(String text) {
        textView.setText(text);
    }
    public String getText() {
        return textView.getText().toString();
    }

    public void setImage(int resid) {
        imageView.setImageResource(resid);
    }

    public void setTextSize(int unit, float size) {
        textView.setTextSize(unit,size);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setBackgroundColor(int color) {
        textView.setBackgroundColor(color);
    }
    public void setBackgroundResource(int color) {
        textView.setBackgroundResource(color);
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        if(gainFocus) {
            boarderView.clearAnimation();
            boarderView.setVisibility(VISIBLE);
        } else {
            startUnFocusAnim(boarderView);
//            boarderView.setVisibility(INVISIBLE);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            startConfirmAnim(boarderView);
        }
        return super.onKeyUp(keyCode, event);
    }

    private void startConfirmAnim(final View v) {
        Animation animation = new ScaleAnimation(1f,1.1f,1f,1.1f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(100);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animation2 = new ScaleAnimation(1.1f,1f,1.1f,1f,
                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation2.setDuration(200);
                animation2.setFillAfter(true);
                v.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
    }
    private void startUnFocusAnim(final View v) {
        Animation animation = new AlphaAnimation(1f,0f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator());
//        animation.setAnimationListener(new Animation.AnimationListener() {
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
        v.startAnimation(animation);
    }
}
