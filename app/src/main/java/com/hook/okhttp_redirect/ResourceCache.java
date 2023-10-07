package com.hook.okhttp_redirect;

import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.common.log;
import com.common.units;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResourceCache implements ResourceCacheInterface {
    static class ApiResponse {
        int code;
        String data;
    }

    static public String ServiceIp;
    static public String ServicePort;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType Bin = MediaType.parse("application/octet-stream");
    OkHttpClient client = new OkHttpClient();

    public byte[] post(String url, JSONObject data) throws Throwable {
        try {
            RequestBody body = RequestBody.create(data.toJSONString(), JSON);
            Request request = new Request.Builder()
                    .url("http://" + ServiceIp + ":" + ServicePort + url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception("response not success!");
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new Exception("response is null!");
            }
            return responseBody.bytes();
        } catch (Throwable e) {
            log.e("cache http request error: " + e);
            e.printStackTrace();
            throw e;
        }
    }

    public byte[] post(String url, byte[] data) throws Throwable {
        try {
            RequestBody body = RequestBody.create(data, Bin);
            Request request = new Request.Builder()
                    .url("http://" + ServiceIp + ":" + ServicePort + url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception("response not success!");
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new Exception("response is null!");
            }
            return responseBody.bytes();
        } catch (Throwable e) {
            log.e("cache http request error: " + e);
            e.printStackTrace();
            throw e;
        }
    }

    public JSONObject httpGet(String url, Map<String, String> query) throws Throwable {
        try {
            Uri.Builder uriBuild = new Uri.Builder()
                    .scheme("http")
                    .path(ServiceIp + ":" + ServicePort + url);
            for (String key : query.keySet()) {
                uriBuild.appendQueryParameter(key, query.get(key));
            }
            Request request = new Request.Builder()
                    .url(uriBuild.build().toString())
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception("response not success!");
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new Exception("response is null!");
            }
            return JSONObject.parseObject(new String(responseBody.bytes()));
        } catch (Throwable e) {
            log.e("cache http request error: " + e);
            e.printStackTrace();
            throw e;
        }
    }

    //查询是否有缓存
    @Override
    public boolean HasCache(CacheId id) throws Throwable {
        JSONObject respJson = httpGet("/has_cache", id.toMap());
        return respJson.getString("data").equals("true");
    }

    //下载缓存
    @Override
    public Cache GetCache(CacheId id) throws Throwable {
        JSONObject respJson = httpGet("/download_cache", id.toMap());
        String data = respJson.getString("data");
        if (data == null || data.isEmpty()) {
            throw new Exception("download error");
        }
        return new Cache(id, Base64.getDecoder().decode(data));
    }

    //上传缓存
    @Override
    public boolean UploadCache(Cache cache, String path) throws Throwable {
        cache.id.path = path;
        units.save_file(path, cache.ToJson().toJSONString().getBytes(StandardCharsets.UTF_8));
        JSONObject respJson = httpGet("/upload_cache", cache.id.toMap());
        return respJson.getString("data").equals("true");
    }

}
