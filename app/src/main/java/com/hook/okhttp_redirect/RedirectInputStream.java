package com.hook.okhttp_redirect;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class RedirectInputStream extends InputStream {

    public RedirectInputStream(URL url, byte[] body) {

    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }
}
