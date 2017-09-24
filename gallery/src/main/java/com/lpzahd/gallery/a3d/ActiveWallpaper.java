package com.lpzahd.gallery.a3d;

import android.app.Activity;
import android.os.Bundle;

import com.lpzahd.gallery.wallpager.RandomDataSource;
import com.lpzahd.gallery.wallpager.Slideshow;

public class ActiveWallpaper extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slideshow slideshow = new Slideshow(this);
        slideshow.setDataSource(new RandomDataSource());
        setContentView(slideshow);
    }
}
