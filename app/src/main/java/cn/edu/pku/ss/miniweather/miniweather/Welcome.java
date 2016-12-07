package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.pku.ss.miniweather.R;

/**
 * Created by hasee on 2016/12/7.
 */
//实现欢迎页面自动跳转到引导页面或主界面
public class Welcome extends Activity {
    private SharedPreferences preferences;//用于记录是不是第一次打开程序
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        preferences = getSharedPreferences("count", Context.MODE_PRIVATE);
        int count = preferences.getInt("count", 0);
        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面

        if (count == 0) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), Guide.class);
                    startActivity(intent);
                }
            };
            timer.schedule(task, 1000 * 2);//2秒后自动跳转
        }
        else{
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            };
            timer.schedule(task, 1000 * 2);//2秒后自动跳转

        }
        editor = preferences.edit();
        //存入数据
        editor.putInt("count", ++count);
        //提交修改
        editor.commit();
    }
}


