package com.hook.okhttp_redirect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.units;
import com.tools.hooker.HookTools;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


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

    private Object createHeaders() throws Throwable {
        Class Headers = HookTools.FindClass("com.android.okhttp.Headers");
        Constructor constructor = Headers.getDeclaredConstructor(String[].class);
        return HookTools.CallConstructor(constructor, (Object) Arrays.copyOf(headers, headers.length));
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

    String name(int index) {
        int nameIndex = index * 2;
        if (nameIndex < 0 || nameIndex >= headers.length) {
            return null;
        }
        return headers[nameIndex];
    }

    String value(int index) {
        int valueIndex = index * 2 + 1;
        if (valueIndex < 0 || valueIndex >= headers.length) {
            return null;
        }
        return headers[valueIndex];
    }

    public List<String> getHeader(String name) {
        List<String> result = null;
        for (int i = 0, size = headers.length / 2; i < size; i++) {
            if (name.equalsIgnoreCase(name(i))) {
                if (result == null) result = new ArrayList<>(2);
                result.add(value(i));
            }
        }
        return result != null
                ? Collections.unmodifiableList(result)
                : Collections.<String>emptyList();
    }

    public void UpdateHeader(String name, String value) {
        for (int i = 0, size = headers.length / 2; i < size; i++) {
            if (name.equalsIgnoreCase(name(i))) {
                int valueIndex = i * 2 + 1;
                if (valueIndex < 0 || valueIndex >= headers.length) {
                    return;
                }
                headers[valueIndex] = value;
            }
        }
    }

    private Object CreateResponseBody(byte[] body) throws Throwable {
        Class ResponseBodyClass = HookTools.FindClass("com.android.okhttp.ResponseBody");
        Class MediaTypeClass = HookTools.FindClass("com.android.okhttp.MediaType");
        Method parse = MediaTypeClass.getDeclaredMethod("parse", String.class);
        Method create = ResponseBodyClass.getDeclaredMethod("create", MediaTypeClass, byte[].class);
        String ctxType = "";
        for (String item : getHeader("Content-Type")) {
            ctxType = item;
            break;
        }
        return create.invoke(null, parse.invoke(null, ctxType), body);
    }

    public Object CreateResponseBody(Object req) throws Throwable {
        Class builder = HookTools.FindClass("com.android.okhttp.Response$Builder");
        ResponseBuilderProxy proxy = new ResponseBuilderProxy(HookTools.CallConstructor(builder));
        proxy.request(req);
        UpdateHeader("Date", units.GetNowServiceTime());
        proxy.headers(createHeaders());
        proxy.code(responseCode);
        proxy.message(responseMessage);
        proxy.protocol(createProtocol());
        proxy.body(CreateResponseBody(body));
        return proxy.build();
    }
}
