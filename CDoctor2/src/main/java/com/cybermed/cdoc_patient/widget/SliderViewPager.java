package com.cybermed.cdoc_patient.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


import com.cybermed.cdoc_patient.common.ViewPagerAdapter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * slide pages
 */
public class SliderViewPager extends ViewPager {

    private boolean isAnimEnabled = false;
    private boolean isTouchEventEnable = false;
    private ViewPagerAdapter mAdapter;
    DisposableObserver<Long> disposableObserver;

    public SliderViewPager(@NonNull Context context) {
        super(context);
    }


    public SliderViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isTouchEventEnable) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isAnimEnabled = false;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                isAnimEnabled = true;
            }
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isTouchEventEnable) {

            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    /**
     * start scrolling of banners &
     * stop banner scrolling on click
     *
     * @param timeInSec delay in between sliding of fragments
     */

    public void setAnimation(int timeInSec) {
        if (mAdapter == null)
            return;
        isAnimEnabled = true;
        isTouchEventEnable = true;
        final int[] currentPage = {0};
        disposableObserver = Observable.interval(timeInSec, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (isAnimEnabled) {
                            if (currentPage[0] == mAdapter.getCount())
                                currentPage[0] = 0;
                            setCurrentItem(currentPage[0]++, true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setAdapter(ViewPagerAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            super.setAdapter(mAdapter);
        }

    }

    public void onViewDestroy() {
        disposableObserver.dispose();

    }

}
