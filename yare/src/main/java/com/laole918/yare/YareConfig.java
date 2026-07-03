package com.laole918.yare;

import android.os.Build;

import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public final class YareConfig {
    public static int sdkLevel;
    public static boolean debug = true;
    public static boolean debuggable;
    public static boolean disableHooks;
    public static boolean antiChecks;
    public static boolean disableHiddenApiPolicy = true;
    public static boolean disableHiddenApiPolicyForPlatformDomain = true;
    public static Yare.LibLoader libLoader = () -> System.loadLibrary("yare");

    static {
        sdkLevel = Build.VERSION.SDK_INT;
        if (sdkLevel == Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && isAtLeastPreReleaseCodename("VanillaIceCream")) {
            sdkLevel = Build.VERSION_CODES.UPSIDE_DOWN_CAKE + 1;
        }
    }

    private YareConfig() {
        throw new AssertionError("No instances.");
    }

    private static boolean isAtLeastPreReleaseCodename(String codename) {
        String buildCodename = Build.VERSION.CODENAME.toUpperCase(Locale.ROOT);
        if ("REL".equals(buildCodename)) {
            return false;
        }
        return buildCodename.compareTo(codename.toUpperCase(Locale.ROOT)) >= 0;
    }
}
