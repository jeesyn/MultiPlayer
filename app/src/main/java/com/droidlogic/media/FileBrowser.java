package com.droidlogic.media;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.droidlogic.media.adapter.FileBrowserAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by yingwei.long on 2016/11/30.
 */

public class FileBrowser extends Dialog {
    private static final String ROOT_PATH           = "/storage";
    private static final String SHEILD_EXT_STOR     = Environment.getExternalStorageDirectory().getPath() + "/external_storage";//"/storage/emulated/0/exterma;_stprage";
    private static final String NAND_PATH           = Environment.getExternalStorageDirectory().getPath();//"/storage/emulated/0";
    private static final String SD_PATH             = "/storage/external_storage/sdcard1";
    private static final String USB_PATH            = "/storage/external_storage";
    private static final String ASEC_PATH           = "/mnt/asec";
    private static final String SECURE_PATH         = "/mnt/secure";
    private static final String OBB_PATH            = "/mnt/obb";
    private static final String USB_DRIVE_PATH      = "/mnt/usbdrive";
    private static final String SHELL_PATH          = "/mnt/shell";
    private String currentPathString = null;
    private  Context mContext;
    private ListView fileListView;
    private FileBrowserAdapter fileBrowserAdapter = null;
    private EditText pathEdit;
    private static final String TAG = "FileBrowser";
    private LinkedHashMap<String, String> mFirstDirsMap;
    private SelectCallback selectCallback;

