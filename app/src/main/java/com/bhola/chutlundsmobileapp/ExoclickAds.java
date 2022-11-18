package com.bhola.chutlundsmobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ExoclickAds {

    @SuppressLint("ClickableViewAccessibility")
    public static void loadAds(Context context, Handler[] handler, Runnable[] runnable) {

        final boolean[] adsSeen = {false};

        if (SplashScreen.Ads_State.equals("inactive")) {
            return;
        }

        if (SplashScreen.adsLoaded == 0 || SplashScreen.adsLoaded == 2) {
            if (SplashScreen.adsLoaded == 0) SplashScreen.adsLoaded = 1;
            if (SplashScreen.adsLoaded == 2) SplashScreen.adsLoaded = 0;
            return;
        }
        final AlertDialog[] dialog = {null};

        WebView webView = null;
        LinearLayout closelayout;
        TextView countDownText;

        String url = "https://passablejeepparliament.com/pvpcafc4kk?key=f25a9b13a509037a68a314ca0278f644";
        String url2 = "https://passablejeepparliament.com/uvm4sgmd?key=1a4c3d6da7024a80c0acf420460a907f";
        final String[] url3 = {"https://www.chutlunds.live/ads/ads.html"};


        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View promptView = inflater.inflate(R.layout.ads_dialog, null);
        closelayout = promptView.findViewById(R.id.closelayout);
        countDownText = promptView.findViewById(R.id.countDownText);
        builder.setView(promptView);
        builder.setCancelable(false);
        dialog[0] = builder.create();


        webView = (WebView) promptView.findViewById(R.id.webview);

        closelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adsSeen[0]) {
                    if (dialog[0] != null) dialog[0].hide();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url3[0]);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("TAGA", "onPageStarted " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (adsSeen[0]) return;
                runnable[0] = new Runnable() {
                    @Override
                    public void run() {
                        dialog[0].show();
                        new CountDownTimer(4000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                countDownText.setText("Ad will end in  " + (millisUntilFinished + 1000) / 1000 + " seconds");
                            }

                            public void onFinish() {
                                SplashScreen.adsLoaded = 2;
                                adsSeen[0] = true;
                                countDownText.setText("Close Ad");
                            }
                        }.start();
                    }
                };
                try {
                    handler[0] = new Handler(Looper.getMainLooper());
                    handler[0].postDelayed(runnable[0], 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }





}

