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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

            commentHandler = new RedditUserCommentHandler(userNames.get(currentUserIndex));

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
                    commentHandler.getContent(), commentHandler.getUrls(), commentHandler.getTime(), commentHandler.getSubreddit(), commentHandler.getId()));

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

    public static class UserFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener, OnTaskCompleted {
        public static final String ARG_OBJECT = "object";
        SwipeRefreshLayout swipeLayout;
        Redditor redditor;
        RSSHandler rssRefresh;
        TitleAdapter adapter;

        @Override
        public void onTaskCompleted(Redditor redditor) {
            changeComments(redditor.getComments());
            adapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
        }

        public void changeComments(ArrayList<Comment> comments) {
            this.redditor.getComments().clear();

            // Find ids to add.
            for(int i = 0; i < comments.size(); i++) {
                this.redditor.getComments().add(comments.get(i));
            }
        }

        @Override
        public void onRefresh() {
            rssRefresh = new RSSHandler(redditor.getUserName(), this);
            rssRefresh.execute();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.user_template, container, false);
            swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);
            swipeLayout.setOnRefreshListener(this);

            Bundle args = getArguments();
            redditor = args.getParcelable("redditor");
            adapter = new TitleAdapter(getActivity(), R.layout.user_template, this.redditor.getComments());
            setListAdapter(adapter);
            return rootView;
        }
    }
}
