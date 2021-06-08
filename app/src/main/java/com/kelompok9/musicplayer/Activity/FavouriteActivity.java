package com.kelompok9.musicplayer.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Fragment.PlayLists;
import com.kelompok9.musicplayer.Model.SavePlayList;
import com.kelompok9.musicplayer.Model.SavePlayListSong;
import com.kelompok9.musicplayer.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.kelompok9.musicplayer.Activity.MainActivity.PresentArrayList;
import static com.kelompok9.musicplayer.Activity.MainActivity.mediaPlayer;
import static com.kelompok9.musicplayer.Activity.MainActivity.position;


public class FavouriteActivity extends AppCompatActivity {
    int k;

    public static MaterialCardView relativeLayout;
    public static ImageView albumArt;
    public static TextView songName;
    public static Button pause,play,clear;
    public static ProgressBar progressBar;
    public static Activity activity;
    public SavePlayList savePlayList;
    public SavePlayListSong savePlayListSong;
    public ArrayList<ArrayList<Integer>> playListSongList;
    public ArrayList<String> SongTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = findViewById(R.id.toolbar_favourite);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_favourite);
        activity = this;



        toolbar.setTitle("My favourite");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        TextView songCount = findViewById(R.id.favourite_activity_songs_count);
        songCount.setText(PlayLists.favList.size()+" Songs");

        RelativeLayout allPlay = findViewById(R.id.activity_favourite_all_play);
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSongSelected(0);
            }
        });

        ImageView imageView = findViewById(R.id.favourite_content_image);

        Glide.with(this)
                .load(PlayLists.favList.get(0).getAlbumArtUri())
                .error(R.drawable.album_art)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .into(imageView);



            ListView listView = findViewById(R.id.list_favourite_contents);
            //explitely defining the listView nestedScrollable
            ViewCompat.setNestedScrollingEnabled(listView, true);

            MyAdapter myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);

            //checking for existing music

            if(mediaPlayer !=null)
            {
                setBottomPlayer();
            }




        LinearLayout linearLayout = findViewById(R.id.linear_layout2);
        TextView songAllPlay = findViewById(R.id.song_play_all2);


        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorDark));
            songCount.setTextColor(getResources().getColor(R.color.colorLight));
            songAllPlay.setTextColor(getResources().getColor(R.color.colorLight));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.toolbar_color));


        }








    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setBottomPlayer()
    {
        relativeLayout = findViewById(R.id.favourite_bottom_now_playing);
        if(relativeLayout.getVisibility() != View.VISIBLE)
        {
            relativeLayout.setVisibility(View.VISIBLE);

        }
        CoordinatorLayout coordinatorLayout = relativeLayout.findViewById(R.id.bottom_now_playing_layout);
        albumArt = findViewById(R.id.bottom_now_playing_album_art);
        songName = findViewById(R.id.bottom_now_playing_song_name);
        play = findViewById(R.id.bottom_now_playing_song_play);
        pause = findViewById(R.id.bottom_now_playing_song_pause);
        clear = findViewById(R.id.bottom_now_playing_song_clear);
        progressBar = findViewById(R.id.bottom_now_playing_progressbar);
        songName.setSingleLine();
        songName.setSelected(true);
        Picasso.with(this)
                .load(PresentArrayList.get(position).getAlbumArtUri())
                .placeholder(R.drawable.album_art)
                .into(albumArt);
        songName.setText(PresentArrayList.get(position).getTrack());

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            coordinatorLayout.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.toolbar_color));
            songName.setTextColor(MainActivity.activity.getResources().getColor(R.color.colorLight));
            progressBar.setProgressTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            play.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            pause.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            clear.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
        }

        controlMedia();

    }
    public static void controlMedia()
    {
        if(!mediaPlayer.isPlaying())
        {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        }
        else {
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
        }



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                progressBar.setMax(0);
                progressBar.setMax(PresentArrayList.get(position).getDuration());


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                mediaPlayer.pause();
            }
        });



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer=null;
                relativeLayout.setVisibility(View.GONE);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity.getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("id",4);
                activity.startActivity(intent);



            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                progressBar.setProgress(0);
                progressBar.setMax(0);
                progressBar.refreshDrawableState();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });

        new Thread(){

            @Override
            public void run() {
                int duration = PresentArrayList.get(position).getDuration();
                int current = 0;
                progressBar.setMax(0);
                progressBar.setMax(duration);
                while (current<duration)
                {
                    if(mediaPlayer!=null) {
                        current = mediaPlayer.getCurrentPosition();
                        progressBar.setProgress(current);
                    }
                }
            }
        }.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return PlayLists.favList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.activity_favourite_content_item, viewGroup, false);
            RelativeLayout relativeLayout = view.findViewById(R.id.favourite_content_items);
            TextView textView = view.findViewById(R.id.songName_favourite);
            TextView textView1 = view.findViewById(R.id.songArtist_favourite);
            TextView slno = view.findViewById(R.id.slno_favourite);
            LinearLayout info = view.findViewById(R.id.favourite_activity_songs_info);
            textView.setText(PlayLists.favList.get(i).getTrack());
            textView1.setText(PlayLists.favList.get(i).getArtist());
            if(i<9)
            {
                slno.setText("0"+(i+1));
            }
            else {
                slno.setText(""+(i+1));
            }

            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);

            if(theme!=null && theme.equals("dark"))
            {
                view.setBackgroundColor(getResources().getColor(R.color.colorDark));
                textView.setTextColor(getResources().getColor(R.color.colorLight));
                textView1.setTextColor(getResources().getColor(R.color.darkgrey));
                info.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity, R.color.darkgrey));
            }



            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onSongSelected(i);


                }
            });

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });


            return view;
        }
    }
    public void onSongSelected(int i)
    {
        if(PresentArrayList!=null)
            PresentArrayList=null;

        PresentArrayList = new ArrayList<>();
        for(int j=0;j<PlayLists.favList.size();j++)
        {
            PresentArrayList.add(PlayLists.favList.get(j));
        }

        position = i;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.parse(PresentArrayList.get(position).getData()));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.parse(PresentArrayList.get(position).getData()));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
        setBottomPlayer();

        MainActivity.prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        MainActivity.prefs.edit().putInt("position",position).commit();
        MainActivity.prefs.edit().putInt("which",4).commit();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setBottomPlayerStatusInMainActicity();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        setBottomPlayerStatusInMainActicity();
        return super.onSupportNavigateUp();
    }


    public void setBottomPlayerStatusInMainActicity()
    {
        if(mediaPlayer==null)
        {
            if(MainActivity.relativeLayout!=null && MainActivity.relativeLayout.getVisibility() == View.VISIBLE)
            {
                MainActivity.relativeLayout.setVisibility(View.GONE);
            }
        }
        else
        {
            setMainBottomPlayer();


        }
    }

    public void setMainBottomPlayer()
    {


        MainActivity.relativeLayout = MainActivity.activity.findViewById(R.id.bottom_now_playing);
        if(MainActivity.relativeLayout.getVisibility() != View.VISIBLE)
        {
            MainActivity.relativeLayout.setVisibility(View.VISIBLE);

        }
        MainActivity.albumArt = MainActivity.activity.findViewById(R.id.bottom_now_playing_album_art);
        MainActivity.songName = MainActivity.activity.findViewById(R.id.bottom_now_playing_song_name);
        MainActivity.play = MainActivity.activity.findViewById(R.id.bottom_now_playing_song_play);
        MainActivity.pause = MainActivity.activity.findViewById(R.id.bottom_now_playing_song_pause);
        MainActivity.clear = MainActivity.activity.findViewById(R.id.bottom_now_playing_song_clear);
        MainActivity.progressBar = MainActivity.activity.findViewById(R.id.bottom_now_playing_progressbar);
        MainActivity.songName.setSingleLine();
        MainActivity.songName.setSelected(true);
        Picasso.with(this)
                .load(PresentArrayList.get(position).getAlbumArtUri())
                .placeholder(R.drawable.album_art)
                .into(MainActivity.albumArt);
        MainActivity.songName.setText(PresentArrayList.get(position).getTrack());
        MainActivity.play.setVisibility(View.GONE);
        MainActivity.pause.setVisibility(View.VISIBLE);



        MainActivity.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.play.setVisibility(View.GONE);
                MainActivity.pause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                MainActivity.progressBar.setMax(0);
                MainActivity.progressBar.setMax(PresentArrayList.get(position).getDuration());


            }
        });

        MainActivity.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.play.setVisibility(View.VISIBLE);
                MainActivity.pause.setVisibility(View.GONE);
                mediaPlayer.pause();
            }
        });


        MainActivity.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer=null;
                MainActivity.relativeLayout.setVisibility(View.GONE);
            }
        });

        MainActivity.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("id",4);
                startActivity(intent);



            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                MainActivity.pause.setVisibility(View.GONE);
                MainActivity.play.setVisibility(View.VISIBLE);
            }
        });

        new Thread(){

            @Override
            public void run() {
                int duration = PresentArrayList.get(position).getDuration();
                int current = 0;
                MainActivity.progressBar.setMax(0);
                MainActivity.progressBar.setMax(duration);
                while (current<duration)
                {
                    if(mediaPlayer!=null)
                    {
                        current = mediaPlayer.getCurrentPosition();
                        MainActivity.progressBar.setProgress(current);
                    }

                }
            }
        }.start();

        if(!mediaPlayer.isPlaying())
        {
            MainActivity.pause.setVisibility(View.GONE);
            MainActivity.play.setVisibility(View.VISIBLE);
        }

    }

    public void showDialog()
    {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_implemented_later);
        dialog.setCancelable(true);
        dialog.onBackPressed();
        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();

        try {
            wm.copyFrom(dialog.getWindow().getAttributes());
            wm.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wm.height = WindowManager.LayoutParams.WRAP_CONTENT;

        }catch(NullPointerException e){
            System.out.println(e.toString());
            Toast.makeText(MainActivity.activity,"Null Pointer Exception", Toast.LENGTH_LONG).show();
        }

        MaterialCardView materialCardView = dialog.findViewById(R.id.layout_not_implemented);
        TextView textView = dialog.findViewById(R.id.text);
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark")) {
            materialCardView.setBackgroundColor(getResources().getColor(R.color.colorDark));
            textView.setTextColor(getResources().getColor(R.color.darkgrey));
        }

        dialog.show();
        dialog.getWindow().setAttributes(wm);

    }

}


