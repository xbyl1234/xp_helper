package com.cache_service;


import android.content.Context;

import com.common.HttpsService;
import com.common.log;
import com.common.units;

import java.io.IOException;
import java.io.InputStream;

public class CacheServer {
    HttpsService service = null;

    byte[] LoadKeyStore(Context context) throws IOException {
//keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048 -ext SAN=DNS:gvt1.com,IP:127.0.0.1  -validity 9999
        return units.readInputStream(context.getAssets().open("keystore.jks"));
    }

    HttpsService.HttpsServerCallback callback = new HttpsService.HttpsServerCallback() {
        @Override
        public String OnHttp(String url, byte[] body) throws Throwable {
            log.d(url);
            return null;
        }
    };

    public void StartHttpServer(Context context) throws Throwable {
//        service = new HttpsService("127.0.0.1", 10443);
        service = new HttpsService("0.0.0.0", 10443);
        service.registerHandler(".*", callback);
        service.StartHttps(LoadKeyStore(context), "password");
    }


}
