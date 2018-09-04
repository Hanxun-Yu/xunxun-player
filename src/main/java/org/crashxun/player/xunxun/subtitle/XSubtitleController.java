package org.crashxun.player.xunxun.subtitle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleController;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;
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
    Context mContext;
    ISubtitleRender subtitleRender;
    ISubtitleMediaPlayer subtitleMediaPlayer;
    ExecutorService executorService = Executors.newCachedThreadPool();
    SubtitleGenerateRunn subtitleGenerateRunn;

    public XSubtitleController(Context context) {
        this.mContext = context;
        subtitleRender = new SrtSubtitleRender();
    }

    @Override
    public void bindMediaPlayer(ISubtitleMediaPlayer player) {
        this.subtitleMediaPlayer = player;
    }

    @Override
    public void setAnchorView(View view) {
        subtitleRender.init(view);
    }

    private String curSubtitlePath = null;
    private OnStateChangedListener onStateChangedListener;
    private Handler handler = new Handler(Looper.myLooper());

    @Override
    public void switchSubtitle(String path) {
        if (!TextUtils.isEmpty(path) && !path.equals(curSubtitlePath)) {
            ISubtitleParser subtitleParser = null;
            if (path.toLowerCase().endsWith(".srt")) {
                subtitleParser = new SrtSubtitleParser();
            } else if (path.toLowerCase().endsWith(".ass")) {

            }
            curSubtitlePath = path;
            subtitleParser.setOnStateChangedListener(this);
            subtitleParser.loadFile(path);
        }
    }

    @Override
    public int timeAdjust(int millisecond) {
        return 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void detach() {

    }

    @Override
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.onStateChangedListener = listener;
    }

    private void startRender(List<SubtitleEvent> events) {
        //起线程开始判断时间
        if(subtitleGenerateRunn != null)
            subtitleGenerateRunn.setStop(true);

        subtitleGenerateRunn = new SubtitleGenerateRunn(events);
        executorService.execute(subtitleGenerateRunn);
    }

    private class SubtitleGenerateRunn implements Runnable {
        public void setStop(boolean stop) {
            this.stop = stop;
        }

        private boolean stop = false;
        private List<SubtitleEvent> events;

        private SubtitleGenerateRunn(List<SubtitleEvent> events) {
            this.events = events;
        }

        SubtitleEvent lastEvent;
        long lastPlayerPosi;
        final int sleep = 80;
        int startSearchIndex = 0;

        @Override
        public void run() {
            while (!stop) {
                long curTime = subtitleMediaPlayer.getCurrentPostion();


                if (lastEvent == null || (lastPlayerPosi != 0 && Math.abs(curTime - lastPlayerPosi) > 2000)) {
                    //如果是没有上一次事件,  或者发生过跳转(播放器间隔大于2秒)
                    //重新搜索当前字幕
                    startSearchIndex = 0;
                }

                List<Integer> foundIndexList = getEventInTime(startSearchIndex, curTime);


                if (!foundIndexList.isEmpty()) {
                    //搜到了
                    for(int i=0;i<foundIndexList.size();i++) {
                        if(i ==0) {
                            startSearchIndex = foundIndexList.get(i);
                        }
                        //抛出显示事件
                        subtitleRender.putSubtitleEvent(subtitleMediaPlayer.getMovieWidth(),
                                subtitleMediaPlayer.getMovieHeight(),events.get(foundIndexList.get(i)));
                    }
                }


                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lastPlayerPosi = curTime;
            }
        }

        private List<Integer> getEventInTimeTmp;
        private List<Integer> getEventInTime(int startIndex, long curTime) {
            if(getEventInTimeTmp == null)
                getEventInTimeTmp = new ArrayList<>();
            else
                getEventInTimeTmp.clear();
            boolean found = false;
            for (int i = startIndex; i < events.size(); i++) {
                if (isEventInTime(events.get(i), curTime)) {
                    getEventInTimeTmp.add(i);
                    found = true;
                } else {
                    if(found) {
                        break;
                    }
                }

            }
            return getEventInTimeTmp;
        }

        private boolean isEventInTime(SubtitleEvent event, long time) {
            boolean ret = false;
            if (event.getStartTimeMilliSec() < time && event.getEndTimeMilliSec() > time) {
                ret = true;
            }
            return ret;
        }


    }

    //-----------------------------subtitle parser------------------------------------
    @Override
    public void onLoading(final String path, final int percent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onStateChangedListener != null)
                    onStateChangedListener.onLoading(path, percent);
            }
        });
    }

    @Override
    public void onFailed(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "failed:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFinish(final List<SubtitleEvent> events) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startRender(events);
            }
        });
    }
    //-----------------------------subtitle parser------------------------------------

}
