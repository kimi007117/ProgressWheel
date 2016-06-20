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
 * 加载动画的loading
 */
public class CustomerLoadingView extends View {

    // 圆环的颜色
    private static final int circleColor = 0xAA39BC30;
    // 旋转一圈的时间
    private static final int circleTime = 1000;
    // 圆环的宽度 默认pix
    private static final int circleWidth = 5;
    // 圆环的大小  默认就是dp
    private static final int DEFAULT_SIZE = 35;
    // 圆环开始旋转的位置
    private float startAngle;
    // 圆环旋转时的弧度 动态变化
    private float sweepAngle;
    // paint对象
    private Paint mPaint;
    // rectF对象
    private RectF mRectF;
    // 上次完成一圈动画的时间
    private long lastTimeAnimated;


    public CustomerLoadingView(Context context) {
        super(context);
        init();
    }


    public CustomerLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setColor(circleColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(circleWidth);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
        spin();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRectF(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateCircleAngle();// 更新圆环的弧度
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);// 开始画
    }


    /**
     * 圆环开始旋转
     */
    private void spin() {
        lastTimeAnimated = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
    }

    /**
     * 实时计算旋转圆环的弧度
     */
    private void updateCircleAngle() {
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
        startAngle = offset - 90;
        sweepAngle = begin - offset;
        postInvalidate();
    }

    /**
     * 初始rectF
     *
     * @param w 宽
     * @param h 高
     */
    private void initRectF(int w, int h) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        mRectF = new RectF(paddingLeft + circleWidth, paddingTop + circleWidth, w - paddingRight - circleWidth, h - paddingBottom - circleWidth);
    }

    /**
     * 计算大小
     *
     * @param defaultSize 默认的大小
     * @param measureSpec 从资源文件或者其他获取的大小
     * @return 返回大小
     */
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

}