    public FileBrowser(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public FileBrowser(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public FileBrowser(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "keyCode:" + keyCode + " event:" + event);
        if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
            if (fileListView != null && fileListView.findFocus() != null) {
                goUpDir();
            }
        } else if  (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
            if (fileListView != null && fileListView.findFocus() != null) {
                pathEdit.requestFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void AddExternalPath(LinkedHashMap<String, String> map) {
        Log.d(TAG, "Now enter AddExternalPath!!!");
        //external storage
        StorageManager mStorageManager;
        Class<?> volumeInfoClazz = null;
        Method getDescriptionComparator = null;
        Method getBestVolumeDescription = null;
        Method getVolumes = null;
        Method isMountedReadable = null;
        Method getType = null;
        Method getPath = null;
        List<?> volumes = null;
        mStorageManager = (StorageManager)mContext.getSystemService(Context.STORAGE_SERVICE);
        try {
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            getDescriptionComparator = volumeInfoClazz.getMethod("getDescriptionComparator");
            getBestVolumeDescription = StorageManager.class.getMethod("getBestVolumeDescription", volumeInfoClazz);
            getVolumes = StorageManager.class.getMethod("getVolumes");
            isMountedReadable = volumeInfoClazz.getMethod("isMountedReadable");
            getType = volumeInfoClazz.getMethod("getType");
            getPath = volumeInfoClazz.getMethod("getPath");
            volumes = (List<?>)getVolumes.invoke(mStorageManager);
            Log.d(TAG, "Now before for, volumes size:" + volumes.size());

            for (Object vol : volumes) {
                Log.d(TAG, "isMountedReadable is:" + isMountedReadable.invoke(vol) + " type:" + getType.invoke(vol));
                if (vol != null && (boolean)isMountedReadable.invoke(vol) && (int)getType.invoke(vol) == 0) {
                    File path = (File)getPath.invoke(vol);
//                    Log.d(TAG, "BrowserFile() tmppath:"+tmppath + ", path.getName():" + path.getName() + ", path.getPath():" + path.getPath());
//                    if (tmppath.equals(path.getName())) {
//                        items.add((String)getBestVolumeDescription.invoke(mStorageManager, vol));
//                        paths.add(path.getPath());
//                    }"
                    String description = (String)getBestVolumeDescription.invoke(mStorageManager, vol);
                    Log.d(TAG, "path:" + path.getAbsolutePath() + "description:" + description);
                    map.put(description, path.getAbsolutePath());
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initFirstDirsMap() {
        mFirstDirsMap = new LinkedHashMap<String, String>();
        mFirstDirsMap.put(mContext.getResources().getString(R.string.sdcard_device_str), NAND_PATH);
        AddExternalPath(mFirstDirsMap);
//        String usbRoot
//        mFirstDirsMap.put("Mass USB drive", usbRoot);
    }

    private void init(Context context) {
        Log.d(TAG, "NAND_PATH:" + NAND_PATH);
        Log.d(TAG, "SHEILD_EXT_STOR:" +SHEILD_EXT_STOR);
        if (Builder.defaultPath != null)
            currentPathString = Builder.defaultPath;
        if (currentPathString == null) {
            currentPathString = ROOT_PATH;
        }
        initFirstDirsMap();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_file_browser, null);
        addContentView(view , new ViewGroup.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        TextView titleView =  (TextView) view.findViewById(R.id.titleView);
        titleView.setText(Builder.title);
        pathEdit = (EditText) view.findViewById(R.id.pathEdit);
        pathEdit.setHint((CharSequence)Builder.defaultPath);
        Button parentBtn = (Button) view.findViewById(R.id.parentBtn);

        fileListView = (ListView)view.findViewById(R.id.fileListView);

        fileBrowserAdapter = new FileBrowserAdapter(context, currentPathString, getInitData());
        if (fileBrowserAdapter.getCount() == 0) {
            TextView emptyHeader = new TextView(mContext);
            emptyHeader.setText(R.string.sel_file_empty);
            fileListView.addHeaderView(emptyHeader);
        }
        fileListView.setAdapter(fileBrowserAdapter);
        fileListView.requestFocus();
        if (fileBrowserAdapter.getCount() > 0)
            fileListView.setSelection(0);

        parentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUpDir();
            }
        });

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View item, int position, long id) {
                ListView lv = (ListView) view;
                int n = lv.getChildCount();
                for (int i = 0; i < n; ++i) {
                    View v = lv.getChildAt(i);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
//                item.setBackgroundColor(0x800000ff);
                TextView textView = (TextView) item
                        .findViewById(R.id.browserFileName);
                if (textView == null)
                    return ;
                File selectedFile = null;

                selectedFile = getSelectedFileByName(textView.getText().toString());
                if (selectedFile == null)
                    return ;
                if (selectedFile.isDirectory()) {
                    if (selectedFile.list() == null || selectedFile.list().length == 0) {
                        Toast.makeText(mContext, R.string.directory_empty, Toast.LENGTH_LONG).show();
                        return;
                    }
                    browseDirectory(selectedFile.getAbsolutePath());
                } else {
                    currentPathString = selectedFile.getAbsolutePath();
                    pathEdit.setText(currentPathString);
                    if (selectCallback != null) {
                        selectCallback.onSelect(currentPathString);
                    }

                }
            }
        });

        Button positiveBtn = (Button) view.findViewById(R.id.positiveBtn);
        positiveBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(Builder.positiveButtonClickListener);
        Button negativeBtn = (Button) view.findViewById(R.id.negativeBtn);
        negativeBtn.setOnClickListener(Builder.negativeButtonClickListener);
        negativeBtn.setVisibility(View.GONE);

    }

    private void goUpDir() {
        if (currentPathString == null)
            return;
        if (mFirstDirsMap.containsValue(currentPathString)) {
            currentPathString = ROOT_PATH;
            pathEdit.setText(currentPathString);
            fileBrowserAdapter = new FileBrowserAdapter(mContext, currentPathString, getInitData());
            fileListView.setAdapter(fileBrowserAdapter);
            return;
        }
        File current = new File(currentPathString);
        File parent = current.getParentFile();
        if (current.isFile()) {
            currentPathString = parent.getAbsolutePath();
            pathEdit.setText(currentPathString);
        } else if (parent != null) {
            // if (!(parent.equals(Environment.getRootDirectory()))) {
            browseDirectory(parent.getAbsolutePath());
        }
    }

    private List<String> getInitData() {
        List<String> ret = new ArrayList<String>();
        if (ROOT_PATH.equals(currentPathString)) {
            Set<String> keySet = mFirstDirsMap.keySet();
            Iterator<String> it = keySet.iterator();
            while(it.hasNext()) {
                ret.add(it.next());
            }
        } else if (currentPathString != null) {
            File[] files = new File(currentPathString).listFiles();
            if (files == null)
                return ret;
            for (int i = 0; i < files.length; i++) {
                ret.add(files[i].getName());
            }
        }
        return ret;
    }

    private File getSelectedFileByName(String name) {
        File ret = null;
        if (currentPathString != null) {
//            File currentDir = new File(currentPathString);
//            File parent = currentFile.getParentFile();
            if (ROOT_PATH.equals(currentPathString)) {
                ret = new File(mFirstDirsMap.get(name));
            } else {
                ret = new File(currentPathString, name);
            }
        }
        return ret;
    }

    private void browseDirectory(String initDir) {
        fileBrowserAdapter.updateDirectory(initDir);
        currentPathString = initDir;
        pathEdit.setText(currentPathString);
    }

    public String getFilePath() {
        return currentPathString;
    }

    public void setSelectCallback(SelectCallback selectCallback) {
        this.selectCallback = selectCallback;
    }

    public interface SelectCallback {
        public void onSelect(String path);
    }

    public static class Builder {
        private  static String title;
        private static String defaultPath;
        private static View.OnClickListener positiveButtonClickListener = null;
        private static View.OnClickListener negativeButtonClickListener = null;

        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDefaultPath(String path) {
            this.defaultPath = path;
            return this;
        }

        public Builder setPositiveButtonListener (
                                         View.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButtonListener(View.OnClickListener listener) {
            this.negativeButtonClickListener = listener;
            return this;
        }

        public FileBrowser create(FileBrowser.SelectCallback callback) {
            final FileBrowser fileBrowser = new FileBrowser(context);
            if (callback != null) {
                fileBrowser.setSelectCallback(callback);
            }
//            Window dialogWindow = fileBrowser.getWindow();
//            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//            DisplayMetrics d = context.getResources().getDisplayMetrics();
//            lp.width = (int) (d.widthPixels * 0.8);
//            dialogWindow.setAttributes(lp);
            return fileBrowser;
        }
    }
}
