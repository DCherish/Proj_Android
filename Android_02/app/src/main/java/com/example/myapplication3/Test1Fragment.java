package com.example.myapplication3 ;

import android.content.Context ;
import android.os.Bundle ;
import android.support.v4.app.Fragment ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.ListView ;

/**
 * A simple {@link Fragment} subclass.
 */
public class Test1Fragment extends Fragment
{
    AdapterView.OnItemClickListener mCallback = null ;

    public Test1Fragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context) ;

        if(context instanceof AdapterView.OnItemClickListener)
        {
            mCallback = (AdapterView.OnItemClickListener)context ;
        }
    }

    static final String[] LIST_MENU = {"1주차", "2주차", "3주차", "4주차"} ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_test1, container, false) ;

        ListView listView = view.findViewById(R.id.listview) ;

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU) ;

        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id)
            {
                if( mCallback != null )
                {
                    mCallback.onItemClick(adapterView, view, position, id) ;
                }
            }
        }) ;

        return view ;
    }
}
