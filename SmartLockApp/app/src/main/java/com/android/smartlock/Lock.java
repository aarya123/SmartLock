package com.android.smartlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Lock extends View {

    private static final int DEFAULT_LOCKED_COLOR = 0xfff50057;
    private static final int DEFAULT_UNLOCKED_COLOR = 0xff76ff03;
    private static final boolean DEFAULT_LOCK_STATUS = false;

    private int mLockedBackgroundColor = DEFAULT_LOCKED_COLOR;
    private int mUnlockedBackgroundColor = DEFAULT_UNLOCKED_COLOR;
    private boolean mLocked = DEFAULT_LOCK_STATUS;

    private Paint mLockPaint;
    private Paint mLockedBackgroundPaint;
    private Paint mUnlockedBackgroundPaint;

    private GestureDetector mGestureDetector;

    public Lock(Context context) {
        this(context, null);
    }

    public Lock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Lock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Lock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Lock, 0, 0);
        try {
            mLockedBackgroundColor = typedArray.getColor(R.styleable.Lock_lockedBackgroundColor, DEFAULT_LOCKED_COLOR);
            mUnlockedBackgroundColor = typedArray.getColor(R.styleable.Lock_lockedBackgroundColor, DEFAULT_UNLOCKED_COLOR);
            mLocked = typedArray.getBoolean(R.styleable.Lock_locked, DEFAULT_LOCK_STATUS);
        } finally {
            typedArray.recycle();
        }
        init();
    }

    public int getLockedColor() {
        return mLockedBackgroundColor;
    }

    public void setLockedColor(int mLockedColor) {
        boolean viewNeedsUpdating = mLockedColor != this.mLockedBackgroundColor;
        this.mLockedBackgroundColor = mLockedColor;
        mLockedBackgroundPaint.setColor(mLockedBackgroundColor);
        if (viewNeedsUpdating) {
            invalidate();
            requestLayout();
        }
    }

    public int getUnlockedColor() {
        return mUnlockedBackgroundColor;
    }

    public void setUnlockedColor(int mUnlockedColor) {
        boolean viewNeedsUpdating = mUnlockedColor != this.mUnlockedBackgroundColor;
        this.mUnlockedBackgroundColor = mUnlockedColor;
        mUnlockedBackgroundPaint.setColor(mUnlockedBackgroundColor);
        if (viewNeedsUpdating) {
            invalidate();
            requestLayout();
        }
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean mLocked) {
        boolean viewNeedsUpdating = mLocked != this.mLocked;
        this.mLocked = mLocked;
        if (viewNeedsUpdating) {
            invalidate();
            requestLayout();
        }
    }

    public void toggleLocked() {
        this.mLocked = !this.mLocked;
        invalidate();
        requestLayout();
    }

    private void init() {
        mLockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnlockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mLockPaint.setColor(0xff424242);
        mLockedBackgroundPaint.setColor(mLockedBackgroundColor);
        mUnlockedBackgroundPaint.setColor(mUnlockedBackgroundColor);

        mLockPaint.setStyle(Paint.Style.STROKE);
        mLockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mUnlockedBackgroundPaint.setStyle(Paint.Style.FILL);

        mLockPaint.setStrokeWidth(10f);

        mGestureDetector = new GestureDetector(getContext(), new LockSimpleGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect dimensions = canvas.getClipBounds();
        int size = Math.min(dimensions.width(), dimensions.height());
        float centerX = size / 2.0f;
        float centerY = size / 2.0f;

        float lockHeight = size * 0.5f;
        float lockWidth = lockHeight * 2.0f / 3.0f;
        float lockLeftX = centerX - lockWidth / 2.0f;
        float lockRightX = centerX + lockWidth / 2.0f;
        float lockTopY = centerY - size * 0.075f;
        float lockBottomY = lockTopY + lockHeight / 2.0f;

        float keyHoleX = centerX;
        float keyHoleY = (lockTopY + lockBottomY) / 2.0f;
        float keyHoleRadius = lockWidth * 0.1f;

        float handleSideLength = size * 0.1f;
        float handleSideBottomY = lockTopY;
        float handleSideTopY = lockTopY - handleSideLength;
        float handleSideLeftX = centerX - lockWidth * 0.25f;
        float handleSideRightX = centerX + lockWidth * 0.25f;

        float handleArcLength = size * .075f;
        float handleArcLeftX = handleSideLeftX;
        float handleArcRightX = handleSideRightX;
        float handleArcBottomY = handleSideTopY + handleArcLength;
        float handleArcTopY = handleSideTopY - handleArcLength;

        float circleRadius = size / 2.0f;
        float circleX = centerX;
        float circleY = centerY;

        canvas.drawCircle(circleX, circleY, circleRadius, isLocked() ? mLockedBackgroundPaint : mUnlockedBackgroundPaint);
        canvas.drawRoundRect(lockLeftX, lockTopY, lockRightX, lockBottomY, 10, 10, mLockPaint);
        canvas.drawCircle(keyHoleX, keyHoleY, keyHoleRadius, mLockPaint);
        canvas.drawLine(handleSideLeftX, handleSideBottomY, handleSideLeftX, handleSideTopY, mLockPaint);
        canvas.drawLine(handleSideRightX, handleSideBottomY, handleSideRightX, handleSideTopY, mLockPaint);
        canvas.drawArc(handleArcLeftX, handleArcTopY, handleArcRightX, handleArcBottomY, 180f, 180f, false, mLockPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //TODO:Make bg lighter
                result = true;
            }
        }
        return result;
    }

    class LockSimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            int color = Lock.this.isLocked() ? mLockedBackgroundColor : mUnlockedBackgroundColor;
            double red = Color.red(color) * .75;
            double green = Color.green(color) * .75;
            double blue = Color.blue(color) * .75;

            //TODO:Make bg darker
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Lock.this.toggleLocked();
            return true;
        }
    }
}
