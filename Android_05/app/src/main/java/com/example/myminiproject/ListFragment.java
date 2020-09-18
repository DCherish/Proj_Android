package com.example.myminiproject ;

import android.content.Context ;
import android.database.Cursor ;
import android.database.sqlite.SQLiteDatabase ;
import android.graphics.Bitmap ;
import android.graphics.BitmapFactory ;
import android.graphics.drawable.BitmapDrawable ;
import android.graphics.drawable.Drawable ;
import android.os.Bundle ;
import android.support.v4.app.Fragment ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.AdapterView ;
import android.widget.ListView ;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment
{
    AdapterView.OnItemClickListener mCallback = null ;

    String dbName = "data_information.db" ;
    int dbVersion = 1 ;
    private MySQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    String tableName = "datainfo" ;

    // SQLite를 이용하기 위해 선언

    public ListFragment()
    {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        return new ListFragment() ;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context) ;

        if( context instanceof AdapterView.OnItemClickListener )
        {
            mCallback = (AdapterView.OnItemClickListener) context ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list, container, false) ;

        ListView listView ;
        ImageListViewAdapter adapter ;
        adapter = new ImageListViewAdapter() ;
        listView = (ListView) view.findViewById(R.id.listview1) ;

        helper = new MySQLiteOpenHelper(getActivity(), dbName, null, dbVersion) ;

        // SQLite를 사용하여 "data_information.db"에 Data 저장

        db = helper.getReadableDatabase() ;

        // Data 읽기기능을 사용할 것이므로 getReadableDatabase로 선언

        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

        // 'datainfo'의 table에 저장된 Data에 접근

        while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
        {
            String title = c.getString(1) ;
            String content = c.getString(2) ;
            byte[] imgview = c.getBlob(5) ;

            Drawable drawable = new BitmapDrawable(getResources(), getDrawableFromByteArray(imgview)) ;

            adapter.addItem(title, content, drawable) ;

            // 각 행의 Data를 각 List에 추가하여 설정
        }

        c.close() ;

        // 'datainfo' table 참조 close

        listView.setAdapter(adapter) ;
        adapter.notifyDataSetChanged() ;

        // listview 갱신

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) // 만약 listview의 한 항목이 클릭된다면
            {
                if( mCallback != null )
                {
                    mCallback.onItemClick(adapterView, view, position, id) ;

                    // 이벤트 콜백을 만들어 부모 activity와 통신하도록 구현
               }
            }
        }) ;

        return view ;
    }

    public Bitmap getDrawableFromByteArray(byte[] b)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length) ;
        return bitmap ;
    }

    // SQLite에 저장되어 있던 이미지를 imageView로 설정하기 위하여 Data Format을 변환하는 함수 구현
}
