package org.crashxun.player.xunxun.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.crashxun.player.R;
import org.crashxun.player.activities.VideoActivity;
import org.crashxun.player.xunxun.fragment.FileBrowerFragment;
import org.crashxun.player.xunxun.fragment.MenuLeftFragment;
import org.crashxun.player.xunxun.service.PlayFileService;
import org.crashxun.player.xunxun.svg.AnimatedSvgView;
import org.crashxun.player.xunxun.svg.GAStudioPath2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.crashxun.player.xunxun.common.Constant.ACTION_MAIN_FILEBROWER_FILESELECTED;
import static org.crashxun.player.xunxun.common.Constant.KEY_PARAMS_ACTIVITY;

/**
 * Created by xunxun on 2018/2/22.
 */

public class XunxunMainActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalFocusChangeListener, FileBrowerFragment.KeyEventDispatcher {
    FileBrowerFragment f = null;
    private Handler mHandler = new Handler();
    ImageView bgImg;

    private AnimatedSvgView mAnimatedSvgView;
    private float mInitialLogoOffset;

    String TAG = "XunxunMainActivity_xunxun";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunxun_main);
        bgImg = findViewById(R.id.bg_img);
        bgImg.setVisibility(View.INVISIBLE);
//        bindListener(this);
        f = FileBrowerFragment.newInstance(ACTION_MAIN_FILEBROWER_FILESELECTED);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.brower, f);
        transaction.commit();

        f.setListener(new FileBrowerFragment.MenuEventListener() {
            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onShow() {

            }
        });
        mInitialLogoOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 49,
                getResources().getDisplayMetrics());

        mAnimatedSvgView = (AnimatedSvgView) findViewById(R.id.animated_svg_view);
//        ViewHelper.setTranslationY(mAnimatedSvgView, mInitialLogoOffset);

        mAnimatedSvgView.setGlyphStrings(GAStudioPath2.path);

        // ARGB values for each glyph
        mAnimatedSvgView.setFillPaints(
                new int[] {
                        210
                },
                new int[] {
                        00
                },
                new int[] {
                        180
                },
                new int[] {
                        00
                });

        // �����ߵ���ɫ
        int traceColor = Color.argb(255, 0, 200, 100);
        int[] traceColors = new int[2]; // 4 glyphs
        // ��Ե�ߵ���ɫ
        int residueColor = Color.argb(100, 175, 190, 6);
        int[] residueColors = new int[2]; // 4 glyphs

        // Every glyph will have the same trace/residue
        for (int i = 0; i < traceColors.length; i++) {
            traceColors[i] = traceColor;
            residueColors[i] = residueColor;
        }
        mAnimatedSvgView.setTraceColors(traceColors);
        mAnimatedSvgView.setTraceResidueColors(residueColors);

//        mReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAnimatedSvgView.reset();
////                ViewHelper.setTranslationY(mAnimatedSvgView, mInitialLogoOffset);
////                mSubtitleView.setVisibility(View.INVISIBLE);
//
//                animateLogo();
//            }
//        });

        mAnimatedSvgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (state == AnimatedSvgView.STATE_FINISHED) {
                    TranslateAnimation animation = new TranslateAnimation(0,300,0,0);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.setDuration(1500);
//                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            int left = mAnimatedSvgView.getLeft();
                            int top = mAnimatedSvgView.getTop();
                            int right = mAnimatedSvgView.getRight();
                            int bottom = mAnimatedSvgView.getBottom();
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mAnimatedSvgView.getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params.addRule(RelativeLayout.CENTER_VERTICAL);
                            params.leftMargin = left + 300;
                            mAnimatedSvgView.setLayoutParams(params);
                            mAnimatedSvgView.clearAnimation();
//
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showShadow();
                                    mAnimatedSvgView.startLoop();
                                    f.setUserVisibleHint(true);
                                }
                            },1000);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mAnimatedSvgView.startAnimation(animation);
//                    ViewHelper.setAlpha(mSubtitleView, 0);
//
//                    mSubtitleView.setVisibility(View.VISIBLE);
//
//                    AnimatorSet set = new AnimatorSet();
//                    Interpolator interpolator = new DecelerateInterpolator();
//                    ObjectAnimator a1 = ObjectAnimator.ofFloat(mAnimatedSvgView, "translationY", 0);
//                    ObjectAnimator a2 = ObjectAnimator.ofFloat(mSubtitleView, "alpha", 1);
//                    a1.setInterpolator(interpolator);
//                    set.setDuration(1000).playTogether(a1, a2);
//                    set.start();
//                    mAnimatedSvgView.reset();
//                    animateLogo();

                }
            }
        });
        registReceiver();

        Intent intent = new Intent(this,PlayFileService.class);
        startService(intent);
    }

    private void animateLogo() {
        mAnimatedSvgView.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateLogo();

            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_MENU) {
//
//
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.d(TAG, "--------------onGlobalFocusChanged------------");
        Log.d(TAG, "oldFocus:"+oldFocus+" newFocus:"+newFocus);
    }

    private void showShadow() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.bg_img).startAnimation(alphaAnimation);
    }

//    private void hideShadow() {
//        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//        alphaAnimation.setDuration(1000);
//        alphaAnimation.setFillAfter(true);
//        findViewById(R.id.bg_img).startAnimation(alphaAnimation);
//    }

    BroadcastReceiver fileSelectReceiver;

    void registReceiver() {
        if(fileSelectReceiver == null) {
            fileSelectReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals(ACTION_MAIN_FILEBROWER_FILESELECTED)) {
                       String path = intent.getStringExtra(KEY_PARAMS_ACTIVITY);

                       String name = path;

                       if(path != null && path.startsWith("smb://")) {
                           try {
                               path = URLEncoder.encode(path, "utf-8");
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                           play("http://" + PlayFileService.IP + ":" + PlayFileService.PORT + "/" + path, name);
                       } else {
                           play(path,name);
                       }
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ACTION_MAIN_FILEBROWER_FILESELECTED);
            registerReceiver(fileSelectReceiver,intentFilter);
        }
    }


    private void play(String path,String name) {
        Intent intentPlayer = new Intent(this, VideoActivity.class);
        intentPlayer.putExtra("videoPath",path);
        intentPlayer.putExtra("videoName",name);
        startActivity(intentPlayer);
    }


    boolean responseKeyEvent = true;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(responseKeyEvent)
            return super.dispatchKeyEvent(event);
        else
            return true;
    }

    @Override
    public void setResponse(boolean flag) {
        this.responseKeyEvent = flag;
    }
}
