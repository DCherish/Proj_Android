package com.example.myminiproject ;

import android.content.Context ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.BaseAdapter ;
import android.widget.CompoundButton ;
import android.widget.ImageView ;
import android.widget.Switch ;
import android.widget.TextView ;


import java.util.ArrayList ;

public class LikeListViewAdapter extends BaseAdapter
{
    private ArrayList<LikeListView> likeArrayList = new ArrayList<LikeListView>() ;

    public LikeListViewAdapter()
    {
        // Required empty public constructor
    }

    @Override
    public int getCount()
    {
        return likeArrayList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position ;
        final Context context = parent.getContext() ;

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            convertView = inflater.inflate(R.layout.like_listview, parent, false) ;
        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewLike) ;
        TextView latTextView = (TextView) convertView.findViewById(R.id.likeviewText2) ;
        TextView lngTextView = (TextView) convertView.findViewById(R.id.likeviewText4) ;
        TextView locTextView = (TextView) convertView.findViewById(R.id.likeviewText6) ;
        final Switch likeswitch = (Switch) convertView.findViewById(R.id.likeswitch) ;

        final LikeListView likeListView = likeArrayList.get(pos) ;

        latTextView.setText(likeListView.getLatitude().toString()) ;
        lngTextView.setText(likeListView.getLongitude().toString()) ;
        locTextView.setText(likeListView.getLocation()) ;

        likeswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if( isChecked )
                {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24px) ;
                    SharedPreferenceUtil.putSharedPreference(context, "swstate"+pos, 1) ;
                    likeListView.setState(1);
                }else if( !isChecked )
                {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_down_24px) ;
                    SharedPreferenceUtil.putSharedPreference(context, "swstate"+pos, 0) ;
                    likeListView.setState(0);
                }
            }
        }) ;

        if( likeListView.getState() == 0 )
        {
            imageView.setImageResource(R.drawable.ic_baseline_thumb_down_24px) ;
            likeswitch.setChecked(false) ;
        }

        if( likeListView.getState() == 1 )
        {
            imageView.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24px) ;
            likeswitch.setChecked(true) ;
        }

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
        return likeArrayList.get(position) ;
    }

    public void addlikeItem(Double lat, Double lng, String loc, Integer ss)
    {
        LikeListView likeitem = new LikeListView() ;

        likeitem.setLatitude(lat) ;
        likeitem.setLongitude(lng) ;
        likeitem.setLocation(loc) ;
        likeitem.setState(ss) ;

        likeArrayList.add(likeitem) ;
    }
}