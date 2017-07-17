package com.droidlogic.media;
//references:
//http://www.cnblogs.com/dangxueting/p/5800025.html
//http://www.eoeandroid.com/thread-547081-1-1.html?_dsign=5a2b47b9

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.droidlogic.media.adapter.FileSelectAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingwei.long on 2016/11/29.
 */

public class FileSelectActivity extends Activity implements  View.OnClickListener,
        AdapterView.OnItemClickListener, FileBrowser.SelectCallback {
    private Fragment currentFragment = null;
    private static final  String TAG = "FileSelectActivity";
    private static final String KEY_CHECKED_LIST = "checked_list";
    private static final int MAX_FILE_NUM = 64;
    private FileBrowser fileBrowser;
    private List<String> selectedList = new ArrayList<String>();
    private ListView mFileListView;
    private FileSelectAdapter mFileSelectAdapter;
    private Button mEnsureBtn;
    private Button mAddBtn;
    private Button mDelBtn;
    private String lastDir = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_select);
        mFileListView  = (ListView)findViewById(R.id.fileListView);
        mFileSelectAdapter = new FileSelectAdapter(this);
        mFileListView.setAdapter(mFileSelectAdapter);
        mEnsureBtn = (Button)findViewById(R.id.ensureBtn);
        mDelBtn = (Button)findViewById(R.id.delBtn);
        mAddBtn = (Button)findViewById(R.id.addBtn);
        mAddBtn.requestFocus();

        mEnsureBtn.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mFileListView.setOnItemClickListener(this);
    }

    private void goFileBrowser() {
        FileBrowser.Builder builder = new FileBrowser.Builder(this);
        fileBrowser = builder.setTitle(R.string.filebrowser_title)
                .setDefaultPath(lastDir)
                .setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String filePath = fileBrowser.getFilePath();
                        selectedList.add(filePath);
                        mFileSelectAdapter.addData(filePath);
                        fileBrowser.dismiss();
                    }
                })
                .setNegativeButtonListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        fileBrowser.dismiss();
                    }
                })
                .create(this);
        fileBrowser.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentFragment != null) {
            Log.d(TAG, "currentFragment, id:" + currentFragment.getId() +
                    ", tag:" + currentFragment.getTag() +
                    ", string:" + currentFragment.toString());
            Log.d(TAG, "keyCode:" + keyCode + " event:" + event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                if (getCheckedItemList().size() >= MAX_FILE_NUM) {
                    String tipStr = String.format(getResources().getString(R.string.max_file_num_tip), MAX_FILE_NUM);
                    Toast.makeText(this, tipStr, Toast.LENGTH_LONG).show();
                    return;
                }
                goFileBrowser();
                break;
            case R.id.ensureBtn:
                if (getCheckedItemList().size() == 0) {
                    Toast.makeText(this, R.string.sel_empty_alert, Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(KEY_CHECKED_LIST, (Serializable)getCheckedItemList());
                startActivity(intent);
                break;
            case R.id.delBtn:
                if (mFileSelectAdapter.getCount() <  1) {
                    Toast.makeText(this, "No more items!", Toast.LENGTH_LONG).show();
                    return;
                }
                mFileSelectAdapter.delOneFromBack();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
        if (checkBox != null) {
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    public List<String> getCheckedItemList() {
        return mFileSelectAdapter.getCheckedItemList();
    }

    @Override
    public void onSelect(String path) {
        selectedList.add(path);
        mFileSelectAdapter.addData(path);
        fileBrowser.dismiss();
        lastDir = new File(path).getParent();
    }
}