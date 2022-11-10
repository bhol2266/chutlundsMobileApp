package com.bhola.chutlundsmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AdsterraAds extends AppCompatActivity {
    private WebView webView, webView2;
    LinearLayout closelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adsterra_ads);

        String url = "https://passablejeepparliament.com/pvpcafc4kk?key=f25a9b13a509037a68a314ca0278f644";
        String url2 = "https://passablejeepparliament.com/uvm4sgmd?key=1a4c3d6da7024a80c0acf420460a907f";
        String url3 = "https://www.chutlunds.live/ads/ads.html";

        webView = (WebView) findViewById(R.id.webview);
        webView2 = (WebView) findViewById(R.id.webvie2);
        closelayout = findViewById(R.id.closelayout);
        closelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url3);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                closelayout.setVisibility(View.VISIBLE);
            }
        });

        webView2.setWebViewClient(new WebViewClient());
        webView2.loadUrl(url2);
        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);
        webView2.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                Toast.makeText(AdsterraAds.this, "Loaded", Toast.LENGTH_SHORT).show();
                closelayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}