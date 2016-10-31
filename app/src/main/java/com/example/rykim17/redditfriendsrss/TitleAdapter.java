package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rykim17 on 2016-10-30.
 */

public class TitleAdapter extends ArrayAdapter<Comment> {
    private ArrayList<Comment> comments;
    String title;
    String content;
    String urlStr;
    String time;
    String subReddit;
    String fontType;
    int fontSize;
    boolean isDescending;

    SharedPreferences sharedPreferences;

    public TitleAdapter(Context context, int resource, ArrayList<Comment> comments) {
        super(context, resource, comments);
        this.comments = comments;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.fontSize = sharedPreferences.getInt("fontSize", 12);
        this.fontType = sharedPreferences.getString("fontType", "Arial");
        this.isDescending = sharedPreferences.getBoolean("isDescending", true);

        if(!this.isDescending) {
            Collections.reverse(comments);
        }

        this.comments = comments;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.comment_template, null);
        }

        title = comments.get(position).getTitle();
        content = comments.get(position).getContent();
        urlStr = comments.get(position).getUrl();
        time = comments.get(position).getTime();
        subReddit = comments.get(position).getSubReddit();

        if (title != null) {


            TextView topTime = (TextView)v.findViewById(R.id.topTime);
            TextView topSubreddit = (TextView)v.findViewById(R.id.topSubreddit);
            TextView tt = (TextView)v.findViewById(R.id.toptext);
            TextView bt = (TextView)v.findViewById(R.id.bottomtext);
            String[] files = new String[0];

            AssetManager assetManager = getContext().getAssets();
            Typeface tf = Typeface.createFromAsset(assetManager, "fonts/" + this.fontType + ".ttf");
            topTime.setTypeface(tf);
            topSubreddit.setTypeface(tf);
            tt.setTypeface(tf);
            bt.setTypeface(tf);
            tt.setTextSize(this.fontSize);

            if(topTime != null) {
                topTime.setText(time);
            }

            if(topSubreddit != null) {
                topSubreddit.setText(subReddit);
            }

            if (tt != null) {
                tt.setText(content);
            }

            if (bt != null) {
                bt.setText(title);
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RedditView.class);
                intent.putExtra("url", urlStr.trim());
                getContext().startActivity(intent);
            }
        });

        return v;
    }
}