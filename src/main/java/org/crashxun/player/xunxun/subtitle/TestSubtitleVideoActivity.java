package org.crashxun.player.xunxun.subtitle;
/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.gson.Gson;

import org.crashxun.player.R;
import org.crashxun.player.fragments.TracksFragment;
import org.crashxun.player.widget.media.IRenderView;
import org.crashxun.player.widget.media.MediaPlayerCompat;
import org.crashxun.player.xunxun.MiPlayController;
import org.crashxun.player.xunxun.XunVideoView2;
import org.crashxun.player.xunxun.activity.SubtitleSelectActivity;
import org.crashxun.player.xunxun.api.IPlayController;
import org.crashxun.player.xunxun.common.Constant;
import org.crashxun.player.xunxun.fragment.MenuLeftFragment;
import org.crashxun.player.xunxun.menu.MenuParams;
import org.crashxun.player.xunxun.subtitle.api.ISubtitleController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;

public class TestSubtitleVideoActivity extends FragmentActivity implements TracksFragment.ITrackHolder {
    private static final String TAG = "VideoActivity_xunxun";

    private String mVideoPath;
    private String mVideoName;
    private Uri mVideoUri;

    private IPlayController mMediaController;
    private ISubtitleController mSubtitleController;
    private XunVideoView2 mVideoView;
    private boolean mBackPressed;
    private BroadcastReceiver menuEventReceiver;

    MenuParams mMenuParams;


    Button btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_xunxun_testsub);
        keepScreenLongLight(this,true);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubtitleController.switchSubtitle("/sdcard/srt1.srt");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubtitleController.switchSubtitle("/sdcard/Game.Of.Thrones.S01.E05.ass");
            }
        });
        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");
        Log.d(TAG, "videoPath:" + mVideoPath);
        mVideoName = getIntent().getStringExtra("videoName");
        Log.d(TAG, "mVideoName:" + mVideoName);

        mVideoPath = Uri.fromFile(new File("/sdcard/264/123.mp4")).toString();
//        mVideoPath = "http://video.venjean.cn/sv/589e162b-1601585d5d7/589e162b-1601585d5d7.mp4";
//        mVideoPath = "http://192.168.199.205/vod/movie/No.Escape.2015.1080p.BluRay.x264-DRONES[rarbg]/No.Escape.2015.1080p.BluRay.x264-DRONES.mkv";
//        mVideoPath = "http://10.1.1.201/Blade.Runner.2049.2017.BD1080P.X264.DTS-HD.MA.7.1.Mandarin&English.CHS-ENG.Mp4BaFans.mkv";
//        mVideoPath = "http://10.1.1.201/The.Bourne.Supremacy.2004.1080p.BluRay.DTS.x264.D-Z0N3.mkv";
//        mVideoPath = "http://10.1.1.201/Oblivion (2013) x264 1080p DD5.1+DTS (Nl subs) SAM TBS.mkv";
//        try {
//            mVideoPath = URLEncoder.encode(mVideoPath,"utf-8");
//            mVideoPath = URLDecoder.decode(mVideoPath);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        handleIntentQuest();

        mMediaController = new MiPlayController(this);
        mMediaController.setTitle(mVideoName);

        mSubtitleController = new XSubtitleController(this);
        mSubtitleController.setOnStateChangedListener(new ISubtitleController.OnStateChangedListener() {
            @Override
            public void onLoading(String path, int percent) {
                Log.d(TAG, "mSubtitleController onLoading p:" + path + ":" + percent);
            }

            @Override
            public void onStopRender(String path) {
                Log.d(TAG, "mSubtitleController onStopRender:" + path);

            }

            @Override
            public void onStartRender(String path) {
                Log.d(TAG, "mSubtitleController onStartRender:" + path);
            }
        });


        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {

                return false;
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mVideoView.setSubtitleController(mSubtitleController);
                initMenu();
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                finish();
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            boolean firstBuffering = true;

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        if (firstBuffering) {
                            firstBuffering = false;
                            setMenuParams();
                        }
                        break;
                }
                return false;
            }
        });
        mVideoView.setMediaController(mMediaController);
