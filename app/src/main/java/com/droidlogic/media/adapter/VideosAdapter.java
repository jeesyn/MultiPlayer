package com.droidlogic.media.adapter;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by yingwei.long on 2016/12/9.
 */

public class VideosAdapter extends BaseAdapter {
    private static final String TAG = "VideosAdapter";
    private Context mContext;
//    private LogicSurfaceView[] mSurfaceViews;
    private SurfaceView[] mSurfaceViews;
    private PercentRelativeLayout[] mSurfaceContainers;
    private List<String>checkedPaths;

    public VideosAdapter(Context context, List<String> checkedPaths, PercentRelativeLayout[] containers) {
        mContext = context;
        this.checkedPaths = checkedPaths;
        mSurfaceContainers = containers;
    }

    @Override
    public int getCount() {
        return checkedPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return checkedPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position <= mSurfaceContainers.length -1) {
                Log.d(TAG, "Now get one SurfaceContainer: position:" + position);
                convertView = mSurfaceContainers[position];
            }
//            TextView textView = new TextView(mContext);
//            textView.setText("Hello World");
//            convertView = textView;
        }
        return convertView;
    }
}
