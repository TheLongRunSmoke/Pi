package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.System.*;

class SurfaceDrawView extends SurfaceView implements SurfaceHolder.Callback {

    private static DrawThread drawThread;

    public SurfaceDrawView(Context context) {
        super(context);
        init();
    }

    public SurfaceDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurfaceDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        drawThread.onResume();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resume(){
        drawThread.onResume();
    }

    class DrawThread extends Thread implements Runnable {

        private boolean running = false;
        private final SurfaceHolder surfaceHolder;
        private boolean isReady = false;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (!isReady) {
                    while (running) {
                        canvas = null;
                        try {
                            canvas = surfaceHolder.lockCanvas(null);
                            if (canvas == null)
                                continue;

                            onDraw(canvas);
                            running = false;
                            FullscreenActivity.AUTO_HIDE = false;
                        } finally {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                        synchronized (surfaceHolder) {
                            if (!running) {
                                try {
                                    surfaceHolder.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                    }
                }
            }
        }

        void onDraw(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
        }

        public void onResume(){
            synchronized (surfaceHolder) {
                this.running = true;
                surfaceHolder.notifyAll();
            }
        }

    }

}