package com.kelompok9.musicplayer.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Fragment.Albums;
import com.kelompok9.musicplayer.Fragment.Artist;
import com.kelompok9.musicplayer.Fragment.PlayLists;
import com.kelompok9.musicplayer.Fragment.Songs;
import com.kelompok9.musicplayer.Model.AudioListModel;
import com.kelompok9.musicplayer.Model.SavePlayListSong;
import com.kelompok9.musicplayer.Permission.PermissionListener;
import com.kelompok9.musicplayer.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Songs.OnSongsClickLitchener,NavigationView.OnNavigationItemSelectedListener {

    private static final int request_permision=1;

    public static ArrayList<AudioListModel> AudioArrayList,AlbumSubArrayList,PresentArrayList;
    public static ArrayList<ArrayList<AudioListModel>> AlbumArrayList;
    public static ArrayList<ArrayList<AudioListModel>> ArtistArrayList;
    public static MediaPlayer mediaPlayer=null;
    public static int position=-1;
    public static MaterialCardView relativeLayout;
    public static Button play,pause;
    public static ImageView albumArt;
    public static TextView songName;
    public static ProgressBar progressBar;
    public static Activity activity;
    public static Button clear;
    public static SharedPreferences prefs;
    TextView songsCount, albumsCount, artistsCount;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PermissionListener permissionListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;


        permissionListener = new PermissionListener(this);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(permissionListener)
                .check();



    }

    public void ondenied()
    {
        finish();
    }
    public void onAllowed()
    {
        doStuff();
    }

    public void showPermissionRationale(final PermissionToken token)
    {

        new AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage("We need your Permission")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        token.continuePermissionRequest();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        token.cancelPermissionRequest();
                        dialogInterface.dismiss();
                    }

                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        token.cancelPermissionRequest();
                    }
                }).show();
    }





    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doStuff(){

        AudioArrayList = new ArrayList<>();
        AlbumArrayList = new ArrayList<>();
        ArtistArrayList = new ArrayList<>();

        getMusic();

        //setting Main Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        // Setting ViewPager for each Tabs
        viewPager = findViewById(R.id.viewpager_);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_main);




        //Setting drwaer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout_);
        NavigationView navigationView = findViewById(R.id.nav_view);
