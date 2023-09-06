package com.hook;

import android.os.Process;

import android.app.Application;
import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;

import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AndroidID implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i(lpparam.packageName + " inject!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                log.i(lpparam.packageName + " android id: " + Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
                try {
                    log.i(lpparam.packageName + " getSerialNumberForUser: " + ((UserManager) context.getSystemService(Context.USER_SERVICE)).getSerialNumberForUser(Process.myUserHandle()));
                } catch (Throwable e) {
                    log.i(lpparam.packageName + " getSerialNumberForUser: " + e);
                }
            }
        });


    }
}
