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
    private Context context;

    public SurfaceDrawView(Context context) {
        super(context);
        init(context);
    }

    public SurfaceDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SurfaceDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context){
        this.context = context;
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

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            prevTime = System.currentTimeMillis();
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            Canvas canvas;
            DrawPi draw = new DrawPi(context);
            boolean firstLoop = true;
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
                                    if (firstLoop){
                                        // Инициализация при запуске приложения
                                        draw.firstFrame(canvas);
                                        firstLoop = false;
                                        isRunning = false;
                                    }else{
                                        // Рисую следующий кадр
                                        draw.nextFrame(canvas);
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
            FullscreenActivity.AUTO_HIDE = false;
            FullscreenActivity.handler.sendEmptyMessage(0);
        }

        public void onResume(){
            synchronized (surfaceHolder) {
                this.isRunning = true;
                surfaceHolder.notifyAll();
            }
        }

    }

}