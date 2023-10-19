package me.melontini.dark_matter.api.config.serializers.gson;

import me.melontini.dark_matter.impl.config.serializers.gson.FixupsImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface FixupsBuilder {

    static FixupsBuilder create() {
        return new FixupsImpl().builder();
    }

    FixupsBuilder add(String key, Fixup fixup);

    default FixupsBuilder add(Fixup fixup, String... keys) {
        for (String key : keys) {
            add(key, fixup);
        }
        return this;
    }

    default FixupsBuilder add(String key, Fixup... fixups) {
        for (Fixup fixup : fixups) {
            add(key, fixup);
        }
        return this;
    }

    Fixups build();
}
