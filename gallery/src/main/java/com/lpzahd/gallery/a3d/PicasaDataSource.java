package com.lpzahd.gallery.a3d;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.lpzahd.gallery.change.AlbumEntry;
import com.lpzahd.gallery.change.Entry;
import com.lpzahd.gallery.change.EntrySchema;
import com.lpzahd.gallery.context.GalleryActivity;

import java.util.ArrayList;

public final class PicasaDataSource implements DataSource {
    private static final String TAG = "PicasaDataSource";
    public static final DiskCache sThumbnailCache = new DiskCache("picasa-thumbs");
    private static final String DEFAULT_BUCKET_SORT_ORDER = AlbumEntry.Columns.USER + ", " + AlbumEntry.Columns.DATE_PUBLISHED
            + " DESC";

    private ContentProviderClient mProviderClient;
    private final Context mContext;
    private ContentObserver mAlbumObserver;

    public PicasaDataSource(final Context context) {
        mContext = context;
    }

//    public static final HashMap<String, Boolean> getAccountStatus(final Context context) {
//        final Account[] accounts = PicasaApi.getAccounts(context);
//        int numAccounts = accounts.length;
//        HashMap<String, Boolean> accountsEnabled = new HashMap<String, Boolean>(numAccounts);
//        for (int i = 0; i < numAccounts; ++i) {
//            Account account = accounts[i];
//            boolean isEnabled = ContentResolver.getSyncAutomatically(account, PicasaContentProvider.AUTHORITY);
//            String username = account.name.toLowerCase();
//            if (username.contains("@gmail.") || username.contains("@googlemail.")) {
//                // Strip the domain from GMail accounts for canonicalization.
//                // TODO: is there an official way?
//                username = username.substring(0, username.indexOf('@'));
//            }
//            accountsEnabled.put(username, new Boolean(isEnabled));
//        }
//        return accountsEnabled;
//    }

    public void loadMediaSets(final MediaFeed feed) {
        // We do this here and not in the constructor to speed application
        // loading time since this method is called in a background thread
        if (mProviderClient == null) {
            mProviderClient = mContext.getContentResolver().acquireContentProviderClient(AUTHORITY);
        }

        final Handler handler = ((GalleryActivity) mContext).getHandler();
        final ContentObserver albumObserver = new ContentObserver(handler) {
            public void onChange(boolean selfChange) {
                loadMediaSetsIntoFeed(feed);
            }
        };
        mAlbumObserver = albumObserver;
        loadMediaSetsIntoFeed(feed);

        // Start listening.
        ContentResolver cr = mContext.getContentResolver();
        cr.registerContentObserver(ALBUMS_URI, false, mAlbumObserver);
        cr.registerContentObserver(PHOTOS_URI, false, mAlbumObserver);
//        loadMediaSetsIntoFeed(feed);
    }

    public void shutdown() {
        if (mAlbumObserver != null) {
            ContentResolver cr = mContext.getContentResolver();
            cr.unregisterContentObserver(mAlbumObserver);
        }
    }

    public void loadItemsForSet(final MediaFeed feed, final MediaSet parentSet, int rangeStart, int rangeEnd) {
        if (parentSet != null) {
            addItemsToFeed(feed, parentSet, rangeStart, rangeEnd);
        }
    }

    public static final String AUTHORITY = "com.cooliris.picasa.contentprovider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri PHOTOS_URI = Uri.withAppendedPath(BASE_URI, "photos");
    public static final Uri ALBUMS_URI = Uri.withAppendedPath(BASE_URI, "albums");

//    public static final String[] PROJECTION = new String[]{
//            "sync_account", "photos_etag", "photos_dirty", "edit_uri", "user", "title", "summary",
//            "date_published", "date_updated", "date_edited", "num_photos", "bytes_used",
//            "location_string", "thumbnail_url", "html_page_url"
//    };

