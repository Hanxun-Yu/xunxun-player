package org.crashxun.player.xunxun.subtitle.srt;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.RenderEvent;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;
import org.crashxun.player.xunxun.subtitle.ass.AssSubtitleEvent;

public class SrtSubtitleRender2 extends AbstractSubtitleRender {


    private final int textSizeRatioScreenWidth = 50;
    private Typeface typeface;

    //底部垂直线性布局
    //如果有些字幕事件重叠,则会叠起来,从底部插入此布局
    private ViewGroup textLinearParent;

    @Override
    protected void onAttach(ViewGroup renderParentView) {
        textLinearParent = getTextLinearParent();
        renderParentView.addView(textLinearParent);
    }

    private ViewGroup getTextLinearParent() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 50;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }

    @Override
    protected void onRender(RenderEvent renderEvent, ViewGroup renderParentView) {
        TextView textView = (TextView) renderEvent.getRenderView();
        SrtSubtitleEvent event = (SrtSubtitleEvent) renderEvent.getEvent();
//        renderParentView.setBackgroundColor(Color.parseColor("#ffffffff"));
        Log.d(TAG, "renderParentView.getWidth():" + renderParentView.getWidth());
        textView.setTextSize(renderParentView.getLayoutParams().width / textSizeRatioScreenWidth);
        textView.setText(Html.fromHtml(event.getText()));
        Log.d(TAG, "textsize:" + textView.getTextSize() + " text:" + event.getText());
        textLinearParent.addView(renderEvent.getRenderView());
        Animation animation = getShowAnim();
        renderEvent.getRenderView().startAnimation(animation);
    }

    @Override
    protected void onRenderEventClean(RenderEvent renderEvent, ViewGroup renderParentView) {
        renderEvent.getRenderView().clearAnimation();
        textLinearParent.removeView(renderEvent.getRenderView());
    }

    private Animation getShowAnim() {
        Animation animation = new AlphaAnimation(0f,1.0f);
        animation.setDuration(50);
        return animation;
    }

    Animation getHideAnim() {
        Animation animation = new AlphaAnimation(1.0f,0f);
        animation.setDuration(100);
        return animation;
    }



    @Override
    protected RenderEvent getRenderEvent(SubtitleEvent event,ViewGroup parent) {
        if(typeface == null)
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/microyahei14m.ttf");

        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.white_text_color));
        textView.setShadowLayer(7f, 3, 3, Color.parseColor("#ff000000"));
        textView.setTypeface(typeface);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params);
        return new RenderEvent(event, textView);
    }

    @Override
    protected void onRenderParentSizeChanged(int w, int height, ViewGroup renderParentView) {

    }

}
