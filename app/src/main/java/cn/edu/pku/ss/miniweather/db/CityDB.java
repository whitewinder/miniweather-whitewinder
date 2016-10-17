//package cn.edu.pku.ss.miniweather.db;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.edu.pku.ss.miniweather.cn.edu.pku.ss.bean.City;
//
///**
// * Created by hasee on 2016/10/12.
// */
//
//public class CityDB {
//    public static final String CITY_DB_NAME="city.db";
//    private static final String CITY_TABLE_NAME="city";
//    private SQLiteDatabase db;
//
//    public CityDB(Context context,String path) {
//        db=context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
//    }
//    public List<City> getallcity(){
//        List<City> list=new ArrayList<City>();
//        Cursor c=db.rawQuery("SELECT * from"+CITY_TABLE_NAME,null);
//        while(c.moveToNext()){
//            String province=c.getString(c.getColumnIndex("province"));
//            String city=c.getString(c.getColumnIndex("city"));
//            String number=c.getString(c.getColumnIndex("number"));
//            String firstPY=c.getString(c.getColumnIndex("firstpy"));
//            String allPY=c.getString(c.getColumnIndex("allpy"));
//            String allfirstPY=c.getString(c.getColumnIndex("allfirstpy"));
//            City item=new City(province, city,number, firstPY, allPY, allfirstPY);
//            list.add(item);
//        }
//        return list;
//
//
//    }
//
//}
