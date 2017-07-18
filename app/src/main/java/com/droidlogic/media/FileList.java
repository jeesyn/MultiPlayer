package com.droidlogic.media;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileList {

    private static String TAG = "FileList";
    private final Context mContext;
    private String mInitDir;
    private List<String> mMediaFiles;
    private String curFilePath;


    public FileList(Context context, String curPath) {
        mContext = context;

        mInitDir = new File(curPath).getParent();
        curFilePath = curPath;
        initData();
    }

    private void initData() {
        File[] fileArray = new File(mInitDir).listFiles(new SupportedMediaFilter());
        mMediaFiles = new ArrayList<String>();
        int length = fileArray.length;
        for (int i = 0; i < length; i++) {
            mMediaFiles.add(fileArray[i].getAbsolutePath());
        }
        if (mMediaFiles.size() == 0)
            Log.d(TAG, "initData: no supported media file added.");
    }

    public String getCurFilePath() {
        return curFilePath;
    }

    private int getIndex(String path) {
        for (int i = 0; i < mMediaFiles.size(); i++) {
            if (mMediaFiles.get(i).equals(path)) {
                return i;
            }
        }
        return -1;
    }

    public String prevPath() {
        int curIndex = getIndex(curFilePath);
        if (mMediaFiles.size() == 0) //return null if no file supported
            return null;
        if (curIndex == 0) {
            curIndex = mMediaFiles.size() - 1;
        } else {
            curIndex = curIndex -1;
        }
        curFilePath = mMediaFiles.get(curIndex);
        return curFilePath;
    }

    public String nextPath() {
        int curIndex = getIndex(curFilePath);
        int size = mMediaFiles.size();
        if (size == 0)
            return null;
        if (curIndex == size - 1 || size == 1) {
            curIndex = 0;
        } else {
            curIndex = curIndex + 1;
        }
        curFilePath = mMediaFiles.get(curIndex);
        return curFilePath;
    }

    class SupportedMediaFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            boolean ret = false;
            String supportedStr = mContext.getResources().getString(R.string.support_media_str);
            String fileName = file.getName();
            int suffixIndex = fileName.lastIndexOf('.');
            if (suffixIndex >= 0) {
                String subString = fileName.substring(suffixIndex);
                ret = supportedStr.contains(subString);
            }
            return ret;
        }
    }
}
