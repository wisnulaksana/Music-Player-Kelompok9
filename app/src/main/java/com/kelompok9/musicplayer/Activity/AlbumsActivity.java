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

import com.kelompok9.musicplayer.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
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


public class AlbumsActivity extends AppCompatActivity {
    int k;

    public static MaterialCardView relativeLayout;
    public static ImageView albumArt;
    public static TextView songName;
    public static Button pause,play,clear;
    public static ProgressBar progressBar;
    public static Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        Toolbar toolbar = findViewById(R.id.toolbar_album);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_album);
        activity = this;

        Intent intent = getIntent();
        k = intent.getIntExtra("index", -1);

        toolbar.setTitle(MainActivity.AlbumArrayList.get(k).get(0).getAlbum());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.linear_layout);

        final ImageView imageView = findViewById(R.id.album_content_image);
        Picasso.with(this)
                .load(MainActivity.AlbumArrayList.get(k).get(0).getAlbumArtUri())
                .fit()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext())
                                .load(R.drawable.album_art)
                                .into(imageView);
                    }
                });


        TextView songCount = findViewById(R.id.album_activity_songs_count);
        songCount.setText(MainActivity.AlbumArrayList.get(k).size()+" Songs");

        RelativeLayout allPlay = findViewById(R.id.activity_album_all_play);
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSongSelected(0);
            }
        });

        TextView songPlayAll = findViewById(R.id.song_play_all);


        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorDark));
            songCount.setTextColor(getResources().getColor(R.color.colorLight));
            songPlayAll.setTextColor(getResources().getColor(R.color.colorLight));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.toolbar_color));


        }



        ListView listView = findViewById(R.id.list_album_contents);
        //explitely defining the listView nestedScrollable
        ViewCompat.setNestedScrollingEnabled(listView, true);

        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        //checking for existing music

        if(MainActivity.mediaPlayer !=null)
        {
            setBottomPlayer();
        }




    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setBottomPlayer()
    {
        relativeLayout = findViewById(R.id.album_bottom_now_playing);
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
                .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                .placeholder(R.drawable.album_art)
                .into(albumArt);
        songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());


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
        if(!MainActivity.mediaPlayer.isPlaying())
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
                MainActivity.mediaPlayer.start();
                progressBar.setMax(0);
                progressBar.setMax(MainActivity.PresentArrayList.get(MainActivity.position).getDuration());


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                MainActivity.mediaPlayer.pause();
            }
        });



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mediaPlayer.stop();
                MainActivity.mediaPlayer=null;
                relativeLayout.setVisibility(View.GONE);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity.getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("id",1);
                activity.startActivity(intent);



            }
        });

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
                int duration = MainActivity.PresentArrayList.get(MainActivity.position).getDuration();
                int current = 0;
                progressBar.setMax(0);
                progressBar.setMax(duration);
                while (current<duration)
                {
                    if(MainActivity.mediaPlayer!=null) {
                        current = MainActivity.mediaPlayer.getCurrentPosition();
                        progressBar.setProgress(current);
                    }
                }
            }
        }.start();

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_album, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MainActivity.AlbumArrayList.get(k).size();
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

            view = getLayoutInflater().inflate(R.layout.albums_content_items, viewGroup, false);
            RelativeLayout relativeLayout = view.findViewById(R.id.album_content_items);
            TextView textView = view.findViewById(R.id.songName_album);
            TextView textView1 = view.findViewById(R.id.songArtist_album);
            LinearLayout info = view.findViewById(R.id.album_activity_songs_info);
            TextView slno = view.findViewById(R.id.slno_album);
            textView.setText(MainActivity.AlbumArrayList.get(k).get(i).getTrack());
            textView1.setText(MainActivity.AlbumArrayList.get(k).get(i).getArtist());

            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);
            if(theme!=null && theme.equals("dark"))
            {
                view.setBackgroundColor(getResources().getColor(R.color.colorDark));
                textView.setTextColor(getResources().getColor(R.color.colorLight));
                textView1.setTextColor(getResources().getColor(R.color.darkgrey));
                info.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.darkgrey));
            }

            if(i<9)
            {
                slno.setText("0"+(i+1));
            }
            else {
                slno.setText(""+(i+1));
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
        if(MainActivity.PresentArrayList!=null)
            MainActivity.PresentArrayList=null;

        MainActivity.PresentArrayList = MainActivity.AlbumArrayList.get(k);
        MainActivity.position = i;
        if(MainActivity.mediaPlayer!=null)
        {
            MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.parse(MainActivity.PresentArrayList.get(MainActivity.position).getData()));
            MainActivity.mediaPlayer.setLooping(false);
            MainActivity.mediaPlayer.start();
        }
        else
        {
            MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.parse(MainActivity.PresentArrayList.get(MainActivity.position).getData()));
            MainActivity.mediaPlayer.setLooping(false);
            MainActivity.mediaPlayer.start();
        }
        setBottomPlayer();

        MainActivity.prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        MainActivity.prefs.edit().putInt("position", MainActivity.position).commit();
        MainActivity.prefs.edit().putInt("which",1).commit();
        MainActivity.prefs.edit().putInt("albumIndex",k).commit();

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
        if(MainActivity.mediaPlayer==null)
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
                .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                .into(MainActivity.albumArt);
        MainActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
        MainActivity.play.setVisibility(View.GONE);
        MainActivity.pause.setVisibility(View.VISIBLE);



        MainActivity.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.play.setVisibility(View.GONE);
                MainActivity.pause.setVisibility(View.VISIBLE);
                MainActivity.mediaPlayer.start();
                MainActivity.progressBar.setMax(0);
                MainActivity.progressBar.setMax(MainActivity.PresentArrayList.get(MainActivity.position).getDuration());


            }
        });

        MainActivity.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.play.setVisibility(View.VISIBLE);
               MainActivity.pause.setVisibility(View.GONE);
                MainActivity.mediaPlayer.pause();
            }
        });


        MainActivity.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mediaPlayer.stop();
                MainActivity.mediaPlayer=null;
                MainActivity.relativeLayout.setVisibility(View.GONE);
            }
        });

        MainActivity.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);



            }
        });

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                MainActivity.pause.setVisibility(View.GONE);
                MainActivity.play.setVisibility(View.VISIBLE);
            }
        });

        new Thread(){

            @Override
            public void run() {
                int duration = MainActivity.PresentArrayList.get(MainActivity.position).getDuration();
                int current = 0;
                MainActivity.progressBar.setMax(0);
                MainActivity.progressBar.setMax(duration);
                while (current<duration)
                {
                    if(MainActivity.mediaPlayer!=null)
                    {
                        current = MainActivity.mediaPlayer.getCurrentPosition();
                        MainActivity.progressBar.setProgress(current);
                    }

                }
            }
        }.start();

        if(!MainActivity.mediaPlayer.isPlaying())
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


