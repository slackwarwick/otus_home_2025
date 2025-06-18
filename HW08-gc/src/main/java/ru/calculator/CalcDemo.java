package ru.calculator;

/*
Оптимальный размер хипа до оптимизации: 3GB
256m    11:40:14.518 [main] INFO ru.calculator.CalcDemo -- spend msec:48990, sec:48
512m    12:02:12.995 [main] INFO ru.calculator.CalcDemo -- spend msec:48151, sec:48
1024m   12:03:30.750 [main] INFO ru.calculator.CalcDemo -- spend msec:41606, sec:41
1536m   12:04:57.269 [main] INFO ru.calculator.CalcDemo -- spend msec:38606, sec:38
2048m   12:07:05.368 [main] INFO ru.calculator.CalcDemo -- spend msec:36722, sec:36
3072m   11:43:43.981 [main] INFO ru.calculator.CalcDemo -- spend msec:34631, sec:34
4096m   11:53:24.053 [main] INFO ru.calculator.CalcDemo -- spend msec:34294, sec:34
...
Оптимальный размер хипа после оптимизации: 128MB
16m     12:18:19.516 [main] INFO ru.calculator.CalcDemo -- spend msec:32631, sec:32
32m     12:16:37.242 [main] INFO ru.calculator.CalcDemo -- spend msec:31127, sec:31
64m     12:14:55.032 [main] INFO ru.calculator.CalcDemo -- spend msec:30309, sec:30
128m    12:20:26.581 [main] INFO ru.calculator.CalcDemo -- spend msec:29770, sec:29
256m    12:13:51.413 [main] INFO ru.calculator.CalcDemo -- spend msec:29856, sec:29
...
*/

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcDemo {
    private static final Logger log = LoggerFactory.getLogger(CalcDemo.class);

    public static void main(String[] args) {
        int counter = 100_000_000;
        var summator = new Summator();
        long startTime = System.currentTimeMillis();

        for (var idx = 0; idx < counter; idx++) {
            var data = new Data(idx);
            summator.calc(data);

            if (idx % 10_000_000 == 0) {
                log.info("{} current idx:{}", LocalDateTime.now(), idx);
            }
        }

        long delta = System.currentTimeMillis() - startTime;
        log.info("PrevValue:{}", summator.getPrevValue());
        log.info("PrevPrevValue:{}", summator.getPrevPrevValue());
        log.info("SumLastThreeValues:{}", summator.getSumLastThreeValues());
        log.info("SomeValue:{}", summator.getSomeValue());
        log.info("Sum:{}", summator.getSum());
        log.info("spend msec:{}, sec:{}", delta, (delta / 1000));
    }
}
