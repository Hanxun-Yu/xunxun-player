package org.crashxun.player.xunxun;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;

import org.crashxun.player.xunxun.api.IPlayController;
import org.crashxun.player.xunxun.api.IPlayControllerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xunxun on 2018/1/30.
 */

public class MiPlayController extends FrameLayout implements IPlayController {
    private final String TAG = "MiPlayController_xunxun";
    private ViewGroup mAnchor;
    private Context mContext;
    private IPlayControllerView iPlayControllerView;
    private MediaController.MediaPlayerControl mediaPlayer;
    private Handler mhandler;
    private boolean respondEvent = false;

    //在缓冲图片出现与自动消失区间内，是否发生过showStatusIcon事件
    private boolean occurShowStatusIconEvent = false;
    private boolean occurBufferringEvent = false;
    private ExecutorService executor = Executors.newCachedThreadPool();

    //剩余显示时间，变0则会消失
    private final int REMAIND_SHOW_TIME_SECOND = 5;
    private int curRemaindShowTimeSecond = REMAIND_SHOW_TIME_SECOND;

    public MiPlayController(@NonNull Context context) {
        super(context);
        mContext = context;

    }

    public MiPlayController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }


    private final int mHANDLER_KEY_ONPLAYSTART = 0x00100;
    private final int mHANDLER_KEY_HIDE_BAR = 0x00200;
    private final int mHANDLER_KEY_SHOW_BAR = 0x00300;

    private void initVar() {
        mhandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case mHANDLER_KEY_ONPLAYSTART:
                        if (mediaPlayer != null) {
                            respondEvent = true;
                            show();
                            sendEmptyMessageDelayed(mHANDLER_KEY_HIDE_BAR,2000);
                            iPlayControllerView.hideStatusIcon();
//                            if (lastBreakPoint != 0) {
//                                mediaPlayer.pause();
//                                mediaPlayer.seekTo(lastBreakPoint);
//                            }
//                            mediaPlayer.start();
                        }
                        break;
                    case mHANDLER_KEY_HIDE_BAR:
                        if (isShowing())
                            hide();
                        break;
                    case mHANDLER_KEY_SHOW_BAR:
                        mhandler.removeMessages(mHANDLER_KEY_HIDE_BAR);
                        if (!isShowing()) {
                            show();
                        }
                        break;
                }
            }

        };

        executor.execute(new CheckPlayStartedRunnable());
    }

    private void freshCurrentProgress() {
        iPlayControllerView.setTitle(title);
        iPlayControllerView.setCurrentProgress(calProgress(mediaPlayer.getCurrentPosition()));
        iPlayControllerView.setCurrentTimeText(getStringDate(mediaPlayer.getCurrentPosition()));
        iPlayControllerView.setTotalTimeText(getStringDate(mediaPlayer.getDuration()));

    }

    @Override
    public boolean handlerEvent(InputEvent event) {
        if (!respondEvent) {
            Log.d("xunxun", "respondEvent:false , player is not work");
            return false;
        }
        Log.d("xunxun", "MiPlayController handlerEvent:");
        boolean ret = false;
        if (event instanceof KeyEvent) {
            switch (((KeyEvent) event).getAction()) {
                case KeyEvent.ACTION_DOWN:
                    ret = handlerKeyDown((KeyEvent) event);
                    break;

                case KeyEvent.ACTION_UP:
                    ret = handlerKeyUp((KeyEvent) event);
                    break;
            }
        } else if (event instanceof MotionEvent) {
            hide();
        }
        return ret;
    }

    String title;
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onPlayerCompletion() {
        Log.d(TAG, "onPlayerCompletion");

    }

    @Override
    public void onPlayerBufferingUpdate(String percent, String networkSpeed) {
//        Log.d(TAG, "onPlayerBufferingUpdate percent:" + percent + " speed:" + networkSpeed);

    }


    @Override
    public void onPlayerBufferingStart() {
        occurBufferringEvent = true;
        showStatusIcon(IPlayControllerView.Status.buffering);
        resetShowStatusIconEvent();
    }

    private void resetShowStatusIconEvent() {
        occurShowStatusIconEvent = false;

    }

    @Override
    public void onPlayerBufferingEnd() {
        Log.d(TAG, "onPlayerBufferingEnd occurShowStatusIconEvent:"+occurShowStatusIconEvent);
        //如果在缓冲开始结束区间内，没有发生过showStatusIcon事件
        //因为如果连续快进中，第一次缓冲结束会去隐藏图标，避免闪一下
        if(!occurShowStatusIconEvent)
            iPlayControllerView.hideStatusIcon();

        occurBufferringEvent = false;
    }

    @Override
    public void onPlayerSeekCompleted() {

        Log.d(TAG, "onPlayerSeekCompleted occurBufferringEvent:"+occurBufferringEvent);
        freshCurrentProgress();

        //如果在缓冲区间内，连续快进抬起，状态会变成快进，但不会再次缓冲，这里要恢复缓冲状态
        if(occurBufferringEvent) {
            showStatusIcon(IPlayControllerView.Status.buffering);
            //因为还在区间内，所以复位图标事件，这样缓冲结束onPlayerBufferingEnd会自动隐藏图标
            resetShowStatusIconEvent();
        }
        mhandler.removeMessages(mHANDLER_KEY_HIDE_BAR);
        mhandler.sendEmptyMessageDelayed(mHANDLER_KEY_HIDE_BAR,1000);
    }

    @Override
    public void onPlayerError(String msg) {
        Log.d(TAG, "onPlayerError msg:" + msg);
    }

    @Override
    public void onPlayerInfo(String info) {
        Log.d(TAG, "onPlayerInfo info:" + info);
    }

    private void showStatusIcon(IPlayControllerView.Status status) {
        //发生过弹出状态的事件
        occurShowStatusIconEvent = true;
        iPlayControllerView.showStatusIcon(status);
    }

    private boolean handlerKeyUp(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (afterFastPlayedTime == 0)
                    mediaPlayer.seekTo(0);
                else
                    mediaPlayer.seekTo((int) afterFastPlayedTime);
                mediaPlayer.start();
//                setStatusByStatus();
                // 重置步进距离
                resetStep();
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mediaPlayer.seekTo((int) afterFastPlayedTime);
                mediaPlayer.start();
//                setStatusByStatus();
                // 重置步进距离
                resetStep();
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mhandler.sendEmptyMessage(mHANDLER_KEY_SHOW_BAR);
                    showStatusIcon(IPlayControllerView.Status.pause);
                } else {
                    mediaPlayer.start();
                    mhandler.sendEmptyMessage(mHANDLER_KEY_HIDE_BAR);
                    iPlayControllerView.hideStatusIcon();
                }
