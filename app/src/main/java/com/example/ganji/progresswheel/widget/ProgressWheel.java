package com.example.ganji.progresswheel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.example.ganji.progresswheel.R;

/**
 * Created by 58 on 2016/6/7.
 */
public class ProgressWheel extends View {
    private static final String TAG = ProgressWheel.class.getSimpleName();
    private final int barLength = 16;
    private final int barMaxLength = 180;
    private final long pauseGrowingTime = 300;
    /**
     * *********
     * DEFAULTS *
     * **********
     */
    //Sizes (with defaults in DP)
    private int circleRadius = 28;
    private int barWidth = 4;
    private int rimWidth = 4;
    private boolean fillRadius = false;
    private double timeStartGrowing = 0;
    private double barSpinCycleTime = 1000;
    private float barExtraLength = 0;
    private boolean barGrowingFromFront = true;
    private long pausedTimeWithoutGrowing = 0;
    //Colors (with defaults)
    private int barColor = 0xAA000000;
    private int rimColor = 0x00FFFFFF;

    //Paints
    private Paint barPaint = new Paint();
    private Paint rimPaint = new Paint();

    //Rectangles
    private RectF circleBounds = new RectF();

    //Animation
    //The amount of degrees per second
    private float spinSpeed = 230.0f;
    //private float spinSpeed = 120.0f;
    // The last time the spinner was animated
    private long lastTimeAnimated = 0;

    private boolean linearProgress;

    private float mProgress = 0.0f;
    private float mTargetProgress = 0.0f;
    private boolean isSpinning = false;

    private ProgressCallback callback;

    private boolean shouldAnimate;

