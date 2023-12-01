package com.hook;

import android.content.pm.PackageInstaller;

import com.common.log;
import com.tools.hooker.HookTools;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PkgInstaller implements IXposedHookLoadPackage {
    static final List<String> DisAbleList = Arrays.asList(
            "com.android.vending",
            "com.google.android.gms",
            "com.google.android.gsf"
    );

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i("PkgInstaller inject process: " + lpparam.processName);
        HookInstaller();
    }

    static boolean HookInstaller() {
        try {
            XposedHelpers.findAndHookMethod(PackageInstaller.class, "createSession", PackageInstaller.SessionParams.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    PackageInstaller.SessionParams session = (PackageInstaller.SessionParams) param.args[0];
                    String pkgName = (String) HookTools.GetFieldValue(PackageInstaller.SessionParams.class, session, "appPackageName");
                    log.i("createSession " + pkgName + " --- " + session);
                    if (DisAbleList.contains(pkgName)) {
                        log.i("intercept createSession " + pkgName + " --- " + session);
                        throw new Exception("createSession");
                    }
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
            });

            XposedHelpers.findAndHookMethod(HookTools.FindClass("android.app.ApplicationPackageManager"), "installExistingPackageAsUser",
                    String.class, int.class, int.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            String pkgName = (String) param.args[0];
                            log.i("installExistingPackageAsUser " + pkgName);
                            if (DisAbleList.contains(pkgName)) {
                                log.i("intercept installExistingPackageAsUser " + pkgName);
                                throw new Exception("installExistingPackageAsUser");
                            }
                            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        }
                    });
            return true;
        } catch (Throwable e) {
            log.e("hook installer error: " + e);
            e.printStackTrace();
            return false;
        }
    }
}
