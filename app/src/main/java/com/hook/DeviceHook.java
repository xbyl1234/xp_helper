package com.hook;

import android.os.Build;
import android.os.Process;

import android.app.Application;
import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;

import com.common.HookTools;
import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DeviceHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i(lpparam.packageName + " inject!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                ClassLoader classLoader = context.getClassLoader();
                log.i(lpparam.packageName + " android id: " + Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
                try {
                    log.i(lpparam.packageName + " getSerialNumberForUser: " + ((UserManager) context.getSystemService(Context.USER_SERVICE)).getSerialNumberForUser(Process.myUserHandle()));
                } catch (Throwable e) {
                    log.i(lpparam.packageName + " getSerialNumberForUser: " + e);
                }
                try {
                    log.i("Build.getSerial() " + Build.getSerial());
                } catch (Throwable e) {
                    log.i(lpparam.packageName + " Build.getSerial(): " + e);
                }
                try {
                    log.i("Build.SERIAL " + Build.SERIAL);
                    Class BuildClass = HookTools.FindClass("android.os.Build", classLoader);
                    log.i("Build.SERIAL ref: " + HookTools.GetFieldValue(BuildClass, null, "SERIAL"));
                } catch (Throwable e) {
                    log.i(lpparam.packageName + " Build.SERIAL: " + e);
                }
            }
        });

        XposedHelpers.findAndHookMethod(Build.class, "getSerial", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log.i("hook getSerial: " + param.getResult());
                try {
                    log.i("hook Build.SERIAL " + Build.SERIAL);
                    log.i("hook Build.SERIAL ref: " + HookTools.GetFieldValue(param.thisObject.getClass(), null, "SERIAL"));
                } catch (Throwable e) {
                    log.i(lpparam.packageName + " Build.SERIAL: " + e);
                }
            }
        });

        XposedHelpers.findAndHookMethod(UserManager.class, "getUserSerialNumber", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log.i("hook getUserSerialNumber: " + param.getResult());
            }
        });

    }
}
