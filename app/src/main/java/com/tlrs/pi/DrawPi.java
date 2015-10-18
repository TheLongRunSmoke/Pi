package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by thelongrunsmoke.
 * Класс для вычисляет число Пи и визуализирует процесс.
 */
public class DrawPi {

    public int[] dots = new int[100];
    public int[] sizes = new int[2];
    public Context context;
    public Paint p;
    public int count = 0;

    public DrawPi(Context context) {
        this.context = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        sizes[0] = metrics.widthPixels;
        sizes[1] = metrics.heightPixels;
        p = new Paint();
    }

    public void firstFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        p.setStrokeWidth(5);
        // Тест
        canvas.drawLine(0,0,sizes[0],sizes[1],p);
    }

    public void nextFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        p.setStrokeWidth(10);
        canvas.drawPoint(5, count, p);
        count += 3;
    }
}
