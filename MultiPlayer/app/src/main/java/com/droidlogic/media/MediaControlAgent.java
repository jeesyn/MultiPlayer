package com.droidlogic.media;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.droidlogic.media.adapter.TrackInfoAdapter;
import com.droidlogic.media.view.MultiMeidaController;
import com.droidlogic.media.view.VideoInfoView;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by yingwei.long on 2016/12/7.
 */

public class MediaControlAgent implements View.OnClickListener,
        View.OnFocusChangeListener,
        SeekBar.OnSeekBarChangeListener,
        ViewTreeObserver.OnGlobalFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "MediaControlAgent";
    private static final long MSG_SEND_DELAY = 0; //1000;//1s
    private static final long MSG_SEND_DELAY_500MS = 500; //500ms

    private static final int MSG_CONTROL_TIME_OUT = 1000;
    private static final int MSG_INFO_TIME_OUT = 1001;
    private static final int MSG_SEEK_BY_BAR = 1002;
    private static final int MSG_UPDATE_PROGRESS = 1003;
    private static final long FADE_TIME_5S = 5000;
    private final ViewTreeObserver mViewTreeObserver;
    private MultiMeidaController multiMeidaController;
    private boolean ignoreUpdateProgressbar = false;
    private boolean canFull = true;
    private int currentPlayerIndex = 0;
    private Context mPlayerActivity;
    private PopupWindow mInfoPopWindow;
    private PopupWindow mControllerPopWindow;
    private Timer timer;
    private PlayerManager playerManager;
    private ControllerCallback mCallback;


    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {
            int pos;
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    pos = getCurrentPosition();
                    Log.d(TAG, "Trace_progress, Now pos is:" + pos + " duration:" + getDuration());
                    msg = obtainMessage (MSG_UPDATE_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    if (needUpdateProgress()) {
                        Log.d(TAG, "Trace_progress, Now to update!!!");
                        updateProgressBar();
                    }
                    break;
                case MSG_CONTROL_TIME_OUT:
                    doShowController(false);
                    break;
                case MSG_INFO_TIME_OUT:
                    if (mInfoPopWindow != null && mInfoPopWindow.isShowing()) {
                        mInfoPopWindow.dismiss();
                    }
                    break;
                case MSG_SEEK_BY_BAR:
                    int progress = msg.arg1;
                    mHandler.removeMessages (MSG_SEEK_BY_BAR);
                    seekByProgressBar(progress);
                    break;
                default:
                    break;
            };
        }
    };


    public MediaControlAgent(Context playerActivity) {
        mPlayerActivity = playerActivity;
//        multiMeidaController = (MultiMeidaController)playerActivity.findViewById(R.id.multi_media_controller);
        multiMeidaController = new MultiMeidaController(playerActivity);
        multiMeidaController.setOnClickListener(this);
        multiMeidaController.setOnSeekBarChangeListener(this);
        multiMeidaController.setOnFocusChangeListener(this);
        mViewTreeObserver = (ViewTreeObserver)multiMeidaController.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalFocusChangeListener(this );
//        mViewTreeObserver.addOnPreDrawListener( this );
//        mViewTreeObserver.addOnTouchModeChangeListener( this );
        mViewTreeObserver.addOnGlobalLayoutListener(this);
        playerManager = PlayerManager.getInstance(playerActivity);
        if (playerActivity != null) {
            mCallback = (ControllerCallback)playerActivity;
        }
    }

    private void startControlerTimeout() {
        TimerTask task = new TimerTask() {
            public void run() {
                mHandler.removeMessages(MSG_CONTROL_TIME_OUT);
                Message message = Message.obtain();
                message.what = MSG_CONTROL_TIME_OUT;
                mHandler.sendMessage (message);
            }
        };
        stopControllerTimeout();
        if (timer == null) {
            timer = new Timer();
        }
        if (timer != null) {
            timer.schedule (task, FADE_TIME_5S);
        }
    }

    private void startInfoTimeout() {
        TimerTask task = new TimerTask() {
            public void run() {
                mHandler.removeMessages(MSG_INFO_TIME_OUT);
                Message message = Message.obtain();
                message.what = MSG_INFO_TIME_OUT;
                mHandler.sendMessage (message);
            }
        };
        stopInfoTimeout();
        if (timer == null) {
            timer = new Timer();
        }
        if (timer != null) {
            timer.schedule (task, FADE_TIME_5S);
        }
    }

    private void stopInfoTimeout() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    private void stopControllerTimeout() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }


    public void showController(int index, boolean toShow, boolean canFull) {
        if (index == currentPlayerIndex && toShow == isControllerShowing()) {
            return;
        }
        this.canFull = canFull;
        currentPlayerIndex = index;
        if (isControllerShowing())
            doShowController(false);
        currentPlayerIndex = index;
        startControlerTimeout();
        doShowController(true);
    }

    private void doShowController(boolean toShow) {
//        if (toShow) {
//            multiMeidaController.setIndex(currentPlayerIndex);
//            int topIndex = multiMeidaController.getTop();
//            multiMeidaController.setZ(topIndex + 5);
//            multiMeidaController.setVisibility(View.VISIBLE);
//            if (mHandler != null) {
//                Message msg = mHandler.obtainMessage (MSG_UPDATE_PROGRESS);
//                mHandler.sendMessageDelayed (msg, 0/*MSG_SEND_DELAY*/);
//            }
//        } else {
//            multiMeidaController.setVisibility((View.GONE));
//            if (mHandler != null) {
//                mHandler.removeMessages(MSG_UPDATE_PROGRESS);
//            }
//        }
        if (toShow) {
            Player player = playerManager.getPlayer(currentPlayerIndex);
            if (player != null) {
                if (player.paused()) {
                    multiMeidaController.showPlayButton(true);
                    multiMeidaController.setIndex(currentPlayerIndex);
                } else {
                    multiMeidaController.showPlayButton(false);
                }

                multiMeidaController.showFullButton(canFull);

            }
            mControllerPopWindow = new PopupWindow(multiMeidaController,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            mControllerPopWindow.setContentView(multiMeidaController);
            View rootView = ((Activity)mPlayerActivity).getWindow().getDecorView();
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage (MSG_UPDATE_PROGRESS);
                mHandler.sendMessageDelayed (msg, 0/*MSG_SEND_DELAY*/);
            }
            mControllerPopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        } else {
            mControllerPopWindow.dismiss();
            if (mHandler != null) {
                mHandler.removeMessages(MSG_UPDATE_PROGRESS);
            }
        }
    }

    public boolean isControllerShowing() {
//        return multiMeidaController != null &&
//                multiMeidaController.getVisibility() == View.VISIBLE;
        return mControllerPopWindow != null && mControllerPopWindow.isShowing();
    }

    @Override
    public void onClick(View v) {
        startControlerTimeout();
        Player player = playerManager.getPlayer(currentPlayerIndex);
        switch (v.getId()) {
            case R.id.playBtn:
                player.play();
                multiMeidaController.showPlayButton(false);
                break;
            case R.id.pauseBtn:
                player.pause();
                multiMeidaController.showPlayButton(true);
                break;
            case R.id.infoBtn:
                doShowController(false);
                showMediaInfo();
                break;
            case R.id.trackBtn:
                doShowController(false);
                showTrackInfo();
                break;
            case R.id.prevBtn:
                player.playPrev();
                break;
            case R.id.nextBtn:
                player.playNext();
                break;
            case R.id.repeatCheckBox:
                setRepeat(((CheckBox)v).isChecked());
                break;
            case R.id.fullBtn:
                if (mCallback != null) {
                    mCallback.onFullScreen(currentPlayerIndex);
                }
                break;
            default:
                break;
        }
    }

    private void setRepeat(boolean bRepeat) {
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player != null) {
            player.setRepeat(bRepeat);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "Now enter onFocusChang,  hasFocus:" + hasFocus);
        if (isControllerShowing()) {
            startControlerTimeout();
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.d(TAG, "Now enter onGlobalFocusChanged!!!");
        if (isControllerShowing()) {
            startControlerTimeout();
        }
    }

    @Override
    public void onGlobalLayout() {
        Log.d(TAG, "Now enter onGlobalLayout!!!");
        if (isControllerShowing()) {
//            startControlerTimeout();
        }
    }

