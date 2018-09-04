package org.crashxun.player.xunxun.subtitle.api;

import java.util.List;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 */
public interface ISubtitleParser {
    void loadFile(String path);
    void setOnStateChangedListener(OnStateChangedListener listener);

    interface OnStateChangedListener {
        void onLoading(String path, int percent);
        void onFailed(String msg);
        void onFinish(List<SubtitleEvent> events);
    }
}
