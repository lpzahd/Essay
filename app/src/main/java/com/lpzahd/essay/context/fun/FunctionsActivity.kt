package com.lpzahd.essay.context.`fun`

import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import com.lpzahd.common.tone.activity.RxActivity
import com.lpzahd.essay.R
import com.lpzahd.essay.context.`fun`.waiter.FunctionsWaiter

class FunctionsActivity : RxActivity() {

    override fun init() {
        super.init()
        addActivityWaiter(FunctionsWaiter(this))
    }

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.activity_collection)

        val tooBar = findViewById<Toolbar>(R.id.tool_bar);
        tooBar.title = "功能"
        setSupportActionBar(tooBar)

    }

    companion object {

        val TAG: String = "com.lpzahd.essay.context.fun.FunctionsActivity"

        fun startActivity(context: Context) {
            val intent = Intent(context,FunctionsActivity::class.java)
            context.startActivity(intent);
        }

    }
}
