package com.lpzahd.essay.context.instinct.waiter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.waiter.FileDownloadWaiter;
import com.lpzahd.essay.context.instinct.InstinctMediaActivity;
import com.lpzahd.essay.context.instinct.yiyibox.YiyiBox;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.essay.tool.OkHttpRxAdapter;
import com.lpzahd.essay.view.SimpleVideo;
import com.lpzahd.fresco.zoomable.DoubleTapGestureListener;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;
import com.lpzahd.waiter.consumer.State;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者 : 迪
 * 时间 : 2017/10/28.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiBoxMediaWaiter extends ToneActivityWaiter<InstinctMediaActivity> {

    public static final String TAG = "com.lpzahd.essay.context.instinct.waiter.YiyiBoxMediaWaiter";

    private static final String REGEX_TAG_IMG = "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\.webp)\\b)[^>]*>";

//    private static final String REGEX_TAG_VIDEO = "<video\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.mp3|\\.mp4|\\.flv|\\.avi|\\.rm|\\.rmvb|\\.wmv|\\.3gp|\\.mkv)\\b)[^>]*>";

    private static final String REGEX_TAG_VIDEO = "<video\\b[^>]*\\b[^>]*>";

    private static final String REGEX_TAG_SRC = "src\\s*=\\s*\"?(.*?)(\"|>|\\s+)";
    private static final String REGEX_TAG_POSTER = "poster\\s*=\\s*\"?(.*?)(\"|>|\\s+)";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.simple_video)
    SimpleVideo simpleVideo;

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Transmitter<YiyiBox.DataBean.ItemsBean> mTransmitter;
    private SinglePicWaiter.PicAdapter mAdapter;

    private YiyiBox.DataBean.ItemsBean mSource;

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_VIDEO = 1;

    private int type = TYPE_PHOTO;

    private OrientationUtils mOriUtils;

    private Disposable loadDispose;

    private List<SinglePicWaiter.PicBean> pics;
    private List<VideoBean> videos;
    private int displayPosition = 0;

    private FileDownloadWaiter mFileDownloadWaiter;

    public YiyiBoxMediaWaiter(InstinctMediaActivity instinctMediaActivity) {
        super(instinctMediaActivity);
    }

    @Override
    protected void init() {
        super.init();
        addWaiter(mFileDownloadWaiter = new FileDownloadWaiter(context));
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void initView() {

        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView) {
            @Override
            public void onLongPress(MotionEvent e) {
                if(type == TYPE_PHOTO) {
                    mFileDownloadWaiter.downloadWithCheckFile(pics.get(displayPosition).uri.toString());
                } else if(type == TYPE_VIDEO) {
                    mFileDownloadWaiter.down(videos.get(displayPosition).video);
                }
            }
        });

        zoomableDraweeView.setIsLongpressEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new SinglePicWaiter.PicAdapter(context);
        recyclerView.setAdapter(mAdapter);


        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ToneItemTouchHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<SinglePicWaiter.PicHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, SinglePicWaiter.PicHolder picHolder) {
                super.onClick(rv, picHolder);
                displayPosition = picHolder.getAdapterPosition();

                if(displayPosition == -1) return;

                if(type == TYPE_PHOTO) {
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setUri(pics.get(displayPosition).uri)
                            .build();
                    zoomableDraweeView.setController(draweeController);
                } else if(type == TYPE_VIDEO) {
                    stepVideo(videos.get(displayPosition));
                }

            }

        });

        RxView.clicks(fab)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(mAdapter == null || Lists.empty(mAdapter.getData())) {
                            T.t("暂无图片哦！");
                        } else {
                            List<SinglePicWaiter.PicBean> pics = mAdapter.getData();

                            String[] urls = new String[pics.size()];

                            for(int i = 0, size = pics.size(); i < size; i++) {
                                urls[i] = pics.get(i).uri.toString();
                            }
                            mFileDownloadWaiter.showDownLoadDialog(urls);
                        }
                    }
                });
    }

    @Override
    protected void initData() {

        mTransmitter.transmit()
                .filter(new Predicate<YiyiBox.DataBean.ItemsBean>() {
                    @Override
                    public boolean test(YiyiBox.DataBean.ItemsBean itemsBean) throws Exception {
                        return itemsBean != null;
                    }
                })
                .subscribe(new Consumer<YiyiBox.DataBean.ItemsBean>() {
                    @Override
                    public void accept(YiyiBox.DataBean.ItemsBean itemsBean) throws Exception {
                        mSource = itemsBean;
                        if (itemsBean.getShorturl().startsWith("v")) {
                            //video
                            type = TYPE_VIDEO;
                            loadVideo(itemsBean);
                        } else if (itemsBean.getShorturl().startsWith("u")) {
                            //photo
                            loadPhoto(itemsBean);
                            type = TYPE_PHOTO;
                        }
                    }
                });
    }

    private void loadVideo(YiyiBox.DataBean.ItemsBean source) {
        simpleVideo.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        String url = "http://www.jilehezi.com/video/" + source.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("referer", "http://www.jilehezi.com")
                .build();

        loadDispose = OkHttpRxAdapter.adapter(client.newCall(request))
                .subscribeOn(Schedulers.io())
                .retry()
                .filter(new Predicate<Response>() {
                    @Override
                    public boolean test(@NonNull Response response) throws Exception {
                        return response.isSuccessful();
                    }
                })
                .map(new Function<Response, List<VideoBean>>() {

                    @Override
                    public List<VideoBean> apply(@NonNull Response response) throws Exception {
                        ResponseBody body = response.body();

                        if (body == null)
                            return Collections.emptyList();

                        String htmlStr = body.string();
                        body.close();

                        List<VideoBean> videos = new ArrayList<>();

                        Pattern videoPattern = Pattern.compile(YiyiBoxMediaWaiter.REGEX_TAG_VIDEO, Pattern.CASE_INSENSITIVE);
                        Matcher videoMatcher = videoPattern.matcher(htmlStr);

                        Pattern srcPattern = Pattern.compile(REGEX_TAG_SRC, Pattern.CASE_INSENSITIVE);
                        Pattern posterPattern = Pattern.compile(REGEX_TAG_POSTER, Pattern.CASE_INSENSITIVE);

                        while (videoMatcher.find()) {
                            String videoStr = videoMatcher.group(0);

                            VideoBean bean = new VideoBean();
                            Matcher srcMatcher = srcPattern.matcher(videoStr);
                            while (srcMatcher.find()) {
                                bean.video = srcMatcher.group(1);
                            }

                            Matcher posterMatcher = posterPattern.matcher(videoStr);
                            while (posterMatcher.find()) {
                                bean.img = posterMatcher.group(1);
                            }

                            if (!Strings.empty(bean.video) || !Strings.empty(bean.img)) {
                                videos.add(bean);
                            }

                        }
                        return videos;
                    }
                })
                .map(new Function<List<VideoBean>, List<VideoBean>>() {
                    @Override
                    public List<VideoBean> apply(@NonNull List<VideoBean> videos) throws Exception {
                        URL fromUrl = new URL("http://www.jilehezi.com");
                        for (int i = 0, size = videos.size(); i < size; i++) {
                            VideoBean video = videos.get(i);
                            video.video = new URL(fromUrl, video.video).toExternalForm();
                            video.img = new URL(fromUrl, video.img).toExternalForm();
                        }
                        YiyiBoxMediaWaiter.this.videos = videos;
                        return videos;
                    }
                })
                .map(new Function<List<VideoBean>, List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public List<SinglePicWaiter.PicBean> apply(@NonNull List<VideoBean> strings) throws Exception {
                        List<SinglePicWaiter.PicBean> pics = new ArrayList<>();
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            SinglePicWaiter.PicBean bean = new SinglePicWaiter.PicBean();
                            bean.uri = Frescoer.uri(strings.get(i).img, ImageSource.SOURCE_NET);
                            pics.add(bean);
                        }
                        return pics;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        T.t("查找视频中...");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public void accept(List<SinglePicWaiter.PicBean> pics) throws Exception {
                        T.t("发现视频 ：" + videos.get(displayPosition).video);
                        mAdapter.setData(pics);

                        if(!Lists.empty(videos)) {
                            displayPosition = 0;
                            stepVideo(videos.get(displayPosition));
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.t(throwable.getMessage());
                    }
                });
    }

    private void stepVideo(VideoBean videoBean) {
        SimpleVideo.Video video = new SimpleVideo.Video();
        video.url = videoBean.video;
        video.name = "超清视频";
        List<SimpleVideo.Video> videos = new ArrayList<>();
        videos.add(video);

        File videoFilePath = Keeper.getF().getScopeFile(Files.Scope.VIDEO_RAW);
        simpleVideo.setUp(videos, true, videoFilePath, "荷尔蒙");

        SimpleDraweeView draweeView = new SimpleDraweeView(context);
        simpleVideo.setThumbImageView(draweeView);
        draweeView.setImageURI(Frescoer.uri(videoBean.img, ImageSource.SOURCE_NET));


        //增加title
        simpleVideo.getTitleTextView().setVisibility(View.VISIBLE);
        //videoPlayer.setShowPauseCover(false);

        //videoPlayer.setSpeed(2f);

        //设置返回键
        simpleVideo.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转
        mOriUtils = new OrientationUtils(context, simpleVideo);

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        simpleVideo.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriUtils.resolveByClick();
            }
        });


        //是否可以滑动调整
        simpleVideo.setIsTouchWiget(true);

        //设置返回按键功能
        simpleVideo.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });

        simpleVideo.start();

    }

    private void loadPhoto(YiyiBox.DataBean.ItemsBean source) {
        simpleVideo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        String url = "http://www.jilehezi.com/photo/" + source.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("referer", "http://www.jilehezi.com")
                .build();

        loadDispose = OkHttpRxAdapter.adapter(client.newCall(request))
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Response>() {
                    @Override
                    public boolean test(@NonNull Response response) throws Exception {
                        return response.isSuccessful();
                    }
                })
                .map(new Function<Response, List<String>>() {

                    @Override
                    public List<String> apply(@NonNull Response response) throws Exception {
                        ResponseBody body = response.body();

                        if (body == null)
                            return Collections.emptyList();

                        String htmlStr = body.string();
                        body.close();

                        Map<String, Integer> imgMap = new HashMap<>();
                        Pattern p = Pattern.compile(YiyiBoxMediaWaiter.REGEX_TAG_IMG, Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(htmlStr);
                        String quote;
                        String src;
                        while (m.find()) {
                            quote = m.group(1);
                            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2);
                            imgMap.put(src, 1);
                        }
                        return new ArrayList<>(imgMap.keySet());
                    }
                })
                .map(new Function<List<String>, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull List<String> strings) throws Exception {
                        URL fromUrl = new URL("http://www.jilehezi.com");
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            String img = strings.get(i);
                            strings.set(i, new URL(fromUrl, img).toExternalForm());
                        }
                        return strings;
                    }
                })
                .map(new Function<List<String>, List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public List<SinglePicWaiter.PicBean> apply(@NonNull List<String> strings) throws Exception {
                        List<SinglePicWaiter.PicBean> pics = new ArrayList<>();
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            SinglePicWaiter.PicBean bean = new SinglePicWaiter.PicBean();
                            bean.uri = Frescoer.uri(strings.get(i), ImageSource.SOURCE_NET);
                            pics.add(bean);
                        }
                        YiyiBoxMediaWaiter.this.pics = pics;
                        return pics;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        T.t("开始检索图片");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public void accept(List<SinglePicWaiter.PicBean> picBeans) throws Exception {
                        T.t("发现%s张图片", picBeans.size());
                        mAdapter.setData(picBeans);

                        if(!Lists.empty(picBeans)) {
                            displayPosition = 0;
                            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                                    .setUri(pics.get(displayPosition).uri)
                                    .build();
                            zoomableDraweeView.setController(draweeController);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.t(throwable.getMessage());
                    }
                });

    }

    @Override
    protected void pause() {
        super.pause();
        if (type == TYPE_VIDEO)
            simpleVideo.onVideoPause();

    }

    @Override
    protected void resume() {
        super.resume();
        if (type == TYPE_VIDEO)
            simpleVideo.onVideoResume();
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mOriUtils != null)
            mOriUtils.releaseListener();

        if(loadDispose != null && !loadDispose.isDisposed())
            loadDispose.dispose();
    }

    @Override
    protected int backPressed() {
        if (type == TYPE_VIDEO) {
            //先返回正常状态
            if (mOriUtils != null && mOriUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                simpleVideo.getFullscreenButton().performClick();
                return State.STATE_PREVENT;
            }

            //释放所有
            simpleVideo.setStandardVideoAllCallBack(null);
            GSYVideoPlayer.releaseAllVideos();
        }

        return super.backPressed();
    }

    private static class VideoBean {
        String video;
        String img;
    }
}