    /**
     * The constructor for the ProgressWheel
     */
    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel));

        setAnimationEnabled();
    }

    /**
     * The constructor for the ProgressWheel
     */
    public ProgressWheel(Context context) {
        super(context);
        setAnimationEnabled();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setAnimationEnabled() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        float animationValue;
        if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            animationValue = Settings.Global.getFloat(getContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1);
        } else {
            animationValue = Settings.System.getFloat(getContext().getContentResolver(),
                    Settings.System.ANIMATOR_DURATION_SCALE, 1);
        }

        shouldAnimate = animationValue != 0;
    }

    //----------------------------------
    //Setting up stuff
    //----------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = circleRadius + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = circleRadius + this.getPaddingTop() + this.getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(viewWidth, widthSize);
        } else {
            //Be whatever you want
            width = viewWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(viewHeight, heightSize);
        } else {
            //Be whatever you want
            height = viewHeight;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setupBounds(w, h);
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWidth);

        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        if (!fillRadius) {
            // Width should equal to Height, find the min value to setup the circle
            int minValue = Math.min(layout_width - paddingLeft - paddingRight,
                    layout_height - paddingBottom - paddingTop);

            int circleDiameter = Math.min(minValue, circleRadius * 2 - barWidth * 2);

            // Calc the Offset if needed for centering the wheel in the available space
            int xOffset = (layout_width - paddingLeft - paddingRight - circleDiameter) / 2 + paddingLeft;
            int yOffset = (layout_height - paddingTop - paddingBottom - circleDiameter) / 2 + paddingTop;

            circleBounds =
                    new RectF(xOffset + barWidth, yOffset + barWidth, xOffset + circleDiameter - barWidth,
                            yOffset + circleDiameter - barWidth);
        } else {
            circleBounds = new RectF(paddingLeft + barWidth, paddingTop + barWidth,
                    layout_width - paddingRight - barWidth, layout_height - paddingBottom - barWidth);
        }
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(TypedArray a) {
        // We transform the default values from DIP to pixels
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        barWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, barWidth, metrics);
        rimWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rimWidth, metrics);
        circleRadius =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, metrics);

        circleRadius =
                (int) a.getDimension(R.styleable.ProgressWheel_matProg_circleRadius, circleRadius);

        fillRadius = a.getBoolean(R.styleable.ProgressWheel_matProg_fillRadius, false);

        barWidth = (int) a.getDimension(R.styleable.ProgressWheel_matProg_barWidth, barWidth);

        rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_matProg_rimWidth, rimWidth);

        float baseSpinSpeed =
                a.getFloat(R.styleable.ProgressWheel_matProg_spinSpeed, spinSpeed / 360.0f);
        spinSpeed = baseSpinSpeed * 360;

        barSpinCycleTime =
                a.getInt(R.styleable.ProgressWheel_matProg_barSpinCycleTime, (int) barSpinCycleTime);

        barColor = a.getColor(R.styleable.ProgressWheel_matProg_barColor, barColor);

        rimColor = a.getColor(R.styleable.ProgressWheel_matProg_rimColor, rimColor);

        linearProgress = a.getBoolean(R.styleable.ProgressWheel_matProg_linearProgress, false);

        if (a.getBoolean(R.styleable.ProgressWheel_matProg_progressIndeterminate, false)) {
            spin();
        }

        // Recycle
        a.recycle();
    }

    public void setCallback(ProgressCallback progressCallback) {
        callback = progressCallback;

        if (!isSpinning) {
            runCallback();
        }
    }

    //----------------------------------
    //Animation stuff
    //----------------------------------

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(circleBounds, 360, 360, false, rimPaint);

        boolean mustInvalidate = false;

        if (!shouldAnimate) {
            return;
        }

        if (isSpinning) {
            //Draw the spinning bar
            mustInvalidate = true;

            long deltaTime = (SystemClock.uptimeMillis() - lastTimeAnimated);
            float deltaNormalized = deltaTime * spinSpeed / 1000.0f;

            updateBarLength(deltaTime);

            mProgress += deltaNormalized;
            if (mProgress > 360) {
                mProgress -= 360f;

                // A full turn has been completed
                // we run the callback with -1 in case we want to
                // do something, like changing the color
                runCallback(-1.0f);
            }
            lastTimeAnimated = SystemClock.uptimeMillis();

            float from = mProgress - 90;
            float length = barLength + barExtraLength;

            if (isInEditMode()) {
                from = 0;
                length = 135;
            }

            canvas.drawArc(circleBounds, from, length, false, barPaint);
        } else {
            float oldProgress = mProgress;

            if (mProgress != mTargetProgress) {
                //We smoothly increase the progress bar
                mustInvalidate = true;

                float deltaTime = (float) (SystemClock.uptimeMillis() - lastTimeAnimated) / 1000;
                float deltaNormalized = deltaTime * spinSpeed;

                mProgress = Math.min(mProgress + deltaNormalized, mTargetProgress);
                lastTimeAnimated = SystemClock.uptimeMillis();
            }

            if (oldProgress != mProgress) {
                runCallback();
            }

            float offset = 0.0f;
            float progress = mProgress;
            if (!linearProgress) {
                float factor = 2.0f;
                offset = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, 2.0f * factor)) * 360.0f;
                progress = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, factor)) * 360.0f;
            }

            if (isInEditMode()) {
                progress = 360;
            }

            canvas.drawArc(circleBounds, offset - 90, progress, false, barPaint);
        }

        if (mustInvalidate) {
            invalidate();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == VISIBLE) {
            lastTimeAnimated = SystemClock.uptimeMillis();
        }
    }

    private void updateBarLength(long deltaTimeInMilliSeconds) {
        if (pausedTimeWithoutGrowing >= pauseGrowingTime) {
            timeStartGrowing += deltaTimeInMilliSeconds;

            if (timeStartGrowing > barSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                timeStartGrowing -= barSpinCycleTime;
                //if(barGrowingFromFront) {
                pausedTimeWithoutGrowing = 0;
                //}
                barGrowingFromFront = !barGrowingFromFront;
            }

            float distance =
                    (float) Math.cos((timeStartGrowing / barSpinCycleTime + 1) * Math.PI) / 2 + 0.5f;
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


    /**
     * Puts the view on spin mode
     */
    public void spin() {
        lastTimeAnimated = SystemClock.uptimeMillis();
        isSpinning = true;
        invalidate();
    }

    private void runCallback(float value) {
        if (callback != null) {
            callback.onProgressUpdate(value);
        }
    }

    private void runCallback() {
        if (callback != null) {
            float normalizedProgress = (float) Math.round(mProgress * 100 / 360.0f) / 100;
            callback.onProgressUpdate(normalizedProgress);
        }
    }


    // Great way to save a view's state http://stackoverflow.com/a/7089687/1991053
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        WheelSavedState ss = new WheelSavedState(superState);

        // We save everything that can be changed at runtime
        ss.mProgress = this.mProgress;
        ss.mTargetProgress = this.mTargetProgress;
        ss.isSpinning = this.isSpinning;
        ss.spinSpeed = this.spinSpeed;
        ss.barWidth = this.barWidth;
        ss.barColor = this.barColor;
        ss.rimWidth = this.rimWidth;
        ss.rimColor = this.rimColor;
        ss.circleRadius = this.circleRadius;
        ss.linearProgress = this.linearProgress;
        ss.fillRadius = this.fillRadius;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof WheelSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        WheelSavedState ss = (WheelSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mProgress = ss.mProgress;
        this.mTargetProgress = ss.mTargetProgress;
        this.isSpinning = ss.isSpinning;
        this.spinSpeed = ss.spinSpeed;
        this.barWidth = ss.barWidth;
        this.barColor = ss.barColor;
        this.rimWidth = ss.rimWidth;
        this.rimColor = ss.rimColor;
        this.circleRadius = ss.circleRadius;
        this.linearProgress = ss.linearProgress;
        this.fillRadius = ss.fillRadius;

        this.lastTimeAnimated = SystemClock.uptimeMillis();
    }


    public interface ProgressCallback {
        /**
         * Method to call when the progress reaches a value
         * in order to avoid float precision issues, the progress
         * is rounded to a float with two decimals.
         * <p/>
         * In indeterminate mode, the callback is called each time
         * the wheel completes an animation cycle, with, the progress value is -1.0f
         *
         * @param progress a double value between 0.00 and 1.00 both included
         */
        void onProgressUpdate(float progress);
    }

    static class WheelSavedState extends BaseSavedState {
        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<WheelSavedState> CREATOR =
                new Parcelable.Creator<WheelSavedState>() {
                    public WheelSavedState createFromParcel(Parcel in) {
                        return new WheelSavedState(in);
                    }

                    public WheelSavedState[] newArray(int size) {
                        return new WheelSavedState[size];
                    }
                };
        float mProgress;
        float mTargetProgress;
        boolean isSpinning;
        float spinSpeed;
        int barWidth;
        int barColor;
        int rimWidth;
        int rimColor;
        int circleRadius;
        boolean linearProgress;
        boolean fillRadius;

        WheelSavedState(Parcelable superState) {
            super(superState);
        }

        private WheelSavedState(Parcel in) {
            super(in);
            this.mProgress = in.readFloat();
            this.mTargetProgress = in.readFloat();
            this.isSpinning = in.readByte() != 0;
            this.spinSpeed = in.readFloat();
            this.barWidth = in.readInt();
            this.barColor = in.readInt();
            this.rimWidth = in.readInt();
            this.rimColor = in.readInt();
            this.circleRadius = in.readInt();
            this.linearProgress = in.readByte() != 0;
            this.fillRadius = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.mProgress);
            out.writeFloat(this.mTargetProgress);
            out.writeByte((byte) (isSpinning ? 1 : 0));
            out.writeFloat(this.spinSpeed);
            out.writeInt(this.barWidth);
            out.writeInt(this.barColor);
            out.writeInt(this.rimWidth);
            out.writeInt(this.rimColor);
            out.writeInt(this.circleRadius);
            out.writeByte((byte) (linearProgress ? 1 : 0));
            out.writeByte((byte) (fillRadius ? 1 : 0));
        }
    }
}
