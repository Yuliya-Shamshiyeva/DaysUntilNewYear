package space.clevercake.daysuntilnewyear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

public class Splash_Screen extends AppCompatActivity {
    TextView txtView;
    private AppOpenManager appOpenManager;
    private CountDownTimer countDownTimer;
    private ProgressBar adsLoaderPbar;

    private ConsentInformation consentInformation;
    private final String TAG = "Splash_Screen";
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //txtView = findViewById(R.id.txtApp_Name);
        adsLoaderPbar = findViewById(R.id.adsloader);

//        MobileAds.initialize(Splash_Screen.this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//
//            }
//        });
        showConsentForm();

        if (isInternetAvailable()) {

            appOpenManager = new AppOpenManager(Splash_Screen.this);
            appOpenManager.fetchAd(getResources().getString(R.string.app_open_id));

            countDownTimer = new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    if (AppOpenManager.adsisLoaded()) {
                        adsLoaderPbar.setVisibility(View.GONE);
                        appOpenManager.showAdIfAvailable();
                        countDownTimer.cancel();
                        Log.d("mmmm","ads is show");
                    }

                }

                public void onFinish() {
//                    txtTitle.setText("done!");

                    if (!AppOpenManager.adsisLoaded()) {
                        intentToHomeScreen();
                        adsLoaderPbar.setVisibility(View.GONE);
                    }

                }

            }.start();

        }else {
//            countDownTimer.cancel();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    intentToHomeScreen();
                    adsLoaderPbar.setVisibility(View.GONE);
                }
            },3000);
        }


    }



    public void intentToHomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 400);
    }

    public void stopCountdown(){
        countDownTimer.cancel();
        Log.d("mmmm","stop countdown");
    }




    private void showConsentForm() {
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w(TAG, String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w(TAG, String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }
    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.

                    MobileAds.initialize(this, initializationStatus -> {});
                    runOnUiThread(
                            () -> {
                                // TODO: Request an ad.
                                // loadInterstitialAd();
                            });

                })
                .start();
    }
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.net.Network network = connectivityManager.getActiveNetwork();
                if (network == null) return false;

                android.net.NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null &&
                        (capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
                                || capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                                || capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

}
