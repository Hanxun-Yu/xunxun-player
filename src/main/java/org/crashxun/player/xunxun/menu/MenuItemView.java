package org.crashxun.player.xunxun.menu;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.crashxun.player.R;
import org.crashxun.player.widget.MarqueeTextView;

/**
 * Created by xunxun on 2018/2/13.
 */

public abstract class MenuItemView extends LinearLayout {
    public MenuItemView(Context context) {
        super(context);
        initView();
    }

    ImageView leftIcon;
    ImageView rightIcon;
    MarqueeTextView textView;
    LayoutInflater inflater;

    MenuBean.MenuItemBean.ItemType type;


    private void initView() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.menu_item_view, null);
        leftIcon = root.findViewById(R.id.left_icon);
        rightIcon = root.findViewById(R.id.right_icon);
        textView = root.findViewById(R.id.text);
        LinearLayout.LayoutParams ll = new LayoutParams(-1,-1);

        addView(root,ll);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        if(gainFocus) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setMarqueeRepeatLimit(-1);
        } else {
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void setType(MenuBean.MenuItemBean.ItemType type) {
        this.type = type;
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextSize(int unit, int size) {
        textView.setTextSize(unit, size);
    }

    public void setTextColor(int color) {
        textView.setTextColor(getResources().getColor(color));
    }

    public void setLeftIcon(int resID) {
        leftIcon.setImageResource(resID);
    }

    public void setRightIcon(int resID) {
        rightIcon.setImageResource(resID);
    }

    public abstract  void onFocus(boolean hasFocus);

    public abstract void onClick();

    public abstract void setParams(String[] params);

    public void hideLeftIcon() {
        leftIcon.setVisibility(View.GONE);
    }

}
