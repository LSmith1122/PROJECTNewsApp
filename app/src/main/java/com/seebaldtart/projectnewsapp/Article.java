package com.seebaldtart.projectnewsapp;
import android.content.Context;
import android.graphics.Bitmap;
class Article {
    private String mTITLE;
    private String mAuthor;
    private String mBody;
    private String mURL;
    private String mDATE;
    private String mSECTION;
    private Bitmap mImage;
    public Article(Context context, String title, String author, String body, String date, String section, String url, Bitmap thumbnail) {
        mTITLE = QueryUtils.checkEntry(context, title);
        mAuthor = QueryUtils.checkEntry(context, author);
        mBody = QueryUtils.checkEntry(context, body);
        mDATE = QueryUtils.checkEntry(context, date);
        mSECTION = QueryUtils.checkEntry(context, section);
        mURL = url;
        mImage = thumbnail;
    }
    public String getArticleTitle() { return mTITLE; }
    public String getAuthor() { return mAuthor; }
    public String getBody() { return mBody; }
    public String getArticleURL() { return mURL; }
    public String getArticleDate() { return mDATE; }
    public String getArticleSection() { return mSECTION; }
    public Bitmap getArticleImage() { return mImage; }
    public boolean hasImage() { return mImage != null; }
}