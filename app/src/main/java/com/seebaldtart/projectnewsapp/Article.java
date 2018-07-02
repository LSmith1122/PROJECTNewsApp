package com.seebaldtart.projectnewsapp;

import android.graphics.Bitmap;

class Article {
    private String mTITLE;
    private String mAuthor;
    private String mBody;
    private String mURL;
    private String mDATE;
    private String mSECTION;
    private Bitmap mImage;
    public Article(String title, String author, String body, String date, String section, String url, Bitmap thumbnail) {
        mTITLE = title;
        mAuthor = author;
        mBody = body;
        mDATE = date;
        mSECTION = section;
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
