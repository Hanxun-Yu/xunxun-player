package org.crashxun.player.xunxun.subtitle.api;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuhanxun
 * 2018/9/10
 * description:
 */
public abstract class AbstractSubtitleRender implements ISubtitleRender {
    protected final String TAG = getClass().getSimpleName() + "_xunxun";
    private TextView loadingText;
    private ViewGroup renderParentView;//中间层,根据影片控件传进来的宽高变化,居中,渲染view往里添加
    private ViewGroup backgroundView; //最外层撑满的view
    private ViewGroup anchor;
    protected Context context;
    //正在渲染的事件
    private List<RenderEvent> renderingEvents = new LinkedList<>();
    private final int MSG_RENDER_PARENT_SIZE_CHANGED = 0x0001;
    private final int MSG_SUBTITLE_EVENT_NEW = 0x0002;
    private final int MSG_SUBTITLE_EVENT_CLEAN_ONE = 0x0003;
    private final int MSG_SUBTITLE_EVENT_CLEAN_ALL = 0x0004;

    private int lastWidth;
    private int lastHeight;

    private int preventOverlapTime = 50;
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
                    SubtitleEvent event = (SubtitleEvent) msg.obj;
                    RenderEvent renderEvent = getRenderEvent(event);
                    Log.d(TAG, "MSG_SUBTITLE_EVENT_NEW renderEvent:" + renderEvent);

                    //加入消息放在主线程,如果在线程中加入
                    //会导致当主线程执行清除过程中,还正在加入
                    if(!isEventIsRendering(event.getIndex())) {
                        addRenderEvent(renderEvent);
                        //删除旧的(自动清空)消息
                        removeCleanMsg(renderEvent);
                        //添加自动清除消息
                        cleanDelay(renderEvent);

                        //渲染
                        onRender(renderEvent, renderParentView);
                    }

                    break;
                case MSG_SUBTITLE_EVENT_CLEAN_ONE:
                    Log.d(TAG, "MSG_SUBTITLE_EVENT_CLEAN");
                    RenderEvent cleanEvent = (RenderEvent) msg.obj;
                    //清除某一条渲染
                    onRenderEventClean(cleanEvent, renderParentView);
                    //从正在渲染中移出
                    removeRenderEvent(cleanEvent);
                    break;

                case MSG_SUBTITLE_EVENT_CLEAN_ALL:
                    //清除所有渲染view,以及正在渲染集合
                    clearAllRenderEvent();
                    //清除此handler后续所有消息
                    clearHandlerMsg();
                    break;
            }


            return false;
        }

        private void cleanDelay(RenderEvent event) {
            msgTmp = handler.obtainMessage(MSG_SUBTITLE_EVENT_CLEAN_ONE, event);
            int processTime = (int) (System.currentTimeMillis() - event.getEvent().getPutIntoRenderTime());
            Log.d(TAG,"processTime:"+processTime);
            handler.sendMessageDelayed(msgTmp, event.getEvent().getDuringMilliSec()-processTime-preventOverlapTime);
        }

        private void removeCleanMsg(RenderEvent event) {
            handler.removeMessages(MSG_SUBTITLE_EVENT_CLEAN_ONE, event);
        }
    });

    @Override
    public void attach(View anchorView) {
        anchor = (ViewGroup) anchorView;
        context = anchorView.getContext();
        backgroundView = getBackgroundView(anchorView.getContext());
        renderParentView = getRenderParentView(anchorView.getContext());
        loadingText = getLoadingTextView(anchorView.getContext());
        //放置View
        backgroundView.addView(loadingText);
        backgroundView.addView(renderParentView);
        anchor.addView(backgroundView);

        onAttach(renderParentView);
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


        //如果不包含在(正在渲染的事件)
        if (!isEventIsRendering(subtitleEvent.getIndex())) {
            handler.obtainMessage(MSG_SUBTITLE_EVENT_NEW, subtitleEvent).sendToTarget();
        }
    }

    @Override
    public void detach() {
        anchor.removeView(backgroundView);
    }

    @Override
    public boolean isVisible() {
        return backgroundView.getVisibility() == View.VISIBLE
                && renderParentView.getVisibility() == View.VISIBLE;
    }

    private void clearHandlerMsg() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void clearAll() {
        handler.obtainMessage(MSG_SUBTITLE_EVENT_CLEAN_ALL).sendToTarget();
    }

    //-----------------------------sync list-------------------------------------


    private synchronized boolean isEventIsRendering(int id) {
        boolean ret = false;
        for (RenderEvent event : renderingEvents) {
            if (event.getId() == id) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private synchronized void addRenderEvent(RenderEvent event) {
        renderingEvents.add(event);
    }

    private synchronized void removeRenderEvent(RenderEvent event) {
        renderingEvents.remove(event);
    }

    private synchronized void clearAllRenderEvent() {
        RenderEvent item;
        while (renderingEvents.size() > 0) {
            item = renderingEvents.remove(0);
            onRenderEventClean(item, renderParentView);
        }
    }

    //-----------------------------sync list--------------------------------------
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

    private TextView getLoadingTextView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(30);
        textView.setTextColor(context.getResources().getColor(R.color.white_text_color));
        textView.setShadowLayer(2f, 0, 0, Color.parseColor("#ff000000"));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 100;
        textView.setLayoutParams(params);
        return textView;
    }

    private void setRenderParentSize(int w, int h) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) renderParentView.getLayoutParams();
        Log.d(TAG, "setRenderParentSize w:" + w + " h:" + h + " nowW:" + params.width + " nowH:" + params.height);

        if (params.width != w || params.height != h) {
            params.width = w;
            params.height = h;
            renderParentView.setLayoutParams(params);
            onRenderParentSizeChanged(w, h, renderParentView);
        }
    }


    protected  abstract void onAttach(ViewGroup renderParentView);
    //how render
    protected abstract void onRender(RenderEvent renderEvent, ViewGroup renderParentView);

    //how clean render
    protected abstract void onRenderEventClean(RenderEvent renderEvent, ViewGroup renderParentView);

    //how build RenderEvent,create rendView
    protected abstract RenderEvent getRenderEvent(SubtitleEvent event);

    //do something when RenderParentSizeChanged
    protected abstract void onRenderParentSizeChanged(int w, int height, ViewGroup renderParentView);


    @Override
    public void showLoading(int percejt) {
        loadingText.setTextSize(backgroundView.getWidth() / 50);
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText("loading:" + percejt);
    }

    @Override
    public void hideLoading() {
        loadingText.setVisibility(View.INVISIBLE);
    }


}
