package com.kelompok9.musicplayer.Permission;

import com.kelompok9.musicplayer.Activity.MainActivity;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;


public class PermissionListener implements com.karumi.dexter.listener.single.PermissionListener {

    MainActivity mainActivity;

    public PermissionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        mainActivity.onAllowed();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
             mainActivity.ondenied();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
             mainActivity.showPermissionRationale(token);
    }
}
