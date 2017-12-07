package com.lpzahd.essay.context.music.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.os.CancellationSignal;

import com.lpzahd.atool.ui.L;
import com.lpzahd.essay.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 作者 : 迪
 * 时间 : 2017/12/5.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, String> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

    static {
//        createMediaMetadataCompat(
//                "Jazz_In_Paris",
//                "Jazz in Paris",
//                "Media Right Productions",
//                "Jazz & Blues",
//                "Jazz",
//                103,
//                TimeUnit.SECONDS,
//                "music/jazz_in_paris.mp3",
//                R.drawable.album_jazz_blues,
//                "album_jazz_blues");
//        createMediaMetadataCompat(
//                "The_Coldest_Shoulder",
//                "The Coldest Shoulder",
//                "The 126ers",
//                "Youtube Audio Library Rock 2",
//                "Rock",
//                160,
//                TimeUnit.SECONDS,
//                "music/the_coldest_shoulder.mp3",
//                R.drawable.album_youtube_audio_library_rock_2,
//                "album_youtube_audio_library_rock_2");
//        createMediaMetadataCompat(
//                "mediaid",
//                "title",
//                "artlist",
//                "album",
//                "genre",
//                160,
//                TimeUnit.SECONDS,
//                "music/evil.mp3",
//                R.drawable.album_youtube_audio_library_rock_2,
//                "-,-");
    }


    public static void initMusicLibrary(Context context) {
        addMusicLibrary(context);
    }

    public static void addMusicLibrary(Context context) {
        getMusicFromContext(context, 0, Integer.MAX_VALUE);
    }

    public static String getRoot() {
        return "root";
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
    }

    public static String getMusicFilename(String mediaId) {
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

    private static String getAlbumRes(String mediaId) {
        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : null;
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        return createAlbumThumbnail(MusicLibrary.getAlbumRes(mediaId));
//        return BitmapFactory.decodeResource(context.getResources(),
//                MusicLibrary.getAlbumRes(mediaId));
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            TimeUnit durationUnit,
            String musicFilename) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(duration, durationUnit))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
        albumRes.put(mediaId, musicFilename);
        musicFileName.put(mediaId, musicFilename);
    }

    private static void getMusicFromContext(Context context, int page, int limit) {
        int offset = page * limit;
        ContentResolver resolver = context.getContentResolver();

        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Audio.Media._ID);
//        projection.add(MediaStore.Audio.Media.DISPLAY_NAME);
        projection.add(MediaStore.Audio.Media.TITLE);
        projection.add(MediaStore.Audio.Media.DURATION);
        projection.add(MediaStore.Audio.Media.ARTIST);
        projection.add(MediaStore.Audio.Media.ALBUM);
//        projection.add(MediaStore.Audio.Media.YEAR);
//        projection.add(MediaStore.Audio.Media.MIME_TYPE);
        projection.add(MediaStore.Audio.Media.DATA);
//        projection.add(MediaStore.Audio.Media.SIZE);
//        projection.add(MediaStore.Audio.Media.GENRE);

        String sortOrder;
        if (limit == Integer.MAX_VALUE) {
            sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        } else {
            sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset;
        }


        Cursor cursor = ContentResolverCompat.query(resolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection.toArray(new String[projection.size()]),
                MediaStore.Audio.Media.MIME_TYPE + "=? or " + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"},
                sortOrder,
                new CancellationSignal());

        if (cursor != null) {

            try {
                int count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    do {

//                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String genre = null;
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                        try {
//                            retriever.setDataSource(fileName);
//                            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                            album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//                            String durationTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            duration = Integer.parseInt(durationTime);
//                        } catch (IllegalArgumentException ex) {
//                            L.e("IllegalArgumentException : " + ex);
//                        } catch (RuntimeException ex) {
//                            L.e("RuntimeException : " + ex);
//                        } finally {
//                            try {
//                                retriever.release();
//                            } catch (RuntimeException ex) {
//                                // Ignore failures while cleaning up.
//                            }
//                        }

                        createMediaMetadataCompat(
                                id,
                                title,
                                artist,
                                album,
                                genre,
                                duration,
                                TimeUnit.MILLISECONDS,
                                fileName
                        );

                    } while (cursor.moveToNext());
                }
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }

        }
    }

    private static Bitmap createAlbumThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            byte[] art = retriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (IllegalArgumentException ex) {
            L.e("IllegalArgumentException : " + ex);
        } catch (RuntimeException ex) {
            L.e("RuntimeException : " + ex);
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }

}
