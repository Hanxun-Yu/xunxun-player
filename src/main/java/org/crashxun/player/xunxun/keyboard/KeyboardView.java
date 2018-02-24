package org.crashxun.player.xunxun.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.crashxun.player.R;


/**
 * Created by yuhanxun on 15/7/18.
 */
public class KeyBoardView extends LinearLayout implements View.OnClickListener {
    private final static String mKeys_1[] = new String[]{
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
            "a", "s", "d", "f", "g", "h", "j", "k", "l", "'",
            "z", "x", "c", "v", "b", "n", "m", ",", ".", "?"};

    private final static String mKeys_2[] = new String[]{
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "\"",
            "Z", "X", "C", "V", "B", "N", "M", "-", "_", "/"};


    private final static String clearKey = "清空";

    private final static int _26Letter_number_parent_ID = 0x0eee;

    public KeyBoardView(Context context) {
        super(context);
        initAttribute();
    }

    public KeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute();
    }

    public KeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute();
    }

    public void initAttribute() {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyBoardView);
//        _columnNum = a.getInteger(R.styleable.KeyBoardView_columnNum, 0);
//        _columnSpace = a.getDimensionPixelOffset(R.styleable.KeyBoardView_columnSpace, 0);
//        _rowSpace = a.getDimensionPixelOffset(R.styleable.KeyBoardView_rowSpace, 0);
//
//        _width = getResources().getDimensionPixelOffset(R.dimen.search_keyboard_width);
//        _height = this.getMeasuredHeight();


        //获取屏幕宽高
        int[] screenWH = getScreenWH();
        mWidth = screenWH[0] * 2 / 5;
        mHeight = screenWH[1] * 2 / 5;
        mSmallBtnWidth = mWidth / 11;
        mSmallBtnHeight = mHeight / 9;
        mBtnSpace = 2;

        int mPadding = 20;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mWidth, mHeight);
        setLayoutParams(params);
        setBackgroundResource(R.drawable.keyboard_bg);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(mPadding, mPadding, mPadding, mPadding);

        initKeyBoard();
    }

    int mWidth;
    int mHeight;
    int mSmallBtnWidth;
    int mSmallBtnHeight;
    int mBtnSpace;

    public void setInitText(String initText) {
        editText.setText(initText);
    }

    private int[] getScreenWH() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        int mScreenHeight = dm.heightPixels;
        return new int[]{mScreenWidth, mScreenHeight};
    }


    EditText editText;
    KeyBoardBtnView closeBtn;
    KeyBoardBtnView confirmBtn;
    View firstFocus = null;

    private LinearLayout getEditClosePart() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setClipChildren(false);
        linearLayout.setOrientation(HORIZONTAL);
        LayoutParams params = new LayoutParams(-2, -2);
        linearLayout.setLayoutParams(params);

        editText = new EditText(getContext());
        editText.setFocusable(false);
        editText.setTextColor(getResources().getColor(R.color.menu_color));
        editText.setPadding(10, 0, 0, 0);
        editText.setClickable(false);
        editText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        editText.setBackgroundResource(R.drawable.keyboard_edit_bg);
        params = new LayoutParams(9 * mSmallBtnWidth + mBtnSpace * 8, mSmallBtnHeight);
        linearLayout.addView(editText, params);

        closeBtn = new KeyBoardBtnView(getContext());
        closeBtn.setImage(R.drawable.close_22_22);
        closeBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_3);
        params = new LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
        params.leftMargin = mBtnSpace;
        linearLayout.addView(closeBtn, params);

        return linearLayout;

    }

    private RelativeLayout get40BtnPart() {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        LayoutParams params = new LayoutParams(-2, -2);
        params.topMargin = mBtnSpace;
        relativeLayout.setLayoutParams(params);
        relativeLayout.setClipChildren(false);
        relativeLayout.removeAllViews();
        //根据设定的列数，计算每个按钮的边长
        int _columnNum = 10;
        int length = mKeys_1.length;
        int div = length % _columnNum;
        int lines = (div == 0) ? length / _columnNum : length / _columnNum + 1;
        int count = 1;
        int column = _columnNum;
        KeyBoardBtnView v;
        RelativeLayout.LayoutParams paramsBtn;
        for (int i = 0; i < lines; i++) {

            if (i == lines - 1 && div != 0) {
                column = div;
            }
            for (int j = 0; j < column; j++) {
                v = new KeyBoardBtnView(getContext());
                v.setId(count);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.setText(mKeys_1[count - 1]);
                if(v.getText().equals("g"))
                    firstFocus = v;


                v.setOnClickListener(this);
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                v.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
                v.setTextColor(getResources().getColor(R.color.menu_color));

                paramsBtn = new RelativeLayout.LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
                if (j != column - 1)
                    paramsBtn.rightMargin = mBtnSpace;
                paramsBtn.topMargin = mBtnSpace;

                if (i != 0) {
                    paramsBtn.addRule(RelativeLayout.BELOW, count - _columnNum);
                }
                if (j != 0) {
                    paramsBtn.addRule(RelativeLayout.RIGHT_OF, count - 1);
                }

                relativeLayout.addView(v, paramsBtn);
                count++;
            }
        }

        return relativeLayout;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = false;
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ||
                    event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE ||
                    event.getKeyCode() == KeyEvent.KEYCODE_DEL
                    ) {

                handled = true;
                dealingBackEvent();
            }
        }
        if (!handled)
            return super.dispatchKeyEvent(event);
        else
            return handled;
    }

    private LinearLayout getShiftSpaceSpart() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setClipChildren(false);
        linearLayout.setOrientation(HORIZONTAL);
        LayoutParams params = new LayoutParams(-2, -2);
        params.topMargin = mBtnSpace;
        linearLayout.setLayoutParams(params);

        KeyBoardBtnView shiftBtn = new KeyBoardBtnView(getContext());
        shiftBtn.setImage(R.drawable.shift_40_40);
        shiftBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
        params = new LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
        linearLayout.addView(shiftBtn, params);


        KeyBoardBtnView symbolBtn = new KeyBoardBtnView(getContext());
        symbolBtn.setText("@#:");
        params = new LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
        params.leftMargin = mBtnSpace;
        symbolBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
        symbolBtn.setTextColor(getResources().getColor(R.color.menu_color));
        linearLayout.addView(symbolBtn, params);

        KeyBoardBtnView space = new KeyBoardBtnView(getContext());
        space.setText("空格");
        space.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
        space.setTextColor(getResources().getColor(R.color.menu_color));
        params = new LayoutParams(mSmallBtnWidth * 4 + 3 * mBtnSpace, mSmallBtnHeight);
        params.leftMargin = mBtnSpace * 2 + mSmallBtnWidth;
        linearLayout.addView(space, params);
        space.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dealingKeyboardEvent(" ");
            }
        });

        confirmBtn = new KeyBoardBtnView(getContext());
        confirmBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_4);
        confirmBtn.setText("确定");
        confirmBtn.setTextColor(getResources().getColor(R.color.menu_color));
        params = new LayoutParams(mSmallBtnWidth * 2 + mBtnSpace, mSmallBtnHeight);
        params.leftMargin = mBtnSpace * 2 + mSmallBtnWidth;
        linearLayout.addView(confirmBtn, params);


        return linearLayout;
    }

