package org.crashxun.player.xunxun.samba;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.service.PlayFileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

/**
 * Created by xunxun on 2018/2/21.
 */

public class SmbFileUtils {
    private static final String TAG = "SmbFileUtils_xunxun";

    private static Map<String, NtlmPasswordAuthentication> authMap = new HashMap<>();
    private List<FoldAuthInfo> foldAuthInfos;

    private NtlmPasswordAuthentication curAuthentication = null;
    private String IP = null;
    private Context mContext;

    public void setCurAuthentication(NtlmPasswordAuthentication curAuthentication) {
        this.curAuthentication = curAuthentication;
    }


    Handler handler = null;

    public SmbFileUtils(Context context, String ip) {
        handler = new Handler(context.getMainLooper());
        mContext = context;
        this.IP = ip;
    }

    public interface CheckIPListener {
        void onSuccess(String ip);

        void onFailed(String ip);
    }

    public interface SmbListFilesListener {
        void onSuccess(SmbFile[] smbFiles);

        void onAuthFailed(String path);

        void onHostUnconnected(String path);
    }

    public UniAddress check(String ip, CheckIPListener checkIPListener) {
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


    private NtlmPasswordAuthentication getAuthentication(String username, String password) {
        Log.d(TAG, "getAuthentication ip:" + IP + " username:" + username + " password:" + password);

        NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(IP, username, password);
        try {
            SmbSession.logon(check(IP, null), mAuthentication);
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

    public void listSmbFiles(String path, SmbListFilesListener smbListFilesListener) {
        listSmbFiles(path, curAuthentication, smbListFilesListener);
    }

    public void listSmbFiles(String path, String username, String password, SmbListFilesListener smbListFilesListener) {
        listSmbFiles(path, getAuthentication(username, password), smbListFilesListener);
    }

    ExecutorService executors = Executors.newCachedThreadPool();

    public void listSmbFilesForAndroid(final String path, final String username, final String password, final SmbListFilesListener smbListFilesListener) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                listSmbFiles(path, username, password, smbListFilesListener);
            }
        });
    }

    public void listSmbFilesForAndroid(final String path, final SmbListFilesListener smbListFilesListener) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                listSmbFiles(path, smbListFilesListener);
            }
        });
    }


    private void listSmbFiles(final String path, NtlmPasswordAuthentication mAuthentication, final SmbListFilesListener smbListFilesListener) {
        Log.d(TAG, "listSmbFiles path:" + path + " mAuthentication:" + mAuthentication);
        String rootPath = path;
        if (path.lastIndexOf("smb://") != 0) {
            rootPath = "smb://" + rootPath;
        }
        if (!path.endsWith("/")) {
            rootPath += "/";
        }
        // 获取跟目录然后获取下面各个盘符
        SmbFile mRootFolder = null;
        SmbFile[] files = null;
        // 匿名登录即无需登录
        try {
            if (mAuthentication == null) {
                mRootFolder = new SmbFile(rootPath);
            } else {
                Log.d(TAG, "listSmbFiles  mAuthentication:" + mAuthentication.getUsername());
                mRootFolder = new SmbFile(rootPath, mAuthentication);

            }
            files = mRootFolder.listFiles();

            if (mAuthentication != null) {
                authMap.put(path, mAuthentication);
                saveFoldAuthInfo(new FoldAuthInfo(path, mAuthentication.getUsername(), mAuthentication.getPassword()));
                curAuthentication = mAuthentication;
                PlayFileService.setAuth(curAuthentication);
            }
            final SmbFile[] finalFiles = files;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //无授权记录
                    if (smbListFilesListener != null)
                        smbListFilesListener.onSuccess(finalFiles);
                }
            });

            for (SmbFile file : files) {
//                Log.e(TAG, "getCanonicalPath:" + file.getCanonicalPath());
//                Log.e(TAG,"getDfsPath:"+file.getDfsPath());
                Log.e(TAG, "getName:" + file.getName());
//                Log.e(TAG, "canRead:" + file.canRead());
//                Log.e(TAG, "getParent:" + file.getParent());
//                Log.e(TAG, "getPath:" + file.getPath());
//                Log.e(TAG, "getServer:" + file.getServer());
//                Log.e(TAG, "getShare:" + file.getShare());
//                Log.e(TAG, "getUncPath:" + file.getUncPath());
//                Log.e(TAG, "---------------------------------");

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "MalformedURLException:" + e.getMessage());
            if (smbListFilesListener != null)
                smbListFilesListener.onHostUnconnected(path);
        } catch (SmbAuthException e) {
            e.printStackTrace();
            Log.e(TAG, "无权限");
            Log.e(TAG, "SmbAuthException:" + e.getMessage());
            if (mAuthentication != null) {
                Log.e(TAG, "mAuthentication domain:" + mAuthentication.getDomain());
                Log.e(TAG, "mAuthentication username:" + mAuthentication.getUsername());
                Log.e(TAG, "mAuthentication password:" + mAuthentication.getPassword());
            }

            if (mAuthentication == null) {
                //尝试已缓存的授权
                NtlmPasswordAuthentication authentication = getCacheAuth(path);
                if (authentication != null) {
                    listSmbFiles(path, authentication, smbListFilesListener);
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //无授权记录
                            if (smbListFilesListener != null)
                                smbListFilesListener.onAuthFailed(path);
                        }
                    });
                }
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //failed
                        if (smbListFilesListener != null)
                            smbListFilesListener.onAuthFailed(path);
                    }
                });
            }
        } catch (SmbException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Failed to connect")) {
                Log.e(TAG, "连接主机失败");
            } else {
                Log.e(TAG, "SmbException:" + e.getMessage());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (smbListFilesListener != null)
                        smbListFilesListener.onHostUnconnected(path);
                }
            });

        }
    }

    private NtlmPasswordAuthentication getCacheAuth(String path) {
        NtlmPasswordAuthentication authentication = null;
        authentication =authMap.get(path);

        if (authentication == null) {
            FoldAuthInfo foldAuthInfo = getFoldAuthInfo(path);
            if (foldAuthInfo != null) {
                authentication = getAuthentication(foldAuthInfo.username, foldAuthInfo.password);
            }
        }
        return authentication;
    }

    private class FoldAuthInfo {

        String path;
        String username;
        String password;

        public String getSaveData() {
            return username + "|" + password;
        }

        public FoldAuthInfo(String path, String username, String password) {
            this.path = path;
            this.username = username;
            this.password = password;
        }

        public FoldAuthInfo(String path, String saveData) {
            this.path = path;
            this.username = saveData.split("\\|")[0];
            this.password = saveData.split("\\|")[1];
            Log.d(TAG, "saveData:" + saveData + " username:" + username + " password:" + password);
        }
    }

    private void saveFoldAuthInfo(FoldAuthInfo foldAuthInfo) {
        SharedPreferencesUtil.saveData(mContext, IP, foldAuthInfo.path, foldAuthInfo.getSaveData());
    }

    private FoldAuthInfo getFoldAuthInfo(String path) {
        Log.d(TAG, "getFoldAuthInfo path:" + path);
        FoldAuthInfo ret = null;
        String saveData = (String) SharedPreferencesUtil.getData(mContext, IP, path, "");
        Log.d(TAG, "getFoldAuthInfo saveData:" + saveData);

        if (!TextUtils.isEmpty(saveData)) {
            ret = new FoldAuthInfo(path, saveData);
        }
        Log.d(TAG, "getFoldAuthInfo ret:" + ret);

        return ret;
    }

    public static void downloadFile(final Context context, final String src, final String target, final SMBDownloadListener listener) {
        Log.d(TAG,"downloadFile src:"+src+" target:"+target);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String regEx="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(src);

                String ip = null;
                if (m.find()) {
                    ip=m.group();
//break;   加break则提取string中的一个IP
                }

                if(TextUtils.isEmpty(ip)) {
                    listener.onFailed("illegal ip :"+src);
                } else {
                    try {
                        if(new File(target).exists()) {
                            new File(target).delete();
                        }
                        String parent = src.substring(0,src.lastIndexOf("/")+1);
                        SmbFileUtils smbFileUtils = new SmbFileUtils(context,ip);
                        SmbFile file = new SmbFile(src,smbFileUtils.getCacheAuth(parent));
                        Log.d(TAG,"parent:"+parent);
                        FileOutputStream fileOutputStream = new FileOutputStream(target);
                        InputStream inputStream = file.getInputStream();
                        byte[] buff = new byte[1024];
                        int count = 0;
                        while ((count = inputStream.read(buff))>0) {
                            Log.d(TAG,"count:"+count);
                            fileOutputStream.write(buff,0,count);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        inputStream.close();
                        listener.onSuccess(target);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        listener.onFailed("MalformedURLException :"+e.getMessage());
                    } catch (SmbAuthException e) {
                        e.printStackTrace();
                        listener.onFailed("SmbAuthException :"+e.getMessage());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        listener.onFailed("FileNotFoundException :"+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.onFailed("IOException :"+e.getMessage());
                    }
                }
            }
        }).start();
    }

    public interface SMBDownloadListener {
        void onSuccess(String path);

        void onFailed(String msg);
    }

}
