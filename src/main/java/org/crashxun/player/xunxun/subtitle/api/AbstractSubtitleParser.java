package org.crashxun.player.xunxun.subtitle.api;

import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.subtitle.FileRW;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuhanxun
 * 2018/9/5
 * description:
 */
public abstract class AbstractSubtitleParser implements ISubtitleParser {
    protected final String TAG = getClass().getSimpleName() + "_xunxun";
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private OnStateChangedListener listener;
    private LoadRunnable loadRunnable;
    protected String path;

    @Override
    public void loadFile(String path) {
        Log.d(TAG, "loadFile path:" + path);
        this.path = path;
        if (!TextUtils.isEmpty(path)) {
            if (loadRunnable == null || !loadRunnable.isRunning()) {
                Log.d(TAG, "loadFile start loading:" + path);
                executorService.execute(loadRunnable = new LoadRunnable(path));
            } else {
                Log.d(TAG, "loadFile already loading:" + loadRunnable.getPath());
            }
        }
    }

    @Override
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    private class LoadRunnable implements Runnable {
        private boolean isRunning = false;

        public String getPath() {
            return path;
        }

        private String path;

        private LoadRunnable(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            isRunning = true;
            String content = FileRW.fileToString(path, getEncoder(path));
            List<? extends SubtitleEvent> ret = convertToEvent(content);
            if (ret != null) {
                if (listener != null)
                    listener.onFinish(path, ret);
            }
            isRunning = false;
        }

        public boolean isRunning() {
            return isRunning;
        }
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

    protected abstract List<? extends  SubtitleEvent> convertToEvent(String str);


    protected void onParseFailer(String error) {
        if (listener != null)
            listener.onFailed(error);
    }

    protected void onParseLoading(String path, int percent) {
        if (listener != null)
            listener.onLoading(path, percent);
    }

    protected int string2Int(String str) {
        return (int) string2Float(str);
    }

    protected double string2Double(String str) {
        double ret = 0d;
        if (str != null && !str.equals("")) {
            str = str.replaceAll(" ", "");
            ret = Double.parseDouble(str);
        }
        return ret;
    }

    protected float string2Float(String str) {
        float ret = 0f;
        if (str != null && !str.equals("")) {
            str = str.replaceAll(" ", "");
            ret = Float.parseFloat(str);
        }
        return ret;
    }


    public static void main(String[] args) {
        testTypeConvert(" 1.");
    }

    private static void testTypeConvert(String str) {
        int ret = 0;
        if (str != null && !str.equals("")) {
            str = str.replaceAll(" ", "");
            ret = (int) Double.parseDouble(str);
        }
        System.out.println(ret);

    }

    private static void testListsort() {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(8);
        list.add(2);
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        System.out.println(Arrays.toString(list.toArray()));
    }
}
