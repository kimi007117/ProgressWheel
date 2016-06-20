package com.example.ganji.progresswheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Created by 58 on 2016/6/13.
 */
public class RotateLoadingView extends View {
    private static final int bigColor = 0xffff9d00;
    private static final int smallColor = 0xffff552e;
    private static final int circleTime = 1500;
    private static final float circleWidth = 5;
    private static final float DEFAULT_SIZE = 40;
    private Paint mPaintSmall;
    private RectF mRectFSmall;
    private Paint mPaintBig;
    private RectF mRectFBig;
    private float startAngleSmall;
    private float sweepAngleSmall;
    private float startAngleBig;
    private float sweepAngleBig;
    private static final float bigCircleRadio = 18;
    private static final float smallCircleRadio = 12;
    // 上次完成一圈动画的时间
    private long lastTimeAnimated;

    public RotateLoadingView(Context context) {
        super(context);
        init();
    }

    public RotateLoadingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        mPaintBig = new Paint();
        mPaintBig.setColor(bigColor);
        mPaintBig.setAntiAlias(true);
        mPaintBig.setStrokeWidth(circleWidth);
        mPaintBig.setStyle(Paint.Style.STROKE);
        mPaintSmall = new Paint();
        mPaintSmall.setColor(smallColor);
        mPaintSmall.setAntiAlias(true);
        mPaintSmall.setStyle(Paint.Style.STROKE);
        mPaintSmall.setStrokeWidth(circleWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateCircleLength();
        canvas.drawArc(mRectFBig, startAngleBig, sweepAngleBig, false, mPaintBig);
        canvas.drawArc(mRectFSmall, startAngleSmall, sweepAngleSmall, false, mPaintSmall);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
        spin();
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

    private int dp2px(float dpValue) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dpValue);
    }

    private void updateCircleLength() {
        if (((int) (AnimationUtils.currentAnimationTimeMillis() - lastTimeAnimated)) > circleTime) {
            lastTimeAnimated = AnimationUtils.currentAnimationTimeMillis();
        }
        // 偏移量
        float offset;
        // 时间差
        int deltaTime = (int) (AnimationUtils.currentAnimationTimeMillis() - lastTimeAnimated);
        // 比例
        float scale = (9.0f * 360.0f) / ((float) (circleTime * 4));
        // 刚刚开始
        float begin = 2.0f * (((float) circleTime) / 9.0f);
        // 接近一半
        float half = 4.0f * (((float) circleTime) / 9.0f);
        // 一大半
        float most = 6.0f * (((float) circleTime) / 9.0f);
        if (((float) deltaTime) < begin) {
            offset = (((((float) deltaTime) * 2.25f) * ((float) deltaTime)) / ((float) circleTime)) * scale;
        } else {
            offset = 0.0f;
        }
        if (((float) deltaTime) >= begin && ((float) deltaTime) < half) {
            offset = ((((float) deltaTime) - begin) * scale) + 90.0f;
        }
        if (((float) deltaTime) > half) {
            offset = 360.0f - (((((most - ((float) deltaTime)) * 2.25f) * (most - ((float) deltaTime))) * scale) / ((float) circleTime));
        }
        if (((float) deltaTime) > most) {
            begin = 360.0f;
        } else {
            begin = offset;
        }
        offset = (((float) circleTime) / 9.0f) * 3.0f;
        half = (((float) circleTime) / 9.0f) * 5.0f;
        most = (((float) circleTime) / 9.0f) * 7.0f;
        if (((float) deltaTime) <= offset || ((float) deltaTime) >= half) {
            offset = 0.0f;
        } else {
            offset = (((((float) deltaTime) - offset) * ((((float) deltaTime) - offset) * 2.25f)) * scale) / ((float) circleTime);
        }
        if (((float) deltaTime) > half && ((float) deltaTime) < most) {
            offset = ((((float) deltaTime) - half) * scale) + 90.0f;
        }
        if (((float) deltaTime) > most) {
            offset = 360.0f - ((((((float) (circleTime - deltaTime)) * 2.25f) * ((float) (circleTime - deltaTime))) / ((float) circleTime)) * scale);
        }
        startAngleBig = 0.0f + offset;
        sweepAngleBig = begin - offset;
        startAngleSmall = 180.0f + offset;
        sweepAngleSmall = begin - offset;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float mBigCircleRadio = (float) dp2px(bigCircleRadio);
        float mSmallCircleRadio = (float) dp2px(smallCircleRadio);
        if (Math.max(2.0f * mBigCircleRadio, 2.0f * mSmallCircleRadio) > ((float) Math.min(w, h))) {
            throw new RuntimeException("the size of RotateLoadingView must bigger then inner cicle");
        }
        initRectF(w/2.0f , h/2.0f , mBigCircleRadio, mSmallCircleRadio);
    }

    private void initRectF(float w, float h, float mBigCircleRadio, float mSmallCircleRadio) {
        mRectFSmall = new RectF(w - mSmallCircleRadio, h - mSmallCircleRadio, w + mSmallCircleRadio, mSmallCircleRadio + h);
        mRectFBig = new RectF(w - mBigCircleRadio, h - mBigCircleRadio, w + mBigCircleRadio, mBigCircleRadio + h);
    }


    public void spin() {
        lastTimeAnimated = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
    }
}
