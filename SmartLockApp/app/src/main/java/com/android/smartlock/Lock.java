package com.android.smartlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

        Rect size = canvas.getClipBounds();
        int width = Math.min(size.width(), size.height());
        int height = width;
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        float lockHeight = height * 0.5f;
        float lockWidth = lockHeight * 2.0f / 3.0f;
        float lockLeftX = centerX - lockWidth / 2.0f;
        float lockRightX = centerX + lockWidth / 2.0f;
        float lockTopY = centerY;
        float lockBottomY = centerY + lockHeight / 2.0f;

        float keyHoleX = centerX;
        float keyHoleY = (lockTopY + lockBottomY) / 2.0f;
        float keyHoleRadius = lockWidth * 0.1f;

        float handleSideLength = height * 0.1f;
        float handleSideBottomY = centerY;
        float handleSideTopY = centerY - handleSideLength;
        float handleSideLeftX = centerX - lockWidth * 0.25f;
        float handleSideRightX = centerX + lockWidth * 0.25f;

        float handleArcLength = height * .075f;
        float handleArcLeftX = handleSideLeftX;
        float handleArcRightX = handleSideRightX;
        float handleArcBottomY = handleSideTopY + handleArcLength;
        float handleArcTopY = handleSideTopY - handleArcLength;

        float circleRadius = height * .675f / 2.0f;
        float circleX = centerX;
        float circleY = lockTopY + ((lockBottomY - lockTopY) * 0.25f);

        canvas.drawCircle(circleX, circleY, circleRadius, isLocked() ? mLockedBackgroundPaint : mUnlockedBackgroundPaint);
        canvas.drawRoundRect(lockLeftX, lockTopY, lockRightX, lockBottomY, 10, 10, mLockPaint);
        canvas.drawCircle(keyHoleX, keyHoleY, keyHoleRadius, mLockPaint);
        canvas.drawLine(handleSideLeftX, handleSideBottomY, handleSideLeftX, handleSideTopY, mLockPaint);
        canvas.drawLine(handleSideRightX, handleSideBottomY, handleSideRightX, handleSideTopY, mLockPaint);
        canvas.drawArc(handleArcLeftX, handleArcTopY, handleArcRightX, handleArcBottomY, 180f, 180f, false, mLockPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    class LockSimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Lock.this.toggleLocked();
            return true;
        }
    }
}
