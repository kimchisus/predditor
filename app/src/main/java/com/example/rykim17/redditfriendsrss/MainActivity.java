package com.example.rykim17.redditfriendsrss;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
    UserCollectionPagerAdapter userCollectionPagerAdapter;
    ViewPager viewPager;
    private SAXParser saxParser;
    private ListView commentPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userCollectionPagerAdapter = new UserCollectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(userCollectionPagerAdapter);
    }

    public class UserCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public UserCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new UserFragment();
            Bundle args = new Bundle();
            args.putInt(UserFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "/u/ " + (position + 1);
        }
    }

    public static class UserFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.user_template, container, false);

            Bundle args = getArguments();


//            this.commentPicker = (ListView) findViewById(R.id.commentPicker);
//            RssProcessingTask rssProcessingTask = new RssProcessingTask();
//            rssProcessingTask.execute();

            ((TextView) rootView.findViewById(android.R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
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
