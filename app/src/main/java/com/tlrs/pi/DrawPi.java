package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

/**
 * Created by thelongrunsmoke.
 * Класс для вычисления числа Пи и визуализации процесса.
 */
public class DrawPi {

    //public int[] dots = new int[100];
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

    public boolean nextFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        p.setStrokeWidth(10);
        p.setColor(Color.RED);
        canvas.drawLine(0, 90, sizes[0], 90, p);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        canvas.drawPoint(5, count, p);
        count += 3;
        return count > 90;
    }
}
