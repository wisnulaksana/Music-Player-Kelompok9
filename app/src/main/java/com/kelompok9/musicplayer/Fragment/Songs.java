package com.kelompok9.musicplayer.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.BottomSheet.SongInfoBottomSheet;
import com.kelompok9.musicplayer.R;

import static com.kelompok9.musicplayer.Activity.MainActivity.AudioArrayList;


public class Songs extends Fragment {


      Context context;
    public Songs(){}

    @SuppressLint("ValidFragment")
    public Songs(Context context )
    {
            this.context = context;
    }

    OnSongsClickLitchener mCallBack;
    public interface OnSongsClickLitchener{
        public void onSongSelected(int position) throws Exception;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (OnSongsClickLitchener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"Must implements interface");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        View view;
        view = inflater.inflate(R.layout.fragment_songs, container, false);


        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String theme = themePref.getString("theme",null);
        if(theme!=null && theme.equals("dark"))
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorDark));
        }

         ListView listView = view.findViewById(R.id.list_songs);
         Myadapter myadapter = new Myadapter();
         listView.setAdapter(myadapter);

         //explitely defining the listView nestedScrollable
         ViewCompat.setNestedScrollingEnabled(listView, true);

        return view;
    }

    class Myadapter extends BaseAdapter{


         class ViewHolder {
            public TextView textView;
            public TextView artist;
            public ImageView albumImage;
            public LinearLayout info;
        }


        @Override
        public int getCount() {
            return AudioArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             ViewHolder viewHolder;
             if(view == null)
             {
                 view = inflater.inflate(R.layout.songs_item,viewGroup,false);
                 viewHolder = new ViewHolder();
                 viewHolder.textView = view.findViewById(R.id.songs_name);
                 viewHolder.artist = view.findViewById(R.id.songs_artist);
                 viewHolder.albumImage = view.findViewById(R.id.album_art);
                 viewHolder.info = view.findViewById(R.id.songs_info);;
                 view.setTag(viewHolder);
             }
             else
             {
                 viewHolder = (ViewHolder) view.getTag();
             }


            viewHolder.textView.setText(AudioArrayList.get(i).getTrack());
            viewHolder.artist.setText(AudioArrayList.get(i).getArtist());




            Glide.with(view.getContext())
                    .load(AudioArrayList.get(i).getAlbumArtUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .into(viewHolder.albumImage);



            SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
            String theme = themePref.getString("theme",null);
            if(theme!=null && theme.equals("dark"))
            {
               view.setBackgroundColor(getResources().getColor(R.color.colorDark));
               viewHolder.textView.setTextColor(getResources().getColor(R.color.colorLight));
                viewHolder.artist.setTextColor(getResources().getColor(R.color.darkgrey));
                viewHolder.info.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.activity,R.color.colorLight));
            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        mCallBack.onSongSelected(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //NotificationGenerator.customBigNotification(view.getContext(),AudioArrayList.get(i));




                }
            });
            viewHolder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SongInfoBottomSheet songInfoBottomSheet = new SongInfoBottomSheet(i);
                    songInfoBottomSheet.show(getFragmentManager(),"fragment");
                }
            });





            return view;
        }
    }



}