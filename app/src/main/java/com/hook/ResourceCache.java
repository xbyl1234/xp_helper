package com.hook;

import com.alibaba.fastjson.JSONObject;
import com.common.log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResourceCache {
    //缓存id
    static class CacheId {
        public String md5;
    }

    static class Code {
        static final int Success = 0;
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
            log.e("lsposed http request error: " + e);
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
            log.e("lsposed http request error: " + e);
            e.printStackTrace();
            throw e;
        }
    }

    //查询是否有缓存
    public boolean HasCache(CacheId id) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("id", id);
        byte[] resp = post("/has_cache", json);
        JSONObject respJson = JSONObject.parseObject(new String(resp));
        return respJson.getInteger("code") == 0;
    }

    //下载缓存
    public byte[] DownloadCache(CacheId id) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("id", id);
        return post("/download_cache", json);
    }

    //上次缓存
    public boolean UploadCache(CacheId id, byte[] data) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("data", data);
        byte[] resp = post("/upload_cache", json);
        JSONObject respJson = JSONObject.parseObject(new String(resp));
        return respJson.getInteger("code") == 0;
    }

}
