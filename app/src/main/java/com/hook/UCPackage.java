package com.hook;

import static com.common.units.AppendToFile;

import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UCPackage implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> EncryptClass = XposedHelpers.findClass("com.uc.business.channel.g", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(EncryptClass,
                "encrypt",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String plain = (String) param.args[0];
                        AppendToFile("/data/data/" + lpparam.packageName + "/pkg", "encrypt: " + plain);
                        log.i("encrypt: " + plain);
                    }
                });

        Class<?> applogClass = XposedHelpers.findClass("com.uc.crashsdk.a.c", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(
                applogClass,
                "a",
                byte[].class,
                byte[].class,
                boolean.class,
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String plain = new String((byte[]) param.args[0]);
                        AppendToFile("/data/data/" + lpparam.packageName + "/pkg", "applog: " + plain);
                        log.i("applog: " + plain);
                    }
                });

        log.i("uc inject success");
    }
}
