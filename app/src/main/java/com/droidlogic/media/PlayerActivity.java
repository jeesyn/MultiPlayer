package com.droidlogic.media;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.droidlogic.media.adapter.VideosAdapter;

import java.util.List;

/**
 * Created by yingwei.long on 2016/11/29.
 */

public class PlayerActivity extends Activity implements SurfaceHolder.Callback,
    MediaControlAgent.ControllerCallback, View.OnClickListener {
    private static final String TAG = "PlayerActivity";
    private static final String KEY_CHECKED_LIST = "checked_list";
//    private PlayerFragmentCallback mCallback = null;
    private List<String> checkedList;
    private GridView videosView;
//    private LogicSurfaceView[] mSurfaceViews;
    private PercentRelativeLayout[] mSurfaceContainer;
    private SurfaceHolder[] mSurfaceHolders;
    private Player[] mPlayers;
    private VideosAdapter mVideosAdapter;
    private MediaControlAgent mediaControlAgent;
    private PlayerManager playerManager;
    private SurfaceView topSurfaceView;
    private SurfaceHolder topSurfaceHolder;
    private  int currentIndex = -1;
    private boolean fullScreenFlag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        checkedList = (List<String>)intent.getSerializableExtra(KEY_CHECKED_LIST);
        setContentView(R.layout.layout_multi_player);
        Log.d(TAG, "checkedList size:" + checkedList.size());
        mediaControlAgent = new MediaControlAgent(this);
        playerManager = PlayerManager.getInstance(this);
        initPlayerViews();
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        registerReceiver(mInfoReceiver, iF);
    }

    private int getAutoSizedSurfacLayout( int size) {
        int layout = 0;
        if (size <= 2) {
            layout =  R.layout.video_item_view_1_2;
        }
        else if (size >=3 &&size <=6) {
            layout = R.layout.video_item_view_3_6;
        }

        return layout;
    }

    private int getAutoSizedNumColumn(int size) {
        if (size <= 2)
            return size;
        else if (size > 2 && size <=4)
            return 2;
        else if(size > 4 && size <= 12)
            return 3;
        else if(size >12 && size <=16)
            return 4;
        else
            return 5;
    }

    private void initPlayerViews() {
        if (checkedList.size() == 0) {
            return;
        }

        videosView = (GridView) findViewById(R.id.videosView);
        mSurfaceHolders = new SurfaceHolder[checkedList.size()];
        mSurfaceContainer = new PercentRelativeLayout[checkedList.size()];
        LayoutInflater inflater = LayoutInflater.from(this);


        for (int i = 0; i < checkedList.size(); i++) {
            mSurfaceContainer[i] = (PercentRelativeLayout)
                    inflater.inflate(getAutoSizedSurfacLayout(checkedList.size()), null);
            SurfaceView surface =(SurfaceView) (
                            mSurfaceContainer[i].findViewById(R.id.surface_view));
            mSurfaceHolders[i] = surface.getHolder();
            mSurfaceHolders[i].addCallback(this);
        }

        mVideosAdapter = new VideosAdapter(this, checkedList, mSurfaceContainer);
        videosView.setNumColumns(getAutoSizedNumColumn(checkedList.size()));
        videosView.setAdapter(mVideosAdapter);
        videosView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Now videosView onItemClick, position:" + position);
                currentIndex = position;
                mediaControlAgent.showController(position, true, true);
            }
        });

        topSurfaceView = (SurfaceView)findViewById(R.id.topSurface);
        topSurfaceHolder = topSurfaceView.getHolder();
        topSurfaceHolder.addCallback(this);
       // topSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        topSurfaceView.setZOrderOnTop(true);
        topSurfaceView.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        playerManager = PlayerManager.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Now onStop!");
        supe.onStop();
        playerManager.clear();
    }

    private void clean() {

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Now enter onDestroy!");
        unregisterReceiver(mInfoReceiver);
        mediaControlAgent.stop();
        clean();
        super.onDestroy();
    }



    private int indexOf(SurfaceHolder holder) {
        for (int i = 0; i < mSurfaceHolders.length; i++)
            if (mSurfaceHolders[i] == holder)
                return i;
        return -1;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
                + "): surfaceCreated called");
        int index = indexOf(holder);
        if (index >= 0 && playerManager.getPlayer(index) == null) {
            Log.d(TAG, "surfaceCreated, Now create player:" + index);
            Player player = new Player(this, holder, checkedList.get(index));
            player.setIndex(index);
            playerManager.addPlayer(player);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
                + "): surfaceChanged called," + "width=" + width + ",height="
                + height);

        int index = indexOf(holder);
        Player player;
        if (index >= 0) {
             player = playerManager.getPlayer(index);
            if (player != null && !player.isAlive()) {
                Log.d(TAG, "surfaceChanged, index:" + index + " enter start!");
                player.start();
            }
        } else if (holder == topSurfaceHolder) {
            player = playerManager.getPlayer(currentIndex);
            if (player != null) {
                Log.d(TAG, "surfaceChanged,  Now player.play");
                player.setDisplay(topSurfaceHolder);
                player.play();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
                + "): surfaceDestroyed called");
        int index = indexOf(holder);
        if (index >= 0) {
            Player player = playerManager.getPlayer(index);
            if (player != null) {
                player.stopPlay();
                player.release();
            }
        }  else if (holder == topSurfaceHolder) {
//            Player player = playerManager.getPlayer(currentIndex);
//            if (player != null) {
//                player.stopPlay();
//                player.release();
//            }
        }
    }

    private BroadcastReceiver mInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.d(TAG, action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.d("TAG", "track info " + artist+":" + album + ":" + track);
        }
    };

    private void fullScreenAtOtherActivity(int index) {
        pauseAllPlayers();
        Intent intent = new Intent();
        intent.setClass(PlayerActivity.this, FullScreenActivity.class);
        intent.putExtra("FULL_INDEX", index);
        startActivity(intent);
    }

    private void pauseAllPlayers() {
        List<Player> playerList = playerManager.getPlayerList();
        if (playerList == null)
            return ;
        for (Player player : playerList) {
            if (player != null) {
                player.pause();
            }
        }
    }

    @Override
    public void onFullScreen(int index) {
        List<Player> playerList = playerManager.getPlayerList();
        Player fullPlayer = null;
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (player != null) {
                if (i != index) {
//                    player.stopPlay();
                    player.pause();
                } else {
                    fullPlayer = player;
//                    toFullPlayer.pause();
                    player.pause();
                }
            }
        }
        if (fullPlayer != null) {
            videosView.setVisibility(View.INVISIBLE);
            topSurfaceView.setVisibility(View.VISIBLE);
            fullScreenFlag = true;
        }

        //fullScreenAtOtherActivity(index);
    }

    @Override
    public void onBackPressed() {
        List<Player> playerList = playerManager.getPlayerList();
        if (fullScreenFlag) { //back from fullscreen
            if (topSurfaceView != null || topSurfaceView.getVisibility() == View.VISIBLE) {

                if (currentIndex == -1)
                    currentIndex = 0;
                Player tmpPlayer = playerList.get(currentIndex);
                if (tmpPlayer != null) {
                    tmpPlayer.pause();
                }

                videosView.setVisibility(View.VISIBLE);
                topSurfaceView.setVisibility(View.INVISIBLE);
                for (int i = 0; i < playerList.size(); i++) {
                    Player player = playerList.get(i);
                    if (player != null) {
                        if (player.paused()) {
                            if (i == currentIndex) {
                                player.setDisplay(mSurfaceHolders[i]);
                            }
                            player.play();
                        }
                    }
                }
                fullScreenFlag = false;
                return;
            }
        }
        else {
            for (Player player : playerList){
                if (player != null) {
                    player.stopPlay();
                }
            }
            //exit PlayerActivity
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.topSurface:
                Log.d(TAG, "Now onClick full_surface");
                mediaControlAgent.showController(currentIndex, true, false);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode=" + keyCode + "event" + event);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            mediaControlAgent.showController(currentIndex, true, false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

