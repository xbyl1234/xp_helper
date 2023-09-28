package com.hook.okhttp_redirect;

import com.common.log;
import com.tools.hooker.FakeClass;
import com.tools.hooker.FakeClassBase;
import com.tools.hooker.FakeMethod;
import com.tools.hooker.FakeParams;
import com.tools.hooker.HookTools;

import java.io.IOException;
import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@FakeClass(ClassName = "com.android.okhttp.Call")
public class OkHttpCall extends FakeClassBase {

    static Field RequestField;

    @Override
    public boolean ShouldFake(XC_MethodHook.MethodHookParam params) {
        return true;
    }

    public boolean InitFakeClass(ClassLoader classLoader, Class originClass) throws Throwable {
        RequestField = HookTools.GetField(originClass, "originalRequest");
        XposedHelpers.findAndHookConstructor(originClass,
                HookTools.FindClass("com.android.okhttp.OkHttpClient", classLoader),
                HookTools.FindClass("com.android.okhttp.Request", classLoader),
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

    public Object GetRequest(XC_MethodHook.MethodHookParam params) {
        return HookTools.GetFieldValue(RequestField, params.thisObject);
    }

    //Response
    @FakeMethod(needXposedParams = true)
    public Object execute(XC_MethodHook.MethodHookParam params) throws Throwable {
        log.i("call execute " + GetRequest(params));
        Object resp = CallOriginalMethod(params);
        log.i("call after execute " + GetRequest(params) + ", resp: " + resp);
        return resp;
    }

    @FakeMethod(needXposedParams = true)
    void enqueue(XC_MethodHook.MethodHookParam params,
                 @FakeParams(ClassName = "com.android.okhttp.Callback") Object responseCallback,
                 boolean forWebSocket) throws Throwable {
        log.i("call enqueue " + GetRequest(params));
        CallOriginalMethod(params);
        log.i("call after enqueue " + GetRequest(params));
    }

    //Response
    @FakeMethod(needXposedParams = true)
    Object getResponse(XC_MethodHook.MethodHookParam params,
                       @FakeParams(ClassName = "com.android.okhttp.Request") Object request,
                       boolean forWebSocket) throws Throwable {
        log.i("call getResponse " + GetRequest(params));
        Object resp = CallOriginalMethod(params);
        log.i("call after getResponse " + GetRequest(params) + ", resp:" + resp);
        return resp;
    }
}