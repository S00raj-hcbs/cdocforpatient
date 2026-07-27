package com.cybermed.cdoc_patient.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

public class SlideMenu extends ViewGroup {
    private int downX;
    private final int MAIN_VIEW = 0;
    private final int MENU_VIEW = 1;
    private int currentView = MAIN_VIEW;
    private Scroller scroller;
    private int touchSlop;

    private ViewPager viewPager;
    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SlideMenu(Context context) {
        this(context, null);
    }
    @SuppressWarnings("deprecation")
    private void init() {

        scroller = new Scroller(getContext());
        touchSlop = ViewConfiguration.getTouchSlop();
    }
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View mainView = getChildAt(1);
        mainView.measure(widthMeasureSpec, heightMeasureSpec);
        View menuView = getChildAt(0);
        menuView.measure(widthMeasureSpec-200, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View mainView = getChildAt(1);
        mainView.layout(l, t, r, b);
        View menuView = getChildAt(0);
        menuView.layout(-menuView.getMeasuredWidth(), t, 0, b);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;

            case MotionEvent.ACTION_UP:
                int center = -getChildAt(0).getMeasuredWidth() / 2;
               /* if (getScrollX() > center) {
                    currentView = MAIN_VIEW;
                } else {
                    currentView = MENU_VIEW;
                }*/
                if (getScrollX() > center) {
                    currentView = MAIN_VIEW;
                    if (viewPager != null) {
                        viewPager.requestDisallowInterceptTouchEvent(false);
                    }
                } else {
                    currentView = MENU_VIEW;
                    if (viewPager != null) {
                        viewPager.requestDisallowInterceptTouchEvent(true);
                    }
                }
                switchView();
                break;
            default:
                break;
        }
        return true;
    }
    private void switchView() {
        int startX = getScrollX();
        int dx = 0;
        if (currentView == MAIN_VIEW) {
            // scrollTo(0, 0);
            dx = -startX;
        } else {
            dx = -getChildAt(0).getMeasuredWidth() - startX;
        }
        int duration = Math.abs(dx) * 10;
        if (duration > 1000) {

            duration = 1000;
        }
        scroller.startScroll(startX, 0, dx, 0, duration);
        invalidate();
    }
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currX = scroller.getCurrX();
            scrollTo(currX, 0);
            invalidate();
        }
    }
    public boolean isMenuShow() {
        return currentView == MENU_VIEW;
    }
    public void hideMenu() {
        currentView = MAIN_VIEW;
        switchView();
    }
    public void showMenu() {
        currentView = MENU_VIEW;
        switchView();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int diff = moveX - downX;
                if (Math.abs(diff) > touchSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

            default:
                break;
        }
        if (viewPager != null) {
            viewPager.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }
}