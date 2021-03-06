package cn.edu.pku.ss.miniweather.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.miniweather.cn.edu.pku.ss.bean.City;
import cn.edu.pku.ss.miniweather.db.CityDB;

/**
 * Created by hasee on 2016/10/12.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApp";
    private static MyApplication application;
    private CityDB citydb;
    private List<City> citylist;

    //在Application创建的时候初始化全局变量
    // 同一个应用的所有Activity都可以取到并修改这些全局变量的值
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication->oncreate");
        application = this;
        citydb = opencitydb();//实现复制项目中的文件到手机中
        initCityList();//实现将数据库记录读入list中
    }

    private void initCityList() {
        citylist = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                preparecitylist();
            }
        }).start();
    }

    private boolean preparecitylist() {
        citylist = citydb.getallcity();//返回所有记录列表，每条记录是一个city对象
        int i = 0;
        for (City city : citylist) {
            i++;
            String cityname = city.getCity();
            String citycode = city.getNumber();
            Log.d(TAG, cityname + citycode);
        }
        return true;
    }

    public List<City> getcitylist() {
        return citylist;
    }


    public static MyApplication getinstance() {
        return application;
    }

    private CityDB opencitydb() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "database1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "database1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
            }
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);//手机中路径，该文件已存在，创建CityDB对象
    }
}
