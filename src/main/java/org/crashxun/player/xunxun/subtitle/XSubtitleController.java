package org.crashxun.player.xunxun.subtitle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import org.crashxun.player.xunxun.subtitle.api.ISubtitleController;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;
import org.crashxun.player.xunxun.subtitle.srt.SrtSubtitleParser;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 */
public class XSubtitleController implements ISubtitleController,  ISubtitleParser.OnStateChangedListener {

    public XSubtitleController() {

    }

    @Override
    public void bindMediaPlayer(IMediaPlayer player) {

    }

    @Override
    public void setAnchorView(View view) {

    }

    private String curSubtitlePath = null;
    private OnStateChangedListener onStateChangedListener;
    private Handler handler = new Handler(Looper.myLooper());
    @Override
    public void switchSubtitle(String path) {
        if(!TextUtils.isEmpty(path) && !path.equals(curSubtitlePath)) {
            ISubtitleParser subtitleParser = null;
            if(path.toLowerCase().endsWith(".srt")) {
                subtitleParser = new SrtSubtitleParser();
            } else if(path.toLowerCase().endsWith(".ass")){

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
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.onStateChangedListener = listener;
    }


    //-----------------------------subtitle parser------------------------------------
    @Override
    public void onLoading(final String path, final int percent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(onStateChangedListener != null)
                    onStateChangedListener.onLoading(path,percent);
            }
        });
    }

    @Override
    public void onFailed(String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onFinish(List<SubtitleEvent> events) {
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
    //-----------------------------subtitle parser------------------------------------

}
