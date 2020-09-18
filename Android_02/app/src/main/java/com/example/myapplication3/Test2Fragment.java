package com.example.myapplication3 ;


import android.app.AlertDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.os.Bundle ;
import android.support.v4.app.Fragment ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.TextView ;

import java.util.List ;


/**
 * A simple {@link Fragment} subclass.
 */

public class Test2Fragment extends Fragment
{
    List<Page> pages ;
    TextView TitleTV ;
    TextView ContentTV ;

    public Test2Fragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context) ;
        pages = Util.getPages(context) ;
    }

    public void setContentId(int position)
    {
        TitleTV.setText(pages.get(position).title) ;
        ContentTV.setText(pages.get(position).content) ;

        if( position == 0 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week1").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 1 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week2").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 2 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week3").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 3 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week4").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else{
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test2, container, false) ;

        TitleTV = (TextView) view.findViewById(R.id.textView1) ;
        ContentTV = (TextView) view.findViewById(R.id.textView2) ;

        return view ;
    }
}

    /**
    public void showView(int position)
    {
        if( position == 0 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week1").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 1 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week2").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 2 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week3").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else if( position == 3 )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
            builder.setMessage("About Week4").setTitle("Success Renewal") ;

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // do something
                }
            }) ;

            AlertDialog dialog = builder.create() ;
            dialog.show() ;
        }else {
        }
    }
}*/
