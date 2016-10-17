package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.ss.miniweather.R;
import cn.edu.pku.ss.miniweather.Util.NetUtil;
import cn.edu.pku.ss.miniweather.cn.edu.pku.ss.bean.TodayWeather;

/**
 * Created by hasee on 2016/9/27.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView updatebtn;
    private TextView city, time, shidu, wendu, week, pmdata, quality, temperature, type, wind, city_name;
    private ImageView weatherImg, pmImg;
    private ImageView selectcity;


    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
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
        updatebtn.setOnClickListener(this);
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
        week = (TextView) findViewById(R.id.week);
        temperature = (TextView) findViewById(R.id.temperature);
        type = (TextView) findViewById(R.id.type);
        wind = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weatherimg);
        queryWeatherCode("101010100");//初始化先定位北京
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_update) {
            //通过 SharedPreferences 读取城市ID
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String citycode = sharedPreferences.getString("main_city_code", "101010100");
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myweather", "网络ok");
                queryWeatherCode(citycode);
            } else {
                Log.d("myweather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.selectcity) {
            Intent i = new Intent(this, SelectCity.class);//关联对应activity
            startActivityForResult(i, 1);//1是请求码
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//requestcode用于标识请求来源；resultcode用于标识返回来源
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newcitycode = data.getStringExtra("citycode");
            Log.d("myweather", "选择的城市代码为：" + newcitycode);

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
        Log.d("myweather", address);
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
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        //Log.d("myweather", "todayWeather!=null");
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
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

    private TodayWeather parseXML(String xmldate) {
        TodayWeather todayWeather = null;
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
            Log.d("myweather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangcount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangcount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglicount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fenglicount++;
                            } else if (xmlPullParser.getName().equals("date") && datecount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                datecount++;
                            } else if (xmlPullParser.getName().equals("high") && highcount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(3).trim());
                                highcount++;
                            } else if (xmlPullParser.getName().equals("low") && lowcount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(3).trim());
                                lowcount++;
                            } else if (xmlPullParser.getName().equals("type") && typecount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typecount++;
                            }
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
        Log.d("myweather", todayWeather.getCity());
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name.setText(todayWeather.getCity() + "天气");
        city.setText(todayWeather.getCity());
        time.setText(todayWeather.getUpdatetime() + "发布");
        shidu.setText("湿度" + todayWeather.getShidu());
        wendu.setText("温度" + todayWeather.getWendu() + "℃");
        pmdata.setText(todayWeather.getPm25());
        quality.setText(todayWeather.getQuality());
        week.setText(todayWeather.getDate());
        temperature.setText(todayWeather.getLow() + "~" + todayWeather.getHigh());
        type.setText(todayWeather.getType());
        wind.setText(todayWeather.getFengli());
        switch (todayWeather.getType()) {
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
        int pm25 = Integer.parseInt(todayWeather.getPm25());
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
}

