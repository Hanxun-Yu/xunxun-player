package org.crashxun.player.xunxun.subtitle.srt;

import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

public class SrtSubtitleEvent extends SubtitleEvent{

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SrtSubtitleEvent{" +
                "text='" + text + '\'' +
                '}';
    }
}
