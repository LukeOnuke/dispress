package com.lukeonuke.dispress.version;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class DispressVersion{
    private final String version;
    private final boolean fullRelease;

    public DispressVersion(String version, boolean fullRelease) {
        this.version = version;
        this.fullRelease = fullRelease;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionAsInt(){
        AtomicInteger intVersion = new AtomicInteger();
        Arrays.stream(version.split("\\.")).forEach((subversion) -> {
            intVersion.addAndGet(Integer.parseInt(subversion));
            intVersion.updateAndGet(v -> v * 10);
        });
        return intVersion.get();
    }


    /**
     * @param other Other version
     * @return If the caller version is greater than the other version
     */
    public boolean isGreaterThan(DispressVersion other){
        return this.getVersionAsInt() > other.getVersionAsInt();
    }

    public boolean isFullRelease() {
        return fullRelease;
    }
}
