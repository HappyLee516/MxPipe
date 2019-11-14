package com.mxpipe.lih.mxpipe;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import static android.os.Build.VERSION_CODES.M;

/*
 *Created by LiHuan at 16:48 on 2019/5/30
 */
public class PermissionUtil {

    //验证权限
    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //申请权限
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }
}
