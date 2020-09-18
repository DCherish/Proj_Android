package com.example.myminiproject ;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageListViewAdapter extends BaseAdapter
{
    private ArrayList<ImageListView> imageArrayList = new ArrayList<ImageListView>() ;

    public ImageListViewAdapter()
    {
        // Required empty public constructor
    }

    @Override
    public int getCount()
    {
        return imageArrayList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position ;
        final Context context = parent.getContext() ;

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            convertView = inflater.inflate(R.layout.image_listview, parent, false) ;
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.listviewImage) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.listviewText2) ;
        TextView contentTextView = (TextView) convertView.findViewById(R.id.listviewText4) ;

        ImageListView imageListView = imageArrayList.get(position) ;

        titleTextView.setText(imageListView.getTitle()) ;
        contentTextView.setText(imageListView.getContent()) ;
        imageView.setImageDrawable(imageListView.getImage()) ;

        return convertView ;
    }

    @Override
    public long getItemId(int position)
    {
        return position ;
    }

    @Override
    public Object getItem(int position)
    {
        return imageArrayList.get(position) ;
    }

    public void addItem(String title, String content, Drawable image)
    {
        ImageListView item = new ImageListView() ;

        item.setTitle(title) ;
        item.setContent(content) ;
        item.setImage(image) ;

        imageArrayList.add(item) ;
    }
}