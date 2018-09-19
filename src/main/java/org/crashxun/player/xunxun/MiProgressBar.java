package org.crashxun.player.xunxun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import org.crashxun.player.R;

public class MiProgressBar extends ProgressBar {

    private String text;
    private Paint mPaint;
    private static final int TEXT_SIZE = 20;
    private Bitmap mBitMap;

    public MiProgressBar(Context context) {
        super(context);
        init();
    }

    public MiProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MiProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initCursorBitmap();
    }

    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCurrentProgresstext(currentProgressText, canvas, initProgressTextPaint());
        drawEndProgresstext(endProgressText, canvas, initProgressTextPaint());
//        drawCursorProgress(cursorBitmap,canvas,initProgressTextPaint());
    }

    Bitmap cursorBitmap;
    private void initCursorBitmap() {
        cursorBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.control_time_box))
                .getBitmap();
    }

    private void drawCursorProgress(Bitmap bitmap, Canvas canvas, Paint paint) {
        Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        Rect destRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,srcRect,destRect,paint);
    }

    private void drawCursorProgressText(String text, Canvas canvas, Paint paint) {

    }

    private void drawCurrentProgresstext(String text, Canvas canvas, Paint paint) {
        Rect textBoundsRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBoundsRect);

        int textX = 0;
        int textY = 0;
        //获取进度条实际长度
        int curProgressWidth = getProgress() * getWidth() / getMax();
        //是否比文字长
        if ((curProgressWidth - 20) > textBoundsRect.width()) {
            //控制文字位置
            textX = curProgressWidth - textBoundsRect.width() - 20;
        } else {
            textX = 20;
        }
        textY = textBoundsRect.height() / 2 + getHeight() / 2;

//        Log.d("xunxun", "textBoundsRect:" + textBoundsRect);
//        Log.d("xunxun", "textX:" + textX + " textY:" + textY);
//        Log.d("xunxun", "padtop:" + getPaddingTop() + " padbottom:" + getPaddingBottom());

//        canvas.drawText(this.text, 20, 20, this.mPaint);
        canvas.drawText(text, textX, textY, paint);
    }

    private void drawEndProgresstext(String text, Canvas canvas, Paint paint) {
        Rect textBoundsRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBoundsRect);

        int textX = 0;
        int textY = 0;
        //获取进度条剩余长度
        int remaindProgressWidth = (getMax() - getProgress()) * getWidth() / getMax();
        //是否比文字长
        textY = textBoundsRect.height() / 2 + getHeight() / 2;
        if ((remaindProgressWidth - 20) > textBoundsRect.width()) {
            //控制文字位置
            textX = getWidth() - textBoundsRect.width() - 20;

            canvas.drawText(text, textX, textY, paint);
        }

//        canvas.drawText(this.text, 20, 20, this.mPaint);
    }

    private String currentProgressText = "00:00:00";
    private String endProgressText = "00:00:00";

    public void setCurrentProgressText(String text) {
        currentProgressText = text;
        invalidate();
    }

    public void setEndProgressText(String text) {
        endProgressText = text;
        invalidate();
    }

    private Paint initProgressTextPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(getHeight() * 3 / 4);
        paint.setAlpha(180);
        return paint;
    }


    public void setText(int resID) {
        String str = getResources().getString(resID);
        this.text = str;
    }

    public void setText(String str) {
        this.text = str;
    }
}

