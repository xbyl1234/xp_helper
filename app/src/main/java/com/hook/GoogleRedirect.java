package com.hook;

import com.common.HookTools;
import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GoogleRedirect implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//    avxc.b(org.apache.http.client.methods.HttpUriRequest, avxb) : org.apache.http.HttpResponse
        XposedHelpers.findAndHookMethod(HookTools.FindClass("avxc", lpparam.classLoader), "b",
                XposedHelpers.findClass("org.apache.http.client.methods.HttpUriRequest", lpparam.classLoader), XposedHelpers.findClass("avxb", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        log.i("hook process: " + lpparam.processName + " ,http request: " + param.args[0] + ", args2: " + param.args[1]);
                    }
                });
    }
}
