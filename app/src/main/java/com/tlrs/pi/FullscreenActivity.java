package com.tlrs.pi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;

import com.tlrs.pi.util.SystemUiHider;

public class FullscreenActivity extends Activity {

    public static boolean AUTO_HIDE = false;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final boolean TOGGLE_ON_CLICK = true;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View surfaceView = findViewById(R.id.surfaceView);

        // Создаю инстанс SystemUiHider, чтобы контролировать UI этой активити.
        mSystemUiHider = SystemUiHider.getInstance(this, surfaceView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

                    @Override
                    public void onVisibilityChange(boolean visible) {
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        if (visible && AUTO_HIDE) { // Избыточность проверки для совместимости с автоскрытием.
                            // Добавляю задержку скрытия
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Показ и скрытие UI по клику.
        surfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // TouchListener для скрытия UI. Будет определён дальше.
        findViewById(R.id.dummy_button).setOnClickListener(mDelayHideClickListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    /**
     *  TouchListener обеспечивающий скрытие UI.
     */
    OnClickListener mDelayHideClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AUTO_HIDE = true;
            SurfaceDrawView.resume();
            // После взаимодействия, быстро скрываю интерфейс.
            delayedHide(100);
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     *  Обеспечение задержки колбэков скрытия UI.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
