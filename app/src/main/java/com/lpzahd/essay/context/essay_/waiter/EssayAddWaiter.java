package com.lpzahd.essay.context.essay_.waiter;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.view.DraweeForm;

import butterknife.BindView;
import io.realm.Realm;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 */
public class EssayAddWaiter extends ToneActivityWaiter<EssayAddActivity> implements DraweeForm.DraweeClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title_edt)
    AppCompatEditText titleEdt;

    @BindView(R.id.content_edt)
    AppCompatEditText contentEdt;

    @BindView(R.id.drawee_form_view)
    DraweeForm draweeFormView;

    private Realm realm;

//    private DataFactory<MediaBean, DraweeForm.Photo> mpFactory = DataFactory.of(new DataFactory.DataProcess<MediaBean, DraweeForm.Photo>() {
//        @Override
//        public DraweeForm.Photo process(MediaBean mediaBean) {
//            return mediaBean == null ? null : new DraweeForm.Photo(mediaBean.getOriginalPath(), mediaBean.getWidth(), mediaBean.getHeight());
//        }
//    });
//
//    private List<MediaBean> medias;


    public EssayAddWaiter(EssayAddActivity essayAddActivity) {
        super(essayAddActivity);
    }

    @Override
    protected void setContentView() {
        realm = Realm.getDefaultInstance();
        setupToolbar();
        draweeFormView.setDraweeClickListener(this);
    }

    private void setupToolbar() {
        toolbar.setTitle("者乎");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void destroy() {
        super.destroy();
        if(!realm.isClosed())
            realm.close();
    }

    @Override
    public void click(DraweeForm parent, SimpleDraweeView view, int index) {

    }

//    @OnClick({R.id.pic_drawee_view, R.id.media_drawee_view, R.id.add_essay_btn})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.pic_drawee_view:
//                openGallery();
//                break;
//            case R.id.media_drawee_view:
//                break;
//            case R.id.add_essay_btn:
//                showAddDialog();
//                break;
//        }
//    }

    /**
     * 打开相册
     */
//    private void openGallery() {
//        RxGalleryFinal.with(context)
//                .image()
//                .maxSize(12)
//                .imageLoader(ImageLoaderType.FRESCO)
//                .subscribe(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
//                    @Override
//                    protected void onEvent(ImageMultipleResultEvent result) throws Exception {
//                        result.getResult();
//                        medias = result.getResult();
//                        draweeFormView.update(mpFactory.processArray(medias));
//                    }
//                })
//                .openGallery();
//    }

    /**
     * 提交
     */
//    private void showAddDialog() {
//        new MaterialDialog.Builder(context)
//                .title("新增")
//                .content("准备好了么")
//                .negativeText(R.string.tip_negative)
//                .positiveText(R.string.tip_positive)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(MaterialDialog dialog, DialogAction which) {
//                        addEssay();
//                        RefreshBusWaiter.post();
//                        finish();
//                    }
//                })
//                .show();
//    }

//    private void addEssay() {
//        realm.beginTransaction();
//        Essay essay = realm.createObject(Essay.class);
//        essay.setTitle(titleEdt.getText().toString());
//        essay.setContent(contentEdt.getText().toString());
//        EFile eFile = new EFile();
//        RealmList<Image> images = new RealmList<>();
//        images.addAll(miFactory.processArray(medias));
//        eFile.setImages(images);
//        essay.seteFile(eFile);
//        realm.commitTransaction();
//    }

//    private DataFactory<MediaBean, Image> miFactory = DataFactory.of(new DataFactory.DataProcess<MediaBean, Image>() {
//        @Override
//        public Image process(MediaBean mediaBean) {
//            if (mediaBean == null) return null;
//
//            Image image = new Image();
//            image.setPath(mediaBean.getOriginalPath());
//            image.setWidth(mediaBean.getWidth());
//            image.setHeight(mediaBean.getHeight());
//            image.setSource(com.lpzahd.Image.SOURCE_FILE);
//            image.setSuffix(mediaBean.getMimeType());
//            return image;
//        }
//    });

//    @Override
//    protected boolean backPressed() {
//        if(checkNotNull()) return super.backPressed();
//
//        new MaterialDialog.Builder(context)
//                .title("警告")
//                .content("放弃编辑的内容")
//                .negativeText(R.string.tip_negative)
//                .positiveText(R.string.tip_positive)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(MaterialDialog dialog, DialogAction which) {
//                        finish();
//                    }
//                })
//                .show();
//
//        return false;
//    }

//    private boolean checkNotNull() {
//        return Strings.empty(titleEdt.getText()) || Strings.empty(contentEdt.getText()) || Lists.empty(medias);
//    }
}
