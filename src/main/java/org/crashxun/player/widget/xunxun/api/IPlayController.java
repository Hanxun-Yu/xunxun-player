package org.crashxun.player.widget.xunxun.api;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.crashxun.player.widget.media.IMediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by xunxun on 2018/1/30.
 */

public interface IPlayController extends IMediaController {
    boolean handlerEvent(InputEvent event);

    void setTitle(String title);
    void onPlayerCompletion();
    void onPlayerBufferingUpdate(String percent, String networkSpeed);
    void onPlayerBufferingStart();
    void onPlayerBufferingEnd();
    void onPlayerSeekCompleted();
    void onPlayerError(String msg);
    void onPlayerInfo(String info);

}
