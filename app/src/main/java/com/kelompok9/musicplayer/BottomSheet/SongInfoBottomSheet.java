package com.kelompok9.musicplayer.BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kelompok9.musicplayer.Activity.AlbumsActivity;
import com.kelompok9.musicplayer.Activity.ArtistsActivity;
import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.Model.SaveFavouriteList;
import com.kelompok9.musicplayer.Model.SavePlayList;
import com.kelompok9.musicplayer.Model.SavePlayListSong;
import com.kelompok9.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class SongInfoBottomSheet extends BottomSheetDialogFragment {

    public Dialog dialog;

    ArrayList<ArrayList<Long>> playListSongList;
    ArrayList<Long> songList;
    ArrayList<String> playListTitle;
    SavePlayList savePlayList;
    SavePlayListSong savePlayListSong;

    int position;
    public SongInfoBottomSheet(int i)
    {
           position = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.song_info_bottom_sheet,container,false);
        TextView songname = view.findViewById(R.id.song_info_bottom_sheet_songname);
        TextView cancel = view.findViewById(R.id.song_info_bottom_sheet_cancel);
        CircleImageView addTo = view.findViewById(R.id.song_info_bottom_sheet_addTo);
        CircleImageView favourite = view.findViewById(R.id.song_info_bottom_sheet_favourite);
        CircleImageView viewAlbum = view.findViewById(R.id.song_info_bottom_sheet_view_album);
        CircleImageView viewArtist = view.findViewById(R.id.song_info_bottom_sheet_artist);

        songname.setText(MainActivity.AudioArrayList.get(position).getTrack());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        addTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectPlayListDialog();
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                setFavourite();

            }
        });

        viewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                showAlbumActivity();
            }
        });

        viewArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                showArtistActivity();
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

    public void showArtistActivity()
    {
        String artist =  MainActivity.AudioArrayList.get(position).getArtist();
        for(int i=0;i<MainActivity.ArtistArrayList.size();i++)
        {
            if(MainActivity.ArtistArrayList.get(i).size()!=0 &&  artist!=null && artist.equals(MainActivity.ArtistArrayList.get(i).get(0).getArtist()))
            {
                Intent intent = new Intent(getContext(), ArtistsActivity.class);
                intent.putExtra("index", i);
                startActivity(intent);
                break;
            }
        }
    }

    public void showAlbumActivity()
    {
       String albumName =  MainActivity.AudioArrayList.get(position).getAlbum();
       for(int i=0;i<MainActivity.AlbumArrayList.size();i++)
       {
           if(MainActivity.AlbumArrayList.get(i).size()!=0 && albumName == MainActivity.AlbumArrayList.get(i).get(0).getAlbum())
           {
               Intent intent = new Intent(getContext(), AlbumsActivity.class);
               intent.putExtra("index", i);
               startActivity(intent);
               break;
           }
       }
    }

    public void setFavourite()
    {
        ArrayList<String> favList=null;
        SaveFavouriteList saveFavouriteList;
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("favourote_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = nPrefs.getString("favouriteSong", "");
        saveFavouriteList = gson1.fromJson(json1, SaveFavouriteList.class);


        int flag=0;
        if(saveFavouriteList!=null && saveFavouriteList.getList().size()!=0)
        {
            favList = saveFavouriteList.getList();
            for(int i=0;i<favList.size();i++)
            {
                if(favList.get(i).equals(MainActivity.AudioArrayList.get(position).getTrack()))
                {
                    flag = 1;
                    break;
                }
            }
        }
        else
        {
            favList=new ArrayList<>();
        }
        if(flag == 0)
        {

            favList.add(MainActivity.AudioArrayList.get(position).getTrack());
            SaveFavouriteList saveFavouriteList1 = new SaveFavouriteList(favList);
            SharedPreferences.Editor editor1 = nPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(saveFavouriteList1);
            editor1.putString("favouriteSong", json);
            editor1.apply();
            Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(getActivity(), "Already Added to My favourite!", Toast.LENGTH_SHORT).show();
        }






    }


    public void showSelectPlayListDialog()
    {
        dialog = new Dialog(MainActivity.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_playlist);
        dialog.setCancelable(true);
        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();

        try {
            wm.copyFrom(dialog.getWindow().getAttributes());
            wm.width = WindowManager.LayoutParams.MATCH_PARENT;
            wm.height = WindowManager.LayoutParams.WRAP_CONTENT;

        }catch(NullPointerException e){
            System.out.println(e.toString());
            Toast.makeText(MainActivity.activity,"Null Pointer Exception", Toast.LENGTH_LONG).show();
        }

        LinearLayout linearLayout = dialog.findViewById(R.id.l_l_select_playlist);
        TextView textView = dialog.findViewById(R.id.slect_playlist_text);
        ListView listView = dialog.findViewById(R.id.dialog_select_playlist_listview);



        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            textView.setTextColor(getResources().getColor(R.color.colorLight));
            listView.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
        }


        SharedPreferences mPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json1 = mPrefs.getString("playlist", "");
            savePlayList = gson1.fromJson(json1, SavePlayList.class);

            Gson gson2 = new Gson();
            String json2 = mPrefs.getString("playlistSong", "");
            savePlayListSong = gson2.fromJson(json2, SavePlayListSong.class);

            if(savePlayList!=null) {
                playListTitle = savePlayList.getList();
                Myadapter myadapter = new Myadapter();
                listView.setAdapter(myadapter);
            }


        dialog.show();
        dialog.getWindow().setAttributes(wm);
    }

    class Myadapter extends BaseAdapter{

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

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.select_playlist_items,viewGroup, false);
            LinearLayout linearLayout = view.findViewById(R.id.select_playlist_items);
            final ImageView imageView = view.findViewById(R.id.select_playlist_items_image);
            TextView textView = view.findViewById(R.id.select_playlist_items_name);
            LinearLayout linearLayout1 = view.findViewById(R.id.l_l_l_select_playlist);
            TextView textView1 = view.findViewById(R.id.select_playlist_items_song_count);


            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);
            if(theme!=null && theme.equals("dark"))
            {
                linearLayout1.setBackgroundColor(getResources().getColor(R.color.dimgrey));
                textView.setTextColor(getResources().getColor(R.color.colorLight));
                textView1.setTextColor(getResources().getColor(R.color.darkgrey));
            }



            textView1.setText("0 Songs");
            if(savePlayListSong!=null && savePlayListSong.getPlayListSong().size()!=0 && savePlayListSong.getPlayListSong().get(i).size()!=0)
            {

                for(int j=0;j<MainActivity.AudioArrayList.size();j++)
                {
                    if(MainActivity.AudioArrayList.get(j).getId().equals(savePlayListSong.getPlayListSong().get(i).get(0)))
                    {

                        final View finalView = view;
                        Picasso.with(view.getContext())
                                .load(MainActivity.AudioArrayList.get(j).getAlbumArtUri())
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        Picasso.with(finalView.getContext())
                                                .load(R.drawable.album_art)
                                                .into(imageView);
                                    }
                                });

                        textView1.setText(savePlayListSong.getPlayListSong().get(i).size()+" Songs");
                        break;

                    }


                }
                }

            textView.setText(playListTitle.get(i));
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSongInPlayList(i);
                    dialog.dismiss();
                    dismiss();
                }
            });


            return view;
        }
    }

    public void setSongInPlayList(int pos)
    {

        loadData(pos);
        saveData(pos);
        Toast.makeText(MainActivity.activity,"Added to "+"'"+playListTitle.get(pos)+"'"+ " Successfully \u2713", Toast.LENGTH_SHORT).show();



    }


    public void loadData(int pos)
    {
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = nPrefs.getString("playlistSong", "");
        savePlayListSong = gson1.fromJson(json1, SavePlayListSong.class);

        if(savePlayListSong!=null) {
            playListSongList = savePlayListSong.getPlayListSong();
            songList = playListSongList.get(pos);
        }
        else {
            songList = new ArrayList<>();
        }

    }

    public void saveData(int pos)
    {
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("playlist_pref",MODE_PRIVATE);
        songList.add(MainActivity.AudioArrayList.get(position).getId());
        //Toast.makeText(MainActivity.activity.getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
        //playListSongList.add(pos, songList);
        savePlayListSong = new SavePlayListSong(playListSongList);
        SharedPreferences.Editor prefsEditor = nPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savePlayListSong);
        prefsEditor.putString("playlistSong", json);
        prefsEditor.apply();
    }

}
