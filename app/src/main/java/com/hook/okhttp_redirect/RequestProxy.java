package com.hook.okhttp_redirect;

import com.tools.proxy.ProxyClass;
import com.tools.proxy.ProxyMethod;

import java.net.URL;

public class RequestProxy extends ProxyClass<RequestProxy> {
    public RequestProxy(Object originObject) {
        super(originObject);
    }

    @ProxyMethod
    public URL url() throws Throwable {
        return (URL) invoke("url");
    }
}
