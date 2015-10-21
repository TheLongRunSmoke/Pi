# Pi

Демонстрационное android-приложение. Вычисляет значение числа Пи методом Монте-Карло.

[Загрузить apk.](https://github.com/TheLongRunSmoke/Pi/raw/master/app-release.apk)

<img src="https://github.com/TheLongRunSmoke/Pi/raw/master/screenshot.png" width="300">

## Содержит следующие элементы реализации:

- Полноэкранное приложение, со скрытием StatusBar.

- Кастомный SurfaceView.

- Thread в SurfaceView содержит имплементацию Runnable, что позволяет управлять процессом выполнения.

- Для связи UI и асинхронного потока применены Handler'ы.

- Пример использования слабых ссылок.

- Процессы вычисления и визуализации в отдельном классе.

В коде много комментариев. Кроме того, я нарисовал материальную иконку.


## Проблемы и решения:

А. Невысокая точность вычисления. Статистически, 2–3 знака после запятой.

   Решения:

   - Использование BigDecimal, вместо float, для хранения и обработки координат точек, это увеличит точность, но потребует значительно больше памяти и скажется на производительности.

   - Замена Random на более качественный источник энтропии.

Б. onPause(), во время вычисления, нарушает работу.

   Решение:

   - Написать реализацию событий onPause() и onResume().
