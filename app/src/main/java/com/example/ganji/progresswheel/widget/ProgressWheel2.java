package com.example.ganji.progresswheel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 58 on 2016/6/8.
 * 简化版的wheel
 */
public class ProgressWheel2 extends View {
    private final int barLength = 16;
    private final int barMaxLength = 180;
    private final long pauseGrowingTime = 200;
    private int barWidth = 6;
    private double timeStartGrowing = 0;
    private double barSpinCycleTime = 500;
    private float barExtraLength = 0;
    private boolean barGrowingFromFront = true;
    private long pausedTimeWithoutGrowing = 0;
    private int barColor = 0xAA39BC30;
    private Paint barPaint = new Paint();
    private RectF circleBounds = new RectF();
    private float spinSpeed = 230.0f;
    private long lastTimeAnimated = 0;
    private float mProgress = 0.0f;
    private boolean shouldAnimate;
    private static final int DEFAULT_SIZE = 45;


    public ProgressWheel2(Context context, AttributeSet attrs) {
        super(context, attrs);
        lastTimeAnimated = SystemClock.uptimeMillis();
        invalidate();
        setAnimationEnabled();
    }

    public ProgressWheel2(Context context) {
        super(context);
        setAnimationEnabled();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setAnimationEnabled() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        float animationValue;
        if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            animationValue = Settings.Global.getFloat(getContext().getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1);
        } else {
            animationValue = Settings.System.getFloat(getContext().getContentResolver(), Settings.System.ANIMATOR_DURATION_SCALE, 1);
        }
        shouldAnimate = animationValue != 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
    }


    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWidth);
    }


    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        circleBounds = new RectF(paddingLeft + barWidth, paddingTop + barWidth,
                layout_width - paddingRight - barWidth, layout_height - paddingBottom - barWidth);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!shouldAnimate) {
            return;
        }
        long deltaTime = (SystemClock.uptimeMillis() - lastTimeAnimated);
        float deltaNormalized = deltaTime * spinSpeed / 1000.0f;
        updateBarLength(deltaTime);
        mProgress += deltaNormalized;
        if (mProgress > 360) {
            mProgress -= 360f;
        }
        lastTimeAnimated = SystemClock.uptimeMillis();
        float from = mProgress - 90;
        float length = barLength + barExtraLength;
        if (isInEditMode()) {
            from = 0;
            length = 135;
        }
        canvas.drawArc(circleBounds, from, length, false, barPaint);
        invalidate();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            lastTimeAnimated = SystemClock.uptimeMillis();
        }
    }

    private void updateBarLength(long deltaTimeInMilliSeconds) { //更新画笔长度
        if (pausedTimeWithoutGrowing >= pauseGrowingTime) {
            timeStartGrowing += deltaTimeInMilliSeconds;
            if (timeStartGrowing > barSpinCycleTime) {
                timeStartGrowing -= barSpinCycleTime;
                pausedTimeWithoutGrowing = 0;
                barGrowingFromFront = !barGrowingFromFront;
            }
            float distance = (float) Math.cos((timeStartGrowing / barSpinCycleTime + 1) * Math.PI) / 2 + 0.5f;
            float destLength = (barMaxLength - barLength);

            if (barGrowingFromFront) {
                barExtraLength = distance * destLength;
            } else {
                float newLength = destLength * (1 - distance);
                mProgress += (barExtraLength - newLength);
                barExtraLength = newLength;
            }
        } else {
            pausedTimeWithoutGrowing += deltaTimeInMilliSeconds;
        }
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
}
