package com.kelompok9.musicplayer.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kelompok9.musicplayer.Activity.FavouriteActivity;
import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.Activity.PlayListActivity;
import com.kelompok9.musicplayer.BottomSheet.PlayListInfoBottomSheet;
import com.kelompok9.musicplayer.Model.AudioListModel;
import com.kelompok9.musicplayer.Model.SaveFavouriteList;
import com.kelompok9.musicplayer.Model.SavePlayList;
import com.kelompok9.musicplayer.Model.SavePlayListSong;
import com.kelompok9.musicplayer.R;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;
import static com.kelompok9.musicplayer.Activity.MainActivity.AudioArrayList;


public class PlayLists extends Fragment  {

    private   ListView listView;
    private  MyAdapter myAdapter;
    public static   ArrayList<String> playListTitle;
    public  SharedPreferences  mPrefs;
    public SavePlayList savePlayList;
    public SavePlayListSong savePlayListSong;

    public static ArrayList<ArrayList<Long>> playListSongList;
    public static ArrayList<Long> songList;

    View view;
    TextView favsongCount;

    public static ArrayList<AudioListModel> favList;
    public PlayLists(){

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_play_lists, container, false);


        LinearLayout linearLayout = view.findViewById(R.id.create_playlist_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdCreatePlaylistDialog(view);
            }
        });
        mPrefs= MainActivity.activity.getPreferences(MODE_PRIVATE);
        listView = view.findViewById(R.id.play_lists);
        favsongCount = view.findViewById(R.id.favourite_count);
        ImageView createPlay = view.findViewById(R.id.create_playlist_image);
        TextView createName = view.findViewById(R.id.create_palylist_name);

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorDark));
            createName.setTextColor(getResources().getColor(R.color.colorLight));
            createPlay.setBackgroundColor(getResources().getColor(R.color.dimgrey));

        }

        this.view = view;


        loadFromCache();




        return view;
    }

    public void loadFromCache()
    {
        //loading data from playlist
        playListSongList =null;
        playListTitle =null;
        favList = null;
        favList = new ArrayList<>();
        mPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = mPrefs.getString("playlistSong", "");
        savePlayListSong = gson1.fromJson(json1, SavePlayListSong.class);

        if(savePlayListSong!=null)
        {
            playListSongList = savePlayListSong.getPlayListSong();
        }

        Gson gson = new Gson();
        String json = mPrefs.getString("playlist", "");
        savePlayList = gson.fromJson(json, SavePlayList.class);
        if(savePlayList!=null) {
            playListTitle = savePlayList.getList();
            myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);
            listView.setDividerHeight(0);
            //expandListViewHeight(listView);
            ViewCompat.setNestedScrollingEnabled(listView, true);
        }


        //loading data from favourite
        ArrayList<String> fav;
        SharedPreferences nnPrefs= MainActivity.activity.getSharedPreferences("favourote_pref",MODE_PRIVATE);
        Gson gson2 = new Gson();
        String json2 = nnPrefs.getString("favouriteSong", "");
        SaveFavouriteList saveFavouriteList;
        saveFavouriteList = gson2.fromJson(json2,SaveFavouriteList.class);

        final ImageView favouriteAlbum = view.findViewById(R.id.create_myfabourite_image);
        LinearLayout favourite = view.findViewById(R.id.favourite_play_list);
        favsongCount.setText("0 songs");

        if(saveFavouriteList!=null && saveFavouriteList.getList().size()!=0)
        {
            fav = saveFavouriteList.getList();
            for(int j=0;j<fav.size();j++) {
                for (int i=0;i<AudioArrayList.size();i++) {
                    if(fav.get(j).equals(AudioArrayList.get(i).getTrack()))
                    {
                        favList.add(AudioArrayList.get(i));
                        break;
                    }
                }
            }


        }

        if(favList.size()!=0)
        {

            Picasso.with(view.getContext())
                    .load(favList.get(0).getAlbumArtUri())
                    .into(favouriteAlbum, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(view.getContext())
                                    .load(R.drawable.ic_album_black_24dp)
                                    .into(favouriteAlbum);
                        }
                    });

            favsongCount.setText(favList.size()+" songs");

        }

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickedFavouriteList();
            }
        });


        LinearLayout ll = view.findViewById(R.id.favourite_play_layout);
        TextView favName = view.findViewById(R.id.fav_name);
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            ll.setBackgroundColor(getResources().getColor(R.color.dimgrey));
            favName.setTextColor(getResources().getColor(R.color.colorLight));
            favsongCount.setTextColor(getResources().getColor(R.color.darkgrey));

        }


    }


    public void onClickedFavouriteList()
    {
        if(favList != null && favList.size()!=0)
        {

            Intent intent = new Intent(getContext(), FavouriteActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Add Song First!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showdCreatePlaylistDialog(View view)
    {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_playlist_dialog);
        dialog.setCancelable(true);
        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();

        try {
            wm.copyFrom(dialog.getWindow().getAttributes());
            wm.width = WindowManager.LayoutParams.MATCH_PARENT;
            wm.height = WindowManager.LayoutParams.WRAP_CONTENT;

        }catch(NullPointerException e){
            System.out.println(e.toString());
            Toast.makeText(view.getContext(),"Null Pointer Exception", Toast.LENGTH_LONG).show();
        }

        RelativeLayout relativeLayout = dialog.findViewById(R.id.r_l_create);
        TextView textView = dialog.findViewById(R.id.create_text_name);
        final Button create = dialog.findViewById(R.id.create_playlist_create);
        final Button cancel = dialog.findViewById(R.id.create_playlist_cancel);
        final EditText playlistName = dialog.findViewById(R.id.create_playlist_edittext);

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            textView.setTextColor(getResources().getColor(R.color.colorLight));
            cancel.setTextColor(getResources().getColor(R.color.colorLight));
            playlistName.setHintTextColor(getResources().getColor(R.color.darkgrey));
            playlistName.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            playlistName.setTextColor(getResources().getColor(R.color.colorLight));
            cancel.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            create.setBackgroundColor(getResources().getColor(R.color.toolbar_color));

        }

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = playlistName.getText().toString().trim();
                loadData();
                saveData(name);
                loadDataIndex();
                saveDataIndex();
                setAdpterAfterUpadte();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(wm);
    }

    public  void setAdpterAfterUpadte()
    {

        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.setDividerHeight(0);
        //expandListViewHeight(listView);
        ViewCompat.setNestedScrollingEnabled(listView,true);
    }


    class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {

            return playListTitle.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.play_lists_items,viewGroup, false);
            LinearLayout playListPlay = view.findViewById(R.id.play_lists_play);
            LinearLayout          info = view.findViewById(R.id.play_lists_info_l);
            ImageView album = view.findViewById(R.id.album_art_playlist);
            TextView textView = view.findViewById(R.id.play_lists_name);
            TextView textView1 = view.findViewById(R.id.play_lists_song_count);
            textView.setText(playListTitle.get(i));
            textView1.setText("0 songs");
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onInfoSelected(i);
                }
            });

                if(playListSongList!=null) {
                    if(playListSongList.get(i).size()!=0) {

                        for(int j=0;j<AudioArrayList.size();j++)
                        {

                            if(playListSongList.get(i).get(0).equals(AudioArrayList.get(j).getId()))
                            {

                                Picasso.with(view.getContext())
                                        .load(AudioArrayList.get(j).getAlbumArtUri())
                                        .error(R.drawable.ic_album_black_24dp)
                                        .into(album);
                                textView1.setText(playListSongList.get(i).size()+" songs");
                                break;
                            }

                        }


                    }

                }



            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);
            if(theme!=null && theme.equals("dark"))
            {
                playListPlay.setBackgroundColor(getResources().getColor(R.color.dimgrey));
                info.setBackgroundColor(getResources().getColor(R.color.dimgrey));
                textView.setTextColor(getResources().getColor(R.color.colorLight));
                textView1.setTextColor(getResources().getColor(R.color.darkgrey));
            }



                playListPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doOnClickedPlayList(i);
                    }
                });







            return view;
        }
    }

    private void doOnClickedPlayList(int pos)
    {
        if(playListSongList.get(pos).size()!=0)
        {
            Intent intent = new Intent(getContext(), PlayListActivity.class);
            intent.putExtra("index", pos);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Add Song first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onInfoSelected(int pos)
    {
        PlayListInfoBottomSheet playListInfoBottomSheet = new PlayListInfoBottomSheet(pos);
        playListInfoBottomSheet.show(getChildFragmentManager(),"fragment");
    }




    public void loadData()
    {
        mPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = mPrefs.getString("playlist", "");
        savePlayList = gson1.fromJson(json1, SavePlayList.class);
        if(savePlayList==null)
        {
            playListTitle = new ArrayList<>();
        }
        else
        {
            playListTitle = savePlayList.getList();
        }


    }
    public void saveData(String name)
    {

        playListTitle.add(name);
        savePlayList = new SavePlayList(playListTitle);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savePlayList);
        prefsEditor.putString("playlist", json);
        prefsEditor.apply();
    }


    public void loadDataIndex()
    {
        Gson gson1 = new Gson();
        String json1 = mPrefs.getString("playlistSong", "");
        savePlayListSong = gson1.fromJson(json1, SavePlayListSong.class);
        if(savePlayListSong == null)
        {
            playListSongList = new ArrayList<>();
            songList = new ArrayList<>();
            playListSongList.add(songList);


        }
        else
        {
            songList = new ArrayList<>();
            playListSongList = savePlayListSong.getPlayListSong();
            playListSongList.add(songList);
        }
    }

    public void saveDataIndex()
    {
        savePlayListSong = new SavePlayListSong(playListSongList);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savePlayListSong);
        prefsEditor.putString("playlistSong", json);
        prefsEditor.apply();
    }


}