package org.crashxun.player.xunxun.subtitle.ass;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleRender;
import org.crashxun.player.xunxun.subtitle.api.RenderEvent;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

import java.util.ArrayList;
import java.util.List;

public class AssSubtitleRender extends AbstractSubtitleRender {


    @Override
    protected void onAttach(ViewGroup renderParentView) {

    }

    @Override
    protected void onRender(RenderEvent renderEvent, ViewGroup renderParentView) {
        AssSubtitleEvent subEvent = (AssSubtitleEvent) renderEvent.getEvent();
//        if (subEvent.getParentAnim() != null) {
//
//        }
        renderParentView.addView(renderEvent.getRenderView());
    }

    private int getSize(int srcBase, int tarBase, float val) {
        int ret = 0;
        ret = (int) (val * tarBase / srcBase);
        Log.d(TAG, "srcBase:" + srcBase + " tarBase:" + tarBase + " val:" + val + " ret:" + ret);
        return ret;
    }

    @Override
    protected void onRenderEventClean(RenderEvent renderEvent, ViewGroup renderParentView) {
        renderParentView.removeView(renderEvent.getRenderView());
    }

    @Override
    protected RenderEvent getRenderEvent(SubtitleEvent event1, ViewGroup renderParentView) {
        AssSubtitleEvent subEvent = (AssSubtitleEvent) event1;

        //根据父尺寸重新计算长度
        int parentW = renderParentView.getLayoutParams().width;
        int parentH = renderParentView.getLayoutParams().height;

        int srcBaseW = subEvent.getBaseScreenWidth();
        int srcBaseH = subEvent.getBaseScreenHeight();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout verticalLinear = new LinearLayout(context);
        verticalLinear.setOrientation(LinearLayout.VERTICAL);
        verticalLinear.setGravity(Gravity.CENTER);

        for (int i = 0; i < subEvent.getTexts().size(); i++) {
            AssSubtitleEvent.Text textItem = subEvent.getTexts().get(i);
//            Log.d(TAG,"textItem.getText():"+textItem.getText());
//            Log.d(TAG,"textItem.getText().trim():"+textItem.getText().trim());

            //如果去除2边空格后,开头或结尾存在\N,则需要换行
            if (textItem.getText().trim().startsWith("\\N") || i == 0) {
                //此条文字再上一条下方
                //需新增一行
                //或者
                //第一次添加,新增一行
//                Log.d(TAG,"textItem startwith addline");
                verticalLinear.addView(getHorizontalLinear());
            }

            //以\N切割出更多子串
            List<TextView> textViewList = getTextView(srcBaseW, srcBaseH, parentW, parentH, textItem);
//            Log.d(TAG,"textViewList.size:"+textViewList.size());

            for (int j = 0; j < textViewList.size(); j++) {
                if (j != 0) {
                    verticalLinear.addView(getHorizontalLinear());
                }
                ((ViewGroup) (verticalLinear.getChildAt(verticalLinear.getChildCount() - 1))).addView(textViewList.get(j));
            }

            if (textItem.getText().trim().endsWith("\\N")) {
//                Log.d(TAG,"textItem endwith addline");
                //尾部换行,下一条在此条下方
                //新增一行给下一条
                verticalLinear.addView(getHorizontalLinear());
            }


//                linearLayout.addView();
        }

        if (subEvent.getPosiX() != 0 || subEvent.getPosiY() != 0) {
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            verticalLinear.measure(widthMeasureSpec, heightMeasureSpec);
            Log.e(TAG, "linearLayout.getMeasuredWidth():" + verticalLinear.getMeasuredWidth());

            params.leftMargin = getSize(srcBaseW, parentW, subEvent.getPosiX()) - verticalLinear.getMeasuredWidth() / 2;
            params.topMargin = getSize(srcBaseH, parentH, subEvent.getPosiY()) - verticalLinear.getMeasuredHeight() / 2;
        } else {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = 50;
        }
        verticalLinear.setLayoutParams(params);
        Log.e(TAG, "left:" + params.leftMargin + " top:" + params.topMargin + " bottom:" + params.bottomMargin);

        return new RenderEvent(subEvent, verticalLinear);
    }

    private LinearLayout getHorizontalLinear() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * 进入此方法的文字使用同一种样式,中间还可能存在换行符
     *
     * @param srcBaseW
     * @param srcBaseH
     * @param tarBaseW
     * @param tarBaseH
     * @param textEvent
     * @return 收尾换行符, 在外部已经处理了,
     * 内部根据换行符分割成多个元素
     * 返回的list的第一个元素,还是使用之前的最后一行,剩下的每多一个新增一行
     */
    private List<TextView> getTextView(int srcBaseW, int srcBaseH, int tarBaseW,
                                       int tarBaseH, AssSubtitleEvent.Text textEvent) {
        String[] textArr = textEvent.getText().split("\\\\N");
        List<TextView> retList = new ArrayList<>();
        for (String item : textArr) {
            if (item.equals(""))
                continue;
            AssTextView ret = new AssTextView(context);
            if (textEvent.getTextStyle().isBold()) {
                ret.setBold(true);
            }
            if (textEvent.getTextStyle().isItalic()) {
                ret.setItalic(true);
            }
            if (textEvent.getTextStyle().isStrikeOut()) {
                ret.setStrikeOut(true);
            }
            if (textEvent.getTextStyle().isUnderline()) {
                ret.setUnderline(true);
            }
            //主颜色,边框颜色
//        Log.d(TAG,"color:"+textEvent.getTextStyle().getPrimaryColor());
            ret.setTextColor(Color.parseColor(textEvent.getTextStyle().getPrimaryColor()));
            ret.setShadowLayer(7, 3, 3, Color.parseColor(textEvent.getTextStyle().getShadowColor()));

            //角度
            ret.setAngle(textEvent.getTextStyle().getAngle());

            ret.setText(item);

            ret.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(srcBaseH, tarBaseH, textEvent.getTextStyle().getFontSize()));

            Log.d(TAG, "size:" + ret.getTextSize()
                    + " color:" + ret.getCurrentTextColor() + " " + textEvent.getTextStyle().getPrimaryColor()
                    + " text:" + ret.getText());
            //边框,先不处理,搞不清是一行一个边框,还是一串文字一个边框
//        ret.setBorderColor(Color.parseColor("#"+textEvent.getTextStyle().getBorderColor()));

//        if(textEvent.get)
//        private BorderStyle borderStyle;
//        private int borderWidth;
//        private int borderShadowWidth;
            retList.add(ret);
        }
        return retList;
    }

    @Override
    protected void onRenderParentSizeChanged(int w, int height, ViewGroup renderParentView) {

    }
}
