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
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    SAXParser saxParser;
    String[] userNames = {"barbell-kun", "rykimchi"};
    ArrayList<Redditor> redditors = new ArrayList<Redditor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar stuff because it doesn't work by default
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional


        // TODO: Grab all the data using the sax parser, iterate through the usernames
        // Grab all comments then populate the list according according to the User objects you pass.
        // ALL DONE VOILA!~
        RssProcessingTask rssProcessingTask = new RssProcessingTask();
        rssProcessingTask.execute();
    }

    public class Redditor {
        private String userName;
        private ArrayList<String> titles;

        public Redditor(String userName, ArrayList<String> titles) {
            this.userName = userName;
            this.titles = titles;
        }

        public String getUserName() {
            return this.userName;
        }

        public ArrayList<String> getTitles() {
            return this.titles;
        }
    }

    public class Comment {
        private String title;
        private String date;
        private String subReddit;
        private String context;
        private String url;

        public Comment(String title, String date, String subReddit, String context, String url) {
            this.title = title;
            this.date = date;
            this.subReddit = subReddit;
            this.context = context;
            this.url = url;
        }
    }


    class RssProcessingTask extends AsyncTask<Void, Void, Void> {
        private RedditUserCommentHandler commentHandler;

        @Override
        protected Void doInBackground(Void... voids) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            URL url = null;
            HttpURLConnection connection = null;

            try {
                saxParser = saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

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
            ArrayList<String> titles = new ArrayList<String>();

            for(int i = 0; i < commentHandler.getTitles().size(); i++) {
                titles.add(commentHandler.getTitles().get(i));
            }

            redditors.add(new Redditor("barbell-kun", titles));

            userCollectionPagerAdapter = new UserCollectionPagerAdapter(getSupportFragmentManager(), redditors);
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(userCollectionPagerAdapter);
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
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }
    public class UserCollectionPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Redditor> redditors;

        public UserCollectionPagerAdapter(FragmentManager fm, ArrayList<Redditor> redditors) {
            super(fm);
            this.redditors = redditors;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new UserFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("titles", redditors.get(position).getTitles());
            args.putInt(UserFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return redditors.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "/u/ " + redditors.get(position).getUserName();
        }
    }

    public static class UserFragment extends ListFragment {
        public static final String ARG_OBJECT = "object";

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.user_template, container, false);

            // Grab the object through the arguments you pass AND THEN POPULATE THE VIEW ACCORDINGLY
            Bundle args = getArguments();
            Redditor redditor = (Redditor)args.get("currentUser");
            LinearLayout user_container = (LinearLayout)rootView.findViewById(R.id.scroll_view);

            ArrayList<String> titles = args.getStringArrayList("titles");

            for(int i = 0; i < titles.size(); i++) {
                View commentView = inflater.inflate(R.layout.comment_template, container, false);
                TextView topText = (TextView)commentView.findViewById(R.id.toptext);
                topText.setText(titles.get(i));
                user_container.addView(commentView);
            }

            return rootView;
        }
    }
}
