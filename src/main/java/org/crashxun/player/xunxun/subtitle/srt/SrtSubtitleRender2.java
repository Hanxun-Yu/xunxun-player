package org.crashxun.player.xunxun.subtitle.srt;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.RenderEvent;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

public class SrtSubtitleRender2 extends AbstractSubtitleRender {


    final int textSizeRatioScreenWidth = 80;
    Typeface typeface;

    @Override
    protected void onRender(RenderEvent renderEvent, ViewGroup renderParentView) {
        TextView textView = (TextView) renderEvent.getRenderView();
        SrtSubtitleEvent event = (SrtSubtitleEvent) renderEvent.getEvent();
        renderParentView.setBackgroundColor(Color.parseColor("#ffffffff"));
        Log.d(TAG, "renderParentView.getWidth():" + renderParentView.getWidth());
        textView.setTextSize(renderParentView.getLayoutParams().width / textSizeRatioScreenWidth);
        textView.setText(Html.fromHtml(event.getText()));
        Log.d(TAG, "textsize:" + textView.getTextSize() + " text:" + event.getText());
        renderParentView.addView(renderEvent.getRenderView());
    }

    @Override
    protected void onRenderEventClean(RenderEvent renderEvent, ViewGroup renderParentView) {
        renderParentView.removeView(renderEvent.getRenderView());
    }

    @Override
    protected RenderEvent getRenderEvent(SubtitleEvent event) {
        if(typeface == null)
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/microyahei14m.ttf");

        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.white_text_color));
        textView.setShadowLayer(7f, 3, 3, Color.parseColor("#ff000000"));
        textView.setTypeface(typeface);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 100;
        textView.setLayoutParams(params);
        return new RenderEvent(event, textView);
    }

    @Override
    protected void onRenderParentSizeChanged(int w, int height, ViewGroup renderParentView) {

    }

}
