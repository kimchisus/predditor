package com.example.rykim17.redditfriendsrss;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
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

//    public class Redditor {
//        private String userName;
//        private ArrayList<String> titles;
//        private ArrayList<String> URLs;
//
//        public Redditor(String userName, ArrayList<String> titles, ArrayList<String> urls) {
//            this.userName = userName;
//            this.titles = titles;
//            this.URLs = urls;
//        }
//
//        public String getUserName() {
//            return this.userName;
//        }
//
//        public ArrayList<String> getTitles() {
//            return this.titles;
//        }
//
//        public ArrayList<String> getURLs() {
//            return this.URLs;
//        }
//
//        public ArrayList<Comment> getComments() {
//            ArrayList<Comment> comments = new ArrayList<Comment>();
//
//            for(int i = 0; i < this.titles.size(); i++) {
//                comments.add(new Comment(this.titles.get(i), this.URLs.get(i)));
//            }
//
//            return comments;
//        }
//    }

    public class Redditor implements Parcelable {
        private String userName;
        private ArrayList<String> titles;
        private ArrayList<String> URLs;

        public Redditor(String userName, ArrayList<String> titles, ArrayList<String> urls) {
            this.userName = userName;
            this.titles = titles;
            this.URLs = urls;
        }

        public String getUserName() {
            return this.userName;
        }

        public ArrayList<String> getTitles() {
            return this.titles;
        }

        public ArrayList<String> getURLs() {
            return this.URLs;
        }

        public ArrayList<Comment> getComments() {
            ArrayList<Comment> comments = new ArrayList<Comment>();

            for(int i = 0; i < this.titles.size(); i++) {
                comments.add(new Comment(this.titles.get(i), this.URLs.get(i)));
            }

            return comments;
        }

        protected Redditor(Parcel in) {
            userName = in.readString();
            if (in.readByte() == 0x01) {
                titles = new ArrayList<String>();
                in.readList(titles, String.class.getClassLoader());
            } else {
                titles = null;
            }
            if (in.readByte() == 0x01) {
                URLs = new ArrayList<String>();
                in.readList(URLs, String.class.getClassLoader());
            } else {
                URLs = null;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userName);
            if (titles == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(titles);
            }
            if (URLs == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(URLs);
            }
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Redditor> CREATOR = new Parcelable.Creator<Redditor>() {
            @Override
            public Redditor createFromParcel(Parcel in) {
                return new Redditor(in);
            }

            @Override
            public Redditor[] newArray(int size) {
                return new Redditor[size];
            }
        };
    }

    public class Comment {
        String title;
        String url;

        public Comment(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return this.title;
        }

        public String getUrl() {
            return this.url;
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
            ArrayList<String> urls = new ArrayList<String>();

            for(int i = 0; i < commentHandler.getTitles().size(); i++) {
                titles.add(commentHandler.getTitles().get(i));
                urls.add(commentHandler.getUrls().get(i));
            }

            redditors.add(new Redditor("barbell-kun", titles, urls));
            userCollectionPagerAdapter = new UserCollectionPagerAdapter(getSupportFragmentManager(), redditors);
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(userCollectionPagerAdapter);
        }
    }

    class RedditUserCommentHandler extends DefaultHandler {
        private ArrayList<String> title;
        private ArrayList<String> content;
        private ArrayList<String> url;

        private StringBuilder stringBuilder;
        private boolean inTitle;
        private boolean inContent;
        private boolean inUrl;

        public RedditUserCommentHandler() {
            stringBuilder = new StringBuilder();
            title = new ArrayList<String>();
            content = new ArrayList<String>();
            url = new ArrayList<String>();
        }

        public ArrayList<String> getTitles() {
            return this.title;
        }

        public ArrayList<String> getUrls() {
            return this.url;
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

                String urlStr = url.get(i);
                urlStr += "http://www.reddit.com";
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
            args.putParcelable("redditorObject", redditors.get(position));
            args.putInt(UserFragment.ARG_OBJECT, position);
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
            Bundle args = getArguments();
            Redditor redditor = args.getParcelable("redditorObject");

            setListAdapter(new TitleAdapter(getActivity(), R.layout.user_template, redditor.getComments()));
            return rootView;
        }
    }

    static class TitleAdapter extends ArrayAdapter<Comment> {
        private ArrayList<Comment> comments;

        public TitleAdapter(Context context, int resource, ArrayList<Comment> comments) {
            super(context, resource, comments);
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

            String title = comments.get(position).getTitle();

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
}
