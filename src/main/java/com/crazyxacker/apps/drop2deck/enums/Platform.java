package com.crazyxacker.apps.drop2deck.enums;

public enum Platform {
    STEAM_DECK("/home", "/run/media/mmcblk0p1"),
    WINDOWS("C:\\", "D:\\"),
    LINUX("/home", "/mnt"),
    MAC_OSX("/Users", "/Volumes");

    private final String internalPath;
    private final String externalPath;

    Platform(String internalPath, String externalPath) {
        this.internalPath = internalPath;
        this.externalPath = externalPath;
    }

    public String getInternalPath() {
        return internalPath;
    }

    public String getExternalPath() {
        return externalPath;
    }
}
