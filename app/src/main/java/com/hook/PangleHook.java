package com.hook;

import android.app.Application;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.common.HookTools;
import com.common.log;
import com.common.units;

import org.json.JSONObject;

import java.io.IOException;

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

    public static void DoNotJump_1(XC_LoadPackage.LoadPackageParam lpparam) throws IOException {
        Class core_b = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.b", lpparam.classLoader);
        if (core_b == null) {
            log.i("hook DoNotJump_1 error");
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

        log.i("hook DoNotJump_1 success");
        units.save_file("/data/data/" + lpparam.packageName + "/lsp", "inject".getBytes());
    }

    public static void DoNotJump_2(XC_LoadPackage.LoadPackageParam lpparam) throws IOException {
        Class model = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.o", lpparam.classLoader);
        Class model_m = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.m", lpparam.classLoader);
        if (model == null || model_m == null) {
            log.i("hook DoNotJump_2 error");
            return;
        }
        XposedHelpers.findAndHookMethod(model_m, "b", model,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DoNotJump_2");
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
        log.i("hook DoNotJump_2 success");
        units.save_file("/data/data/" + lpparam.packageName + "/lsp", "inject".getBytes());
    }

    public static void DoNotJump_3(XC_LoadPackage.LoadPackageParam lpparam) throws IOException {
        Class model = HookTools.FindClass("com.bytedance.sdk.openadsdk.core.model.o", lpparam.classLoader);
        if (model == null) {
            log.i("hook DoNotJump_3 error");
            return;
        }
        XposedHelpers.findAndHookMethod(model, "a",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DoNotJump_3");
                        return true;
                    }
                });
        log.i("hook DoNotJump_3 success");
        units.save_file("/data/data/" + lpparam.packageName + "/lsp", "inject".getBytes());
    }

    public static void DoNotJump_4(XC_LoadPackage.LoadPackageParam lpparam) throws IOException {
        XposedHelpers.findAndHookMethod(HookTools.FindClass("android.widget.RelativeLayout", lpparam.classLoader), "performClick",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        log.i("do not jump by DoNotJump_4");
                        Throwable th = new Throwable();
                        StackTraceElement[] sts = th.getStackTrace();
                        for (StackTraceElement item : sts) {
                            log.i("-----" + item);
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                });
        log.i("hook DoNotJump_4 success");
        units.save_file("/data/data/" + lpparam.packageName + "/lsp", "inject".getBytes());
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
//                new Throwable().printStackTrace();
                log.i("interaction_method: " + param.getResult());
            }
        });
    }



    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i("inject " + lpparam.processName);

        try {
            WitchAdsType(lpparam);
        } catch (Throwable e) {
            log.i("WitchAdsType error: " + e);
        }

        try {
            DoNotJump_3(lpparam);
        } catch (Throwable e) {
            log.i("DoNotJump_3 error: " + e);
        }


//        try {
//            DoNotJump_2(lpparam);
//        } catch (Throwable e) {
//            log.i("DoNotJump_2 error: " + e);
//        }

//        DoNotJump_4(lpparam);
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


//    public static void LogInjectSuccess() {
//        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Context context = (Context) param.args[0];
//            }
//        });
//    }