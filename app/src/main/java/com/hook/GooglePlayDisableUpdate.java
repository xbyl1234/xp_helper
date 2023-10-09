package com.hook;

import android.app.Application;
import android.content.Context;

import com.common.log;
import com.common.units;
import com.tools.hooker.HookTools;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GooglePlayDisableUpdate implements IXposedHookLoadPackage {
    static Set<String> isids = new HashSet<>();
    static final String IsIdFilePath = "/data/data/com.android.vending/isid";

    private static boolean LoadIsidFile() {
        try {
            if (!units.FileExists(IsIdFilePath)) {
                return false;
            }
            String data = new String(units.load_file(IsIdFilePath));
            isids.addAll(Arrays.asList(data.split("\n")));
            return true;
        } catch (IOException e) {
            log.e("read isid file error: " + e);
            return false;
        }
    }

    private static boolean CheckGooglePlay(String isid) {
        synchronized (isids) {
            if (!LoadIsidFile()) {
                log.e("load isid file error!");
            }
            return isids.contains(isid);
        }
    }

    private static boolean AddGooglePlay(String isid) {
        synchronized (isids) {
            try {
                units.AppendToFile(IsIdFilePath, isid + "\n");
                return true;
            } catch (Throwable e) {
                log.e("add isid error: " + e);
                return false;
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.vending")) {
            return;
        }
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                String packageVersion = context.getPackageManager().getPackageInfo("com.android.vending", 0).versionName;
                log.i("inject processName: " + lpparam.processName + ", pkg version: " + packageVersion);
                if (packageVersion.contains("29.5.14")) {
                    packageVersion = "29.5.14";
                } else if (packageVersion.contains("36.9.16")) {
                    packageVersion = "36.9.16";
                } else if (packageVersion.contains("37.2.18")) {
                    packageVersion = "37.2.18";
                } else if (packageVersion.contains("37.7.20")) {
                    packageVersion = "37.7.20";
                }
                PkgInstaller.HookInstaller();
                GooglePlayDisableUpdate.HookDownload(lpparam, packageVersion, param1 -> {
                });
            }
        });
    }

    static class ClassInfo {
        String DeliveryClass;
        String DeliveryMethod;
        String DeliveryMethodParams1;
        String DownloadClass;
        String DownloadMethod;
        String ErrorEnumClass;
    }

    static Map<String, ClassInfo> ClassInfoMap = new HashMap<>();

    static {
        ClassInfo v29 = new ClassInfo();
        v29.DeliveryClass = "qhh";
        v29.DeliveryMethod = "b";
        v29.DeliveryMethodParams1 = "drh";
        v29.DownloadClass = "ieb";
        v29.DownloadMethod = "b";
        v29.ErrorEnumClass = "ido";
        ClassInfoMap.put("29.5.14", v29);

        ClassInfo v37 = new ClassInfo();
        v37.DeliveryClass = "utb";
        v37.DeliveryMethod = "b";
        v37.DeliveryMethodParams1 = "idt";
        v37.DownloadClass = "nel";
        v37.DownloadMethod = "c";
        v37.ErrorEnumClass = "ndj";
        ClassInfoMap.put("37.2.18", v37);

        ClassInfo v37_7_20 = new ClassInfo();
        v37_7_20.DeliveryClass = "vdn";
        v37_7_20.DeliveryMethod = "b";
        v37_7_20.DeliveryMethodParams1 = "iii";
        v37_7_20.DownloadClass = "nlq";
        v37_7_20.DownloadMethod = "c";
        v37_7_20.ErrorEnumClass = "nkl";
        ClassInfoMap.put("37.7.20", v37_7_20);
    }

    private static Object Get_CANNOT_CONNECT_Enum(Object[] ErrorCodeClassConstructor) {
        for (Object obj : ErrorCodeClassConstructor) {
            if (obj.toString().equalsIgnoreCase("8")) {
                return obj;
            }
        }
        log.e("not find enum CANNOT_CONNECT");
        return null;
    }

    private static boolean httpDelivery(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws Throwable {
        Object request = param.args[0];
        Class<?> requestClass = request.getClass();
//        String name = requestClass.getName();
//        if (name.equals("jwt") || name.equals("esd")) {
//            log.i("pass request: " + request);
//            return false;
//        }
        log.i("processName: " + lpparam.processName + "\t" + "HttpDelivery request: " + request);
        Method getUrl = HookTools.GetMethod(requestClass, "f");
        if (getUrl == null) {
            log.i("not find f method: " + requestClass);
            return false;
        }
        URL _url = new URL((String) HookTools.CallMethod(getUrl, request));
        if (!_url.getPath().contains("fdfe/delivery")) {
            log.i("pass url:" + _url);
            return false;
        }
        Map<String, String> queries = units.GetUriParams(_url.getQuery());
        if (!"com.android.vending".equals(queries.get("doc"))) {
            log.i("pass app:" + queries.get("doc"));
            return false;
        }

        String isid = queries.get("isid");
        log.i("processName: " + lpparam.processName + "\t" + "find google play isid: " + isid);
        AddGooglePlay(isid);
        return true;
    }

    private static boolean httpDownload(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, ClassInfo classInfo) throws Throwable {
        String url = (String) param.args[0];
        log.i("processName: " + lpparam.processName + "\t" + "download url: " + url);
        URL _url = new URL(url);
        Map<String, String> queries = units.GetUriParams(_url.getQuery());
        String cpn = queries.get("cpn");
        String isid = queries.get("isid");
        boolean isGooglePlay = false;
        if (cpn != null && !cpn.equals("")) {
            isGooglePlay = CheckGooglePlay(cpn);
        }
        if (isid != null && !isid.equals("")) {
            isGooglePlay |= CheckGooglePlay(isid);
        }
        if (isGooglePlay) {
            Class DownloadServiceException = HookTools.FindClass("com.google.android.finsky.downloadservicecommon.DownloadServiceException", lpparam.classLoader);
            Class ErrorCodeClass = HookTools.FindClass(classInfo.ErrorEnumClass, lpparam.classLoader);
            Object CANNOT_CONNECT = Get_CANNOT_CONNECT_Enum(ErrorCodeClass.getEnumConstants());
            Constructor DownloadServiceExceptionConstructor = HookTools.GetConstructor(DownloadServiceException, ErrorCodeClass, String.class, Throwable.class);
            log.i("processName: " + lpparam.processName + "\t" + "intercept download isid: " + isid + " cpn: " + cpn);
            throw (Throwable) HookTools.CallConstructor(DownloadServiceExceptionConstructor, CANNOT_CONNECT, "Cannot connect to " + url, new SocketTimeoutException(""));
        } else {
            log.i("pass download isid: " + isid + " cpn: " + cpn);
        }
        return false;
    }

    interface DownloadCallback {
        void OnDownloadBefore(XC_MethodHook.MethodHookParam param);
    }

    public static void HookDownload(XC_LoadPackage.LoadPackageParam lpparam, String version, DownloadCallback callback) {
        ClassInfo classInfo = ClassInfoMap.get(version);

        Class targetClassDelivery = HookTools.FindClass(classInfo.DeliveryClass, lpparam.classLoader);
        Class paramsClass1 = HookTools.FindClass(classInfo.DeliveryMethodParams1, lpparam.classLoader);
        if (targetClassDelivery == null || paramsClass1 == null) {
            log.e("some class not find: " + targetClassDelivery + " " + paramsClass1);
            return;
        }
        XposedHelpers.findAndHookMethod(targetClassDelivery, classInfo.DeliveryMethod, paramsClass1, Map.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                httpDelivery(lpparam, param);
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });

        Class targetClassDownload = HookTools.FindClass(classInfo.DownloadClass, lpparam.classLoader);
        if (targetClassDownload == null) {
            log.e("class ieb not find");
            return;
        }
        XposedHelpers.findAndHookMethod(targetClassDownload, classInfo.DownloadMethod, String.class, Map.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                httpDownload(lpparam, param, classInfo);
                callback.OnDownloadBefore(param);
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });
    }
}
