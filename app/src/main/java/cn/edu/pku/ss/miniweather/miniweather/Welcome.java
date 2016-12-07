package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.pku.ss.miniweather.R;

/**
 * Created by hasee on 2016/12/7.
 */
//实现欢迎页面自动跳转到引导页面或主界面
public class Welcome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        final Intent it=new Intent(this,Guide.class);
        Timer timer=new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                startActivity(it);
            }
        };
        timer.schedule(task,1000*2);//2秒后自动跳转
    }

}


