package space.clevercake.daysuntilnewyear;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;



public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_day = "widget_text_";
    //////Реклама при открытииРеклама при открытии
    /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//скрываем нижнюю панель навигации
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//появляется и исчезает
        setContentView(R.layout.activity_main);
        /////Реклама баннер
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        /////Реклама баннер


        Log.d(LOG_TAG, "onCreate config");//widget


        //Добавляем для считывания наши текст вью
        TextView daycount = findViewById(R.id.daycounter);
        TextView hourcount = findViewById(R.id.hourcounter);
        TextView mincount = findViewById(R.id.mincounter);
        TextView seccount = findViewById(R.id.seccounter);

        //Calendar позволяет преобразовать время в миллисекундах в более удобном виде - год, месяц, день, часы, минуты, секунды.
        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calendar = Calendar.getInstance();

        end_calendar.set(2025,0,1,0,0,0);//заканчивается календарь 1 января 2024


        long todayTime = start_calendar.getTimeInMillis();
        long nyTime = end_calendar.getTimeInMillis();
        long countdowntoNY = nyTime-todayTime;

        CountDownTimer countDownTimer = new CountDownTimer(countdowntoNY,1000) {//1000 = 1 секунда
            @Override
            public void onTick(long l) {
                long days = TimeUnit.MILLISECONDS.toDays(l);//целочисленный тип содержащий практически бесконечное количество значений. Используется в случаях, где числа превосходят 2 миллиарда и стандартного int уже не хватает. Используется в повседневной жизни для создания уникальных значений.
                l -= TimeUnit.DAYS.toMillis(days);

                long hour = TimeUnit.MILLISECONDS.toHours(l);
                l -= TimeUnit.HOURS.toMillis(hour);

                long min = TimeUnit.MILLISECONDS.toMinutes(l);
                l -= TimeUnit.MINUTES.toMillis(min);

                long sec = TimeUnit.MILLISECONDS.toSeconds(l);

                //вывод на экране
                daycount.setText(" "+ days +" ");
                hourcount.setText(" "+ hour +" ");
                mincount.setText(" "+ min +" ");
                seccount.setText(" "+ sec +" ");


            }
            //когда будет нг вывести надпись с новым годом
            @Override
            public void onFinish() {
                daycount.setText(" ");
                hourcount.setText(" ");
                mincount.setText(" ");
                seccount.setText(" ");


            }
        };
        countDownTimer.start();//запуск класса
        View snow = findViewById(R.id.snow);
        snow.setVisibility(View.INVISIBLE);
        final int[] x = {0};

        TextView btnsnow = (TextView) findViewById(R.id.textView3);
        btnsnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (x[0] == 0){
                    snow.setVisibility(View.VISIBLE);
                    x[0] = 1;
                }else{
                    snow.setVisibility(View.INVISIBLE);
                    x[0] = 0;
                }

            }
        });



    }


}


