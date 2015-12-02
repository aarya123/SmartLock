package com.android.smartlock.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.android.smartlock.R;

public class Lock extends View {

    private static final int DEFAULT_LOCKED_COLOR = 0xfff50057;
    private static final int DEFAULT_UNLOCKED_COLOR = 0xff76ff03;
    private static final int DEFAULT_DISABLED_COLOR = 0xff9e9e9e;
    private static final int FPS = 1000 / 60;
    private static final boolean DEFAULT_LOCK_STATUS = true;
    private static final boolean DEFAULT_ENABLED_STATUS = true;

    private int mLockedBackgroundColor = DEFAULT_LOCKED_COLOR;
    private int mUnlockedBackgroundColor = DEFAULT_UNLOCKED_COLOR;
    private int mDisabledBackgroundColor = DEFAULT_DISABLED_COLOR;
    private boolean mLocked = DEFAULT_LOCK_STATUS;
    private boolean mEnabled = DEFAULT_ENABLED_STATUS;

    private Paint mLockPaint;
    private Paint mLockedBackgroundPaint;
    private Paint mUnlockedBackgroundPaint;
    private Paint mDisabledBackgroundPaint;
    private Paint mShadowPaint;

    private GestureDetector mGestureDetector;

    private boolean mDown = false;
    private int mDownCount = 0;
    private boolean mAnimateLock = false;

    private LockDimensions mLockDimensions = new LockDimensions();

    private Interpolator mShadowInterpolator;
    private Interpolator mAnimationInterpolator;

