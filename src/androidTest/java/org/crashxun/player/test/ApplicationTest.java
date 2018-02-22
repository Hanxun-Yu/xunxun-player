package org.crashxun.player.test;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.crashxun.player.xunxun.samba.SmbFileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;
import jcifs.smb.SmbShareInfo;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    String TAG = "ApplicationTest_xunxun";

    public void testSmb() {
        String ip = "10.1.1.200/system";
        String username = "tvp";
        String password = "mmmmmm";
//        listSmbFiles(ip);

//        listSmbFiles(ip + "/html");
//        listSmbFiles(ip + "/shared");
//        listSmbFiles(ip,"system/",getAuthentication(ip,username,password));


//        SmbFileUtils sfu = new SmbFileUtils();
        getAuthentication(ip,username,password);
    }

    public UniAddress check(String ip, SmbFileUtils.CheckIPListener checkIPListener) {
        UniAddress mDomain = null;
        try {
            mDomain = UniAddress.getByName(ip);
            if (checkIPListener != null)
                checkIPListener.onSuccess(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "ip错误");
            if (checkIPListener != null)
                checkIPListener.onFailed(ip);
        }
        return mDomain;
    }

    private NtlmPasswordAuthentication getAuthentication(String ip, String username, String password) {
        Log.d(TAG, "getAuthentication");

        NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(ip, username, password);
        try {
            SmbSession.logon(check(ip, null), mAuthentication);
        } catch (SmbAuthException e) {
            e.printStackTrace();
            Log.e(TAG, "用户名密码错误");
            mAuthentication = null;
        } catch (SmbException e) {
            e.printStackTrace();

            String msg = e.getMessage();
            if (msg.contains("Failed to connect")) {
                Log.e(TAG, "连接主机失败");
            }
            Log.e(TAG, e.getMessage());
            mAuthentication = null;
        }
        return mAuthentication;
    }


}