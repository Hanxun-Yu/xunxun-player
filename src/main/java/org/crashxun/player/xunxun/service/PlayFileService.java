package org.crashxun.player.xunxun.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.cybergarage.http.HTTPServerList;

import jcifs.smb.NtlmPasswordAuthentication;

public class PlayFileService extends Service {

    private static FileServer fileServer = new FileServer();;

    public static String IP;
    public static int PORT;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fileServer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        HTTPServerList httpServerList = fileServer.getHttpServerList();
        httpServerList.stop();
        httpServerList.close();
        httpServerList.clear();
        fileServer.interrupt();

    }

    public static void setAuth(NtlmPasswordAuthentication auth) {
        fileServer.setmNtlmPasswordAuthentication(auth);
    }
}