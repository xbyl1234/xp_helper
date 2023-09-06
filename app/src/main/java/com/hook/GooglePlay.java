package com.hook;

import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.HookTools;
import com.common.HttpService;
import com.common.log;
import com.common.units;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GooglePlay implements IXposedHookLoadPackage {
    static String host = "127.0.0.1";
    static short port = 11111;

    static class MainProcess {
        HttpService server = new HttpService(host, port);
        Set<String> isids = new HashSet<>();
        static final String IsIdFilePath = "/data/data/com.android.vending/isid";

        public MainProcess() {
            try {
                if (!units.FileExists(IsIdFilePath)) {
                    return;
                }
                String data = new String(units.load_file(IsIdFilePath));
                isids.addAll(Arrays.asList(data.split("\n")));
            } catch (IOException e) {
                log.e("read isid file error: " + e);
            }
        }

        void StartService() {
            server.registerHandler("/check_google_play", new HttpService.HttpServerCallback() {
                @Override
                public String OnHttp(String url, JSONObject body) throws Throwable {
                    synchronized (this) {
                        String isid = body.getString("isid");
                        if (isids.contains(isid)) {
                            return "success";
                        } else {
                            return "failed";
                        }
                    }
                }
            });
            server.registerHandler("/add_google_play", new HttpService.HttpServerCallback() {
                @Override
                public String OnHttp(String url, JSONObject body) throws Throwable {
                    synchronized (this) {
                        String isid = body.getString("isid");
                        isids.add(isid);
                        units.AppendToFile(IsIdFilePath, isid + "\n");
                        return "success";
                    }
                }
            });
            try {
                server.start();
            } catch (Exception e) {
                Log.e("fake_device", "start http error!", e);
                e.printStackTrace();
            }
        }
    }

    static class SubProcess {
        OkHttpClient client = new OkHttpClient();
        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public boolean post(String url, com.alibaba.fastjson.JSONObject data) {
            RequestBody body = RequestBody.create(data.toJSONString(), JSON);
            Request request = new Request.Builder()
                    .url("http://" + host + ":" + port + url)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return false;
                }
                if (!response.body().string().equals("success")) {
                    log.e("http request failed: " + response.body().string());
                    return false;
                }
                return true;
            } catch (Throwable e) {
                log.e("http request error: " + e);
                e.printStackTrace();
                return false;
            }
        }

        boolean CheckGooglePlay(String isid) {
            synchronized (this) {
                com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
                json.put("isid", isid);
                return post("/check_google_play", json);
            }
        }

        boolean AddGooglePlay(String isid) {
            synchronized (this) {
                com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
                json.put("isid", isid);
                return post("/add_google_play", json);
            }
        }
    }

    static MainProcess mainProcess = null;
    static SubProcess subProcess = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i("processName: " + lpparam.processName + "\t" + "inject google play");
        if (lpparam.processName.equals("com.android.vending")) {
            mainProcess = new MainProcess();
            mainProcess.StartService();

        }
        subProcess = new SubProcess();

        try {
            HookUrlDelivery_v29(lpparam);
            HookDownload_v29(lpparam);
        } catch (Exception e) {
            log.i("error: " + e);
            e.printStackTrace();
        }
    }

    static boolean HttpDelivery_v29(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws Throwable {
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
        subProcess.AddGooglePlay(isid);
        return true;
    }

    static void HookUrlDelivery_v29(XC_LoadPackage.LoadPackageParam lpparam) {
        Class targetClass = HookTools.FindClass("qhh", lpparam.classLoader);
        Class paramsClass1 = HookTools.FindClass("drh", lpparam.classLoader);
        if (targetClass == null || paramsClass1 == null) {
            log.e("some class not find: " + targetClass + " " + paramsClass1);
            return;
        }
        XposedHelpers.findAndHookMethod(targetClass, "b", paramsClass1, Map.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                HttpDelivery_v29(lpparam, param);
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });
    }

    static public Object Get_CANNOT_CONNECT_Enum(Object[] ErrorCodeClassConstructor) {
        for (Object obj : ErrorCodeClassConstructor) {
            if (obj.toString().equalsIgnoreCase("CANNOT_CONNECT")) {
                return obj;
            }
        }
        log.e("not find enum CANNOT_CONNECT");
        return null;
    }

    static boolean HttpDownload_v29(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws Throwable {
        String url = (String) param.args[0];
        log.i("processName: " + lpparam.processName + "\t" + "download url: " + url);
        URL _url = new URL(url);
        Map<String, String> queries = units.GetUriParams(_url.getQuery());
        String cpn = queries.get("cpn");
        String isid = queries.get("isid");
        boolean isGooglePlay = false;
        if (cpn != null && !cpn.equals("")) {
            isGooglePlay = subProcess.CheckGooglePlay(cpn);
        }
        if (isid != null && !isid.equals("")) {
            isGooglePlay |= subProcess.CheckGooglePlay(isid);
        }
        if (isGooglePlay) {
            Class DownloadServiceException = HookTools.FindClass("com.google.android.finsky.downloadservicecommon.DownloadServiceException", lpparam.classLoader);
            Class ErrorCodeClass = HookTools.FindClass("ido", lpparam.classLoader);
            Constructor DownloadServiceExceptionConstructor = DownloadServiceException.getDeclaredConstructor(ErrorCodeClass, String.class, Throwable.class);
            Object[] ErrorCodeClassConstructor = ErrorCodeClass.getEnumConstants();
            Object CANNOT_CONNECT = Get_CANNOT_CONNECT_Enum(ErrorCodeClassConstructor);
            log.i("processName: " + lpparam.processName + "\t" + "intercept download isid: " + isid + " cpn: " + cpn);
            throw (Throwable) HookTools.CallConstructor(DownloadServiceExceptionConstructor, CANNOT_CONNECT, "Cannot connect to " + url, new SocketTimeoutException(""));
        } else {
            log.i("pass download isid: " + isid + " cpn: " + cpn);
        }
        return false;
    }


    static void HookDownload_v29(XC_LoadPackage.LoadPackageParam lpparam) {
        Class targetClass = HookTools.FindClass("ieb", lpparam.classLoader);
        if (targetClass == null) {
            log.e("class ieb not find");
            return;
        }
        XposedHelpers.findAndHookMethod(targetClass, "b", String.class, Map.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                HttpDownload_v29(lpparam, param);
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });
    }

}