//        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && menuHasParams) {
            MenuLeftFragment f = (MenuLeftFragment) getSupportFragmentManager().findFragmentById(R.id.right_drawer);
            if (f != null) {
//                FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
//                transaction.remove(f);
//                transaction.commit();
//                f.setArgument(getMenuParams());
                f.setUserVisibleHint(true);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initMenu() {
        registMenuEvent();
        MenuLeftFragment f = MenuLeftFragment.newInstance();
        f.setListener(new MenuLeftFragment.MenuEventListener() {
            @Override
            public void onClose() {
                mVideoView.setFocusable(true);
            }

            @Override
            public void onShow() {
                mVideoView.setFocusable(false);
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_drawer, f);
        transaction.commit();
    }


    private void registMenuEvent() {
        Log.d(TAG, "registMenuEvent");
        menuEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "menuEventReceiver----intent:" + intent);
//                Log.d(TAG,"menuEventReceiver----intent:"+intent.getExtras());
                switch (intent.getAction()) {
                    case Constant.ACTION_AUDIO_CHANGED:
                        int trackIndex = Integer.parseInt(intent.getStringExtra(Constant.KEY_PARAMS_AUDIO));
//                        Log.d(TAG, "getCurrentPosition----:" + getCurrentPosition());
                        int position = mVideoView.getCurrentPosition();
                        selectTrack(trackIndex);
                        mVideoView.seekTo(position - 1000);
                        Log.d(TAG, "menuEventReceiver----ACTION_AUDIO_CHANGED:" + trackIndex);
                        int selectedAudioTrack = getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
                        Log.d(TAG, "menuEventReceiver----selectedAudioTrack:" + selectedAudioTrack);

                        break;

                    case Constant.ACTION_RATIO_CHANGED:
                        String params = intent.getStringExtra(Constant.KEY_PARAMS_RATIO);
                        Log.d(TAG, "menuEventReceiver----ACTION_RATIO_CHANGED:" + params);
                        int ratio = 0;
                        if (params.equals("stretch"))
                            ratio = IRenderView.AR_MATCH_PARENT;
                        else if (params.equals("adapt"))
                            ratio = IRenderView.AR_ASPECT_FIT_PARENT;
                        mVideoView.setCurrentRatio(ratio);
                        break;

                    case Constant.ACTION_SUBTITLE_CHANGED:
                        String id = intent.getStringExtra(Constant.KEY_PARAMS_SUBTITLE_ID);
                        String type = intent.getStringExtra(Constant.KEY_PARAMS_SUBTITLE_TYPE);

                        int selectedSubtitleTrack = getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
                        if (id.equals("off")) {
                            //内字幕
                            if (selectedSubtitleTrack != -1) {
                                deselectTrack(selectedSubtitleTrack);
                            }

                            //停止外挂字幕

                        } else {
                            //内嵌字幕
                            if (type.equals(Constant.VALUE_PARAMS_SUBTITLE_TYPE_INTERNAL)) {
                                trackIndex = Integer.parseInt(id);
                                Log.d(TAG, "menuEventReceiver----ACTION_SUBTITLE_CHANGED: internal:" + trackIndex);

                                for (MenuParams.MTrackInfo mTrackInfo : mMenuParams.internalSubtitleList) {
                                    if (mTrackInfo.trackIndex == trackIndex) {
                                        selectTrack(trackIndex);
                                        break;
                                    }
                                }
                            } else if (type.equals(Constant.VALUE_PARAMS_SUBTITLE_TYPE_EXTERNAL)) {
                                //外挂字幕
                                Log.d(TAG, "menuEventReceiver----ACTION_SUBTITLE_CHANGED: external:" + id);

                                String path = id;
                            }

                        }
//                        selectedSubtitleTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
//                        Log.d(TAG, "menuEventReceiver----selectedSubtitleTrack:" + selectedSubtitleTrack);

                        break;

                    case Constant.ACTION_MEDIAINFO:
                        mVideoView.showMediaInfo();
                        break;

                    case Constant.ACTION_SUBTITLE_LOAD:
                        intent = new Intent(context, SubtitleSelectActivity.class);
                        context.startActivity(intent);
                        break;

                    case Constant.ACTION_SUBTITLE_LOAD_FINISH:
                        //选完字幕
                        String path = intent.getStringExtra(Constant.KEY_PARAMS_SUBTITLE);
                        addExternalSubtitle(path);
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_AUDIO_CHANGED);
        intentFilter.addAction(Constant.ACTION_SUBTITLE_AUTO_DOWNLOAD);
        intentFilter.addAction(Constant.ACTION_RATIO_CHANGED);
        intentFilter.addAction(Constant.ACTION_SUBTITLE_CHANGED);
        intentFilter.addAction(Constant.ACTION_SUBTITLE_TIME_ADJUST);
        intentFilter.addAction(Constant.ACTION_MEDIAINFO);
        intentFilter.addAction(Constant.ACTION_SUBTITLE_LOAD);
        intentFilter.addAction(Constant.ACTION_SUBTITLE_LOAD_FINISH);

        registerReceiver(menuEventReceiver, intentFilter);

    }

    private void addExternalSubtitle(String path) {
        MenuLeftFragment f = (MenuLeftFragment) getSupportFragmentManager().findFragmentById(R.id.right_drawer);
        f.addSubtitle("外挂", path);
    }

    boolean menuHasParams = false;

    private void setMenuParams() {
        MenuLeftFragment f = (MenuLeftFragment) getSupportFragmentManager().findFragmentById(R.id.right_drawer);
        Bundle bundle = new Bundle();
        bundle.putString("params", getMenuParams());
        f.updateArgument(bundle);
        menuHasParams = true;
    }

    public String getMenuParams() {
        mMenuParams = new MenuParams();
        mMenuParams.audioList = new ArrayList<>();
        mMenuParams.internalSubtitleList = new ArrayList<>();
        mMenuParams.externalSubtitleList = new ArrayList<>();

        //音轨列表
        //字幕列表
        int selectedAudioTrack = getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int selectedSubtitleTrack = getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);

        Log.d(TAG, "selectedSubtitleTrack:" + selectedSubtitleTrack);

        ITrackInfo trackInfos[] = getTrackInfo();
        Log.d(TAG, "trackInfos:" + Arrays.toString(trackInfos));
        if (trackInfos != null) {
            int index = -1;
            for (ITrackInfo trackInfo : trackInfos) {
                index++;
                int trackType = trackInfo.getTrackType();
//                builder.appendRow2(R.string.mi_type, buildTrackType(trackType));
//                builder.appendRow2(R.string.mi_language, buildLanguage(trackInfo.getLanguage()));

                IMediaFormat mediaFormat = trackInfo.getFormat();
                if (mediaFormat == null) {
                } else if (mediaFormat instanceof IjkMediaFormat) {
                    switch (trackType) {
//                        case ITrackInfo.MEDIA_TRACK_TYPE_SUBTITLE:
//                            break;
                        case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            MenuParams.MTrackInfo mTrackInfo = new MenuParams.MTrackInfo();
                            mTrackInfo.trackIndex = index;
                            mTrackInfo.selected = index == selectedAudioTrack;
                            mTrackInfo.trackName = mVideoView.buildLanguage(trackInfo.getLanguage());

                            String[] infoline = trackInfo.getInfoInline().split(",");
                            if (infoline.length > 1) {
                                mTrackInfo.trackName += " " + infoline[1];
                            }
                            mMenuParams.audioList.add(mTrackInfo);
                            break;
                        case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                            mTrackInfo = new MenuParams.MTrackInfo();
                            mTrackInfo.trackIndex = index;
                            mTrackInfo.selected = index == selectedSubtitleTrack;
                            mTrackInfo.trackName = "内嵌" + (mMenuParams.internalSubtitleList.size() + 1) + " " + mVideoView.buildLanguage(trackInfo.getLanguage());
                            mMenuParams.internalSubtitleList.add(mTrackInfo);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        //画面比例
        mMenuParams.ratio = mVideoView.getRatioStr(mVideoView.getCurrentRatio());

        //字幕延迟时间
        //影片信息


        return new Gson().toJson(mMenuParams);
    }


    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }

    void handleIntentQuest() {
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    String scheme = mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown scheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistMenuEvent();

    }

    private void unregistMenuEvent() {
        Log.d(TAG, "unregistMenuEvent");
        if (menuEventReceiver != null) {
//            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(menuEventReceiver);
            unregisterReceiver(menuEventReceiver);
        }
    }

    public static void keepScreenLongLight(Activity activity, boolean flag) {
        Window window = activity.getWindow();
        if (flag) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
