package org.crashxun.player.xunxun.subtitle.srt;

import android.view.View;

import org.crashxun.player.xunxun.subtitle.api.ISubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

public class SrtSubtitleRender implements ISubtitleRender{
    @Override
    public void init(View anchorView) {
        //放置一个View进去
    }

    @Override
    public void putSubtitleEvent(int width, int height, SubtitleEvent subtitleEvent) {
        //in thread
        //外部会刷重复事件进来,根据事件id,判断当前是否存在,存在则忽略
    }

    @Override
    public void clean() {

    }
}