//                    if (isShowing())
//                        hide();
//                    else
//                        show();
                break;
        }

        ret = true;
        return ret;
    }

    private boolean handlerKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mhandler.sendEmptyMessage(mHANDLER_KEY_SHOW_BAR);
                fastPlay(false);
//                iPlayControllerView.setCurrentProgress(progress-=10);
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mhandler.sendEmptyMessage(mHANDLER_KEY_SHOW_BAR);
                fastPlay(true);
                break;

        }

        ret = true;
        return ret;
    }

    @Override
    public void hide() {
        iPlayControllerView.hide();
    }

    @Override
    public boolean isShowing() {
        return iPlayControllerView.isShowing();
    }

    @Override
    public void setAnchorView(View view) {
        Log.d("xunxun", "setAnchorView:" + view);
        mAnchor = (ViewGroup) view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
        mAnchor.addView(this);
        showStatusIcon(IPlayControllerView.Status.buffering);
    }


    protected View makeControllerView() {
        iPlayControllerView = new MiPlayControllerView(mContext, this);
        return iPlayControllerView.getView();
    }


    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        this.mediaPlayer = player;
        initVar();
    }

    @Override
    public void show(int timeout) {

    }

    @Override
    public void show() {
        freshCurrentProgress();
        iPlayControllerView.show();
    }

    @Override
    public void showOnce(View view) {

    }


    private class CheckPlayStartedRunnable implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                if (mediaPlayer.getCurrentPosition() > 0) {
                    mhandler.sendMessage(mhandler.obtainMessage(mHANDLER_KEY_ONPLAYSTART));
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    // 重置步进距离
    private void resetStep() {
        stepTotal = 0;
        stepMultiple = 1;
        afterFastPlayedTime = 0;
    }

    // 根据已步进的总长度，来改变单词步进百分比
    private void calStepDistance() {
        if (stepTotal < STEP_LEVEL_ONE) {
            stepMultiple = 1;
        } else if (stepTotal < STEP_LEVEL_TWO)
            stepMultiple = 2;
        else if (stepTotal < STEP_LEVEL_THREE) {
            stepMultiple = 4;
        } else {
            stepMultiple = 8;
        }
        stepDistance = STEP_LEVEL_PER * stepMultiple;
    }

    long stepTotal = 0;// 记录按下快进了多少，按键放掉就清�?>10�?stepDistance = 2 ,>20�?
    // stepDistance = 4
    final long STEP_LEVEL_ONE = 50000l;
    final long STEP_LEVEL_TWO = 200000l;
    final long STEP_LEVEL_THREE = 600000l;
    final long STEP_LEVEL_PER = 10000;// 步进1次的时间
    long stepDistance = 10000;// 当前步进1次的时间
    int stepMultiple = 0; // 倍数
    long afterFastPlayedTime = 0;

    public void fastPlay(boolean isForword) {
        mediaPlayer.pause();
        stepTotal += stepDistance;
        calStepDistance();
        if (isForword) {
            showStatusIcon(IPlayControllerView.Status.forward);
            // 设置�?��该seek的时�?
            setAfterFastPlayedTime(mediaPlayer.getCurrentPosition() + stepTotal);
        } else {
            showStatusIcon(IPlayControllerView.Status.backward);
            setAfterFastPlayedTime(mediaPlayer.getCurrentPosition() - stepTotal);
        }
        // 设置进度条当前时间变�?
        iPlayControllerView.setCursorTimeText(getStringDate(afterFastPlayedTime));
        // 设置进度条变�?
        iPlayControllerView.setCursorProgress(calProgress(afterFastPlayedTime));
    }

    int calProgress(long currentTime) {
        // 小数
        double progressDecimals = (double) currentTime / (double) (mediaPlayer.getDuration());
        String result = String.format("%.3f", progressDecimals);
        progressDecimals = Double.parseDouble(result);
        return (int) (progressDecimals * 1000);
    }

    void setAfterFastPlayedTime(long time) {
        if (time <= 0) {
            afterFastPlayedTime = 0;//
        } else if (afterFastPlayedTime >= mediaPlayer.getDuration()) {
            afterFastPlayedTime = mediaPlayer.getDuration();
        } else {
            afterFastPlayedTime = time;
        }
    }

    public static String getStringDate(long time) {
        // Date currentTime = new Date(0);
        // SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // String dateString = formatter.format(currentTime);
        long sec = (time / 1000) % 60;
        long min = (time / 1000 / 60) % 60;
        long hour = (time / 1000 / 60 / 60) % 24;

        String secStr = String.valueOf(sec);
        String minStr = String.valueOf(min);
        String hourStr = "";
        if(hour != 0) {
             hourStr = String.valueOf(hour);
            hourStr+=":";
        }
        secStr = secStr.length() < 2 ? "0" + secStr : secStr;
        minStr = minStr.length() < 2 ? "0" + minStr : minStr;
//        hourStr = hourStr.length() < 2 ? "0" + hourStr : hourStr;

        return hourStr + minStr + ":" + secStr;
    }


}
