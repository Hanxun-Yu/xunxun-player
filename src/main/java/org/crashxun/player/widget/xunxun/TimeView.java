package org.crashxun.player.widget.xunxun;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xunxun on 2018/2/1.
 */

public class TimeView extends RelativeLayout {
    private Context mContext;
    private View mRoot;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TimeView(Context context) {
        super(context);
        init(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    TimeViewHold timeViewHold;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.widget_timeview, null);
        addView(mRoot);

        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                TimeBean obj = (TimeBean) msg.obj;
                if (obj != null) {
                    timeViewHold.setTime(obj);
                }
            }
        };

        timeViewHold = new TimeViewHold();
        timeViewHold.hour = (TextView) findViewById(R.id.hour);
        timeViewHold.minute = (TextView) findViewById(R.id.minute);
        timeViewHold.maohao = (TextView) findViewById(R.id.maohao);
//        timeViewHold.year = (TextView) findViewById(R.id.year);
//        timeViewHold.month = (TextView) findViewById(R.id.month);
//        timeViewHold.day = (TextView) findViewById(R.id.day);

        timeViewHold.maohao.startAnimation(getMaohaoInVisAnim());
        fetchTime();
    }

    public void setTextSize(int pixel) {
        timeViewHold.hour.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixel);
        timeViewHold.maohao.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixel);
        timeViewHold.minute.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixel);

        //冒号marginTop -pixel的1/12
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) timeViewHold.maohao.getLayoutParams();
        params.topMargin = pixel / -12;
        timeViewHold.maohao.setLayoutParams(params);

    }

    public void setTextBold(boolean bold) {
        Paint paint = timeViewHold.hour.getPaint();
        paint.setFakeBoldText(bold);
        paint = timeViewHold.minute.getPaint();
        paint.setFakeBoldText(bold);
        paint = timeViewHold.maohao.getPaint();
        paint.setFakeBoldText(bold);
    }


    Handler handler;
    TimeRunnable timeRunnable;

    Animation getMaohaoVisAnim() {
        Animation maohaoVis = new AlphaAnimation(1f, 0.3f);
        maohaoVis.setDuration(1500);
        maohaoVis.setFillAfter(true);
        maohaoVis.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeViewHold.maohao.startAnimation(getMaohaoInVisAnim());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return maohaoVis;
    }

    Animation getMaohaoInVisAnim() {
        Animation maohaoInvis = new AlphaAnimation(0.3f, 1f);
        maohaoInvis.setDuration(1500);
        maohaoInvis.setFillAfter(true);
        maohaoInvis.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeViewHold.maohao.startAnimation(getMaohaoVisAnim());

                    }
                },2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return maohaoInvis;
    }

    private void fetchTime() {
        executor.execute(timeRunnable = new TimeRunnable());
    }

    class TimeRunnable implements Runnable {
        public void setStop(boolean stop) {
            this.stop = stop;
        }

        boolean stop = false;

        @Override
        public void run() {
            while (!stop) {
                Calendar c = Calendar.getInstance();
                TimeBean tb = new TimeBean(c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.YEAR));
                Message msg = handler.obtainMessage();
                msg.obj = tb;
                msg.sendToTarget();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TimeViewHold {
        public TextView hour, minute, year, month, day, maohao;

        void setTime(TimeBean timeBean) {
            hour.setText(timeBean.hour);
            minute.setText(timeBean.minute);
//            year.setText(timeBean.year);
//            month.setText(timeBean.month);
//            day.setText(timeBean.day);
        }
    }

    private class TimeBean {
        public TimeBean(String day, String hour, String minute, String month, String year) {
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.month = month;
            this.year = year;
        }

        public TimeBean(int day, int hour, int minute, int month, int year) {
            this.day = String.valueOf(day);
            this.hour = String.valueOf(hour);
            this.minute = String.valueOf(minute);
            this.month = String.valueOf(month);
            this.year = String.valueOf(year);

            if (this.minute.length() == 1) {
                this.minute = "0" + this.minute;
            }
            if (this.hour.length() == 1) {
                this.hour = "0" + this.hour;
            }
        }

        public String hour, minute, year, month, day;
    }
}
