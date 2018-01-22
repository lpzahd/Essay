package com.lpzahd.essay.context.preview.waiter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.waiter.FileDownloadWaiter;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;
import com.lpzahd.essay.context.preview.SinglePicActivity;
import com.lpzahd.essay.exotic.fresco.FrescoInit;
import com.lpzahd.essay.tool.OkHttpRxAdapter;
import com.lpzahd.fresco.zoomable.DoubleTapGestureListener;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class SinglePicWaiter extends ToneActivityWaiter<SinglePicActivity> {

    public static final String TAG = "com.lpzahd.essay.context.preview.waiter.SinglePicWaiter";

    private static final String REGEX_TAG_IMG = "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\.webp)\\b)[^>]*>";

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FileDownloadWaiter mFileDownloadWaiter;

    private BaiduPic.ImgsBean objBean;
    private PicBean firstBean;

    private PicBean displayBean;

    private PicAdapter mAdapter;

    private Transmitter<BaiduPic.ImgsBean> mTransmitter;

    public SinglePicWaiter(SinglePicActivity activity) {
        super(activity);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void init() {
        super.init();
        addWaiter(mFileDownloadWaiter = new FileDownloadWaiter(context));
    }

    @Override
    protected void initView() {
        super.initView();
//        zoomableDraweeView.setTapListener(new GestureDetector.SimpleOnGestureListener());
        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView) {
            @Override
            public void onLongPress(MotionEvent e) {
                mFileDownloadWaiter.downloadWithCheckFile(displayBean.uri.toString());
            }
        });

        zoomableDraweeView.setIsLongpressEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new PicAdapter(context);
        recyclerView.setAdapter(mAdapter);


        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ToneItemTouchHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<PicHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, PicHolder picHolder) {
                super.onClick(rv, picHolder);
                displayBean = mAdapter.getItem(picHolder.getAdapterPosition());

                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setOldController(zoomableDraweeView.getController())
                        .setUri(displayBean.uri)
                        .setTapToRetryEnabled(true)
                        .build();
                zoomableDraweeView.setController(draweeController);
            }

        });
    }

    @Override
    protected void initData() {
        mTransmitter.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaiduPic.ImgsBean>() {
                    @Override
                    public void accept(BaiduPic.ImgsBean been) throws Exception {
                        objBean = been;

                        FrescoInit.get().changeReferer(been.getFromURL());

                        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                                .setUri(been.getObjURL())
                                .build();
                        zoomableDraweeView.setController(draweeController);

                        firstBean = new PicBean();
                        firstBean.uri = Frescoer.uri(been.getObjURL(), ImageSource.SOURCE_NET);
                        displayBean = firstBean;

                        loadingHtmlPic(been);
                    }
                });
    }

    private void loadingHtmlPic(BaiduPic.ImgsBean bean) throws MalformedURLException {
        String fromUrlStr = bean.getFromURL();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fromUrlStr)
                .addHeader("referer", bean.getFromURLHost())
                .build();

        final URL fromUrl;
        if (fromUrlStr.startsWith("http://") || fromUrlStr.startsWith("https://") || fromUrlStr.startsWith("ftp://")) {
            fromUrl = new URL(fromUrlStr);
        } else {
            fromUrl = new URL("http://" + fromUrlStr);
        }

        Disposable htmlDisposable = OkHttpRxAdapter.adapter(client.newCall(request))
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
                        Pattern p = Pattern.compile(SinglePicWaiter.REGEX_TAG_IMG, Pattern.CASE_INSENSITIVE);
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
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            String img = strings.get(i);
                            strings.set(i, new URL(fromUrl, img).toExternalForm());
                        }
                        return strings;
                    }
                })
                .map(new Function<List<String>, List<PicBean>>() {
                    @Override
                    public List<PicBean> apply(@NonNull List<String> strings) throws Exception {
                        List<PicBean> pics = new ArrayList<>();
                        for (int i = 0, size = strings.size(); i < size; i++) {
                            PicBean bean = new PicBean();
                            bean.uri = Frescoer.uri(strings.get(i), ImageSource.SOURCE_NET);
                            pics.add(bean);
                        }
                        return pics;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PicBean>>() {
                    @Override
                    public void accept(List<PicBean> picBeans) throws Exception {
                        if (!mAdapter.getData().contains(firstBean)) {
                            picBeans.add(0, firstBean);
                        }
                        mAdapter.setData(picBeans);
                    }
                });

        context.addDispose(htmlDisposable);

    }

    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
                String id,
                ImageInfo imageInfo,
                Animatable anim) {
            if (imageInfo == null) {
                return;
            }
            QualityInfo qualityInfo = imageInfo.getQualityInfo();
            L.e("Final image received! " +
                            "Size %d x %d",
                    "Quality level %d, good enough: %s, full quality: %s",
                    imageInfo.getWidth(),
                    imageInfo.getHeight(),
                    qualityInfo.getQuality(),
                    qualityInfo.isOfGoodEnoughQuality(),
                    qualityInfo.isOfFullQuality());
        }

        @Override
        public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
            L.e("Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            L.e("Error loading %s : " + throwable);
        }
    };

    public static class PicBean {
        public Uri uri;

        @Override
        public boolean equals(Object obj) {
            if (obj == this || getClass() == obj.getClass()) return true;

            PicBean another = (PicBean) obj;
            return this.uri == another.uri || Strings.equals(this.uri.toString(), another.toString());
        }
    }

    public static class PicHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        PicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PicAdapter extends ToneAdapter<PicBean, PicHolder> {

        private int size;

        public PicAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 56);
        }

        @Override
        public PicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PicHolder(inflateItemView(R.layout.item_essay_add_pic, parent));
        }

        @Override
        public void onBindViewHolder(PicHolder holder, int position) {
            PicBean bean = getItem(position);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                    .setResizeOptions(new ResizeOptions(size, size))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .setTapToRetryEnabled(true)
                    .build();
            holder.imageDraweeView.setController(controller);
        }
    }

}
