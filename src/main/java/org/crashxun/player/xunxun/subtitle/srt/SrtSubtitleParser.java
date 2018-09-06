package org.crashxun.player.xunxun.subtitle.srt;

import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.subtitle.DateUtil;
import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 */
public class SrtSubtitleParser extends AbstractSubtitleParser {

    public List<SubtitleEvent> convertToEvent(String allcontent) {
        Log.d(TAG, "allcontent:" + allcontent);
        if (TextUtils.isEmpty(allcontent)) {
            onParseFailer("subtitle content:null");
            return null;
        }

        //unify various 'enter' character, clear 'utf-16' serial character
        allcontent = allcontent.replaceAll("\r\n", "\n").replaceAll("\ufeff", "");

        List<SubtitleEvent> ret = new ArrayList<>();

        //使用空行分割
        String[] strArr = allcontent.split("\n\n");
        String[] subArr;
        String index;
        String time;
        String content = null;
        SubtitleEvent subtitleEventItem = null;
        try {
            for (int i = 0; i < strArr.length; i++) {
                content = "";
                subArr = strArr[i].split("\n");
                index = subArr[0];
                time = subArr[1];
                if (subArr.length > 3) {
                    for (int j = 0; j < subArr.length - 2; j++) {
                        content += subArr[j + 2];
                        if (j != subArr.length - 2 - 1) {
                            content += "<br>";
                        }
                    }
                } else {
                    content = subArr[2];
                }


                subtitleEventItem = new SubtitleEvent();
                subtitleEventItem.setIndex(Integer.parseInt(index));
                subtitleEventItem.setText(content);

                time = time.replaceAll(" ", "");
                String[] timeArr = time.split("-->");
                subtitleEventItem.setStartTimeText(timeArr[0]);
                subtitleEventItem.setEndTimeText(timeArr[1]);
                subtitleEventItem.setStartTimeMilliSec(DateUtil.convertTimeNoYMD2ms(subtitleEventItem.getStartTimeText()));
                subtitleEventItem.setEndTimeMilliSec(DateUtil.convertTimeNoYMD2ms(subtitleEventItem.getEndTimeText()));
                subtitleEventItem.setDuringMilliSec(subtitleEventItem.getEndTimeMilliSec() - subtitleEventItem.getStartTimeMilliSec());
//            Log.d(TAG, "convertToEvent i:"+index+" t:"+time+" c:"+content);
                ret.add(subtitleEventItem);

                int percent = (int) (i * 100f / strArr.length) - 20;
                onParseLoading(path, percent < 0 ? 0 : percent);
                Log.d(TAG, "convertToEvent i:" + index + " subtitleEventItem:" + subtitleEventItem);

            }
            //排序
            //...
            Collections.sort(ret, new Comparator<SubtitleEvent>() {
                @Override
                public int compare(SubtitleEvent o1, SubtitleEvent o2) {
                    if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() < 0)
                        return -1;
                    else if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() > 0)
                        return 1;
                    else
                        return 0;
                }
            });
            onParseLoading(path, 100);
        } catch (Exception e) {
            e.printStackTrace();
            onParseFailer(e.getMessage());
            ret = null;
        }
//        Log.d(TAG, "strArr:" + Arrays.toString(strArr));
        return ret;
    }

//    public static void main(String[] args) {
//        ISubtitleParser subtitleParser = new SrtSubtitleParser();
////        subtitleParser.loadFile("C:\\Users\\yuhanxun\\Videos\\Game.of.Thrones.S01E01.srt");
//        subtitleParser.loadFile("C:\\Users\\yuhanxun\\Videos\\Game.of.Thrones.S01E10.ass");
//    }


}
