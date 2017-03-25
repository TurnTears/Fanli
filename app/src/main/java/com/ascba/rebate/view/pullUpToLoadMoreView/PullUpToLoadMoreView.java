package com.ascba.rebate.view.pullUpToLoadMoreView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 商品详情，上滑显示详情
 */
public class PullUpToLoadMoreView extends ViewGroup {
    public static String TAG = PullUpToLoadMoreView.class.getName();

    NickScrollView topScrollView, bottomScrollView;
    VelocityTracker velocityTracker = VelocityTracker.obtain();
    Scroller scroller = new Scroller(getContext());

    int currPosition = 0;
    int position1Y;
    int lastY;
    public int scaledTouchSlop;//最小滑动距离
    int speed = 200;
    boolean isIntercept;

    public boolean bottomScrollVIewIsInTop = false;
    public boolean topScrollViewIsBottom = false;
    public boolean topScrollViewIsTop = false;
    OnCurrPosition onCurrPosition;

    public PullUpToLoadMoreView(Context context) {
        super(context);
        init();
    }

    public PullUpToLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullUpToLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnCurrPosition(OnCurrPosition onCurrPosition) {
        this.onCurrPosition = onCurrPosition;
    }

    private void init() {

        post(new Runnable() {
            @Override
            public void run() {
                topScrollView = (NickScrollView) getChildAt(0);
                bottomScrollView = (NickScrollView) getChildAt(1);
                topScrollView.setScrollListener(new NickScrollView.ScrollListener() {
                    @Override
                    public void onScrollToBottom() {
                        topScrollViewIsBottom = true;
                        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
                    }

                    @Override
                    public void onScrollToTop() {
                        topScrollViewIsTop = true;
                        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
                    }

                    @Override
                    public void onScroll(int scrollY) {
                        topScrollViewIsBottom = false;
                        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
                    }

                    @Override
                    public void notBottom() {
                        topScrollViewIsBottom = false;
                        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
                    }

                    @Override
                    public void noTop() {
                        topScrollViewIsTop = false;
                        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
                    }

                });

                bottomScrollView.setScrollListener(new NickScrollView.ScrollListener() {
                    @Override
                    public void onScrollToBottom() {

                    }

                    @Override
                    public void onScrollToTop() {

                    }

                    @Override
                    public void onScroll(int scrollY) {
                        if (scrollY == 0) {
                            bottomScrollVIewIsInTop = true;
                        } else {
                            bottomScrollVIewIsInTop = false;
                        }
                    }

                    @Override
                    public void notBottom() {

                    }

                    @Override
                    public void noTop() {

                    }
                });

                position1Y = topScrollView.getBottom();

                scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //防止子View禁止父view拦截事件
        this.requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否已经滚动到了底部
                if (topScrollViewIsBottom) {
                    int dy = lastY - y;

                    //判断是否是向上滑动和是否在第一屏
                    if (dy > 0 && currPosition == 0) {
                        if (dy >= scaledTouchSlop) {
                            isIntercept = true;//拦截事件
                            lastY = y;
                        }
                    }
                }

                if (bottomScrollVIewIsInTop) {
                    int dy = lastY - y;

                    //判断是否是向下滑动和是否在第二屏
                    if (dy < 0 && currPosition == 1) {
                        if (Math.abs(dy) >= scaledTouchSlop) {
                            isIntercept = true;
                        }
                    }
                }

                break;
        }
        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int dy = lastY - y;
                if (getScrollY() + dy < 0) {
                    dy = getScrollY() + dy + Math.abs(getScrollY() + dy);
                }

                if (getScrollY() + dy + getHeight() > bottomScrollView.getBottom()) {
                    dy = dy - (getScrollY() + dy - (bottomScrollView.getBottom() - getHeight()));
                }
                scrollBy(0, dy);
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;

                velocityTracker.computeCurrentVelocity(1000);
                float yVelocity = velocityTracker.getYVelocity();

                if (currPosition == 0) {
                    if (yVelocity < 0 && yVelocity < -speed) {
                        smoothScroll(position1Y);
                        currPosition = 1;
                    } else {
                        smoothScroll(0);
                    }
                } else {
                    if (yVelocity > 0 && yVelocity > speed) {
                        smoothScroll(0);
                        currPosition = 0;
                    } else {
                        smoothScroll(position1Y);
                    }
                }

                break;
        }
        lastY = y;
        onCurrPosition.currPosition(currPosition, topScrollViewIsTop);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childTop = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(l, childTop, r, childTop + child.getMeasuredHeight());
            childTop += child.getMeasuredHeight();
        }
    }


    //通过Scroller实现滑动有弹性效果
    private void smoothScroll(int tartY) {
        int dy = tartY - getScrollY();
        scroller.startScroll(getScrollX(), getScrollY(), 0, dy);
        invalidate();
    }


    //滚动到顶部
    public void scrollToTop() {
        smoothScroll(0);
        currPosition = 0;
        topScrollView.smoothScrollTo(0, 0);
    }

    //滚动到第二页
    public void scrollToSecond() {
        smoothScroll(1);
        currPosition = 1;
        scroller.startScroll(getScrollX(), getScrollY(), 0, topScrollView.getHeight());
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    public interface OnCurrPosition {
        void currPosition(int currPosition, boolean isTop);
    }

    public int getCurrPosition() {
        return currPosition;
    }
}
