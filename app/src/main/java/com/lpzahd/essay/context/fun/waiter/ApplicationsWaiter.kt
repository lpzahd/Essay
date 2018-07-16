package com.lpzahd.essay.context.`fun`.waiter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.SystemClock
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.producers.Consumer
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.lpzahd.atool.keeper.Bitmaps
import com.lpzahd.atool.ui.L
import com.lpzahd.atool.ui.Ui
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.taxi.Taxi
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener
import com.lpzahd.common.tone.adapter.ToneAdapter
import com.lpzahd.common.tone.waiter.ToneActivityWaiter
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter
import com.lpzahd.essay.R
import com.lpzahd.essay.context.`fun`.FunctionDetailActivity
import com.lpzahd.essay.context.`fun`.FunctionsFrameActivity
import com.lpzahd.essay.exotic.fresco.FrescoInit
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import okhttp3.CacheControl
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.util.HashMap
import java.util.concurrent.Executor

class ApplicationsWaiter(activity: FunctionsFrameActivity) : ToneActivityWaiter<FunctionsFrameActivity>(activity) {

    private lateinit var mAdapter: ApplicationAdapter
    private lateinit var mDspRefreshWaiter: DspRefreshWaiter<AppInfo, AppInfo>
    private val mTaxi: Taxi = RxTaxi.get();

    override fun setContentView() {
        context.setContentView(R.layout.layout_common_refresh_activity)
    }

    override fun initToolBar() {
        super.initToolBar()
        val toolbar = find<Toolbar>(R.id.tool_bar)
        toolbar.title = "应用"
        context.setSupportActionBar(toolbar)
    }

    override fun initView() {
        val swipeRefreshLayout: SwipeRefreshLayout = find(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent)
        val recyclerView: RecyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
                with(GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)) {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (mAdapter.getItemViewType(position) == ApplicationAdapter.LINE) spanCount else 1;
                        }

                    }
                    this
                }

        mAdapter = ApplicationAdapter(context);
        recyclerView.adapter = mAdapter;

        recyclerView.addOnItemTouchListener(object : OnItemHolderTouchListener<ToneAdapter.ToneHolder>(recyclerView) {
            override fun onClick(rv: RecyclerView?, holder: ToneAdapter.ToneHolder?) {
                val position = holder!!.adapterPosition

                if (mAdapter.getItemViewType(position) == ApplicationAdapter.VIEW) {
                    mTaxi.regist(ApplicationDetailWaiter.TAG, { Flowable.just(mAdapter.getItem(position).pkg) })
                    mTaxi.regist(FunctionDetailActivity.TAG, { Flowable.just(0) })
                    FunctionDetailActivity.startActivity(context)
                }

            }
        })

        mDspRefreshWaiter = object : DspRefreshWaiter<AppInfo, AppInfo>(swipeRefreshLayout, recyclerView) {

            private var packageManager: PackageManager = context.packageManager

            override fun doRefresh(page: Int): Flowable<MutableList<AppInfo>> {
                return Flowable.just(page)
                        .subscribeOn(Schedulers.io())
                        .map { packageManager.getInstalledPackages(0) }
                        .map { packageInfos ->
                            val persons = mutableListOf<PackageInfo>()
                            val systems = mutableListOf<PackageInfo>()
                            packageInfos.forEach {
                                val appInfo = it!!.applicationInfo
                                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) <= 0) {
                                    // 用户应用
                                    persons.add(it)
                                } else {
                                    // 系统应用
                                    systems.add(it)
                                }
                            }
                            with(mutableListOf<AppInfo>()) {
                                add(AppInfo(ApplicationAdapter.LINE, "个人应用"))
                                persons.forEach { add(convert(it)) }
                                add(AppInfo(ApplicationAdapter.LINE, "系统应用"))
                                systems.forEach { add(convert(it)) }
                                this
                            }
                        }
            }

            fun convert(packageInfo: PackageInfo?): AppInfo {
                val appInfo = packageInfo!!.applicationInfo
                var iconUri: Uri = Uri.parse("http://" + appInfo.packageName)
//                var iconUri: Uri = Uri.EMPTY
//                val icon = appInfo.loadIcon(packageManager)
//                if (icon is BitmapDrawable) {
//                    val iconBase64 = Bitmaps.toBase64(icon.bitmap)
//                    iconUri = Uri.parse("data:image/png;base64,".plus(iconBase64))
//                }
                val type: AppType
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    // 用户应用
                    type = AppType.PERSON;
                } else {
                    // 系统应用
                    type = AppType.SYSTEM;
                }
                return AppInfo(ApplicationAdapter.VIEW,
                        appInfo.loadLabel(packageManager).toString(),
                        appInfo.packageName,
                        type,
                        iconUri)
            }

            override fun process(appInfo: AppInfo): AppInfo {
                return appInfo
            }

        }

        mDspRefreshWaiter.setCount(Integer.MAX_VALUE)
        mDspRefreshWaiter.autoRefresh()
        addWindowWaiter(mDspRefreshWaiter)
    }

    override fun destroy() {
        super.destroy()
        mTaxi.unregist(FunctionDetailActivity.TAG)
        mTaxi.unregist(ApplicationDetailWaiter.TAG)
    }

    override fun start() {
        super.start()
        FrescoInit.get().setFetcher(IconFetcher(context.packageManager, FrescoInit.get().okHttpClient.dispatcher().executorService()))
    }

    override fun stop() {
        super.stop()
        FrescoInit.get().resetOkHttpNetworkFetcher()
    }
}

