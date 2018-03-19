package com.lpzahd.essay.context.video.waiter;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.essay.context.video.VideoActivity;
import com.lpzahd.gallery.tool.MediaTool;
import com.lpzahd.waiter.consumer.State;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lpzahd
 * @describe
 * @time 2018/3/12 17:01
 * @change
 */
public class VideoWaiter extends ToneActivityWaiter<VideoActivity> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.video_full_container)
    FrameLayout videoFullContainer;

    private GSYVideoHelper mVideoHelper;

    private VideoAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private DspRefreshWaiter<MediaTool.VideoBean, Video> refreshWaiter;

    private int mFirstVisibleItem;
    private int mLastVisibleItem;

    public VideoWaiter(VideoActivity videoActivity) {
        super(videoActivity);
    }

    @Override
    protected void initView() {
        super.initView();

        mLinearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new VideoAdapter(context) {
            private int mDeleteTimes;

            @Override
            public void onSwiped(final int position) {
                if(mPlayPosition == position) return;

                final List<Video> data = getData();
                if(data.isEmpty()) return;

                final List<MediaTool.VideoBean> videoList = refreshWaiter.getSource();
                final MediaTool.VideoBean videoBean = videoList.get(position);
                final Video video = data.get(position);

                videoList.remove(position);
                remove(position);

                // 最多提醒三次
                if(mDeleteTimes > 3) {
                    Files.delete(videoBean.getOriginalPath());
                    Ui.scanSingleMedia(context, new File(videoBean.getOriginalPath()));
                    return;
                }


                String name = new File(videoBean.getOriginalPath()).getName();
                new MaterialDialog.Builder(context)
                        .title("删除视频")
                        .content("视频" + name + "将被移除，确定？")
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .negativeText(R.string.tip_negative)
                        .positiveText(R.string.tip_positive)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                mDeleteTimes++;
                                Files.delete(videoBean.getOriginalPath());
                                Ui.scanSingleMedia(context, new File(videoBean.getOriginalPath()));

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                videoList.add(position, videoBean);
                                add(position, video);
                            }
                        })
                        .show();
            }
        };
        recyclerView.setAdapter(mAdapter);

        ToneItemTouchHelperCallback touchCallback = new ToneItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(touchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                //大于0说明有播放,//对应的播放列表TAG
                if (mVideoHelper.getPlayPosition() >= 0 && mVideoHelper.getPlayTAG().equals(VideoAdapter.TAG)) {
                    //当前播放的位置
                    int position = mVideoHelper.getPlayPosition();
                    //不可视的是时候
                    if ((position < mFirstVisibleItem || position > mLastVisibleItem)) {
                        //如果是小窗口就不需要处理
                        if (!mVideoHelper.isSmall() && !mVideoHelper.isFull()) {
                            //小窗口
                            int size = Ui.dip2px(context, 150);
                            //actionbar为true才不会掉下面去
                            mVideoHelper.showSmallVideo(new Point(size, size), true, true);
                        }
                    } else {
                        if (mVideoHelper.isSmall()) {
                            mVideoHelper.smallVideoToNormal();
                        }
                    }
                }
            }
        });

        mVideoHelper = new GSYVideoHelper(context, new NormalGSYVideoPlayer(context));
        mVideoHelper.setFullViewContainer(videoFullContainer);

        //配置
        GSYVideoHelper.GSYVideoHelperBuilder videoBuilder = new GSYVideoHelper.GSYVideoHelperBuilder();
        videoBuilder
                .setHideActionBar(true)
                .setHideStatusBar(true)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
                .setShowFullAnimation(true)
                .setLockLand(true).setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                L.e("Duration " + mVideoHelper.getGsyVideoPlayer().getDuration() + " CurrentPosition " + mVideoHelper.getGsyVideoPlayer().getCurrentPositionWhenPlaying());
            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {
                super.onQuitSmallWidget(url, objects);
                //大于0说明有播放,//对应的播放列表TAG
                if (mVideoHelper.getPlayPosition() >= 0 && mVideoHelper.getPlayTAG().equals(VideoAdapter.TAG)) {
                    //当前播放的位置
                    int position = mVideoHelper.getPlayPosition();
                    //不可视的是时候
                    if ((position < mFirstVisibleItem || position > mLastVisibleItem)) {
                        //释放掉视频
                        mVideoHelper.releaseVideoPlayer();
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        mVideoHelper.setGsyVideoOptionBuilder(videoBuilder);

        mAdapter.setVideoHelper(mVideoHelper, videoBuilder);
    }

    @Override
    protected void initData() {
        super.initData();

        addWindowWaiter(refreshWaiter = new DspRefreshWaiter<MediaTool.VideoBean, Video>(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<List<MediaTool.VideoBean>> doRefresh(int page) {
                return Flowable.create(new FlowableOnSubscribe<List<MediaTool.VideoBean>>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<List<MediaTool.VideoBean>> e) throws Exception {
                        List<MediaTool.VideoBean> imageBeanList = MediaTool.getVideoFromContext(context);
                        e.onNext(imageBeanList);
                    }
                }, BackpressureStrategy.BUFFER)
                        .subscribeOn(Schedulers.computation());
            }

            @Override
            public Video process(MediaTool.VideoBean videoBean) {
                Video video = new Video();
                video.name = videoBean.getTitle();
                video.url = videoBean.getOriginalPath();
                return video;
            }
        });
        refreshWaiter.setCount(Integer.MAX_VALUE);
        refreshWaiter.autoRefresh();
    }

    @Override
    protected int backPressed() {
        if (mVideoHelper.backFromFull()) {
            return State.STATE_PREVENT;
        }

        return super.backPressed();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mVideoHelper.releaseVideoPlayer();
        GSYVideoManager.releaseAllVideos();
    }

    private static class Video {
        String name;
        String url;
    }

    static class VideoHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.video_layout)
        FrameLayout videoLayout;

        @BindView(R.id.video_btn)
        AppCompatImageView videoBtn;

        SimpleDraweeView coverDraweeView;

        VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            coverDraweeView = new SimpleDraweeView(itemView.getContext());
        }
    }

    private static class VideoAdapter extends ToneAdapter<Video, VideoHolder> {

        public static final String TAG = "com.lpzahd.essay.context.video.waiter.VideoWaiter.VideoAdapter";

        private GSYVideoHelper mHelper;
        private GSYVideoHelper.GSYVideoHelperBuilder mBuilder;

        int mPlayPosition = -1;

        VideoAdapter(Context context) {
            super(context);
        }

        void setVideoHelper(GSYVideoHelper helper, GSYVideoHelper.GSYVideoHelperBuilder builder) {
            mHelper = helper;
            mBuilder = builder;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final VideoHolder holder = new VideoHolder(inflateItemView(R.layout.item_video, parent));
            holder.videoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    mHelper.setPlayPositionAndTag(position, TAG);
                    Video video = getItem(position);
                    mBuilder.setVideoTitle(video.name).setUrl(video.url);
                    mHelper.startPlay();
                    mPlayPosition = position;
                    notifyDataSetChanged();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            Video video = getItem(position);
            holder.coverDraweeView.setImageURI(Frescoer.uri(video.url, ImageSource.SOURCE_FILE));
            mHelper.addVideoPlayer(position, holder.coverDraweeView, TAG, holder.videoLayout, holder.videoBtn);
        }
    }
}
