package org.crashxun.player.xunxun.subtitle.srt;

import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.subtitle.DateUtil;
import org.crashxun.player.xunxun.subtitle.FileRW;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description:
 */
public class SrtSubtitleParser implements ISubtitleParser {
    final String TAG = getClass().getSimpleName() + "_xunxun";
    ExecutorService executorService = Executors.newCachedThreadPool();
    OnStateChangedListener listener;
    LoadRunnable loadRunnable;
    String path;

    @Override
    public void loadFile(String path) {
//        Log.d(TAG,"loadFile path:"+path);
        this.path = path;
//        if(!TextUtils.isEmpty(path)) {
        if (loadRunnable == null || !loadRunnable.isRunning()) {
            executorService.execute(new LoadRunnable());
        }
//        }
    }

    @Override
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    private class LoadRunnable implements Runnable {
        boolean isRunning = false;

        @Override
        public void run() {
            isRunning = true;
            String content = FileRW.fileToString(path, getEncoder(path));
//            Log.d(TAG,content);
//            System.out.println(content);
            convertToEvent(content);
            isRunning = false;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }


    private List<SubtitleEvent> convertToEvent(String allcontent) {
        if (TextUtils.isEmpty(allcontent))
            return null;

        allcontent = allcontent.replaceAll("\r\n", "\n");
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
                if (listener != null)
                    listener.onLoading(path, (int) (i * 100f / strArr.length));
                Log.d(TAG, "convertToEvent i:" + index + " subtitleEventItem:" + subtitleEventItem);
            }

            if (listener != null)
                listener.onFinish(ret);

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onFailed(e.getMessage());
        }
//        Log.d(TAG, "strArr:" + Arrays.toString(strArr));
        return ret;
    }

    private String getEncoder(String path) {
        String ret = null;
        UniversalDetector encDetector = new UniversalDetector(null);
        int l = 0;
        byte[] tmp = new byte[1024];
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            while ((l = fileInputStream.read(tmp)) != -1) {
                if (!encDetector.isDone()) {
                    encDetector.handleData(tmp, 0, l);
                } else {
                    break;
                }
            }
            encDetector.dataEnd();
            ret = encDetector.getDetectedCharset();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getEncoder path:" + path + " ret:" + ret);
        return ret;
    }

    public static void main(String[] args) {
        ISubtitleParser subtitleParser = new SrtSubtitleParser();
//        subtitleParser.loadFile("C:\\Users\\yuhanxun\\Videos\\Game.of.Thrones.S01E01.srt");
        subtitleParser.loadFile("C:\\Users\\yuhanxun\\Videos\\Game.of.Thrones.S01E10.ass");
    }


}
