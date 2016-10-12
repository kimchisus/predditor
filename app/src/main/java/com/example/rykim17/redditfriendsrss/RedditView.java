package com.example.rykim17.redditfriendsrss;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class RedditView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getIntent();
            String url = intent.getStringExtra("url");
            WebView webView = new WebView(this);
            setContentView(webView);
            webView.loadUrl(url);
        } catch(Exception e) {
            Log.d("test", e.toString());
        }
    }
}
