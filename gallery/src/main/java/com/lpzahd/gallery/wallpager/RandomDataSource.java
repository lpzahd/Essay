package com.lpzahd.gallery.wallpager;

import android.content.Context;
import android.graphics.Bitmap;

import com.lpzahd.gallery.a3d.UriTexture;
import com.lpzahd.gallery.a3d.Util;
import com.lpzahd.gallery.cache.CacheService;
import com.lpzahd.gallery.cache.ImageList;

import java.io.IOException;
import java.net.URISyntaxException;

public class RandomDataSource implements Slideshow.DataSource {

    public Bitmap getBitmapForIndex(Context context, int currentSlideshowCounter) {
        ImageList list = CacheService.getImageList(context);
        // Once we have the id and the thumbid, we can return a bitmap
        // First we select a random numbers
        if (list.ids == null)
            return null;
        double random = Math.random();
        random *= list.ids.length;
        int index = (int) random;
        long cacheId = list.thumbids[index];
        final String uri = CacheService.BASE_CONTENT_STRING_IMAGES + list.ids[index];
        Bitmap retVal = null;
        try {
            retVal = UriTexture.createFromUri(context, uri, UriTexture.MAX_RESOLUTION, UriTexture.MAX_RESOLUTION, cacheId);
            if (retVal != null) {
                retVal = Util.rotate(retVal, list.orientation[index]);
            }
        } catch (OutOfMemoryError e) {
            ;
        } catch (IOException e) {
            ;
        } catch (URISyntaxException e) {
            ;
        }
        return retVal;
    }

}
