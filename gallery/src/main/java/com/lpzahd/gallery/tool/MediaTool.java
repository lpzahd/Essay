package com.lpzahd.gallery.tool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;

import com.lpzahd.Strings;
import com.lpzahd.atool.ui.L;
import com.lpzahd.base.NoInstance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class MediaTool extends NoInstance {

    public static final String MEDIA_NO_BUCKET = String.valueOf(Integer.MIN_VALUE);

    public static List<ImageBean> getImageFromContext(Context context) {
        return getImageFromContext(context, MEDIA_NO_BUCKET, 1, Integer.MAX_VALUE);
    }

    public static List<ImageBean> getImageFromContext(Context context, String bucketId) {
        return getImageFromContext(context, bucketId, 1, Integer.MAX_VALUE);
    }

    public static List<ImageBean> getImageFromContext(Context context, int page, int limit) {
        return getImageFromContext(context, MEDIA_NO_BUCKET, page, limit);
    }

    public static List<ImageBean> getImageFromContext(Context context, String bucketId, int page, int limit) {
        return getImageFromContext(context, MEDIA_NO_BUCKET, page, limit, true);
    }

    /**
     * 获取
     *
     * @param context  上下文
     * @param bucketId 当bucketId = Integer.MIN_VALUE 时，全查
     * @param page     页码
     * @param limit    数量 limit = Integer.MAX_VALUE 时，全查
     * @param strict   严格校验文件是否存在
     */
    public static List<ImageBean> getImageFromContext(Context context, String bucketId, int page, int limit, boolean strict) {
        int offset = page * limit;
        ContentResolver resolver = context.getContentResolver();

        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Images.Media._ID);
        projection.add(MediaStore.Images.Media.TITLE);
        projection.add(MediaStore.Images.Media.DATA);
        projection.add(MediaStore.Images.Media.BUCKET_ID);
        projection.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        projection.add(MediaStore.Images.Media.MIME_TYPE);
        projection.add(MediaStore.Images.Media.DATE_ADDED);
        projection.add(MediaStore.Images.Media.DATE_MODIFIED);
        projection.add(MediaStore.Images.Media.LATITUDE);
        projection.add(MediaStore.Images.Media.LONGITUDE);
        projection.add(MediaStore.Images.Media.SIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Video.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
        }

        String selection = null;
        String[] selectionArgs = null;
        if (!Strings.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{bucketId};
        }

        String sortOrder;
        if (limit == Integer.MAX_VALUE) {
            sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        } else {
            sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset;
        }

        Cursor cursor = ContentResolverCompat.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection.toArray(new String[projection.size()]),
                selection,
                selectionArgs,
                sortOrder,
                new CancellationSignal());

        List<ImageBean> imageBeanList = new ArrayList<>();
        if (cursor != null) {

            try {
                int count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    do {
                        ImageBean imageBean = parseImageCursor(cursor);

                        if(!strict) {
                            imageBeanList.add(imageBean);
                        } else {
                            if(new File(imageBean.getOriginalPath()).exists()) {
                                imageBeanList.add(imageBean);
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }

        }

        return imageBeanList;
    }

    private static ImageBean parseImageCursor(Cursor cursor) {
        ImageBean imageBean = new ImageBean();
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        imageBean.setId(id);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
        imageBean.setTitle(title);
        String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        imageBean.setOriginalPath(originalPath);
        String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        imageBean.setBucketId(bucketId);
        String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        imageBean.setBucketDisplayName(bucketDisplayName);
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        imageBean.setMimeType(mimeType);
        long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        imageBean.setCreateDate(createDate);
        long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
        imageBean.setModifiedDate(modifiedDate);
        long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
        imageBean.setLength(length);


        int width = 0, height = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
        } else {
            try {
                ExifInterface exifInterface = new ExifInterface(originalPath);
                width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            } catch (IOException e) {
                L.e(e.getMessage());
            }
        }
        imageBean.setWidth(width);
        imageBean.setHeight(height);

        double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
        imageBean.setLatitude(latitude);
        double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
        imageBean.setLongitude(longitude);
        return imageBean;
    }

    public static List<VideoBean> getVideoFromContext(Context context) {
        return getVideoFromContext(context, MEDIA_NO_BUCKET, 1, Integer.MAX_VALUE);
    }

    public static List<VideoBean> getVideoFromContext(Context context, String bucketId) {
        return getVideoFromContext(context, bucketId, 1, Integer.MAX_VALUE);
    }

    public static List<VideoBean> getVideoFromContext(Context context, int page, int limit) {
        return getVideoFromContext(context, MEDIA_NO_BUCKET, page, limit);
    }

    public static List<VideoBean> getVideoFromContext(Context context, String bucketId, int page, int limit) {
        return getVideoFromContext(context, MEDIA_NO_BUCKET, page, limit, true);
    }

    public static List<VideoBean> getVideoFromContext(Context context, String bucketId, int page, int limit, boolean strict) {
        int offset = page * limit;
        ContentResolver resolver = context.getContentResolver();

        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Video.Media._ID);
        projection.add(MediaStore.Video.Media.TITLE);
        projection.add(MediaStore.Video.Media.DATA);
        projection.add(MediaStore.Video.Media.BUCKET_ID);
        projection.add(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        projection.add(MediaStore.Video.Media.MIME_TYPE);
        projection.add(MediaStore.Video.Media.DATE_ADDED);
        projection.add(MediaStore.Video.Media.DATE_MODIFIED);
        projection.add(MediaStore.Video.Media.LATITUDE);
        projection.add(MediaStore.Video.Media.LONGITUDE);
        projection.add(MediaStore.Video.Media.SIZE);
        projection.add(MediaStore.Video.Media.WIDTH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Video.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
        }
        projection.add(MediaStore.Video.Media.DURATION);

        String selection = null;
        String[] selectionArgs = null;
        if (!Strings.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Video.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{bucketId};
        }

        String sortOrder;
        if (limit == Integer.MAX_VALUE) {
            sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";
        } else {
            sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset;
        }

        Cursor cursor = ContentResolverCompat.query(resolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection.toArray(new String[projection.size()]),
                selection,
                selectionArgs,
                sortOrder,
                null);

        List<VideoBean> videoBeanList = new ArrayList<>();
        if (cursor != null) {

            try {
                int count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    do {
                        VideoBean videoBean = parseVideoCursor(cursor);

                        if(!strict) {
                            videoBeanList.add(videoBean);
                        } else {
                            if(new File(videoBean.getOriginalPath()).exists()) {
                                videoBeanList.add(videoBean);
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }

        }

        return videoBeanList;
    }

    private static VideoBean parseVideoCursor(Cursor cursor) {
        VideoBean video = new VideoBean();

        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        video.setId(id);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        video.setTitle(title);
        String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        video.setOriginalPath(originalPath);
        String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
        video.setBucketId(bucketId);
        String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
        video.setBucketDisplayName(bucketDisplayName);
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        video.setMimeType(mimeType);
        long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
        video.setCreateDate(createDate);
        long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
        video.setModifiedDate(modifiedDate);
        long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        video.setLength(length);

        int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
        video.setWidth(width);
        video.setHeight(height);

        double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE));
        video.setLatitude(latitude);
        double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE));
        video.setLongitude(longitude);

        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        video.setDuration(duration);

        return video;
    }

    public static class VideoBean extends ImageBean {

        private long duration;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

    }

    public static class ImageBean {
        private long id;
        private String title;
        private String originalPath;
        private String bucketId;
        private String bucketDisplayName;
        private String mimeType;
        private long createDate;
        private long modifiedDate;
        private long length;
        private int width;
        private int height;
        private double latitude;
        private double longitude;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOriginalPath() {
            return originalPath;
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }

        public String getBucketId() {
            return bucketId;
        }

        public void setBucketId(String bucketId) {
            this.bucketId = bucketId;
        }

        public String getBucketDisplayName() {
            return bucketDisplayName;
        }

        public void setBucketDisplayName(String bucketDisplayName) {
            this.bucketDisplayName = bucketDisplayName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(long modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

}
