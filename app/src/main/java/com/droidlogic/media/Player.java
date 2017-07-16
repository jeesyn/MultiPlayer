package com.droidlogic.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

//import com.droidlogic.app.MediaPlayer;


/**
 * Created by yingwei.long on 2016/12/16.
 */

public class Player extends Thread {
    private static final String TAG = "Player";
    private static final int MSG_CMD_INIT = 100;
    private static final int MSG_CMD_PLAY = 101;
    private static final int MSG_CMD_PAUSE= 102;
    private static final int MSD_CMD_SEEK = 103;
    private MediaExtractor extractor;
    private MediaCodec decoder;
    private MediaPlayer mPlayer;
    private SurfaceHolder holder;
    private Handler mHandler;
    private boolean isPaused = false;
    private boolean mIsRepeat;
    private boolean isCompleted = false;
    private String uri;
    FileList mFileList;
    private int index;
    public int getIndex() {
        return index;
    }

    public boolean isSame(Player player) {
        return index == player.getIndex();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public Player(Context context, SurfaceHolder holder, String uri) {
        this.holder = holder;
        this.uri = uri;
        mFileList =  new FileList(context, uri);
    }

    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
        }
    }

    public boolean paused() {
        return isPaused;
    }

    public String getUri() {
        return uri;
    }

//    public MediaPlayer.MediaInfo getMediaInfo() {
//        return mPlayer.getMediaInfo();
//    }

    public void setRepeat(boolean bRepeat) {
        mIsRepeat = bRepeat;
    }

    public void stopPlay() {
        mPlayer.stop();
    }

    public void release() {
        mPlayer.release();
    }

    private String getPrevUri() {
        uri = mFileList.prevPath();
        return uri;
    }

    private String getNextUri() {
        uri = mFileList.nextPath();
        return uri;
    }

    public void playNext() {
        String newUri = getNextUri();
        if (newUri != null) {
            mPlayer.stop();
//            mPlayer.release();
            uri = newUri;
//            Message msg = mHandler.obtainMessage(MSG_CMD_INIT, index);
//            msg.sendToTarget();
            restartPlayer();
        }
    }

    public void playPrev() {
        String newUri = getPrevUri();
        if (newUri != null) {
            mPlayer.stop();
//            mPlayer.release();
            uri = newUri;
//            Message msg = mHandler.obtainMessage(MSG_CMD_INIT, index);
//            msg.sendToTarget();
            restartPlayer();
        }
    }

    public void setDisplay(SurfaceHolder holder) {
        this.holder = holder;
        mPlayer.setDisplay(holder);
    }

    public void play() {
        mPlayer.start();
        isPaused = false;
    }

    public void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    private void initPlayer() {
        try {
            sleep((index) * 1000);
            mPlayer= new MediaPlayer();
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(uri);
            mPlayer.setDisplay(holder);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "Now onCompletion, mIsRepeat:" + mIsRepeat);
                    isCompleted =  true;
                    if (mIsRepeat) {
                        play();
                    } else {
                        playNext();
                    }
                }
            });

        } catch(Exception e) {
            if (e instanceof IOExecption || e instanceof IllegalArgumentException) {
                Log.d(TAG, "Failed to get resource of URI/URL");
            } else if (e instanceof InterruptedException) {
                Log.d(TAG, "Failed to sleep.");
            } else {
                e.printStackTrace();
            }
        }
    }

    private void restartPlayer() {
        try {
//            mPlayer= new MediaPlayer();
            mPlayer.reset();
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(uri);
//            mPlayer.setDisplay(holder);
            mPlayer.prepareAsync();
            isCompleted = false;
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                }
            });
            mPlayer.start();
        } catch(Exception e) {
            if (e instanceof IOExecption || e instanceof IllegalArgumentException) {
                Log.d(TAG, "Failed to get resource of URI/URL");
            } else {
                e.printStackTrace();
            }
        }
    }

    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void run() {
        Looper.prepare();
        initPlayer();
        mHandler  = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CMD_INIT:
                        if (index == msg.arg1)
                            initPlayer();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        Looper.loop();
    }

    public boolean isPlayEnd() {
        return isCompleted || (mPlayer.getDuration() == mPlayer.getCurrentPosition());
    }
}
