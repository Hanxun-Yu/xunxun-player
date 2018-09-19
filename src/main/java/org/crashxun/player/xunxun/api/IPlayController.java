package org.crashxun.player.xunxun.api;

import android.view.InputEvent;

import org.crashxun.player.widget.media.IMediaController;

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
