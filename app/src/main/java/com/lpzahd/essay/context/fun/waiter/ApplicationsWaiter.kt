package com.lpzahd.essay.context.`fun`.waiter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.lpzahd.atool.keeper.Bitmaps
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
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

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

                var iconUri: Uri = Uri.EMPTY;
                val icon = appInfo.loadIcon(packageManager)
                if (icon is BitmapDrawable) {
                    val iconBase64 = Bitmaps.toBase64(icon.bitmap)
                    iconUri = Uri.parse("data:image/png;base64,".plus(iconBase64))
                }
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