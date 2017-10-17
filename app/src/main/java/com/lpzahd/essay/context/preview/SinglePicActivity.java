package com.lpzahd.essay.context.preview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.lpzahd.atool.ui.L;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;
import com.lpzahd.essay.exotic.fresco.FrescoInit;
import com.lpzahd.fresco.zoomable.DoubleTapGestureListener;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class SinglePicActivity extends RxActivity {

    public static final String TAG = "com.lpzahd.essay.context.preview.SinglePicActivity";

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    private Transmitter<BaiduPic.ImgsBean> mTransmitter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SinglePicActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_single_pic);
        ButterKnife.bind(this);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void initView() {
        super.initView();
//        zoomableDraweeView.setTapListener(new GestureDetector.SimpleOnGestureListener());
        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView));
    }

    @Override
    protected void initData() {
        mTransmitter.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaiduPic.ImgsBean>() {
                    @Override
                    public void accept(BaiduPic.ImgsBean been) throws Exception {
                        FrescoInit.get().changeReferer(been.getFromURL());
                        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                                .setUri(been.getObjURL())
                                .setControllerListener(controllerListener)
                                .build();
                        zoomableDraweeView.setController(draweeController);
                    }
                });
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
        public void onIntermediateImageSet(String id,  ImageInfo imageInfo) {
            L.e("Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            L.e("Error loading %s : " + throwable);
        }
    };

}
