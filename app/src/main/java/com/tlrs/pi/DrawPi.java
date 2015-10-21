package com.tlrs.pi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
     *
     * Точность вычислений можно значительно повысить заменив float на BigDecimals,
     * но это потребует больше памяти и скажется на скорости работы, хотя позволит
     * уменьшить количество необходимых точек.
     *
     * @param canvas - рабочая канва.
     * @return - возвратит true после заполнения массива точек.
     */
    public boolean nextFrame(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        drawCord(canvas);
        for (int i=0;i<200;i++){
            // Использую nextInt вместо nextFloat, это дает лучшее качество чисел и позволяет покрыть весь диапазон.
            float[] dot = {(random.nextInt(1000001)/(float)1000000) * width, (random.nextInt(1000001)/(float)1000000) * width};
                if ((Math.pow(dot[0], 2) + Math.pow(dot[1], 2)) <= (Math.pow(width, 2))) {
                    dotsIn.add(dot[0]);
                    dotsIn.add(width - dot[1]);
                } else {
                    dots.add(dot[0]);
                    dots.add(width - dot[1]);
                }
        }
        p.setStrokeWidth(stroke);
        p.setColor(Color.BLUE);
        canvas.drawPoints(toPrimitive(dots), p);
        p.setColor(Color.RED);
        canvas.drawPoints(toPrimitive(dotsIn), p);
        p.setTextAlign(Paint.Align.LEFT);
        p.setColor(ContextCompat.getColor(context, R.color.base_gray));
        p.setStrokeWidth(stroke / 2);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawText(String.format(Locale.ENGLISH, "%.7f", ((float) dotsIn.size() / (dotsIn.size() + dots.size())) * 4)+" - вычисленное значение", margin, width + (2 * margin), p);
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
        canvas.drawArc(arc, 0, -90, false, p);
        canvas.drawLine(margin, margin / 2, margin, margin * (float) 1.5 + width, p);
        canvas.drawLine(margin, margin / 2, margin - (2 * stroke), margin / 2 + (8 * stroke), p);
        canvas.drawLine(margin, margin / 2, margin + (2 * stroke), margin / 2 + (8 * stroke), p);
        canvas.drawLine(margin / 2, margin + width, margin * (float) 1.5 + width, margin + width, p);
        canvas.drawLine(margin*(float)1.5 + width, margin + width, margin*(float)1.5+width-(8*stroke), margin+width-(2*stroke), p);
        canvas.drawLine(margin*(float)1.5 + width, margin + width, margin*(float)1.5+width-(8*stroke), margin+width+(2*stroke), p);
        p.setTextSize(10 * stroke);
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("X", margin - (5 * stroke), margin / 2 + (5 * stroke), p);
        canvas.drawText("Y", margin*(float)1.5+width, margin+width+(12*stroke), p);
        canvas.drawText("0", margin-(5*stroke), margin+width+(12*stroke), p);
        p.setStrokeWidth(stroke / 2);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("3.1415927 - число π", margin, width+(3*margin), p);
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
