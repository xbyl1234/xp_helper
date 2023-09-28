package com.hook.okhttp_redirect;

import com.tools.proxy.ProxyClass;
import com.tools.proxy.ProxyMethod;
import com.tools.proxy.ProxyParams;

public class ResponseBuilderProxy extends ProxyClass<ResponseBuilderProxy> {
    public ResponseBuilderProxy(Object originObject) {
        super(originObject);
    }

    @ProxyMethod
    public ResponseBuilderProxy request(@ProxyParams(ClassName = "com.android.okhttp.Request") Object request) throws Throwable {
        invoke("request", request);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy protocol(@ProxyParams(ClassName = "com.android.okhttp.Protocol") Object protocol) throws Throwable {
        invoke("protocol", protocol);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy code(int code) throws Throwable {
        invoke("code", code);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy message(String message) throws Throwable {
        invoke("message", message);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy handshake(@ProxyParams(ClassName = "com.android.okhttp.Handshake") Object handshake) throws Throwable {
        invoke("handshake", handshake);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy header(String name, String value) throws Throwable {
        invoke("header", name, value);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy addHeader(String name, String value) throws Throwable {
        invoke("addHeader", name, value);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy removeHeader(String name) throws Throwable {
        invoke("removeHeader", name);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy headers(@ProxyParams(ClassName = "com.android.okhttp.Headers") Object headers) throws Throwable {
        invoke("headers", headers);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy body(@ProxyParams(ClassName = "com.android.okhttp.ResponseBody") Object body) throws Throwable {
        invoke("body", body);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy networkResponse(@ProxyParams(ClassName = "com.android.okhttp.Response") Object networkResponse) throws Throwable {
        invoke("networkResponse", networkResponse);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy cacheResponse(@ProxyParams(ClassName = "com.android.okhttp.Response") Object cacheResponse) throws Throwable {
        invoke("cacheResponse", cacheResponse);
        return this;
    }

    @ProxyMethod
    public ResponseBuilderProxy priorResponse(@ProxyParams(ClassName = "com.android.okhttp.Response") Object priorResponse) throws Throwable {
        invoke("priorResponse", priorResponse);
        return this;
    }

    @ProxyMethod
    private void checkPriorResponse(@ProxyParams(ClassName = "com.android.okhttp.Response") Object response) throws Throwable {
        invoke("checkPriorResponse", response);
    }

    @ProxyMethod
    private void checkSupportResponse(String name, @ProxyParams(ClassName = "com.android.okhttp.Response") Object response) throws Throwable {
        invoke("checkSupportResponse", name, response);
    }

    @ProxyMethod
    public Object build() throws Throwable {
        return invoke("build");
    }

}
