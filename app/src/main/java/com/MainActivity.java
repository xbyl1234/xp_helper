package com;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;

import com.alibaba.fastjson.JSONObject;
import com.common.log;
import com.google.protobuf.FieldType;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ProtoSyntax;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends Activity {
    private static SecretKeySpec a(String str) {
        byte[] bArr = new byte[32];
        Arrays.fill(bArr, (byte) 0);
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.arraycopy(bytes, 0, bArr, 0, bytes.length < 32 ? bytes.length : 32);
        return new SecretKeySpec(bArr, "AES");
    }

    public static synchronized String decode(String str, String str2) {
        byte[] decodeToBytes = decodeToBytes(str, str2);
        if (decodeToBytes != null) {
            return new String(decodeToBytes);
        }
        return "";
    }


    public static synchronized byte[] decodeToBytes(String str, String str2) {
        try {
            SecretKeySpec a2 = a(str);
            byte[] bArr = new byte[16];
            Arrays.fill(bArr, (byte) 0);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
            byte[] decode = Base64.decode(str2, 0);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(2, a2, ivParameterSpec);
            return cipher.doFinal(decode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    void test() {

        JSONObject json = JSONObject.parseObject("{\"response\":\"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\"}");
        String d = decode("C38FB23A402222A0C17D34A92F971D1F", json.getString("response"));
        log.i(d);

    }

    public static String n(Context context) {
        String s = null;
        String str = s;
        if (str == null) {
            try {
                String str2 = context.getPackageManager().getPackageInfo("com.android.vending", 0).versionName;
                s = str2;
                if (str2 == null) {
                    s = "";
                }
                return s;
            } catch (Exception unused) {
                s = "";
                return "";
            }
        }
        if (str == null) {
            s = "";
        }
        return s;
    }

    public MainActivity() throws RemoteException {
//        test();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String testPkg = "com.android.faker";
//                    List<LsposedHttpClient.Application> scope = new ArrayList<>();
//                    scope.add(new LsposedHttpClient.Application("com.android.vending", 0));
//                    scope.add(new LsposedHttpClient.Application(testPkg, 0));
//
//                    LsposedHttpClient client = LsposedHttpClient.GetInstance();
//                    client.enableModule("com.test1");
//                    client.setModuleScope("com.test1", scope);
//
//                    client.enableModule("com.test2");
//                    client.setModuleScope("com.test2", scope);
//                    log.i("finish");
//                    throw new RuntimeException("");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}