//    private LinearLayout get4DirectionConfirmPart() {
//        LinearLayout linearLayout = new LinearLayout(getContext());
//        linearLayout.setOrientation(HORIZONTAL);
//        LayoutParams params = new LayoutParams(-2, -2);
//        params.topMargin = mBtnSpace;
//        linearLayout.setLayoutParams(params);
//
//        KeyBoardBtnView shiftBtn = new KeyBoardBtnView(getContext());
//        shiftBtn.setImage(R.drawable.shift_40_40);
//        shiftBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
//        params = new LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
//        linearLayout.addView(shiftBtn, params);
//
//
//        KeyBoardBtnView symbolBtn = new KeyBoardBtnView(getContext());
//        symbolBtn.setText("@#:");
//        params = new LayoutParams(mSmallBtnWidth, mSmallBtnHeight);
//        params.leftMargin = mBtnSpace;
//        symbolBtn.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
//        symbolBtn.setTextColor(getResources().getColor(R.color.menu_color));
//        linearLayout.addView(symbolBtn, params);
//
//        KeyBoardBtnView space = new KeyBoardBtnView(getContext());
//        space.setText("空格");
//        space.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
//        space.setTextColor(getResources().getColor(R.color.menu_color));
//        params = new LayoutParams(mSmallBtnWidth*4+3*mBtnSpace, mSmallBtnHeight);
//        params.leftMargin = mBtnSpace*2+mSmallBtnWidth;
//        linearLayout.addView(space, params);
//
//        KeyBoardBtnView delback = new KeyBoardBtnView(getContext());
//        delback.setBackgroundResource(R.drawable.keyboard_btn_bg_1);
//        delback.setImage(R.drawable.delback_32);
//        params = new LayoutParams(mSmallBtnWidth*2+mBtnSpace, mSmallBtnHeight);
//        params.leftMargin = mBtnSpace*2+mSmallBtnWidth;
//        linearLayout.addView(delback, params);
//    }

    public void initKeyBoard() {
        //编辑框 与关闭按钮
        addView(getEditClosePart());

        //40个按钮
        addView(get40BtnPart());

        //大小写，符号，空格，删除

        addView(getShiftSpaceSpart());
        //4个方向键 确认


        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onKeyboardListener!=null)
                    onKeyboardListener.onConfirm(editText.getText().toString());
            }
        });

        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onKeyboardListener!=null)
                    onKeyboardListener.onClose();
            }
        });
    }


    private void dealingKeyboardEvent(String text) {
        String original = editText.getText().toString();
        if (original.length() <= 30) {
            StringBuilder builder = new StringBuilder();
            builder.append(original);
            builder.append(text);
            editText.setText(builder.toString());
            if(onKeyboardListener!=null) {
                onKeyboardListener.onTextChanged(builder.toString());
            }
        }
    }

    private void dealingBackEvent() {
        String original = editText.getText().toString();
        if (!TextUtils.isEmpty(original)) {
            if (original.length() > 1) {
                original = original.substring(0, original.length() - 1);
            } else {
                original = "";
            }
        } else {
            if(onKeyboardListener!=null) {
                onKeyboardListener.onClose();
            }
        }
        editText.setText(original);
        if(onKeyboardListener!=null) {
            onKeyboardListener.onTextChanged(original);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -9:
                break;

            default:
                dealingKeyboardEvent(((KeyBoardBtnView)v).getText());
                break;
        }
    }


    class KeyBoardViewException extends Throwable {
        public KeyBoardViewException(String detailMessage) {
            super(detailMessage);
        }
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }

    private OnKeyboardListener onKeyboardListener;
    public interface OnKeyboardListener {
        void onClose();
        void onConfirm(String text);
        void onTextChanged(String text);
    }

    public void notifyFocus() {
        if(firstFocus != null)
            firstFocus.requestFocus();
    }
}
