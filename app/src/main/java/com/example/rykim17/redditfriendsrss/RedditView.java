package com.example.rykim17.redditfriendsrss;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RedditView extends AppCompatActivity {
    WebView webView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_view);

        webView = (WebView)findViewById(R.id.activity_reddit_view);
        webView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        webView.loadUrl(intent.getStringExtra("url"));
    }
}
