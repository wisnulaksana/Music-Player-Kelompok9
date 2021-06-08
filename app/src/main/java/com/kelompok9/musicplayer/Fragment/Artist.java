package com.kelompok9.musicplayer.Fragment;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Activity.ArtistsActivity;
import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kelompok9.musicplayer.Activity.MainActivity.AlbumArrayList;
import static com.kelompok9.musicplayer.Activity.MainActivity.ArtistArrayList;


public class Artist extends Fragment {



    public Artist(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_artist, container, false);

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorDark));
        }

        ListView listView = view.findViewById(R.id.artist_listview);
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        ViewCompat.setNestedScrollingEnabled(listView, true);



        return view;
    }

    class MyAdapter extends BaseAdapter{


        class ViewHolder {
            public TextView artist;
            public TextView songsCount;
            public CircleImageView albumImage;
        }

        @Override
        public int getCount() {
            return ArtistArrayList.size();
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

            ViewHolder viewHolder;
            if(view==null)
            {
                view = getLayoutInflater().inflate(R.layout.artists_items,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.albumImage = view.findViewById(R.id.artist_image);
                viewHolder.artist = view.findViewById(R.id.artist_name);
                viewHolder.songsCount = view.findViewById(R.id.songs_count);
                view.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)view.getTag();
            }


             if(ArtistArrayList.get(i).size()!=0)
             {
                 viewHolder.songsCount.setText(" "+ArtistArrayList.get(i).size()+" Songs");
                 viewHolder.artist.setText(ArtistArrayList.get(i).get(0).getArtist());


                 Glide.with(view.getContext())
                         .load(AlbumArrayList.get(i).get(0).getAlbumArtUri())
                         .error(R.color.blankImageColor)
                         .diskCacheStrategy(DiskCacheStrategy.ALL)
                         .dontTransform()
                         .into(viewHolder.albumImage);


                 view.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent intent = new Intent(view.getContext(), ArtistsActivity.class);
                         intent.putExtra("index", i);
                         //Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.right_to_left, R.anim.left_to_right).toBundle();
                         startActivity(intent);
                     }

                     });
             }

            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);
            if(theme!=null && theme.equals("dark"))
            {
                view.setBackgroundColor(getResources().getColor(R.color.colorDark));
                viewHolder.artist.setTextColor(getResources().getColor(R.color.colorLight));
                viewHolder.songsCount.setTextColor(getResources().getColor(R.color.darkgrey));
            }




            return view;
        }

    }

}