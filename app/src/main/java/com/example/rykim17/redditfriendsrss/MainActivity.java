package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private SAXParser saxParser;
    private ListView commentPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.commentPicker = (ListView) findViewById(R.id.commentPicker);
        RssProcessingTask rssProcessingTask = new RssProcessingTask();
        rssProcessingTask.execute();
    }

    class RssProcessingTask extends AsyncTask<Void, Void, Void> {
        private RedditUserCommentHandler commentHandler;

        @Override
        protected Void doInBackground(Void... voids) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            try {
                saxParser = saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            URL url = null;
            HttpURLConnection connection = null;

            try {
                url = new URL("https://www.reddit.com/user/barbell-kun/comments/.rss");
                connection = (HttpURLConnection)url.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            commentHandler = new RedditUserCommentHandler();

            try {
                saxParser.parse(connection.getInputStream(), commentHandler);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ListView commentPicker = MainActivity.this.commentPicker;
            ArrayAdapter<String> titleAdapter = new TitleAdapter(MainActivity.this, R.layout.comment_template, commentHandler.getTitles());
            commentPicker.setAdapter(titleAdapter);
        }
    }

    class TitleAdapter extends ArrayAdapter<String> {
        private ArrayList<String> titles;

        public TitleAdapter(Context context, int resource, ArrayList<String> titles) {
            super(context, resource, titles);
            this.titles = titles;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.comment_template, null);
            }

            String title = titles.get(position);
            if (title != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(title);
                }
            }
            return v;
        }
    }


    class RedditUserCommentHandler extends DefaultHandler {
        private ArrayList<String> title;
        private ArrayList<String> content;
        private StringBuilder stringBuilder;
        private boolean inTitle;
        private boolean inContent;

        public RedditUserCommentHandler() {
            stringBuilder = new StringBuilder();
            title = new ArrayList<String>();
            content = new ArrayList<String>();
        }

        public ArrayList<String> getTitles() {
            return this.title;
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            Log.d("RAWR", "startDocument()");
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            Log.d("RAWR", "endDocument()");

            // The first element is just for 'overview for:'
            title.remove(0);

            for(int i = 0; i < title.size(); i++) {
                String titleStr = title.get(i);
                titleStr = titleStr.replace("/u/barbell-kun on", "").trim();
                title.set(i, titleStr);
                Log.d("RAWR", "TITLE: " + titleStr);
                Log.d("RAWR", "CONTENT: " + content.get(i));
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            Log.d("RAWR", "startElement(): " + qName);

            // Clear the string builder
            stringBuilder.setLength(0);

            switch(qName) {
                case "title":
                    inTitle = true;
                    break;
                case "content":
                    inContent = true;
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (qName.equals("title")) {
                inTitle = false;
                title.add(stringBuilder.toString());
            } else if(qName.equals("content")) {
                inContent = false;
                content.add(stringBuilder.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            if(inTitle || inContent) {
                stringBuilder.append(ch, start, length);
            }
        }
    }
}
