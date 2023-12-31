package com.common;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpService extends NanoHTTPD {
    public interface HttpServerCallback {
        String OnHttp(String url, JSONObject body) throws Throwable;
    }

    Map<String, HttpServerCallback> callback = new HashMap<>();

    public HttpService(String ip, int host) {
        super(ip, host);
    }

    private JSONObject parseBody(IHTTPSession session) {
        try {
            Map<String, String> files = new HashMap<String, String>();
            JSONObject json = null;
            session.parseBody(files);
            String body = files.get("postData");
            if (body == null) {
                return null;
            }
            return new JSONObject(body);
        } catch (Throwable e) {
            Log.e("fake_device", "parseBody error!", e);
            e.printStackTrace();
            return null;
        }
    }

    public void registerHandler(String url, HttpServerCallback callback) {
        this.callback.put(url, callback);
    }

    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String url = session.getUri();
        if (url.endsWith("/") && url.length() > 1) {
            url = url.substring(0, url.length() - 1);
        }
        HttpServerCallback handler = callback.get(url);
        if (handler == null) {
            return newFixedLengthResponse("500");
        }
        String resp;
        try {
            resp = handler.OnHttp(session.getUri(), parseBody(session));
        } catch (Throwable e) {
            resp = "error";
            Log.e("fake_device", "OnHttp error!", e);
            e.printStackTrace();
        }
        return NanoHTTPD.newFixedLengthResponse(resp);
    }
}