package org.crashxun.player.xunxun.api;

import android.view.View;

/**
 * Created by xunxun on 2018/1/30.
 */

public interface IPlayControllerView {
    View getView();

    void show();

    boolean isShowing();

    void hide();
    void setTitle(String title);

    void setCurrentProgress(int perThousands);

    void setCurrentTimeText(String time);

    void setCursorProgress(int perThousands);

    void setCursorTimeText(String time);

    void setTotalTimeText(String time);

    void setBufferingProgress(int percent);

    void showStatusIcon(Status status);

    void hideStatusIcon();


    enum Status {
        forward,
        backward,
        play,
        pause,
        buffering
    }
}
