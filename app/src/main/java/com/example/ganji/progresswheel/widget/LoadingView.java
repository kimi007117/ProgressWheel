package com.example.ganji.progresswheel.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 58 on 2016/6/7.
 */
public class LoadingView extends View {

    private static final int DEFAULT_SIZE = 45;

    private int barColor = 0xAA39BC30;

    private int barWidth = 5;

    private Paint mPaint;

    float scaleFloat = 1, degrees;

    float circleSpacing = 12;

    private RectF rectF1;
    private RectF rectF2;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(barColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(barWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth() / 2;
        float height = getHeight() / 2;
        canvas.save();
        canvas.translate(width, height);
        canvas.scale(scaleFloat, scaleFloat);
        canvas.rotate(degrees);
        float[] bStartAngles = new float[]{135, -45};
        for (int i = 0; i < 2; i++) {
            canvas.drawArc(rectF1, bStartAngles[i], 90, false, mPaint);
        }

        canvas.restore();
        canvas.translate(width, height);
        canvas.scale(scaleFloat, scaleFloat);
        canvas.rotate(-degrees);
        //draw two small arc
        float[] sStartAngles = new float[]{225, 45};
        for (int i = 0; i < 2; i++) {
            canvas.drawArc(rectF2, sStartAngles[i], 90, false, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect(w / 2, h / 2);
    }

    private void initRect(int w, int h) {
        rectF1 = new RectF(-w + circleSpacing, -h + circleSpacing, w - circleSpacing, h - circleSpacing);
        rectF2 = new RectF(-w / 1.8f + circleSpacing, -h / 1.8f + circleSpacing, w / 1.8f - circleSpacing, h / 1.8f - circleSpacing);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    private void startAnimation() {
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.6f, 1);
        scaleAnim.setDuration(1000);
        scaleAnim.setRepeatCount(-1);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleFloat = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        scaleAnim.start();

        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.start();
    }
}
