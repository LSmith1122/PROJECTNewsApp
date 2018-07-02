package com.seebaldtart.projectnewsapp;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
public class ArticleTaskLoader extends AsyncTaskLoader<ArrayList<Article>> {
    private String customUrl;
    private ArrayList<Article> articles;
    public ArticleTaskLoader(Context context, String url) {
        super(context);
        customUrl = url;
    }
    @Override
    public ArrayList<Article> loadInBackground() {
        if (customUrl == null) {
            return null;
        }
        articles = QueryUtils.extractArticleBlocks(customUrl);
        Log.i("TEST", "Article Size: " + String.valueOf(articles.size() + " and its contents are: " + articles));
        return articles;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}