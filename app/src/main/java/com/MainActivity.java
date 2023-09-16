package com;


import android.app.Activity;
import android.os.RemoteException;

import com.common.log;
import com.google.protobuf.FieldType;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ProtoSyntax;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends Activity {

    public MainActivity() throws RemoteException {
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

}