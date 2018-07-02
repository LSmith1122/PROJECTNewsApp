package com.seebaldtart.projectnewsapp;
import android.content.AsyncTaskLoader;
import android.content.Context;
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
        return articles;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}