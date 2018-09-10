package org.crashxun.player.xunxun.subtitle.api;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description: 字幕事件
 */
public class SubtitleEvent {

    private int index;//序号,或行号,某一行可能产生多个事件,样式不同
    private String startTimeText;
    private String endTimeText;
    private long startTimeMilliSec;
    private long endTimeMilliSec;
    private long duringMilliSec;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public long getDuringMilliSec() {
        return duringMilliSec;
    }

    public void setDuringMilliSec(long duringMilliSec) {
        this.duringMilliSec = duringMilliSec;
    }

    public SubtitleEvent clone() {
        SubtitleEvent o = null;
        try {
            o = (SubtitleEvent) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(e.toString());
        }
        return o;
    }

    @Override
    public String toString() {
        return "SubtitleEvent{" +
                "index=" + index +
                ", startTimeText='" + startTimeText + '\'' +
                ", endTimeText='" + endTimeText + '\'' +
                ", startTimeMilliSec=" + startTimeMilliSec +
                ", endTimeMilliSec=" + endTimeMilliSec +
                ", duringMilliSec=" + duringMilliSec +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SubtitleEvent)
            return index == ((SubtitleEvent) obj).index;
        return super.equals(obj);
    }
}
