package org.crashxun.player.xunxun.subtitle.srt;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

public class SrtSubtitleRender implements ISubtitleRender {

    private final String TAG = getClass().getSimpleName() + "_xunxun";
    private TextView renderView;//内层渲染view
    private ViewGroup renderParentView;//中间层,根据影片控件传进来的宽高变化,居中
    private ViewGroup backgroundView; //最外层撑满的view
    private ViewGroup anchor;

    private final int MSG_RENDER_PARENT_SIZE_CHANGED = 0x0001;
    private final int MSG_SUBTITLE_EVENT_NEW = 0x0002;
    private final int MSG_SUBTITLE_EVENT_CLEAN = 0x0003;

    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        Message msgTmp = null;

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RENDER_PARENT_SIZE_CHANGED:
                    Log.d(TAG, "MSG_RENDER_PARENT_SIZE_CHANGED w:" + msg.arg1 + " h:" + msg.arg2);
                    setRenderParentSize(msg.arg1, msg.arg2);
                    break;
                case MSG_SUBTITLE_EVENT_NEW:
                    SubtitleEvent subtitleEvent = (SubtitleEvent) msg.obj;
                    Log.d(TAG, "MSG_SUBTITLE_EVENT_NEW event:" + subtitleEvent);

                    //有字幕事件
                    //删除旧的自动清空消息
                    //隐藏旧的
                    removeCleanMsg();
                    cleanRender();
                    renderText(subtitleEvent.getText());
                    cleanDelay(subtitleEvent.getDuringMilliSec());
                    break;
                case MSG_SUBTITLE_EVENT_CLEAN:
                    Log.d(TAG, "MSG_SUBTITLE_EVENT_CLEAN");
                    cleanRender();
                    break;
            }


            return false;
        }

        private void cleanDelay(long millisecond) {
            msgTmp = handler.obtainMessage(MSG_SUBTITLE_EVENT_CLEAN);
            handler.sendMessageDelayed(msgTmp, millisecond);
        }

        private void removeCleanMsg() {
            handler.removeMessages(MSG_SUBTITLE_EVENT_CLEAN);
        }
    });

    private void cleanRender() {
        renderView.setText("");
    }

    private void renderText(String text) {
        renderView.setText(Html.fromHtml(text));
    }

    private void setRenderTextSize(int size) {
        renderView.setTextSize(size);
    }

    @Override
    public void attach(View anchorView) {
        anchor = (ViewGroup) anchorView;

        backgroundView = getBackgroundView(anchorView.getContext());
        renderParentView = getRenderParentView(anchorView.getContext());
        renderView = getRenderView(anchorView.getContext());

        //放置View
        backgroundView.addView(renderParentView);
        renderParentView.addView(renderView);
        anchor.addView(backgroundView);
    }

    @Override
    public void putSubtitleEvent(int width, int height, SubtitleEvent subtitleEvent) {
        //in thread
        //外部会刷重复事件进来,根据事件id,判断当前是否存在,存在则忽略
        if (width != lastWidth || height != lastHeight) {
            //post parent size change
            handler.obtainMessage(MSG_RENDER_PARENT_SIZE_CHANGED, width, height).sendToTarget();
            lastWidth = width;
            lastHeight = height;
        }


        if (LAST_EVENT == null || LAST_EVENT.getIndex() != subtitleEvent.getIndex()) {
            handler.obtainMessage(MSG_SUBTITLE_EVENT_NEW, subtitleEvent).sendToTarget();
            LAST_EVENT = subtitleEvent;
        }


    }

    @Override
    public void detach() {
        anchor.removeView(backgroundView);
    }


    @Override
    public boolean isVisible() {
        return backgroundView.getVisibility() == View.VISIBLE
                && renderView.getVisibility() == View.VISIBLE
                && renderParentView.getVisibility() == View.VISIBLE;

    }

    @Override
    public void showLoading(int percent) {
        renderText("Subtitle Loading:"+percent+"%");
    }

    @Override
    public void hideLoading() {
        renderText("");
    }

    private int lastWidth;
    private int lastHeight;
    private SubtitleEvent LAST_EVENT;

    private void setRenderParentSize(int w, int h) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) renderParentView.getLayoutParams();
        if (params.width != w || params.height != h) {
            params.width = w;
            params.height = h;
            renderParentView.setLayoutParams(params);
            setRenderTextSize(w / 70);
        }
    }

    private RelativeLayout getBackgroundView(Context context) {
        RelativeLayout ret = new RelativeLayout(context);
        ret.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return ret;
    }

    private RelativeLayout getRenderParentView(Context context) {
        RelativeLayout ret = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        ret.setLayoutParams(params);
        return ret;
    }

    private TextView getRenderView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(50);
        textView.setTextColor(context.getResources().getColor(R.color.white_text_color));
        textView.setShadowLayer(2f, 0, 0, Color.parseColor("#ff000000"));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 100;
        textView.setLayoutParams(params);
        return textView;
    }
}