//    private List<> getTrackTypes(int index, int type) {
//        LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
//        Player player = playerManager.getPlayer(index);
//
//        if (player != null) {
//            MediaPlayer mediaPlayer = player.getPlayer();
//            int audioTrackIndex = mediaPlayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO);
//            int subTrackIndex  = mediaPlayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE);
//            MediaPlayer.TrackInfo[] trackInfoArray = mediaPlayer.getTrackInfo();
////            ret = Arrays.asList(trackInfoArray);
//            int length = trackInfoArray.length;
//            for (int i = 0; i < length; i++) {
//
//                if (i == audioTrackIndex || i== subTrackIndex) {
//                    ret.put(trackInfoArray[i].toString(), 1);
//                } else {
//                    ret.put(trackInfoArray[i].toString(), 0);
//                }
//            }
//        }
//        return ret;
//    }

    private void showTrackInfo() {
        View contentView = LayoutInflater.from(mPlayerActivity).inflate(R.layout.track_info, null);
        mInfoPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        contentView.setBackgroundColor(Color.BLACK);
        mInfoPopWindow.setContentView(contentView);
        ListView audioTrackListView = (ListView)contentView.findViewById(R.id.audio_track_list);
        TrackInfoAdapter audioTrackInfoAdapter = new TrackInfoAdapter(mPlayerActivity,
                MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO, currentPlayerIndex);
        audioTrackListView.setAdapter(audioTrackInfoAdapter);
        ListView subTrackListView = (ListView)contentView.findViewById(R.id.sub_track_list);
        TrackInfoAdapter subTrackInfoAdapter = new TrackInfoAdapter(mPlayerActivity,
                MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE,
                currentPlayerIndex);
        subTrackListView.setAdapter(subTrackInfoAdapter);
        View rootView = ((Activity)mPlayerActivity).getWindow().getDecorView();
//        View rootview = LaytInflater.from(mPlayerActivity).inflate(R.layout.main, null);
        startInfoTimeout();
        mInfoPopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private  void showMediaInfo() {
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player == null)
            return;
        MediaPlayer mediaPlayer = player.getPlayer();
        VideoInfoView contentView = new VideoInfoView(mPlayerActivity);
//        contentView.setBackgroundColor(Color.BLACK);
        contentView.setPlayerInfo(player);
        mInfoPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mInfoPopWindow.setContentView(contentView);

        View rootView = ((Activity)mPlayerActivity).getWindow().getDecorView();
        startInfoTimeout();
        mInfoPopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private String secToTime (int i) {
        String retStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (i <= 0) {
            return "00:00:00";
        }
        else {
            minute = i / 60;
            if (minute < 60) {
                second = i % 60;
                retStr = "00:" + unitFormat (minute) + ":" + unitFormat (second);
            }
            else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = i % 60;
                retStr = unitFormat (hour) + ":" + unitFormat (minute) + ":" + unitFormat (second);
            }
        }
        return retStr;
    }

    private String unitFormat (int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString (i);
        }
        else {
            retStr = Integer.toString (i);
        }
        return retStr;
    }

    private void sendSeekByProgressBarMsg(int progress) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage (MSG_SEEK_BY_BAR);
            msg.arg1 = progress;
            mHandler.sendMessageDelayed (msg, MSG_SEND_DELAY_500MS);
        }
    }

    private int getDuration() {
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    private int getCurrentPosition() {
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    private void seekByProgressBar(int progress) {
        int dest = progress;
        int totaltime = getDuration();
        int pos = totaltime * (dest + 1) / 100;
        //check for small stream while seeking
        int pos_check = totaltime * (dest + 1) - pos * 100;
        if (pos_check > 0) {
            pos += 1;
        }
        if (pos >= totaltime) {
            pos = totaltime;
        }
        if (dest <= 1) {
            pos = 0;
        }
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player != null) {
            player.seekTo(pos);
        }
    }

    private boolean needUpdateProgress() {
        if (!isControllerShowing() || ignoreUpdateProgressbar)
            return false;
        Player player = playerManager.getPlayer(currentPlayerIndex);
        if (player == null ||
                (player.getDuration() == 0) ||
                player.isPlayEnd())
            return false;
        return true;
    }

    private void updateProgressBar() {
        int curtime = getCurrentPosition();
        int totaltime = getDuration();
        multiMeidaController.setCurTime(secToTime (curtime / 1000));
        multiMeidaController.setTotalTime(secToTime (totaltime / 1000));
        if (totaltime != 0) {
            int curtimetmp = curtime / 1000;
            int totaltimetmp = totaltime / 1000;
            if (totaltimetmp != 0) {
                int step = curtimetmp*100/totaltimetmp;
                multiMeidaController.setProgress(step);
            }
        } else {
            multiMeidaController.setProgress(0);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "Trace_progress, progress:" + progress + " fromUser:" + fromUser);
        if (fromUser == true) {
            ignoreUpdateProgressbar = true;
            startControlerTimeout();
            mHandler.removeMessages (MSG_SEEK_BY_BAR);
//            progressBarSeekFlag = true;
            sendSeekByProgressBarMsg(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "Trace_progress, onStartTrackingTouch, progress:" + seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "Trace_progress, getProgress:" + seekBar.getProgress());
    }

    public void stop() {
        if (isControllerShowing()) {
            doShowController(false);
        }
    }

    interface ControllerCallback  {
        public void onFullScreen(int index);
    }
}