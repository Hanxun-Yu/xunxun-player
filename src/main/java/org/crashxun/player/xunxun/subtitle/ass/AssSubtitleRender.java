package org.crashxun.player.xunxun.subtitle.ass;

import android.view.ViewGroup;

import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.RenderEvent;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

public class AssSubtitleRender extends AbstractSubtitleRender {


    @Override
    protected void onAttach(ViewGroup renderParentView) {

    }

    @Override
    protected void onRender(RenderEvent renderEvent, ViewGroup renderParentView) {

    }

    @Override
    protected void onRenderEventClean(RenderEvent renderEvent, ViewGroup renderParentView) {

    }

    @Override
    protected RenderEvent getRenderEvent(SubtitleEvent event) {
        return null;
    }

    @Override
    protected void onRenderParentSizeChanged(int w, int height, ViewGroup renderParentView) {

    }
}
