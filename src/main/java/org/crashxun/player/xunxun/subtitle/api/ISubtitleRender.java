package org.crashxun.player.xunxun.subtitle.api;

import android.view.View;

public interface ISubtitleRender {
    void attach(View anchorView);
    void putSubtitleEvent(int width, int height, SubtitleEvent subtitleEvent);
    void detach();
    boolean isVisible();

    /**
     * show loading info
     * @param percejt
     */
    void showLoading(int percejt);

    /**
     * hide loading info
     */
    void hideLoading();
}
