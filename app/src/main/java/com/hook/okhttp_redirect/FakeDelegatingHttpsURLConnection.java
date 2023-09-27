package com.hook.okhttp_redirect;

import com.common.log;
import com.tools.hooker.FakeClass;
import com.tools.hooker.FakeClassBase;
import com.tools.hooker.FakeMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import de.robv.android.xposed.XC_MethodHook;

@FakeClass(ClassName = "com.android.okhttp.internal.huc.HttpsURLConnectionImpl")
public class FakeDelegatingHttpsURLConnection extends FakeClassBase {
    ResourceCacheInterface resourceCache;

    public FakeDelegatingHttpsURLConnection(ResourceCacheInterface resourceCache) {
        this.resourceCache = resourceCache;
    }

    URL GetObjectUrl(XC_MethodHook.MethodHookParam params) {
        HttpsURLConnection conn = (HttpsURLConnection) params.thisObject;
        URL url = conn.getURL();
        return url;
    }

    @Override
    public boolean ShouldFake(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        if (url != null && url.getHost().contains("fonts.gstatic.com")) {
            CacheId cacheId = new CacheId(url);
            try {
                boolean hasCache = resourceCache.HasCache(cacheId);
                log.i("HasCache: " + hasCache + ", " + url.toString());
                return hasCache;
            } catch (Throwable e) {
                log.e("HasCache " + url.toString() + " error: " + e);
                return false;
            }
        }
        return false;
    }

    @FakeMethod(needXposedParams = true)
    public InputStream getInputStream(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        CacheId cacheId = new CacheId(url);
        byte[] data = new byte[0];
        try {
            data = resourceCache.DownloadCache(cacheId);
        } catch (Throwable e) {
            log.e("DownloadCache " + url + " error: " + e);
            throw new IOException();
        }
        return new RedirectInputStream(url, data);
    }

    @FakeMethod(needXposedParams = true)
    public OutputStream getOutputStream(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on getOutputStream " + url);
        return new RedirectOutputStream();
    }

//    @FakeMethod(needXposedParams = true)
//    public URL getURL(XC_MethodHook.MethodHookParam params) {
//    }

    @FakeMethod(needXposedParams = true)
    public void connect(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on connect " + url);
        return;
    }

    @FakeMethod(needXposedParams = true)
    public void disconnect(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on disconnect " + url);
        return;
    }

    @FakeMethod(needXposedParams = true)
    public InputStream getErrorStream(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getErrorStream " + url);
        return new RedirectInputStream(url, "".getBytes());
    }

    @FakeMethod(needXposedParams = true)
    public int getResponseCode(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on getResponseCode " + url);
        return 200;
    }

    @FakeMethod(needXposedParams = true)
    public String getResponseMessage(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on getResponseMessage " + url);
        return "200";
    }

    @FakeMethod(needXposedParams = true)
    public Object getContent(XC_MethodHook.MethodHookParam params) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on getContent " + url);
        return null;
    }

    @FakeMethod(needXposedParams = true)
    public Object getContent(XC_MethodHook.MethodHookParam params, Class[] types) throws IOException {
        URL url = GetObjectUrl(params);
        log.i("on getContent " + url);
        return null;
    }

    @FakeMethod(needXposedParams = true)
    public String getContentEncoding(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getContentEncoding " + url);
        return null;
    }

    @FakeMethod(needXposedParams = true)
    public int getContentLength(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getContentLength " + url);
        return 0;
    }

    @FakeMethod(needXposedParams = true)
    public String getContentType(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getContentType " + url);
        return null;
    }

    @FakeMethod(needXposedParams = true)
    public long getDate(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getDate " + url);
        return 0;
    }

    @FakeMethod(needXposedParams = true)
    public String getHeaderField(XC_MethodHook.MethodHookParam params, int pos) {
        URL url = GetObjectUrl(params);
        log.i("on getHeaderField " + url);
        return null;
    }

    @FakeMethod(needXposedParams = true)
    public Map<String, List<String>> getHeaderFields(XC_MethodHook.MethodHookParam params) {
        URL url = GetObjectUrl(params);
        log.i("on getHeaderFields " + url);
        return null;
    }

//    public String getCipherSuite() {
//    }
//
//    public Certificate[] getLocalCertificates() {
//    }
//
//    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
//    }
//    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
//    }
//    public Principal getLocalPrincipal() {
//    }
//    public String getRequestMethod() {
//    }
//    public void setRequestMethod(String method) throws ProtocolException {
//    }
//    public boolean usingProxy() {
//    }
//    public boolean getInstanceFollowRedirects() {
//    }
//    public void setInstanceFollowRedirects(boolean followRedirects) {
//    }
//    public boolean getAllowUserInteraction() {
//    }
//    public boolean getDefaultUseCaches() {
//    }
//    public boolean getDoInput() {
//    }
//    public boolean getDoOutput() {
//    }
//    public long getExpiration() {
//    }
//    public Map<String, List<String>> getRequestProperties() {
//    }
//    public void addRequestProperty(String field, String newValue) {
//    }
//    public String getHeaderField(String key) {
//    }
//    public long getHeaderFieldDate(String field, long defaultValue) {
//    }
//    public int getHeaderFieldInt(String field, int defaultValue) {
//    }
//    public String getHeaderFieldKey(int position) {
//    }
//    public long getIfModifiedSince() {
//    }
//    public long getLastModified() {
//    }
//    public Permission getPermission() throws IOException {
//    }
//    public String getRequestProperty(String field) {
//    }
//    public boolean getUseCaches() {
//    }
//    public void setAllowUserInteraction(boolean newValue) {
//    }
//    public void setDefaultUseCaches(boolean newValue) {
//    }
//    public void setDoInput(boolean newValue) {
//    }
//    public void setDoOutput(boolean newValue) {
//    }
//    public void setIfModifiedSince(long newValue) {
//    }
//    public void setRequestProperty(String field, String newValue) {
//    }
//    public void setUseCaches(boolean newValue) {
//    }
//
//    public void setConnectTimeout(int timeoutMillis) {
//    }
//
//    public int getConnectTimeout() {
//    }
//
//    public void setReadTimeout(int timeoutMillis) {
//    }
//
//    public int getReadTimeout() {
//    }
//
//    public void setFixedLengthStreamingMode(int contentLength) {
//    }
//
//    public void setChunkedStreamingMode(int chunkLength) {
//    }
}
