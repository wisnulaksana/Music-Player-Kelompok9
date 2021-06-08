package com.kelompok9.musicplayer.Gesture_Control;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.Activity.NowPlayingActivity;


public class DetectGesture extends GestureDetector.SimpleOnGestureListener {

    private final static int MIN_SWIPE_X = 100;
    private final static int MIN_SWIPE_Y = 100;
    private final static int MAX_SWIPE_X = 1000;
    private final static int MAX_SWIPE_Y = 1000;


    NowPlayingActivity mainActivity;

    public NowPlayingActivity getMainActivity()
    {
        return mainActivity;
    }

    public void setMainActivity(NowPlayingActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();

        float absdeltaX = Math.abs(deltaX);
        float absdeltaY = Math.abs(deltaY);
        if(absdeltaX >= MIN_SWIPE_X && absdeltaX <= MAX_SWIPE_X)
        {
            if(deltaX > 0)
            {
                //swipe to left
                mainActivity.btnNextFunc();
                mainActivity.setFavIcon();
            }
            else
            {
                //swipde to right
                mainActivity.btnPrevFunc();
                mainActivity.setFavIcon();
            }
        }

        if(absdeltaY >= MIN_SWIPE_Y && absdeltaY <= MAX_SWIPE_Y)
        {
            if(deltaY > 0)
            {
                //swipe to up

            }
            else
            {
                //swipde to down

            }
        }

        return  true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        if(MainActivity.mediaPlayer!=null && MainActivity.mediaPlayer.isPlaying())
        {
            mainActivity.btnPauseFunc();
        }
        else if(MainActivity.mediaPlayer!=null && !MainActivity.mediaPlayer.isPlaying())
        {
            mainActivity.btnPlayFunc();
        }
        return true;

    }
}