    private OnClickListener mOnClickListener;

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
            mDisabledBackgroundColor = typedArray.getColor(R.styleable.Lock_disabledBackgroundColor, DEFAULT_DISABLED_COLOR);
            mLocked = typedArray.getBoolean(R.styleable.Lock_locked, DEFAULT_LOCK_STATUS);
            mEnabled = typedArray.getBoolean(R.styleable.Lock_enabled, DEFAULT_LOCK_STATUS);
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
            invalidateLock();
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
            invalidateLock();
        }
    }

    public int getDisabledColor() {
        return mDisabledBackgroundColor;
    }

    public void setDisabledColor(int mDisabledColor) {
        boolean viewNeedsUpdating = mDisabledColor != this.mDisabledBackgroundColor;
        this.mDisabledBackgroundColor = mDisabledColor;
        mDisabledBackgroundPaint.setColor(mDisabledBackgroundColor);
        if (viewNeedsUpdating) {
            invalidateLock();
        }
    }

    private boolean isDown() {
        if (mDown && mDownCount < 30) {
            mDownCount++;
            postInvalidateDelayed(FPS);
            return false;
        }
        return mDown;
    }

    private void setDown(boolean newDown) {
        this.mDown = newDown;
        if (!newDown) {
            mLockDimensions.tintSize = 0;
            mDownCount = 0;
            mShadowInterpolator = null;
        }
        invalidateLock();
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean mLocked) {
        boolean viewNeedsUpdating = mLocked != this.mLocked;
        this.mLocked = mLocked;
        if (viewNeedsUpdating) {
            mAnimationInterpolator = null;
            mAnimateLock = true;
            invalidateLock();
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean mEnabled) {
        boolean viewNeedsUpdating = mEnabled != this.mEnabled;
        this.mEnabled = mEnabled;
        if (viewNeedsUpdating) {
            invalidateLock();
        }
    }

    public void toggleLocked() {
        setLocked(!isLocked());
    }

    private void init() {
        mLockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnlockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDisabledBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mLockPaint.setColor(0xff424242);
        mLockedBackgroundPaint.setColor(mLockedBackgroundColor);
        mUnlockedBackgroundPaint.setColor(mUnlockedBackgroundColor);
        mDisabledBackgroundPaint.setColor(mDisabledBackgroundColor);
        mShadowPaint.setColor(0x66000000);

        mLockPaint.setStyle(Paint.Style.STROKE);
        mLockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mUnlockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mDisabledBackgroundPaint.setStyle(Paint.Style.FILL);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mLockDimensions.circleX, mLockDimensions.circleY, mLockDimensions.circleRadius, isEnabled() ? isLocked() ? mLockedBackgroundPaint : mUnlockedBackgroundPaint : mDisabledBackgroundPaint);
        canvas.drawRoundRect(mLockDimensions.lockLeftX, mLockDimensions.lockTopY, mLockDimensions.lockRightX, mLockDimensions.lockBottomY, mLockDimensions.strokeWidth, mLockDimensions.strokeWidth, mLockPaint);
        canvas.drawCircle(mLockDimensions.keyHoleX, mLockDimensions.keyHoleY, mLockDimensions.keyHoleRadius, mLockPaint);
        canvas.drawLine(mLockDimensions.handleSideLeftX, mLockDimensions.handleLeftSideBottomY, mLockDimensions.handleSideLeftX, mLockDimensions.handleSideTopY, mLockPaint);
        canvas.drawLine(mLockDimensions.handleSideRightX, mLockDimensions.handleRightSideBottomY, mLockDimensions.handleSideRightX, mLockDimensions.handleSideTopY, mLockPaint);
        canvas.drawArc(mLockDimensions.handleArcLeftX, mLockDimensions.handleArcTopY, mLockDimensions.handleArcRightX, mLockDimensions.handleArcBottomY, 180f, 180f, false, mLockPaint);
        canvas.drawCircle(mLockDimensions.circleX, mLockDimensions.circleY, mLockDimensions.tintSize, mShadowPaint);

        if (isDown()) {
            if (mShadowInterpolator == null) {
                mShadowInterpolator = new Interpolator();
            }
            mLockDimensions.tintSize = mShadowInterpolator.getInterpolation() * mLockDimensions.circleRadius;
            if (mLockDimensions.tintSize < mLockDimensions.circleRadius) {
                postInvalidateDelayed(FPS);
            }
        } else if (mAnimateLock) {
            if (mAnimationInterpolator == null) {
                mAnimationInterpolator = new Interpolator();
            }
            if (isLocked()) {
                mLockDimensions.animateHandle = (1 - mAnimationInterpolator.getInterpolation()) * mLockDimensions.handleRightSideLength;
                mLockDimensions.update();
                if (mLockDimensions.animateHandle > 0) {
                    postInvalidateDelayed(FPS);
                } else {
                    mAnimateLock = false;
                }
            } else {
                mLockDimensions.animateHandle = mAnimationInterpolator.getInterpolation() * mLockDimensions.handleRightSideLength;
                mLockDimensions.update();
                if (mLockDimensions.animateHandle < mLockDimensions.handleRightSideLength * 2.0) {
                    postInvalidateDelayed(FPS);
                } else {
                    mAnimateLock = false;
                }
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            boolean result = mGestureDetector.onTouchEvent(event);
            if (!result) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setDown(false);
                    result = true;
                }
            }
            return result;
        } else {
            return false;
        }
    }

    public OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    private void invalidateLock() {
        try {
            if (Looper.myLooper().equals(Looper.getMainLooper())) {
                invalidate();
            } else {
                postInvalidate();
            }
        } catch (NullPointerException e) {
            postInvalidate();
        }

    }

    private class LockSimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            setDown(true);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(Lock.this);
            }
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

        float handleLeftSideLength;
        float handleRightSideLength;
        float handleLeftSideBottomY;
        float handleRightSideBottomY;
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
        float animateHandle;

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

            handleRightSideLength = size * 0.1f;
            handleLeftSideLength = handleRightSideLength + animateHandle;
            handleLeftSideBottomY = lockTopY;
            handleRightSideBottomY = lockTopY - animateHandle;
            handleSideTopY = lockTopY - handleLeftSideLength;
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

    private class Interpolator {
        private long start;

        public Interpolator() {
            start = System.currentTimeMillis();
        }

        public float getInterpolation() {
            return (float) (Math.cos((getTime() + 1) * Math.PI) / 2.0f) + 0.5f;

        }

        private float getTime() {
            float time = (System.currentTimeMillis() - start) / 1000.0f;
            if (time > 1) {
                time = 1;
            }
            return time;
        }
    }
}
