package com.lpzahd.essay.context.`fun`

import android.content.Context
import android.content.Intent
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.tone.activity.RxActivity
import com.lpzahd.essay.context.`fun`.waiter.ApplicationDetailWaiter

class FunctionDetailActivity : RxActivity() {

    companion object {

        const val TAG = "com.lpzahd.essay.context.fun.FunctionDetailActivity"

        fun startActivity(context: Context) {
            val intent = Intent(context, FunctionDetailActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun init() {
        RxTaxi.get().pull<Int>(TAG).transmit().subscribe({
            when(it) {
                0 -> addActivityWaiter( ApplicationDetailWaiter(this))
//                1 -> addActivityWaiter(CollectionEditWaiter(null))
//                2 -> addActivityWaiter(CollectionEditWaiter(null))
                else -> addActivityWaiter(ApplicationDetailWaiter(this))
            }
        })
    }


}