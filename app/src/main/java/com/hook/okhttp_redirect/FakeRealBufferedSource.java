package com.hook.okhttp_redirect;

import com.tools.hooker.FakeClass;
import com.tools.hooker.FakeClassBase;
import com.tools.hooker.FakeMethod;
import com.tools.hooker.HookTools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

@FakeClass(ClassName = "com.android.okhttp.okio.RealBufferedSource")
public class FakeRealBufferedSource extends FakeClassBase {
    @Override
    public boolean ShouldFake(XC_MethodHook.MethodHookParam params) {
        return true;
    }

    @FakeMethod(needXposedParams = true)
    public void close(XC_MethodHook.MethodHookParam params) throws Throwable {
        Field closed = HookTools.GetField(params.thisObject.getClass(), "closed");
        boolean isClosed = (boolean) HookTools.GetFieldValue(closed, params.thisObject);
        if (isClosed) {
            return;
        }
        HookTools.SetField(closed, params.thisObject, true);
        Object source = HookTools.GetFieldValue(params.thisObject.getClass(), params.thisObject, "source");
        Object buffer = HookTools.GetFieldValue(params.thisObject.getClass(), params.thisObject, "buffer");
        try {
            Method close = source.getClass().getDeclaredMethod("close");
            close.invoke(source);
        } catch (Throwable e) {

        }

        Method clear = buffer.getClass().getDeclaredMethod("clear");
        clear.invoke(buffer);
    }

}
