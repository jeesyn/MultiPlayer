package com.droidlogic.media.view;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.droidlogic.app.MediaPlayerExt;
import com.droidlogic.media.Player;
import com.droidlogic.media.R;

import java.io.IOException;

/**
 * Created by yingwei.long on 2016/12/20.
 */

public class VideoInfoView extends FrameLayout {
    private static final String TAG = "VideoInfoView";
    private View containerView;
    private TextView resolutionView;
    private TextView videoFormatView;
    private TextView audioFormatView;

    public VideoInfoView(Context context) {
        super(context);
        init(context);

    }

    public VideoInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public VideoInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        containerView  = inflater.inflate(R.layout.media_info, null);
        addView(containerView);
    }

    private void setInfoByExtractor(String uri) {
        MediaFormat format;
        try {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(uri);
            int numTracks = extractor.getTrackCount();
            Log.d(TAG, "Trace_info, numTracks:" + numTracks);
            for (int i = 0; i < numTracks; ++i) {
                Log.d(TAG, "Trace_info, enter for, i:" + i);
                format = extractor.getTrackFormat(i);
                Log.d(TAG, "Trace_info, after getFormat!!!");
                String mime = format.getString(MediaFormat.KEY_MIME);

                Log.d(TAG, "Trace_info, mime:" + mime);
                if (mime.startsWith("video")) {
//                    Log.d(TAG, "Trace_info, video now before frame_rate!");
//                      Log.d(TAG, "Trace_info, color_format:" + format.getInteger(MediaFormat.KEY_COLOR_FORMAT));
//                    Log.d(TAG, "Trace_info, frame_rate:" + format.getInteger(MediaFormat.KEY_FRAME_RATE));
                    videoFormatView.setText(mime);
//                    videoFramerateView.setText(format.getInteger(MediaFormat.KEY_FRAME_RATE));
                } else if (mime.startsWith("audio")) {
                    Log.d(TAG, "Trace_info, audio now before sample rate!");
//                    Log.d(TAG, "Trace_info, sample rate:" + format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                    audioFormatView.setText(mime);
//                    audioSamplerateView.setText(format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                }
//                int bitRate =  format.getInteger(MediaFormat.KEY_BIT_RATE);
//                Log.d(TAG, "Trace_info, bitrate:" + bitRate);
            }
            extractor.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlayerInfo(Player player)  {
        String uri = player.getUri();
        MediaPlayer mediaPlayer = player.getPlayer();
//        MediaPlayerExt.MediaInfo info = player.getMediaInfo();
        resolutionView = (TextView)containerView.findViewById(R.id.resolution);
        TextView videoFramerateView = (TextView)containerView.findViewById(R.id.video_bitrate);
        TextView audioSamplerateView = (TextView)containerView.findViewById(R.id.audio_bitrate);
        videoFormatView = (TextView)containerView.findViewById(R.id.video_format);
        audioFormatView = (TextView)containerView.findViewById(R.id.audio_format);
//        int audioTrackIndex = mediaPlayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO);
//        int videoTrackIndex = mediaPlayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO);
        MediaPlayer.TrackInfo [] trackInfoArray = mediaPlayer.getTrackInfo();
        int length = trackInfoArray.length;
        String resolutionStr = mediaPlayer.getVideoWidth()  + " x " + mediaPlayer.getVideoHeight();
        resolutionView.setText(resolutionStr);
//        Log.d(TAG, "Trace_info, trackInfoArray, length:" + length +
//                " audioTrackIndex:" + audioTrackIndex +
//                " videoTrackIndex:" + videoTrackIndex);
//        Log.d(TAG, "Trace_info, info.cur_video_index;" + info.cur_video_index);
//        MediaPlayerExt.VideoInfo videoInfos []  = info.videoInfo;
//        for (int i = 0; i < videoInfos.length; i++) {
//            MediaPlayerExt.VideoInfo vInfo = videoInfos[i];
//            Log.d(TAG, "Trace_info, vformat:" + vInfo.vformat + " bitrate:" + info.bitrate);
//        }
        MediaFormat format;
        setInfoByExtractor(uri);
    }
}
