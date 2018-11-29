package com.jt808.server;

import io.netty.channel.Channel;

import java.util.HashMap;

public class ChannelMap {

    public static int channelNum = 0;
    private static HashMap<String, Channel> channelHashMap = null;//应改为concurrentHashmap以解决多线程冲突

    public static HashMap<String, Channel> getChannelHashMap() {
        return channelHashMap;
    }

    public static Channel getChannelByName(String name) {
        if (channelHashMap == null || channelHashMap.isEmpty()) {
            return null;
        }
        return channelHashMap.get(name);
    }

    public static void addChannel(String name, Channel channel) {
        if (channelHashMap == null) {
            channelHashMap = new HashMap<>(100);
        }
        channelHashMap.put(name, channel);
        channelNum++;
    }

    public static int removeChannelByName(String name) {
        if (channelHashMap.containsKey(name)) {
            channelHashMap.remove(name);
            return 0;
        } else {
            return 1;
        }
    }
}
