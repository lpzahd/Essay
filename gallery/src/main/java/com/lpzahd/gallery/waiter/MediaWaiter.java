package com.lpzahd.gallery.waiter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.lpzahd.gallery.R;
import com.lpzahd.gallery.a3d.ConcatenatedDataSource;
import com.lpzahd.gallery.a3d.CropImage;
import com.lpzahd.gallery.a3d.DataSource;
import com.lpzahd.gallery.a3d.GridLayer;
import com.lpzahd.gallery.a3d.GridLayoutInterface;
import com.lpzahd.gallery.a3d.ImageManager;
import com.lpzahd.gallery.a3d.LocalDataSource;
import com.lpzahd.gallery.a3d.MediaFeed;
import com.lpzahd.gallery.a3d.MediaItem;
import com.lpzahd.gallery.a3d.MediaSet;
import com.lpzahd.gallery.a3d.PicasaDataSource;
import com.lpzahd.gallery.a3d.RenderView;
import com.lpzahd.gallery.a3d.ReverseGeocoder;
import com.lpzahd.gallery.a3d.SingleDataSource;
import com.lpzahd.gallery.a3d.UriTexture;
import com.lpzahd.gallery.a3d.Utils;
import com.lpzahd.gallery.cache.CacheService;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.wallpager.RandomDataSource;
import com.lpzahd.gallery.wallpager.Slideshow;
import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.consumer.State;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.TimeZone;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 */
public class MediaWaiter extends ActivityWaiter<GalleryActivity, ActivityWaiter> {

    public static final TimeZone CURRENT_TIME_ZONE = TimeZone.getDefault();

    /**
     * 像素密度
     */
    public static float PIXEL_DENSITY = 0.0f;

    public static final int CROP_MSG_INTERNAL = 100;
    public static final int CROP_MSG = 10;

    private GridLayer mGridLayer;

    private boolean mPause;

    public MediaWaiter(GalleryActivity activity) {
        super(activity);
    }

    public GalleryActivity getActivity() {
        return context;
    }

    public ReverseGeocoder getReverseGeocoder() {
        return mReverseGeocoder;
    }

    public boolean isPaused() {
        return mPause;
    }

    public void refreshUIForSet(MediaSet set) {
        if (mGridLayer != null) {
            final MediaFeed feed = mGridLayer.getFeed();
            if (feed != null) {
                final MediaSet currentSet = feed.getMediaSet(set.mId);
                if (currentSet != null) {
                    // We need to refresh the UI with context set if the number of items have changed
                    if (currentSet.getNumItems() != set.getNumItems() || currentSet.mMaxAddedTimestamp != set.mMaxAddedTimestamp) {
                        final MediaSet newSet = feed.replaceMediaSet(set.mId, currentSet.mDataSource);
                        newSet.mName = currentSet.mName;
                        newSet.generateTitle(true);
                    }
                }
            }
        }
    }

    private ReverseGeocoder mReverseGeocoder;
    private RenderView mRenderView;
    private boolean mDockSlideshow = false;
    private final boolean imageManagerHasStorage = ImageManager.hasStorage();

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (parseOuterIntent()) return;

        density();

        setContent();

