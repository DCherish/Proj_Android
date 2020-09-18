package com.example.myminiproject ;

import android.graphics.drawable.Drawable ;

public class ImageListView
{
    private Drawable image ;
    private String title ;
    private String content ;

    public void setTitle(String t)
    {
        title = t ;
    }

    public void setContent(String c)
    {
        content = c ;
    }

    public void setImage(Drawable i)
    {
        image = i ;
    }

    public String getTitle() {
        return this.title ;
    }

    public String getContent() {
        return this.content ;
    }

    public Drawable getImage()
    {
        return this.image ;
    }
}