    protected void loadMediaSetsIntoFeed(final MediaFeed feed) {
        final ContentProviderClient client = mProviderClient;
        if (client == null)
            return;
        try {
            final EntrySchema albumSchema = AlbumEntry.SCHEMA;
            final Cursor cursor = client.query(ALBUMS_URI, albumSchema.getProjection(), null, null,
                    DEFAULT_BUCKET_SORT_ORDER);
            if (cursor == null) return;

            final AlbumEntry album = new AlbumEntry();
            MediaSet mediaSet;
            if (cursor.moveToFirst()) {
                final int numAlbums = cursor.getCount();
                final ArrayList<MediaSet> picasaSets = new ArrayList<MediaSet>(numAlbums);
                do {
                    albumSchema.cursorToObject(cursor, album);
                    mediaSet = feed.getMediaSet(album.id);
                    if (mediaSet == null) {
                        mediaSet = feed.addMediaSet(album.id, this);
                        mediaSet.mName = album.title;
                        mediaSet.mEditUri = album.editUri;
                        mediaSet.generateTitle(true);
                    } else {
                        mediaSet.setNumExpectedItems(album.numPhotos);
                    }
                    mediaSet.mPicasaAlbumId = album.id;
                    mediaSet.mSyncPending = album.photosDirty;
                    picasaSets.add(mediaSet);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (RemoteException e) {
            Log.e(TAG, "Error occurred loading albums");
        }
    }

    private void addItemsToFeed(MediaFeed feed, MediaSet set, int start, int end) {
        final ContentProviderClient client = mProviderClient;
        Cursor cursor = null;
        try {
            // Query photos in the album.
            final EntrySchema photosSchema = PhotoProjection.SCHEMA;
            final String whereInAlbum = "album_id = " + Long.toString(set.mId);
            cursor = client.query(PHOTOS_URI, photosSchema.getProjection(), whereInAlbum, null, null);
            if (cursor == null) return;

            final PhotoProjection photo = new PhotoProjection();
            int count = cursor.getCount();
            if (count < end) {
                end = count;
            }
            set.setNumExpectedItems(count);
            set.generateTitle(true);
            // Move to the next unread item.
            final int newIndex = start + 1;
            if (newIndex > count || !cursor.move(newIndex)) {
                end = 0;
                cursor.close();
                set.updateNumExpectedItems();
                set.generateTitle(true);
                return;
            }
            if (set.mNumItemsLoaded == 0) {
                photosSchema.cursorToObject(cursor, photo);
                set.mMinTimestamp = photo.dateTaken;
                cursor.moveToLast();
                photosSchema.cursorToObject(cursor, photo);
                set.mMinTimestamp = photo.dateTaken;
                cursor.moveToFirst();
            }
            for (int i = 0; i < end; ++i) {
                photosSchema.cursorToObject(cursor, photo);
                final MediaItem item = new MediaItem();
                item.mId = photo.id;
                item.mEditUri = photo.editUri;
                item.mMimeType = photo.contentType;
                item.mDateTakenInMs = photo.dateTaken;
                item.mLatitude = photo.latitude;
                item.mLongitude = photo.longitude;
                item.mThumbnailUri = photo.thumbnailUrl;
                item.mScreennailUri = photo.screennailUrl;
                item.mContentUri = photo.contentUrl;
                item.mCaption = photo.title;
                item.mWeblink = photo.htmlPageUrl;
                item.mDescription = photo.summary;
                item.mFilePath = item.mContentUri;
                feed.addItemToMediaSet(item, set);
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred loading photos for album " + set.mId);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean performOperation(final int operation, final ArrayList<MediaBucket> mediaBuckets, final Object data) {
        try {
            if (operation == MediaFeed.OPERATION_DELETE) {
                ContentProviderClient client = mProviderClient;
                for (int i = 0, numBuckets = mediaBuckets.size(); i != numBuckets; ++i) {
                    MediaBucket bucket = mediaBuckets.get(i);
                    ArrayList<MediaItem> items = bucket.mediaItems;
                    if (items == null) {
                        // Delete an album.
                        String albumUri = ALBUMS_URI + "/" + bucket.mediaSet.mId;
                        client.delete(Uri.parse(albumUri), null, null);
                    } else {
                        // Delete a set of photos.
                        for (int j = 0, numItems = items.size(); j != numItems; ++j) {
                            MediaItem item = items.get(j);
                            if (item != null) {
                                String itemUri = PHOTOS_URI + "/" + item.mId;
                                client.delete(Uri.parse(itemUri), null, null);
                            }
                        }
                    }
                }
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public DiskCache getThumbnailCache() {
        return sThumbnailCache;
    }

    /**
     * The projection of PhotoEntry needed by the data source.
     */
    private static final class PhotoProjection extends Entry {
        public static final EntrySchema SCHEMA = new EntrySchema(PhotoProjection.class);
        @Column("edit_uri")
        public String editUri;
        @Column("title")
        public String title;
        @Column("summary")
        public String summary;
        @Column("date_taken")
        public long dateTaken;
        @Column("latitude")
        public double latitude;
        @Column("longitude")
        public double longitude;
        @Column("thumbnail_url")
        public String thumbnailUrl;
        @Column("screennail_url")
        public String screennailUrl;
        @Column("content_url")
        public String contentUrl;
        @Column("content_type")
        public String contentType;
        @Column("html_page_url")
        public String htmlPageUrl;
    }

}
