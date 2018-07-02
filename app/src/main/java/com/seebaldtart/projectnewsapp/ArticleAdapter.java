package com.seebaldtart.projectnewsapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
public class ArticleAdapter extends ArrayAdapter<Article> {
    static ArrayList<Article> articleList;
    private ImageView imageView;
    private Article currentArticle;
    private TextView titleText;
    private TextView bodyText;
    private TextView metaText;
    private int maxEmsTitle = 50;
    private int maxEmsBody = 250;
    public ArticleAdapter(Context context, int resource, ArrayList<Article> articles) {
        super(context, 0, articles);
        articleList = articles;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }
        currentArticle = articleList.get(position);
        imageView = (ImageView) convertView.findViewById(R.id.image);
        titleText = (TextView) convertView.findViewById(R.id.title_text);
        bodyText = (TextView) convertView.findViewById(R.id.body_text);
        metaText = (TextView) convertView.findViewById(R.id.meta_text);
        setImage(currentArticle.getArticleImage());
        setDescriptionText(titleText, currentArticle.getArticleTitle(), maxEmsTitle);
        setDescriptionText(bodyText, currentArticle.getBody(), maxEmsBody);
        setMetaText(metaText, currentArticle.getArticleSection(), currentArticle.getAuthor(), currentArticle.getArticleDate());
        return convertView;
    }
    private void setImage(Bitmap bitmap) {
        if (currentArticle.hasImage()) {        // Set downloaded image
            imageView.setImageBitmap(bitmap);
        } else {        // Set default image
            imageView.setImageResource(R.drawable.missing_image);
        }
    }
    private void setDescriptionText(TextView view, String text, int max) {
        String dottedText = "...";
        int maxEmsForText = max - dottedText.length();
        StringBuilder builder = new StringBuilder();
        CharSequence shortenedText = null;
        if (text.length() > max) {
            shortenedText = text.substring(0, maxEmsForText);
        } else {
            shortenedText = text;
        }
        builder.append(shortenedText).append(dottedText);
        view.setText(builder.toString());
    }
    private void setMetaText(TextView view, String section, String author, String date) {
        date = convertDateTime(date);
        Log.i("TEST", "Date: " + date);
        if (author == null || author.equals("")) {
            author = getContext().getString(R.string.no_author);
        }
        String metaString = section + " / " + author + " / " + date;
        view.setText(metaString);
        Log.i("TEST", "Author: " + author + ", Date: " + date);
    }
    private String convertDateTime(String dateString) {     // Retrieved from: https://beginnersbook.com/2013/04/java-string-to-date-conversion/
        dateString = dateString.substring(0, 10);
        String divider = "-";
        String[] dateList = dateString.split(divider);
        String year = dateList[0];
        String month = dateList[1];
        String day = dateList[2];
        return month + divider + day + divider + year;
    }
}