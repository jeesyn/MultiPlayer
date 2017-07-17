package com.droidlogic.media.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.droidlogic.media.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingwei.long on 2016/11/28.
 */

public class FileSelectAdapter extends BaseAdapter {
    private static final String TAG = "FileSelectAdapter";
    private final Context mContext;
    private List<String> mData = new ArrayList<String>();
    private ArrayList<Boolean> checkStatusList = new ArrayList<Boolean>();
    
    public FileSelectAdapter(Context context) {
        mContext = context;
        updateStatus();
    }

    private void updateStatus() {
        for (int i = 0; i < mData.size(); i++) {
            checkStatusList.add(false);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_item_select, null);
            MultiChoiceViewHolder holder = new MultiChoiceViewHolder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            TextView filePathView = (TextView) view.findViewById(R.id.filePath);
            filePathView.setText(mData.get(position));
            view.setTag(holder);
        } else {
            view = convertView;
        }
        MultiChoiceViewHolder holder = (MultiChoiceViewHolder) view.getTag();
        if (checkStatusList.get(position)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v(TAG,
                        "checkBox before changed " +  buttonView.isChecked());
                    checkStatusList.set(position, isChecked);
                Log.v(TAG,
                        "checkBox after changed " +  buttonView.isChecked());
                notifyDataSetChanged();
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,
                        "checkBox before click " + ((CheckBox) v).isChecked());
                if (((CheckBox) v).isChecked()) {
                    checkStatusList.set(position, true);
                } else {
                    checkStatusList.set(position, false);
                }
                Log.v(TAG,
                        "checkBox after click " + ((CheckBox) v).isChecked());
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public void addData(String s) {
        mData.add(s);
        checkStatusList.add(true);
        notifyDataSetChanged();
    }

    public void delOneFromBack() {
        mData.remove(mData.size() -1);
        checkStatusList.remove(checkStatusList.size() -1);
        notifyDataSetChanged();
    }

    public List<Integer> getChoicedIndexList() {
        List<Integer> choicedIndexList = new ArrayList<Integer>();
        for (int i = 0; i < checkStatusList.size(); i++) {
            if (checkStatusList.get(i)) {
                choicedIndexList.add(i);
            }
        }
        return choicedIndexList;
    }

    public List<String> getCheckedItemList() {
        List<String> checkedItemList = new ArrayList<String>();
        for (int i = 0; i < checkStatusList.size(); i++) {
            if (checkStatusList.get(i)) {
                checkedItemList.add(mData.get(i));
            }
        }
        return checkedItemList;
    }

    class MultiChoiceViewHolder {
        CheckBox checkBox;
    }
}

