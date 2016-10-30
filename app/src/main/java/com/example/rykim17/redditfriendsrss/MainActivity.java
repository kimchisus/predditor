package com.example.rykim17.redditfriendsrss;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    // UI Adapters
    UserCollectionPagerAdapter userCollectionPagerAdapter;
    ViewPager viewPager;
    SAXParser saxParser;
    RssProcessingTask rssProcessingTask;

    // Global Variables
    ArrayList<Redditor> redditors;
    ArrayList<String> userNames;
    int currentUserIndex;
    int fontSize;
    boolean isDescending;
    String stringRedditors;
    String fontType;

    // UI stuff.
    Button editRedditors;
    SharedPreferences sharedPreferences;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 42) {
            // TODO check if there was anything changed and if there was, call this. If not, don't call this.
            recreate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar stuff because it doesn't work by default WHYYYYY?! FUUUUUUU~
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Init variables.
        initSharedPref();


        viewPager = (ViewPager) findViewById(R.id.pager);
        editRedditors = (Button) findViewById(R.id.btnAddRedditors);
        ClickHandler clickHandler = new ClickHandler();

        editRedditors.setOnClickListener(clickHandler);

        if(!stringRedditors.equals("")) {
            userNames = new ArrayList<String>(Arrays.asList(stringRedditors.split(",")));
            redditors = new ArrayList<Redditor>();
            currentUserIndex = 0;
            rssProcessingTask = new RssProcessingTask();
            rssProcessingTask.execute();
        } else {
            viewPager.setVisibility(View.GONE);
            RelativeLayout noRedditors = (RelativeLayout)findViewById(R.id.noRedditors);
            noRedditors.setVisibility(View.VISIBLE);
        }
    }

    public void initSharedPref() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        stringRedditors = sharedPreferences.getString("redditors", "");
        fontSize = sharedPreferences.getInt("fontSize", 12);
        fontType = sharedPreferences.getString("fontType", "Arial");
        isDescending = sharedPreferences.getBoolean("isDescending", true);
    }

    public class ClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            handleView(v.getId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        menu.clear();
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleView(item.getItemId());
    }

    public boolean handleView(int id) {
        boolean result = true;

        switch(id) {
            case R.id.btnAddRedditors:
            case R.id.addUser:
                openEditRedditors();
                break;
            case R.id.settings:
                openSettings();
                break;
        }

        return result;
    }

    public void openSettings() {
        Intent i = new Intent(this, Settings.class);
        startActivityForResult(i, 111);
    }

    public void openEditRedditors() {
        Intent i = new Intent(this, Redditors.class);
        startActivity(i);
    }

    class RssProcessingTask extends AsyncTask<Void, Void, Void> {
        private RedditUserCommentHandler commentHandler;

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
                url = new URL("https://www.reddit.com/user/" + userNames.get(currentUserIndex) + "/comments/.rss");
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

            redditors.add(new Redditor(userNames.get(currentUserIndex), commentHandler.getTitles(),
                    commentHandler.getContent(), commentHandler.getUrls(), commentHandler.getTime(), commentHandler.getSubreddit()));

            if(currentUserIndex == userNames.size() - 1) {
                userCollectionPagerAdapter = new UserCollectionPagerAdapter(getSupportFragmentManager(), redditors);
                viewPager.setAdapter(userCollectionPagerAdapter);
            } else {
                currentUserIndex++;
                rssProcessingTask = new RssProcessingTask();
                rssProcessingTask.execute();
            }
        }
    }

    class RedditUserCommentHandler extends DefaultHandler {
        private ArrayList<String> title, content, url, time, subreddit;
        private StringBuilder stringBuilder;
        private boolean inTitle, inContent, inTime;

        public RedditUserCommentHandler() {
            stringBuilder = new StringBuilder();
            title = new ArrayList<String>();
            content = new ArrayList<String>();
            url = new ArrayList<String>();
            time = new ArrayList<String>();
            subreddit = new ArrayList<String>();
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
                titleStr = titleStr.replace("/u/" + userNames.get(currentUserIndex) + " on", "").trim();
                title.set(i, titleStr);

                // Format the url
                String urlStr = url.get(i);
                urlStr = "http://www.reddit.com" + urlStr + "?context=3";
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
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            if(inTitle || inContent || inTime) {
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
            args.putParcelable("redditor", redditors.get(position));
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
            Redditor redditor = args.getParcelable("redditor");
            setListAdapter(new TitleAdapter(getActivity(), R.layout.user_template, redditor.getComments()));
            return rootView;
        }
    }
}
