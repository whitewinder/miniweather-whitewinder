package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.miniweather.R;
import cn.edu.pku.ss.miniweather.Util.NetUtil;
import cn.edu.pku.ss.miniweather.cn.edu.pku.ss.bean.TodayWeather;

/**
 * Created by hasee on 2016/9/27.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView updatebtn,sharebtn;
    private ProgressBar updateprogress;
    private TextView city, time, shidu, wendu, pmdata, quality, city_name;
    private TextView[] week=new TextView[7];
    private TextView[] temperature=new TextView[7];
    private TextView[]type=new TextView[7];
    private TextView[]wind=new TextView[7];
    private ImageView weatherImg, pmImg;
    private ImageView selectcity;
    private ImageView share;
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    private TodayWeather[] weathers;


    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateWeathers((TodayWeather[])msg.obj);//更新主界面，一下更新今天和未来六天的
            break;
            default:
            break;
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        updatebtn = (ImageView) findViewById(R.id.title_update);
        sharebtn=(ImageView) findViewById(R.id.share);
        updatebtn.setOnClickListener(this);
        sharebtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myweather", "网络ok");
            Toast.makeText(MainActivity.this, "网络ok", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myweather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }
        selectcity = (ImageView) findViewById(R.id.selectcity);
        selectcity.setOnClickListener(this);
        initview();
        initviews();
        queryWeatherCode("101010100");//初始化先定位北京
    }

    void initview() {
        city_name = (TextView) findViewById(R.id.city_name);
        city = (TextView) findViewById(R.id.city);
        time = (TextView) findViewById(R.id.updatetime);
        shidu = (TextView) findViewById(R.id.shidu);
        wendu = (TextView) findViewById(R.id.wendu);
        pmdata = (TextView) findViewById(R.id.pmdata);
        quality = (TextView) findViewById(R.id.quality);
        pmImg = (ImageView) findViewById(R.id.pmimg);
        week[0] = (TextView) findViewById(R.id.week);
        temperature[0] = (TextView) findViewById(R.id.temperature);
        type[0] = (TextView) findViewById(R.id.type);
        wind[0] = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weatherimg);
        updateprogress=(ProgressBar) findViewById(R.id.title_update_progress);
        updateprogress.setVisibility(View.GONE);

    }
    private void initviews(){
        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.future1,null));
        views.add(inflater.inflate(R.layout.future2,null));
        vpAdapter=new ViewPagerAdapter(views,this);
        vp=(ViewPager)findViewById(R.id.future);
        vp.setAdapter(vpAdapter);

        week[1]= (TextView)views.get(0).findViewById(R.id.week1);//太聪明了，要加上views.get(0)，不是vp，不加找不到该控件
        week[2]= (TextView)views.get(0). findViewById(R.id.week2);
        week[3]= (TextView) views.get(0).findViewById(R.id.week3);
        week[4]= (TextView) views.get(1).findViewById(R.id.week4);
        week[5]= (TextView)views.get(1). findViewById(R.id.week5);
        week[6]= (TextView) views.get(1).findViewById(R.id.week6);
        type[1] = (TextView) views.get(0).findViewById(R.id.type1);
        type[2] = (TextView)views.get(0). findViewById(R.id.type2);
        type[3] = (TextView) views.get(0).findViewById(R.id.type3);
        type[4] = (TextView) views.get(1).findViewById(R.id.type4);
        type[5] = (TextView)views.get(1). findViewById(R.id.type5);
        type[6] = (TextView) views.get(1).findViewById(R.id.type6);
        temperature[1] = (TextView) views.get(0).findViewById(R.id.temperature1);
        temperature[2] = (TextView) views.get(0).findViewById(R.id.temperature2);
        temperature[3] = (TextView)views.get(0). findViewById(R.id.temperature3);
        temperature[4] = (TextView)views.get(1). findViewById(R.id.temperature4);
        temperature[5] = (TextView) views.get(1).findViewById(R.id.temperature5);
        temperature[6] = (TextView) views.get(1).findViewById(R.id.temperature6);
        wind[1] = (TextView)views.get(0). findViewById(R.id.wind1);
        wind[2] = (TextView)views.get(0). findViewById(R.id.wind2);
        wind[3] = (TextView) views.get(0).findViewById(R.id.wind3);
        wind[4] = (TextView)views.get(1). findViewById(R.id.wind4);
        wind[5] = (TextView)views.get(1). findViewById(R.id.wind5);
        wind[6] = (TextView)views.get(1). findViewById(R.id.wind6);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_update) {
            updatebtn.setVisibility(view.GONE);
            updateprogress.setVisibility(view.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)sharebtn.getLayoutParams();
            params.addRule(RelativeLayout.LEFT_OF, R.id.title_update_progress);
            sharebtn.setLayoutParams(params);
              String citycode="101010100";
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myweather", "网络ok");
                queryWeatherCode(citycode);
            } else {
                Log.d("myweather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.selectcity) {
        Intent i = new Intent(MainActivity.this, SelectCity.class);//关联对应activity
        startActivityForResult(i, 1);//1是请求码
    }
        if (view.getId() == R.id.share){
            //1、屏幕截图
            int width = getWindow().getDecorView().getRootView().getWidth();
            int height = getWindow().getDecorView().getRootView().getHeight();
            View view00 =  getWindow().getDecorView().getRootView();
            view00.setDrawingCacheEnabled(true);
            view00.buildDrawingCache();
            Bitmap temBitmap = view00.getDrawingCache();
            //weatherImg.setImageBitmap(temBitmap);//设置图片
            //2、利用系统intent实现分享，将屏幕截图分享出去
            //仅仅分享文字
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_TEXT, "这里是分享内容");
//            intent.setType("text/plain");
//            //设置分享列表的标题，并且每次都显示分享列表
//            startActivity(Intent.createChooser(intent, "分享到"));
            //分享屏幕截图
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, temBitmap);
//            shareIntent.setType("image/*");
//            startActivity(Intent.createChooser(shareIntent, "分享到"));
        }
}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//requestcode用于标识请求来源；resultcode用于标识返回来源
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newcitycode = data.getStringExtra("citycode");
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myweather", "网络ok");
                queryWeatherCode(newcitycode);
            } else {
                Log.d("myweather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String citycode) {//拼接URL
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + citycode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                    }
                    String responseStr = response.toString();
                    Log.d("myweather", responseStr);
                    weathers= parseXML(responseStr);//1 解析返回weather数组
                    if (weathers != null) {//解析回来的数据更新主界面
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = weathers;
                        mhandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }

            }
        }).start();
    }
//解析返回weather列表
    private TodayWeather[] parseXML(String xmldate) {
        weathers=new TodayWeather[7];
        for(int i=0;i<7;i++){
            weathers[i]=new TodayWeather();
        }
        int fengxiangcount = 0;
        int fenglicount = 0;
        int datecount = 0;
        int highcount = 0;
        int lowcount = 0;
        int typecount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldate));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                weathers[0].setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangcount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setFengxiang(xmlPullParser.getText());
                                fengxiangcount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fenglicount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglicount == 1) {
                                eventType = xmlPullParser.next();
                                weathers[1].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fenglicount == 2) {
                                eventType = xmlPullParser.next();
                                weathers[2].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fenglicount == 3) {
                                eventType = xmlPullParser.next();
                                weathers[3].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fenglicount == 4) {
                                eventType = xmlPullParser.next();
                                weathers[4].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fenglicount == 5) {
                                eventType = xmlPullParser.next();
                                weathers[5].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fenglicount == 6) {
                                eventType = xmlPullParser.next();
                                weathers[6].setFengli(xmlPullParser.getText());
                                fenglicount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setDate(xmlPullParser.getText());
                                datecount++;
                            }  else if (xmlPullParser.getName().equals("date") && datecount == 1) {
                                eventType = xmlPullParser.next();
                                weathers[1].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 2) {
                                eventType = xmlPullParser.next();
                                weathers[2].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 3) {
                                eventType = xmlPullParser.next();
                                weathers[3].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 4) {
                                eventType = xmlPullParser.next();
                                weathers[4].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 5) {
                                eventType = xmlPullParser.next();
                                weathers[5].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && datecount == 6) {
                                eventType = xmlPullParser.next();
                                weathers[6].setDate(xmlPullParser.getText().substring(xmlPullParser.getText().length()-3).trim());
                                datecount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 1) {
                                eventType = xmlPullParser.next();
                                weathers[1].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 2) {
                                eventType = xmlPullParser.next();
                                weathers[2].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 3) {
                                eventType = xmlPullParser.next();
                                weathers[3].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 4) {
                                eventType = xmlPullParser.next();
                                weathers[4].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highcount == 5) {
                                eventType = xmlPullParser.next();
                                weathers[5].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            } else if (xmlPullParser.getName().equals("high") && highcount == 6) {
                                eventType = xmlPullParser.next();
                                weathers[6].setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 1) {
                                eventType = xmlPullParser.next();
                                weathers[1].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 2) {
                                eventType = xmlPullParser.next();
                                weathers[2].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 3) {
                                eventType = xmlPullParser.next();
                                weathers[3].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 4) {
                                eventType = xmlPullParser.next();
                                weathers[4].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 5) {
                                eventType = xmlPullParser.next();
                                weathers[5].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowcount == 6) {
                                eventType = xmlPullParser.next();
                                weathers[6].setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 0) {
                                eventType = xmlPullParser.next();
                                weathers[0].setType(xmlPullParser.getText());
                                typecount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 1) {
                                eventType = xmlPullParser.next();
                                weathers[1].setType(xmlPullParser.getText());
                                typecount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 2) {
                                eventType = xmlPullParser.next();
                                weathers[2].setType(xmlPullParser.getText());
                                typecount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 3) {
                                eventType = xmlPullParser.next();
                                weathers[3].setType(xmlPullParser.getText());
                                typecount++;
                            } else if (xmlPullParser.getName().equals("type") && typecount == 4) {
                                eventType = xmlPullParser.next();
                                weathers[4].setType(xmlPullParser.getText());
                                typecount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 5) {
                                eventType = xmlPullParser.next();
                                weathers[5].setType(xmlPullParser.getText());
                                typecount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typecount == 6) {
                                eventType = xmlPullParser.next();
                                weathers[6].setType(xmlPullParser.getText());
                                typecount++;
                            }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weathers;
    }

    void  updateWeathers(TodayWeather[] weathers) {
        //更新今日天气
        Log.d("myweather", weathers[0].getCity());
        city_name.setText(weathers[0].getCity()==null?"N/A":weathers[0].getCity()+ "天气");
        city.setText(weathers[0].getCity()==null?"N/A":weathers[0].getCity());
        time.setText(weathers[0].getUpdatetime()==null?"N/A":weathers[0].getUpdatetime() + "发布");
        shidu.setText("湿度" + weathers[0].getShidu()==null?"N/A":weathers[0].getShidu());
        wendu.setText("温度" + weathers[0].getWendu()==null?"N/A": weathers[0].getWendu()+ "℃");
        pmdata.setText(weathers[0].getPm25()==null?"N/A":weathers[0].getPm25());
        quality.setText(weathers[0].getQuality()==null?"N/A":weathers[0].getQuality());
        week[0].setText(weathers[0].getDate()==null?"N/A":weathers[0].getDate());
        temperature[0].setText((weathers[0].getLow()==null ||weathers[0].getHigh()==null )?"N/A~N/A":(weathers[0].getLow() + "~" + weathers[0].getHigh()));
        type[0].setText(weathers[0].getType()==null?"N/A":weathers[0].getType());

        Log.d("weather","ok");
        if(weathers[0].getType()!=null) {
            switch (weathers[0].getType()) {
                case "晴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "阴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "多云":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雾":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "暴雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "雷阵雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵冰雹":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "小雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
            }
        }
        if(weathers[0].getPm25()!=null) {
            int pm25 = Integer.parseInt(weathers[0].getPm25());
            if (pm25 >= 0 && pm25 <= 50) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm25 >= 51 && pm25 <= 100) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm25 >= 101 && pm25 <= 150) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm25 >= 151 && pm25 <= 200) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
        }
        //更新未来六天的天气
        for(int i=1;i<=6;i++){
            week[i].setText(weathers[i].getDate()==null?"N/A":weathers[i].getDate());
            temperature[i].setText((weathers[i].getLow()==null ||weathers[i].getHigh()==null )?"N/A~N/A":(weathers[i].getLow() + "~" + weathers[i].getHigh()));
            type[i].setText(weathers[i].getType()==null?"N/A":weathers[i].getType());
            wind[i].setText(weathers[i].getFengli()==null?"N/A":weathers[i].getFengli());
        }
        updatebtn.setVisibility(View.VISIBLE);
        updateprogress.setVisibility(View.GONE);
    }


}

