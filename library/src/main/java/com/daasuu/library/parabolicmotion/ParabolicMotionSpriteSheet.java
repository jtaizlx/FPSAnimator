package com.daasuu.library.parabolicmotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.daasuu.library.callback.AnimCallBack;
import com.daasuu.library.constant.Constant;
import com.daasuu.library.spritesheet.UpdatePositionListener;
import com.daasuu.library.util.Util;


public class ParabolicMotionSpriteSheet extends ParabolicMotion {

    private Bitmap mBitmap;

    private boolean mDpSize = false;

    private float mBitmapDpWidth;
    private float mBitmapDpHeight;
    private Rect mBitmapRect;


    private UpdatePositionListener mUpdatePositionListener;
    public float mFrameWidth;
    public float mFrameHeight;
    private int mFrameNum;
    private int mFrequency = 1;
    // The number of which frame, there is about line 1 of side
    private int mFrameNumPerLine;
    private boolean mSpriteLoop = true;


    public float dx = 0;
    public float dy = 0;
    public int currentPosition = Constant.DEFAULT_CURRENT_POSITION;
    private int mDrawingNum = Constant.DEFAULT_DRAWING_NUM;
    private AnimCallBack mSpriteSheetFinishCallback;

    public ParabolicMotionSpriteSheet(Bitmap bitmap, float frameWidth, float frameHeight, int frameNum, int frameNumPerLine) {
        this.mBitmap = bitmap;
        this.mFrameWidth = frameWidth;
        this.mFrameHeight = frameHeight;
        this.mFrameNum = frameNum;
        this.mFrameNumPerLine = frameNumPerLine;
    }

    public ParabolicMotionSpriteSheet dpSize(Context context) {
        mDpSize = true;

        mFrameWidth = Util.convertPixelsToDp(mFrameWidth, context);
        mFrameHeight = Util.convertPixelsToDp(mFrameHeight, context);

        mBitmapDpWidth = Util.convertPixelsToDp(mBitmap.getWidth(), context);
        mBitmapDpHeight = Util.convertPixelsToDp(mBitmap.getHeight(), context);
        mBitmapRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        return this;
    }

    public ParabolicMotionSpriteSheet transform(float x, float y) {
        mAnimParameter.x = x;
        mAnimParameter.y = y;
        return this;
    }

    public ParabolicMotionSpriteSheet frequency(int frequency) {
        mFrequency = frequency;
        return this;
    }

    public ParabolicMotionSpriteSheet spriteLoop(boolean loop) {
        mSpriteLoop = loop;
        return this;
    }

    public ParabolicMotionSpriteSheet initialVelocityY(float velocityY) {
        mInitialVelocityY = velocityY;
        return this;
    }

    public ParabolicMotionSpriteSheet accelerationY(float accelerationY) {
        mAccelerationY = accelerationY;
        return this;
    }

    public ParabolicMotionSpriteSheet accelerationX(float accelerationX) {
        mAccelerationX = accelerationX;
        return this;
    }

    public ParabolicMotionSpriteSheet coefficientRestitutionY(float coefficientRestitutionY) {
        mCoefficientRestitutionY = coefficientRestitutionY;
        return this;
    }

    public ParabolicMotionSpriteSheet coefficientRestitutionX(float coefficientRestitutionX) {
        mCoefficientRestitutionX = coefficientRestitutionX;
        return this;
    }

    public ParabolicMotionSpriteSheet coefficientBottom(boolean coefficientBottom) {
        mCoefficientBottom = coefficientBottom;
        return this;
    }

    public ParabolicMotionSpriteSheet coefficientLeft(boolean coefficientLeft) {
        mCoefficientLeft = coefficientLeft;
        return this;
    }

    public ParabolicMotionSpriteSheet coefficientRight(boolean coefficientRight) {
        mCoefficientRight = coefficientRight;
        return this;
    }

    public ParabolicMotionSpriteSheet bottomHitCallback(AnimCallBack animCallBack) {
        setBottomHitCallback(animCallBack);
        return this;
    }

    public ParabolicMotionSpriteSheet leftHitCallback(AnimCallBack animCallBack) {
        setLeftHitCallback(animCallBack);
        return this;
    }

    public ParabolicMotionSpriteSheet rightHitCallback(AnimCallBack animCallBack) {
        setRightHitCallback(animCallBack);
        return this;
    }

    public ParabolicMotionSpriteSheet spriteAnimationEndCallBack(AnimCallBack callBack) {
        mSpriteSheetFinishCallback = callBack;
        return this;
    }

    public ParabolicMotionSpriteSheet updatePositionListener(UpdatePositionListener callBack) {
        mUpdatePositionListener = callBack;
        return this;
    }

    private synchronized void updateSpritePosition() {
        if (mDrawingNum != mFrequency) {
            mDrawingNum++;
            return;
        }
        mDrawingNum = Constant.DEFAULT_DRAWING_NUM;

        if (mUpdatePositionListener != null) {
            mUpdatePositionListener.update(dx, dy, currentPosition);
            repeatPosition();
            return;
        }

        boolean edge = currentPosition % mFrameNumPerLine == 0;
        if (edge) {
            // 端の場合下に下がる
            dy -= mFrameHeight;
            dx = 0;
            currentPosition++;
            repeatPosition();
            return;
        }

        dx -= mFrameWidth;
        currentPosition++;
        repeatPosition();

    }

    private void repeatPosition() {
        if (currentPosition != mFrameNum) return;

        if (mSpriteLoop) {
            currentPosition = Constant.DEFAULT_CURRENT_POSITION;
            dx = 0;
            dy = 0;
        }

        if (mSpriteSheetFinishCallback != null) {
            mSpriteSheetFinishCallback.call();
        }

    }

    private void setBaseLength(Canvas canvas) {
        mBottomBase = canvas.getHeight() - mFrameHeight;
        mRightSide = canvas.getWidth() - mFrameWidth;
    }


    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null) return;
        setBaseLength(canvas);

        updatePosition();
        canvas.save();
        RectF bounds = new RectF(
                mAnimParameter.x,
                mAnimParameter.y,
                mAnimParameter.x + mFrameWidth,
                mAnimParameter.y + mFrameHeight
        );
        canvas.saveLayer(bounds, null, Canvas.ALL_SAVE_FLAG);
        updateSpritePosition();

        if (mDpSize) {
            RectF dpSizeRect = new RectF(
                    mAnimParameter.x + dx,
                    mAnimParameter.y + dy,
                    mAnimParameter.x + dx + mBitmapDpWidth,
                    mAnimParameter.y + dy + mBitmapDpHeight
            );
            canvas.drawBitmap(mBitmap, mBitmapRect, dpSizeRect, mPaint);
        } else {
            canvas.drawBitmap(mBitmap, mAnimParameter.x + dx, mAnimParameter.y + dy, mPaint);
        }
        canvas.restore();
    }

}
