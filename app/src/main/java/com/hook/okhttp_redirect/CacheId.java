package com.hook.okhttp_redirect;

import com.common.units;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CacheId {
    public String md5;
    public String channel;
    public String path;
    public URL url;

    public CacheId(URL url) {
        this.url = url;
        this.md5 = units.MD5(url.toString());
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (!md5.isEmpty())
            map.put("md5", md5);
        if (!channel.isEmpty())
            map.put("channel", channel);
        if (!path.isEmpty())
            map.put("path", path);
        return map;
    }
}