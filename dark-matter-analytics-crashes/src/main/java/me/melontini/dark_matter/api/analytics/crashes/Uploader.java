package me.melontini.dark_matter.api.analytics.crashes;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.analytics.crashes.UploaderInternals;

@UtilityClass
public final class Uploader {

    public static String uploadToMclo_gs(String log) {
        return UploaderInternals.uploadToMclo_gs(log);
    }
}
