package com.kelompok9.musicplayer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Gesture_Control.DetectGesture;
import com.kelompok9.musicplayer.Model.SaveFavouriteList;
import com.kelompok9.musicplayer.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;


public class NowPlayingActivity extends AppCompatActivity {

    ImageView albumArt;
    TextView songName, songArtist, currentDuration,totalDuration, songAlbum;
    Button btnPlay,btnPrev,btnNext,btnPause,repeatOne,repeatAll,shuffle,fav,notFav;
    ImageView back, share;
    SeekBar seekBar;
    int id=-1;

    GestureDetectorCompat gestureDetectorCompat;

    View rootView;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.music_player_layout);
        back = findViewById(R.id.btn_clear);
        albumArt = findViewById(R.id.now_playing_album_art);
        songName = findViewById(R.id.now_playing_song_name);
        songArtist = findViewById(R.id.now_playing_song_artist);
        btnPrev = findViewById(R.id.now_playing_btn_previous);
        btnPlay = findViewById(R.id.now_playing_btn_play);
        btnPause = findViewById(R.id.now_playing_btn_pause);
        btnNext = findViewById(R.id.now_playing_btn_next);
        seekBar = findViewById(R.id.now_playing_seekbar);
        repeatOne = findViewById(R.id.now_playing_btn_repeat_one);
        repeatAll = findViewById(R.id.now_playing_btn_repeat_all);
        shuffle = findViewById(R.id.now_playing_btn_shuffle);
        fav = findViewById(R.id.now_playing_btn_fav);
        notFav = findViewById(R.id.now_playing_btn_not_fav);
        currentDuration = findViewById(R.id.current_duration_);
        totalDuration = findViewById(R.id.total_duration_);
        songAlbum = findViewById(R.id.song_album);
        share = findViewById(R.id.now_playing_share);
        setView();

        activity = this;
        if(MainActivity.mediaPlayer!=null && !MainActivity.mediaPlayer.isPlaying())
        {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setSeekBar();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlayFunc();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPauseFunc();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNextFunc();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPrevFunc();
            }
        });

        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int flag = prefs.getInt("onCompletion",-1);
        if(flag == 1) //in shuffle mode
        {
            repeatAll.setVisibility(View.GONE);
            repeatOne.setVisibility(View.VISIBLE);
            shuffle.setVisibility(View.GONE);
        }
        else if(flag == 0) //
        {
            repeatAll.setVisibility(View.VISIBLE);
            repeatOne.setVisibility(View.GONE);
            shuffle.setVisibility(View.GONE);
        }
        else if(flag == 2)
        {
            repeatAll.setVisibility(View.GONE);
            repeatOne.setVisibility(View.GONE);
            shuffle.setVisibility(View.VISIBLE);

        }

        final CoordinatorLayout coordinatorLayout = findViewById(R.id.m_layout);
        repeatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("onCompletion",2).apply();

                repeatAll.setVisibility(View.GONE);
                shuffle.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Repeat All is set", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("onCompletion",1).apply();
                shuffle.setVisibility(View.GONE);
                repeatOne.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Shuffle is set", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        repeatOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("onCompletion",0).apply();
                repeatOne.setVisibility(View.GONE);
                repeatAll.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Repeat One is set \u2713", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        setFavIcon();


        notFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavourite();
                notFav.setVisibility(View.GONE);
                fav.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Added to My favourite \u2713", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unSetFavourite();
                fav.setVisibility(View.GONE);
                notFav.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Removed from My favourite \u2713", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });


         DetectGesture detectGesture = new DetectGesture();
         detectGesture.setMainActivity(this);
         gestureDetectorCompat = new GestureDetectorCompat(this, detectGesture);

         rootView = getWindow().getDecorView().findViewById(R.id.m_layout);
         share.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                  share();
             }
         });

    }

    public void share()
    {
        Bitmap bitmap = getScreenShot(rootView);
        store(bitmap);

    }

    public void store(Bitmap bitmap)
    {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/BeatsMusicPlayer";
        File dir = new File(dirPath);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        File file = new File(dirPath,MainActivity.PresentArrayList.get(MainActivity.position).getTrack()+".PNG");
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        }catch (Exception e){}

        shareImage(file);

    }

    public void shareImage(File file)
    {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Sharing");
        intent.putExtra(Intent.EXTRA_TEXT,"Hi, I am listening "+MainActivity.PresentArrayList.get(MainActivity.position).getTrack()+" with #Beats Music Player.\nhttp://play.google.com/store/apps/details?id=io.sample.musicism");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try{
            startActivity(Intent.createChooser(intent,"Share"));

        }catch (Exception e){}
    }


    public Bitmap getScreenShot(View view)
    {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    public void setFavIcon()
    {

        //set not fav or fav

        ArrayList<String> favList;
        SaveFavouriteList saveFavouriteList;
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("favourote_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = nPrefs.getString("favouriteSong", "");
        saveFavouriteList = gson1.fromJson(json1, SaveFavouriteList.class);

        if(saveFavouriteList == null)
        {
            favList = new ArrayList<>();
        }
        else
        {
            favList = saveFavouriteList.getList();
        }



        if(favList.size()!=0 && favList.contains(MainActivity.PresentArrayList.get(MainActivity.position).getTrack()))
        {
            notFav.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
        }
        else
        {
            notFav.setVisibility(View.VISIBLE);
            fav.setVisibility(View.GONE);
        }


    }



    public void setFavourite()
    {
        ArrayList<String> favList;
        SaveFavouriteList saveFavouriteList;
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("favourote_pref",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = nPrefs.getString("favouriteSong", "");
        saveFavouriteList = gson.fromJson(json, SaveFavouriteList.class);
        if(saveFavouriteList==null)
        {
            favList = new ArrayList<>();
        }
        else
        {
            favList = saveFavouriteList.getList();
        }

        favList.add(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
        SaveFavouriteList saveFavouriteList1 = new SaveFavouriteList(favList);
        SharedPreferences.Editor editor1 = nPrefs.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(saveFavouriteList1);
        editor1.putString("favouriteSong", json1);
        editor1.apply();


    }


    public int mappIndex()
    {

            for(int i=0;i<MainActivity.AudioArrayList.size();i++)
            {
                if(MainActivity.PresentArrayList.get(MainActivity.position).getTrack() == MainActivity.AudioArrayList.get(i).getTrack())
                {
                    return i;
                }
            }

         return -1;
    }


    public void unSetFavourite()
    {
        ArrayList<String> favList;
        SaveFavouriteList saveFavouriteList;
        SharedPreferences nPrefs= MainActivity.activity.getSharedPreferences("favourote_pref",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = nPrefs.getString("favouriteSong", "");
        saveFavouriteList = gson1.fromJson(json1, SaveFavouriteList.class);

        if(saveFavouriteList == null)
        {
            favList = new ArrayList<>();
        }
        else
        {
            favList = saveFavouriteList.getList();
        }


        if(favList.size()!=0 && favList.contains(MainActivity.PresentArrayList.get(MainActivity.position).getTrack()))
        {
            favList.remove(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
        }

        saveFavouriteList = new SaveFavouriteList(favList);
        SharedPreferences.Editor prefsEditor = nPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(saveFavouriteList);
        prefsEditor.putString("favouriteSong", json);
        prefsEditor.apply();




    }


    public void setView()
    {



        Glide.with(this)
                .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_album_black_24dp)
                .dontTransform()
                .into(albumArt);
        songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
        songName.setSelected(true);
        songName.setSingleLine();
        songArtist.setText(MainActivity.PresentArrayList.get(MainActivity.position).getArtist());
        if(MainActivity.mediaPlayer.isPlaying())
        {
            btnPlay.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
        }

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onCompletionMediaPlayer();
            }
        });

        songAlbum.setText(MainActivity.PresentArrayList.get(MainActivity.position).getAlbum());


    }
    public void btnNextFunc()
    {

        if(MainActivity.position < MainActivity.PresentArrayList.size()-1)
        {
            MainActivity.position++;
        }
        else
        {
            MainActivity.position = 0;
        }


        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(MainActivity.PresentArrayList.get(MainActivity.position).getData()));
        MainActivity.mediaPlayer.start();
        setView();
        setSeekBar();
    }
    public void btnPrevFunc()
    {
        if(MainActivity.position > 0)
        {
            MainActivity.position--;
        }
        else
        {
            MainActivity.position = MainActivity.PresentArrayList.size()-1;
        }
        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(MainActivity.PresentArrayList.get(MainActivity.position).getData()));
        MainActivity.mediaPlayer.start();
        setView();
        setSeekBar();
    }
    public void onCompletionMediaPlayer()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int flag = prefs.getInt("onCompletion",-1);

        if(flag == 0)  //repeat one
        {
             btnPlayFunc();
             setSeekBar();

        }
        else if(flag == 2) //repeat all
        {
            btnNextFunc();

        }
        else if(flag == 1) //shuffle all
        {
            Random random = new Random();
            MainActivity.position = random.nextInt(MainActivity.PresentArrayList.size());
            setView();

            MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(MainActivity.PresentArrayList.get(MainActivity.position).getData()));
            MainActivity.mediaPlayer.start();
            setSeekBar();

        }
        else
        {
            btnPlayFunc();
            setSeekBar();
        }


    }

    public void btnPlayFunc()
    {
        MainActivity.mediaPlayer.start();
        btnPlay.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);
        seekBar.setMax(0);
        seekBar.setMax(MainActivity.PresentArrayList.get(MainActivity.position).getDuration());
    }
    public void btnPauseFunc()
    {
        MainActivity.mediaPlayer.pause();
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    public void setSeekBar()
    {

         final int duration = MainActivity.PresentArrayList.get(MainActivity.position).getDuration();
         long m = (duration/1000)/60;
         long s = (int)((duration/1000)%60);
         totalDuration.setText(m+":"+s);
         seekBar.setMax(duration);
         final Handler handler = new Handler();
         new Thread(){

             @Override
             public void run() {

                 int currentPosition = 0;
                 while(currentPosition<duration)
                 {
                     try {
                         sleep(500);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     if(MainActivity.mediaPlayer!=null) {
                         currentPosition = MainActivity.mediaPlayer.getCurrentPosition();
                         seekBar.setProgress(currentPosition);
                         seekBar.refreshDrawableState();

                         final int finalCurrentPosition = currentPosition;
                         handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 long m = (finalCurrentPosition/1000)/60;
                                 long s = (int)((finalCurrentPosition/1000)%60);
                                 currentDuration.setText(m+":"+s);
                             }
                         });
                     }
                 }
             }
         }.start();

       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

               MainActivity.mediaPlayer.seekTo(seekBar.getProgress());
           }
       });

        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onCompletionMediaPlayer();
            }
        });

    }




    public void setPrevBottomPlayer() {
        if (id == 0) {
            Picasso.with(getApplicationContext())
                    .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                    .into(MainActivity.albumArt);
            MainActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
            if (!MainActivity.mediaPlayer.isPlaying()) {
                MainActivity.play.setVisibility(View.VISIBLE);
                MainActivity.pause.setVisibility(View.GONE);

            } else {
                MainActivity.play.setVisibility(View.GONE);
                MainActivity.pause.setVisibility(View.VISIBLE);
            }
           try {
               MainActivity.controlMedia();
           }catch (Exception e){}



        } else if (id == 1) {
            Picasso.with(getApplicationContext())
                    .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                    .into(AlbumsActivity.albumArt);
            AlbumsActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
            if (!MainActivity.mediaPlayer.isPlaying()) {
                AlbumsActivity.play.setVisibility(View.VISIBLE);
                AlbumsActivity.pause.setVisibility(View.GONE);

            } else {
                AlbumsActivity.play.setVisibility(View.GONE);
                AlbumsActivity.pause.setVisibility(View.VISIBLE);
            }
            try {
                AlbumsActivity.controlMedia();
            }catch (Exception e){}


        }
        else if(id == 2)
        {
            Picasso.with(getApplicationContext())
                    .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                    .into(ArtistsActivity.albumArt);
            ArtistsActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
            if (!MainActivity.mediaPlayer.isPlaying()) {
                ArtistsActivity.play.setVisibility(View.VISIBLE);
                ArtistsActivity.pause.setVisibility(View.GONE);

            } else {
                ArtistsActivity.play.setVisibility(View.GONE);
                ArtistsActivity.pause.setVisibility(View.VISIBLE);
            }
            try {
                ArtistsActivity.controlMedia();
            }catch (Exception e){}


        }
        else if(id == 3)
        {
            Picasso.with(getApplicationContext())
                    .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                    .into(PlayListActivity.albumArt);
            PlayListActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
            if (!MainActivity.mediaPlayer.isPlaying()) {
                PlayListActivity.play.setVisibility(View.VISIBLE);
                PlayListActivity.pause.setVisibility(View.GONE);

            } else {
                PlayListActivity.play.setVisibility(View.GONE);
                PlayListActivity.pause.setVisibility(View.VISIBLE);
            }
            try {
                PlayListActivity.controlMedia();
            }catch (Exception e){}


        }
        else if(id == 4)
        {
            Picasso.with(getApplicationContext())
                    .load(MainActivity.PresentArrayList.get(MainActivity.position).getAlbumArtUri())
                    .into(FavouriteActivity.albumArt);
            FavouriteActivity.songName.setText(MainActivity.PresentArrayList.get(MainActivity.position).getTrack());
            if (!MainActivity.mediaPlayer.isPlaying()) {
                FavouriteActivity.play.setVisibility(View.VISIBLE);
                FavouriteActivity.pause.setVisibility(View.GONE);

            } else {
                FavouriteActivity.play.setVisibility(View.GONE);
                FavouriteActivity.pause.setVisibility(View.VISIBLE);
            }
            try {
                FavouriteActivity.controlMedia();
            }catch (Exception e){}


        }



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setPrevBottomPlayer();


    }





}
