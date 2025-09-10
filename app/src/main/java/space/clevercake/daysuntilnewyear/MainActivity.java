package space.clevercake.daysuntilnewyear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ProcessLifecycleOwner;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.my.tracker.MyTracker;
import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;


import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_day = "widget_text_";

    private AppOpenAdLoader appOpenAdLoader;
    private AppOpenAd mAppOpenAd;
    private final String AD_UNIT_ID = "R-M-15755284-2"; // R-M-15755284-2 пока можно использовать демо
    private final String SDK_KEY = "NQn9p7RUNYNRJE35KGFN0t";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//скрываем нижнюю панель навигации
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//появляется и исчезает
        setContentView(R.layout.activity_main);
// Инициализируйте трекер vk
        MyTracker.initTracker(SDK_KEY, getApplication());
        MyTracker.trackLaunchManually(this);
        //Инициализация и загрузка рекламы яндекс старт
        com.yandex.mobile.ads.common.MobileAds.initialize(this, () -> {
            // Инициализация завершена — создаём загрузчик
            appOpenAdLoader = new AppOpenAdLoader(this);
            appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);

            // Загружаем рекламу
            AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();
            appOpenAdLoader.loadAd(adRequestConfiguration);
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                showAppOpenAd(); // показать рекламу, если доступна
            }
        });



        new Thread(
                () ->

                        // Initialize the Google Mobile Ads SDK on a background thread.
                        MobileAds.initialize(
                                this,
                                initializationStatus -> {
                                    Map<String, AdapterStatus> statusMap =
                                            initializationStatus.getAdapterStatusMap();
                                    for (String adapterClass : statusMap.keySet()) {
                                        AdapterStatus status = statusMap.get(adapterClass);
                                        Log.d(
                                                "MyApp",
                                                String.format(
                                                        "Adapter name: %s, Description: %s, Latency: %d",
                                                        adapterClass, status.getDescription(), status.getLatency()));
                                    }
                                    // Переключаемся в главный UI-поток
                                    runOnUiThread(() -> {
                                        loadBannerAd(); // Безопасно загружаем баннер
                                    });
                                }))
                .start();



        Log.d(LOG_TAG, "onCreate config");//widget
        showYandexBanner();

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

    //метод показа
    private void showAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.show(this);
            mAppOpenAd = null; // сбрасываем, чтобы повторно не показывать
            // можно сразу заново загружать следующую
            AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();
            appOpenAdLoader.loadAd(adRequestConfiguration);
        } else {
            Log.d(LOG_TAG, "AppOpenAd not ready");
        }
    }
    //слушатель загрузки
    private final AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
        @Override
        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
            Log.d(LOG_TAG, "AppOpenAd загружена");
            mAppOpenAd = appOpenAd;
        }

        @Override
        public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
            Log.e(LOG_TAG, "Ошибка загрузки AppOpenAd: " + adRequestError.getDescription());
        }
    };



    private void loadBannerAd() {
        AdView adView = findViewById(R.id.adView);

        // Устанавливаем adUnitId и размер только ОДИН раз, если они ещё не были установлены
        if (adView.getAdSize() == null) {
            adView.setAdUnitId("ca-app-pub-9702271696859992/4380179857");
            adView.setAdSize(getAdSize());
        }

        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
    private void showYandexBanner() {
        BannerAdView yandexAdView = new BannerAdView(this);
        yandexAdView.setAdUnitId("R-M-15755284-1");
        yandexAdView.setAdSize(BannerAdSize.stickySize(this, 320));

        LinearLayout layout = findViewById(R.id.yandex_ad_container); // добавь в xml
        layout.addView(yandexAdView);

        yandexAdView.loadAd(new AdRequest.Builder().build());
    }
}



