package com.lpzahd.gallery.waiter.multi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.fragment.ToneDialogFragment;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.gallery.R;
import com.lpzahd.gallery.R2;
import com.lpzahd.gallery.context.GalleryActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class BucketPresenter extends ToneActivityWaiter<GalleryActivity> {

    public BucketPresenter(GalleryActivity context) {
        super(context);
    }

    public void showDialog(String title, ArrayList<BucketBean> data) {
        showDialog(title, data, null);
    }

    public void showDialog(String title, ArrayList<BucketBean> data, OnBucketClickListener listener) {
        BucketDialog dialog = BucketDialog.newInstance(title, data);
        dialog.setOnBucketClickListener(listener);
        dialog.show(context);
    }

    public interface OnBucketClickListener {
        void click(BucketDialog dialog, int position, BucketBean bucket);
    }

    public static class BucketDialog extends ToneDialogFragment {

        private static String BUNDLE_EXTRA_TITLE = "bundle_extra_title";
        private static String BUNDLE_EXTRA_BUCKETS = "bundle_extra_buckets";
        public static String TAG = "com.lpzahd.gallery.presenter.multi.BucketPresenter.BucketDialog.TAG";

        Unbinder unbinder;

        @BindView(R2.id.title_tv)
        AppCompatTextView titleTv;

        @BindView(R2.id.recycler_view)
        RecyclerView recyclerView;

        private BucketAdapter mAdapter;
        private OnBucketClickListener mListener;

        public void setOnBucketClickListener(OnBucketClickListener listener) {
            mListener = listener;
        }

        public static BucketDialog newInstance(String title, ArrayList<BucketBean> buckets) {
            BucketDialog dialog = new BucketDialog();
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_EXTRA_TITLE, title);
            bundle.putParcelableArrayList(BUNDLE_EXTRA_BUCKETS, buckets);
            dialog.setArguments(bundle);
            return dialog;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            View rootView = inflater.inflate(R.layout.dialog_bucket, container);
            unbinder = ButterKnife.bind(this, rootView);

            final Context context = getContext();
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false));
            recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<BucketHolder>(recyclerView) {
                @Override
                public void onClick(RecyclerView rv, BucketHolder bucketHolder) {
                    if(mListener != null) {
                        final int position = bucketHolder.getAdapterPosition();
                        mListener.click(BucketDialog.this, position, mAdapter.getItem(position));
                    }
                }
            });

            mAdapter = new BucketAdapter(context);
            recyclerView.setAdapter(mAdapter);

            Bundle bundle = getArguments();
            String title = bundle.getString(BUNDLE_EXTRA_TITLE, "所有图片");
            titleTv.setText(title);

            ArrayList<BucketBean> data = bundle.getParcelableArrayList(BUNDLE_EXTRA_BUCKETS);
            mAdapter.setData(data);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            setWidthPercent(0.9f);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

    }

    public static class BucketBean implements Parcelable {

        private String id;
        private Uri uri;
        private int num;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeParcelable(this.uri, flags);
            dest.writeInt(this.num);
            dest.writeString(this.name);
        }

        public BucketBean() {
        }

        protected BucketBean(Parcel in) {
            this.id = in.readString();
            this.uri = in.readParcelable(Uri.class.getClassLoader());
            this.num = in.readInt();
            this.name = in.readString();
        }

        public static final Parcelable.Creator<BucketBean> CREATOR = new Parcelable.Creator<BucketBean>() {
            @Override
            public BucketBean createFromParcel(Parcel source) {
                return new BucketBean(source);
            }

            @Override
            public BucketBean[] newArray(int size) {
                return new BucketBean[size];
            }
        };
    }

    static class BucketHolder extends ToneAdapter.ToneHolder {

        @BindView(R2.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        @BindView(R2.id.num_tv)
        AppCompatTextView numTv;

        @BindView(R2.id.name_tv)
        AppCompatTextView nameTv;

        public BucketHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class BucketAdapter extends ToneAdapter<BucketBean, BucketHolder> {

        private int size;

        public BucketAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 56);
        }

        @Override
        public BucketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BucketHolder(inflateItemView(R.layout.item_bucket_dialog, parent));
        }

        @Override
        public void onBindViewHolder(BucketHolder holder, int position) {
            BucketBean bean = getItem(position);
            holder.numTv.setText(String.valueOf(bean.num));
            holder.nameTv.setText(bean.name);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                    .setResizeOptions(new ResizeOptions(size, size))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            holder.imageDraweeView.setController(controller);
        }
    }
}
