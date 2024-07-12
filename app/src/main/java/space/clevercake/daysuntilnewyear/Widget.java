package space.clevercake.daysuntilnewyear;



import static android.app.PendingIntent.getActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Widget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //Calendar позволяет преобразовать время в миллисекундах в более удобном виде - год, месяц, день, часы, минуты, секунды.
        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calendar = Calendar.getInstance();

        end_calendar.set(2025, 0, 1, 0, 0, 0);


        long todayTime = start_calendar.getTimeInMillis();
        long nyTime = end_calendar.getTimeInMillis();
        long countdowntoNY = nyTime - todayTime;

        CountDownTimer countDownTimer = new CountDownTimer(countdowntoNY, 1000) {//1000 = 1 секунда
            @Override
            public void onTick(long l) {
                long days = TimeUnit.MILLISECONDS.toDays(l);//целочисленный тип содержащий практически бесконечное количество значений. Используется в случаях, где числа превосходят 2 миллиарда и стандартного int уже не хватает. Используется в повседневной жизни для создания уникальных значений.
                l -= TimeUnit.DAYS.toMillis(days);

                long hour = TimeUnit.MILLISECONDS.toHours(l);
                l -= TimeUnit.HOURS.toMillis(hour);

                long min = TimeUnit.MILLISECONDS.toMinutes(l);
                l -= TimeUnit.MINUTES.toMillis(min);

                long sec = TimeUnit.MILLISECONDS.toSeconds(l);





                /* Progress Bar */
                int max = 365;
                int progress = (int) (365 - days);

                final int N = appWidgetIds.length;

                for (int i = 0; i < N; i++) {
                    int appWidgetId = appWidgetIds[i];

                 //Intent intent = new Intent(context, MainActivity.class);
                   // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                    views.setTextViewText(R.id.days, "" + days);
                    views.setTextViewText(R.id.hours,  hour+"h");

                    views.setProgressBar(R.id.progress_bar, max, progress, false);

                    appWidgetManager.updateAppWidget(appWidgetId, views);


                    //Для перехода
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Установка PendingIntent на корневой элемент
                    views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }



            }

            //когда будет нг вывести надпись с новым годом
            @Override
            public void onFinish() {


            }
        };
        countDownTimer.start();//запуск класса

    }

}



