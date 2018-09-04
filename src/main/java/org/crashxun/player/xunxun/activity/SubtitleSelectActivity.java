package org.crashxun.player.xunxun.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import org.crashxun.player.R;
import org.crashxun.player.activities.VideoActivity;
import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.fragment.FileBrowerFragment;
import org.crashxun.player.xunxun.samba.SmbFileUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.crashxun.player.xunxun.common.Constant.ACTION_MAIN_FILEBROWER_FILESELECTED;
import static org.crashxun.player.xunxun.common.Constant.KEY_PARAMS_ACTIVITY;

/**
 * Created by xunxun on 2018/2/22.
 */

public class SubtitleSelectActivity extends FragmentActivity implements FileBrowerFragment.KeyEventDispatcher {
    FileBrowerFragment f = null;
    private Handler mHandler = new Handler();

    String TAG = "SubtitleSelectActivity_xunxun";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitle_select);
//        bindListener(this);
        f = FileBrowerFragment.newInstance(ACTION_MAIN_FILEBROWER_FILESELECTED);
        f.setSupportVideoSuffix(new String[] {"srt","ass"});
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.brower, f);
        transaction.commit();

        f.setListener(new FileBrowerFragment.MenuEventListener() {
            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onShow() {

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                f.setUserVisibleHint(true);
            }
        }, 200);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_MENU) {
//
//
//        }
//        return super.onKeyUp(keyCode, event);
//    }


    BroadcastReceiver fileSelectReceiver;

    void registReceiver() {
        if (fileSelectReceiver == null) {
            fileSelectReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(ACTION_MAIN_FILEBROWER_FILESELECTED)) {
                        String path = intent.getStringExtra(KEY_PARAMS_ACTIVITY);

                        String name = path;

                        if (!checkFile(path)) {
                            Toast.makeText(SubtitleSelectActivity.this, "请选择srt或ass文件", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (path.startsWith("smb://")) {
//                            try {
//                                path = URLEncoder.encode(path, "utf-8");
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }

                            File file = new File(context.getObbDir().getPath()+"/"+path.substring(path.lastIndexOf("/")+1));
                            Log.d(TAG,"file:"+file.getAbsolutePath());
                            //下载文件
                            SmbFileUtils.downloadFile(SubtitleSelectActivity.this, path, file.getAbsolutePath(), new SmbFileUtils.SMBDownloadListener() {
                                @Override
                                public void onSuccess(final String path) {
                                    Log.d(TAG,"onSuccess path:"+path);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            handlerFile(path);
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(String msg) {
                                    Log.d(TAG,"onFailed msg:"+msg);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SubtitleSelectActivity.this,"字幕下载失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
//                           play("http://" + PlayFileService.IP + ":" + PlayFileService.PORT + "/" + path, name);
                        } else {
                            //返回文件路径
                            handlerFile(path);
//                           play(path,name);
                        }
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ACTION_MAIN_FILEBROWER_FILESELECTED);
            registerReceiver(fileSelectReceiver, intentFilter);
        }
    }

    boolean checkFile(String path) {
        boolean ret = false;
        if (!TextUtils.isEmpty(path) && (path.toLowerCase().endsWith(".srt")
                || path.toLowerCase().endsWith(".ass")))
            ret = true;
        return ret;
    }

    void handlerFile(String path) {
        Log.d(TAG,"handlerFile path:"+path);
        Intent intent = new Intent(Constant.ACTION_SUBTITLE_LOAD_FINISH);
        intent.putExtra(Constant.KEY_PARAMS_SUBTITLE,path);
        sendBroadcast(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregistReceiver();
    }

    private void unregistReceiver() {
        if (fileSelectReceiver != null) {
            unregisterReceiver(fileSelectReceiver);
            fileSelectReceiver = null;
        }
    }


    private void play(String path, String name) {
        Intent intentPlayer = new Intent(this, VideoActivity.class);
        intentPlayer.putExtra("videoPath", path);
        intentPlayer.putExtra("videoName", name);
        startActivity(intentPlayer);
    }


    boolean responseKeyEvent = true;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (responseKeyEvent)
            return super.dispatchKeyEvent(event);
        else
            return true;
    }

    @Override
    public void setResponse(boolean flag) {
        this.responseKeyEvent = flag;
    }


}
