package com.android.smartlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Lock extends View {

    private static final int DEFAULT_LOCKED_COLOR = 0xfff50057;
    private static final int DEFAULT_UNLOCKED_COLOR = 0xff76ff03;
    private static final int FPS = 1000 / 60;
    private static final boolean DEFAULT_LOCK_STATUS = false;


    private int mLockedBackgroundColor = DEFAULT_LOCKED_COLOR;
    private int mUnlockedBackgroundColor = DEFAULT_UNLOCKED_COLOR;
    private boolean mLocked = DEFAULT_LOCK_STATUS;

    private Paint mLockPaint;
    private Paint mLockedBackgroundPaint;
    private Paint mUnlockedBackgroundPaint;
    private Paint mShadowPaint;

    private GestureDetector mGestureDetector;

    private boolean down = false;
    private int downCount = 0;

    private LockDimensions mLockDimensions = new LockDimensions();

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
        }
    }

    private boolean isDown() {
        if (down && downCount < 30) {
            downCount++;
            postInvalidateDelayed(FPS);
            return false;
        }
        return down;
    }

    private void setDown(boolean newDown) {
        this.down = newDown;
        if (!newDown) {
            mLockDimensions.tintSize = 0;
            downCount = 0;
        }
        invalidate();
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean mLocked) {
        boolean viewNeedsUpdating = mLocked != this.mLocked;
        this.mLocked = mLocked;
        if (viewNeedsUpdating) {
            invalidate();
        }
    }

    public void toggleLocked() {
        this.mLocked = !this.mLocked;
        invalidate();
    }

    private void init() {
        mLockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnlockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mLockPaint.setColor(0xff424242);
        mLockedBackgroundPaint.setColor(mLockedBackgroundColor);
        mUnlockedBackgroundPaint.setColor(mUnlockedBackgroundColor);
        mShadowPaint.setColor(0x66000000);

        mLockPaint.setStyle(Paint.Style.STROKE);
        mLockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mUnlockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mGestureDetector = new GestureDetector(getContext(), new LockSimpleGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        mLockDimensions.size = size;
        mLockDimensions.update();
        setMeasuredDimension(size, size);
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mLockDimensions.circleX, mLockDimensions.circleY, mLockDimensions.circleRadius, isLocked() ? mLockedBackgroundPaint : mUnlockedBackgroundPaint);
        canvas.drawRoundRect(mLockDimensions.lockLeftX, mLockDimensions.lockTopY, mLockDimensions.lockRightX, mLockDimensions.lockBottomY, mLockDimensions.strokeWidth, mLockDimensions.strokeWidth, mLockPaint);
        canvas.drawCircle(mLockDimensions.keyHoleX, mLockDimensions.keyHoleY, mLockDimensions.keyHoleRadius, mLockPaint);
        canvas.drawLine(mLockDimensions.handleSideLeftX, mLockDimensions.handleSideBottomY, mLockDimensions.handleSideLeftX, mLockDimensions.handleSideTopY, mLockPaint);
        canvas.drawLine(mLockDimensions.handleSideRightX, mLockDimensions.handleSideBottomY, mLockDimensions.handleSideRightX, mLockDimensions.handleSideTopY, mLockPaint);
        canvas.drawArc(mLockDimensions.handleArcLeftX, mLockDimensions.handleArcTopY, mLockDimensions.handleArcRightX, mLockDimensions.handleArcBottomY, 180f, 180f, false, mLockPaint);
        canvas.drawCircle(mLockDimensions.circleX, mLockDimensions.circleY, mLockDimensions.tintSize, mShadowPaint);

        if (isDown()) {
            if (mLockDimensions.tintSize < mLockDimensions.circleRadius) {
                mLockDimensions.tintSize += 3000 / (mLockDimensions.circleRadius - mLockDimensions.tintSize);
            }
            if (mLockDimensions.tintSize != mLockDimensions.circleRadius) {
                if (mLockDimensions.tintSize > mLockDimensions.circleRadius) {
                    mLockDimensions.tintSize = mLockDimensions.circleRadius;
                }
                postInvalidateDelayed(FPS);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                setDown(false);
                result = true;
            }
        }
        return result;
    }

    class LockSimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            setDown(true);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Lock.this.toggleLocked();
            return true;
        }
    }

    private class LockDimensions {

        int size;

        float centerX;
        float centerY;
        float strokeWidth;

        float lockHeight;
        float lockWidth;
        float lockLeftX;
        float lockRightX;
        float lockTopY;
        float lockBottomY;

        float keyHoleX;
        float keyHoleY;
        float keyHoleRadius;

        float handleSideLength;
        float handleSideBottomY;
        float handleSideTopY;
        float handleSideLeftX;
        float handleSideRightX;

        float handleArcLength;
        float handleArcLeftX;
        float handleArcRightX;
        float handleArcBottomY;
        float handleArcTopY;

        float circleRadius;
        float circleX;
        float circleY;

        float tintSize;

        public void update() {
            centerX = size / 2.0f;
            centerY = size / 2.0f;
            strokeWidth = size * 0.02f;

            lockHeight = size * 0.5f;
            lockWidth = lockHeight * 2.0f / 3.0f;
            lockLeftX = centerX - lockWidth / 2.0f;
            lockRightX = centerX + lockWidth / 2.0f;
            lockTopY = centerY - size * 0.075f;
            lockBottomY = lockTopY + lockHeight / 2.0f;

            keyHoleX = centerX;
            keyHoleY = (lockTopY + lockBottomY) / 2.0f;
            keyHoleRadius = lockWidth * 0.1f;

            handleSideLength = size * 0.1f;
            handleSideBottomY = lockTopY;
            handleSideTopY = lockTopY - handleSideLength;
            handleSideLeftX = centerX - lockWidth * 0.25f;
            handleSideRightX = centerX + lockWidth * 0.25f;

            handleArcLength = size * .075f;
            handleArcLeftX = handleSideLeftX;
            handleArcRightX = handleSideRightX;
            handleArcBottomY = handleSideTopY + handleArcLength;
            handleArcTopY = handleSideTopY - handleArcLength;

            circleRadius = size / 2.0f;
            circleX = centerX;
            circleY = centerY;

            mLockPaint.setStrokeWidth(mLockDimensions.strokeWidth);
        }
    }
}
