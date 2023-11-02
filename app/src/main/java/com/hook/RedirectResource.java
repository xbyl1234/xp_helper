package com.hook;

import com.common.log;
import com.hook.okhttp_redirect.FakeRealBufferedSource;
import com.hook.okhttp_redirect.HttpEngine;
import com.hook.okhttp_redirect.ResourceCache;
import com.hook.okhttp_redirect.ResourceCacheTest;
import com.hook.unused.FakeDelegatingHttpsURLConnection;
import com.tools.hooker.Hooker;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RedirectResource implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        if (!lpparam.packageName.equals("com.google.android.gms")) {
//            return;
//        }
        log.i("hook fonts inject process: " + lpparam.processName);


        XposedHelpers.findAndHookMethod(System.class, "loadLibrary", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (((String) param.args[0]).contains("libcronet")) {
                    log.i("not load so");
                    throw new Exception();
                }
            }
        });
        XposedHelpers.findAndHookMethod(System.class, "load", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (((String) param.args[0]).contains("libcronet")) {
                    log.i("not load so");
                    throw new Exception();
                }
            }
        });

        ResourceCacheTest resourceCacheTest = new ResourceCacheTest();
        Hooker.HookClass(lpparam.classLoader, FakeRealBufferedSource.class, new FakeRealBufferedSource());
        Hooker.HookClass(lpparam.classLoader, HttpEngine.class, new HttpEngine(resourceCacheTest, lpparam.packageName));


//        Hooker.HookClass(lpparam.classLoader, FakeDelegatingHttpsURLConnection.class, new FakeDelegatingHttpsURLConnection(new ResourceCacheTest()));
//        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                System.load("/data/libxphelper.so");
//                log.i("hook fonts inject process: " + lpparam.processName);
//                Context context = (Context) param.args[0];
//                XposedHelpers.findAndHookMethod(HookTools.FindClass("avjp", context.getClassLoader()),
//                        "b", HookTools.FindClass("avki", context.getClassLoader()),
//                        new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                log.i("call: " + param.args[0]);
//                            }
//                        }
//                );
//                String packageVersion = context.getPackageManager().getPackageInfo("com.android.vending", 0).versionName;
//                log.i("inject processName: " + lpparam.processName + ", pkg version: " + packageVersion);
//                GooglePlayDisableUpdate.ClassInfo v37 = new GooglePlayDisableUpdate.ClassInfo();
//                v37.DeliveryClass = "utb";
//                v37.DeliveryMethod = "b";
//                v37.DeliveryMethodParams1 = "idt";
//                v37.DownloadClass = "nel";
//                v37.DownloadMethod = "c";
//                v37.ErrorEnumClass = "ndj";
//                Class targetClassDownload = HookTools.FindClass(v37.DownloadClass, lpparam.classLoader);
//                XposedHelpers.findAndHookMethod(targetClassDownload, v37.DownloadMethod, String.class, Map.class, boolean.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        String url = (String) param.args[0];
//                        log.i("processName: " + lpparam.processName + "\t" + "download url: " + url);
//                        Object ret = XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
//                        log.i("processName: " + lpparam.processName + "\t" + " ret: " + ret.getClass() + "download url: " + url);
//                        return ret;
//                    }
//                });
    }
}


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
//    }
//}
