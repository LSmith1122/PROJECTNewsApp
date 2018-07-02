package com.seebaldtart.projectnewsapp;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
public final class QueryUtils {
    private static Context mContext;
    private static String LOG_TAG;
    private static ArrayList<Article> articleList;
    private static String mUrl;
    private QueryUtils(Context context, String url) {
        mContext = context;
        LOG_TAG = context.getClass().getSimpleName();
        mUrl = url;
    }
    public static ArrayList<Article> extractArticleBlocks(String url) {
        mUrl = url;
        articleList = new ArrayList<>();
        makeHTTPRequest(mUrl, articleList);
        return articleList;
    }
    private static void makeHTTPRequest(String stringUrl, ArrayList<Article> articles) {
        String jsonResponse = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            url = createUrl(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response Code Error: " + urlConnection.getErrorStream());
            }
            articleList = createJSONProperties(jsonResponse);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error: Possible bad URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: Possible bad connection", e);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Error: Problem opening connection", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: Problem closing connection", e);
                }
            }
        }
    }
    private static String readInputStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error reading InputStream ", e);
            }
        } else {
            Log.i(LOG_TAG, "inputStream is empty");
        }
        return output.toString();
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
    private static ArrayList<Article> createJSONProperties(String jsonResponse) {
        ArrayList<Article> articles = new ArrayList<>();
        if (jsonResponse.equals("") || jsonResponse.equals(null)) {
            Log.e(LOG_TAG, "Error: No JSON Response");
            return null;
        }
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject rootResponse = root.getJSONObject("response");
            JSONArray rootList = rootResponse.getJSONArray("results");
            int numberOfEvents = rootList.length();
            for (int i = 0; i < numberOfEvents; i++) {
                JSONObject currentArticle = rootList.getJSONObject(i);
                JSONObject fields = currentArticle.getJSONObject("fields");
                String title = fields.getString("headline");
                String author = fields.getString("byline");
                String bodyText = fields.getString("bodyText");
                String thumbnailString = fields.getString("thumbnail");
                String url = currentArticle.getString("webUrl");
                String date = currentArticle.getString("webPublicationDate");
                String section = currentArticle.getString("sectionName");
                Bitmap thumbnail = getBitmap(thumbnailString);
                articles.add(new Article(mContext, title, author, bodyText, date, section, url, thumbnail));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return articles;
    }
    /**
     *
     * The getBitmap() method was inspired by Sean Medlin's custom {@link AsyncTask} DownloadImageTask
     * His custom class {@link AsyncTask} DownloadImageTask can be found here:
     * https://github.com/Medlinator/NewsApp/blob/master/app/src/main/java/com/example/android/newsapp/ReviewAdapter.java
     *
     * Thank you, Sean Medlin (Slack: @Sean Medlin (ABND))
     */
    private static Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        String urlDisplay = url;
        try {
            InputStream in = new URL(urlDisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return bitmap;
    }
    public static void performSearch(Context context, String query, SearchView view) {
        if (!view.getQuery().equals("")) {
            Intent intent = new Intent(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("tag", query);
            intent.putExtras(bundle);
            context.startActivity(intent);

        }
    }
    public static String checkEntry(Context context, String entry) {
        if (entry.equals(null) || entry.equals("")) {
            entry = context.getString(R.string.no_entry);
            return entry;
        }
        return entry;
    }
}