package com.hook;

import com.common.log;
import com.hook.okhttp_redirect.FakeRealBufferedSource;
import com.hook.okhttp_redirect.HttpEngine;
import com.hook.okhttp_redirect.ResourceCache;
import com.hook.okhttp_redirect.ResourceCacheTest;
import com.tools.hooker.Hooker;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GoogleRedirectFontsDownload implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        if (!lpparam.packageName.equals("com.google.android.gms")) {
//            return;
//        }
        log.i("hook fonts inject process: " + lpparam.processName);
        Hooker.HookClass(lpparam.classLoader, FakeRealBufferedSource.class, new FakeRealBufferedSource());
        Hooker.HookClass(lpparam.classLoader, HttpEngine.class, new HttpEngine(new ResourceCache(), lpparam.packageName));

//        XposedHelpers.findAndHookMethod(HttpURLConnection.class, "connect", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                HttpURLConnection conn = (HttpURLConnection) param.thisObject;
//                URL url = conn.getURL();
//                log.i("hook process: " + lpparam.processName + " ,http request: " + url);
//            }
//        });
//        Class HttpsURLConnectionImpl = HookTools.FindClass("com.android.okhttp.internal.huc.HttpsURLConnectionImpl", lpparam.classLoader);
//        XposedHelpers.findAndHookMethod(HttpsURLConnectionImpl, "connect", new XC_MethodReplacement() {
//            @Override
//            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                HttpURLConnection conn = (HttpURLConnection) param.thisObject;
//                URL url = conn.getURL();
//                if (url.getHost().contains("fonts.gstatic.com")) {
//                    log.i("process: " + lpparam.processName + " ,intercept http request: " + url);
//                    throw new SocketTimeoutException("");
//                }
//                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
//            }
//        });
        //version v29
//        public final HttpResponse b(HttpUriRequest httpUriRequest, agtg agtgVar) {
//        Class HttpUriRequestClass = XposedHelpers.findClass("org.apache.http.client.methods.HttpUriRequest", lpparam.classLoader);
//        Method getURI = HttpUriRequestClass.getDeclaredMethod("getURI");
//
//        XposedHelpers.findAndHookMethod(HookTools.FindClass("agth", lpparam.classLoader), "b",
//                HttpUriRequestClass, XposedHelpers.findClass("agtg", lpparam.classLoader),
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        URL uri = ((URI) getURI.invoke(param.args[0])).toURL();
//                        log.i("hook process: " + lpparam.processName + " ,http request: " + uri.toString() + ", args2: " + param.args[1]);
//                    }
//                });
//
////        public final HttpURLConnection e(URL url, agtu agtuVar) {
//        XposedHelpers.findAndHookMethod(HookTools.FindClass("agth", lpparam.classLoader), "e",
//                URL.class, XposedHelpers.findClass("agtu", lpparam.classLoader),
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        log.i("hook process: " + lpparam.processName + " ,get request: " + param.args[0] + ", class:" + param.getResult().getClass() + ", args2: " + param.args[1]);
//                    }
//                });

//    avxc.b(org.apache.http.client.methods.HttpUriRequest, avxb) : org.apache.http.HttpResponse
//        XposedHelpers.findAndHookMethod(HookTools.FindClass("avxc", lpparam.classLoader), "b",
//                XposedHelpers.findClass("org.apache.http.client.methods.HttpUriRequest", lpparam.classLoader), XposedHelpers.findClass("avxb", lpparam.classLoader),
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        log.i("hook process: " + lpparam.processName + " ,http request: " + param.args[0] + ", args2: " + param.args[1]);
//                    }
//                });
    }
}
