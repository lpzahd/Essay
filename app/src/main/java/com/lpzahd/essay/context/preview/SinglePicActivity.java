package com.lpzahd.essay.context.preview;

import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
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

    private Transmitter<String> mTransmitter;

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
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                                .setUri(s)
                                .build();
                        zoomableDraweeView.setController(draweeController);
                    }
                });
    }
}
