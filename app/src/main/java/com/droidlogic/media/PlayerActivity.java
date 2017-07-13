package com.droidlogic.media;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.droidlogic.media.adapter.VideosAdapter;

import java.util.List;

/**
 * Created by yingwei.long on 2016/11/29.
 */

public class PlayerActivity extends Activity implements SurfaceHolder.Callback,
    MediaControlAgent.ControllerCallback, View.OnClickListener {
    private static final String TAG = "PlayerActivity";
    private static final String KEY_CHECKED_LIST = "checked_list";
    private static final int[] SURFACE_RES_IDS =
            { R.id.video_1_surfaceview, R.id.video_2_surfaceview ,
                    R.id.video_3_surfaceview, R.id.video_4_surfaceview};/*,
                    R.id.video_5_surfaceview, R.id.video_6_surfaceview,
                    R.id.video_7_surfaceview, R.id.video_8_surfaceview};*///, R.id.video_3_surfaceview, R.id.video_4_surfaceview};
//    private PlayerFragmentCallback mCallback = null;
    private List<String> checkedList;
    private GridView videoGridView;
//    private LogicSurfaceView[] mSurfaceViews;
    private SurfaceView[] mSurfaceViews;
    private LinearLayout [] mSurfaceContainers;
    private SurfaceHolder[] mSurfaceHolders;
    private Player[] mPlayers;
    private VideosAdapter mVideosAdapter;
    private MediaControlAgent mediaControlAgent;
    private PlayerManager playerManager;
    private SurfaceView topSurfaceView;
    private SurfaceHolder topSurfaceHolder;
    private  int currentIndex = -1;
    private boolean fullScreenOn = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.layout_multi_player);
        setContentView(R.layout.layout_multi_player);
        Intent intent = getIntent();
        checkedList = (List<String>)intent.getSerializableExtra(KEY_CHECKED_LIST);
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

    private void initPlayerViews() {
        if (checkedList.size() == 0) {
            return;
        }
//        mSurfaceViews = new LogicSurfaceView[checkedList.size()];
        mSurfaceViews = new SurfaceView[checkedList.size()];
        videoGridView = (GridView)findViewById(R.id.videoGridView);
        mSurfaceContainers = new LinearLayout[checkedList.size()];
        mSurfaceHolders = new SurfaceHolder[checkedList.size()];
//        mPlayers = new Player[checkedList.size()];
        LayoutInflater inflater = LayoutInflater.from(this);
//        for (int i = 0; i < checkedList.size(); i++) {
//            mSurfaceViews[i] = new LogicSurfaceView(this);
//            mSurfaceViews[i] = (SurfaceView)findViewById(SURFACE_RES_IDS[i]);
//            mSurfaceContainers[i] = (LinearLayout)inflater.inflate(R.layout.video_item_view, null);
//            mSurfaceViews[i].setMinimumWidth(1280);
//            mSurfaceViews[i].setMinimumHeight(720);
//        if (i == 1)
//        mSurfaceViews[i].setZOrderOnTop(true);
//            mSurfaceHolders[i] = mSurfaceViews[i].getHolder();
//            mSurfaceHolders[i].addCallback(this);
//            mSurfaceHolders[i].setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        }
        for (int i = 0; i < checkedList.size(); i++) {
            mSurfaceContainers[i] = (LinearLayout) inflater.inflate(R.layout.video_item_view, null);
            SurfaceView surface = (SurfaceView) (mSurfaceContainers[i].findViewById(R.id.surfaceView));
            mSurfaceViews[i] = surface;
            mSurfaceHolders[i] = surface.getHolder();
            mSurfaceHolders[i].addCallback(this);
           // mSurfaceHolders[i].setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mVideosAdapter = new VideosAdapter(this, checkedList, mSurfaceContainers);
        videoGridView.setAdapter(mVideosAdapter);
        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Now gridview onItemClick, position:" + position);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Now onStop!");
        super.onStop();
        mediaControlAgent.stop();
    }

    private void clean() {

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Now enter onDestroy!");
        unregisterReceiver(mInfoReceiver);
        playerManager.clear();
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
        Player player = null;
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
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (player != null) {
//                if (i != index) {
//                    player.stopPlay();
//                } else {
//                    fullPlayer = player;
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
            videoGridView.setVisibility(View.INVISIBLE);
            topSurfaceView.setVisibility(View.VISIBLE);
            fullScreenOn = true;
        }

        //fullScreenAtOtherActivity(index);
    }

    @Override
    public void onBackPressed() {
        List<Player> playerList = playerManager.getPlayerList();
        if (fullScreenOn) { //back from fullscreen
            if (topSurfaceView != null || topSurfaceView.getVisibility() == View.VISIBLE) {

                if (currentIndex == -1)
                    currentIndex = 0;
                Player tmpPlayer = playerList.get(currentIndex);
                if (tmpPlayer != null) {
                    tmpPlayer.pause();
                }

                videoGridView.setVisibility(View.VISIBLE);
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
                fullScreenOn = false;
                return;
            }
        }
        else {
            for (Player player : playerList){
                player.stopPlay();
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
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            mediaControlAgent.showController(currentIndex, true, false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

