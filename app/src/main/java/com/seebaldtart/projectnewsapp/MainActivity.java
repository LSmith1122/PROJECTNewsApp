package com.seebaldtart.projectnewsapp;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {
    private final String LOG_TAG = getClass().getSimpleName();
    private String END_POINT_URL = "https://content.guardianapis.com/search?";
    private int ARTICLE_LOADER_ID = 0;
    private static String inputTag;
    private ListView listView;
    private TextView emptyText;
    private LinearLayout loadingGroup;
    private SearchView searchbar;
    private ArticleAdapter adapter;
    private ArrayList<Article> articleList;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tag = "tag";
        try {
            if (getIntent().hasExtra(tag)) {
                inputTag = bundle.getString(tag);
            } else {
                inputTag = getUserInput();
            }
            String titleString = "\"" + inputTag + "\"";
            this.setTitle(titleString);
            initViews();
            if (isConnectedToInternet()) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(ARTICLE_LOADER_ID, null, this).forceLoad();
            } else {
                loadingGroup.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText(getString(R.string.no_internet_connection));
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Possible null Bundle Key: " + tag, e);
        }
    }
    private String getUserInput() {
        String input = sharedPreferences.getString(
                getString(R.string.user_input_key),
                getString(R.string.user_input_default));
        return input;
    }
    private boolean isConnectedToInternet() {
        boolean state = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            state = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return state;
    }
    private void initViews() {
        listView = findViewById(R.id.list);
        emptyText = findViewById(R.id.empty_text);
        loadingGroup = findViewById(R.id.loading_group);
        searchbar = findViewById(R.id.search_bar);
        TextView loadingText = findViewById(R.id.loading_text);
        loadingText.setText(getString(R.string.loading));
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryUtils.performSearch(getApplicationContext(), query, searchbar);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void updateUI(ArrayList<Article> articles) {
        loadingGroup.setVisibility(View.GONE);
        if (articles.isEmpty()) {
            listView.setEmptyView(emptyText);
            emptyText.setText(getString(R.string.empty_text));
        } else {
            adapter = new ArticleAdapter(getApplicationContext(), 0, articles);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri = Uri.parse(articleList.get(position).getArticleURL());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }
    }
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String limit = sharedPreferences.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));
        Uri baseUri = Uri.parse(END_POINT_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", inputTag);
        uriBuilder.appendQueryParameter("show-fields", "all");
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", limit);
        uriBuilder.appendQueryParameter("show-elements", "image");
        uriBuilder.appendQueryParameter("api-key", QueryUtils.getApiKey());
        return new ArticleTaskLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> data) {
        articleList = data;
        emptyText.setVisibility(View.GONE);
        loadingGroup.setVisibility(View.VISIBLE);
        updateUI(articleList);
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        if (adapter != null) {
            adapter.clear();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Bundle bundle = new Bundle();
            bundle.putString("tag", inputTag);
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.putExtras(bundle);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}