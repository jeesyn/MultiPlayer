package com.droidlogic.media;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    static private PlayerManager mInstance = null;
    private List<Player> playerList;


    public  static PlayerManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlayerManager(context);
        }
        return mInstance;
    }

    public PlayerManager(Context context) {
        playerList = new ArrayList<Player>();
    }

//    public void setPathList(List<String> pathList) {
//        mPathList = pathList;
//    }
    public List<Player> getPlayerList() {
        return playerList;
    }

    public int getPlayerCount() {
        return playerList.size();
    }

    public Player getPlayer(int index) {
        if (index < 0 || index >= playerList.size()) {
            return null;
        }
        return playerList.get(index);
    }

    public boolean hasPlayer(Player player) {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).isSame(player)) {
                return true;
            }
        }
        return false;
    }

    public int addPlayer(Player player) {
        if (hasPlayer(player))
            return -1;
        playerList.add(playerList.size(), player);
        return playerList.size() -1;
    }

    public int delPlayer(Player player) {
        if (hasPlayer(player)) {
            playerList.remove(player);
        }
        return playerList.size() - 1;
    }

    public void clear()  {
        for (int i = 0; i <  playerList.size(); i++) {
            getPlayer(i).interrupt();
            getPlayer(i).release();
        }
        playerList.clear();
    }
}
