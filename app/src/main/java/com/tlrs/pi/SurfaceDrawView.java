package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

        private boolean isRunning = false;
        private final SurfaceHolder surfaceHolder;
        private boolean isReady = false;
        private long prevTime = 0;
        private static final int frameRate = 2;
        private boolean firstLoop = true;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            prevTime = System.currentTimeMillis();
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }

        private int loop = 0;

        @Override
        public void run() {

            Canvas canvas;
            while (!isReady) {
                    while (isRunning) {
                        synchronized (surfaceHolder) {
                            if (!isRunning) {
                                try {
                                    surfaceHolder.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Ограничение фреймрейта
                            long now = System.currentTimeMillis();
                            if ((now - prevTime) > (1000 / frameRate)) {
                                prevTime = now;
                                canvas = null;
                                try {
                                    canvas = surfaceHolder.lockCanvas(null);
                                    if (canvas == null)
                                        continue;
                                    // Отрисовка
                                    canvas.drawColor(Color.WHITE);
                                    onDraw(canvas);
                                    if (firstLoop){ firstLoop = false; isRunning = false; }
                                    if (loop >= 5) {
                                        isRunning = false;
                                        isReady = true;
                                    } else {
                                        loop++;
                                    }
                                } finally {
                                    if (canvas != null) {
                                        surfaceHolder.unlockCanvasAndPost(canvas);
                                    }
                                }
                            }
                    }
                }
            }
            Log.d("Thread", "isFinished");
            FullscreenActivity.AUTO_HIDE = false;
            FullscreenActivity.handler.sendEmptyMessage(0);
        }

        void onDraw(Canvas canvas) {
            //canvas.drawColor(Color.GREEN);
        }

        public void onResume(){
            synchronized (surfaceHolder) {
                this.isRunning = true;
                surfaceHolder.notifyAll();
            }
        }

    }

}