package org.crashxun.player.xunxun.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleController;

import java.text.DecimalFormat;

public class SubtitleAdjustTimeActivity extends Activity {
    ImageView leftArrowImg, rightArrowImg;
    TextView timeText;

    public static final String TIME_PARAMS = "time_params";
    DecimalFormat df = new java.text.DecimalFormat("#.0");

    private int adjustTime = 0;
    private final int ADJUST_UNIT_MILLISEC = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitle_adjust_time);
        leftArrowImg = findViewById(R.id.left_arrow_img);
        rightArrowImg = findViewById(R.id.right_arrow_img);
        timeText = findViewById(R.id.time_text);

        adjustTime = getIntent().getIntExtra(TIME_PARAMS, 0);
        setTimeText(adjustTime);
    }

    private void setTimeText(int time) {
        float textTime = time / 1000f;
        timeText.setText(df.format(textTime));
        timeText.setText(String.format("%.1f",textTime));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                notifySubtitleController(false);
                setLeftArrowStatus(false);
                setTimeText(adjustTime -= ADJUST_UNIT_MILLISEC);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                notifySubtitleController(true);
                setRightArrowStatus(false);
                setTimeText(adjustTime += ADJUST_UNIT_MILLISEC);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                setLeftArrowStatus(true);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                setRightArrowStatus(true);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setLeftArrowStatus(boolean press) {
        leftArrowImg.setImageResource(press ? R.drawable.arrow_left_focused : R.drawable.arrow_left_normal);
    }

    private void setRightArrowStatus(boolean press) {
        rightArrowImg.setImageResource(press ? R.drawable.arrow_right_focused : R.drawable.arrow_right_normal);
    }

    private void notifySubtitleController(boolean add) {
        Intent intent = new Intent(ISubtitleController.ACTION_ADJUST_TIME);
        intent.putExtra(ISubtitleController.KEY_ADJUST_TIME_PARAM, add ? ADJUST_UNIT_MILLISEC : -ADJUST_UNIT_MILLISEC);
        sendBroadcast(intent);
    }
}
