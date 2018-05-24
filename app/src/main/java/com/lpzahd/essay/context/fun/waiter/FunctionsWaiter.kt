package com.lpzahd.essay.context.`fun`.waiter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.lpzahd.atool.ui.Ui
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.taxi.Taxi
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener
import com.lpzahd.common.tone.adapter.ToneAdapter
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback
import com.lpzahd.common.tone.waiter.ToneActivityWaiter
import com.lpzahd.common.util.fresco.Frescoer
import com.lpzahd.essay.R
import com.lpzahd.essay.context.`fun`.FunctionsActivity
import com.lpzahd.essay.context.`fun`.FunctionsFrameActivity
import io.reactivex.Flowable

class FunctionsWaiter(context: FunctionsActivity) : ToneActivityWaiter<FunctionsActivity>(context) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: FuncationsAdapter;

    private val mTaxi: Taxi = RxTaxi.get();

    override fun initView() {
        super.initView()
        recyclerView = rootView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false);

        mAdapter = FuncationsAdapter(context);

        val touchCallback = ToneItemTouchHelperCallback(mAdapter)
        touchCallback.setCanDrag(false)
        touchCallback.setCanSwipe(true)
        ItemTouchHelper(touchCallback).attachToRecyclerView(recyclerView)

        recyclerView.adapter = mAdapter;

        recyclerView.addOnItemTouchListener(object : OnItemHolderTouchListener<FunctionsHolder>(recyclerView) {
            override fun onClick(rv: RecyclerView?, t: FunctionsHolder?) {
                mTaxi.regist(FunctionsFrameActivity.TAG, { Flowable.just(t?.adapterPosition) })
                FunctionsFrameActivity.startActivity(context)
            }
        })

    }

    override fun initData() {
        val datas = with(mutableListOf<Funcations>()) {
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "应用信息"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "汇率"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))

            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))

            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "待定"))
            add(Funcations(Frescoer.res(R.mipmap.ic_hang_tag), "给未来一段信息"))
            this
        }


        mAdapter.data = datas;
    }

    override fun destroy() {
        super.destroy()
        mTaxi.unregist(FunctionsFrameActivity.TAG)
    }
}

data class Funcations(var uri: Uri, var title: String)

class FunctionsHolder(itemView: View) : ToneAdapter.ToneHolder(itemView) {
    val picDraweeView: SimpleDraweeView = itemView.findViewById(R.id.pic_drawee_view)
    val titleTv: AppCompatTextView = itemView.findViewById(R.id.title_tv)
}


class FuncationsAdapter(context: Context) : ToneAdapter<Funcations, FunctionsHolder>(context) {

    var size = 100;

    init {
        size = Ui.dip2px(context, 72);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionsHolder {
        return FunctionsHolder(inflateItemView(R.layout.item_function, parent))
    }

    override fun onBindViewHolder(holder: FunctionsHolder, position: Int) {
        val bean = data.get(position);

        val request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                .setResizeOptions(ResizeOptions(size, size))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.picDraweeView.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build()
        holder.picDraweeView.setController(controller)
        holder.titleTv.text = bean.title;
    }

}
