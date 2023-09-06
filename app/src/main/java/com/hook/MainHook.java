package com.hook;

import android.app.Application;

import com.common.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import javax.crypto.Cipher;

import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    File logFile;
    FileOutputStream logFileStream;
    String processName;
    String logFilePath;

    native public boolean InitLib(String processName, String LogFilePath);

    void WriteLog(String log) {
        synchronized (logFileStream) {
            try {
                logFileStream.write(log.getBytes(StandardCharsets.UTF_8));
                logFileStream.write("\n".getBytes(StandardCharsets.UTF_8));
                logFileStream.flush();
            } catch (IOException e) {
            }
        }
    }

    public static Field GetField(Class _class, String name) {
        try {
            return _class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object GetFieldValue(Field field, Object obj) {
        try {
            Object result = null;
            boolean oldAcc = field.isAccessible();
            field.setAccessible(true);
            result = field.get(obj);
            field.setAccessible(oldAcc);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object GetFieldValue(Class _class, Object obj, String name) {
        try {
            Object result = null;
            Field field = GetField(_class, name);
            if (field == null) {
                return null;
            }
            boolean oldAcc = field.isAccessible();
            field.setAccessible(true);
            result = field.get(obj);
            field.setAccessible(oldAcc);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String bytesToHex(ByteBuffer buffer) {
        buffer.rewind();
        StringBuilder hexString = new StringBuilder();
        while (buffer.hasRemaining()) {
            String hex = Integer.toHexString(0xFF & buffer.get());
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        buffer.rewind();
        return hexString.toString();
    }

    boolean CheckCipherIsEncoded(Cipher cipher) {
        return (int) GetFieldValue(Cipher.class, cipher, "opmode") == 1;
    }

    void UcHook(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.uc.base.secure.EncryptHelper", classLoader, "a", byte[].class, short.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                WriteLog("m1 input:" + bytesToHex((byte[]) param.args[0]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
            }
        });
        XposedHelpers.findAndHookMethod("com.uc.base.system.SystemHelper", classLoader, "nativeM9Encode", byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                WriteLog("m2 input:" + bytesToHex((byte[]) param.args[0]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
            }
        });

        XposedHelpers.findAndHookMethod("com.uc.base.system.SystemHelper", classLoader, "nativeM10Encode", int.class, byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                WriteLog("m3 input:" + bytesToHex((byte[]) param.args[1]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
            }
        });

        XposedHelpers.findAndHookMethod("com.uc.util.base.g.d", classLoader, "c", byte[].class, int[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                WriteLog("m4 input:" + bytesToHex((byte[]) param.args[0]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
            }
        });


        XposedHelpers.findAndHookMethod("com.ta.audid.utils.RC4", classLoader, "rc4",
                byte[].class, XposedHelpers.findClass("com.ta.audid.utils.RC4$RC4Key", classLoader), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        WriteLog("m5 input:" + bytesToHex((byte[]) param.args[0]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
                    }
                });

    }

    void HookCipher() {
//                public final byte[] doFinal()
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("output: " + bytesToHex((byte[]) param.getResult()));
                }
            }
        });

//  public final int doFinal(byte[] output, int outputOffset)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("output: " + bytesToHex((byte[]) param.args[0]));
                }
            }
        });

//        public final int doFinal(ByteBuffer input, ByteBuffer output)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", ByteBuffer.class, ByteBuffer.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("input: " + bytesToHex((ByteBuffer) param.args[0]) + "\t" + "output: " + bytesToHex((ByteBuffer) param.args[1]));
                }
            }
        });


//     public final byte[] doFinal(byte[] input)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("input: " + bytesToHex((byte[]) param.args[0]) + "\t" + "output: " + bytesToHex((byte[]) param.getResult()));
                }
            }
        });

//      public final byte[] doFinal(byte[] input, int inputOffset, int inputLen)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("input: " + bytesToHex((byte[]) param.args[0]) + "\t" + "output: " + bytesToHex((byte[]) param.getResult()));
                }
            }
        });


// public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, int.class, int.class, byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("input: " + bytesToHex((byte[]) param.args[0]) + "\t" + "output: " + bytesToHex((byte[]) param.args[3]));
                }
            }
        });

