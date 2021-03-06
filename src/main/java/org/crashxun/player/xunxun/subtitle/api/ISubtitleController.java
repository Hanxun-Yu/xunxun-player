package org.crashxun.player.xunxun.subtitle.api;

import android.view.View;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 * 字幕控制器,根据字幕文件类型,选择相应字幕解析器
 */
public interface ISubtitleController {
    String ACTION_ADJUST_TIME = "org.crashxun.player.xunxun.action_adjust_time";
    String KEY_ADJUST_TIME_PARAM = "key_adjust_time_param";

    void bindMediaPlayer(ISubtitleMediaPlayer player);
    void setAnchorView(View view);
    void switchSubtitle(String path);
    int timeAdjust(int millisecond);
    int getTimeAjust();
    void show();
    void hide();
    boolean isShowing();
    void detach();

    void stop();

    void setOnStateChangedListener(OnStateChangedListener listener);

    interface OnStateChangedListener {
        void onLoading(String path, int percent);

        void onStopRender(String path);

        void onStartRender(String path);
    }

    interface ISubtitleMediaPlayer {
        long getCurrentPostion();
        int getMovieWidth();
        int getMovieHeight();
    }
}
