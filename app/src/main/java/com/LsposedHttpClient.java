package com;

import android.os.RemoteException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.common.log;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LsposedHttpClient {
    static class Application {
        @JSONField(name = "pkg_name")
        public String packageName;
        @JSONField(name = "gid")
        public int userId;

        public Application(String packageName, int userId) {
            this.packageName = packageName;
            this.userId = userId;
        }
    }

    static LsposedHttpClient instance = null;

    static LsposedHttpClient GetInstance() {
        if (instance == null) {
            instance = new LsposedHttpClient();
        }
        return instance;
    }

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public boolean post(String url, JSONObject data) {
        RequestBody body = RequestBody.create(data.toJSONString(), JSON);
        Request request = new Request.Builder()
                .url("http://127.0.0.1:12306" + url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
            if (!response.body().string().equals("success")) {
                log.e("lsposed http request error: " + response.body().string());
                return false;
            }
            return true;
        } catch (Throwable e) {
            log.e("lsposed http request error: " + e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean enableModule(String packageName) {
        JSONObject json = new JSONObject();
        json.put("pkg_name", packageName);
        return post("/enable_module", json);
    }

    public boolean disableModule(String packageName) {
        JSONObject json = new JSONObject();
        json.put("pkg_name", packageName);
        return post("/disable_module", json);
    }

    public boolean setModuleScope(String packageName, List<Application> scope) throws RemoteException {
        JSONObject json = new JSONObject();
        json.put("pkg_name", packageName);
        json.put("scope", scope);
        return post("/set_scope", json);
    }

}
