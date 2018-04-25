package com.lpzahd.essay.context.`fun`

import android.content.Context
import android.content.Intent
import com.lpzahd.atool.ui.L
import com.lpzahd.common.bus.Bus
import com.lpzahd.common.bus.Receiver
import com.lpzahd.common.bus.RxBus
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.tone.activity.RxActivity
import com.lpzahd.essay.context.`fun`.waiter.ApplicationInfoWaiter
import com.lpzahd.essay.context.collection.waiter.CollectionEditWaiter
import io.reactivex.Flowable

/**
 * 功能容器Activity
 */
class FunctionsFrameActivity() : RxActivity() {

    companion object {

        val TAG = "com.lpzahd.essay.context.fun.FunctionsFrameActivity"

        fun startActivity(context: Context) {
            val intent = Intent(context, FunctionsFrameActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun init() {
        RxTaxi.get().pull<Int>(TAG).transmit().subscribe({
            when(it) {
                0 -> addActivityWaiter(ApplicationInfoWaiter(this))
//                1 -> addActivityWaiter(CollectionEditWaiter(null))
//                2 -> addActivityWaiter(CollectionEditWaiter(null))
                else -> addActivityWaiter(ApplicationInfoWaiter(this))
            }
        })
    }

}