        runLayer();

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void runLayer() {
        Thread t = new Thread() {
            public void run() {
                int numRetries = 25;
                if (!imageManagerHasStorage) {
                    showToast(context.getResources().getString(R.string.no_sd_card), Toast.LENGTH_LONG);
                    do {
                        --numRetries;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                        }
                    } while (numRetries > 0 && !ImageManager.hasStorage());
                }
                final boolean imageManagerHasStorageAfterDelay = ImageManager.hasStorage();
                CacheService.computeDirtySets(MediaWaiter.this);
                CacheService.startCache(context, false);
                final boolean isCacheReady = CacheService.isCacheReady(false);

                // Creating the DataSource objects.
                final PicasaDataSource picasaDataSource = new PicasaDataSource(context);
                final LocalDataSource localDataSource = new LocalDataSource(MediaWaiter.this);
                final ConcatenatedDataSource combinedDataSource = new ConcatenatedDataSource(localDataSource, picasaDataSource);

                // Depending upon the intent, we assign the right dataSource.
                if (!isPickIntent() && !isViewIntent()) {
                    if (imageManagerHasStorageAfterDelay) {
                        mGridLayer.setDataSource(combinedDataSource);
                    } else {
                        mGridLayer.setDataSource(picasaDataSource);
                    }
                    if (!isCacheReady && imageManagerHasStorageAfterDelay) {
                        showToast(context.getResources().getString(R.string.loading_new), Toast.LENGTH_LONG);
                    }
                } else if (!isViewIntent()) {
                    final Intent intent = context.getIntent();
                    if (intent != null) {
                        final String type = intent.resolveType(context);
                        boolean includeImages = isImageType(type);
                        boolean includeVideos = isVideoType(type);
                        localDataSource.setMimeFilter(!includeImages, !includeVideos);
                        if (includeImages) {
                            if (imageManagerHasStorageAfterDelay) {
                                mGridLayer.setDataSource(combinedDataSource);
                            } else {
                                mGridLayer.setDataSource(picasaDataSource);
                            }
                        } else {
                            mGridLayer.setDataSource(localDataSource);
                        }
                        mGridLayer.setPickIntent(true);
                        if (!imageManagerHasStorageAfterDelay) {
                            showToast(context.getResources().getString(R.string.no_sd_card), Toast.LENGTH_LONG);
                        } else {
                            showToast(context.getResources().getString(R.string.pick_prompt), Toast.LENGTH_LONG);
                        }
                    }
                } else {
                    // View intent for images.
                    Uri uri = context.getIntent().getData();
                    boolean slideshow = context.getIntent().getBooleanExtra("slideshow", false);
                    final SingleDataSource singleDataSource = new SingleDataSource(MediaWaiter.this, uri.toString(), slideshow);
                    final ConcatenatedDataSource singleCombinedDataSource = new ConcatenatedDataSource(singleDataSource, picasaDataSource);
                    mGridLayer.setDataSource(singleCombinedDataSource);
                    mGridLayer.setViewIntent(true, Utils.getBucketNameFromUri(uri));
                    if (singleDataSource.isSingleImage()) {
                        mGridLayer.setSingleImage(false);
                    } else if (slideshow) {
                        mGridLayer.setSingleImage(true);
                        mGridLayer.startSlideshow();
                    }
                }
            }
        };
        t.start();
    }

    private void setContent() {
        mReverseGeocoder = new ReverseGeocoder(context);
        mRenderView = new RenderView(context);
        mGridLayer = new GridLayer(this, (int) (96.0f * PIXEL_DENSITY), (int) (72.0f * PIXEL_DENSITY), new GridLayoutInterface(4),
                mRenderView);
        mRenderView.setRootLayer(mGridLayer);
        context.setContentView(mRenderView);
    }

    private void density() {
        if (PIXEL_DENSITY == 0.0f) {
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            PIXEL_DENSITY = metrics.density;
        }
    }

    private boolean isViewIntent() {
        String action = context.getIntent().getAction();
        return Intent.ACTION_VIEW.equals(action);
    }

    private boolean isPickIntent() {
        String action = context.getIntent().getAction();
        return (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action));
    }

    private boolean isImageType(String type) {
        return type.equals("vnd.android.cursor.dir/image") || type.equals("image/*");
    }

    private boolean isVideoType(String type) {
        return type.equals("vnd.android.cursor.dir/video") || type.equals("video/*");
    }

    private void showToast(final String string, final int duration) {
        context.getHandler().post(new Runnable() {
            public void run() {
                Toast.makeText(context, string, duration).show();
            }
        });
    }

    private boolean parseOuterIntent() {
        boolean slideshowIntent = false;
        if (isViewIntent()) {
            Bundle extras = context.getIntent().getExtras();
            if (extras != null) {
                slideshowIntent = extras.getBoolean("slideshow", false);
            }
        }
        if (isViewIntent() && context.getIntent().getData().equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                && slideshowIntent) {
            if (!imageManagerHasStorage) {
                Toast.makeText(context, context.getResources().getString(R.string.no_sd_card), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Slideshow slideshow = new Slideshow(context);
                slideshow.setDataSource(new RandomDataSource());
                context.setContentView(slideshow);
                mDockSlideshow = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void resume() {
        super.resume();

        if (mDockSlideshow) {
            return;
        }
        if (ImageManager.hasStorage()) {
            CacheService.computeDirtySets(this);
            CacheService.startCache(context, false);
        }
        if (mRenderView != null) {
            mRenderView.onResume();
        }
        if (mPause) {
            // We check to see if the authenticated accounts have changed, and
            // if so, reload the datasource.
            mPause = false;
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (mRenderView != null)
            mRenderView.onPause();

        mPause = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (mGridLayer != null)
            mGridLayer.stop();
        if (mReverseGeocoder != null) {
            mReverseGeocoder.flushCache();
        }
        LocalDataSource.sThumbnailCache.flush();
        LocalDataSource.sThumbnailCacheVideo.flush();
        PicasaDataSource.sThumbnailCache.flush();
        CacheService.startCache(context, true);
    }

    @Override
    public void destroy() {
        // 快速释放view资源
        context.setContentView(R.layout.main);
        if (mGridLayer != null) {
            DataSource dataSource = mGridLayer.getDataSource();
            if (dataSource != null) {
                dataSource.shutdown();
            }
            mGridLayer.shutdown();
        }
        if (mReverseGeocoder != null)
            mReverseGeocoder.shutdown();
        if (mRenderView != null) {
            mRenderView.shutdown();
            mRenderView = null;
        }
        mGridLayer = null;
    }

    @Override
    public void configurationChanged(Configuration newConfig) {
        super.configurationChanged(newConfig);
        if (mGridLayer != null) {
            mGridLayer.markDirty(30);
        }
        if (mRenderView != null)
            mRenderView.requestRender();
    }

    @Override
    public int keyDown(int keyCode, KeyEvent event) {
        if (mRenderView == null)
            return super.keyDown(keyCode, event);

        if(mRenderView.onKeyDown(keyCode, event))
            return State.STATE_ALLOW;

        return super.keyDown(keyCode, event);
    }

    @Override
    public int backPressed() {
        // ???
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        context.startActivity(intent);

        return super.backPressed();
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data) {
        super.activityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CROP_MSG: {
                if (resultCode == Activity.RESULT_OK) {
                    context.setResult(resultCode, data);
                    finish();
                }
                break;
            }
            case CROP_MSG_INTERNAL: {
                // We cropped an image, we must try to set the focus of the camera
                // to that image.
                if (resultCode == Activity.RESULT_OK) {
                    String contentUri = data.getAction();
                    if (mGridLayer != null) {
                        mGridLayer.focusItem(contentUri);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void lowMemory() {
        super.lowMemory();

        if (mRenderView != null)
            mRenderView.handleLowMemory();
    }

    private MediaScannerConnection mConnection;

    public void launchCropperOrFinish(final MediaItem item) {
        final Bundle myExtras = context.getIntent().getExtras();
        String cropValue = myExtras != null ? myExtras.getString("crop") : null;
        final String contentUri = item.mContentUri;
        if (cropValue != null) {
            Bundle newExtras = new Bundle();
            if (cropValue.equals("circle")) {
                newExtras.putString("circleCrop", "true");
            }
            Intent cropIntent = new Intent();
            cropIntent.setData(Uri.parse(contentUri));
            cropIntent.setClass(context, CropImage.class);
            cropIntent.putExtras(newExtras);
            // Pass through any extras that were passed in.
            cropIntent.putExtras(myExtras);
            context.startActivityForResult(cropIntent, CROP_MSG);
        } else {
            if (contentUri.startsWith("http://")) {
                // This is a http uri, we must save it locally first and
                // generate a content uri from it.
                final ProgressDialog dialog = ProgressDialog.show(context, context.getResources().getString(R.string.initializing),
                        context.getResources().getString(R.string.running_face_detection), true, false);
                MediaScannerConnection.MediaScannerConnectionClient client = new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                        if (mConnection != null) {
                            try {
                                final String path = UriTexture.writeHttpDataInDirectory(context, contentUri,
                                        LocalDataSource.DOWNLOAD_BUCKET_NAME);
                                if (path != null) {
                                    mConnection.scanFile(path, item.mMimeType);
                                } else {
                                    shutdown("");
                                }
                            } catch (Exception e) {
                                shutdown("");
                            }
                        }
                    }

                    public void onScanCompleted(String path, Uri uri) {
                        shutdown(uri.toString());
                    }

                    private void shutdown(String uri) {
                        dialog.dismiss();
                        performReturn(myExtras, uri);
                        if (mConnection != null) {
                            mConnection.disconnect();
                        }
                    }
                };
                MediaScannerConnection connection = new MediaScannerConnection(context, client);
                connection.connect();
                mConnection = connection;
            } else {
                performReturn(myExtras, contentUri);
            }
        }
    }

    private void performReturn(Bundle myExtras, String contentUri) {
        Intent result = new Intent(null, Uri.parse(contentUri));
        if (myExtras != null && myExtras.getBoolean("return-data")) {
            // The size of a transaction should be below 100K.
            Bitmap bitmap = null;
            try {
                bitmap = UriTexture.createFromUri(context, contentUri, 1024, 1024, 0);
            } catch (IOException | URISyntaxException ignored) {
            }
            if (bitmap != null) {
                result.putExtra("data", bitmap);
            }
        }
        context.setResult(Activity.RESULT_OK, result);
        finish();
    }
}
