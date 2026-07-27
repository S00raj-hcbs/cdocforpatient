package com.cybermed.cdoc_patient.me.vitalcheck;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;

public class AnimatedBottomCurveView extends View {

    private Paint paint;
    private Path path;
    private float wave = 0f;
    private ValueAnimator animator;

    public AnimatedBottomCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#3CA7D6"));
        path = new Path();
        startAnim();
    }

    private void startAnim() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(a -> {
            wave = (float) a.getAnimatedValue();
            invalidate();
        });

        animator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float baseDepth = h * 0.48f;
        float animDepth = h * 0.06f * wave;
        float curveDepth = baseDepth + animDepth;

        float startY = h - curveDepth;

        path.reset();
        path.moveTo(0, 0);
        path.lineTo(w, 0);
        path.lineTo(w, startY);

        // 🔥 number of curves (change this)
        int waveCount = 5;
        float segment = w / waveCount;

        for (int i = 0; i < waveCount; i++) {
            float startX = w - segment * i;
            float endX = w - segment * (i + 1);

            float controlX1 = startX - segment * 0.25f;
            float controlX2 = startX - segment * 0.75f;

            path.cubicTo(
                    controlX1, startY + curveDepth,
                    controlX2, startY - curveDepth * 0.15f,
                    endX, startY
            );
        }

        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }
}


