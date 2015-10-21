package com.tlrs.pi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tlrs.pi.util.SystemUiHider;

import java.lang.ref.WeakReference;

public class FullscreenActivity extends Activity {

    public static boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    static Handler handler; // Да, вот просто так.

    public static boolean doNotShowUI = false;

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
                        if (!doNotShowUI) {
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                            if (visible && AUTO_HIDE) { // Избыточность проверки для совместимости с автоскрытием.
                                // Добавляю задержку скрытия
                                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                            }
                        }else{
                            controlsView.setVisibility(View.GONE);  // Делаю так для читабельности кода
                        }
                    }
                });

        // Фикс бага с размерами SurfaceView. Высота была меньше нужной на высоту статусбара, оверлей не подходит.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        lp.width = dm.widthPixels;
        lp.height = dm.heightPixels;
        surfaceView.setLayoutParams(lp);

        // Показ и скрытие UI по клику.
        surfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (AUTO_HIDE) {
                        mSystemUiHider.toggle();
                    } else {
                        mSystemUiHider.show();
                    }
            }
        });

        // Хэндлер для связи с DrawThread.
        handler = new Handler(new Handler.Callback(){
            //  Сделаю слабую ссылку для вьюшки. Не очень надо, но пусть будет.
            private WeakReference<View> wControlView = new WeakReference<>(findViewById(R.id.fullscreen_content_controls));

            @Override
            public boolean handleMessage(Message msg){
                wControlView.get().setVisibility(View.VISIBLE);
                doNotShowUI = false;
                return true;
            }
        });

        // TouchListener для взаимодействия с UI. Будет определён дальше.
        findViewById(R.id.dummy_button).setOnClickListener(mDelayHideClickListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /**
     *  TouchListener обеспечивающий взаимодействие UI.
     */
    OnClickListener mDelayHideClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AUTO_HIDE = true;
            doNotShowUI = true;
            SurfaceDrawView.handler.sendEmptyMessage(0);
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
