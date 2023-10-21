package com.common;

import static com.common.units.readInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;

import fi.iki.elonen.NanoHTTPD;

public class HttpsService extends NanoHTTPD {
    public interface HttpsServerCallback {
        String OnHttp(String url, byte[] body) throws Throwable;
    }

    static class Rule {
        String url;
        HttpsServerCallback callback;

        public Rule(String url, HttpsServerCallback callback) {
            this.callback = callback;
            this.url = url;
        }
    }

    List<Rule> callback = new ArrayList<>();

    public HttpsService(String ip, int host) {
        super(ip, host);
    }

    public void StartHttps(byte[] ks, String password) throws Throwable {
        char[] passwd = password.toCharArray();
        ByteArrayInputStream ins = new ByteArrayInputStream(ks);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(ins, passwd);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, passwd);
        makeSecure(NanoHTTPD.makeSSLSocketFactory(keystore, keyManagerFactory), null);
        start();
    }

    public void registerHandler(String url, HttpsServerCallback callback) {
        this.callback.add(new Rule(url, callback));
    }

    public Response serve(IHTTPSession session) {
        String url = session.getUri();
        if (url.endsWith("/") && url.length() > 1) {
            url = url.substring(0, url.length() - 1);
        }
        HttpsServerCallback handler = null;
        for (Rule rule : callback) {
            if (url.matches(rule.url)) {
                handler = rule.callback;
                break;
            }
        }
        if (handler == null) {
            return newFixedLengthResponse("500");
        }
        String resp;
        try {
            resp = handler.OnHttp(session.getUri(), readInputStream(session.getInputStream()));
        } catch (Throwable e) {
            resp = "error";
            log.e("OnHttp error!" + e);
            e.printStackTrace();
        }
        return NanoHTTPD.newFixedLengthResponse(resp);
    }
}