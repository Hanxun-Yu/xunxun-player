package org.crashxun.player.xunxun.subtitle.ass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yuhanxun
 * 2018/9/11
 * description:
 */
public class AssTextView extends android.support.v7.widget.AppCompatTextView {


    public AssTextView(Context context) {
        this(context, null);
    }

    public AssTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AssTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initBorderPaint();
    }


    private Paint borderPaint;
    private float angle;

    private void initBorderPaint() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(angle != 0) {
            canvas.save();
            canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
            canvas.rotate(-angle, this.getWidth() / 2f, this.getHeight() / 2f);
        }

        super.onDraw(canvas);
    }


    public void setBold(boolean flag) {
        getPaint().setFakeBoldText(flag);
    }

    public void setItalic(boolean flag) {
        if (flag) {
            getPaint().setTextSkewX(-0.5f);
        } else {
            getPaint().setTextSkewX(0f);
        }
    }

    public void setUnderline(boolean flag) {
        getPaint().setUnderlineText(flag);

    }

    public void setStrikeOut(boolean flag) {
        getPaint().setStrikeThruText(flag);
    }

    public void setBorderWidth(int width) {
        borderPaint.setStrokeWidth(width);
        setPadding(width * 2, width * 2, width * 2, width * 2);
    }

    public void setBorderColor(int color) {
        borderPaint.setColor(color);
    }

    public void setBorderShadow(int depth) {
        borderPaint.setShadowLayer(7, depth, depth, Color.parseColor("#ff000000"));
    }


    public void setAngle(float angle) {
        this.angle = angle;
    }

}
