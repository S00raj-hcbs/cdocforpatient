package com.cybermed.cdoc_patient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.cybermed.cdoc_patient.R;
import com.stemoscope.stemolib.bus.BusManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;


// stemoscope wave form chart, not in use
public class WaveFormView extends View {


    private List<Float> list = Collections.synchronizedList(new ArrayList<Float>());

    private List<Float> list1 = new ArrayList<>();

    private Paint paint;


    public static final float POINT_NUM = 560;


    private boolean isNeedAddPoint = false;


    public WaveFormView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.loginDefaultColor));
        paint.setStrokeWidth(2f);
        for (int i = 0; i < POINT_NUM; i++) {
            list1.add(0f);
        }
        start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        float jj = width / POINT_NUM;


        list.clear();
        list.addAll(list1);

        list.remove(null);

        float min = Collections.min(list);
        float max = Collections.max(list);

        float realMax = Math.max(max, Math.abs(min));

        int height = getHeight();
        //将总高度均分为 max-min份

        float perHeight;
        if (realMax != 0) {
            perHeight = height / 2.0f / realMax;
        } else {
            perHeight = 0;
        }
        //每个点的高度尾    （点的数值 -min） *perHeight
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                canvas.drawLine((i - 1) * jj,
                        height / 2 -list.get(i - 1) * perHeight,
                        i * jj,
                        height / 2-list.get(i) * perHeight ,
                        paint);
            }
        }
    }

    public WaveFormView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveFormView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void start() {
        isNeedAddPoint = true;
        int postDelayed = 100;
        Flowable.interval(0, postDelayed, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .takeWhile(along -> {
                    invalidate();
                    return isNeedAddPoint;
                })
                .subscribe();

    }

    public void stop() {
        isNeedAddPoint = false;
        list.clear();
        list1.clear();
        for (int i = 0; i < POINT_NUM; i++) {
            list1.add(0f);
        }
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusManager.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        BusManager.getInstance().unregister(this);
        super.onDetachedFromWindow();
    }


//    @Subscribe
//    public void getPoint(ViewPointEvent event) {
//        if (isNeedAddPoint) {
//            if (list1.size() >= POINT_NUM) {
//                list1.remove(0);
//            }
//            list1.add(((float) event.getPoint()));
//        }
//    }
}
