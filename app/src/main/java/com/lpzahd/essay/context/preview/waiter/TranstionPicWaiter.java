package com.lpzahd.essay.context.preview.waiter;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeTransition;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.TranstionPicActivity;
import com.lpzahd.fresco.zoomable.AbstractAnimatedZoomableController;
import com.lpzahd.fresco.zoomable.DefaultZoomableController;
import com.lpzahd.fresco.zoomable.DoubleTapGestureListener;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;
import com.lpzahd.waiter.consumer.State;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/23 16:07
 * @change
 */
public class TranstionPicWaiter extends ToneActivityWaiter<TranstionPicActivity> {

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    private Transmitter<Uri> mTransmitter;

    public TranstionPicWaiter(TranstionPicActivity transtionPicActivity) {
        super(transtionPicActivity);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TranstionPicActivity.TAG)) != null);
    }

    @Override
    protected void initView() {
        super.initView();

        ViewCompat.setTransitionName(zoomableDraweeView, TranstionPicActivity.SHARE_ELEMENT_NAME);

        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                context.onBackPressed();
                return true;
            }

        });
        zoomableDraweeView.setIsLongpressEnabled(true);
    }

    @Override
    protected void initData() {
        mTransmitter.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {
                        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                                .setOldController(zoomableDraweeView.getController())
                                .setUri(uri)
                                .setAutoPlayAnimations(true)
                                .setTapToRetryEnabled(true)
                                .build();
                        zoomableDraweeView.setController(draweeController);

                        ScalingUtils.ScaleType toScaleType = ScalingUtils.ScaleType.FIT_CENTER;
//                        PointF toFocusPoint = new PointF(0.5f, 0);
//
//                        zoomableDraweeView.getHierarchy().setActualImageScaleType(toScaleType);
//                        zoomableDraweeView.getHierarchy().setActualImageFocusPoint(toFocusPoint);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ScalingUtils.ScaleType fromScaleType = ScalingUtils.ScaleType.FOCUS_CROP;
                            context.getWindow()
                                    .setSharedElementEnterTransition(
                                            DraweeTransition.createTransitionSet(
                                                    fromScaleType, toScaleType));
                            context.getWindow()
                                    .setSharedElementReturnTransition(
                                            DraweeTransition.createTransitionSet(
                                                    toScaleType, fromScaleType));
                        }
                    }
                });
    }

    @Override
    protected int backPressed() {
        // 检查状态是否还原
        AbstractAnimatedZoomableController zc =
                (AbstractAnimatedZoomableController) zoomableDraweeView.getZoomableController();

        if(zc.getScaleFactor() != zc.getMinScaleFactor()) {
            PointF vp = new PointF(0,0);
            PointF ip = zc.mapViewToImage(vp);
            zc.zoomToPoint(
                    zc.getMinScaleFactor(),
                    ip,
                    vp,
                    DefaultZoomableController.LIMIT_ALL,
                    300L,
                    null);
            return State.STATE_FALSE;
        }
        return super.backPressed();
    }

}
