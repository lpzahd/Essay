package com.lpzahd.essay.context.`fun`.waiter

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding2.view.RxView
import com.lpzahd.Codec
import com.lpzahd.Strings
import com.lpzahd.atool.keeper.Bitmaps
import com.lpzahd.atool.keeper.Files
import com.lpzahd.atool.keeper.Keeper
import com.lpzahd.atool.ui.Ui
import com.lpzahd.common.taxi.RxTaxi
import com.lpzahd.common.taxi.Transmitter
import com.lpzahd.common.tone.waiter.ToneActivityWaiter
import com.lpzahd.essay.R
import com.lpzahd.essay.context.`fun`.FunctionDetailActivity
import com.lpzahd.essay.tool.DateTime
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 应用详情页面
 */
class ApplicationDetailWaiter(activity: FunctionDetailActivity) : ToneActivityWaiter<FunctionDetailActivity>(activity) {

    private var mTransmitter: Transmitter<String>? = RxTaxi.get().pull<String>(TAG)

    private lateinit var toolbar: Toolbar
    private lateinit var fab: FloatingActionButton
    private lateinit var contentLayout: LinearLayoutCompat

    private lateinit var mPkg: String

    /**
     * 统一tag
     */
    companion object {
        const val TAG = "com.lpzahd.essay.context.fun.waiter.ApplicationDetailWaiter"

        const val MIN_LINES = 5;
    }

    /**
     * 控制视图展示
     */
    override fun setContentView() {
        context.setContentView(R.layout.activity_fun_detail)
    }

    /**
     * 检查是否参数
     */
    override fun checkArgus(intent: Intent?): Boolean {
        return super.checkArgus(intent) && mTransmitter != null
    }

    /**
     * 查找ui控件
     */
    override fun initView() {
        super.initView()

        toolbar = find(R.id.tool_bar)
        fab = find(R.id.fab)
        contentLayout = find(R.id.content_layout)

        RxView.clicks(fab)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe { showShareDialog() }
    }

