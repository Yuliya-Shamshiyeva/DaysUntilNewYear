package space.clevercake.daysuntilnewyear;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_day = "widget_text_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//скрываем нижнюю панель навигации
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//появляется и исчезает
        setContentView(R.layout.activity_main);

        loadBannerAd();



        Log.d(LOG_TAG, "onCreate config");//widget

        startCountdown();
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

    private void loadBannerAd() {
        // Create a new ad view.
        //////Реклама при открытииРеклама при открытии
        /////
        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-9702271696859992/4380179857");
//        adView.setAdUnitId("ca-app-pub-3940256099942544/2014213617");

        adView.setAdSize(getAdSize());
// Находим контейнер FrameLayout
        AdView addContainerView = findViewById(R.id.adView);
        // Replace ad container with new ad view.
        addContainerView.removeAllViews();
        addContainerView.addView(adView);

        AdRequest adRequest= new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        /////Реклама баннер
    }

    public AdSize getAdSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void startCountdown() {
        TextView daycount = findViewById(R.id.daycounter);
        TextView hourcount = findViewById(R.id.hourcounter);
        TextView mincount = findViewById(R.id.mincounter);
        TextView seccount = findViewById(R.id.seccounter);
        TextView newyertext = findViewById(R.id.newyertext);
        TextView newyertextNum = findViewById(R.id.textView9);

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);

        // Новый год: 1 января следующего года
        Calendar nextNY = Calendar.getInstance();
        nextNY.set(currentYear + 1, Calendar.JANUARY, 1, 0, 0, 0);
        newyertextNum.setText(String.valueOf(currentYear + 1));

        long timeToNY = nextNY.getTimeInMillis() - now.getTimeInMillis();

        CountDownTimer countDownTimer = new CountDownTimer(timeToNY, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                // если время вышло — показываем поздравление
                if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
                    newyertext.setText(R.string.happynewyear);
                } else {
                    newyertext.setText(R.string.newyertextplusyear);
                    daycount.setText(" " + days + " ");
                    hourcount.setText(" " + hours + " ");
                    mincount.setText(" " + minutes + " ");
                    seccount.setText(" " + seconds + " ");

                }
            }

            @Override
            public void onFinish() {
                // Показываем поздравление на 1 секунду
                newyertext.setText(R.string.happynewyear);
                // Запускаем новый таймер через секунду
                new android.os.Handler().postDelayed(() -> {
                    startCountdown(); // запускаем новый отсчёт до следующего года
                }, 1000);
            }
        };

        countDownTimer.start();
    }
}


