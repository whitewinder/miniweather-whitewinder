package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.ss.miniweather.R;
import cn.edu.pku.ss.miniweather.app.MyApplication;
import cn.edu.pku.ss.miniweather.cn.edu.pku.ss.bean.City;

/**
 * Created by hasee on 2016/10/12.
 */

public class SelectCity extends Activity  {
    private ImageView back;
    private MyApplication myApp;
    Map<String,String> map=new HashMap<String,String>();
    String selectcitycode;
    private EditText edit_search;
    private ArrayAdapter<String> adapter;
    List<City> list;
    List<City> newlist;
    String cityname[];
    String citycode[];
    ListView mlistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApplication)getApplication();
        list=myApp.getcitylist();
       cityname=new String[list.size()];
       citycode=new String[list.size()];
        setContentView(R.layout.select_city);
        mlistview=(ListView)findViewById(R.id.list_view);
        cityname=getcitynamearray(list);
        adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityname);
        init();
        mlistview.setAdapter(adapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                selectcitycode=map.get(mlistview.getItemAtPosition(i));
                Intent intent = new Intent();
                intent.putExtra("citycode", selectcitycode);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    //初始化控件
    private void init(){
        edit_search=(EditText)findViewById(R.id.search_edit);
        //为输入添加TextWatch监听文字变化
        edit_search.addTextChangedListener(new TextWatcher_Enum());
    }
   class  TextWatcher_Enum implements TextWatcher{
       @Override
       public void afterTextChanged(Editable s) {

       }

       @Override
       public void beforeTextChanged(CharSequence s, int start, int count, int after) {

       }

       @Override
       public void onTextChanged(CharSequence s, int start, int before, int count) {
           newlist=new ArrayList<City>();
           if(newlist!=null){newlist.clear();}
           if(edit_search.getText()!=null){
               String input_info=edit_search.getText().toString();
               newlist=getnewdata(input_info);
               cityname=getcitynamearray(newlist);
               adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityname);
               mlistview.setAdapter(adapter);
           }
       }
       //根据输入返回新的list
       private List<City> getnewdata(String input_info){
           //遍历list
          cityname=getcitynamearray(list);
           for(int i=0;i<list.size();i++){
               if(cityname[i].contains(input_info)){
                   newlist.add(list.get(i));
               }
           }
           return newlist;
       }
   }
    public String[] getcitynamearray(List<City> list){
        for(int i=0;i<list.size();i++){
            cityname[i]=list.get(i).getCity();
            citycode[i]=list.get(i).getNumber();
            map.put(cityname[i],citycode[i]);
        }
        return cityname;
    }
}
