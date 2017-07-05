package com.droidlogic.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yingwei.long on 2016/11/23.
 */

public class VideoItemView extends LinearLayout {
    private int mWidth;
    private int mHeight;


    public VideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public VideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public VideoItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
//        mInflater.inflate(R.layout.layout_video_item, null);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        setLayoutParams(params);
//        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    public void setVisibility(boolean b) {

    }

    public void setWidth(int w) {
        mWidth = w;
    }

    public void setHeight(int h) {
        mHeight = h;
    }


}
