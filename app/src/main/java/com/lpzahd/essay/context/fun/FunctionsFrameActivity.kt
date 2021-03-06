package com.lpzahd.essay.context.`fun`

import android.content.Context
import android.content.Intent
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.tone.activity.RxActivity
import com.lpzahd.essay.context.`fun`.waiter.ApplicationsWaiter

/**
 * 功能容器Activity
 */
class FunctionsFrameActivity() : RxActivity() {

    companion object {

        const val TAG = "com.lpzahd.essay.context.fun.FunctionsFrameActivity"

        fun startActivity(context: Context) {
            val intent = Intent(context, FunctionsFrameActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun init() {
        RxTaxi.get().pull<Int>(TAG).transmit().subscribe({
            when(it) {
                0 -> addActivityWaiter(ApplicationsWaiter(this))
//                1 -> addActivityWaiter(CollectionEditWaiter(null))
//                2 -> addActivityWaiter(CollectionEditWaiter(null))
                else -> addActivityWaiter(ApplicationsWaiter(this))
            }
        })
    }

}