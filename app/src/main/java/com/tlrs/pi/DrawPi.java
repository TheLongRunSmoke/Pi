package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by thelongrunsmoke.
 * Класс для вычисления числа Пи и визуализации процесса.
 * Используется метод Монте-Карло.
 */
public class DrawPi {

    private double dotCount;
    private List<Float> dots = new ArrayList<>();
    private List<Float> dotsIn = new ArrayList<>();
    public Context context;
    private Paint p = new Paint();
    private RectF rectangle, arc;
    private int stroke;
    private Random random;
    private int width;
    private int margin;

    public DrawPi(Context context) {
        this.context = context;
        random = new Random();
        int[] sizes = new int[2];
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        sizes[0] = metrics.widthPixels;
        sizes[1] = metrics.heightPixels;
        // Ширина кисти. Двухсотая часть ширины экрана
        stroke = sizes[0]/200;
        // Размеры квадрата и отступ
        width = (int)(sizes[0] * 0.8);
        margin = (sizes[0] - width)/2;
        // Количество точек для репрезентативной выборки.
        // На самом деле точек будет в два раза меньше.
        dotCount = Math.pow(width, 2)/2;
        dotCount = (dotCount>=50000 ? dotCount : 50000);
        // Объект для построения квадрата
        rectangle = new RectF();
        rectangle.set(margin, margin, margin + width, margin + width);
        // Объект для построения дуги
        arc = new RectF();
        arc.set(margin-width, margin, margin + width, margin + 2 * width);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * Инициализация.
     * @param canvas - рабочая канва.
     */
    public void firstFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        drawCord(canvas);
    }

    /**
     * Отрисовка следующего кадра.
     * @param canvas - рабочая канва.
     * @return - возвратит true после заполнения массива точек.
     */
    public boolean nextFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        drawCord(canvas);
        for (int i=0;i<200;i++){
            float[] dot = {random.nextFloat() * width, random.nextFloat() * width};
                if ((Math.pow(dot[0], 2) + Math.pow(dot[1], 2)) <= (Math.pow(width, 2))) {
                    dotsIn.add(dot[0]);
                    dotsIn.add(width - dot[1]);
                } else {
                    dots.add(dot[0]);
                    dots.add(width - dot[1]);
                }
        }
        p.setColor(Color.BLUE);
        canvas.drawPoints(toPrimitive(dots), p);
        p.setColor(Color.RED);
        canvas.drawPoints(toPrimitive(dotsIn), p);
        canvas.drawText(Float.toString(((float) dotsIn.size() / (dotsIn.size() + dots.size())) * 4), margin, width + (2 * margin), p);
        canvas.drawText("3.1415926", margin, width+(3*margin), p);
        drawProgress(canvas, dots.size() + dotsIn.size(), dotCount);
        return dotsIn.size() + dots.size() > dotCount;
    }

    /**
     * Построение координатной плоскости.
     * @param canvas - рабочая канва.
     */
    private void drawCord(Canvas canvas){
        p.setStrokeWidth(stroke);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        canvas.drawRect(rectangle, p);
        p.setColor(ContextCompat.getColor(context, R.color.light_gray));
        canvas.drawArc(arc, 0, -90, false, p);
        p.setTextSize(20);
        canvas.drawText(Double.toString(dotCount), (float) 10, (float) 30, p);
    }

    /**
     * Лист в примитив. Замена недоступного ArrayUtils.toPrimitive
     * @param list - входной лист.
     * @return - примитив вида float[].
     */
    private float[] toPrimitive(List<Float> list){
        float[] result = new float[list.size()];
        int i = 0;
        for (Float f : list) {
            result[i++] = (f != null ? f + margin : margin); // Значения не должны быть null, но я пользуюсь эти снипетом.
        }
        return result;
    }

    /**
     * Рисует сектор отображающий текущий процесс.
     * @param canvas - рабочая канва.
     * @param current - текущее значение.
     * @param whole - общее значение.
     */
    private void drawProgress(Canvas canvas, float current, double whole){
        float deg = (float)360*(current/(float)whole);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        RectF prg = new RectF();
        prg.set(width-margin, width+2*margin, width+margin, width+4*margin);
        canvas.drawArc(prg, 0, deg, true, p);
    }
}
