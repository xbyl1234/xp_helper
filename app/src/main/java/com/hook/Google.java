package com.hook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.common.log;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Google implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ApplicationPackageManager", lpparam.classLoader), "getSystemAvailableFeatures", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                log.i("call getSystemAvailableFeatures");
//
//                new Throwable().printStackTrace();
//            }
//        });
//
//        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ApplicationPackageManager", lpparam.classLoader), "getSystemSharedLibraryNames", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                log.i("call getSystemSharedLibraryNames");
//                new Throwable().printStackTrace();
//            }
//        });

//        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("cvbj", lpparam.classLoader), "b",
//                ContentResolver.class, Uri.class, String.class, String.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        log.i("call GoogleSettings");
//                        new Throwable().printStackTrace();
//                    }
//                });
        XposedHelpers.findAndHookMethod(android.content.ContentResolver.class, "insert",
                Uri.class, ContentValues.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Uri uri = (Uri) param.args[0];
                        if (uri.toString().contains("com.google.settings/partner")) {
                            log.i("call GoogleSettings: " + param.args[1]);
                            new Throwable().printStackTrace();
                        }
                    }
                });

    }


}
