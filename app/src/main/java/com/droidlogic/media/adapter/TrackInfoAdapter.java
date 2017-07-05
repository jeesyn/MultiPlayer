package com.droidlogic.media.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.droidlogic.media.Player;
import com.droidlogic.media.PlayerManager;
import com.droidlogic.media.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by yingwei.long on 2016/12/19.
 */

public class TrackInfoAdapter extends BaseAdapter {
    private static final String TAG = "TrackInfoAdapter";
    private Context mContext;
    private int mInfoType;
    private int playerIndex;
    private List<Pair<Integer, MediaPlayer.TrackInfo>> mInfoData = new ArrayList<Pair<Integer, MediaPlayer.TrackInfo>>();
    private int currentTrackIndex;
    private String mSelectInfo;
    private PlayerManager playerManager;



    public TrackInfoAdapter(Context context, int type, int playerIndex) {
        mContext = context;
        mInfoType = type;
        this.playerIndex = playerIndex;
        playerManager = PlayerManager.getInstance(context);
        if (type == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {

        }
//        initData();
    }

    private void initData() {
        Player player = playerManager.getPlayer(playerIndex);
        if (player != null) {
            MediaPlayer mediaPlayer = player.getPlayer();
            MediaPlayer.TrackInfo[] trackInfoArray = mediaPlayer.getTrackInfo();
            int length = trackInfoArray.length;
            int selIndex = mediaPlayer.getSelectedTrack(mInfoType);
            Log.d(TAG, "Trace_track, length:" + length + " selIndex:" + selIndex);
            for (int i = 0; i < length; i++) {
                if (trackInfoArray[i].getTrackType() == mInfoType) {
                    mInfoData.add(new Pair(i, trackInfoArray[i]));
                    if (selIndex == i) {
                        currentTrackIndex = mInfoData.size() - 1;
                    }

                }
            }
        }
    }

    @Override
    public int getCount() {
        return mInfoData.size();
    }

    @Override
    public Pair<Integer, MediaPlayer.TrackInfo> getItem(int position) {
        return mInfoData.get(position);
    }

//    private int getSelectedIndex() {
//        for (int i = 0; i < mInfoData.size(); i++) {
//            if (mSelectInfo != null && mSelectInfo.equals(mInfoList.get(i))) {
//                return i;
//            }
//        }
//        return  -1;
//    }
//
//    public void setSelectedInfo(String selStr) {
//        mSelectInfo = selStr;
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.track_item, null);
            MyHolder holder = new MyHolder();
            holder.trackItemInfo = (TextView)view.findViewById(R.id.trackItemInfo);
            holder.trackRadioButton = (RadioButton)view.findViewById(R.id.trackRadioButton);
            view.setTag(holder);
            convertView = view;
        }
        MyHolder holder = (MyHolder) convertView.getTag();
        Pair<Integer, MediaPlayer.TrackInfo>  pair = mInfoData.get(position);
        holder.trackItemInfo.setText(pair.second.toString());

        if (currentTrackIndex == position) {
            holder.trackRadioButton.setChecked(true);
        } else {
            holder.trackRadioButton.setChecked(false);
        }
        return convertView;
    }

    class MyHolder {
        public TextView trackItemInfo;
        public RadioButton trackRadioButton;
    }
}
