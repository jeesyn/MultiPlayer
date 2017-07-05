package com.droidlogic.media;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;

/**
 * Created by yingwei.long on 2017/1/26.
 */

public class FullScreenActivity extends Activity  implements SurfaceHolder.Callback, View.OnClickListener, MediaControlAgent.ControllerCallback {

    private static final String TAG = "FullScreenActivity";
    private int fullIndex = 0;
    private PlayerManager playerManager;
    private MediaControlAgent mediaControlAgent;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_fullscreen);
        playerManager = PlayerManager.getInstance(this);
        Intent intent = getIntent();
        fullIndex = intent.getIntExtra("FULL_INDEX", 0);
        surfaceView = (SurfaceView)findViewById(R.id.full_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaControlAgent = new MediaControlAgent(this);
        surfaceView.setOnClickListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Player player;
        if (fullIndex >= 0) {
            player = playerManager.getPlayer(fullIndex);
            Log.d(TAG, "player count:" + playerManager.getPlayerCount());
            if (player != null) {
                player.setDisplay(holder);
                player.play();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.full_surface:
//                mediaControlAgent.showController(fullIndex, true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            mediaControlAgent.showController(fullIndex, true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFullScreen(int index) {

    }
}
