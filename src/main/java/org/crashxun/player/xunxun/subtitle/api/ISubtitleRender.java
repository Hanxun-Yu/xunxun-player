package org.crashxun.player.xunxun.subtitle.api;

import android.view.View;

public interface ISubtitleRender {
    /**
     * ISubtitleRender will add the views associated with ISubtitleRender to this anchorView
     * @param anchorView
     */
    void attach(View anchorView);

    /**
     *  put the subtitleEvent ready to be render
     * @param width MediaPlayer RenderView width
     * @param height MediaPlayer RenderView height
     * @param subtitleEvent subtitleEvent ready to be render
     */
    void putSubtitleEvent(int width, int height, SubtitleEvent subtitleEvent);

    /**
     * remove all views belong subtitleRender
     */
    void detach();

    /**
     * is visible or not
     * @return
     */
    boolean isVisible();

    /**
     * clear screen
     */
    void clearAll();

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
