package com.example.rykim17.redditfriendsrss;

import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by rykim17 on 2016-10-30.
 */

public class RSSHandler extends AsyncTask<Void, Void, Void> {
    private RedditUserCommentHandler commentHandler;
    String redditorName;
    Redditor redditor;
    SAXParser saxParser;
    OnTaskCompleted listener;

    public RSSHandler(String redditorName, OnTaskCompleted listener) {
        super();
        this.listener = listener;
        this.redditorName = redditorName;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        URL url = null;
        HttpURLConnection connection = null;

        if(saxParser == null) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            try {
                saxParser = saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        try {
            url = new URL("https://www.reddit.com/user/" + redditorName + "/comments/.rss");
            connection = (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commentHandler = new RedditUserCommentHandler(redditorName);

        try {
            saxParser.parse(connection.getInputStream(), commentHandler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Redditor getRedditor() {
        return this.redditor;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        this.redditor = new Redditor(redditorName, commentHandler.getTitles(),
                commentHandler.getContent(), commentHandler.getUrls(), commentHandler.getTime(), commentHandler.getSubreddit(), commentHandler.getId());

        listener.onTaskCompleted(this.redditor);
    }
}