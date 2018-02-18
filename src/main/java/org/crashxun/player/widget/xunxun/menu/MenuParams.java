package org.crashxun.player.widget.xunxun.menu;

import java.util.List;

/**
 * Created by xunxun on 2018/2/18.
 */

public class MenuParams {
    //音轨列表
    //字幕列表
    //字幕延迟时间
    //影片信息
    //画面比例

    public List<MTrackInfo> audioList;
    public List<MTrackInfo> internalSubtitleList;
    public List<MTrackInfo> externalSubtitleList;
    public int subtitleAdjustTime;
    public String mediaInfo;
    public String ratio;

    public static class MTrackInfo {
        public int trackIndex;
        public String trackName;
        public boolean selected;
    }


}
