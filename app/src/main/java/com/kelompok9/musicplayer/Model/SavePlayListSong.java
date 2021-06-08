package com.kelompok9.musicplayer.Model;

import java.util.ArrayList;

public class SavePlayListSong {

    ArrayList<ArrayList<Long>> playListSong;

    public SavePlayListSong(ArrayList<ArrayList<Long>> playListSong) {
        this.playListSong = playListSong;
    }

    public ArrayList<ArrayList<Long>> getPlayListSong()
    {
        return playListSong;
    }
}
