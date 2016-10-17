package cn.edu.pku.ss.miniweather.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hasee on 2016/10/5.
 */

public class NetUtil {
    public static final int NETWORN_NONE=0;
    public static final int NETWORN_WIFI=1;
    public static final int NETWORN_MOBILE=2;

    public static int getNetworkState(Context context){
        ConnectivityManager connManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //WiFi
        NetworkInfo.State state=connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state== NetworkInfo.State.CONNECTED ||state ==NetworkInfo.State.CONNECTING)
            return NETWORN_WIFI;
       //Mobile
        state=connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(state== NetworkInfo.State.CONNECTED ||state ==NetworkInfo.State.CONNECTING)
            return NETWORN_MOBILE;
        return NETWORN_NONE;
    }
}
