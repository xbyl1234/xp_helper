package com.hook.okhttp_redirect;

import com.common.log;
import com.common.units;
import com.tools.hooker.HookTools;
import com.tools.proxy.ProxyClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class ResponseProxy extends ProxyClass<ResponseProxy> {
    public ResponseProxy(Object originObject) {
        super(originObject);
    }

    public byte[] GetBodyBytes() throws Throwable {
        Object realResponseBody = HookTools.GetFieldValue(originClass, originObject, "body");
        Class ResponseBodyClass = HookTools.FindClass("com.android.okhttp.ResponseBody");
        Method bytes = ResponseBodyClass.getDeclaredMethod("bytes");
        byte[] bodyData = (byte[]) bytes.invoke(realResponseBody);

        Object bufferedSource = HookTools.GetFieldValue(realResponseBody.getClass(), realResponseBody, "source");
        Object buffer = HookTools.GetFieldValue(bufferedSource.getClass(), bufferedSource, "buffer");
        Method write = buffer.getClass().getDeclaredMethod("write", byte[].class);
        write.invoke(buffer, bodyData);
        HookTools.SetField(bufferedSource.getClass(), bufferedSource, "closed", false);

        return bodyData;
    }


    public String[] GetHeaders() {
        Object headers = HookTools.GetFieldValue(originClass, originObject, "headers");
        return (String[]) HookTools.GetFieldValue(headers.getClass(), headers, "namesAndValues");
    }

    public int GetCode() {
        return (int) HookTools.GetFieldValue(originClass, originObject, "code");
    }

}
