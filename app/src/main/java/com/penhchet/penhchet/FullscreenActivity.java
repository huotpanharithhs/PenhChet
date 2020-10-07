package com.penhchet.penhchet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenActivity extends AppCompatActivity {
    private WebView mWebView;
    private static final String TEL_PREFIX = "tel:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);
        if (!DetectConnection.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
        }
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.requestFocus();
        mWebView.getSettings().setLightTouchEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);

        webSettings.setAppCachePath(getBaseContext().getCacheDir().getPath());
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.loadUrl("https://ppenhchet.blogspot.com/");
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
//        webSettings.setCacheMode(isNetworkConnected()?WebSettings.LOAD_NO_CACHE: WebSettings.LOAD_CACHE_ONLY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            try {
                mWebView.stopLoading();
            } catch (Exception e) {
            }

            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
            Context context = getApplicationContext();
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            super.onReceivedError(mWebView, request, error);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            if (url.startsWith(TEL_PREFIX)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            if (url.contains("maps/")) {
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
            }
            if (url.contains("@gmail")) {
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.gm");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
            }
            if (url.contains("photos.app")) {
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.photos");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
            }
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}