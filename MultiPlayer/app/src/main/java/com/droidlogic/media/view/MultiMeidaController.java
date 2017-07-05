package com.droidlogic.media.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidlogic.media.R;

/**
 * Created by yingwei.long on 2016/12/7.
 */

public class MultiMeidaController extends RelativeLayout {

    Button infoBtn;
    Button trackBtn;
    Button prevBtn;
    Button nextBtn;
    Button playBtn;
    Button pauseBtn;
    Button fullBtn;
    CheckBox repeatCheckBox;
    SeekBar seekBar;
    TextView leftTimeLabel;
    TextView  indexLabel;
    TextView rightTimeLabel;


    public MultiMeidaController(Context context) {
        super(context);
        init(context);
    }

    public MultiMeidaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiMeidaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MultiMeidaController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        this.setMeasuredDimension(this.getMeasuredWidth(), leftTimeLabel.getHeight() + playBtn.getHeight() + 10);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_media_controller, null);
        indexLabel = (TextView)view.findViewById(R.id.indexLabel);
        infoBtn = (Button)view.findViewById(R.id.infoBtn);
        trackBtn = (Button)view.findViewById(R.id.trackBtn);
        prevBtn = (Button)view.findViewById(R.id.prevBtn);
        nextBtn = (Button)view.findViewById(R.id.nextBtn);
        playBtn = (Button)view.findViewById(R.id.playBtn);
        pauseBtn = (Button)view.findViewById(R.id.pauseBtn);
        fullBtn = (Button)view.findViewById(R.id.fullBtn);
        leftTimeLabel = (TextView)view.findViewById(R.id.leftTimeLabel);
//        rightTimeLabel = (TextView)view.findViewById(R.id.rightTimeLabel);
        repeatCheckBox = (CheckBox)view.findViewById(R.id.repeatCheckBox);

        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
//        setMinimumHeight(50);
//        setMinimumWidth(50);
        this.setBackgroundColor(Color.CYAN);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//        setLayoutParams(params);

        this.addView(view, lp);
//        this.setOrientation(LinearLayout.VERTICAL);
//        setLayoutParams(params);

        this.requestFocus();
        this.setGravity(Gravity.CENTER);
        this.setMinimumHeight(100);
    }

    public void showPlayButton(boolean b) {
        if (b) {
            playBtn.setVisibility(VISIBLE);
            pauseBtn.setVisibility(GONE);
            playBtn.requestFocus();
        } else {
            playBtn.setVisibility(GONE);
            pauseBtn.setVisibility(VISIBLE);
            pauseBtn.requestFocus();
        }
    }

    public void tooglePlayPause() {
        if (playBtn.getVisibility() == GONE) {
            playBtn.setVisibility(VISIBLE);
            pauseBtn.setVisibility(GONE);
        } else {
            playBtn.setVisibility(GONE);
            pauseBtn.setVisibility(VISIBLE);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        if (listener != null) {
            infoBtn.setOnClickListener(listener);
            trackBtn.setOnClickListener(listener);
            playBtn.setOnClickListener(listener);
            pauseBtn.setOnClickListener(listener);
            prevBtn.setOnClickListener(listener);
            nextBtn.setOnClickListener(listener);
            fullBtn.setOnClickListener(listener);
            repeatCheckBox.setOnClickListener(listener);

        }
    }

    public  void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        if (listener != null) {
            seekBar.setOnSeekBarChangeListener(listener);
        }
    }

    public void setIndex(int index) {
        String labelStr = String.format(getContext().getResources().getString(R.string.video_index_label), index);
        indexLabel.setText(labelStr);
    }

    public void setCurTime(String t) {
        leftTimeLabel.setText(t);
    }

    public void setTotalTime(String t) {

    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    public void showFullButton(boolean canFull) {
        if (canFull) {
            fullBtn.setVisibility(View.VISIBLE);
        } else {
            fullBtn.setVisibility(View.GONE);
        }
    }
}
