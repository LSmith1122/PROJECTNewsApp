package com.seebaldtart.projectnewsapp;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private String customURL;
    private String userInputTag = "";
    private String inputTag;
    private ListView listView;
    private TextView emptyText;
    private LinearLayout loadingGroup;
    private SearchView searchbar;
    private ArticleAdapter adapter;
    private ArrayList<Article> articleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        String tag = "tag";
        try {
            inputTag = bundle.getString(tag);
            String titleString = "\"" + inputTag + "\"";
            this.setTitle(titleString);
            initViews();
            if (isConnectedToInternet()) {
                customURL = compileInfo(inputTag);
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(ARTICLE_LOADER_ID, null, this).forceLoad();
            } else {
                loadingGroup.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText(getString(R.string.no_internet_connection));
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Possible null key: " + tag, e);
        }
    }
    private String compileInfo(String input) {
        customURL = "";
        String apiKey = "&api-key=c49528a8-efe3-4f88-87ea-13530bb963b5";
        String imgRequest = "&show-elements=image";
        String apiFormat = "&format=json";
        String relevanceRequest = "&order-by=relevance";
        String fieldsRequest = "&show-fields=headline,byline,bodyText,thumbnail";
        userInputTag = compileTag(input);
        customURL = END_POINT_URL + userInputTag + apiFormat + fieldsRequest + imgRequest + relevanceRequest + apiKey;
        return customURL;
    }
    private String compileTag(String tag) {
        StringBuilder builder = new StringBuilder();
        String queryCode = "q=";
        String tagString = removeEndingEmptySpaces(tag);
        String emptySpace = " ";
        String urlEmptySpace = "%20";
        builder.append(queryCode);
        String[] stringList = tagString.split(emptySpace);
        for (int i = 0; i < stringList.length; i++) {
            int lastPos = stringList.length - 1;
            String currentWord = stringList[i];
            builder.append(currentWord);
            if (i < lastPos) {
                builder.append(urlEmptySpace);
            }
        }
        return builder.toString();
    }
    private String removeEndingEmptySpaces(String tag) {
        if (tag != null) {
            CharSequence tagChar = tag;
            String emptySpace = " ";
            if (tag.contains(emptySpace)) {
                if (tag.endsWith(emptySpace)) {
                    int length = tag.length() - 1;
                    tagChar = tag.substring(0, length);
                }
            }
            return tagChar.toString();
        }
        return null;
    }
    private boolean isConnectedToInternet() {
        boolean state = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
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
        Log.i("TEST", "Article Size: " + articles.size());
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
        ArticleTaskLoader loader = new ArticleTaskLoader(this, customURL);
        return loader;
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
}