    /**
     * rxbus拉取包名
     */
    override fun initData() {
        mTransmitter!!.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pkg ->
                    mPkg = pkg
                    inflateView(mPkg)
                }
    }

    /**
     * 显示分享dialog
     */
    private fun showShareDialog() {
        MaterialDialog.Builder(context)
                .title("分享")
                .content("你确定要分享该应用么？")
                .positiveText(R.string.tip_positive)
                .negativeText(R.string.tip_negative)
                .onPositive { _, _ -> shareApk(mPkg) }
                .show()
    }

    /**
     * 分享apk
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun shareApk(pkg:String) {
        context.rxAction(Observable.just(pkg)
                .subscribeOn(Schedulers.io())
                .map {
                    val manager = context.packageManager
                    val packageInfo = manager.getPackageInfo(it, PackageManager.GET_META_DATA xor PackageManager.GET_SIGNATURES)
                    val applicationInfo = packageInfo.applicationInfo

                    // 检查指定目录下是否存在对应的apk
                    val files = Keeper.getF()
                    val pkgFile = files.getFile(Files.Scope.FILE_APK, toPkgName(it))

                    if(!pkgFile.exists() || !pkgFile.isFile
                            || Strings.equals(Codec.md5Hex(pkgFile.absolutePath), Codec.md5Hex(packageInfo.signatures[0].toByteArray()))) {
                        // apk 尚未生成 或者 版本不匹配, 则 复制apk
                        Files.copy(applicationInfo.sourceDir, pkgFile.absolutePath)
                    }

                    pkgFile.absolutePath
                },
                { Ui.shareApk(context, File(it), "分享：" + toolbar.title) })

    }

    private fun toPkgName(pkg: String) : String {
        return pkg.replace('.', '_', true)
    }

    /**
     * 填充视图
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun inflateView(pkg: String) {
        val manager = context.packageManager
        val packageInfo = manager.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES xor
                PackageManager.GET_SERVICES xor PackageManager.GET_PERMISSIONS xor PackageManager.GET_SIGNATURES)

        val applicationInfo = packageInfo.applicationInfo
        val signature = packageInfo.signatures[0]

        toolbar.title = applicationInfo.loadLabel(manager)
        toolbar.logo = applicationInfo.loadIcon(manager).let {
            val dp48 = Ui.dip2px(context, 48)
            Bitmaps.zoomDrawable(context, it as BitmapDrawable?, dp48, dp48)
        }
        context.setSupportActionBar(toolbar)

        contentLayout.run {
            addView(add("包名", packageInfo.packageName))
            addView(add("版本号", packageInfo.versionCode.toString()))
            addView(add("版本名称", packageInfo.versionName))
            addView(add("MD5", Codec.md5Hex(signature.toByteArray())))
            addView(add("SHA1", Codec.shaHex(signature.toByteArray())))
            addView(add("size", Files.formatFileLength(applicationInfo.sourceDir)))
            addView(add("存储位置", if ((applicationInfo.flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) "内存" else "SD卡"))
            addView(add("安装日期", DateTime.format(packageInfo.firstInstallTime)))
            addView(add("更新日期", DateTime.format(packageInfo.lastUpdateTime)))
            addView(add("使用天数", ((DateTime.now() - packageInfo.firstInstallTime) / 1000 / 3600 / 24).toString() + "天"))


            addView(add("权限", kotlin.run {
                packageInfo.requestedPermissions?.let {
                    val permissionList = mutableListOf<String>()
                    for (item in it) {
                        var permissionStr = ""
                        try {
                            val permissionInfo = manager.getPermissionInfo(item, PackageManager.GET_META_DATA)
                            permissionStr += permissionInfo.name + "\n"
                            val desc = permissionInfo.loadDescription(manager)
                            permissionStr += if (!Strings.empty(desc)) (desc.toString() + "\n") else ""
                        } catch (e: PackageManager.NameNotFoundException) {
                            permissionStr += item + "\n"
                        }
                        permissionList.add(permissionStr)
                    }
                    permissionList
                }
            }))
            addView(add("视窗", kotlin.run {
                packageInfo.activities?.let {
                    val activityList = mutableListOf<String>()
                    it.forEach { activityList.add(it.name + "\n") }
                    activityList
                }
            }))
            addView(add("服务", kotlin.run {
                packageInfo.services?.let {
                    val serviceList = mutableListOf<String>()
                    it.forEach { serviceList.add(it.name + "\n") }
                    serviceList
                }
            }))
        }
    }

    /**
     * 新增一条数据
     *
     * example:  安装日期  2018-04-01
     */
    fun add(title: String, content: String?): LinearLayoutCompat {
        return LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply {
                        bottomMargin = Ui.dip2px(context, 8)
                    }
        }.apply {
            addView(AppCompatTextView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                minWidth = Ui.dip2px(context, 64)
                setTextColor(Color.parseColor("#242424"))
                textSize = 14f
                text = title.plus("  ")
            })

            content?.let {
                addView(AppCompatTextView(context).apply {
                    layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    val dp2 = Ui.dip2px(context, 2)
                    val dp4 = Ui.dip2px(context, 4)
                    setPadding(dp4, dp2, dp4, dp2)
                    setBackgroundResource(R.drawable.shape_rect_round_can_select)
                    setTextColor(Color.parseColor("#242424"))
                    setTextIsSelectable(true)
                    textSize = 14f
                    text = content
                })
            }
        }
    }

    /**
     * 新增大量数据
     * 默认展示MIN_LINES(5)条，点击全部展示，通过setMaxLines 控制
     *
     * example: 权限(100) 监听网络状态
     */
    fun add(title: String, list: List<String>?): LinearLayoutCompat {
        return LinearLayoutCompat(context).apply {

            orientation = LinearLayoutCompat.HORIZONTAL
            layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = Ui.dip2px(context, 8)
            }

        }.apply {
            addView(AppCompatTextView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                minWidth = Ui.dip2px(context, 64)
                setTextColor(Color.parseColor("#242424"))
                textSize = 14f
                text = if (list == null) title else title + "(" + list.size.toString() + ")"
            })

            list?.let {
                addView(AppCompatTextView(context).apply {
                    layoutParams = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    maxLines = MIN_LINES

                    setOnClickListener {
                        if (maxLines < MIN_LINES) return@setOnClickListener
                        maxLines = if (maxLines <= MIN_LINES) Int.MAX_VALUE else MIN_LINES
                    }

                    val dp2 = Ui.dip2px(context, 2)
                    val dp4 = Ui.dip2px(context, 4)
                    setPadding(dp4, dp2, dp4, dp2)
                    setBackgroundResource(R.drawable.shape_rect_round_can_select)
                    setTextColor(Color.parseColor("#242424"))
                    textSize = 14f
                    text = with(list) {
                        var str = ""
                        list.forEach { str += it + "\n" }
                        str
                    }
                })
            }
        }
    }

}