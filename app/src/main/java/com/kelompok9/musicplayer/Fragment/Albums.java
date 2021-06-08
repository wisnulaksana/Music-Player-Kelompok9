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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Activity.AlbumsActivity;
import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.R;

import static com.kelompok9.musicplayer.Activity.MainActivity.AlbumArrayList;


public class Albums extends Fragment {

  public Albums(){}




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_albums, container, false);

        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorDark));
        }

        GridView gridView = view.findViewById(R.id.grid_view);
        Myadapter myadapter = new Myadapter();
        gridView.setNumColumns(2);
        gridView.setAdapter(myadapter);

        ViewCompat.setNestedScrollingEnabled(gridView, true);



        return view;
    }

    class Myadapter extends BaseAdapter {

        class ViewHolder{
            TextView albumName;
            ImageView albumImage;
            TextView songsCount;


        }


        @Override
        public int getCount() {
            return AlbumArrayList.size();
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

            if(AlbumArrayList.get(i).size()!=0) {

                ViewHolder viewHolder;
                if(view==null)
                {
                    view = getLayoutInflater().inflate(R.layout.albums_item, viewGroup, false);
                    viewHolder = new ViewHolder();
                    viewHolder.albumImage = view.findViewById(R.id.album_image);
                    viewHolder.albumName = view.findViewById(R.id.album_name);;
                    viewHolder.songsCount =  view.findViewById(R.id.album_songs_count);
                    view.setTag(viewHolder);
                }
                else
                {
                    viewHolder = (ViewHolder) view.getTag();
                }


                viewHolder.songsCount.setText(" " + AlbumArrayList.get(i).size() + " Songs");
                viewHolder.albumName.setText(AlbumArrayList.get(i).get(0).getAlbum());

                Glide.with(view.getContext())
                        .load(AlbumArrayList.get(i).get(0).getAlbumArtUri())
                        .error(R.drawable.ic_album_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform()
                        .into(viewHolder.albumImage);



                viewHolder.albumImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), AlbumsActivity.class);
                        intent.putExtra("index", i);
                        // Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.right_to_left, R.anim.left_to_right).toBundle();
                        startActivity(intent);


                    }
                });


            }

                return view;


        }
    }

}