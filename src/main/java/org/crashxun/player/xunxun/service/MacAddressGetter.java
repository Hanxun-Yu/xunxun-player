package org.crashxun.player.xunxun.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by yuhanxun on 15/10/13.
 */
public class MacAddressGetter {
    /**
     * 获取wifi模块的mac地址，即使wifi是关闭的，需要添加权限 ACCESS_WIFI_STATE
     *
     * @param context
     * @return
     */
    public static String getWifiMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        Log.e("", "wifi mac : " + mac);
        return mac;
    }

    public static String getWifiIP(Context context) {
        WifiManager wifimanage=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);//获取WifiManager
        WifiInfo wifiinfo= wifimanage.getConnectionInfo();

        String ip=intToIp(wifiinfo.getIpAddress());
        return ip;
    }

    private static String intToIp(int i)  {
        return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
    }


    /**
     * 获取当前系统连接网络的网卡的mac地址
     * @return
     */
    @SuppressLint("NewApi")
    public static final String getActiveMac() {
        byte[] mac = null;
        StringBuffer sb = new StringBuffer();
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();

                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                        continue;
                    if (ip.isSiteLocalAddress())
                        mac = ni.getHardwareAddress();
                    else if (!ip.isLinkLocalAddress()) {
                        mac = ni.getHardwareAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if(mac != null){
            for(int i=0 ;i<mac.length ;i++){
                sb.append(parseByte(mac[i]));
            }
            return sb.substring(0, sb.length()-1);
        }else{
            return null;
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
//                        inetAddress.getAddress();
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("yhx", ex.toString());
        }
        return null;
    }
    private static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b)+":";
        return s.substring(s.length() - 3);
    }

}
