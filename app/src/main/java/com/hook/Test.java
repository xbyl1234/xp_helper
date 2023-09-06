package com.hook;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.os.UserManager;
import android.provider.Settings;


import com.common.HookTools;
import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

class MyException extends Exception {

}

public class Test implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(HookTools.FindClass("com.MainActivity", lpparam.classLoader), "test", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                throw new MyException();
                param.setThrowable(new MyException());
                log.i("set error");
                return null;
            }
        });
    }
}
