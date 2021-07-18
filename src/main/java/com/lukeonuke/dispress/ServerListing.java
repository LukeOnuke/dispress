package com.lukeonuke.dispress;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListing {
    private Map<String, String> servers = new HashMap<>();
    private static ServerListing instance = null;

    public static ServerListing getInstance() {
        if(instance == null){
            instance = new ServerListing();
            instance.getServers();
        }
        return instance;
    }

    private void getServers(){
        servers.put("beocraft.net", "beocraft");
        servers.put("hypixel.net", "hypixel");
    }

    public boolean hasIconFor(String ip){
        AtomicBoolean hasIconFor = new AtomicBoolean(false);
        servers.keySet().forEach((key) -> {
            if(ip.endsWith(key)){
                hasIconFor.set(true);
            }
        });
        return hasIconFor.get();
    }

    public String getIcon(String ip){
        for (Map.Entry<String, String> entry : servers.entrySet()) {
            if (entry.getKey().endsWith(ip)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
