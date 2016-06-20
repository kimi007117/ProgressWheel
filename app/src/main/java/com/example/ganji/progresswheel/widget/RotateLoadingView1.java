package com.example.ganji.progresswheel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.ganji.progresswheel.R;

/**
 * Created by 58 on 2016/6/13.
 */
public class RotateLoadingView1 extends View {
    private static final float k = 40.0f;
    private static final String l = "#ff9d00";
    private static final float m = 2.0f;
    private static final float n = 27.0f;
    private static final String o = "#ff552e";
    private static final float p = 2.0f;
    private static final int q = 3000;
    private Paint mPaintSmall;
    private RectF mRectFSmall;
    private Paint mPaintBig;
    private RectF mRectFBig;
    private float startAngleSmall;
    private float sweepAngleSmall;
    private float startAngleBig;
    private float sweepAngleBig;
    private int circleTime;
    private float bigCircleRadio;
    private float smallCircleRadio;

    private static final float circleWidth = 5;
    private Context mContext;
    private a u;

    static class a {
        private static final int f = 250;
        private long a;
        private int b;
        private boolean c;
        private int d;
        private int e;

        public a() {
            this.c = true;
            this.e = 1;
        }

        public final boolean a() {
            return this.c;
        }

        public final void a(boolean z) {
            this.c = z;
        }

        public final int b() {
            return this.b;
        }

        public boolean c() {
            if (this.c) {
                return false;
            }
            if (((int) (AnimationUtils.currentAnimationTimeMillis() - this.a)) > this.b) {
                if (this.e >= this.d) {
                    return false;
                }
                g();
                this.e++;
            }
            return true;
        }



        public void a(int i) {
            a(i, Integer.MAX_VALUE);
        }

        public void a(int i, int i2) {
            this.c = false;
            this.b = i;
            this.d = i2;
            this.a = AnimationUtils.currentAnimationTimeMillis();
        }

        private void g() {
            this.c = false;
            this.a = AnimationUtils.currentAnimationTimeMillis();
        }

        public void e() {
            this.c = true;
        }

        public void b(int i) {
            this.b = f() + i;
            this.c = false;
        }

        public int f() {
            return (int) (AnimationUtils.currentAnimationTimeMillis() - this.a);
        }
    }

    public RotateLoadingView1(Context context) {
        super(context, null);

        bigCircleRadio = k;
        smallCircleRadio = n;
    }

    public RotateLoadingView1(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        bigCircleRadio = k;
        smallCircleRadio = n;
        mContext = context;
        a(context, attributeSet);
    }

    private void a(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.rotate_view_styleable);
        bigCircleRadio = obtainStyledAttributes.getFloat(R.styleable.rotate_view_styleable_big_circle_radio, k);
        int color = obtainStyledAttributes.getColor(R.styleable.rotate_view_styleable_big_circle_color, Color.parseColor(l));
        float f = obtainStyledAttributes.getFloat(R.styleable.rotate_view_styleable_big_circle_width, m);
        smallCircleRadio = obtainStyledAttributes.getFloat(R.styleable.rotate_view_styleable_small_circle_radio, n);
        int color2 = obtainStyledAttributes.getColor(R.styleable.rotate_view_styleable_small_circle_color, Color.parseColor(o));
        float f2 = obtainStyledAttributes.getFloat(R.styleable.rotate_view_styleable_small_circle_width, p);
        circleTime = obtainStyledAttributes.getInteger(R.styleable.rotate_view_styleable_circle_time, q);
        obtainStyledAttributes.recycle();
        mPaintSmall = new Paint();
        mPaintSmall.setColor(color2);
        mPaintSmall.setAntiAlias(true);
        mPaintSmall.setStyle(Paint.Style.STROKE);
        mPaintSmall.setStrokeWidth(circleWidth);
        mPaintBig = new Paint();
        mPaintBig.setColor(color);
        mPaintBig.setAntiAlias(true);
        mPaintBig.setStrokeWidth(circleWidth);
        mPaintBig.setStyle(Paint.Style.STROKE);
        this.u = new a();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        a(canvas);
    }

    private void a(Canvas canvas) {
        canvas.drawArc(mRectFSmall, startAngleSmall, sweepAngleSmall, false, mPaintSmall);
        canvas.drawArc(mRectFBig, startAngleBig, sweepAngleBig, false, mPaintBig);
    }

    public int a(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public void computeScroll() {
        if (this.u.c()) {
            a(this.u);
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(40), widthMeasureSpec);
        int height = measureDimension(dp2px(40), heightMeasureSpec);
        setMeasuredDimension(width, height);
        a();
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

    private void a(a aVar) {
        float f;
        int b = aVar.b();
        int f2 = aVar.f();
        float f3 = (9.0f * 360.0f) / ((float) (b * 4));
        float f4 = 2.0f * (((float) b) / 9.0f);
        float f5 = 4.0f * (((float) b) / 9.0f);
        float f6 = 6.0f * (((float) b) / 9.0f);
        if (((float) f2) < f4) {
            f = (((((float) f2) * 2.25f) * ((float) f2)) / ((float) b)) * f3;
        } else {
            f = 0.0f;
        }
        if (((float) f2) >= f4 && ((float) f2) < f5) {
            f = ((((float) f2) - f4) * f3) + 90.0f;
        }
        if (((float) f2) > f5) {
            f = 360.0f - (((((f6 - ((float) f2)) * 2.25f) * (f6 - ((float) f2))) * f3) / ((float) b));
        }
        if (((float) f2) > f6) {
            f4 = 360.0f;
        } else {
            f4 = f;
        }
        f = (((float) b) / 9.0f) * 3.0f;
        f5 = (((float) b) / 9.0f) * 5.0f;
        f6 = (((float) b) / 9.0f) * 7.0f;
        if (((float) f2) < f) {
        }
        if (((float) f2) <= f || ((float) f2) >= f5) {
            f = 0.0f;
        } else {
            f = (((((float) f2) - f) * ((((float) f2) - f) * 2.25f)) * f3) / ((float) b);
        }
        if (((float) f2) > f5 && ((float) f2) < f6) {
            f = ((((float) f2) - f5) * f3) + 90.0f;
        }
        if (((float) f2) > f6) {
            f = 360.0f - ((((((float) (b - f2)) * 2.25f) * ((float) (b - f2))) / ((float) b)) * f3);
        }
        startAngleSmall = 180.0f + f;
        sweepAngleSmall = f4 - f;
        startAngleBig = 0.0f + f;
        sweepAngleBig = f4 - f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float mBigCircleRadio = (float) a(mContext, bigCircleRadio);
        float mSmallCircleRadio = (float) a(mContext, smallCircleRadio);
        if (Math.max(2.0f * mBigCircleRadio, 2.0f * mSmallCircleRadio) > ((float) Math.min(w, h))) {
            throw new RuntimeException("the size of RotateLoadingView must bigger then inner cicle");
        }
        mRectFSmall = new RectF((((float) w) / 2.0f) - mSmallCircleRadio, (((float) h) / 2.0f) - mSmallCircleRadio, (((float) w) / 2.0f) + mSmallCircleRadio, mSmallCircleRadio + (((float) h) / 2.0f));
        mRectFBig = new RectF((((float) w) / 2.0f) - mBigCircleRadio, (((float) h) / 2.0f) - mBigCircleRadio, (((float) w) / 2.0f) + mBigCircleRadio, mBigCircleRadio + (((float) h) / 2.0f));
    }

    public void a() {
        if (!this.u.a()) {
            this.u.a(true);
        }
        this.u.a(circleTime);
        invalidate();
    }

    public void b() {
        this.u.a(true);
    }

}
