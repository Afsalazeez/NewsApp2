package com.example.anjikkadans.newsapp2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String API_KEY = BuildConfig.NEWS_API_KEY;
    private static final String GUARDIAN_API_BASE_URL = "https://content.guardianapis.com/search?show-tags=contributor";
    private static final String FORMAT = "format";
    private static final String QUERY = "q";
    private static final String API = "api-key";
    private String content = "";
    private static final String ORDERBY = "order-by";
    private static final String SECTION = "section";

    private final int NEWS_LOADER_ID = 1;
    private RecyclerView newsRecyclerView;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<News> newsList;
    private TextView errorTextView;
    private ConnectivityManager connectMgr;
    private ConnectivityManager connectMgr1;
    private NetworkInfo netInfo;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup the connectivity manager and check connection status
        connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check currently active network
        connectMgr1 = connectMgr;
        netInfo = connectMgr1.getActiveNetworkInfo();
        //If there is an active network connection get data otherwise display error
        if (netInfo != null && netInfo.isConnected()) {
            // initializing the loader for the first time
            getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
            // hiding the error display
            hideErrorTextView();
        } else {
            // when the network is not available hide the progressbar and show no network available
            hideProgressBar();
            showErrorTextView(getString(R.string.no_internet));
        }
        // initializing the recycler view
        newsRecyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
        // setting the recycler view has fixed size
        newsRecyclerView.setHasFixedSize(true);
        // initializing the layout manager for the recycler view
        mLayoutManager = new LinearLayoutManager(this);
        // setting the layout for the recyler view
        newsRecyclerView.setLayoutManager(mLayoutManager);

        //register shared preference listener
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // receiving intent from the search view
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleEvent(intent);
    }

    // handles the event of search
    private void handleEvent(Intent intent) {

        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            // getting the search query from the intent
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                content = query;
            }

            // to-do whatever we want
            //If there is an active network connection get data otherwise display error
            if (netInfo != null && netInfo.isConnected()) {
                getSupportLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
                hideErrorTextView();
            } else {
                hideProgressBar();
                showErrorTextView(getString(R.string.no_internet));
            }
        }
    }

    // overriding onclick interface from NewsAdapter class to show web content
    // when an item is clicked
    @Override
    public void onClick(int position) {
        // get the object data of which list item is clicked
        News clickedNewsItem = newsList.get(position);
        // getting the url for web
        String webURL = clickedNewsItem.getNewsURL();
        // starting a webIntent to show the web data in a browser using Intent.ACTION_VIEW
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(webURL));
        startActivity(webIntent);
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        // show the progress bar when the loader is created
        showProgressBar();
        return new newsLoader(this, buildUrl());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        if (data != null) {
            // updates recycler view with data
            showNewsData(data);
            // saving the data for click handling
            newsList = data;
        } else
            showErrorTextView(getString(R.string.no_news_available));
        hideProgressBar();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {

    }

    // initializes newsAdapter with newsList data and set it to the recyclerView
    // added a divider between recyclerView items
    public void showNewsData(List<News> newsList) {
        newsAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(newsAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        newsRecyclerView.addItemDecoration(itemDecor);
    }

    // showes progressbar
    public void showProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    // hides progressbar
    public void hideProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    // shows errorTextView
    public void showErrorTextView(String message) {
        errorTextView = (TextView) findViewById(R.id.error_text_view);
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    // hides errorTextView
    public void hideErrorTextView() {
        errorTextView = (TextView) findViewById(R.id.error_text_view);
        errorTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        //Setup the connectivity manager and check connection status
        connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check currently active network
        connectMgr1 = connectMgr;
        netInfo = connectMgr1.getActiveNetworkInfo();
        //If there is an active network connection get data otherwise display error
        if (netInfo != null && netInfo.isConnected()) {
            // initializing the loader for the first time
            getSupportLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
            // hiding the error display
            hideErrorTextView();
        } else {
            // when the network is not available hide the progressbar and show no network available
            hideProgressBar();
            showErrorTextView(getString(R.string.no_internet));
        }

    }


    public static class newsLoader extends AsyncTaskLoader<List<News>> {
        String url = "";

        public newsLoader(@NonNull Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<News> loadInBackground() {
            NetworkUtils networkUtils = new NetworkUtils();
            return networkUtils.getNews(url);
        }

        @Override
        public void deliverResult(@Nullable List<News> data) {
            super.deliverResult(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // codes for implementing search view from the menu
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // collapse the search view when the query is submitted
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.setIconified(true);
                searchView.clearFocus();
                (menu.findItem(R.id.item_search)).collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.item_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return true;
    }

    // builds url for a specific content
    public String buildUrl() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri.Builder uriBuilder = Uri.parse(GUARDIAN_API_BASE_URL).buildUpon()
                .appendQueryParameter(API, API_KEY)
                .appendQueryParameter(QUERY, content)
                .appendQueryParameter(ORDERBY, sharedPreferences.getString(this.getString(R.string.order_by_key), ""))
                .appendQueryParameter(FORMAT, "json");
        return uriBuilder.toString();

    }

}
