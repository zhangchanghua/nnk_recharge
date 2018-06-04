package com.nnk.rechargeplatform.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;

import butterknife.BindView;

public class WebActivity extends BaseActivity {
    private static final String KEY_TITLE = "title";
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    public static void openCustomService(Context context) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(KEY_TITLE, context.getString(R.string.contact_service));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        titleView.setText(intent.getStringExtra(KEY_TITLE));
        configWebView();
        webView.loadUrl("http://www.nnk100.net/");
    }

    private void configWebView() {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
        }
    }
}
