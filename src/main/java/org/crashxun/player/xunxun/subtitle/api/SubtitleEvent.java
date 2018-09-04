package org.crashxun.player.xunxun.subtitle.api;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description: 字幕事件
 */
public class SubtitleEvent {
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDuringMilliSec() {
        return duringMilliSec;
    }

    public void setDuringMilliSec(long duringMilliSec) {
        this.duringMilliSec = duringMilliSec;
    }

    private int index;
    private String text;

    public String getStartTimeText() {
        return startTimeText;
    }

    public void setStartTimeText(String startTimeText) {
        this.startTimeText = startTimeText;
    }

    public String getEndTimeText() {
        return endTimeText;
    }

    public void setEndTimeText(String endTimeText) {
        this.endTimeText = endTimeText;
    }

    public long getStartTimeMilliSec() {
        return startTimeMilliSec;
    }

    public void setStartTimeMilliSec(long startTimeMilliSec) {
        this.startTimeMilliSec = startTimeMilliSec;
    }

    public long getEndTimeMilliSec() {
        return endTimeMilliSec;
    }

    public void setEndTimeMilliSec(long endTimeMilliSec) {
        this.endTimeMilliSec = endTimeMilliSec;
    }

    private String startTimeText;
    private String endTimeText;
    private long startTimeMilliSec;
    private long endTimeMilliSec;
    private long duringMilliSec;
    //...样式后续加
    //...动画类型
    //...动画参数


    @Override
    public String toString() {
        return "SubtitleEvent{" +
                "index=" + index +
                ", text='" + text + '\'' +
                ", startTimeText='" + startTimeText + '\'' +
                ", endTimeText='" + endTimeText + '\'' +
                ", startTimeMilliSec=" + startTimeMilliSec +
                ", endTimeMilliSec=" + endTimeMilliSec +
                ", duringMilliSec=" + duringMilliSec +
                '}';
    }
}
