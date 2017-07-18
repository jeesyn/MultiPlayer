package com.droidlogic.media.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidlogic.media.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserAdapter extends BaseAdapter {
    private static final String TAG = "FileBrowserAdapter";
    private static final String ROOT_PATH  = "/storage";
    private Context mContext;
    private List<String> mData = new ArrayList<String>();
    private String parentDir = null;

    public FileBrowserAdapter(Context context, String dirPath, List<String> data) {
        mContext = context;
        mData = data;
        parentDir = dirPath;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        if (mData.size()  == 0)
            return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.layout_file_item, null);
            MyHolder holder = new MyHolder();
            holder.fileIcon = (ImageView)view.findViewById(R.id.fileIcon);
            holder.browserFileName = (TextView)view.findViewById(R.id.browserFileName);
            view.setTag(holder);
        } else {
            view = convertView;
        }
        MyHolder holder = (MyHolder)view.getTag();
        boolean isDirectory = false;
        String fileName = mData.get(position);
        if (parentDir != null) {
            if (ROOT_PATH.equals(parentDir)) {
                isDirectory = true;
            } else {
                isDirectory = new File(parentDir, fileName).isDirectory();
            }
        }
        if (isDirectory) {
            holder.fileIcon.setImageResource(R.drawable.folder);
        } else {
            holder.fileIcon.setImageResource(R.drawable.doc);
        }
        holder.browserFileName.setText(fileName);
        return view;
    }

    public void updateDirectory(String dirPath) {
        Log.d(TAG, "update Directory dirPath:" + dirPath);
        if (dirPath == null)
            return;
        File[] files = new File(dirPath).listFiles();
        if (files == null || files.length <= 0)
            return;
        mData.clear();
        parentDir = dirPath;
        for (int i = 0; i < files.length; i++) {
            mData.add(files[i].getName());
        }
        notifyDataSetChanged();
    }

    public void updateData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    class MyHolder {
        ImageView fileIcon;
        TextView browserFileName;
    }
}