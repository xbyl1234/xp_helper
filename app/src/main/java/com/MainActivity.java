package com;


import android.app.Activity;

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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends Activity {
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
}