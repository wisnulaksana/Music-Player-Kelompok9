package com.kelompok9.musicplayer.Model;

import java.util.ArrayList;

public class SaveFavouriteList {


    ArrayList<String> arrayList;

    public SaveFavouriteList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<String> getList()
    {
        return arrayList;
    }
}
