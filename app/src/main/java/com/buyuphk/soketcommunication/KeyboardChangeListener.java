package com.buyuphk.soketcommunication;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-08-20 12:13
 * motto: 勇于向未知领域探索
 */
public class KeyboardChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "ListenerHandler";
    private View mContentView;   // 当前界面的根视图
    private int mOriginHeight;   // 此时根视图的高度
    private int mPreHeight;   // 改变之前根视图的高度
    private KeyBoardListener mKeyBoardListen;

    public KeyboardChangeListener(Activity activity) {
        if (activity == null) {
            Log.i(TAG, "contextObj is null");
            return;
        }
        mContentView = findContentView(activity);
        if (mContentView != null) {
            addContentTreeObserver();
        }
    }

    private View findContentView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }

    private void addContentTreeObserver() {
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        // 先获取到当前根视图的高度
        int currHeight = mContentView.getHeight();
        if (currHeight == 0) {
            return;
        }

        boolean hasChange = false;
        if (mPreHeight == 0) {
            mPreHeight = currHeight;
            mOriginHeight = currHeight;
        } else {
            if (mPreHeight != currHeight) {
                hasChange = true;
                mPreHeight = currHeight;
            } else {
                hasChange = false;
            }
        }
        if (hasChange) {
            boolean isShow;
            int keyboardHeight = 0;
            // 当前的根视图高度和初始化时的高度一样时，说明此时软键盘没有显示，是消失状态
            if (mOriginHeight == currHeight) {
                //hidden
                isShow = false;
            } else {
                // 此时，根视图的高度减少了，而减少的部分就是软键盘的高度，软键盘显示状态
                //show
                keyboardHeight = mOriginHeight - currHeight;
                isShow = true;
            }

            if (mKeyBoardListen != null) {
                mKeyBoardListen.onKeyboardChange(isShow, keyboardHeight);
            }
        }
    }

    public void setKeyBoardListener(KeyBoardListener keyBoardListen) {
        this.mKeyBoardListen = keyBoardListen;
    }

    // 资源释放
    public void destroy() {
        if (mContentView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    public interface KeyBoardListener {

        void onKeyboardChange(boolean isShow, int keyboardHeight);
    }
}
