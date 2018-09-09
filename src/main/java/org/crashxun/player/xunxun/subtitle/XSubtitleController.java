package org.crashxun.player.xunxun.subtitle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleController;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;
import org.crashxun.player.xunxun.subtitle.ass.AssSubtitleParser;
import org.crashxun.player.xunxun.subtitle.srt.SrtSubtitleParser;
import org.crashxun.player.xunxun.subtitle.srt.SrtSubtitleRender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 */
public class XSubtitleController implements ISubtitleController, ISubtitleParser.OnStateChangedListener {
    final String TAG = getClass().getSimpleName() + "_xunxun";
    Context mContext;
    ISubtitleRender subtitleRender;
    ISubtitleMediaPlayer subtitleMediaPlayer;
    ExecutorService executorService = Executors.newCachedThreadPool();
    SubtitleGenerateRunn subtitleGenerateRunn;
    View anchorView;
    MReceiver receiver;

    public XSubtitleController(Context context) {
        this.mContext = context;
        registReceiver();
    }

    @Override
    public void bindMediaPlayer(ISubtitleMediaPlayer player) {
        Log.d(TAG, "bindMediaPlayer player:" + player);
        this.subtitleMediaPlayer = player;
    }

    @Override
    public void setAnchorView(View view) {
        Log.d(TAG, "setAnchorView view:" + view);
        this.anchorView = view;
    }

    private String curSubtitlePath = null;
    private OnStateChangedListener onStateChangedListener;
    private Handler handler = new Handler(Looper.myLooper());

    @Override
    public void switchSubtitle(String path) {
        Log.d(TAG, "switchSubtitle path:" + path);
        stop();

        if (!TextUtils.isEmpty(path) && !path.equals(curSubtitlePath)) {
            ISubtitleParser subtitleParser = null;
            if (path.toLowerCase().endsWith(".srt")) {
                subtitleParser = new SrtSubtitleParser();
                subtitleRender = new SrtSubtitleRender();
            } else if (path.toLowerCase().endsWith(".ass")) {
                subtitleParser = new AssSubtitleParser();
                subtitleRender = new SrtSubtitleRender();
            }
            subtitleRender.attach(anchorView);

            curSubtitlePath = path;
            subtitleParser.setOnStateChangedListener(this);
            subtitleParser.loadFile(path);
        }
    }

    //+提前 -延后
    private int adjustTimeMilliSec = 0;

    @Override
    public int timeAdjust(int millisecond) {
        adjustTimeMilliSec += millisecond;
        Log.d(TAG, "timeAdjust millisecond:" + millisecond + " ret:" + adjustTimeMilliSec);
        return adjustTimeMilliSec;
    }

    @Override
    public int getTimeAjust() {
        return adjustTimeMilliSec;
    }

    @Override
    public void show() {
        Log.d(TAG, "show");

    }

    @Override
    public void hide() {
        Log.d(TAG, "hide");

    }

    @Override
    public boolean isShowing() {
        Log.d(TAG, "isShowing:" + subtitleRender.isVisible());

        return subtitleRender.isVisible();
    }

    @Override
    public void detach() {
        Log.d(TAG, "detach");
        unRegistReceiver();
        stop();
    }

    @Override
    public void stop() {
        //起线程开始判断时间
        if (subtitleGenerateRunn != null)
            subtitleGenerateRunn.setStop(true);
        if(subtitleRender != null)
            subtitleRender.detach();
    }

