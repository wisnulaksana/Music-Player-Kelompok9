package com.kelompok9.musicplayer.Model;

import android.graphics.Bitmap;
import android.net.Uri;

public class AudioListModel {

    String artist,album,Data,track;
    Bitmap bitmap;
    Long albumId,artistId,id;
    int duration;
    Uri albumArtUri;

    public AudioListModel(){}

    public void setId(Long id){this.id=id;}
    public void setArtist(String artist){
        this.artist=artist;
    }
    public void  setArtistId(Long artistId){
        this.artistId=artistId;
    }
    public void setTrack(String track){
        this.track=track;
    }
    public void setAlbum(String album){
        this.album = album;
    }
    public void setData(String Data){
        this.Data = Data;
    }
    public void setAlbumId(Long albumId){
        this.albumId=albumId;
    }
    public void setDuration(int duration){
        this.duration=duration;
    }
    public void setAlbumArtUri(Uri albumArtUri){
        this.albumArtUri=albumArtUri;
    }


    public Long getId(){return id;}
    public String getArtist(){
        return artist;
    }
    public Long  getArtistId(){
        return artistId;
    }
    public String getTrack(){
        return track;
    }
    public String getAlbum(){
        return album;
    }
    public String getData(){
        return Data;
    }
    public Long getAlbumId(){
        return albumId;
    }
    public int getDuration(){
        return duration;
    }
    public Uri getAlbumArtUri(){
        return albumArtUri;
    }



}
