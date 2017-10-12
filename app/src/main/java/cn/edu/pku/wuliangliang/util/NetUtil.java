package cn.edu.pku.wuliangliang.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telecom.ConnectionRequest;

/**
 * Created by WLL on 2017/10/11.
 */

public class NetUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;

    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NETWORK_NONE;
        }

        int netType = networkInfo.getType();
        if (netType == ConnectivityManager.TYPE_MOBILE) {
            return NETWORK_MOBILE;
        } else if (netType == ConnectivityManager.TYPE_WIFI){
            return NETWORK_WIFI;
        }

        return NETWORK_NONE;
    }
}