    @Override
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.onStateChangedListener = listener;
    }

    private void startRender(String path, List<? extends SubtitleEvent> events) {
        Log.d(TAG, "startRender path:" + path + " size:" + events.size());
        subtitleGenerateRunn = new SubtitleGenerateRunn(path, events);
        executorService.execute(subtitleGenerateRunn);
    }

    private class SubtitleGenerateRunn implements Runnable {
        public void setStop(boolean stop) {
            this.stop = stop;
        }

        private boolean stop = false;
        private List<? extends SubtitleEvent> events;
        private String path;

        private SubtitleGenerateRunn(String path, List<? extends SubtitleEvent> events) {
            this.events = events;
            this.path = path;
        }

        SubtitleEvent lastEvent;
        long lastPlayerPosi;
        final int sleep = 80;
        int startSearchIndex = 0;


        int incremental = 0;
        int frequence = 1000 / sleep;

        @Override
        public void run() {
            Log.d(TAG, "SubtitleGenerateRunn start");
            if (onStateChangedListener != null)
                onStateChangedListener.onStartRender(curSubtitlePath);
            while (!stop) {
                long curTime = subtitleMediaPlayer.getCurrentPostion();


                if (lastPlayerPosi != 0 && Math.abs(curTime - lastPlayerPosi) > 2000) {
                    //如果是没有上一次事件,  或者发生过跳转(播放器间隔大于2秒)
                    //重新搜索当前字幕
                    Log.d(TAG, "SubtitleGenerateRunn startSearchIndex:retset to 0");
                    startSearchIndex = 0;
                }

                List<Integer> foundIndexList = getEventInTime(startSearchIndex, curTime);


                if (!foundIndexList.isEmpty()) {
                    if (incremental % frequence == 0) {
                        incremental = 0;
                        Log.d(TAG, "SubtitleGenerateRunn found:" + foundIndexList.size());
                    }
                    //搜到了
                    for (int i = 0; i < foundIndexList.size(); i++) {
                        if (i == 0) {
                            if(startSearchIndex != foundIndexList.get(i)) {
                                startSearchIndex = foundIndexList.get(i);
                                Log.d(TAG, "SubtitleGenerateRunn startSearchIndex:" + startSearchIndex);
                            }
                        }
                        //抛出显示事件
                        subtitleRender.putSubtitleEvent(subtitleMediaPlayer.getMovieWidth(),
                                subtitleMediaPlayer.getMovieHeight(), events.get(foundIndexList.get(i)));
                    }
                } else {
                    if (incremental % frequence == 0) {
                        incremental = 0;
                        Log.d(TAG, "SubtitleGenerateRunn loop curTime:" + curTime);
                    }

                }
                incremental++;

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lastPlayerPosi = curTime;
            }
            if (onStateChangedListener != null)
                onStateChangedListener.onStopRender(curSubtitlePath);
            Log.d(TAG, "SubtitleGenerateRunn stop");
        }

        private List<Integer> getEventInTimeTmp;

        private List<Integer> getEventInTime(int startIndex, long curTime) {
            if (getEventInTimeTmp == null)
                getEventInTimeTmp = new ArrayList<>();
            else
                getEventInTimeTmp.clear();
            boolean found = false;
            for (int i = startIndex; i < events.size(); i++) {
                if (isEventInTime(events.get(i), curTime)) {
                    getEventInTimeTmp.add(i);
                    found = true;
                } else {
                    if (found) {
                        break;
                    }
                }

            }
            return getEventInTimeTmp;
        }

        private boolean isEventInTime(SubtitleEvent event, long time) {
            boolean ret = false;
            time+=adjustTimeMilliSec;
            if (event.getStartTimeMilliSec() < time && event.getEndTimeMilliSec() > time) {
                ret = true;
            }
            return ret;
        }


    }


    private void registReceiver() {
        if(receiver == null)
            receiver = new MReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_ADJUST_TIME);
        mContext.registerReceiver(receiver,intentFilter);
    }

    private void unRegistReceiver() {
        if(receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class MReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_ADJUST_TIME)) {
                int milliSecond = intent.getIntExtra(KEY_ADJUST_TIME_PARAM,0);
                timeAdjust(milliSecond);
            }
        }
    }


    //-----------------------------subtitle parser------------------------------------
    int lastPercent = -1;
    @Override
    public void onLoading(final String path, final int percent) {
        Log.d(TAG,"onLoading path:"+path+":"+percent+"%");
        if(lastPercent != percent) {
            lastPercent = percent;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (onStateChangedListener != null)
                        onStateChangedListener.onLoading(path, percent);
                    subtitleRender.showLoading(percent);
                }
            });
        }
    }

    @Override
    public void onFailed(String msg) {
        Log.d(TAG,"onFailed msg:"+msg);
        if(msg.indexOf("For input string: \"\"") != -1) {
            msg+=" 可能存在多余换行符号";
        }
        final String finalMsg = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "非法格式 failed:" + finalMsg, Toast.LENGTH_SHORT).show();
                subtitleRender.hideLoading();
            }
        });
    }

    @Override
    public void onFinish(final String path, final List<? extends SubtitleEvent> events) {
        Log.d(TAG,"onFinish path:"+path+" event:"+events.size());
        handler.post(new Runnable() {
            @Override
            public void run() {
                subtitleRender.hideLoading();
                startRender(path, events);
            }
        });
    }
    //-----------------------------subtitle parser------------------------------------

}
