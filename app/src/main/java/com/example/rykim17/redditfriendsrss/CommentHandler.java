package com.example.rykim17.redditfriendsrss;

import android.text.Html;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by rykim17 on 2016-10-30.
 */

class RedditUserCommentHandler extends DefaultHandler {
    private ArrayList<String> title, content, url, time, subreddit, id;
    private StringBuilder stringBuilder;
    private boolean inTitle, inContent, inTime, inId;
    String username;

    public RedditUserCommentHandler(String username) {
        this.username = username;
        stringBuilder = new StringBuilder();
        title = new ArrayList<String>();
        content = new ArrayList<String>();
        url = new ArrayList<String>();
        time = new ArrayList<String>();
        subreddit = new ArrayList<String>();
        id = new ArrayList<String>();
    }

    public ArrayList<String> getTitles() {
        return this.title;
    }
    public ArrayList<String> getUrls() {
        return this.url;
    }
    public ArrayList<String> getContent() { return this.content; }
    public ArrayList<String> getTime() { return this.time; }
    public ArrayList<String> getSubreddit() { return this.subreddit; }
    public ArrayList<String> getId() { return this.id; }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        // The first for these are just data for the user. Un-needed.
        title.remove(0);
        time.remove(0);
        subreddit.remove(0);

        // Format the xml stuff.
        for(int i = 0; i < title.size(); i++) {
            // Format the title
            String titleStr = title.get(i);
            titleStr = titleStr.replace("/u/" + username + " on", "").trim();
            title.set(i, titleStr);

            // Format the url
            String urlStr = url.get(i);
            urlStr = "http://www.reddit.com" + urlStr;
            url.set(i, urlStr);

            // Format the content
            String contentStr = content.get(i);
            contentStr = Html.fromHtml(contentStr).toString();
            content.set(i, contentStr);

            // Format the subreddit name
            String subRedditStr = subreddit.get(i);
            subreddit.set(i, subRedditStr);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Clear the string builder
        stringBuilder.setLength(0);

        switch(qName) {
            case "title":
                inTitle = true;
                break;
            case "content":
                inContent = true;
                break;
            case "link":
                if(attributes.getValue("href").length() > 3 && attributes.getValue("href").substring(0,3).equals("/r/")) {
                    url.add(attributes.getValue("href"));
                }
            case "updated":
                inTime = true;
                break;
            case "category":
                if(attributes.getValue("label").length() > 3 && attributes.getValue("label").substring(0,3).equals("/r/")) {
                    subreddit.add(attributes.getValue("label"));
                }
                break;
            case "id":
                inId = true;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        inTitle = inContent = inTime = false;

        switch(qName) {
            case "title":
                title.add(stringBuilder.toString());
                break;
            case "content":
                content.add(stringBuilder.toString());
                break;
            case "updated":
                time.add(stringBuilder.toString());
                break;
            case "id":
                id.add(stringBuilder.toString());
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if(inTitle || inContent || inTime || inId) {
            stringBuilder.append(ch, start, length);
        }
    }
}