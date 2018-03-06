package com.lpzahd.common.waiter.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class PermissionsWaiter extends ToneActivityWaiter<AppCompatActivity> {

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 需要进行检测的权限数组 (全部)
     */
    protected String[] needPermissions = {
//            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK
    };

    public interface Accept {
        void accept();
    }

    private Accept mAccept;

    private boolean isAccept = false;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    public PermissionsWaiter(AppCompatActivity activity) {
        super(activity);
    }

    public PermissionsWaiter(AppCompatActivity activity, String... needPermissions) {
        super(activity);
        this.needPermissions = needPermissions;
    }

    public void setAccept(Accept accept) {
        mAccept = accept;
    }

    @Override
    protected void resume() {
        super.resume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }


    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(context,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        } else {
            if(mAccept != null && !isAccept) {
                isAccept = true;
                mAccept.accept();
            }
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    context, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.requestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {
                if(mAccept != null) {
                    isAccept = true;
                    mAccept.accept();
                }
            }
        }
    }


    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        new MaterialDialog.Builder(context)
                .title("提示")
                .content("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。")
                .positiveText("确定")
                .negativeText("取消")
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startAppSettings();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        context.finish();
                    }
                })
                .show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
