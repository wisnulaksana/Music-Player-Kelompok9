package com.kelompok9.musicplayer.BottomSheet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.Fragment.PlayLists;
import com.kelompok9.musicplayer.Model.SavePlayList;
import com.kelompok9.musicplayer.Model.SavePlayListSong;
import com.kelompok9.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;


import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class PlayListInfoBottomSheet extends BottomSheetDialogFragment
{

    int position;
    SharedPreferences mPrefs;


    public PlayListInfoBottomSheet(int position) {
        this.position = position;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.playlist_info_bottmsheet, container, false);
        TextView playListName = view.findViewById(R.id.playlist_info_bottom_sheet_playlist_name);
        TextView playListDelete = view.findViewById(R.id.playlist_info_bottom_sheet_delete);
        final TextView playListCancel = view.findViewById(R.id.playlist_info_bottom_sheet_cancel);

        playListName.setText("Delete '"+ PlayLists.playListTitle.get(position)+"' ?");

        playListCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


            playListDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    saveData();
                    dismiss();
                }
            });

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            view.setBackgroundColor(getResources().getColor(R.color.grey));
        }




        return view;
    }

    public void saveData()
    {
        PlayLists.playListTitle.remove(position);
        PlayLists.playListSongList.remove(position);

        SharedPreferences mPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
        SavePlayList savePlayList;
        savePlayList = new SavePlayList(PlayLists.playListTitle);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(savePlayList);
        prefsEditor.putString("playlist", json1);


        SavePlayListSong savePlayListSong;
        savePlayListSong = new SavePlayListSong(PlayLists.playListSongList);
        Gson gson = new Gson();
        String json = gson.toJson(savePlayListSong);
        prefsEditor.putString("playlistSong", json);
        prefsEditor.commit();

        ((PlayLists)getParentFragment()).loadFromCache();
        ((PlayLists)getParentFragment()).setAdpterAfterUpadte();

    }

}
