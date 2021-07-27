package com.lukeonuke.dispress.version;

import com.lukeonuke.dispress.Dispress;

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
        int intVersion = 0;
        for (String subversion : version.split("\\.")) {
            intVersion += Integer.parseInt(subversion);
            intVersion *= 10;
        }
        Dispress.LOGGER.warn(this + " : " + intVersion);
        return intVersion;
    }


    /**
     * @param other Other version
     * @return If the caller version is greater than the other version
     */
    public boolean isGreaterThan(DispressVersion other){
        return this.getVersionAsInt() >= other.getVersionAsInt();
    }

    public boolean isFullRelease() {
        return fullRelease;
    }
}
