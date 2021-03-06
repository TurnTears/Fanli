package com.ascba.rebate.activities.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by linhonghong on 2015/8/10.
 */
public class APSTSViewPager extends ViewPager {
    private boolean mNoFocus = false; //if true, keep View don't move
    public APSTSViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public APSTSViewPager(Context context){
        this(context,null);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mNoFocus) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mNoFocus) {
            return super.onTouchEvent(event);
        }

        return false;
    }


    public void setNoFocus(boolean b){
        mNoFocus = b;
    }
}