//        public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
        XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, int.class, int.class, byte[].class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (CheckCipherIsEncoded((Cipher) param.thisObject)) {
                    WriteLog("input: " + bytesToHex((byte[]) param.args[0]) + "\t" + "output: " + bytesToHex((byte[]) param.args[3]));
                }
            }
        });
    }

    void HookPkg(ClassLoader classLoader) {
//        System.load("/data/data/libxphelper.so");
//        InitLib(processName, logFilePath);
        HookCipher();
//        try {
//            Class clz = XposedHelpers.findClass("com.uc.base.wa.c.g", classLoader);
//            Class clz2 = XposedHelpers.findClass("com.uc.base.wa.cache.o", classLoader);
//            XposedHelpers.findAndHookMethod(clz, "a", Object.class, String.class, ArrayList.class, clz2, String.class, new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                    log.i("-------------pass");
//                    return 0;
//                }
//            });
//            log.i("-------------success");
//        } catch (Throwable e) {
//            log.i("Exception: " + e);
//            e.printStackTrace();
//        }
    }


    void On_tobEmbedEncrypt_loaded(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.bytedance.embedapplog.util.TTEncryptUtils", classLoader, "ttEncrypt", byte[].class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                WriteLog("t1 input:" + bytesToHex((byte[]) param.args[0]) + "\t output:" + bytesToHex((byte[]) param.getResult()));
            }
        });
    }

    void On_wireless_loaded(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.alibaba.wireless.security.framework.SGApmMonitorManager", classLoader, "a", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log.i("sg1: " + param.args[0] + " output:" + param.getResult());
            }
        });
    }

    void On_MtopApi_loaded(ClassLoader classLoader) {
        try {
            Class mqtt = XposedHelpers.findClass("mtopsdk.mtop.domain.MethodEnum", classLoader);
            XposedHelpers.findAndHookConstructor("com.taobao.dai.adapter.MtopApi", classLoader,
                    String.class, String.class, boolean.class, boolean.class, Map.class, Class.class, mqtt, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            log.i("MtopApi: " + param.args[0]);
                            new Throwable().printStackTrace();
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        log.i("xphelper inject process " + lpparam.processName + "!");
        int pid = android.os.Process.myPid();
        logFilePath = "/data/data/" + lpparam.packageName + "/log_" + lpparam.processName + "_" + System.currentTimeMillis() + "_" + pid;
        logFile = new File(logFilePath);
        logFileStream = new FileOutputStream(logFile);
        processName = lpparam.processName;

        HookPkg(null);

        Class ActivityThread = XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader);
        XposedBridge.hookAllMethods(ActivityThread, "performLaunchActivity", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Application mInitialApplication = (Application) XposedHelpers.getObjectField(param.thisObject, "mInitialApplication");
                ClassLoader classLoader = (ClassLoader) XposedHelpers.callMethod(mInitialApplication, "getClassLoader");
                log.i("find class loader: " + classLoader.toString());
                UcHook(classLoader);
            }
        });


//        XposedHelpers.findAndHookMethod(System.class, "loadLibrary", String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if (((String) param.args[0]).equals("tobEmbedEncrypt")) {
//                    log.i("loadLibrary tobEmbedEncrypt");
//                    On_tobEmbedEncrypt_loaded(param.thisObject.getClass().getClassLoader());
//                    log.i("hook tobEmbedEncrypt");
//                }
//            }
//        });

        XposedHelpers.findAndHookMethod(DexFile.class, "defineClassNative",
                String.class, ClassLoader.class, Object.class, DexFile.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String clsName = (String) param.args[0];
                        ClassLoader classLoader = (ClassLoader) param.args[1];

                        if (clsName.equals("com.bytedance.embedapplog.util.TTEncryptUtils")) {
                            log.i("loadLibrary tobEmbedEncrypt");
                            On_tobEmbedEncrypt_loaded((ClassLoader) param.args[1]);
                            log.i("hook tobEmbedEncrypt");
                        } else if (clsName.equals("com.alibaba.wireless.security.framework.SGApmMonitorManager")) {
//                            log.i("loadLibrary wireless");
//                            On_wireless_loaded((ClassLoader) param.args[1]);
//                            log.i("hook wireless");
                        } else if (clsName.equals("com.taobao.dai.adapter.MtopApi") || clsName.equals("mtopsdk.mtop.domain.MethodEnum")) {
//                            log.i("loadLibrary MtopApi");
//                            On_MtopApi_loaded((ClassLoader) param.args[1]);
//                            log.i("hook MtopApi");
                        } else if (clsName.equals("mtopsdk.mtop.domain.IMTOPDataObject")) {
//                            log.i("loadLibrary Mtop");
//                            try {
//                                XposedHelpers.findAndHookMethod("mtopsdk.mtop.intf.Mtop", classLoader, "build", XposedHelpers.findClass("mtopsdk.mtop.domain.IMTOPDataObject", classLoader), String.class, new XC_MethodHook() {
//                                    @Override
//                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                        super.afterHookedMethod(param);
//                                        log.i("Mtop1 " + param.args[0] + " " + param.args[1]);
//                                        new Throwable().printStackTrace();
//                                    }
//                                });
//                                XposedHelpers.findAndHookMethod("mtopsdk.mtop.intf.Mtop", classLoader, "build", XposedHelpers.findClass("mtopsdk.mtop.domain.MtopRequest", classLoader), String.class, new XC_MethodHook() {
//                                    @Override
//                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                        super.afterHookedMethod(param);
//                                        log.i("Mtop2 " + param.args[0] + " " + param.args[1]);
//                                        new Throwable().printStackTrace();
//                                    }
//                                });
//                            } catch (Throwable e) {
//                                e.printStackTrace();
//                            }
//
//                            log.i("hook Mtop");
                        }
                    }
                });

    }
}
