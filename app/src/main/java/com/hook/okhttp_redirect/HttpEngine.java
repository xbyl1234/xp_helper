package com.hook.okhttp_redirect;

import com.common.log;
import com.tools.hooker.FakeClass;
import com.tools.hooker.FakeClassBase;
import com.tools.hooker.FakeMethod;
import com.tools.hooker.HookTools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@FakeClass(ClassName = "com.android.okhttp.internal.http.HttpEngine")
public class HttpEngine extends FakeClassBase {
    static Field RequestField;
    static Field ResponseField;
    ResourceCacheInterface resourceCache;
    String pkgName;

    public HttpEngine(ResourceCacheInterface resourceCache, String pkgName) {
        this.resourceCache = resourceCache;
        this.pkgName = pkgName;
    }

    URL GetObjectUrl(XC_MethodHook.MethodHookParam params) throws Throwable {
        return GetRequest(params).url();
    }

    @Override
    public boolean ShouldFake(XC_MethodHook.MethodHookParam params) {
        URL url = null;
        try {
            url = GetObjectUrl(params);
            if (url == null) {
                return false;
            }
            if (url.getHost().contains("fonts.gstatic.com")) {
                return true;
            }
            if (url.getHost().contains("baidu.com")) {
                return true;
            }
        } catch (Throwable e) {
            log.e("wtf? " + e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean InitFakeClass(ClassLoader classLoader, Class originClass) throws Throwable {
        RequestField = HookTools.GetField(originClass, "userRequest");
        ResponseField = HookTools.GetField(originClass, "userResponse");
        XposedHelpers.findAndHookConstructor(originClass,
                HookTools.FindClass("com.android.okhttp.OkHttpClient", classLoader),
                HookTools.FindClass("com.android.okhttp.Request", classLoader),
                boolean.class, boolean.class, boolean.class,
                HookTools.FindClass("com.android.okhttp.internal.http.StreamAllocation", classLoader),
                HookTools.FindClass("com.android.okhttp.internal.http.RetryableSink", classLoader),
                HookTools.FindClass("com.android.okhttp.Response", classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log.i("call init " + GetRequest(param));
                    }
                }
        );
        return true;
    }

    public RequestProxy GetRequest(XC_MethodHook.MethodHookParam params) {
        return new RequestProxy(HookTools.GetFieldValue(RequestField, params.thisObject));
    }

    public ResponseProxy GetResponse(XC_MethodHook.MethodHookParam params) {
        return new ResponseProxy(HookTools.GetFieldValue(ResponseField, params.thisObject));
    }

    public void SetResponse(XC_MethodHook.MethodHookParam params, Object value) {
        HookTools.SetField(ResponseField, params.thisObject, value);
    }

    //HttpStream
    @FakeMethod(needXposedParams = true)
    private Object connect(XC_MethodHook.MethodHookParam params) throws Throwable {
        log.i("call connect " + GetRequest(params));
        Object result = CallOriginalMethod(params);
        log.i("call after connect " + GetRequest(params));
        return result;
    }


    Object CreateCacheStrategy(Object req) throws Throwable {
        Class FactoryClass = HookTools.FindClass("com.android.okhttp.internal.http.CacheStrategy$Factory");
        Class RequestClass = HookTools.FindClass("com.android.okhttp.Request");
        Class ResposeClass = HookTools.FindClass("com.android.okhttp.Response");
        Constructor constructor = FactoryClass.getConstructor(long.class, RequestClass, ResposeClass);
        Object factory = HookTools.CallConstructor(constructor, System.currentTimeMillis(), req, null);
        Method get = FactoryClass.getDeclaredMethod("get");
        return get.invoke(factory);
    }

    Object CreateRetryableSink() throws Throwable {
        Class RetryableSink = HookTools.FindClass("com.android.okhttp.internal.http.RetryableSink");
        Constructor constructor = RetryableSink.getConstructor();
        return HookTools.CallConstructor(constructor);
    }

    @FakeMethod(needXposedParams = true)
    public void sendRequest(XC_MethodHook.MethodHookParam params) throws Throwable {
        RequestProxy req = GetRequest(params);
        CacheId cacheId = new CacheId(req.url());
        boolean hasCache = resourceCache.HasCache(cacheId);
        log.i("sendRequest: " + req.url() + ", hasCache: " + hasCache);
        if (hasCache) {
            HookTools.SetField(params.thisObject.getClass(), params.thisObject, "cacheStrategy",
                    CreateCacheStrategy(req.originObject));
            HookTools.SetField(params.thisObject.getClass(), params.thisObject, "requestBodyOut",
                    CreateRetryableSink());
            log.i("sendRequest: " + req.url() + ", pass");
            return;
        }
        log.i("call sendRequest " + req.url());
        CallOriginalMethod(params);
        log.i("call after sendRequest " + req.url());
    }

    @FakeMethod(needXposedParams = true)
    public void readResponse(XC_MethodHook.MethodHookParam params) throws Throwable {
        RequestProxy req = GetRequest(params);
        CacheId cacheId = new CacheId(req.url());
        boolean hasCache = resourceCache.HasCache(cacheId);
        log.i("readResponse: " + req.url() + ", hasCache: " + hasCache);
        if (!hasCache) {
            log.i("call readResponse " + req.url());
            CallOriginalMethod(params);
            ResponseProxy resp = GetResponse(params);
            log.i("call after readResponse " + req.url() + ", resp: " + resp);
            if (resp.GetCode() == 200) {
                Cache newCache = new Cache(req.url(), resp.GetHeaders(), resp.GetBodyBytes());
                boolean result = resourceCache.UploadCache(newCache, "/data/data/" + pkgName + "/" + newCache.id.md5);
                log.i("readResponse " + req.url() + ", UploadCache: " + result);
            } else {
                log.i("readResponse " + req.url() + ", resp error code: " + resp.GetCode());
            }
        } else {
            Cache cache = resourceCache.GetCache(cacheId);
            SetResponse(params, cache.CreateResponseBody(req.originObject));
            log.i("readResponse return cache " + req.url());
        }
    }

}
