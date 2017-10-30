package com.lpzahd.essay.context.instinct.waiter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.instinct.InstinctPhotoActivity;
import com.lpzahd.essay.context.instinct.yiyibox.YiyiBox;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.essay.tool.OkHttpRxAdapter;
import com.lpzahd.essay.view.SimpleVideo;
import com.lpzahd.waiter.consumer.State;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.reactivestreams.Publisher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者 : 迪
 * 时间 : 2017/10/28.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiBoxPhotoWaiter extends ToneActivityWaiter<InstinctPhotoActivity> {

    public static final String TAG = "com.lpzahd.essay.context.instinct.waiter.YiyiBoxPhotoWaiter";

    private static final String REGEX_TAG_IMG = "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\.webp)\\b)[^>]*>";

    private static final String REGEX_TAG_VIDEO = "<video\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.mp3|\\.mp4|\\.flv|\\.avi|\\.rm|\\.rmvb|\\.wmv|\\.3gp|\\.mkv)\\b)[^>]*>";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.simple_video)
    SimpleVideo simpleVideo;

    private Transmitter<YiyiBox.DataBean.ItemsBean> mTransmitter;
    private PreviewPicWaiter.PreviewAdapter mAdapter;

    private YiyiBox.DataBean.ItemsBean mSource;

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_VIDEO = 1;

    private int type = TYPE_PHOTO;

    private OrientationUtils mOriUtils;

    private Disposable loadDispose;

    public YiyiBoxPhotoWaiter(InstinctPhotoActivity instinctPhotoActivity) {
        super(instinctPhotoActivity);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        mAdapter = new PreviewPicWaiter.PreviewAdapter(context);
        recyclerView.setAdapter(mAdapter);
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

        String url = "http://www.yiyibox.com/video/" + source.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("referer", "http://www.yiyibox.com")
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
                        Map<String, Integer> imgMap = new HashMap<>();
                        Pattern p = Pattern.compile(YiyiBoxPhotoWaiter.REGEX_TAG_VIDEO, Pattern.CASE_INSENSITIVE);
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
                .map(new Function<List<String>, String>() {
                    @Override
                    public String apply(@NonNull List<String> strings) throws Exception {
                        URL fromUrl = new URL("http://www.yiyibox.com");
                        String video = strings.get(0);
                        return new URL(fromUrl, video).toExternalForm();
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        context.showKangNaDialog();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        stepVideo(new URL(s).toExternalForm());
                        context.dismissKangNaDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        context.dismissKangNaDialog();
                        T.t(throwable.getMessage());
                    }
                });
    }

    private void stepVideo(String url) {
        SimpleVideo.Video video = new SimpleVideo.Video();
        video.url = url;
        video.name = "超清视频";
        List<SimpleVideo.Video> videos = new ArrayList<>();
        videos.add(video);

        File videoFilePath = Keeper.getF().getScopeFile(Files.Scope.VIDEO_RAW);
        simpleVideo.setUp(videos, true, videoFilePath, "荷尔蒙");

        SimpleDraweeView draweeView = new SimpleDraweeView(context);
        simpleVideo.setThumbImageView(draweeView);
        draweeView.setImageURI("http:" + mSource.getImg());


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

    }

    private void loadPhoto(YiyiBox.DataBean.ItemsBean source) {
        simpleVideo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        String url = "http://www.yiyibox.com/photo/" + source.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("referer", "http://www.yiyibox.com")
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
                        Map<String, Integer> imgMap = new HashMap<>();
                        Pattern p = Pattern.compile(YiyiBoxPhotoWaiter.REGEX_TAG_IMG, Pattern.CASE_INSENSITIVE);
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
                        URL fromUrl = new URL("http://www.yiyibox.com");
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            String img = strings.get(i);
                            strings.set(i, new URL(fromUrl, img).toExternalForm());
                        }
                        return strings;
                    }
                })
                .map(new Function<List<String>, List<PreviewPicWaiter.PreviewBean>>() {
                    @Override
                    public List<PreviewPicWaiter.PreviewBean> apply(@NonNull List<String> strings) throws Exception {
                        List<PreviewPicWaiter.PreviewBean> pics = new ArrayList<>();
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            PreviewPicWaiter.PreviewBean bean = new PreviewPicWaiter.PreviewBean();
                            bean.uri = Frescoer.uri(strings.get(i), ImageSource.SOURCE_NET);
                            pics.add(bean);
                        }
                        return pics;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        context.showKangNaDialog();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PreviewPicWaiter.PreviewBean>>() {
                    @Override
                    public void accept(List<PreviewPicWaiter.PreviewBean> picBeans) throws Exception {
                        mAdapter.setData(picBeans);
                        context.dismissKangNaDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        context.dismissKangNaDialog();
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
}