private class IconFetcher(private val manager: PackageManager, private val mCancellationExecutor: Executor) : NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

    override fun createFetchState(consumer: Consumer<EncodedImage>, context: ProducerContext): OkHttpNetworkFetcher.OkHttpNetworkFetchState {
        return OkHttpNetworkFetcher.OkHttpNetworkFetchState(consumer, context)
    }

    override fun fetch(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState, callback: NetworkFetcher.Callback) {
        fetchState.submitTime = SystemClock.elapsedRealtime()
        val uri = fetchState.uri

        try {
            val packageName = uri.host
            L.e("uri $uri packageName $packageName")
            val applicationInfo = manager.getApplicationInfo(packageName, 0)

            mCancellationExecutor.execute {
                fetchState.responseTime = SystemClock.elapsedRealtime()
                val drawable = applicationInfo.loadIcon(manager)
                val bitmap = Bitmaps.drawable2Bitmap(drawable)
                callback.onResponse(Bitmaps.bitmap2InputStream(bitmap),  bitmap.byteCount)
            }
        } catch ( e: PackageManager.NameNotFoundException) {
            callback.onFailure(e)
        }

    }

    override fun shouldPropagate(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState): Boolean {
        return true
    }

    override fun onFetchCompletion(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState, byteSize: Int) {
        fetchState.fetchCompleteTime = SystemClock.elapsedRealtime()
    }

    override fun getExtraMap(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState, byteSize: Int): Map<String, String>? {
        val extraMap = HashMap<String, String>(4)
        extraMap[QUEUE_TIME] = java.lang.Long.toString(fetchState.responseTime - fetchState.submitTime)
        extraMap[FETCH_TIME] = java.lang.Long.toString(fetchState.fetchCompleteTime - fetchState.responseTime)
        extraMap[TOTAL_TIME] = java.lang.Long.toString(fetchState.fetchCompleteTime - fetchState.submitTime)
        extraMap[IMAGE_SIZE] = Integer.toString(byteSize)
        return extraMap
    }

    companion object {

        private val QUEUE_TIME = "queue_time"
        private val FETCH_TIME = "fetch_time"
        private val TOTAL_TIME = "total_time"
        private val IMAGE_SIZE = "image_size"
    }
}

enum class AppType {
    PERSON, SYSTEM
}

data class AppInfo(val itemType: Int, val title: String, var pkg: String? = null, var type: AppType? = null, var uri: Uri? = null)

class TitleHolder(itemView: View) : ToneAdapter.ToneHolder(itemView) {
    val titleTv: AppCompatTextView = itemView.findViewById(R.id.title_tv)
}

class ApplicationHolder(itemView: View) : ToneAdapter.ToneHolder(itemView) {
    val appDraweeView: SimpleDraweeView = itemView.findViewById(R.id.app_drawee_view)
    val appTv: AppCompatTextView = itemView.findViewById(R.id.app_tv)
}

class ApplicationAdapter(context: Context) : ToneAdapter<AppInfo, ToneAdapter.ToneHolder>(context) {

    companion object {
        const val LINE = 0
        const val VIEW = 1
    }

    var size = 100;

    init {
        size = Ui.dip2px(context, 48);
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToneAdapter.ToneHolder {
        return when (viewType) {
            LINE -> TitleHolder(inflateItemView(R.layout.item_application_line, parent))
            VIEW -> ApplicationHolder(inflateItemView(R.layout.item_application_info, parent))
            else -> throw AssertionError("无法解析的类型！")
        }
    }

    override fun onBindViewHolder(holder: ToneAdapter.ToneHolder, position: Int) {
        val bean = data.get(position);

        when (getItemViewType(position)) {
            LINE -> {
                holder as TitleHolder;
                holder.titleTv.text = bean.title
            }
            VIEW -> {
                holder as ApplicationHolder
                val request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                        .setResizeOptions(ResizeOptions(size, size))
                        .build()
                val controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.appDraweeView.getController())
                        .setImageRequest(request)
                        .setAutoPlayAnimations(true)
                        .setTapToRetryEnabled(true)
                        .build()
                holder.appDraweeView.setController(controller)
                holder.appTv.text = bean.title;
            }
        }

    }

}