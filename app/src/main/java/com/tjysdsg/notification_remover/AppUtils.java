package com.tjysdsg.notification_remover;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class AppUtils {
    public static String getAppName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return (String) packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA))
            );
        } else {
            return (String) packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            );
        }
    }
}
