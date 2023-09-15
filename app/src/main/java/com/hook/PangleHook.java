package com.hook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.common.HookTools;
import com.common.log;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PangleHook implements IXposedHookLoadPackage {

    public static void DonotJump_1(XC_LoadPackage.LoadPackageParam lpparam) {
        Class core_b = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.b", lpparam.classLoader);
        if (core_b == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(core_b, "b",
                org.json.JSONObject.class,
                HookTools.FindClass("com.bytedance.sdk.openadsdk.AdSlot", lpparam.classLoader),
                HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.q", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        org.json.JSONObject json = (JSONObject) param.args[0];
                        log.i("json interaction_type: " + json.getInt("interaction_type"));
                        log.i("json interaction_method: " + json.getInt("interaction_method"));
                        if (json.getInt("interaction_method") == 21) {
                            log.i("json set interaction_method to 7");
                            json.put("interaction_method", 7);
                        }
//                        if (json.getInt("interaction_method") == 7) {
//                            log.i("json set interaction_method to 21");
//                            json.put("interaction_method", 21);
//                        }
                    }
                });
    }

    public static void DonotJump_2(XC_LoadPackage.LoadPackageParam lpparam) {
        Class model = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.o", lpparam.classLoader);
        Class model_m = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.m", lpparam.classLoader);
        if (model == null || model_m == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(model_m, "b", model,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DonotJump_2");
                        return true;
//                        Object params = param.args[0];
//                        try {
//                            final boolean fieldBased = true;
//                            SerializeConfig serializeConfig = new SerializeConfig(fieldBased);
//                            log.i(JSON.toJSONString(params, serializeConfig));
//                        } catch (Throwable e) {
//                            log.i("error: " + e.toString());
//                        }
//                        log.i("click: param M:" + HookTools.GetFieldValue(model, params, "M") + " ret: " + param.getResult());
                    }
                });
    }


    public static void DonotJump_3(XC_LoadPackage.LoadPackageParam lpparam) {
        Class model = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.o", lpparam.classLoader);
        if (model == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(model, "a",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DonotJump_3");
                        return true;
                    }
                });
    }

    public static void DonotJump_4(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(HookTools.FindClass("android.widget.RelativeLayout", lpparam.classLoader), "performClick",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DonotJump_4");
                        Throwable th = new Throwable();
                        StackTraceElement[] sts = th.getStackTrace();
                        for (StackTraceElement item : sts) {
                            log.i("-----" + item);
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                });
    }


    public static void WitchAdsType(XC_LoadPackage.LoadPackageParam lpparam) {
        Class model = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.o", lpparam.classLoader);
        if (model == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(model, "j", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log.i("interaction_method: " + param.getResult());
            }
        });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i("inject " + lpparam.processName);
        WitchAdsType(lpparam);
//        DonotJump_3(lpparam);
//        DonotJump_4(lpparam);


//        XposedHelpers.findAndHookMethod(HookTools.FindClass("com.bytedance.sdk.openadsdk.core.video.c.a", lpparam.classLoader),
//                "a",
//                long.class, long.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        long p1 = (long) param.args[0];
//                        long p2 = (long) param.args[1];
//                        log.i("call click: " + p1 + " " + p2);
//                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
//                    }
//                });
    }
}