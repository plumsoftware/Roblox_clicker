package ru.plumsoftware.robloxclicker.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import java.util.List;

import ru.plumsoftware.robloxclicker.R;
import ru.plumsoftware.robloxclicker.adapter.HeroAdapter;
import ru.plumsoftware.robloxclicker.data.Data;
import ru.plumsoftware.robloxclicker.dialogs.CustomProgressDialog;
import ru.plumsoftware.robloxclicker.heroes.Hero;
import ru.plumsoftware.robloxclicker.heroes.Heroes;

public class ShopActivity extends AppCompatActivity {
    private long score;
    private int click;
    private int imageResId;
    private SharedPreferences sharedPreferences;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private CustomProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

//        region::Base variables
        Context context = ShopActivity.this;
        Activity activity = ShopActivity.this;

        MobileAds.initialize(this, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {

            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("R-M-2483723-2");
        interstitialAd.setInterstitialAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdLoaded() {
                progressDialog.dismissProgressDialog();
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                progressDialog.dismissProgressDialog();
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(ShopActivity.this, MainActivity.class));
            }

            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdDismissed() {
                progressDialog.dismissProgressDialog();
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(ShopActivity.this, MainActivity.class));
            }

            @Override
            public void onAdClicked() {
                progressDialog.dismissProgressDialog();
            }

            @Override
            public void onLeftApplication() {

            }

            @Override
            public void onReturnedToApplication() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {

            }
        });
        adRequest = new AdRequest.Builder().build();

        progressDialog = new CustomProgressDialog(this);
        progressDialog.setMessage("Загрузка...");
//        endregion

//        region::Find views
        TextView textViewScore2 = (TextView) findViewById(R.id.textViewScore2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHeroes);
//        endregion

//        region::Animations

//        endregion

//        region::Get data
        sharedPreferences = context.getSharedPreferences(Data.SP_NAME, Context.MODE_PRIVATE);
        score = sharedPreferences.getLong(Data.SP_SCORE, 0);
        click = sharedPreferences.getInt(Data.SP_CLICK, 1);
        imageResId = sharedPreferences.getInt(Data.SP_IMAGE_RES_ID, R.drawable.capitan_roblox_1);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                textViewScore2.setText(Long.toString(sharedPreferences.getLong(Data.SP_SCORE, 0)));
            }
        });
//        endregion

//        region::Setup heroes
        textViewScore2.setText(Long.toString(score));
        List<Hero> list = Heroes.buildHeroes();
        for (int i = 0; i < list.size(); i++) {
            Hero hero = list.get(i);
            hero.setBuy(sharedPreferences.getBoolean(Data.SP_HEROES_IS_BUY[i], false));
            list.set(i, hero);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new HeroAdapter(context, list));
//        endregion

//        region::Clickers

//        endregion
    }

    @Override
    public void onBackPressed() {
        progressDialog.showProgressDialog();
        interstitialAd.loadAd(adRequest);
    }
}