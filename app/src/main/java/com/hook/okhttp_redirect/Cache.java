package com.hook.okhttp_redirect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tools.hooker.HookTools;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class Cache {
    public int responseCode = 200;
    public String responseMessage = "OK";
    String[] headers = null;
    byte[] body = null;
    CacheId id;

    public Cache(CacheId id, byte[] cache) {
        this.id = id;
        JSONObject json = JSONObject.parseObject(new String(cache));
        body = Base64.getDecoder().decode(json.getString("body"));
        JSONArray headers = json.getJSONArray("headers");
        this.headers = new String[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            this.headers[i] = headers.getString(i);
        }
    }

    public Cache(URL url, String[] headers, byte[] body) {
        this.id = new CacheId(url);
        this.headers = headers;
        this.body = body;
    }

    public JSONObject ToJson() {
        JSONObject json = new JSONObject();
        json.put("body", Base64.getEncoder().encode(body));
        json.put("headers", headers);
        json.put("url", id.url);
        return json;
    }

    public byte[] GetBodyByte() throws IOException {
        return body;
    }

    private Object createHeaders() {
        Class Headers = HookTools.FindClass("com.android.okhttp.Headers");
        return HookTools.CallConstructor(Headers, headers);
    }

    private Object createProtocol() {
        Class Protocol = HookTools.FindClass("com.android.okhttp.Protocol");
        for (Object obj : Protocol.getEnumConstants()) {
            if (obj.toString().equalsIgnoreCase("http/1.1")) {
                return obj;
            }
        }
        return null;
    }

    public Object CreateResponseBody(Object req) throws Throwable {
        Class builder = HookTools.FindClass("com.android.okhttp.Response$Builder");
        ResponseBuilderProxy proxy = new ResponseBuilderProxy(HookTools.CallConstructor(builder));
        proxy.request(req);
        proxy.headers(createHeaders());
        proxy.code(responseCode);
        proxy.message(responseMessage);
        proxy.protocol(createProtocol());
        return proxy.build();
    }
}
