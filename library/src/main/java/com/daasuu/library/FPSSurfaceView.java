package com.daasuu.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.daasuu.library.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * It recommended better to use the FPSTextureView.
 */
public class FPSSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Timer mTimer;
    private long mFps = Constant.DEFAULT_FPS;

    private SurfaceHolder mSurfaceHolder;

    private List<DisplayObject> mDisplayObjectList = new ArrayList<>();

    public FPSSurfaceView(Context context) {
        this(context, null, 0);
    }

    public FPSSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FPSSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

    }


    public FPSSurfaceView tickStart() {
        stop();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTick();
            }
        }, 0, 1000 / mFps);
        return this;
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // do nothing
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    private void onTick() {

        synchronized (this) {

            List<DisplayObject> copyDisplayObjectList = new ArrayList<DisplayObject>(mDisplayObjectList);

            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (canvas == null) return;

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            for (DisplayObject displayObject : copyDisplayObjectList) {
                if (displayObject == null) {
                    continue;
                }
                displayObject.draw(canvas);
            }

            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    public FPSSurfaceView addChild(@NonNull DisplayObject displayObject) {
        displayObject.setUp(mFps);
        mDisplayObjectList.add(displayObject);
        return this;
    }

    public FPSSurfaceView removeChild(@NonNull DisplayObject displayObject) {
        mDisplayObjectList.remove(displayObject);
        return this;
    }

    public FPSSurfaceView setFps(long fps) {
        this.mFps = fps;
        return this;
    }

    public List<DisplayObject> getAnimList() {
        return mDisplayObjectList;
    }
}
