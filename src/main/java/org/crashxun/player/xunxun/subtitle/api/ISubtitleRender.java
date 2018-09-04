package org.crashxun.player.xunxun.subtitle.api;

import android.view.View;

public interface ISubtitleRender {
    void init(View anchorView);
    void putSubtitleEvent(int width, int height, SubtitleEvent subtitleEvent);
    void clean();
}
