package com.hook;

import android.content.pm.PackageInstaller;

import com.common.HookTools;
import com.common.log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PkgInstaller implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(PackageInstaller.class, "createSession", PackageInstaller.SessionParams.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                PackageInstaller.SessionParams session = (PackageInstaller.SessionParams) param.args[0];
                String pkgName = (String) HookTools.GetFieldValue(PackageInstaller.SessionParams.class, session, "appPackageName");
                log.i("createSession " + pkgName + " --- " + session);
                throw new Exception("createSession");
//                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });

        XposedHelpers.findAndHookMethod(HookTools.FindClass("android.app.ApplicationPackageManager"), "installExistingPackageAsUser",
                String.class, int.class, int.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("installExistingPackageAsUser " + param.args[0]);
                        throw new Exception("installExistingPackageAsUser");
//                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                });
    }
}
