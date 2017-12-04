package com.fafa.visitor.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by ZhouBing on 2017/7/28.
 */
public class WiFiUtil {

    public static boolean isWiFiActive(Context inContext) {
        WifiManager mWifiManager = (WifiManager) inContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            System.out.println("**** WIFI is on");
            return true;
        } else {
            System.out.println("**** WIFI is off");
            return false;
        }
    }
}