////        LinearLayout navHeader = findViewById(R.id.nav_header_layout);
//        navHeader.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.toolbar_color));
        //drawer.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.colorDark));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(0).setChecked(true);
        songsCount = header.findViewById(R.id.songs_count);
        albumsCount = header.findViewById(R.id.albums_count);
        artistsCount = header.findViewById(R.id.artists_count);
        songsCount.setText(AudioArrayList.size()+"\nSongs");
        albumsCount.setText(AlbumArrayList.size()+"\nAlbum");
        artistsCount.setText(ArtistArrayList.size()+"\nArtist");


        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
            header.setBackgroundColor(getResources().getColor(R.color.toolbar_color));

        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            if (mediaPlayer != null) {
                setReference();
                controlMedia();
            } else {


                if (prefs.getInt("flag", -1) == 0) {

                    if (prefs.getInt("position", -1) != -1) {

                        if (prefs.getInt("which", -1) == 0) {

                            //from Songs
                            PresentArrayList = AudioArrayList;

                        } else if (prefs.getInt("which", -1) == 1) {

                            //from Albums
                            PresentArrayList = AlbumArrayList.get(prefs.getInt("albumIndex", -1));
                        } else if (prefs.getInt("which", -1) == 2) {

                            //from Artist
                            PresentArrayList = AlbumArrayList.get(prefs.getInt("artistIndex", -1));
                        } else if (prefs.getInt("which", -1) == 3) {

                            //from playList
                            SharedPreferences nPrefs = MainActivity.activity.getPreferences(MODE_PRIVATE);
                            Gson gson1 = new Gson();
                            String json1 = nPrefs.getString("playlistSong", "");
                            SavePlayListSong savePlayListSong;
                            ArrayList<ArrayList<Long>> playListSongList;
                            ArrayList<Long> playlist;
                            savePlayListSong = gson1.fromJson(json1, SavePlayListSong.class);

                            if (savePlayListSong != null) {
                                playListSongList = savePlayListSong.getPlayListSong();
                                playlist = playListSongList.get(prefs.getInt("playlistIndex", -1));
                                PresentArrayList = null;
                                PresentArrayList = new ArrayList<>();



                            }


                        } else if (prefs.getInt("which", -1) == 4) {

                            //from favourite
                            ArrayList<String> favList;
                            SharedPreferences nPrefs = getPreferences(MODE_PRIVATE);
                            Gson gson2 = new Gson();
                            String json2 = nPrefs.getString("favouriteSong", "");
                            Type type = new TypeToken<ArrayList<String>>(){}.getType();
                            favList = gson2.fromJson(json2, type);

                            PresentArrayList = null;
                            PresentArrayList = new ArrayList<>();

                            for (int i = 0; i < favList.size(); i++) {

                                for(int j=0;j< AudioArrayList.size();j++)
                                {
                                    if(favList.get(i) == AudioArrayList.get(j).getTrack())
                                    {
                                        PresentArrayList.add(AudioArrayList.get(j));
                                        break;
                                    }
                                }


                            }


                        }

                        position = prefs.getInt("position", -1);

                        mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), Uri.parse(PresentArrayList.get(position).getData()));
                        mediaPlayer.setLooping(false);
                        setReference();
                        controlMedia();
                    }
                }
            }

        }catch (Exception e){}



    }



    public void getMusic()
    {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION };
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = getContentResolver().query(uri,
                cursor_cols, where, null, MediaStore.Audio.Media.TITLE);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            Long id = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));


            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);


            AudioListModel audioListModel = new AudioListModel();
            audioListModel.setArtist(artist);
            //audioListModel.setArtistId(artistId);
            audioListModel.setAlbum(album);
            audioListModel.setTrack(track);
            audioListModel.setData(data);
            audioListModel.setAlbumId(albumId);
            audioListModel.setDuration(duration);
            audioListModel.setAlbumArtUri(albumArtUri);
            audioListModel.setId(id);

            AudioArrayList.add(audioListModel);




        }

        //adding AlbumList of each album

        int a[]= new int[AudioArrayList.size()];
        for(int i=0;i< AudioArrayList.size()-1;i++)
        {
            if(a[i]!=1)
            {
                AlbumSubArrayList = new ArrayList<>();

                AlbumSubArrayList.add(AudioArrayList.get(i));

                for(int j=i+1;j<AudioArrayList.size();j++)
                {
                    if(AudioArrayList.get(i).getAlbumId().equals(AudioArrayList.get(j).getAlbumId()))
                    {
                        AlbumSubArrayList.add(AudioArrayList.get(j));
                        a[j]=1;
                    }
                }

                AlbumArrayList.add(AlbumSubArrayList);
                a[i]=1;

            }
        }
        AlbumSubArrayList=null;

        //Adding Artist list of each Artist

        int b[]= new int[AudioArrayList.size()];
        for(int i=0;i< AudioArrayList.size()-1;i++)
        {
            if(b[i]!=1)
            {
                AlbumSubArrayList = new ArrayList<>();

                AlbumSubArrayList.add(AudioArrayList.get(i));

                for(int j=i+1;j<AudioArrayList.size();j++)
                {
                    if(AudioArrayList.get(i).getArtist().equals(AudioArrayList.get(j).getArtist()))
                    {
                        AlbumSubArrayList.add(AudioArrayList.get(j));
                        b[j]=1;
                    }
                }

                ArtistArrayList.add(AlbumSubArrayList);
                b[i]=1;

            }
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

      if(item.getItemId() == R.id.exit_id)
        {
            onBackPressed();
        }

        return true;



    }


    public void showDialog()
    {
        Dialog dialog = new Dialog(MainActivity.activity);
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
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark")) {
            materialCardView.setBackgroundColor(getResources().getColor(R.color.colorDark));
            textView.setTextColor(getResources().getColor(R.color.darkgrey));
        }



        dialog.show();
        dialog.getWindow().setAttributes(wm);

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new Songs(this), "Songs");
        adapter.addFragment(new Albums(), "Albums");
        adapter.addFragment(new Artist(), "Artists");
        adapter.addFragment(new PlayLists(), "Play Lists");
        viewPager.setAdapter(adapter);




    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public  void onSongSelected(final int pos) throws Exception {
        position = pos;
        if(PresentArrayList!=null)
        {
            PresentArrayList=null;

        }
        PresentArrayList = AudioArrayList;
        //Toast.makeText(context, ""+PresentArrayList.size(), Toast.LENGTH_SHORT).show();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("position",position).commit();
        prefs.edit().putInt("which",0).commit();



        playSong();
        setReference();

        controlMedia();







    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    public static void setReference()
    {
        relativeLayout = activity.findViewById(R.id.bottom_now_playing);
        if(relativeLayout.getVisibility() != View.VISIBLE)
        {
            relativeLayout.setVisibility(View.VISIBLE);

        }
        CoordinatorLayout relativeLayout1 = relativeLayout.findViewById(R.id.bottom_now_playing_layout);
        albumArt = activity.findViewById(R.id.bottom_now_playing_album_art);
        songName = activity.findViewById(R.id.bottom_now_playing_song_name);
        play = activity.findViewById(R.id.bottom_now_playing_song_play);
        pause = activity.findViewById(R.id.bottom_now_playing_song_pause);
        clear = activity.findViewById(R.id.bottom_now_playing_song_clear);
        progressBar = activity.findViewById(R.id.bottom_now_playing_progressbar);
        songName.setSingleLine();
        songName.setSelected(true);

        Glide.with(activity)
                .load(PresentArrayList.get(position).getAlbumArtUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .into(albumArt);

        songName.setText(PresentArrayList.get(position).getTrack()+"");
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);


        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            relativeLayout1.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.toolbar_color));
            songName.setTextColor(MainActivity.activity.getResources().getColor(R.color.colorLight));
            progressBar.setProgressTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            play.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            pause.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            clear.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
        }

    }




    public static void playSong() throws Exception
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(activity.getApplicationContext(),Uri.parse(PresentArrayList.get(position).getData()));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
        else
        {
            //Toast.makeText(activity, "now playing", Toast.LENGTH_SHORT).show();
            mediaPlayer = MediaPlayer.create(activity.getApplicationContext(),Uri.parse(PresentArrayList.get(position).getData()));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    }


    public static void controlMedia() throws Exception
    {
        if(mediaPlayer.isPlaying())
        {
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
                position = -1;
                relativeLayout.setVisibility(View.GONE);

                prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                prefs.edit().putInt("flag",1).apply();


            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity.getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("id",0);
                activity.startActivity(intent);



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
                    if(mediaPlayer!=null)
                    {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if(id == R.id.nav_theme)
        {
            showThemeSelect();
        }

        //item.setIconTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.darkgrey));

        DrawerLayout drawer = findViewById(R.id.drawer_layout_);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showThemeSelect()
    {
        final Dialog dialog = new Dialog(MainActivity.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_theme);
        dialog.setCancelable(true);
        dialog.onBackPressed();
        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();

        try {
            wm.copyFrom(dialog.getWindow().getAttributes());
            wm.width = WindowManager.LayoutParams.MATCH_PARENT;
            wm.height = WindowManager.LayoutParams.WRAP_CONTENT;

        }catch(NullPointerException e){
            System.out.println(e.toString());
            Toast.makeText(MainActivity.activity,"Null Pointer Exception", Toast.LENGTH_LONG).show();
        }


        LinearLayout linearLayout = dialog.findViewById(R.id.theme_layout);
        TextView select_theme_text = dialog.findViewById(R.id.select_theme);
        RadioGroup select_radio = dialog.findViewById(R.id.radio_select);
        RadioButton light = dialog.findViewById(R.id.radio_light);
        RadioButton dark = dialog.findViewById(R.id.radio_dark);

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        final SharedPreferences.Editor editor = themePref.edit();
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            dark.setChecked(true);
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorDark));
            select_theme_text.setTextColor(getResources().getColor(R.color.colorLight));
            light.setTextColor(getResources().getColor(R.color.colorLight));
            dark.setTextColor(getResources().getColor(R.color.colorLight));

        }
        else
        {
            light.setChecked(true);
        }





        select_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radio_light)
                {
                    editor.putString("theme","light");
                    editor.commit();
                    dialog.dismiss();
                    onBackPressed();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
                else
                {
                    editor.putString("theme","dark");
                    editor.commit();
                    dialog.dismiss();
                    onBackPressed();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }
        });





        dialog.show();
        dialog.getWindow().setAttributes(wm);

    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(mediaPlayer!=null)
        {
            prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            prefs.edit().putInt("flag",0).apply();
        }


    }



}

