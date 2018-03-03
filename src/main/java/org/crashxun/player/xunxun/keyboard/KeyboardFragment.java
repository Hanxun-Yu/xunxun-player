package org.crashxun.player.xunxun.keyboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import org.crashxun.player.R;

/**
 * Created by xunxun on 2018/2/24.
 */

public class KeyboardFragment extends Fragment {
    KeyBoardView keyBoardView;
    ViewGroup rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new RelativeLayout(getContext());
        rootView.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2,-2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        keyBoardView = new KeyBoardView(getContext());
        rootView.addView(keyBoardView,params);
        rootView.setVisibility(View.INVISIBLE);
        return rootView;
    }

    public void setInputType(int type) {
        keyBoardView.setInputType(type);
    }
    public void setInitText(String text) {
        keyBoardView.setInitText(text);
    }

    public static KeyboardFragment newInstance() {
       return newInstance(null);
    }
    public static KeyboardFragment newInstance(String params) {
        Bundle args = new Bundle();
        args.putString("params",params);
        KeyboardFragment fragment = new KeyboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        keyBoardView.setOnKeyboardListener(new KeyBoardView.OnKeyboardListener() {
            @Override
            public void onClose() {
                setUserVisibleHint(false);
                if(keyboardEventListener != null)
                    keyboardEventListener.onCancel();
            }

            @Override
            public void onConfirm(String text) {
                setUserVisibleHint(false);
                if(keyboardEventListener != null)
                    keyboardEventListener.onConfirm(text);
            }

            @Override
            public void onTextChanged(String text) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser) {
            if(keyboardEventListener != null)
                keyboardEventListener.onShow();
            showKeyboard();
        } else {
            if(keyboardEventListener != null)
                keyboardEventListener.onClose();
            hideKeyboard();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void showKeyboard() {
        Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.keyboard_comein);
        anim.setFillAfter(true);
        anim.setDuration(300);
        rootView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        rootView.setVisibility(View.VISIBLE);
        rootView.startAnimation(anim);
        keyBoardView.notifyFocus();
    }

    private void hideKeyboard() {
        Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.keyboard_comeout);
        anim.setFillAfter(true);
        anim.setDuration(300);
        rootView.startAnimation(anim);
        rootView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        rootView.setFocusable(false);
        rootView.clearFocus();

    }

    public void setKeyboardEventListener(KeyboardEventListener keyboardEventListener) {
        this.keyboardEventListener = keyboardEventListener;
    }

    KeyboardEventListener keyboardEventListener;

    public interface KeyboardEventListener {
        void onClose();
        void onShow();
        void onCancel();
        void onConfirm(String text);
    }
}
